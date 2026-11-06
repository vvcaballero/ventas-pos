package com.pos.ventas.repository;

import com.pos.ventas.dto.ProductoMasVendidoDTO;
import com.pos.ventas.entity.DetalleVenta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByIdVenta(Long idVenta);

    @Query("SELECT new com.pos.ventas.dto.ProductoMasVendidoDTO(p.idProducto, p.nombre, SUM(dv.cantidad)) " +
           "FROM DetalleVenta dv, Producto p, Venta v " +
           "WHERE dv.idProducto = p.idProducto AND dv.idVenta = v.idVenta AND v.estado = 'PAGADA' " +
           "GROUP BY p.idProducto, p.nombre " +
           "ORDER BY SUM(dv.cantidad) DESC")
    List<ProductoMasVendidoDTO> findMasVendidos(Pageable pageable);
}
