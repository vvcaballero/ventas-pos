package com.pos.ventas.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;
    private Long idVenta;
    private String metodoPago;   // EFECTIVO | TARJETA | TRANSFERENCIA
    private BigDecimal monto;
    private String referencia;
    private LocalDateTime fechaPago;
}
