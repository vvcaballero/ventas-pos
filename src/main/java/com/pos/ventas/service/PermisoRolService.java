
package com.pos.ventas.service;

import com.pos.ventas.entity.PermisoRol;

import java.util.List;

public interface PermisoRolService {

    List<PermisoRol> listarRoles();

    PermisoRol obtenerPorRol(String rol);

    PermisoRol actualizarPermisos(String rol, PermisoRol permisos);
}
