package com.example.neetflex.patterns.factories;

import com.example.neetflex.model.player.AviPlayer;
import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.model.substitles.ISubtitle;
import com.example.neetflex.model.substitles.SubtitleSub;

public class AviMediaFactory implements IMediaFactory {

    @Override
    public IVideoPlayer createVideoPlayer() {
       return new AviPlayer();
    }

    @Override
    public ISubtitle createSubtitle() {
        return new SubtitleSub();
    }
}
