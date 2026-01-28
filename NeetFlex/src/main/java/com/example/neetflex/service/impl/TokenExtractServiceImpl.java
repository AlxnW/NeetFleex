package com.example.neetflex.service.impl;

import org.springframework.stereotype.Service;

@Service
public class TokenExtractServiceImpl {

    public String getToken(String token){
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }
}