package com.pos.ventas.controller;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Pago;
import com.pos.ventas.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> listar() {
        return ResponseEntity.ok(pagoService.listarPagos());
    }

    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<ResponseDTO> listarPorVenta(@PathVariable Long idVenta) {
        return ResponseEntity.ok(pagoService.listarPorVenta(idVenta));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> registrar(@RequestBody Pago request) {
        return ResponseEntity.ok(pagoService.registrarPago(request));
    }
}
