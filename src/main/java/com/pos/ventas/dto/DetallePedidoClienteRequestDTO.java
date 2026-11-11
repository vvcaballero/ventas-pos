package com.pos.ventas.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DetallePedidoClienteRequestDTO {

    private String descripcionProducto;
    private Integer cantidad;
    private BigDecimal precioEstimado;
    private String observaciones;
}