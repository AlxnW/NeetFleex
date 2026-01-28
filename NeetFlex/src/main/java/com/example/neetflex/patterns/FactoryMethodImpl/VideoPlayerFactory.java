package com.example.neetflex.patterns.FactoryMethodImpl;

import com.example.neetflex.model.player.IVideoPlayer;
import com.example.neetflex.model.player.MkvPlayer;
import com.example.neetflex.model.player.Mp4Player;
import com.example.neetflex.model.player.AviPlayer;

public abstract class VideoPlayerFactory {

  public abstract  IVideoPlayer getPlayer() ;

}