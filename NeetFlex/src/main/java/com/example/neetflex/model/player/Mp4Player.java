package com.example.neetflex.model.player;

public class Mp4Player implements IVideoPlayer {

    @Override
    public void play(String filePath) {
        System.out.println("PlayingMp4 " + filePath);
    }
}
