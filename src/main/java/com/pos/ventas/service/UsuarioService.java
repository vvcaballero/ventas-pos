package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Usuario;

public interface UsuarioService {
    ResponseDTO listarUsuarios();
    ResponseDTO buscarPorId(Long id);
    ResponseDTO insertarUsuario(Usuario request);
    ResponseDTO actualizarUsuario(Long id, Usuario request);
    ResponseDTO eliminarLogicoUsuario(Long id);
}
