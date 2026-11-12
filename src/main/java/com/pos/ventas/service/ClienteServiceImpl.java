package com.pos.ventas.service;

import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Cliente;
import com.pos.ventas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public ResponseDTO listarClientes() {
        ResponseDTO response = new ResponseDTO();
        try {
            response.setMensaje("OK");
            response.setBody(clienteRepository.findAll());
        } catch (Exception e) {
            response.setMensaje("Error al listar clientes: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO buscarPorId(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Cliente> cliente = clienteRepository.findById(id);
            if (cliente.isPresent()) {
                response.setMensaje("OK");
                response.setBody(cliente.get());
            } else {
                response.setMensaje("Cliente no encontrado con el ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al buscar el cliente: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO insertarCliente(Cliente request) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (request == null || request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                response.setMensaje("El nombre del cliente es obligatorio");
                response.setBody(null);
                return response;
            }
            if (request.getActivo() == null) {
                request.setActivo(true);
            }
            response.setMensaje("OK");
            response.setBody(clienteRepository.save(request));
        } catch (Exception e) {
            response.setMensaje("Error al insertar cliente: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO actualizarCliente(Long id, Cliente request) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Cliente> existente = clienteRepository.findById(id);
            if (existente.isPresent()) {
                Cliente cli = existente.get();
                cli.setNombre(request.getNombre());
                cli.setTelefono(request.getTelefono());
                cli.setEmail(request.getEmail());
                cli.setDireccion(request.getDireccion());
                if (request.getActivo() != null) {
                    cli.setActivo(request.getActivo());
                }
                response.setMensaje("OK");
                response.setBody(clienteRepository.save(cli));
            } else {
                response.setMensaje("Cliente no encontrado para actualizar con ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al actualizar cliente: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }

    @Override
    public ResponseDTO eliminarLogicoCliente(Long id) {
        ResponseDTO response = new ResponseDTO();
        try {
            Optional<Cliente> existente = clienteRepository.findById(id);
            if (existente.isPresent()) {
                Cliente cli = existente.get();
                cli.setActivo(false);
                response.setMensaje("OK");
                response.setBody(clienteRepository.save(cli));
            } else {
                response.setMensaje("Cliente no encontrado con ID: " + id);
                response.setBody(null);
            }
        } catch (Exception e) {
            response.setMensaje("Error al realizar el borrado lógico: " + e.getMessage());
            response.setBody(null);
        }
        return response;
    }
}
