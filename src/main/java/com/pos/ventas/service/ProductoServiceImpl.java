package com.pos.ventas.service;


import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Producto;
import com.pos.ventas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public ResponseDTO listarProductos() {
        ResponseDTO response = new ResponseDTO();
        try {
            List<Producto> lista = productoRepository.findAll();
            response.setMensaje("OK");
            response.setBody(lista);
        } catch (Exception e) {
            response.setMensaje("Error al listar productos: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO buscarPorId(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            if (producto.isPresent()) {
                response.setMensaje("OK");
                response.setBody(producto.get());
            } else {
                response.setMensaje("Producto no encontrado con el ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al buscar el producto: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO insertarProducto(Producto request) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (request != null) {
                // Por defecto al crear aseguramos que esté activo
                if (request.getActivo() == null) {
                    request.setActivo(true);
                }
                Producto insertProducto = productoRepository.save(request);
                response.setMensaje("OK");
                response.setBody(insertProducto);
            } else {
                response.setMensaje("El producto no puede ser nulo");
                response.setBody(null);
            }
        } catch (RuntimeException e) {
            response.setMensaje("Error al insertar producto: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO actualizarProducto(Long id, Producto request) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Producto> productoExistente = productoRepository.findById(id);
            if (productoExistente.isPresent()) {
                Producto prod = productoExistente.get();
                prod.setCodigo(request.getCodigo());
                prod.setNombre(request.getNombre());
                prod.setDescripcion(request.getDescripcion());
                prod.setIdCategoria(request.getIdCategoria());
                prod.setPrecioCompra(request.getPrecioCompra());
                prod.setPrecioVenta(request.getPrecioVenta());
                prod.setStock(request.getStock());
                prod.setStockMinimo(request.getStockMinimo());
                if (request.getActivo() != null) {
                    prod.setActivo(request.getActivo());
                }

                Producto productoActualizado = productoRepository.save(prod);
                response.setMensaje("OK");
                response.setBody(productoActualizado);
            } else {
                response.setMensaje("Producto no encontrado para actualizar con ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al actualizar producto: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO eliminarLogicoProducto(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Producto> productoExistente = productoRepository.findById(id);
            if (productoExistente.isPresent()) {
                Producto prod = productoExistente.get();
                prod.setActivo(false); // <-- BORRADO LÓGICO (cambia a falso)
                Producto productoBorrado = productoRepository.save(prod);

                response.setMensaje("OK");
                response.setBody(productoBorrado); // Retornamos el objeto desactivado
            } else {
                response.setMensaje("Producto no encontrado con ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al realizar el borrado lógico: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }
}
