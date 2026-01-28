package com.example.neetflex.proxy;

import java.util.HashMap;
import java.util.Map;

public class StreamingServiceProxy implements StreamingService {
    private RealStreamingService realService;
    private Map<String, Boolean> userSubscriptions;

    public StreamingServiceProxy() {
        this.realService = new RealStreamingService();
        this.userSubscriptions = new HashMap<>();

        userSubscriptions.put("user1", true);  // Abonat
        userSubscriptions.put("user2", false); // Neabonat
    }

    @Override
    public void playMovie(String movieTitle, String userId) {
        if (isUserSubscribed(userId)) {
            realService.playMovie(movieTitle, userId);
        } else {
            System.out.println("Eroare: Utilizatorul " + userId + " nu are abonament activ.");
        }
    }

    private boolean isUserSubscribed(String userId) {
        return userSubscriptions.getOrDefault(userId, false);
    }
}
