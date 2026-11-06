package com.pos.ventas.repository;

import com.pos.ventas.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    long countByActivoTrue();

    // Equivale a: WHERE activo = TRUE AND stock <= stock_minimo
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo")
    List<Producto> findStockBajo();
}
