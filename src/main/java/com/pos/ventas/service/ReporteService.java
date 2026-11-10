package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;

import java.time.LocalDate;

public interface ReporteService {

    // Dashboard.
    ResponseDTO dashboard();

    // Reportes actuales.
    ResponseDTO ventasDelDia();

    ResponseDTO productosStockBajo();

    ResponseDTO productosMasVendidos(int limite);

    ResponseDTO ultimasVentas(int limite);

    ResponseDTO movimientosRecientes(int limite);

    // Reportes por período.
    ResponseDTO resumenPeriodo(LocalDate desde, LocalDate hasta);

    ResponseDTO ventasDiarias(LocalDate desde, LocalDate hasta);

    ResponseDTO ventasPorUsuario(LocalDate desde, LocalDate hasta);

    ResponseDTO ventasPorCliente(LocalDate desde, LocalDate hasta);

    ResponseDTO metodosPago(LocalDate desde, LocalDate hasta);

    ResponseDTO productosMasVendidosPeriodo(
            LocalDate desde,
            LocalDate hasta,
            int limite
    );
}