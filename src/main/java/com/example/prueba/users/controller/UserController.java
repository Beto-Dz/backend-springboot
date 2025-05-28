package com.example.prueba.users.controller;

import com.example.prueba.users.entity.User;
import com.example.prueba.users.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
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
    public User createUser(@RequestBody User user) throws IOException {
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) throws IOException {
        user.setId(id);
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws IOException {
        userService.deleteUser(id);
    }
}
