package com.agrovalle.service;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.agrovalle.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long expirationMs;

  public JwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-ms}") long expirationMs) {

    this.signingKey = Keys.hmacShaKeyFor(
        Decoders.BASE64.decode(secret));

    this.expirationMs = expirationMs;
  }

  // Generar token con ID, correo y rol del usuario
  public String generateToken(Usuario usuario) {

    Instant now = Instant.now();

    return Jwts.builder()
        .subject(usuario.getCorreo())
        .claim("id", usuario.getId())
        .claim("role", usuario.getRol().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusMillis(expirationMs)))
        .signWith(signingKey)
        .compact();
  }

  // Extraer correo del token
  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  // Extraer ID del usuario
  public Integer extractUserId(String token) {

    Object id = extractAllClaims(token).get("id");

    if (!(id instanceof Number)) {
      throw new IllegalArgumentException(
          "El token no contiene un ID de usuario válido");
    }

    return ((Number) id).intValue();
  }

  // Extraer rol del usuario
  public String extractRole(String token) {
    return extractAllClaims(token).get("role", String.class);
  }

  // Validar firma y obtener los claims
  private Claims extractAllClaims(String token) {

    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  // Comprobar vigencia e identidad del usuario
  public boolean isTokenValid(String token, Usuario usuario) {

    try {
      Claims claims = extractAllClaims(token);

      Date expiration = claims.getExpiration();

      return java.util.Objects.equals(
          claims.getSubject(), usuario.getCorreo())
          && extractUserId(token).equals(usuario.getId())
          && java.util.Objects.equals(
              claims.get("role", String.class),
              usuario.getRol().name())
          && expiration != null
          && expiration.after(new Date());

    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public long getExpirationMs() {
    return expirationMs;
  }
}
