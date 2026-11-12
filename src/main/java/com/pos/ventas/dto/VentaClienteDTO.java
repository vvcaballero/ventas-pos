package com.pos.ventas.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaClienteDTO {

    private Long idCliente;
    private String nombreCliente;
    private Long cantidadCompras;
    private BigDecimal totalComprado;
}