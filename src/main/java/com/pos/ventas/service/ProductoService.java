package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    ResponseDTO listarProductos();
    ResponseDTO buscarPorId(Long id);
    ResponseDTO insertarProducto(Producto request);
    ResponseDTO actualizarProducto(Long id, Producto request);
    ResponseDTO eliminarLogicoProducto(Long id);

}
