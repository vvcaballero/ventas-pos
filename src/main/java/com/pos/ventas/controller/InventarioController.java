package com.pos.ventas.controller;

import com.pos.ventas.dto.MovimientoRequestDTO;
import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> listar() {
        return ResponseEntity.ok(inventarioService.listarMovimientos());
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<ResponseDTO> listarPorProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(inventarioService.listarPorProducto(idProducto));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> registrar(@RequestBody MovimientoRequestDTO request) {
        return ResponseEntity.ok(inventarioService.registrarMovimiento(request));
    }
}
