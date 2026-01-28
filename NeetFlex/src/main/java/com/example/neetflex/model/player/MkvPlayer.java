package com.example.neetflex.model.player;

public class MkvPlayer implements IVideoPlayer {

    @Override
    public void play(String filePath) {
        System.out.println("PlayingMkv " + filePath);
    }
}

