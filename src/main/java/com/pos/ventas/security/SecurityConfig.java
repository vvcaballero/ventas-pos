
package com.pos.ventas.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtUtil jwtUtil;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(
            JwtUtil jwtUtil,
            CustomAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtUtil = jwtUtil;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // Habilitar CORS
                .cors()
                .and()

                // Deshabilitar CSRF: utilizamos JWT sin sesiones
                .csrf()
                .disable()

                // Manejo de errores de autenticación
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()

                // Aplicación sin sesiones
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Configuración de rutas
                .authorizeRequests()

                // Permitir solicitudes OPTIONS de CORS
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Login público
                .antMatchers("/api/auth/login").permitAll()

                // Las demás rutas requieren autenticación
                .anyRequest().authenticated();

        // Agregar filtro JWT
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
