package com.pos.ventas.repository;

import com.pos.ventas.entity.MovimientoInventario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByIdProductoOrderByFechaDesc(Long idProducto);

    @Query("SELECT m FROM MovimientoInventario m ORDER BY m.fecha DESC")
    List<MovimientoInventario> findRecientes(Pageable pageable);
}