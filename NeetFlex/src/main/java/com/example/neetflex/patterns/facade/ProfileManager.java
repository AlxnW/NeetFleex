package com.example.neetflex.patterns.facade;

public class ProfileManager {

    public void createProfile(String profileName) {
        System.out.println("Profil creat: " + profileName);
    }

    public void deleteProfile(String profileName) {
        System.out.println("Profil șters: " + profileName);
    }

}
