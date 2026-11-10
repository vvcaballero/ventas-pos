package com.pos.ventas.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaUsuarioDTO {

    private Long idUsuario;
    private String nombreUsuario;
    private Long cantidadVentas;
    private BigDecimal totalVentas;
}