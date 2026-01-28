package com.example.neetflex.service.impl;

import com.example.neetflex.model.user.User;
import com.example.neetflex.repositories.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserExtractServiceImpl {

    private JwtServiceImpl jwtService;
    private UserRepository userRepository;

    public User getUser(String jwtToken) {
        String username = jwtService.extractUsername(jwtToken);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid JWT"));
    }
}