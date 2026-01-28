package com.example.neetflex.model.player;

public class AviPlayer implements IVideoPlayer
{

    @Override
    public void play(String filePath) {
        System.out.println("PlayingAvi " + filePath);
    }
}
