package com.pos.ventas.repository;

import com.pos.ventas.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByIdVenta(Long idVenta);
}
