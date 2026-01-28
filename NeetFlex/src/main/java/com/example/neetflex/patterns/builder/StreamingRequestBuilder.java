package com.example.neetflex.patterns.builder;

import lombok.Getter;

@Getter
public class StreamingRequestBuilder implements IBuilder<StreamingRequest> {
    private int userId;
    private int movieId;
    private String quality ;
    private boolean subtitles ;
    private String audioLanguage ;
    private boolean parentalControl;

    public StreamingRequestBuilder userId(int userId) {
        this.userId = userId;
        return this;
    }

    public StreamingRequestBuilder movieId(int movieId) {
        this.movieId = movieId;
        return this;
    }

    public StreamingRequestBuilder quality(String quality) {
        this.quality = quality;
        return this;
    }

    public StreamingRequestBuilder subtitles(boolean subtitles) {
        this.subtitles = subtitles;
        return this;
    }

    public StreamingRequestBuilder audioLanguage(String audioLanguage) {
        this.audioLanguage = audioLanguage;
        return this;
    }

    public StreamingRequestBuilder parentalControl(boolean parentalControl) {
        this.parentalControl = parentalControl;
        return this;
    }

    @Override
    public StreamingRequest build() {
        return new StreamingRequest(this);
    }
}
