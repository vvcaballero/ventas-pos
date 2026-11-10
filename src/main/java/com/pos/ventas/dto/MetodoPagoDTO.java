package com.pos.ventas.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetodoPagoDTO {

    private String metodoPago;
    private BigDecimal total;
    private Long cantidadPagos;
}