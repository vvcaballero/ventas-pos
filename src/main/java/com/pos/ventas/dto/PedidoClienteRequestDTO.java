package com.pos.ventas.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PedidoClienteRequestDTO {

    private Long idCliente;
    private Long idUsuario;
    private String observaciones;

    private List<DetallePedidoClienteRequestDTO> detalles;
}