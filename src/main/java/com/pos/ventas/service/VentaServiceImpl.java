package com.pos.ventas.service;

import com.pos.ventas.dto.DetalleRequestDTO;
import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.dto.VentaCompletaDTO;
import com.pos.ventas.dto.VentaRequestDTO;
import com.pos.ventas.entity.DetalleVenta;
import com.pos.ventas.entity.MovimientoInventario;
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
import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

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
                        pagoRepository.findByIdVenta(id)));
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

    /**
     * Registra venta + detalles, descuenta stock y genera movimientos SALIDA.
     * Todo en UNA transacción: si algo falla, se revierte todo.
     */
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

            Venta venta = new Venta();
            venta.setIdCliente(request.getIdCliente());
            venta.setIdUsuario(request.getIdUsuario());
            venta.setFechaVenta(LocalDateTime.now());
            venta.setTotal(BigDecimal.ZERO);
            venta.setEstado("PENDIENTE");
            venta = ventaRepository.save(venta);   // necesitamos el id

            BigDecimal total = BigDecimal.ZERO;

            for (DetalleRequestDTO item : request.getDetalles()) {
                if (item.getCantidad() == null || item.getCantidad() <= 0) {
                    throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
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
                BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(item.getCantidad()));

                DetalleVenta detalle = new DetalleVenta();
                detalle.setIdVenta(venta.getIdVenta());
                detalle.setIdProducto(prod.getIdProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(precio);
                detalle.setSubtotal(subtotal);
                detalleVentaRepository.save(detalle);

                prod.setStock(prod.getStock() - item.getCantidad());
                productoRepository.save(prod);

                registrarMovimiento(prod.getIdProducto(), "SALIDA", item.getCantidad(),
                        "Venta #" + venta.getIdVenta(), request.getIdUsuario());

                total = total.add(subtotal);
            }

            venta.setTotal(total);
            venta = ventaRepository.save(venta);

            response.setMensaje("OK");
            response.setBody(new VentaCompletaDTO(
                    venta,
                    detalleVentaRepository.findByIdVenta(venta.getIdVenta()),
                    pagoRepository.findByIdVenta(venta.getIdVenta())));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setMensaje("Error al registrar venta: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    /** Anula la venta y devuelve el stock al inventario. */
    @Override
    @Transactional
    public ResponseDTO anularVenta(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Venta venta = ventaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + id));

            if ("ANULADA".equals(venta.getEstado())) {
                throw new IllegalArgumentException("La venta ya está anulada");
            }

            List<DetalleVenta> detalles = detalleVentaRepository.findByIdVenta(id);
            for (DetalleVenta d : detalles) {
                Producto prod = productoRepository.findById(d.getIdProducto()).orElse(null);
                if (prod != null) {
                    prod.setStock(prod.getStock() + d.getCantidad());
                    productoRepository.save(prod);
                    registrarMovimiento(prod.getIdProducto(), "ENTRADA", d.getCantidad(),
                            "Anulación venta #" + id, venta.getIdUsuario());
                }
            }

            venta.setEstado("ANULADA");
            response.setMensaje("OK");
            response.setBody(ventaRepository.save(venta));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setMensaje("Error al anular venta: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    private void registrarMovimiento(Long idProducto, String tipo, Integer cantidad, String motivo, Long idUsuario) {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdProducto(idProducto);
        mov.setTipoMovimiento(tipo);
        mov.setCantidad(cantidad);
        mov.setMotivo(motivo);
        mov.setIdUsuario(idUsuario);
        mov.setFechaMovimiento(LocalDateTime.now());
        movimientoRepository.save(mov);
    }
}
