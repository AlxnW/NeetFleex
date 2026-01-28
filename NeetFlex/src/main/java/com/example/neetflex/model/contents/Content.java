package com.example.neetflex.model.contents;

import com.example.neetflex.enums.ContentType;
import lombok.*;
@Data
public abstract class Content {


    private Long id;
    private String imageUrl;
    private String title;
    private String videoUrl;
    private String description;
    private int yearReleased;
    private String director;
    private String genre;

    // You can also add an abstract method if needed
    public abstract ContentType getType();

}
