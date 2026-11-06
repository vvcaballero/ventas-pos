package com.pos.ventas.repository;

import com.pos.ventas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByUsername(String username);
//    autenticacion
    Optional<Usuario> findByUsername(String username);
}
