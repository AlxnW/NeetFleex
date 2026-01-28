package com.example.neetflex.patterns.factories;

import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.model.player.MkvPlayer;
import com.example.neetflex.model.substitles.ISubtitle;
import com.example.neetflex.model.substitles.SubtitleSsa;

public class MkvMediaFactory implements IMediaFactory {
    @Override
    public IVideoPlayer createVideoPlayer() {
        return new MkvPlayer();
    }

    @Override
    public ISubtitle createSubtitle() {
        return new SubtitleSsa();
    }
}
