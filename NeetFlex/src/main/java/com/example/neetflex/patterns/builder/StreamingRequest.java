package com.example.neetflex.patterns.builder;

public class StreamingRequest {
    private final int userId;
    private final int movieId;
    private final String quality;
    private final boolean subtitles;
    private final String audioLanguage;
    private final boolean parentalControl;

    public StreamingRequest(StreamingRequestBuilder builder) {
        this.userId = builder.getUserId();
        this.movieId = builder.getMovieId();
        this.quality = builder.getQuality();
        this.subtitles = builder.isSubtitles();
        this.audioLanguage = builder.getAudioLanguage();
        this.parentalControl = builder.isParentalControl();
    }

    @Override
    public String toString() {
        return "StreamingRequest{" +
                "userId=" + userId +
                ", movieId=" + movieId +
                ", quality='" + quality + '\'' +
                ", subtitles=" + subtitles +
                ", audioLanguage='" + audioLanguage + '\'' +
                ", parentalControl=" + parentalControl +
                '}';
    }
}