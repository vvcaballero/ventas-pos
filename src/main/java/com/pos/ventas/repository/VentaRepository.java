package com.pos.ventas.repository;

import com.pos.ventas.dto.*;
import com.pos.ventas.entity.Venta;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findAllByOrderByFechaVentaDesc();

    long countByEstado(String estado);

    long countByEstadoAndFechaVentaBetween(
            String estado,
            LocalDateTime inicio,
            LocalDateTime fin
    );

    // Ventas pagadas de un período.
    // El fin es exclusivo: fechaVenta < fin.
    List<Venta> findByEstadoAndFechaVentaGreaterThanEqualAndFechaVentaLessThanOrderByFechaVentaAsc(
            String estado,
            LocalDateTime inicio,
            LocalDateTime fin
    );

    // Total vendido en un período.
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
            "WHERE v.estado = :estado " +
            "AND v.fechaVenta >= :inicio " +
            "AND v.fechaVenta < :fin")
    BigDecimal sumTotalPorRango(
            @Param("estado") String estado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Resumen financiero del período.
    @Query("SELECT new com.pos.ventas.dto.ResumenReporteDTO(" +
            "COUNT(v.idVenta), " +
            "COALESCE(SUM(v.subtotal), 0), " +
            "COALESCE(SUM(v.descuento), 0), " +
            "COALESCE(SUM(v.total), 0)) " +
            "FROM Venta v " +
            "WHERE v.estado = 'PAGADA' " +
            "AND v.fechaVenta >= :inicio " +
            "AND v.fechaVenta < :fin")
    ResumenReporteDTO obtenerResumenPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Ventas agrupadas por usuario.
    @Query("SELECT new com.pos.ventas.dto.VentaUsuarioDTO(" +
            "u.idUsuario, u.nombre, COUNT(v.idVenta), COALESCE(SUM(v.total), 0)) " +
            "FROM Venta v " +
            "LEFT JOIN Usuario u ON v.idUsuario = u.idUsuario " +
            "WHERE v.estado = 'PAGADA' " +
            "AND v.fechaVenta >= :inicio " +
            "AND v.fechaVenta < :fin " +
            "GROUP BY u.idUsuario, u.nombre " +
            "ORDER BY SUM(v.total) DESC")
    List<VentaUsuarioDTO> obtenerVentasPorUsuario(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Ventas agrupadas por cliente.
    @Query("SELECT new com.pos.ventas.dto.VentaClienteDTO(" +
            "c.idCliente, c.nombre, COUNT(v.idVenta), COALESCE(SUM(v.total), 0)) " +
            "FROM Venta v " +
            "LEFT JOIN Cliente c ON v.idCliente = c.idCliente " +
            "WHERE v.estado = 'PAGADA' " +
            "AND v.idCliente IS NOT NULL " +
            "AND v.fechaVenta >= :inicio " +
            "AND v.fechaVenta < :fin " +
            "GROUP BY c.idCliente, c.nombre " +
            "ORDER BY SUM(v.total) DESC")
    List<VentaClienteDTO> obtenerVentasPorCliente(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Últimas ventas.
    @Query("SELECT new com.pos.ventas.dto.UltimaVentaDTO(" +
            "v.idVenta, v.fechaVenta, c.nombre, u.nombre, v.total, v.estado) " +
            "FROM Venta v " +
            "LEFT JOIN Cliente c ON v.idCliente = c.idCliente " +
            "LEFT JOIN Usuario u ON v.idUsuario = u.idUsuario " +
            "ORDER BY v.fechaVenta DESC")
    List<UltimaVentaDTO> findUltimasVentas(Pageable pageable);
}