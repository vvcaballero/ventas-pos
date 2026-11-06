package com.pos.ventas.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagoRequestDTO {
    private String metodoPago;   // EFECTIVO | TARJETA | TRANSFERENCIA
    private BigDecimal monto;    // lo que entrega el cliente
}