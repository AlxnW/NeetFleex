package com.example.neetflex.patterns.factories;

import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.model.player.Mp4Player;
import com.example.neetflex.model.substitles.ISubtitle;
import com.example.neetflex.model.substitles.SubtitleSrt;

public class Mp4MediaFactory implements IMediaFactory {
    @Override
    public IVideoPlayer createVideoPlayer() {
        return new Mp4Player();
    }

    @Override
    public ISubtitle createSubtitle() {
        return new SubtitleSrt();
    }
}
