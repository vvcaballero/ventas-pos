
package com.pos.ventas.service;

import com.pos.ventas.dto.LoginRequestDTO;
import com.pos.ventas.dto.LoginResponseDTO;
import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Usuario;
import com.pos.ventas.repository.UsuarioRepository;
import com.pos.ventas.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PermisosService permisosService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Override
    public ResponseDTO login(LoginRequestDTO request) {

        ResponseDTO response = new ResponseDTO();

        // 1. Validar datos
        if (request == null
                || request.getUsername() == null
                || request.getUsername().trim().isEmpty()
                || request.getPassword() == null
                || request.getPassword().isEmpty()) {

            response.setMensaje("Usuario y contraseña son obligatorios");
            response.setBody(null);
            return response;
        }

        // 2. Buscar usuario
        Optional<Usuario> optUsuario =
                usuarioRepository.findByUsername(
                        request.getUsername().trim()
                );

        // 3. Validar credenciales
        if (!optUsuario.isPresent()
                || !passwordEncoder.matches(
                request.getPassword(),
                optUsuario.get().getPassword()
        )) {

            response.setMensaje("Usuario o contraseña incorrectos");
            response.setBody(null);
            return response;
        }

        Usuario usuario = optUsuario.get();

        // 4. Validar estado
        if (!Boolean.TRUE.equals(usuario.getActivo())) {

            response.setMensaje("El usuario está inactivo");
            response.setBody(null);
            return response;
        }

        // 5. Consultar permisos del rol
        List<String> permisos =
                permisosService.obtenerPermisos(usuario.getRol());

        // 6. Validar que tenga permisos configurados
        if (permisos == null || permisos.isEmpty()) {

            response.setMensaje(
                    "El rol del usuario no tiene permisos configurados"
            );
            response.setBody(null);
            return response;
        }

        // 7. Generar JWT con permisos
        String token = jwtUtil.generarToken(usuario, permisos);

        // 8. Construir respuesta
        LoginResponseDTO body = new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getUsername(),
                usuario.getRol(),
                token,
                permisos
        );

        response.setMensaje("Inicio de sesión exitoso");
        response.setBody(body);

        return response;
    }
}
