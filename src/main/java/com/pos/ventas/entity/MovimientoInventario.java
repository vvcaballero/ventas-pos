package com.pos.ventas.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;
    private Long idProducto;
    private Long idUsuario;
    private String tipo;        // ENTRADA | SALIDA | AJUSTE
    private Integer cantidad;
    private String motivo;
    private LocalDateTime fecha;
}