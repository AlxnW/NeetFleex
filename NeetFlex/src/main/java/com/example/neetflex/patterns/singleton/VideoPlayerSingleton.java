package com.example.neetflex.patterns.singleton;

public class VideoPlayerSingleton {
    private static VideoPlayerSingleton instance;


    private VideoPlayerSingleton() {
       System.out.println("Instance of VideoPlayer was created");
    }

    public static VideoPlayerSingleton getInstance() {
        if (instance == null) {
            synchronized (VideoPlayerSingleton.class) {
                if (instance == null) {
                    instance = new VideoPlayerSingleton();
                }
            }
        }
        return instance;
    }

    public void loadVideo(String videoPath) {

    }

    public void play() {

    }

    public void pause() {

    }

    public void stop() {

    }


}
