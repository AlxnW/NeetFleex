package com.example.neetflex.model.contents;

import com.example.neetflex.patterns.composite.MediaComponent;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Episode implements MediaComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", allocationSize = 1)
    private Long id;
    private String episodeTitle;
    private int episodeNumber;
    private int duration; // Duration in minutes

    public Episode(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "series_id")
    private Series series;

    public Episode() {

    }

    @Override
    public void play() {
        System.out.println("Playing episode: " + episodeTitle);
    }
}

