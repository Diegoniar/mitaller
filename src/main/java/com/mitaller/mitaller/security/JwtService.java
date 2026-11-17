package com.mitaller.mitaller.security;

import com.mitaller.mitaller.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Clave secreta de 256 bits (en producción se lee desde variable de entorno)
    private static final String SECRET = System.getenv().getOrDefault(
            "JWT_SECRET",
            "MiTallerSaaSClaveSecretaSuperSeguraParaJWT2026Confeccion!"
    );
    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24; // 24 horas

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("empresaId", usuario.getEmpresa().getId())
                .claim("razonSocial", usuario.getEmpresa().getRazonSocial())
                .claim("nombreUsuario", usuario.getNombreCompleto())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    public boolean esTokenValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}