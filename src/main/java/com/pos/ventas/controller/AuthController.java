package com.pos.ventas.controller;

import com.pos.ventas.dto.LoginRequestDTO;
import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO request) {
        ResponseDTO response = authService.login(request);
        if (response.getBody() == null) {
            // Credenciales inválidas, usuario inactivo o datos faltantes
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }
}