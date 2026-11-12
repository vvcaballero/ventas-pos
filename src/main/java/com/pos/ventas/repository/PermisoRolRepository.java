
package com.pos.ventas.repository;

import com.pos.ventas.entity.PermisoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermisoRolRepository
        extends JpaRepository<PermisoRol, Integer> {

    Optional<PermisoRol> findByRolIgnoreCase(String rol);
}
