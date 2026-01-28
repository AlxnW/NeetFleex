package com.example.neetflex.service;

import com.example.neetflex.dto.UserDTO;
import com.example.neetflex.model.user.User;

public interface AuthenticationService {
    boolean signup(UserDTO input);
    User authenticate(UserDTO input);
}
