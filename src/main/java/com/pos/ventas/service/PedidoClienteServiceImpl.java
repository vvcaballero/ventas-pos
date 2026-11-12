package com.pos.ventas.service;

import com.pos.ventas.dto.*;
import com.pos.ventas.entity.DetallePedidoCliente;
import com.pos.ventas.entity.MovimientoInventario;
import com.pos.ventas.entity.PedidoCliente;
import com.pos.ventas.entity.Producto;
import com.pos.ventas.repository.DetallePedidoClienteRepository;
import com.pos.ventas.repository.MovimientoInventarioRepository;
import com.pos.ventas.repository.PedidoClienteRepository;
import com.pos.ventas.repository.ProductoRepository;

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
public class PedidoClienteServiceImpl implements PedidoClienteService {

    @Autowired
    private PedidoClienteRepository pedidoRepository;

    @Autowired
    private DetallePedidoClienteRepository detalleRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    private ResponseDTO respuesta(String mensaje, Object body) {
        ResponseDTO response = new ResponseDTO();
        response.setMensaje(mensaje);
        response.setBody(body);
        return response;
    }

    @Override
    public ResponseDTO listarTodos() {
        try {
            return respuesta(
                    "OK",
                    pedidoRepository.findAllByOrderByFechaPedidoDesc()
            );
        } catch (Exception e) {
            return respuesta(
                    "Error al listar pedidos: " + e.getMessage(),
                    null
            );
        }
    }

    @Override
    public ResponseDTO listarPendientes() {
        try {
            return respuesta("OK", pedidoRepository.findPendientes());
        } catch (Exception e) {
            return respuesta(
                    "Error al listar pedidos pendientes: " + e.getMessage(),
                    null
            );
        }
    }

    @Override
    public ResponseDTO buscarPorId(Long idPedido) {
        try {
            Optional<PedidoCliente> pedido =
                    pedidoRepository.findByIdPedido(idPedido);

            if (!pedido.isPresent()) {
                return respuesta("Pedido no encontrado", null);
            }

            return respuesta("OK", pedido.get());

        } catch (Exception e) {
            return respuesta(
                    "Error al consultar pedido: " + e.getMessage(),
                    null
            );
        }
    }

    @Override
    @Transactional
    public ResponseDTO registrarPedido(PedidoClienteRequestDTO request) {

        try {
            if (request == null) {
                throw new IllegalArgumentException("La solicitud está vacía");
            }

            if (request.getIdCliente() == null) {
                throw new IllegalArgumentException("El cliente es obligatorio");
            }

            if (request.getIdUsuario() == null) {
                throw new IllegalArgumentException("El usuario es obligatorio");
            }

            if (request.getDetalles() == null ||
                    request.getDetalles().isEmpty()) {
                throw new IllegalArgumentException(
                        "Debes agregar al menos un producto al pedido"
                );
            }

            PedidoCliente pedido = new PedidoCliente();

            pedido.setIdCliente(request.getIdCliente());
            pedido.setIdUsuario(request.getIdUsuario());
            pedido.setObservaciones(request.getObservaciones());
            pedido.setEstado("REGISTRADO");
            pedido.setFechaPedido(LocalDateTime.now());

            for (DetallePedidoClienteRequestDTO item : request.getDetalles()) {

                if (item.getDescripcionProducto() == null ||
                        item.getDescripcionProducto().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "La descripción del producto es obligatoria"
                    );
                }

                if (item.getCantidad() == null || item.getCantidad() <= 0) {
                    throw new IllegalArgumentException(
                            "La cantidad debe ser mayor a cero"
                    );
                }

                BigDecimal precio = item.getPrecioEstimado() == null
                        ? BigDecimal.ZERO
                        : item.getPrecioEstimado();

                if (precio.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(
                            "El precio estimado no puede ser negativo"
                    );
                }

                DetallePedidoCliente detalle = new DetallePedidoCliente();

                detalle.setPedido(pedido);
                detalle.setDescripcionProducto(
                        item.getDescripcionProducto().trim()
                );
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioEstimado(precio);
                detalle.setObservaciones(item.getObservaciones());

                pedido.getDetalles().add(detalle);
            }

            PedidoCliente guardado = pedidoRepository.save(pedido);

            return respuesta("Pedido registrado correctamente", guardado);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus()
                    .setRollbackOnly();

            return respuesta(
                    "Error al registrar pedido: " + e.getMessage(),
                    null
            );
        }
    }

    @Override
    @Transactional
    public ResponseDTO actualizarEstado(
            Long idPedido,
            EstadoPedidoRequestDTO request
    ) {

        try {
            if (request == null || request.getEstado() == null) {
                throw new IllegalArgumentException("El estado es obligatorio");
            }

            PedidoCliente pedido = pedidoRepository.findByIdForUpdate(idPedido)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Pedido no encontrado")
                    );

            String nuevoEstado = request.getEstado()
                    .trim()
                    .toUpperCase();

            List<String> estadosPermitidos = Arrays.asList(
                    "REGISTRADO",
                    "APARTADO",
                    "PEDIDO_PROVEEDOR",
                    "EN_ARRIBO",
                    "RECIBIDO",
                    "CANCELADO"
            );

            if (!estadosPermitidos.contains(nuevoEstado)) {
                throw new IllegalArgumentException(
                        "Estado inválido: " + nuevoEstado
                );
            }

            if ("CONVERTIDO".equals(pedido.getEstado())) {
                throw new IllegalArgumentException(
                        "El pedido ya fue convertido a producto"
                );
            }

            if ("CANCELADO".equals(pedido.getEstado())) {
                throw new IllegalArgumentException(
                        "No puedes modificar un pedido cancelado"
                );
            }

            if ("RECIBIDO".equals(nuevoEstado)) {
                pedido.setFechaRecepcion(LocalDateTime.now());
            }

            pedido.setEstado(nuevoEstado);

            if (request.getObservaciones() != null) {
                pedido.setObservaciones(request.getObservaciones());
            }

            return respuesta(
                    "Estado actualizado correctamente",
                    pedidoRepository.save(pedido)
            );

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus()
                    .setRollbackOnly();

            return respuesta(
                    "Error al actualizar estado: " + e.getMessage(),
                    null
            );
        }
    }

    @Override
    @Transactional
    public ResponseDTO convertirProducto(
            Long idPedido,
            Long idDetallePedido,
            RegistrarProductoPedidoDTO request
    ) {

        try {
            if (request == null) {
                throw new IllegalArgumentException(
                        "Debes proporcionar los datos del producto"
                );
            }

            PedidoCliente pedido = pedidoRepository.findByIdForUpdate(idPedido)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Pedido no encontrado")
                    );

            if (!"RECIBIDO".equals(pedido.getEstado())) {
                throw new IllegalArgumentException(
                        "El pedido debe estar en estado RECIBIDO"
                );
            }

            DetallePedidoCliente detalle = detalleRepository
                    .findById(idDetallePedido)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Detalle de pedido no encontrado"
                            )
                    );

            if (detalle.getPedido() == null ||
                    !detalle.getPedido().getIdPedido().equals(idPedido)) {
                throw new IllegalArgumentException(
                        "El detalle no pertenece al pedido indicado"
                );
            }

            if (detalle.getIdProducto() != null) {
                throw new IllegalArgumentException(
                        "Este detalle ya fue convertido a producto"
                );
            }

            String codigo = request.getCodigo() == null
                    ? null
                    : request.getCodigo().trim();

            if (codigo == null || codigo.isEmpty()) {
                throw new IllegalArgumentException(
                        "El código del producto es obligatorio"
                );
            }

            String nombre = request.getNombre() == null
                    ? detalle.getDescripcionProducto()
                    : request.getNombre().trim();

            if (nombre.isEmpty()) {
                throw new IllegalArgumentException(
                        "El nombre del producto es obligatorio"
                );
            }

            int cantidad = request.getCantidadRecibida() == null
                    ? detalle.getCantidad()
                    : request.getCantidadRecibida();

            if (cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad recibida debe ser mayor a cero"
                );
            }

            Producto producto = productoRepository.findByCodigo(codigo)
                    .orElse(null);

            boolean productoNuevo = producto == null;

            if (productoNuevo) {

                producto = new Producto();

                producto.setCodigo(codigo);
                producto.setNombre(nombre);

                producto.setDescripcion(
                        request.getDescripcion() != null
                                ? request.getDescripcion()
                                : detalle.getDescripcionProducto()
                );

                producto.setIdCategoria(request.getIdCategoria());

                producto.setPrecioCompra(
                        request.getPrecioCompra() != null
                                ? request.getPrecioCompra()
                                : detalle.getPrecioEstimado()
                );

                producto.setPrecioVenta(
                        request.getPrecioVenta() != null
                                ? request.getPrecioVenta()
                                : BigDecimal.ZERO
                );

                producto.setStock(0);

                producto.setStockMinimo(
                        request.getStockMinimo() != null
                                ? request.getStockMinimo()
                                : 0
                );

                producto.setActivo(true);

            } else {
                if (producto.getActivo() == null || !producto.getActivo()) {
                    throw new IllegalArgumentException(
                            "El producto encontrado está inactivo"
                    );
                }
            }

            if (producto.getStock() == null) {
                producto.setStock(0);
            }

            // Incrementar stock con la cantidad realmente recibida.
            producto.setStock(producto.getStock() + cantidad);

            Producto productoGuardado = productoRepository.save(producto);

            // Registrar movimiento ENTRADA sin llamar al servicio de inventario.
            // Así toda la conversión queda dentro de esta misma transacción.
            MovimientoInventario movimiento = new MovimientoInventario();

            movimiento.setIdProducto(productoGuardado.getIdProducto());
            movimiento.setIdUsuario(pedido.getIdUsuario());
            movimiento.setTipo("ENTRADA");
            movimiento.setCantidad(cantidad);

            movimiento.setMotivo(
                    request.getMotivo() != null &&
                            !request.getMotivo().trim().isEmpty()
                            ? request.getMotivo()
                            : "Entrada por pedido de cliente #" + idPedido
            );

            movimiento.setFecha(LocalDateTime.now());

            movimientoRepository.save(movimiento);

            // Vincular el detalle con el producto del catálogo.
            detalle.setIdProducto(productoGuardado.getIdProducto());
            detalleRepository.save(detalle);

            // Solo cerrar el pedido cuando todos sus detalles estén convertidos.
            List<DetallePedidoCliente> detalles =
                    detalleRepository.findByPedido_IdPedido(idPedido);

            boolean todosConvertidos = detalles.stream()
                    .allMatch(d -> d.getIdProducto() != null);

            if (todosConvertidos) {
                pedido.setEstado("CONVERTIDO");
                pedido.setFechaConversion(LocalDateTime.now());
                pedidoRepository.save(pedido);
            }

            return respuesta(
                    productoNuevo
                            ? "Producto creado y registrado en inventario"
                            : "Stock del producto actualizado correctamente",
                    productoGuardado
            );

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus()
                    .setRollbackOnly();

            return respuesta(
                    "Error al convertir pedido en producto: " + e.getMessage(),
                    null
            );
        }
    }
}