package com.pos.ventas.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDTO {
    private Long idCliente;     // opcional (venta a público general)
    private Long idUsuario;
    private List<DetalleRequestDTO> detalles;
}
