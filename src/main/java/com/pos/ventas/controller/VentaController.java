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
        ResponseDTO r = ventaService.buscarPorId(id);
        return r.getBody() == null ? ResponseEntity.status(404).body(r) : ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> registrar(@RequestBody VentaRequestDTO request) {
        ResponseDTO r = ventaService.registrarVenta(request);
        return r.getBody() == null ? ResponseEntity.badRequest().body(r) : ResponseEntity.ok(r);
    }

    // Una venta no se borra físicamente: se cancela y se devuelve el stock
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> cancelar(@PathVariable Long id) {
        ResponseDTO r = ventaService.anularVenta(id);
        return r.getBody() == null ? ResponseEntity.badRequest().body(r) : ResponseEntity.ok(r);
    }
}