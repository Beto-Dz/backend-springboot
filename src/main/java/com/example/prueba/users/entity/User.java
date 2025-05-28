package com.example.prueba.users.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String clave;
    private String tipo;
    private String nombre;
    private String username;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String password;
    private LocalDateTime fechaInicioSesion;
    private LocalDateTime fechaFinSesion;
    private String estatus;

    private String tiempoEnLinea;


}
