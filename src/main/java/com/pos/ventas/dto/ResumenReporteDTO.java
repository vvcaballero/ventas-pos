package com.pos.ventas.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumenReporteDTO {

    private Long cantidadVentas;
    private BigDecimal subtotal;
    private BigDecimal descuentos;
    private BigDecimal totalVentas;
}