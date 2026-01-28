package com.example.neetflex.patterns.facade;

public class FavoritesManager {
    public void addToFavorites(String profileName, String contentId) {
        System.out.println("Adăugat la favorite pentru " + profileName + ": " + contentId);
    }
}
