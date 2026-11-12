package com.pos.ventas.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RegistrarProductoPedidoDTO {

    private String codigo;
    private String nombre;
    private String descripcion;

    private Long idCategoria;

    private BigDecimal precioCompra;
    private BigDecimal precioVenta;

    private Integer stockMinimo;

    // Si no se especifica, se utiliza la cantidad del pedido.
    private Integer cantidadRecibida;

    private String motivo;
}