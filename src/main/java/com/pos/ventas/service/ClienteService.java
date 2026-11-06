package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Cliente;

public interface ClienteService {
    ResponseDTO listarClientes();
    ResponseDTO buscarPorId(Long id);
    ResponseDTO insertarCliente(Cliente request);
    ResponseDTO actualizarCliente(Long id, Cliente request);
    ResponseDTO eliminarLogicoCliente(Long id);
}
