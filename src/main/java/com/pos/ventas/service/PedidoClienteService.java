package com.pos.ventas.service;

import com.pos.ventas.dto.EstadoPedidoRequestDTO;
import com.pos.ventas.dto.PedidoClienteRequestDTO;
import com.pos.ventas.dto.RegistrarProductoPedidoDTO;
import com.pos.ventas.dto.ResponseDTO;

public interface PedidoClienteService {

    ResponseDTO listarTodos();

    ResponseDTO listarPendientes();

    ResponseDTO buscarPorId(Long idPedido);

    ResponseDTO registrarPedido(PedidoClienteRequestDTO request);

    ResponseDTO actualizarEstado(
            Long idPedido,
            EstadoPedidoRequestDTO request
    );

    ResponseDTO convertirProducto(
            Long idPedido,
            Long idDetallePedido,
            RegistrarProductoPedidoDTO request
    );
}