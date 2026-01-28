package com.example.neetflex.proxy;

public class RealStreamingService implements StreamingService {
    @Override
    public void playMovie(String movieTitle, String userId) {
        System.out.println("Redare film: " + movieTitle + " pentru utilizatorul " + userId);
    }
}
