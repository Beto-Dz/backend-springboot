package com.example.prueba.users.entity;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;

    @NotBlank(message = "clave es obligatoria")
    @Size(min = 6, max = 10, message = "la clave debe tener entre 6 y 10 caracteres")
    private String clave;

    @NotBlank(message = "tipo de usuario es obligatorio")
    @Pattern(regexp = "^(admin|user)$", message = "el tipo de usuario debe ser admin o user")
    private String tipo;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 30, message = "El nombre no puede exceder los 30 caracteres")
    private String nombre;

    @NotBlank(message = "username es obligatorio")
    @Size(min = 4, max = 12, message = "username debe tener entre 4 y 12 caracteres")
    private String username;

    @NotBlank(message = "apellido paterno es obligatorio")
    @Size(max = 30, message = "pellido paterno no puede exceder 30 caracteres")
    private String apellidoPaterno;

    @NotBlank(message = "apellido materno es obligatorio")
    @Size(max = 30, message = "El apellido materno no puede exceder 30 caracteres")
    private String apellidoMaterno;


    @NotBlank(message = "contrasena es obligatoria")
    @Size(min = 6, message = "contrasena debe tener al menos 6 caracteres")
    private String password;

    @PastOrPresent(message = "La fecha de inicio de sesión debe ser en el pasado o presente")
    private LocalDateTime fechaInicioSesion;

    @FutureOrPresent(message = "La fecha de fin de sesión debe ser en el futuro o presente")
    private LocalDateTime fechaFinSesion;

    @NotBlank(message = "estatus es obligatorio")
    @Pattern(regexp = "^(activo|inactivo)$", message = "El estatus debe ser 'activo' o 'inactivo'")
    private String estatus;

    private String tiempoEnLinea;


    public void setTipo(String tipo) {
        this.tipo = (tipo != null) ? tipo.toLowerCase() : null;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null) ? nombre.toLowerCase() : null;
    }

    public void setUsername(String username) {
        this.username = (username != null) ? username.toLowerCase() : null;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = (apellidoPaterno != null) ? apellidoPaterno.toLowerCase() : null;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = (apellidoMaterno != null) ? apellidoMaterno.toLowerCase() : null;
    }

    public void setEstatus(String estatus) {
        this.estatus = (estatus != null) ? estatus.toLowerCase() : null;
    }
}
