package com.agrovalle.bdd;

import com.agrovalle.JwtAuthenticationFilter;
import com.agrovalle.UsuarioRepository;
import com.agrovalle.service.JwtService;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * replica la cadena de SecurityConfig, que no
 * se carga con el perfil "test". Así las pruebas de integración ejercitan el
 * JWT real: sin token o con token inválido la API responde 401.
 * Si cambia SecurityConfig, hay que mantener esta clase alineada.
 */

@TestConfiguration
public class BddSecurityConfig {

    @Bean
    public SecurityFilterChain bddSecurityFilterChain(
        HttpSecurity http,
        JwtService jwtService,
        UsuarioRepository usuarioRepository) throws Exception {

        JwtAuthenticationFilter jwtFilter =
            new JwtAuthenticationFilter(jwtService, usuarioRepository);

        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(authorize -> authorize
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login")
                .permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

