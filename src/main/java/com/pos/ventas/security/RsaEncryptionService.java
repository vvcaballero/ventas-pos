
package com.pos.ventas.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class RsaEncryptionService {

    @Value("${crypto.private-key}")
    private String privateKeyBase64;

    @Value("${crypto.public-key}")
    private String publicKeyBase64;

    public String decryptPassword(String encryptedPassword)
            throws Exception {

        if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cifrado obligatorio");
        }

        // Decodificar clave privada
        byte[] privateKeyBytes = Base64.getDecoder()
                .decode(privateKeyBase64);

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(privateKeyBytes);

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(keySpec);

        // Configurar RSA-OAEP SHA-256 y MGF1 SHA-256
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");

        cipher.init(
                Cipher.DECRYPT_MODE,
                privateKey,
                oaepParams
        );

        // Descifrar contraseña
        byte[] decryptedBytes = cipher.doFinal(
                Base64.getDecoder().decode(encryptedPassword)
        );

        return new String(
                decryptedBytes,
                java.nio.charset.StandardCharsets.UTF_8
        );
    }

    public String getPublicKey() {
        return publicKeyBase64;
    }
}
