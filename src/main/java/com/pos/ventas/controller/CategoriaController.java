package com.pos.ventas.controller;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Categoria;
import com.pos.ventas.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> listar() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> insertar(@RequestBody Categoria request) {
        return ResponseEntity.ok(categoriaService.insertarCategoria(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> actualizar(@PathVariable Long id, @RequestBody Categoria request) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> eliminarLogico(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.eliminarLogicoCategoria(id));
    }
}
