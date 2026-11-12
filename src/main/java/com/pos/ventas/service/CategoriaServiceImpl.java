package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Categoria;
import com.pos.ventas.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public ResponseDTO listarCategorias() {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(categoriaRepository.findAll());
        } catch (Exception e) {
            response.setMensaje("Error al listar categorías: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO buscarPorId(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Categoria> categoria = categoriaRepository.findById(id);
            if (categoria.isPresent()) {
                response.setMensaje("OK");
                response.setBody(categoria.get());
            } else {
                response.setMensaje("Categoría no encontrada con el ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al buscar la categoría: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO insertarCategoria(Categoria request) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (request == null || request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                response.setMensaje("El nombre de la categoría es obligatorio");
                response.setBody(null);
                return response;
            }
            if (request.getActivo() == null) {
                request.setActivo(true);
            }
            response.setMensaje("OK");
            response.setBody(categoriaRepository.save(request));
        } catch (Exception e) {
            response.setMensaje("Error al insertar categoría: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO actualizarCategoria(Long id, Categoria request) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Categoria> existente = categoriaRepository.findById(id);
            if (existente.isPresent()) {
                Categoria cat = existente.get();
                cat.setNombre(request.getNombre());
                cat.setDescripcion(request.getDescripcion());
                if (request.getActivo() != null) {
                    cat.setActivo(request.getActivo());
                }
                response.setMensaje("OK");
                response.setBody(categoriaRepository.save(cat));
            } else {
                response.setMensaje("Categoría no encontrada para actualizar con ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al actualizar categoría: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO eliminarLogicoCategoria(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Categoria> existente = categoriaRepository.findById(id);
            if (existente.isPresent()) {
                Categoria cat = existente.get();
                cat.setActivo(false);
                response.setMensaje("OK");
                response.setBody(categoriaRepository.save(cat));
            } else {
                response.setMensaje("Categoría no encontrada con ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al realizar el borrado lógico: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }
}
