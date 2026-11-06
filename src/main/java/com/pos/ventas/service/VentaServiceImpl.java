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
import com.pos.ventas.repository.DetalleVentaRepository;
import com.pos.ventas.repository.MovimientoInventarioRepository;
import com.pos.ventas.repository.PagoRepository;
import com.pos.ventas.repository.ProductoRepository;
import com.pos.ventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
            if (request == null || request.getIdUsuario() == null) {
                throw new IllegalArgumentException("El usuario que registra la venta es obligatorio");
            }
            if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
                throw new IllegalArgumentException("La venta debe tener al menos un producto");
            }
            if (request.getPagos() == null || request.getPagos().isEmpty()) {
                throw new IllegalArgumentException("Debe registrar al menos un pago");
            }

            BigDecimal descuento = request.getDescuento() == null ? BigDecimal.ZERO : request.getDescuento();
            if (descuento.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El descuento no puede ser negativo");
            }

            Venta venta = new Venta();
            venta.setIdCliente(request.getIdCliente());
            venta.setIdUsuario(request.getIdUsuario());
            venta.setFechaVenta(LocalDateTime.now());
            venta.setSubtotal(BigDecimal.ZERO);
            venta.setDescuento(descuento);
            venta.setTotal(BigDecimal.ZERO);
            venta.setEstado("PAGADA");
            venta = ventaRepository.save(venta);   // necesitamos el id

            // ---------- Detalles, stock y movimientos ----------
            BigDecimal subtotal = BigDecimal.ZERO;

            for (DetalleRequestDTO item : request.getDetalles()) {
                if (item.getIdProducto() == null || item.getCantidad() == null || item.getCantidad() <= 0) {
                    throw new IllegalArgumentException("Cada producto requiere id y cantidad mayor a 0");
                }
                Producto prod = productoRepository.findById(item.getIdProducto())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Producto no encontrado con ID: " + item.getIdProducto()));

                if (!Boolean.TRUE.equals(prod.getActivo())) {
                    throw new IllegalArgumentException("El producto está inactivo: " + prod.getNombre());
                }
                if (prod.getStock() < item.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para '" + prod.getNombre()
                            + "'. Disponible: " + prod.getStock());
                }

                BigDecimal precio = prod.getPrecioVenta();
                BigDecimal subtotalLinea = precio.multiply(BigDecimal.valueOf(item.getCantidad()));

                DetalleVenta detalle = new DetalleVenta();
                detalle.setIdVenta(venta.getIdVenta());
                detalle.setIdProducto(prod.getIdProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(precio);
                detalle.setSubtotal(subtotalLinea);
                detalleVentaRepository.save(detalle);

                prod.setStock(prod.getStock() - item.getCantidad());
                productoRepository.save(prod);

                registrarMovimiento(prod.getIdProducto(), "SALIDA", item.getCantidad(),
                        "Venta #" + venta.getIdVenta(), request.getIdUsuario());

                subtotal = subtotal.add(subtotalLinea);
            }

            // ---------- Totales ----------
            if (descuento.compareTo(subtotal) > 0) {
                throw new IllegalArgumentException("El descuento no puede ser mayor al subtotal (" + subtotal + ")");
            }
            BigDecimal total = subtotal.subtract(descuento);

            venta.setSubtotal(subtotal);
            venta.setTotal(total);
            venta = ventaRepository.save(venta);

            // ---------- Pagos (obligatorios) ----------
            BigDecimal recibido = BigDecimal.ZERO;
            BigDecimal efectivo = BigDecimal.ZERO;
            for (PagoRequestDTO p : request.getPagos()) {
                if (p.getMetodoPago() == null || !METODOS_VALIDOS.contains(p.getMetodoPago().toUpperCase())) {
                    throw new IllegalArgumentException("Método de pago inválido. Permitidos: " + METODOS_VALIDOS);
                }
                if (p.getMonto() == null || p.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("El monto del pago debe ser mayor a 0");
                }
                recibido = recibido.add(p.getMonto());
                if ("EFECTIVO".equals(p.getMetodoPago().toUpperCase())) {
                    efectivo = efectivo.add(p.getMonto());
                }
            }

            if (recibido.compareTo(total) < 0) {
                throw new IllegalArgumentException("Los pagos (" + recibido + ") no cubren el total (" + total + ")");
            }

            // El sobrepago solo se permite en efectivo y se devuelve como cambio
            BigDecimal cambio = recibido.subtract(total);
            if (cambio.signum() > 0 && cambio.compareTo(efectivo) >= 0) {
                throw new IllegalArgumentException("El sobrepago solo se permite en efectivo");
            }

            // En pagos se guarda solo lo aplicado a la venta (se descuenta el cambio del efectivo)
            BigDecimal cambioPendiente = cambio;
            for (PagoRequestDTO p : request.getPagos()) {
                BigDecimal monto = p.getMonto();
                if ("EFECTIVO".equals(p.getMetodoPago().toUpperCase()) && cambioPendiente.signum() > 0) {
                    BigDecimal descontar = cambioPendiente.min(monto);
                    monto = monto.subtract(descontar);
                    cambioPendiente = cambioPendiente.subtract(descontar);
                }
                if (monto.signum() == 0) {
                    continue;
                }
                Pago pago = new Pago();
                pago.setIdVenta(venta.getIdVenta());
                pago.setMetodoPago(p.getMetodoPago().toUpperCase());
                pago.setMonto(monto);
                pago.setFechaPago(LocalDateTime.now());
                pagoRepository.save(pago);
            }

            // ---------- Respuesta ----------
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

    /** Cancela la venta y devuelve el stock al inventario. */
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

            List<DetalleVenta> detalles = detalleVentaRepository.findByIdVenta(id);
            for (DetalleVenta d : detalles) {
                Producto prod = productoRepository.findById(d.getIdProducto()).orElse(null);
                if (prod != null) {
                    prod.setStock(prod.getStock() + d.getCantidad());
                    productoRepository.save(prod);
                    registrarMovimiento(prod.getIdProducto(), "ENTRADA", d.getCantidad(),
                            "Cancelación venta #" + id, venta.getIdUsuario());
                }
            }

            venta.setEstado("CANCELADA");
            response.setMensaje("OK");
            response.setBody(ventaRepository.save(venta));
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