package com.example.neetflex.patterns.FactoryMethodImpl;

import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.model.player.MkvPlayer;

public class MkvVideoPlayerFactory extends VideoPlayerFactory {

    @Override
   public IVideoPlayer getPlayer() {
        return new MkvPlayer();
    }
}
