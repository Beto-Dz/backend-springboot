package com.example.prueba.security;

import com.example.prueba.users.entity.User;
import com.example.prueba.users.repository.UserFileRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserFileRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, UserFileRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("Interceptando: " + path);

        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtService.extractUsername(jwt);
            System.out.println("Token extraído: " + jwt);
            System.out.println("Username extraído del token: " + username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("UserDetails cargado: " + userDetails.getUsername());
                System.out.println("Authorities: " + userDetails.getAuthorities());

                boolean tokenValido = jwtService.validateToken(jwt, userDetails);
                System.out.println("¿Token válido?: " + tokenValido);

                if (tokenValido) {
                    //buscando el usuario
                    User user = userRepository.findByUsername(username).orElse(null);

                    if (user != null) {
                        // extrayendo fechaFinSesion
                        String tokenFechaFinSesion = jwtService.extractClaim(jwt, claims -> claims.get("fechaFinSesion", String.class));

                        if (tokenFechaFinSesion != null && user.getFechaFinSesion() != null) {
                            LocalDateTime tokenLogout = LocalDateTime.parse(tokenFechaFinSesion);
                            // verificando las fechas de fin sesion para invalidar el token
                            if (user.getFechaFinSesion().isAfter(tokenLogout)) {
                                System.out.println("Token inválido por fechaFinSesion");
                                filterChain.doFilter(request, response);
                                return;
                            }
                        }
                    }
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("Autenticación establecida en SecurityContext");
                } else {
                    System.out.println("Token inválido");
                }
            } catch (Exception e) {
                System.out.println("Error al autenticar usuario: " + e.getMessage());
            }
        } else if (username == null) {
            System.out.println("No se extrajo username del token");
        } else {
            System.out.println("Ya hay autenticación en el SecurityContext");
        }

        filterChain.doFilter(request, response);
    }

}