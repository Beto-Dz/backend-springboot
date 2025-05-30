package com.example.prueba.auth;

import com.example.prueba.security.JwtService;
import com.example.prueba.users.entity.User;
import com.example.prueba.users.repository.UserFileRepository;
import com.example.prueba.users.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserFileRepository userRepository;
    private UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserFileRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userService = new UserService(userRepository);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            User user = userRepository.findAll().stream()
                    .filter(u -> u.getUsername().equals(authRequest.getUsername()))
                    .findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // actualizanco datos para cuando inicia sesion
            if (user.getFechaInicioSesion() == null) {
                user.setFechaInicioSesion(LocalDateTime.now());
            }

            user.setEstatus("activo");
            userRepository.save(user);

            String token = jwtService.generateToken(user.getUsername(), user.getTipo(), user.getFechaFinSesion());

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido");
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            User user = userRepository.findAll().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // actualizacndo datos para cuando cierra sesion
            user.setFechaFinSesion(LocalDateTime.now());
            user.setEstatus("inactivo");

            // calcular tiempo en línea
            userService.calculateOnlineTime(user);

            if (user.getFechaInicioSesion() != null) {
                Duration duration = Duration.between(user.getFechaInicioSesion(), LocalDateTime.now());
                long minutes = duration.toMinutes();
                user.setTiempoEnLinea(minutes + " minutos");
            } else {
                user.setTiempoEnLinea("Desconocido");
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Logout exitoso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cerrar sesión"));
        }
    }

}

class AuthRequest {
    @NotBlank(message = "username es obligatorio")
    @Size(min = 4, max = 12, message = "username debe tener entre 4 y 12 caracteres")
    private String username;
    @NotBlank(message = "contrasena es obligatoria")
    @Size(min = 6, message = "contrasena debe tener al menos 6 caracteres")
    private String password;

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}