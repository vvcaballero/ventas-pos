package com.pos.ventas.repository;

import com.pos.ventas.dto.ProductoMasVendidoDTO;
import com.pos.ventas.entity.DetalleVenta;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByIdVenta(Long idVenta);

    // Productos más vendidos de todo el historial.
    @Query("SELECT new com.pos.ventas.dto.ProductoMasVendidoDTO(" +
            "p.idProducto, p.nombre, SUM(dv.cantidad)) " +
            "FROM DetalleVenta dv, Producto p, Venta v " +
            "WHERE dv.idProducto = p.idProducto " +
            "AND dv.idVenta = v.idVenta " +
            "AND v.estado = 'PAGADA' " +
            "GROUP BY p.idProducto, p.nombre " +
            "ORDER BY SUM(dv.cantidad) DESC")
    List<ProductoMasVendidoDTO> findMasVendidos(Pageable pageable);

    // Productos más vendidos dentro de un período.
    @Query("SELECT new com.pos.ventas.dto.ProductoMasVendidoDTO(" +
            "p.idProducto, p.nombre, SUM(dv.cantidad)) " +
            "FROM DetalleVenta dv, Producto p, Venta v " +
            "WHERE dv.idProducto = p.idProducto " +
            "AND dv.idVenta = v.idVenta " +
            "AND v.estado = 'PAGADA' " +
            "AND v.fechaVenta >= :inicio " +
            "AND v.fechaVenta < :fin " +
            "GROUP BY p.idProducto, p.nombre " +
            "ORDER BY SUM(dv.cantidad) DESC")
    List<ProductoMasVendidoDTO> findMasVendidosPorPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            Pageable pageable
    );
}