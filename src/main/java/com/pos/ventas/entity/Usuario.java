package com.pos.ventas.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

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

    // En la BD la columna se llama "usuario"; en el JSON seguimos usando "username"
    @Column(name = "usuario")
    private String username;

    // WRITE_ONLY: se acepta al recibir el JSON pero nunca se devuelve al frontend
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String rol;      // ADMIN | CAJERO
    private Boolean activo;

    // La llena MySQL (DEFAULT CURRENT_TIMESTAMP); Hibernate solo la lee
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}