package com.pos.ventas.service;

import com.pos.ventas.dto.DetalleRequestDTO;
import com.pos.ventas.dto.PagoRequestDTO;
import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.dto.VentaCompletaDTO;
import com.pos.ventas.dto.VentaRequestDTO;
import com.pos.ventas.entity.DetalleVenta;
import com.pos.ventas.entity.MovimientoInventario;
import com.pos.ventas.entity.Pago;
import com.pos.ventas.entity.Producto;
import com.pos.ventas.entity.Venta;
import com.pos.ventas.repository.ClienteRepository;
import com.pos.ventas.repository.DetalleVentaRepository;
import com.pos.ventas.repository.MovimientoInventarioRepository;
import com.pos.ventas.repository.PagoRepository;
import com.pos.ventas.repository.ProductoRepository;
import com.pos.ventas.repository.UsuarioRepository;
import com.pos.ventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class VentaServiceImpl implements VentaService {

    private static final List<String> METODOS_VALIDOS = Arrays.asList("EFECTIVO", "TARJETA", "TRANSFERENCIA");

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private MovimientoInventarioRepository movimientoRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    /** Línea de venta ya validada, lista para persistir. */
    private static class LineaVenta {
        Producto producto;
        int cantidad;
        BigDecimal precio;
        BigDecimal subtotal;
    }

    @Override
    public ResponseDTO listarVentas() {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(ventaRepository.findAllByOrderByFechaVentaDesc());
        } catch (Exception e) {
            response.setMensaje("Error al listar ventas: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO buscarPorId(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Venta> venta = ventaRepository.findById(id);
            if (venta.isPresent()) {
                response.setMensaje("OK");
                response.setBody(new VentaCompletaDTO(
                        venta.get(),
                        detalleVentaRepository.findByIdVenta(id),
                        pagoRepository.findByIdVenta(id),
                        BigDecimal.ZERO));
            } else {
                response.setMensaje("Venta no encontrada con el ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al buscar la venta: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    @Transactional
    public ResponseDTO registrarVenta(VentaRequestDTO request) {
        ResponseDTO response = new ResponseDTO();
        try {
            // ================= 1. VALIDACIONES (sin escribir nada) =================
            if (request == null || request.getIdUsuario() == null) {
                throw new IllegalArgumentException("El usuario que registra la venta es obligatorio");
            }
            if (!usuarioRepository.existsById(request.getIdUsuario())) {
                throw new IllegalArgumentException("Usuario no encontrado con ID: " + request.getIdUsuario());
            }
            if (request.getIdCliente() != null && !clienteRepository.existsById(request.getIdCliente())) {
                throw new IllegalArgumentException("Cliente no encontrado con ID: " + request.getIdCliente());
            }
            if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
                throw new IllegalArgumentException("La venta debe tener al menos un producto");
            }
            if (request.getPagos() == null || request.getPagos().isEmpty()) {
                throw new IllegalArgumentException("Debe registrar al menos un pago");
            }

            BigDecimal descuento = request.getDescuento() == null ? BigDecimal.ZERO : request.getDescuento();
            if (descuento.signum() < 0) {
                throw new IllegalArgumentException("El descuento no puede ser negativo");
            }
            descuento = descuento.setScale(2, RoundingMode.HALF_UP);

            // Agrupar productos repetidos y ordenar por id (evita deadlocks al bloquear)
            Map<Long, Integer> cantidades = new TreeMap<Long, Integer>();
            for (DetalleRequestDTO item : request.getDetalles()) {
                if (item == null || item.getIdProducto() == null
                        || item.getCantidad() == null || item.getCantidad() <= 0) {
                    throw new IllegalArgumentException("Cada producto requiere id y cantidad mayor a 0");
                }
                Integer actual = cantidades.get(item.getIdProducto());
                cantidades.put(item.getIdProducto(),
                        actual == null ? item.getCantidad() : actual + item.getCantidad());
            }

            // Bloquear productos, validar y calcular
            List<LineaVenta> lineas = new ArrayList<LineaVenta>();
            BigDecimal subtotal = BigDecimal.ZERO;
            for (Map.Entry<Long, Integer> entry : cantidades.entrySet()) {
                final Long idProducto = entry.getKey();
                int cantidad = entry.getValue();

                Producto prod = productoRepository.findByIdForUpdate(idProducto)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Producto no encontrado con ID: " + idProducto));

                if (!Boolean.TRUE.equals(prod.getActivo())) {
                    throw new IllegalArgumentException("El producto está inactivo: " + prod.getNombre());
                }
                if (prod.getPrecioVenta() == null) {
                    throw new IllegalArgumentException("El producto no tiene precio de venta: " + prod.getNombre());
                }
                int stock = prod.getStock() == null ? 0 : prod.getStock();
                if (stock < cantidad) {
                    throw new IllegalArgumentException("Stock insuficiente para '" + prod.getNombre()
                            + "'. Disponible: " + stock);
                }

                LineaVenta linea = new LineaVenta();
                linea.producto = prod;
                linea.cantidad = cantidad;
                linea.precio = prod.getPrecioVenta();
                linea.subtotal = linea.precio.multiply(BigDecimal.valueOf(cantidad))
                        .setScale(2, RoundingMode.HALF_UP);
                lineas.add(linea);
                subtotal = subtotal.add(linea.subtotal);
            }

            if (descuento.compareTo(subtotal) > 0) {
                throw new IllegalArgumentException("El descuento no puede ser mayor al subtotal (" + subtotal + ")");
            }
            BigDecimal total = subtotal.subtract(descuento);

            // Pagos
            BigDecimal recibido = BigDecimal.ZERO;
            BigDecimal efectivo = BigDecimal.ZERO;
            for (PagoRequestDTO p : request.getPagos()) {
                if (p == null || p.getMetodoPago() == null
                        || !METODOS_VALIDOS.contains(p.getMetodoPago().trim().toUpperCase())) {
                    throw new IllegalArgumentException("Método de pago inválido. Permitidos: " + METODOS_VALIDOS);
                }
                if (p.getMonto() == null || p.getMonto().signum() <= 0) {
                    throw new IllegalArgumentException("El monto del pago debe ser mayor a 0");
                }
                recibido = recibido.add(p.getMonto());
                if ("EFECTIVO".equals(p.getMetodoPago().trim().toUpperCase())) {
                    efectivo = efectivo.add(p.getMonto());
                }
            }
            if (recibido.compareTo(total) < 0) {
                throw new IllegalArgumentException("Los pagos (" + recibido + ") no cubren el total (" + total + ")");
            }

            BigDecimal cambio = recibido.subtract(total).setScale(2, RoundingMode.HALF_UP);
            if (cambio.signum() > 0 && cambio.compareTo(efectivo) >= 0) {
                throw new IllegalArgumentException("El sobrepago solo se permite en efectivo");
            }

            // ================= 2. ESCRITURA =================
            Venta venta = new Venta();
            venta.setIdCliente(request.getIdCliente());
            venta.setIdUsuario(request.getIdUsuario());
            venta.setFechaVenta(LocalDateTime.now());
            venta.setSubtotal(subtotal);
            venta.setDescuento(descuento);
            venta.setTotal(total);
            venta.setEstado("PAGADA");
            venta = ventaRepository.save(venta);

            for (LineaVenta l : lineas) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setIdVenta(venta.getIdVenta());
                detalle.setIdProducto(l.producto.getIdProducto());
                detalle.setCantidad(l.cantidad);
                detalle.setPrecioUnitario(l.precio);
                detalle.setSubtotal(l.subtotal);
                detalleVentaRepository.save(detalle);

                l.producto.setStock(l.producto.getStock() - l.cantidad);
                productoRepository.save(l.producto);

                registrarMovimiento(l.producto.getIdProducto(), "SALIDA", l.cantidad,
                        "Venta #" + venta.getIdVenta(), request.getIdUsuario());
            }

            // En pagos se guarda solo lo aplicado a la venta (al efectivo se le resta el cambio)
            BigDecimal cambioPendiente = cambio;
            for (PagoRequestDTO p : request.getPagos()) {
                String metodo = p.getMetodoPago().trim().toUpperCase();
                BigDecimal monto = p.getMonto();
                if ("EFECTIVO".equals(metodo) && cambioPendiente.signum() > 0) {
                    BigDecimal descontar = cambioPendiente.min(monto);
                    monto = monto.subtract(descontar);
                    cambioPendiente = cambioPendiente.subtract(descontar);
                }
                if (monto.signum() == 0) {
                    continue;
                }
                Pago pago = new Pago();
                pago.setIdVenta(venta.getIdVenta());
                pago.setMetodoPago(metodo);
                pago.setMonto(monto.setScale(2, RoundingMode.HALF_UP));
                pago.setFechaPago(LocalDateTime.now());
                pagoRepository.save(pago);
            }

            // ================= 3. RESPUESTA =================
            response.setMensaje("OK");
            response.setBody(new VentaCompletaDTO(
                    venta,
                    detalleVentaRepository.findByIdVenta(venta.getIdVenta()),
                    pagoRepository.findByIdVenta(venta.getIdVenta()),
                    cambio));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setMensaje("Error al registrar venta: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    /** Cancela la venta (sin borrarla) y devuelve el stock al inventario. */
    @Override
    @Transactional
    public ResponseDTO anularVenta(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Venta venta = ventaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + id));

            if ("CANCELADA".equals(venta.getEstado())) {
                throw new IllegalArgumentException("La venta ya está cancelada");
            }

            List<DetalleVenta> detalles = new ArrayList<DetalleVenta>(detalleVentaRepository.findByIdVenta(id));
            detalles.sort(Comparator.comparing(DetalleVenta::getIdProducto));

            for (DetalleVenta d : detalles) {
                final Long idProducto = d.getIdProducto();
                Producto prod = productoRepository.findByIdForUpdate(idProducto)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No se puede devolver el stock: producto " + idProducto + " no existe"));
                int stock = prod.getStock() == null ? 0 : prod.getStock();
                prod.setStock(stock + d.getCantidad());
                productoRepository.save(prod);
                registrarMovimiento(idProducto, "ENTRADA", d.getCantidad(),
                        "Cancelación venta #" + id, venta.getIdUsuario());
            }

            venta.setEstado("CANCELADA");
            venta = ventaRepository.save(venta);

            response.setMensaje("OK");
            response.setBody(new VentaCompletaDTO(
                    venta,
                    detalleVentaRepository.findByIdVenta(id),
                    pagoRepository.findByIdVenta(id),
                    BigDecimal.ZERO));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setMensaje("Error al cancelar venta: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    private void registrarMovimiento(Long idProducto, String tipo, Integer cantidad, String motivo, Long idUsuario) {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdProducto(idProducto);
        mov.setTipo(tipo);
        mov.setCantidad(cantidad);
        mov.setMotivo(motivo);
        mov.setIdUsuario(idUsuario);
        mov.setFecha(LocalDateTime.now());
        movimientoRepository.save(mov);
    }
}