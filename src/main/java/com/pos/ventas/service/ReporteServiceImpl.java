package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.dto.VentaDiariaDTO;
import com.pos.ventas.entity.Venta;

import com.pos.ventas.repository.DetalleVentaRepository;
import com.pos.ventas.repository.MovimientoInventarioRepository;
import com.pos.ventas.repository.PagoRepository;
import com.pos.ventas.repository.ProductoRepository;
import com.pos.ventas.repository.VentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

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

    @Autowired
    private PagoRepository pagoRepository;

    // =====================================================
    // MÉTODOS AUXILIARES
    // =====================================================

    private ResponseDTO ejecutar(Supplier<Object> operacion, String mensajeError) {

        ResponseDTO response = new ResponseDTO();

        try {
            response.setMensaje("OK");
            response.setBody(operacion.get());

        } catch (Exception e) {
            response.setMensaje(mensajeError + ": " + e.getMessage());
            response.setBody(null);
        }

        return response;
    }

    private int ajustarLimite(int limite, int porDefecto) {
        return (limite <= 0 || limite > 100) ? porDefecto : limite;
    }

    private LocalDateTime obtenerInicio(LocalDate desde) {
        validarFechas(desde, desde);
        return desde.atStartOfDay();
    }

    private LocalDateTime obtenerFin(LocalDate hasta) {
        return hasta.plusDays(1).atStartOfDay();
    }

    private void validarFechas(LocalDate desde, LocalDate hasta) {

        if (desde == null || hasta == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar las fechas desde y hasta"
            );
        }

        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException(
                    "La fecha hasta no puede ser anterior a desde"
            );
        }
    }

    // =====================================================
    // VENTAS DEL DÍA
    // =====================================================

    private Map<String, Object> calcularVentasDelDia() {

        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1);

        long cantidad = ventaRepository
                .findByEstadoAndFechaVentaGreaterThanEqualAndFechaVentaLessThanOrderByFechaVentaAsc(
                        "PAGADA", inicio, fin
                ).size();

        BigDecimal total = ventaRepository.sumTotalPorRango(
                "PAGADA", inicio, fin
        );

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("cantidadVentas", cantidad);
        data.put("totalVentas", total);

        return data;
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    @Override
    public ResponseDTO dashboard() {

        return ejecutar(() -> {

            Map<String, Object> data = new LinkedHashMap<>();

            data.put("ventasDelDia", calcularVentasDelDia());

            data.put("productosRegistrados",
                    productoRepository.countByActivoTrue());

            data.put("ventasRealizadas",
                    ventaRepository.countByEstado("PAGADA"));

            data.put("productosStockBajo",
                    productoRepository.findStockBajo());

            data.put("ultimasVentas",
                    ventaRepository.findUltimasVentas(PageRequest.of(0, 5)));

            data.put("productosMasVendidos",
                    detalleVentaRepository.findMasVendidos(PageRequest.of(0, 5)));

            data.put("movimientosRecientes",
                    movimientoRepository.findRecientes(PageRequest.of(0, 10)));

            return data;

        }, "Error al generar el dashboard");
    }

    // =====================================================
    // VENTAS DEL DÍA
    // =====================================================

    @Override
    public ResponseDTO ventasDelDia() {

        return ejecutar(
                () -> calcularVentasDelDia(),
                "Error al calcular ventas del día"
        );
    }

    // =====================================================
    // PRODUCTOS CON STOCK BAJO
    // =====================================================

    @Override
    public ResponseDTO productosStockBajo() {

        return ejecutar(
                () -> productoRepository.findStockBajo(),
                "Error al consultar stock bajo"
        );
    }

    // =====================================================
    // PRODUCTOS MÁS VENDIDOS
    // =====================================================

    @Override
    public ResponseDTO productosMasVendidos(int limite) {

        return ejecutar(
                () -> detalleVentaRepository.findMasVendidos(
                        PageRequest.of(0, ajustarLimite(limite, 5))
                ),
                "Error al consultar productos más vendidos"
        );
    }

    // =====================================================
    // ÚLTIMAS VENTAS
    // =====================================================

    @Override
    public ResponseDTO ultimasVentas(int limite) {

        return ejecutar(
                () -> ventaRepository.findUltimasVentas(
                        PageRequest.of(0, ajustarLimite(limite, 5))
                ),
                "Error al consultar últimas ventas"
        );
    }

    // =====================================================
    // MOVIMIENTOS RECIENTES
    // =====================================================

    @Override
    public ResponseDTO movimientosRecientes(int limite) {

        return ejecutar(
                () -> movimientoRepository.findRecientes(
                        PageRequest.of(0, ajustarLimite(limite, 10))
                ),
                "Error al consultar movimientos recientes"
        );
    }

    // =====================================================
    // RESUMEN FINANCIERO POR PERÍODO
    // =====================================================

    @Override
    public ResponseDTO resumenPeriodo(LocalDate desde, LocalDate hasta) {

        return ejecutar(() -> {

            validarFechas(desde, hasta);

            return ventaRepository.obtenerResumenPeriodo(
                    obtenerInicio(desde),
                    obtenerFin(hasta)
            );

        }, "Error al generar resumen financiero");
    }

    // =====================================================
    // VENTAS AGRUPADAS POR DÍA
    // =====================================================

    @Override
    public ResponseDTO ventasDiarias(LocalDate desde, LocalDate hasta) {

        return ejecutar(() -> {

            validarFechas(desde, hasta);

            List<Venta> ventas =
                    ventaRepository
                            .findByEstadoAndFechaVentaGreaterThanEqualAndFechaVentaLessThanOrderByFechaVentaAsc(
                                    "PAGADA",
                                    obtenerInicio(desde),
                                    obtenerFin(hasta)
                            );

            Map<LocalDate, VentaDiariaDTO> agrupadas = new TreeMap<>();

            for (Venta venta : ventas) {

                LocalDate fecha = venta.getFechaVenta().toLocalDate();

                VentaDiariaDTO dto = agrupadas.get(fecha);

                if (dto == null) {

                    dto = new VentaDiariaDTO(
                            fecha,
                            0L,
                            BigDecimal.ZERO
                    );

                    agrupadas.put(fecha, dto);
                }

                dto.setCantidadVentas(dto.getCantidadVentas() + 1);

                dto.setTotalVentas(
                        dto.getTotalVentas().add(venta.getTotal())
                );
            }

            return new ArrayList<>(agrupadas.values());

        }, "Error al consultar ventas diarias");
    }

    // =====================================================
    // VENTAS POR USUARIO
    // =====================================================

    @Override
    public ResponseDTO ventasPorUsuario(LocalDate desde, LocalDate hasta) {

        return ejecutar(() -> {

            validarFechas(desde, hasta);

            return ventaRepository.obtenerVentasPorUsuario(
                    obtenerInicio(desde),
                    obtenerFin(hasta)
            );

        }, "Error al consultar ventas por usuario");
    }

    // =====================================================
    // VENTAS POR CLIENTE
    // =====================================================

    @Override
    public ResponseDTO ventasPorCliente(LocalDate desde, LocalDate hasta) {

        return ejecutar(() -> {

            validarFechas(desde, hasta);

            return ventaRepository.obtenerVentasPorCliente(
                    obtenerInicio(desde),
                    obtenerFin(hasta)
            );

        }, "Error al consultar ventas por cliente");
    }

    // =====================================================
    // MÉTODOS DE PAGO
    // =====================================================

    @Override
    public ResponseDTO metodosPago(LocalDate desde, LocalDate hasta) {

        return ejecutar(() -> {

            validarFechas(desde, hasta);

            return pagoRepository.obtenerTotalesPorMetodoPago(
                    obtenerInicio(desde),
                    obtenerFin(hasta)
            );

        }, "Error al consultar métodos de pago");
    }

    // =====================================================
    // PRODUCTOS MÁS VENDIDOS POR PERÍODO
    // =====================================================

    @Override
    public ResponseDTO productosMasVendidosPeriodo(
            LocalDate desde,
            LocalDate hasta,
            int limite
    ) {

        return ejecutar(() -> {

            validarFechas(desde, hasta);

            return detalleVentaRepository.findMasVendidosPorPeriodo(
                    obtenerInicio(desde),
                    obtenerFin(hasta),
                    PageRequest.of(0, ajustarLimite(limite, 5))
            );

        }, "Error al consultar productos más vendidos por período");
    }
}