package com.pos.ventas.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequestDTO {
    private Long idProducto;
    private String tipoMovimiento;  // ENTRADA | SALIDA | AJUSTE
    private Integer cantidad;       // en AJUSTE puede ser negativa (diferencia)
    private String motivo;
    private Long idUsuario;
}
