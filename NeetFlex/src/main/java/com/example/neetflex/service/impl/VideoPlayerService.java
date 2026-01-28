package com.example.neetflex.service.impl;

import com.example.neetflex.patterns.FactoryMethodImpl.AviVideoPlayerFactory;
import com.example.neetflex.patterns.FactoryMethodImpl.MkvVideoPlayerFactory;
import com.example.neetflex.patterns.FactoryMethodImpl.Mp4VideoPlayerFactory;
import com.example.neetflex.patterns.FactoryMethodImpl.VideoPlayerFactory;
import com.example.neetflex.patterns.factories.AviMediaFactory;
import com.example.neetflex.patterns.factories.IMediaFactory;
import com.example.neetflex.patterns.factories.MkvMediaFactory;
import com.example.neetflex.patterns.factories.Mp4MediaFactory;
import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.model.substitles.ISubtitle;

public class VideoPlayerService {

    public void playVideo(String filePath) {
        VideoPlayerFactory factory;

        if (filePath.endsWith(".mp4")) {
            factory = new Mp4VideoPlayerFactory();
        } else if (filePath.endsWith(".avi")) {
            factory = new AviVideoPlayerFactory();
        } else if (filePath.endsWith(".mkv")) {
            factory = new MkvVideoPlayerFactory();
        } else {
            throw new IllegalArgumentException("Unsupported video format");
        }

        IVideoPlayer player = factory.getPlayer();
        player.play(filePath);
    }

    public void playVideoWithSubtitles(String filePath) {
        IMediaFactory mediaFactory;

        if (filePath.endsWith(".mp4")) {
            mediaFactory = new Mp4MediaFactory();
        } else if (filePath.endsWith(".avi")) {
            mediaFactory = new AviMediaFactory();
        } else if (filePath.endsWith(".mkv")) {
            mediaFactory = new MkvMediaFactory();
        } else {
            throw new IllegalArgumentException("Unsupported video format");
        }

        IVideoPlayer player = mediaFactory.createVideoPlayer();
        ISubtitle subtitle = mediaFactory.createSubtitle();
        player.play(filePath);
        subtitle.addSubtitle();
    }


}
