
package com.pos.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private Long idUsuario;
    private String nombre;
    private String username;
    private String rol;
    private String token;
    private List<String> permisos;
}
