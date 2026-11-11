
package com.pos.ventas.repository;

import com.pos.ventas.entity.PermisoRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermisoRolRepository
        extends JpaRepository<PermisoRol, Integer> {

    Optional<PermisoRol> findByRolIgnoreCase(String rol);
}
