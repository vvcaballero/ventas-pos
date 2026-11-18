
package com.pos.ventas.controller;

import com.pos.ventas.dto.LoginRequestDTO;
import com.pos.ventas.dto.ResponseDTO;
import com.pos.ventas.security.RsaEncryptionService;
import com.pos.ventas.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RsaEncryptionService rsaEncryptionService;

    // Angular consulta la clave pública
    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {

        return ResponseEntity.ok(
                Collections.singletonMap(
                        "publicKey",
                        rsaEncryptionService.getPublicKey()
                )
        );
    }

    // Login con password cifrado
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(
            @RequestBody LoginRequestDTO request) {

        if (request == null
                || request.getUsername() == null
                || request.getPassword() == null) {

            ResponseDTO response = new ResponseDTO();
            response.setMensaje("Usuario y contraseña son obligatorios");
            response.setBody(null);

            return ResponseEntity.badRequest().body(response);
        }

        try {
            String decryptedPassword =
                    rsaEncryptionService.decryptPassword(
                            request.getPassword()
                    );

            request.setPassword(decryptedPassword);

        } catch (Exception e) {

            ResponseDTO response = new ResponseDTO();
            response.setMensaje("No fue posible procesar las credenciales");
            response.setBody(null);

            return ResponseEntity.badRequest().body(response);
        }

        // La autenticación original permanece intacta
        ResponseDTO response = authService.login(request);

        if (response.getBody() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/version")
    public Map<String, String> healthCheck() {

        Map<String, String> response = new LinkedHashMap<>();

        response.put("version", "1.0.0");
        response.put("mensaje", "Servicio Ventas-pos funcionando correctamente");

        return response;
    }
}
