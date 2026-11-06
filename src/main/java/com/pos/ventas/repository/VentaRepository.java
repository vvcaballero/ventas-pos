package com.pos.ventas.repository;

import com.pos.ventas.dto.UltimaVentaDTO;
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

    long countByEstadoAndFechaVentaBetween(String estado, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
           "WHERE v.estado = :estado AND v.fechaVenta >= :inicio AND v.fechaVenta < :fin")
    BigDecimal sumTotalPorRango(@Param("estado") String estado,
                                @Param("inicio") LocalDateTime inicio,
                                @Param("fin") LocalDateTime fin);

    @Query("SELECT new com.pos.ventas.dto.UltimaVentaDTO(v.idVenta, v.fechaVenta, c.nombre, u.nombre, v.total, v.estado) " +
           "FROM Venta v " +
           "LEFT JOIN Cliente c ON v.idCliente = c.idCliente " +
           "LEFT JOIN Usuario u ON v.idUsuario = u.idUsuario " +
           "ORDER BY v.fechaVenta DESC")
    List<UltimaVentaDTO> findUltimasVentas(Pageable pageable);
}
