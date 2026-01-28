package com.example.neetflex.controller;

import com.example.neetflex.dto.UserDTO;
import com.example.neetflex.dto.response.LoginResponse;
import com.example.neetflex.model.user.User;
import com.example.neetflex.service.AuthenticationService;
import com.example.neetflex.service.impl.JwtServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final JwtServiceImpl jwtService;

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            authenticationService.signup(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Registration failed"));
        }


        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserDTO input) {
        User authenticatedUser = authenticationService.authenticate(input);
        String token = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setName(authenticatedUser.getUsername());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/validate")
    public Boolean validateToken(@RequestHeader("Authorization") String token) {
        return jwtService.isTokenExpired(token);
    }



}