package com.example.prueba.users.repository;

import com.example.prueba.users.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserFileRepository {
    @Value("${users.file.path}")
    private String filePath;

    // mapper para leer el JSON y convertirlo en objetos Java
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Método para Leer todos los usuarios
     *
     * @return una lisya de los usuarios
     * @throws IOException
     */
    public List<User> findAll() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(file, new TypeReference<List<User>>() {
        });
    }

    /**
     * Método para guardar todos los usuarios
     *
     * @param users usuarios a guardar
     * @throws IOException
     */
    private void saveAll(List<User> users) throws IOException {
        File file = new File(filePath);
        objectMapper.writeValue(file, users);
    }

    /**
     * Método para guardar un usuario
     *
     * @param user usuario a guardar
     * @return el usuario guardado
     * @throws IOException
     */
    public User save(User user) throws IOException {
        List<User> users = findAll();
        if (user.getId() == null) {
            user.setId(users.stream().mapToLong(User::getId).max().orElse(0) + 1);
            users.add(user);
        } else {
            users.replaceAll(u -> u.getId().equals(user.getId()) ? user : u);
        }
        saveAll(users);
        return user;
    }

    /**
     * Método para obtener un usuario por su id
     *
     * @param id de usuario a obtener
     * @return usuario encontrado
     * @throws IOException
     */
    public Optional<User> findById(Long id) throws IOException {
        return findAll().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    /**
     * Método para obtener los usuarios que correspondan a un tipo
     *
     * @param tipo de usuarios a obtener
     * @return usuarios que sean de x tipo
     * @throws IOException
     */
    public List<User> findByType(String tipo) throws IOException {
        if (tipo == null || tipo.isBlank()) {
            return new ArrayList<>();
        }

        // Elimina comillas si están presentes
        String tipoLimpio = tipo.replace("\"", "").trim();

        return findAll().stream()
                .filter(user -> user.getTipo() != null
                        && user.getTipo().equalsIgnoreCase(tipoLimpio))
                .collect(Collectors.toList());
    }

    /**
     * Método para eliminar un usuario por su id
     *
     * @param id de usuario a eliminar
     * @throws IOException
     */
    public void deleteById(Long id) throws IOException {
        List<User> users = findAll();
        users.removeIf(u -> u.getId().equals(id));
        saveAll(users);
    }

    /**
     * metodo para buscar un usuario por su username
     *
     * @param username de usuario a buscar
     * @return posible usuario encontrado
     * @throws IOException
     */
    public Optional<User> findByUsername(String username) throws IOException {
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }
}
