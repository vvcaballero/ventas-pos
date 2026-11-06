package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;

public interface ReporteService {
    ResponseDTO dashboard();
    ResponseDTO ventasDelDia();
    ResponseDTO productosStockBajo();
    ResponseDTO productosMasVendidos(int limite);
    ResponseDTO ultimasVentas(int limite);
    ResponseDTO movimientosRecientes(int limite);
}
