package com.example.neetflex.model.contents;

import com.example.neetflex.patterns.composite.MediaComponent;
import com.example.neetflex.enums.ContentType;
import com.example.neetflex.util.IPlayable;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity

public class Movie extends Content implements IPlayable, MediaComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", allocationSize = 1)
    private Long id;
    private String title;
    private String description;
    private String genre;
    private String director;
    private int duration;
    private int yearReleased;
    private String imageUrl;
    private String videoUrl;







    @Override
    public void play() {
      System.out.println("Playing a movie " + title);
    }


    @Override
    public ContentType getType() {
        return ContentType.MOVIE;
    }
}
