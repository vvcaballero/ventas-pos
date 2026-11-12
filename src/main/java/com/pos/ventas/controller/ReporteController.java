package com.pos.ventas.controller;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.service.ReporteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // =====================================================
    // DASHBOARD
    // =====================================================

    @GetMapping("/dashboard")
    public ResponseEntity<ResponseDTO> dashboard() {
        return ResponseEntity.ok(reporteService.dashboard());
    }

    // =====================================================
    // REPORTES ACTUALES
    // =====================================================

    @GetMapping("/ventas-hoy")
    public ResponseEntity<ResponseDTO> ventasDelDia() {
        return ResponseEntity.ok(reporteService.ventasDelDia());
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<ResponseDTO> stockBajo() {
        return ResponseEntity.ok(reporteService.productosStockBajo());
    }

    @GetMapping("/mas-vendidos")
    public ResponseEntity<ResponseDTO> masVendidos(
            @RequestParam(defaultValue = "5") int limite) {

        return ResponseEntity.ok(
                reporteService.productosMasVendidos(limite)
        );
    }

    @GetMapping("/ultimas-ventas")
    public ResponseEntity<ResponseDTO> ultimasVentas(
            @RequestParam(defaultValue = "5") int limite) {

        return ResponseEntity.ok(
                reporteService.ultimasVentas(limite)
        );
    }

    @GetMapping("/movimientos-recientes")
    public ResponseEntity<ResponseDTO> movimientosRecientes(
            @RequestParam(defaultValue = "10") int limite) {

        return ResponseEntity.ok(
                reporteService.movimientosRecientes(limite)
        );
    }

    // =====================================================
    // REPORTES POR FECHAS
    // =====================================================

    @GetMapping("/resumen")
    public ResponseEntity<ResponseDTO> resumenPeriodo(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta) {

        return ResponseEntity.ok(
                reporteService.resumenPeriodo(desde, hasta)
        );
    }

    @GetMapping("/ventas-diarias")
    public ResponseEntity<ResponseDTO> ventasDiarias(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta) {

        return ResponseEntity.ok(
                reporteService.ventasDiarias(desde, hasta)
        );
    }

    @GetMapping("/por-usuario")
    public ResponseEntity<ResponseDTO> ventasPorUsuario(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta) {

        return ResponseEntity.ok(
                reporteService.ventasPorUsuario(desde, hasta)
        );
    }

    @GetMapping("/por-cliente")
    public ResponseEntity<ResponseDTO> ventasPorCliente(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta) {

        return ResponseEntity.ok(
                reporteService.ventasPorCliente(desde, hasta)
        );
    }

    @GetMapping("/metodos-pago")
    public ResponseEntity<ResponseDTO> metodosPago(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta) {

        return ResponseEntity.ok(
                reporteService.metodosPago(desde, hasta)
        );
    }

    @GetMapping("/productos-periodo")
    public ResponseEntity<ResponseDTO> productosMasVendidosPeriodo(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,

            @RequestParam(defaultValue = "5") int limite) {

        return ResponseEntity.ok(
                reporteService.productosMasVendidosPeriodo(
                        desde, hasta, limite
                )
        );
    }
}