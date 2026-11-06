package com.pos.ventas.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UltimaVentaDTO {
    private Long idVenta;
    private LocalDateTime fechaVenta;
    private String cliente;
    private String usuario;
    private BigDecimal total;
    private String estado;
}
