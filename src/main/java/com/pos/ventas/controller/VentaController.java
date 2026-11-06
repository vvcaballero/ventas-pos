package com.pos.ventas.controller;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.dto.VentaRequestDTO;
import com.pos.ventas.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> listar() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> registrar(@RequestBody VentaRequestDTO request) {
        return ResponseEntity.ok(ventaService.registrarVenta(request));
    }

    // Una venta no se borra: se anula y se devuelve el stock
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anularVenta(id));
    }
}
