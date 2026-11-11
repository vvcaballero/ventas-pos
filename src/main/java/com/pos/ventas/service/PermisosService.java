
package com.pos.ventas.service;

import java.util.List;

public interface PermisosService {

    // Obtener los permisos habilitados de un rol
    List<String> obtenerPermisos(String rol);

    // Comprobar si un rol tiene un permiso específico
    boolean tienePermiso(String rol, String permiso);
}
