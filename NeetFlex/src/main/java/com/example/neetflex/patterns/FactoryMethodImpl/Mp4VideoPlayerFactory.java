package com.example.neetflex.patterns.FactoryMethodImpl;

import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.model.player.Mp4Player;

public class Mp4VideoPlayerFactory extends VideoPlayerFactory {



@Override
    public  IVideoPlayer getPlayer() {

        return new Mp4Player();
    }

}
