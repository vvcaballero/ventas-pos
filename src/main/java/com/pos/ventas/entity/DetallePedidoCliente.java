package com.pos.ventas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "detalle_pedido_cliente")
public class DetallePedidoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pedido")
    private Long idDetallePedido;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private PedidoCliente pedido;

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "descripcion_producto", nullable = false, length = 200)
    private String descripcionProducto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_estimado", nullable = false)
    private BigDecimal precioEstimado;

    @Column(name = "observaciones", length = 300)
    private String observaciones;
}