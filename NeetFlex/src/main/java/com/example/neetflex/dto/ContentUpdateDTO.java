package com.example.neetflex.dto;

import com.example.neetflex.enums.ContentType;
import com.example.neetflex.model.contents.Episode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentUpdateDTO {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private String director;
    private int yearReleased;
    private String imageUrl;
    private String videoUrl;
    private ContentType type; // "series" or "movie"
    private List<Episode> episodes; // only filled if it's a series
}