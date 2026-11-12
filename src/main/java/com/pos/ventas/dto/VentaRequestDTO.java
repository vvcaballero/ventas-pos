package com.pos.ventas.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDTO {
    private Long idCliente;                 // opcional
    private Long idUsuario;
    private BigDecimal descuento;           // opcional, monto en dinero
    private List<DetalleRequestDTO> detalles;
    private List<PagoRequestDTO> pagos;     // obligatorio
}