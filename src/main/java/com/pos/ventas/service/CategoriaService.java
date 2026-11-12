package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Categoria;

public interface CategoriaService {
    ResponseDTO listarCategorias();
    ResponseDTO buscarPorId(Long id);
    ResponseDTO insertarCategoria(Categoria request);
    ResponseDTO actualizarCategoria(Long id, Categoria request);
    ResponseDTO eliminarLogicoCategoria(Long id);
}
