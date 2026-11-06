package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Usuario;
import com.pos.ventas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final List<String> ROLES_VALIDOS = Arrays.asList("ADMIN", "CAJERO");

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ResponseDTO listarUsuarios() {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(usuarioRepository.findAll());
        } catch (Exception e) {
            response.setMensaje("Error al listar usuarios: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO buscarPorId(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isPresent()) {
                response.setMensaje("OK");
                response.setBody(usuario.get());
            } else {
                response.setMensaje("Usuario no encontrado con el ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al buscar el usuario: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO insertarUsuario(Usuario request) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (request == null || request.getUsername() == null || request.getUsername().trim().isEmpty()
                    || request.getPassword() == null || request.getPassword().isEmpty()) {
                response.setMensaje("Username y password son obligatorios");
                response.setBody(null);
                return response;
            }
            if (request.getRol() == null || !ROLES_VALIDOS.contains(request.getRol().toUpperCase())) {
                response.setMensaje("Rol inválido. Valores permitidos: " + ROLES_VALIDOS);
                response.setBody(null);
                return response;
            }
            if (usuarioRepository.existsByUsername(request.getUsername())) {
                response.setMensaje("Ya existe un usuario con el username: " + request.getUsername());
                response.setBody(null);
                return response;
            }
            request.setRol(request.getRol().toUpperCase());
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            if (request.getActivo() == null) {
                request.setActivo(true);
            }
            response.setMensaje("OK");
            response.setBody(usuarioRepository.save(request));
        } catch (Exception e) {
            response.setMensaje("Error al insertar usuario: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO actualizarUsuario(Long id, Usuario request) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Usuario> existente = usuarioRepository.findById(id);
            if (existente.isPresent()) {
                Usuario usu = existente.get();
                if (request.getRol() != null) {
                    if (!ROLES_VALIDOS.contains(request.getRol().toUpperCase())) {
                        response.setMensaje("Rol inválido. Valores permitidos: " + ROLES_VALIDOS);
                        response.setBody(null);
                        return response;
                    }
                    usu.setRol(request.getRol().toUpperCase());
                }
                usu.setNombre(request.getNombre());
                // El username solo cambia si es distinto y no está en uso
                if (request.getUsername() != null && !request.getUsername().equals(usu.getUsername())) {
                    if (usuarioRepository.existsByUsername(request.getUsername())) {
                        response.setMensaje("Ya existe un usuario con el username: " + request.getUsername());
                        response.setBody(null);
                        return response;
                    }
                    usu.setUsername(request.getUsername());
                }
                // Solo se cambia la contraseña si viene una nueva
                if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                    usu.setPassword(passwordEncoder.encode(request.getPassword()));
                }
                if (request.getActivo() != null) {
                    usu.setActivo(request.getActivo());
                }
                response.setMensaje("OK");
                response.setBody(usuarioRepository.save(usu));
            } else {
                response.setMensaje("Usuario no encontrado para actualizar con ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al actualizar usuario: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO eliminarLogicoUsuario(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Usuario> existente = usuarioRepository.findById(id);
            if (existente.isPresent()) {
                Usuario usu = existente.get();
                usu.setActivo(false);
                response.setMensaje("OK");
                response.setBody(usuarioRepository.save(usu));
            } else {
                response.setMensaje("Usuario no encontrado con ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al realizar el borrado lógico: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }
}
