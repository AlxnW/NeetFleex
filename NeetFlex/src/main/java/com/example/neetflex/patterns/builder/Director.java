package com.example.neetflex.patterns.builder;

public class Director
{



    public static StreamingRequest buildStandardStreaming(int userId, int movieId) {
        return new StreamingRequestBuilder()
                .userId(userId)
                .movieId(movieId)
                .quality("HD")
                .subtitles(true)
                .audioLanguage("EN")
                .parentalControl(false)
                .build();
    }

    public static StreamingRequest buildHighQualityStreaming(int userId, int movieId) {
        return new StreamingRequestBuilder()
                .userId(userId)
                .movieId(movieId)
                .build();
    }

    public static StreamingRequest buildStreamingWithCustomParams(int userId, int movieId, String quality, boolean subtitles, String audioLanguage, boolean parentalControl) {
        return new StreamingRequestBuilder()
                .userId(userId)
                .movieId(movieId)
                .quality(quality)
                .subtitles(subtitles)
                .audioLanguage(audioLanguage)
                .parentalControl(parentalControl)
                .build();
    }

}