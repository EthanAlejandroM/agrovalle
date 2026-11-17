package com.agrovalle;

import com.agrovalle.service.JwtService;
//import com.agrovalle.UsuarioRepository;
//import com.agrovalle.Usuario;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtService jwtService;
  private final UsuarioRepository usuarioRepository;

  public JwtAuthenticationFilter(
      JwtService jwtService,
      UsuarioRepository usuarioRepository) {
    this.jwtService = jwtService;
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String authorization = request.getHeader(AUTHORIZATION_HEADER);

    if (authorization != null
        && authorization.startsWith(BEARER_PREFIX)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      authenticate(authorization.substring(BEARER_PREFIX.length()), request);
    }

    filterChain.doFilter(request, response);
  }

  private void authenticate(String token, HttpServletRequest request) {
    try {
      String username = jwtService.extractUsername(token);
      Usuario usuario = usuarioRepository.findByCorreo(username).orElse(null);

      if (usuario == null || !jwtService.isTokenValid(token, usuario)) {
        return;
      }

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          username,
          null,
          List.of(new SimpleGrantedAuthority(
              "ROLE_" + usuario.getRol().name())));
      authentication.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (JwtException | IllegalArgumentException exception) {
      SecurityContextHolder.clearContext();
    }
  }
}