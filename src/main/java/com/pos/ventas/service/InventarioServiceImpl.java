package com.pos.ventas.service;

import com.pos.ventas.dto.MovimientoRequestDTO;
import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.MovimientoInventario;
import com.pos.ventas.entity.Producto;
import com.pos.ventas.repository.MovimientoInventarioRepository;
import com.pos.ventas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public ResponseDTO listarMovimientos() {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(
                    movimientoRepository.findAll(
                            Sort.by(Sort.Direction.DESC, "fecha")
                    )
            );
        } catch (Exception e) {
            response.setMensaje("Error al listar movimientos: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO listarPorProducto(Long idProducto) {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(movimientoRepository.findByIdProductoOrderByFechaDesc(idProducto));
        } catch (Exception e) {
            response.setMensaje("Error al listar movimientos del producto: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    /**
     * ENTRADA: suma stock (cantidad > 0)
     * SALIDA : resta stock (cantidad > 0, no puede dejar stock negativo)
     * AJUSTE : suma o resta (cantidad positiva o negativa, distinta de 0)
     */
    @Override
    @Transactional
    public ResponseDTO registrarMovimiento(MovimientoRequestDTO request) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (request == null || request.getIdProducto() == null || request.getCantidad() == null
                    || request.getTipoMovimiento() == null || request.getIdUsuario() == null) {
                throw new IllegalArgumentException("Producto, tipo, cantidad y usuario son obligatorios");
            }

            String tipo = request.getTipoMovimiento().toUpperCase();
            int cantidad = request.getCantidad();

            Producto prod = productoRepository.findById(request.getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado con ID: " + request.getIdProducto()));

            int nuevoStock;
            switch (tipo) {
                case "ENTRADA":
                    if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
                    nuevoStock = prod.getStock() + cantidad;
                    break;
                case "SALIDA":
                    if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
                    nuevoStock = prod.getStock() - cantidad;
                    break;
                case "AJUSTE":
                    if (cantidad == 0) throw new IllegalArgumentException("El ajuste no puede ser 0");
                    nuevoStock = prod.getStock() + cantidad;
                    break;
                default:
                    throw new IllegalArgumentException("Tipo inválido. Permitidos: ENTRADA, SALIDA, AJUSTE");
            }

            if (nuevoStock < 0) {
                throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + prod.getStock());
            }

            prod.setStock(nuevoStock);
            productoRepository.save(prod);

            MovimientoInventario mov = new MovimientoInventario();
            mov.setIdProducto(prod.getIdProducto());
            mov.setTipo(tipo);
            mov.setCantidad(cantidad);
            mov.setMotivo(request.getMotivo());
            mov.setIdUsuario(request.getIdUsuario());
            mov.setFecha(LocalDateTime.now());

            response.setMensaje("OK");
            response.setBody(movimientoRepository.save(mov));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setMensaje("Error al registrar movimiento: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }
}
