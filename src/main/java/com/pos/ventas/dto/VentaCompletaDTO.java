package com.pos.ventas.dto;

import com.pos.ventas.entity.DetalleVenta;
import com.pos.ventas.entity.Pago;
import com.pos.ventas.entity.Venta;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaCompletaDTO {
    private Venta venta;
    private List<DetalleVenta> detalles;
    private List<Pago> pagos;
    private BigDecimal cambio;
}
