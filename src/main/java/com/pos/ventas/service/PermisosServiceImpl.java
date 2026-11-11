
package com.pos.ventas.service.impl;

import com.pos.ventas.entity.PermisoRol;
import com.pos.ventas.repository.PermisoRolRepository;
import com.pos.ventas.service.PermisosService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PermisosServiceImpl implements PermisosService {

    private final PermisoRolRepository permisoRolRepository;

    public PermisosServiceImpl(
            PermisoRolRepository permisoRolRepository) {
        this.permisoRolRepository = permisoRolRepository;
    }

    @Override
    public List<String> obtenerPermisos(String rol) {

        if (rol == null || rol.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Optional<PermisoRol> resultado =
                permisoRolRepository.findByRolIgnoreCase(rol.trim());

        if (!resultado.isPresent()) {
            return Collections.emptyList();
        }

        PermisoRol p = resultado.get();

        List<String> permisos = new ArrayList<>();

        // DASHBOARD
        agregar(permisos, p.getDashboardVer(), "dashboard:ver");

        // PUNTO DE VENTA
        agregar(permisos, p.getPuntoVentaVer(), "punto-venta:ver");

        // VENTAS
        agregar(permisos, p.getVentasVer(), "ventas:ver");
        agregar(permisos, p.getVentasCrear(), "ventas:crear");
        agregar(permisos, p.getVentasEditar(), "ventas:editar");
        agregar(permisos, p.getVentasEliminar(), "ventas:eliminar");

        // PRODUCTOS
        agregar(permisos, p.getProductosVer(), "productos:ver");
        agregar(permisos, p.getProductosCrear(), "productos:crear");
        agregar(permisos, p.getProductosEditar(), "productos:editar");
        agregar(permisos, p.getProductosEliminar(), "productos:eliminar");

        // INVENTARIO
        agregar(permisos, p.getInventarioVer(), "inventario:ver");
        agregar(permisos, p.getInventarioCrear(), "inventario:crear");
        agregar(permisos, p.getInventarioEditar(), "inventario:editar");

        // CLIENTES
        agregar(permisos, p.getClientesVer(), "clientes:ver");
        agregar(permisos, p.getClientesCrear(), "clientes:crear");
        agregar(permisos, p.getClientesEditar(), "clientes:editar");
        agregar(permisos, p.getClientesEliminar(), "clientes:eliminar");

        // REPORTES
        agregar(permisos, p.getReportesVer(), "reportes:ver");

        // USUARIOS
        agregar(permisos, p.getUsuariosVer(), "usuarios:ver");
        agregar(permisos, p.getUsuariosCrear(), "usuarios:crear");
        agregar(permisos, p.getUsuariosEditar(), "usuarios:editar");
        agregar(permisos, p.getUsuariosEliminar(), "usuarios:eliminar");

        // CONFIGURACIÓN
        agregar(permisos, p.getConfiguracionVer(), "configuracion:ver");
        agregar(permisos, p.getConfiguracionEditar(), "configuracion:editar");

        return permisos;
    }

    @Override
    public boolean tienePermiso(String rol, String permiso) {

        if (permiso == null || permiso.trim().isEmpty()) {
            return false;
        }

        return obtenerPermisos(rol).contains(permiso.trim());
    }

    private void agregar(
            List<String> permisos,
            Boolean habilitado,
            String permiso) {

        if (Boolean.TRUE.equals(habilitado)) {
            permisos.add(permiso);
        }
    }
}
