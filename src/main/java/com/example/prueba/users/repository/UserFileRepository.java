package com.example.prueba.users.repository;

import com.example.prueba.users.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Repository
public class UserFileRepository {
    // nombre de archivo de texto
    private static final String FILE_PATH = "users.txt";

    // mapper para leer el JSON y convertirlo en objetos Java
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Método para Leer todos los usuarios
     *
     * @return una lisya de los usuarios
     * @throws IOException
     */
    public List<User> findAll() throws IOException {
        // crea una entrada para leer el archivo de texto
        InputStream inputStream = new ClassPathResource(FILE_PATH).getInputStream();

        // retorna la conversion de la entrada del archivo a una lista de usuarios
        return objectMapper.readValue(inputStream, new TypeReference<List<User>>() {
        });
    }

    /**
     * Método para guardar todos los usuarios
     *
     * @param users usuarios a guardar
     * @throws IOException
     */
    private void saveAll(List<User> users) throws IOException {
        File file = new ClassPathResource(FILE_PATH).getFile();
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
}
