package com.example.neetflex.patterns.facade;

public class HistoryManager {
    public void addToHistory(String profileName, String contentId) {
        System.out.println("Adăugat la istoric pentru " + profileName + ": " + contentId);
    }

    public void clearHistory(String profileName) {
        System.out.println("Istoric șters pentru " + profileName);
    }
}
