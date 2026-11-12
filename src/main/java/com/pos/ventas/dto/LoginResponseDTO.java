package com.pos.ventas.dto;

import lombok.*;

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
}
