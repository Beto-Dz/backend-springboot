package com.example.prueba.users.controller;

import com.example.prueba.users.entity.User;
import com.example.prueba.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers(@RequestParam(required = false) String type) throws IOException {
        if (type == null || type.isBlank()) {
            return userService.getAllUsers();
        }
        return userService.getByType(type);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) throws IOException {
        return userService.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) throws IOException {
        // Validación de username único
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El username ya está en uso"));
        }

        // prevenir que el usuario envie estos datos
        user.setFechaInicioSesion(null);
        user.setFechaFinSesion(null);
        user.setTiempoEnLinea(null);
        user.setEstatus("inactivo");

        User createdUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) throws IOException {
        user.setId(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "No tienes permiso para actualizar usuarios"));
        }

        // validación de username único (excluyendo al usuario actual)
        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El username ya está en uso por otro usuario"));
        }

        // prevenir que el usuario envie estos datos
        user.setFechaInicioSesion(null);
        user.setFechaFinSesion(null);
        user.setTiempoEnLinea(null);
        user.setEstatus("inactivo");

        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws IOException {
        userService.deleteUser(id);
    }
}
