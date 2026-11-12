
package com.pos.ventas.controller;

import com.pos.ventas.entity.PermisoRol;
import com.pos.ventas.service.PermisoRolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permisos-rol")
@CrossOrigin
public class PermisoRolController {

    @Autowired
    private PermisoRolService permisoRolService;

    // Consultar todos los roles y sus permisos
    @GetMapping
    public ResponseEntity<List<PermisoRol>> listarRoles() {
        return ResponseEntity.ok(permisoRolService.listarRoles());
    }

    // Consultar permisos de un rol específico
    @GetMapping("/{rol}")
    public ResponseEntity<PermisoRol> obtenerPorRol(
            @PathVariable String rol) {

        return ResponseEntity.ok(
                permisoRolService.obtenerPorRol(rol));
    }

    // Actualizar permisos de un rol
    @PutMapping("/{rol}")
    public ResponseEntity<PermisoRol> actualizarPermisos(
            @PathVariable String rol,
            @RequestBody PermisoRol permisos) {

        return ResponseEntity.ok(
                permisoRolService.actualizarPermisos(rol, permisos));
    }
}
