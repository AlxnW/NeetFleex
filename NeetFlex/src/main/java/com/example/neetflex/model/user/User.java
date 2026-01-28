package com.example.neetflex.model.user;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
//SRP Single Responsibility Principle
    public class User implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
        @SequenceGenerator(name = "users_id_seq", allocationSize = 1)
        private Long id;
        @Column(name="username",nullable = false, unique = true)
        private String username;
        @Column(name="password",nullable = false)
        private String password;
        @Column(name="email",nullable = false)
        private String email;
        @Column(name="first_name",nullable = true)
        private String firstName;
        @Column(name="last_name",nullable = true)
        private String lastName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "watchlist_id")
    private Watchlist watchlist;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "playback_id")
    private Playback playback;


    @OneToOne(
                mappedBy = "user",
                cascade = CascadeType.ALL,
        fetch = FetchType.LAZY)
        private RecommendationSystem recommendationSystem;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL)
    private Subscription subscription;


    }
