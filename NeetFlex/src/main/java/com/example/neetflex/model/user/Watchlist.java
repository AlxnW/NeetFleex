package com.example.neetflex.model.user;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "watchlists")
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "watchlist_seq")
    @SequenceGenerator(name = "watchlist_seq", allocationSize = 1)
    private long id;

    private String name;

    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<WatchlistContent> contents;
}
