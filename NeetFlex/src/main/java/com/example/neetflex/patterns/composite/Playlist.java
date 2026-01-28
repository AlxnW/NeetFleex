package com.example.neetflex.patterns.composite;

import com.example.neetflex.model.contents.Episode;
import com.example.neetflex.model.contents.Series;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class Playlist implements MediaComponent{
    private String name;
    private List<MediaComponent> mediaList = new ArrayList<>();

    public Playlist(String name) {
        this.name = name;
    }

    public void addMedia(MediaComponent media) {
        mediaList.add(media);
    }

    @Override

    public void play() {
        System.out.println("Playing playlist: " + name);
        for (MediaComponent media : mediaList) {
            // Verificăm dacă obiectul este un serial
            if (media instanceof Series) {
                Series series = (Series) media;

                series.play();
                List<Episode> episodes = series.getEpisodes();
                for (Episode episode : episodes) {
                    episode.play();
                }


            } else {
                media.play();
            }
        }
    }
}
