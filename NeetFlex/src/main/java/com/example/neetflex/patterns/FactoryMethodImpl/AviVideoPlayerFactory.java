package com.example.neetflex.patterns.FactoryMethodImpl;

import com.example.neetflex.model.player.AviPlayer;
import com.example.neetflex.model.player.IVideoPlayer;

public class AviVideoPlayerFactory extends VideoPlayerFactory {

    @Override
   public IVideoPlayer getPlayer() {
        return new AviPlayer();
    }
}
