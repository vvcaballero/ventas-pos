package com.pos.ventas.repository;

import com.pos.ventas.dto.MetodoPagoDTO;
import com.pos.ventas.entity.Pago;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByIdVenta(Long idVenta);

    // Ingresos agrupados por método de pago.
    @Query("SELECT new com.pos.ventas.dto.MetodoPagoDTO(" +
            "p.metodoPago, COALESCE(SUM(p.monto), 0), COUNT(p.idPago)) " +
            "FROM Pago p, Venta v " +
            "WHERE p.idVenta = v.idVenta " +
            "AND v.estado = 'PAGADA' " +
            "AND v.fechaVenta >= :inicio " +
            "AND v.fechaVenta < :fin " +
            "GROUP BY p.metodoPago " +
            "ORDER BY SUM(p.monto) DESC")
    List<MetodoPagoDTO> obtenerTotalesPorMetodoPago(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}