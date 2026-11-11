package com.pos.ventas.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pedido_cliente")
public class PedidoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name = "fecha_conversion")
    private LocalDateTime fechaConversion;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DetallePedidoCliente> detalles = new ArrayList<>();

    @PrePersist
    public void antesDeGuardar() {
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }

        if (estado == null) {
            estado = "REGISTRADO";
        }
    }
}