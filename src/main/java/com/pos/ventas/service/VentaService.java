package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.dto.VentaRequestDTO;

public interface VentaService {
    ResponseDTO listarVentas();
    ResponseDTO buscarPorId(Long id);
    ResponseDTO registrarVenta(VentaRequestDTO request);
    ResponseDTO anularVenta(Long id);
}
