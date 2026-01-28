package com.example.neetflex.service.impl;

import com.example.neetflex.model.user.Playback;
import com.example.neetflex.model.user.User;
import com.example.neetflex.model.user.Watchlist;
import com.example.neetflex.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public Playback getPlaybackByName(String userName) {

        return userRepository.getPlaybackByUsername(userName);
    }


    public Watchlist getWatchListByName(String userName) {
        return userRepository.getWatchlistByUsername(userName);
    }


    public void save(User user) {
        userRepository.save(user);
    }
}
