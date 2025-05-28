package com.example.prueba.security;

import com.example.prueba.users.repository.UserFileRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserFileRepository userRepository;

    public UserDetailsServiceImpl(UserFileRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userRepository.findAll().stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst()
                    .map(user -> User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .authorities(user.getTipo())
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } catch (IOException e) {
            throw new RuntimeException("Error reading users", e);
        }
    }
}