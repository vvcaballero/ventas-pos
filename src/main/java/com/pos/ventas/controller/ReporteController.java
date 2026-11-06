package com.pos.ventas.controller;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // Todo el dashboard en una sola llamada
    @GetMapping("/dashboard")
    public ResponseEntity<ResponseDTO> dashboard() {
        return ResponseEntity.ok(reporteService.dashboard());
    }

    @GetMapping("/ventas-hoy")
    public ResponseEntity<ResponseDTO> ventasDelDia() {
        return ResponseEntity.ok(reporteService.ventasDelDia());
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<ResponseDTO> stockBajo() {
        return ResponseEntity.ok(reporteService.productosStockBajo());
    }

    @GetMapping("/mas-vendidos")
    public ResponseEntity<ResponseDTO> masVendidos(@RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(reporteService.productosMasVendidos(limite));
    }

    @GetMapping("/ultimas-ventas")
    public ResponseEntity<ResponseDTO> ultimasVentas(@RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(reporteService.ultimasVentas(limite));
    }

    @GetMapping("/movimientos-recientes")
    public ResponseEntity<ResponseDTO> movimientosRecientes(@RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(reporteService.movimientosRecientes(limite));
    }
}
