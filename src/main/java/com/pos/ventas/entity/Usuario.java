package com.pos.ventas.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    private String nombre;
    private String username;

    // WRITE_ONLY: se acepta al recibir JSON pero nunca se devuelve al frontend
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String rol;      // ADMIN | CAJERO
    private Boolean activo;
}
