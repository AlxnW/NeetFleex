package com.example.neetflex.patterns.facade;

public class SettingsManager {
    public void setLanguage(String profileName, String language) {
        System.out.println("Limba setată pentru " + profileName + ": " + language);
    }

    public void setVideoQuality(String profileName, String quality) {
        System.out.println("Calitatea video setată pentru " + profileName + ": " + quality);
    }
}
