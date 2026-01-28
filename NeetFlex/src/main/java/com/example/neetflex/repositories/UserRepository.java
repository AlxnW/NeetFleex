package com.example.neetflex.repositories;

import com.example.neetflex.model.user.Playback;
import com.example.neetflex.model.user.User;
import com.example.neetflex.model.user.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findFirstByUsername(String username);
    @Query("SELECT pb FROM User u " +
            "JOIN u.playback pb " +
            "LEFT JOIN FETCH pb.contents " +
            "WHERE u.username = :userName")
    Playback getPlaybackByUsername(String userName);

    @Query("SELECT pb FROM User u " +
            "JOIN u.watchlist pb " +
            "LEFT JOIN FETCH pb.contents " +
            "WHERE u.username = :userName")
    Watchlist getWatchlistByUsername(String userName);





}
