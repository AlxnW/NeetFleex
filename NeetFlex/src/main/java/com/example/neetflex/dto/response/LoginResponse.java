package com.example.neetflex.dto.response;

import lombok.*;

@Getter @Setter
public class LoginResponse {
    private String token;
    private Long expiresIn;
    private String name;
}