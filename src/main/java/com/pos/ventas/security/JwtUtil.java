package com.pos.ventas.security;

import com.pos.ventas.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Debe tener al menos 32 caracteres (HS256)
    @Value("${jwt.secret}")
    private String secret;

    // Por defecto 8 horas
    @Value("${jwt.expiration-ms:28800000}")
    private long expirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(Usuario usuario) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .claim("idUsuario", usuario.getIdUsuario())
                .claim("rol", usuario.getRol())
                .setIssuedAt(new Date(ahora))
                .setExpiration(new Date(ahora + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Lanza JwtException si el token es inválido o expiró. */
    public Claims validarToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
