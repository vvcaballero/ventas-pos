package com.pos.ventas.service;

import com.pos.ventas.dto.MovimientoRequestDTO;
import com.pos.ventas.dto.ResponseDTO;

public interface InventarioService {
    ResponseDTO listarMovimientos();
    ResponseDTO listarPorProducto(Long idProducto);
    ResponseDTO registrarMovimiento(MovimientoRequestDTO request);
}
