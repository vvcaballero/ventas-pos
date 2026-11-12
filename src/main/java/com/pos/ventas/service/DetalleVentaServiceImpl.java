package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.repository.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleVentaServiceImpl implements DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Override
    public ResponseDTO listarPorVenta(Long idVenta) {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(detalleVentaRepository.findByIdVenta(idVenta));
        } catch (Exception e) {
            response.setMensaje("Error al listar detalle de la venta: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }
}
