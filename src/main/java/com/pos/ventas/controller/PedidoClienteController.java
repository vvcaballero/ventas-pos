package com.pos.ventas.controller;

import com.pos.ventas.dto.EstadoPedidoRequestDTO;
import com.pos.ventas.dto.PedidoClienteRequestDTO;
import com.pos.ventas.dto.RegistrarProductoPedidoDTO;
import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.service.PedidoClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos-clientes")
@CrossOrigin(origins = "*")
public class PedidoClienteController {

    @Autowired
    private PedidoClienteService pedidoService;

    // Listar todos los pedidos, incluidos los convertidos y cancelados.
    @GetMapping("/all")
    public ResponseDTO listarTodos() {
        return pedidoService.listarTodos();
    }

    // Listar únicamente pedidos pendientes.
    @GetMapping("/pendientes")
    public ResponseDTO listarPendientes() {
        return pedidoService.listarPendientes();
    }

    // Consultar un pedido con sus detalles.
    @GetMapping("/{id}")
    public ResponseDTO buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    // Registrar un nuevo pedido.
    @PostMapping
    public ResponseDTO registrarPedido(
            @RequestBody PedidoClienteRequestDTO request
    ) {
        return pedidoService.registrarPedido(request);
    }

    // Cambiar el estado de un pedido.
    @PutMapping("/{id}/estado")
    public ResponseDTO actualizarEstado(
            @PathVariable Long id,
            @RequestBody EstadoPedidoRequestDTO request
    ) {
        return pedidoService.actualizarEstado(id, request);
    }

    // Convertir un detalle recibido en producto del catálogo.
    @PostMapping("/{idPedido}/detalles/{idDetalle}/convertir")
    public ResponseDTO convertirProducto(
            @PathVariable Long idPedido,
            @PathVariable Long idDetalle,
            @RequestBody RegistrarProductoPedidoDTO request
    ) {
        return pedidoService.convertirProducto(
                idPedido,
                idDetalle,
                request
        );
    }
}