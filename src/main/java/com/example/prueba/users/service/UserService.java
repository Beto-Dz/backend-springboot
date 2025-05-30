package com.example.prueba.users.service;

import com.example.prueba.users.entity.User;
import com.example.prueba.users.repository.UserFileRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserFileRepository repository;

    /**
     * Constructor parametrizado
     *
     * @param repository para instancear el atributo
     */
    public UserService(UserFileRepository repository) {
        this.repository = repository;
    }

    public boolean existsByUsername(String username) throws IOException {
        return repository.findAll().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    public Optional<User> findByUsername(String username) throws IOException {
        return repository.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }


    /**
     * Método para obtener los usuarios, a través del repository
     *
     * @return lista de usuarios
     * @throws IOException
     */
    public List<User> getAllUsers() throws IOException {
        List<User> users = repository.findAll();
        users.forEach(this::calculateOnlineTime);
        return users;
    }

    /**
     * Método para guardar un usuario, a través del repository
     *
     * @param user usuario a guardar
     * @return usuario guardado
     * @throws IOException
     */
    public User saveUser(User user) throws IOException {
        return repository.save(user);
    }

    /**
     * método para eliminar un usuario por su id , a través del repository
     *
     * @param id de usuario a eliminar
     * @throws IOException
     */
    public void deleteUser(Long id) throws IOException {
        repository.deleteById(id);
    }

    /**
     * método para buscar un usuario por su id, a través del repository
     *
     * @param id de usuario a buscar
     * @return posible usuario encontrado
     * @throws IOException
     */
    public Optional<User> getById(Long id) throws IOException {
        return repository.findById(id);
    }

    /**
     * Método para filtrar por el tipo
     *
     * @param tipo a filtrar
     * @return lista de los usuarios que tengan ese tipo
     * @throws IOException
     */
    public List<User> getByType(String tipo) throws IOException {
        return repository.findByType(tipo);
    }

    /**
     * Método para obtener el tiempo que ha estado activo un usuario
     *
     * @param user usuario a calvular su tiempo activo
     */
    public void calculateOnlineTime(User user) {
        if (user.getFechaInicioSesion() != null) {
            LocalDateTime endTime = user.getFechaFinSesion() != null
                    ? user.getFechaFinSesion()
                    : LocalDateTime.now();

            Duration duration = Duration.between(user.getFechaInicioSesion(), endTime);
            user.setTiempoEnLinea(String.format("%dh %dm", duration.toHours(), duration.toMinutesPart()));
        } else {
            user.setTiempoEnLinea("0h 0m");
        }
    }

}
