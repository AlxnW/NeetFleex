package com.example.neetflex.patterns.factories;

import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.model.substitles.ISubtitle;

public interface IMediaFactory
{
    IVideoPlayer createVideoPlayer();
    ISubtitle createSubtitle();
}
