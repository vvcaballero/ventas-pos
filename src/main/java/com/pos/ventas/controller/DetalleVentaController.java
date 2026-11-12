package com.pos.ventas.controller;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.service.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    // El detalle se crea dentro de POST /api/ventas; aquí solo se consulta
    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<ResponseDTO> listarPorVenta(@PathVariable Long idVenta) {
        return ResponseEntity.ok(detalleVentaService.listarPorVenta(idVenta));
    }
}
