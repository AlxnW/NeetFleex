package com.example.neetflex.model.contents;

import com.example.neetflex.patterns.composite.MediaComponent;
import com.example.neetflex.enums.ContentType;
import com.example.neetflex.util.IPlayable;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "series")
public class Series extends Content implements IPlayable,  MediaComponent
{
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



    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes;  // Lista de episoade ale serialului

    @Override
    public void play() {
     System.out.println("Playing a series" + title);
    }

    public Series(String title) {
        this.title = title;
    }

    public Series() {}

//    @Override
//    public Series shallowClone() {
//        Series clone = new Series();
//        clone.setTitle(this.title);
//        clone.setDescription(this.description);
//        clone.setGenre(this.genre);
//        clone.setDirector(this.director);
//        clone.setDuration(this.duration);
//        clone.setYearReleased(this.yearReleased);
//        clone.setImageUrl(this.imageUrl);
//        clone.setVideoUrl(this.videoUrl);
//        // Shallow copy: copy references
//        clone.setWatchlist(this.watchlist);
//        clone.setPlayback(this.playback);
//        return clone;
//    }

//    @Override
//    public Series deepClone() {
//        Series clone = new Series();
//        clone.setTitle(this.title);
//        clone.setDescription(this.description);
//        clone.setGenre(this.genre);
//        clone.setDirector(this.director);
//        clone.setDuration(this.duration);
//        clone.setYearReleased(this.yearReleased);
//        clone.setImageUrl(this.imageUrl);
//        clone.setVideoUrl(this.videoUrl);
//        // Deep copy: clone related objects if possible
//        clone.setWatchlist(this.watchlist != null ? this.watchlist.deepClone() : null);
//        clone.setPlayback(this.playback != null ? this.playback.deepClone() : null);
//        return clone;
//    }


    public void addEpisode(Episode episode) {
        if (this.episodes == null) this.episodes = new ArrayList<>();
        if (episode != null && !this.episodes.contains(episode)) {
            this.episodes.add(episode);
            episode.setSeries(this);
            System.out.println("[Series: " + getTitle() + "] Added Episode: " + episode.getEpisodeTitle());
        }
    }

    @Override
    public String toString() { // Example toString
        return "Series{" +
                "id=" + id +
                ", title='" + title + '\'' +
                // Accessing size() might trigger lazy loading if not careful
                ", episodeCount_reported=" + (episodes != null ? episodes.size() : 0) +
                '}';
    }

    @Override
    public ContentType getType() {
        return ContentType.SERIES;
    }


//    public List<Episode> addEpisode(Episode episode) {
//        episodes.add(episode);
//        return  episodes;
//    }
}
