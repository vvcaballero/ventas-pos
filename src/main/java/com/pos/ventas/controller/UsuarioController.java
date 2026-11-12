package com.pos.ventas.controller;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Usuario;
import com.pos.ventas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> insertar(@RequestBody Usuario request) {
        return ResponseEntity.ok(usuarioService.insertarUsuario(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> actualizar(@PathVariable Long id, @RequestBody Usuario request) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> eliminarLogico(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.eliminarLogicoUsuario(id));
    }
}
