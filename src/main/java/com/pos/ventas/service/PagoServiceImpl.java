package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Pago;
import com.pos.ventas.entity.Venta;
import com.pos.ventas.repository.PagoRepository;
import com.pos.ventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class PagoServiceImpl implements PagoService {

    private static final List<String> METODOS_VALIDOS = Arrays.asList("EFECTIVO", "TARJETA", "TRANSFERENCIA");

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private VentaRepository ventaRepository;

    @Override
    public ResponseDTO listarPagos() {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(pagoRepository.findAll());
        } catch (Exception e) {
            response.setMensaje("Error al listar pagos: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO listarPorVenta(Long idVenta) {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(pagoRepository.findByIdVenta(idVenta));
        } catch (Exception e) {
            response.setMensaje("Error al listar pagos de la venta: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    /** Registra el pago y, si ya se cubrió el total, marca la venta como PAGADA. */
    @Override
    @Transactional
    public ResponseDTO registrarPago(Pago request) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (request == null || request.getIdVenta() == null) {
                throw new IllegalArgumentException("La venta es obligatoria");
            }
            if (request.getMetodoPago() == null
                    || !METODOS_VALIDOS.contains(request.getMetodoPago().toUpperCase())) {
                throw new IllegalArgumentException("Método de pago inválido. Permitidos: " + METODOS_VALIDOS);
            }
            if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor a 0");
            }

            Venta venta = ventaRepository.findById(request.getIdVenta())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Venta no encontrada con ID: " + request.getIdVenta()));

            if ("ANULADA".equals(venta.getEstado())) {
                throw new IllegalArgumentException("No se puede pagar una venta anulada");
            }
            if ("PAGADA".equals(venta.getEstado())) {
                throw new IllegalArgumentException("La venta ya está pagada");
            }

            request.setMetodoPago(request.getMetodoPago().toUpperCase());
            request.setFechaPago(LocalDateTime.now());
            Pago guardado = pagoRepository.save(request);

            BigDecimal pagado = pagoRepository.findByIdVenta(venta.getIdVenta()).stream()
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (pagado.compareTo(venta.getTotal()) >= 0) {
                venta.setEstado("PAGADA");
                ventaRepository.save(venta);
            }

            response.setMensaje("OK");
            response.setBody(guardado);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setMensaje("Error al registrar pago: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }
}
