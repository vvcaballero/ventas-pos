package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;

public interface DetalleVentaService {
    ResponseDTO listarPorVenta(Long idVenta);
}
