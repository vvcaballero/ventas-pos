package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.repository.DetalleVentaRepository;
import com.pos.ventas.repository.MovimientoInventarioRepository;
import com.pos.ventas.repository.ProductoRepository;
import com.pos.ventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    // ---------- helpers que devuelven datos crudos ----------
    private Map<String, Object> calcularVentasDelDia() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("cantidadVentas", ventaRepository.countByEstadoAndFechaVentaBetween("PAGADA", inicio, fin));
        data.put("totalVentas", ventaRepository.sumTotalPorRango("PAGADA", inicio, fin));
        return data;
    }

    private int ajustarLimite(int limite, int porDefecto) {
        return (limite <= 0 || limite > 100) ? porDefecto : limite;
    }

    // ---------- endpoints ----------
    @Override
    public ResponseDTO dashboard() {
        ResponseDTO response = new ResponseDTO();
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("ventasDelDia", calcularVentasDelDia());
            data.put("productosRegistrados", productoRepository.countByActivoTrue());
            data.put("ventasRealizadas", ventaRepository.countByEstado("PAGADA"));
            data.put("productosStockBajo", productoRepository.findStockBajo());
            data.put("ultimasVentas", ventaRepository.findUltimasVentas(PageRequest.of(0, 5)));
            data.put("productosMasVendidos", detalleVentaRepository.findMasVendidos(PageRequest.of(0, 5)));
            data.put("movimientosRecientes", movimientoRepository.findRecientes(PageRequest.of(0, 10)));

            response.setMensaje("OK");
            response.setBody(data);
        } catch (Exception e) {
            response.setMensaje("Error al generar el dashboard: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO ventasDelDia() {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(calcularVentasDelDia());
        } catch (Exception e) {
            response.setMensaje("Error al calcular ventas del día: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO productosStockBajo() {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(productoRepository.findStockBajo());
        } catch (Exception e) {
            response.setMensaje("Error al consultar stock bajo: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO productosMasVendidos(int limite) {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(detalleVentaRepository.findMasVendidos(PageRequest.of(0, ajustarLimite(limite, 5))));
        } catch (Exception e) {
            response.setMensaje("Error al consultar productos más vendidos: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO ultimasVentas(int limite) {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(ventaRepository.findUltimasVentas(PageRequest.of(0, ajustarLimite(limite, 5))));
        } catch (Exception e) {
            response.setMensaje("Error al consultar últimas ventas: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO movimientosRecientes(int limite) {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(movimientoRepository.findRecientes(PageRequest.of(0, ajustarLimite(limite, 10))));
        } catch (Exception e) {
            response.setMensaje("Error al consultar movimientos recientes: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }
}
