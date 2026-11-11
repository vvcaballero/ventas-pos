package com.pos.ventas.repository;

import com.pos.ventas.entity.PedidoCliente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoClienteRepository extends JpaRepository<PedidoCliente, Long> {

    @EntityGraph(attributePaths = {"detalles"})
    List<PedidoCliente> findAllByOrderByFechaPedidoDesc();

    @EntityGraph(attributePaths = {"detalles"})
    Optional<PedidoCliente> findByIdPedido(Long idPedido);

    @EntityGraph(attributePaths = {"detalles"})
    @Query("SELECT p FROM PedidoCliente p " +
            "WHERE p.estado NOT IN ('CONVERTIDO', 'CANCELADO') " +
            "ORDER BY p.fechaPedido DESC")
    List<PedidoCliente> findPendientes();

    @Query("SELECT p FROM PedidoCliente p WHERE p.idPedido = :id")
    @org.springframework.data.jpa.repository.Lock(
            javax.persistence.LockModeType.PESSIMISTIC_WRITE
    )
    Optional<PedidoCliente> findByIdForUpdate(@Param("id") Long id);
}