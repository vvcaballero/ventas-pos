package com.pos.ventas.controller;


import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.entity.Producto;
import com.pos.ventas.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequestMapping("/api/productos")
    public class ProductoController {

        @Autowired
        private ProductoService productoService;

        @GetMapping("/all")
        public ResponseEntity<ResponseDTO> listar() {
            ResponseDTO response = productoService.listarProductos();
            return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ResponseDTO> buscarPorId(@PathVariable Long id) {
            ResponseDTO response = productoService.buscarPorId(id);
            return ResponseEntity.ok(response);
        }

        @PostMapping
        public ResponseEntity<ResponseDTO> insertar(@RequestBody Producto request) {
            ResponseDTO response = productoService.insertarProducto(request);
            return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ResponseDTO> actualizar(@PathVariable Long id, @RequestBody Producto request) {
            ResponseDTO response = productoService.actualizarProducto(id, request);
            return ResponseEntity.ok(response);
        }

        // Endpoint para el borrado lógico usando PATCH o DELETE
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseDTO> eliminarLogico(@PathVariable Long id) {
            ResponseDTO response = productoService.eliminarLogicoProducto(id);
            return ResponseEntity.ok(response);
        }
}
