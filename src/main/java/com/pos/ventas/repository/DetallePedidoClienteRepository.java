package com.pos.ventas.repository;

import com.pos.ventas.entity.DetallePedidoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetallePedidoClienteRepository
        extends JpaRepository<DetallePedidoCliente, Long> {

    List<DetallePedidoCliente> findByPedido_IdPedido(Long idPedido);
}