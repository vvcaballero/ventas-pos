package com.pos.ventas.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaDiariaDTO {

    private LocalDate fecha;
    private Long cantidadVentas;
    private BigDecimal totalVentas;
}