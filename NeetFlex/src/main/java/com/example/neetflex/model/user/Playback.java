package com.example.neetflex.model.user;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data

public class Playback {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playback_seq")
    @SequenceGenerator(name = "playback_seq", allocationSize = 1)
    private Long id;

    @OneToMany(mappedBy = "playback", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PlaybackContent> contents = new ArrayList<>();



}
