
package com.pos.ventas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        // Si no viene token, continúa.
        // Spring Security decidirá si la ruta necesita autenticación.
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            Claims claims = jwtUtil.validarToken(token);

            String username = claims.getSubject();
            String rol = claims.get("rol", String.class);

            List<SimpleGrantedAuthority> authorities =
                    new ArrayList<>();

            // Agregar el rol como autoridad
            if (rol != null && !rol.trim().isEmpty()) {
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + rol)
                );
            }

            // Recuperar permisos del JWT
            List<?> permisos = claims.get("permisos", List.class);

            if (permisos != null) {
                for (Object permiso : permisos) {
                    if (permiso instanceof String) {
                        authorities.add(
                                new SimpleGrantedAuthority(
                                        (String) permiso
                                )
                        );
                    }
                }
            }

            // Crear autenticación de Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            // Guardar autenticación en el contexto
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException e) {

            // Token inválido o vencido.
            // Se limpia el contexto y las rutas protegidas
            // terminarán respondiendo 401.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
