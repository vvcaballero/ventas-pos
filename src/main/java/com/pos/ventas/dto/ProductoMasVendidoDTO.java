package com.pos.ventas.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoMasVendidoDTO {
    private Long idProducto;
    private String nombre;
    private Long unidadesVendidas;
}
