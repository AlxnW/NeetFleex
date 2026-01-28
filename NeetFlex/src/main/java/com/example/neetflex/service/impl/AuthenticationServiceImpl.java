package com.example.neetflex.service.impl;

import com.example.neetflex.dto.UserDTO;
import com.example.neetflex.model.user.User;
import com.example.neetflex.repositories.UserRepository;
import com.example.neetflex.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean signup(UserDTO input) {
        var user = userRepository.findByUsername(input.getUsername());
        if (user.isPresent()) {
            throw new IllegalArgumentException("Username is already in use");

        }
        User newUser = new User();

        newUser.setUsername(input.getUsername());
        newUser.setPassword(passwordEncoder.encode(input.getPassword()));
        newUser.setEmail(input.getEmail());


        userRepository.save(newUser);

        return true;

    }

    @Override
    public User authenticate(UserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }
}