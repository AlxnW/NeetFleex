package com.example.neetflex.patterns.flyweight;

public class SharedVideo implements VideoFlyweight {
    private final String title;
    private final String resolution;

    public SharedVideo(String title, String resolution) {
        this.title = title;
        this.resolution = resolution;
    }

    @Override
    public void play(String user) {
        System.out.println(user + " is watching " + title + " at " + resolution);
    }
}
