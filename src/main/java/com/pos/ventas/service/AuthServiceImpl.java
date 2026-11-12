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

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ResponseDTO login(LoginRequestDTO request) {
        ResponseDTO response = new ResponseDTO();

        if (request == null
                || request.getUsername() == null || request.getUsername().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()) {
            response.setMensaje("Usuario y contraseña son obligatorios");
            response.setBody(null);
            return response;
        }

        // 1. Buscar al usuario por username
        Optional<Usuario> optUsuario = usuarioRepository.findByUsername(request.getUsername().trim());

        // 2. Validar contraseña (mismo mensaje si no existe o si la contraseña falla,
        //    para no revelar qué usuarios existen)
        if (!optUsuario.isPresent()
                || !passwordEncoder.matches(request.getPassword(), optUsuario.get().getPassword())) {
            response.setMensaje("Usuario o contraseña incorrectos");
            response.setBody(null);
            return response;
        }

        Usuario usuario = optUsuario.get();

        // 3. Verificar que esté activo
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            response.setMensaje("El usuario está inactivo");
            response.setBody(null);
            return response;
        }

        // 4. Devolver datos + token
        LoginResponseDTO body = new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getUsername(),
                usuario.getRol(),
                jwtUtil.generarToken(usuario));

        response.setMensaje("Inicio de sesión exitoso");
        response.setBody(body);
        return response;
    }
}
