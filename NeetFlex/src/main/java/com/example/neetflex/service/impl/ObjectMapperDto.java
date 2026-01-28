package com.example.neetflex.service.impl;

import com.example.neetflex.dto.ContentUpdateDTO;
import com.example.neetflex.dto.MovieDTO;
import com.example.neetflex.dto.SeriesDTO;
import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.contents.Movie;
import com.example.neetflex.model.contents.Series;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class ObjectMapperDto {


    public static ContentResponseDTO toResponseDTO(Content content) {
        if (content == null) return null;

        ContentResponseDTO dto = new ContentResponseDTO();
        dto.setTitle(content.getTitle());
        dto.setDescription(content.getDescription());
        dto.setGenre(content.getGenre());
        dto.setDirector(content.getDirector());
        dto.setYearReleased(content.getYearReleased());
        dto.setImageUrl(content.getImageUrl());
        dto.setVideoUrl(content.getVideoUrl());
        dto.setType(content.getType()); // Assuming getContentType() returns ContentType
        dto.setEpisodes(null);
//        // If it's a series, map episodes
//        if (content instanceof Series series) {
//            if (series.getEpisodes() != null) {
//                List<EpisodeDTO> episodeDTOs = series.getEpisodes().stream()
//                        .map(episode -> new EpisodeDTO(
//                                episode.getId(),
//                                episode.getEpisodeTitle(),
//                                episode.getDuration()
//                        ))
//                        .toList();
//                dto.setEpisodes(episodeDTOs);
//            }
//        } else {
//            dto.setEpisodes(null); // not a series, no episodes
//        }

        return dto;
    }


    public static ContentUpdateDTO toUpdateDTO(Content content) {
        if (content == null) return null;

        ContentUpdateDTO dto = new ContentUpdateDTO();
        dto.setId(content.getId());
        dto.setTitle(content.getTitle());
        dto.setDescription(content.getDescription());
        dto.setGenre(content.getGenre());
        dto.setDirector(content.getDirector());
        dto.setYearReleased(content.getYearReleased());
        dto.setImageUrl(content.getImageUrl());
        dto.setVideoUrl(content.getVideoUrl());
        dto.setType(content.getType()); // Assuming getContentType() returns ContentType
        dto.setEpisodes(null);
//        // If it's a series, map episodes
//        if (content instanceof Series series) {
//            if (series.getEpisodes() != null) {
//                List<EpisodeDTO> episodeDTOs = series.getEpisodes().stream()
//                        .map(episode -> new EpisodeDTO(
//                                episode.getId(),
//                                episode.getEpisodeTitle(),
//                                episode.getDuration()
//                        ))
//                        .toList();
//                dto.setEpisodes(episodeDTOs);
//            }
//        } else {
//            dto.setEpisodes(null); // not a series, no episodes
//        }

        return dto;
    }

    public MovieDTO toMovieDTO(Movie movie) {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setImageUrl(movie.getImageUrl());
        movieDTO.setTitle(movie.getTitle());
        movieDTO.setVideoUrl(movie.getVideoUrl());
        return movieDTO;
    }

    public SeriesDTO toSeriesDTO(Series series) {
      SeriesDTO seriesDTO = new SeriesDTO();
      seriesDTO.setTitle(series.getTitle());
      seriesDTO.setImageUrl(series.getImageUrl());
      seriesDTO.setVideoUrl(series.getVideoUrl());
      return seriesDTO;
    }


}
