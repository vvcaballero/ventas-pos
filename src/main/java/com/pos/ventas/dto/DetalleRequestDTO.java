package com.pos.ventas.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalleRequestDTO {
    private Long idProducto;
    private Integer cantidad;
}
