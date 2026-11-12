package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Pago;

public interface PagoService {
    ResponseDTO listarPagos();
    ResponseDTO listarPorVenta(Long idVenta);
    ResponseDTO registrarPago(Pago request);
}
