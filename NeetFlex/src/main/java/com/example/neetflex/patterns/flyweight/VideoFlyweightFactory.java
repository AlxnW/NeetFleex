package com.example.neetflex.patterns.flyweight;

import java.util.HashMap;
import java.util.Map;

public class VideoFlyweightFactory {
    public static final Map<String, SharedVideo> videos = new HashMap<>();

    public static VideoFlyweight getVideo(String title, String resolution) {
        String key = title + "_" + resolution;
        videos.putIfAbsent(key, new SharedVideo(title, resolution));
        return videos.get(key);
    }
}
