package com.example.neetflex.patterns.facade;


   public class UserProfileFacade {
        private ProfileManager profileManager;
        private SettingsManager settingsManager;
        private HistoryManager historyManager;
        private FavoritesManager favoritesManager;

        public UserProfileFacade() {
            this.profileManager = new ProfileManager();
            this.settingsManager = new SettingsManager();
            this.historyManager = new HistoryManager();
            this.favoritesManager = new FavoritesManager();
        }


        public void createProfileWithDefaultSettings(String profileName) {
            profileManager.createProfile(profileName);
            settingsManager.setLanguage(profileName, "English");
            settingsManager.setVideoQuality(profileName, "HD");
        }


        public void updateProfileSettings(String profileName, String language, String quality) {
            settingsManager.setLanguage(profileName, language);
            settingsManager.setVideoQuality(profileName, quality);
        }


        public void addContentToProfile(String profileName, String contentId) {
            favoritesManager.addToFavorites(profileName, contentId);
            historyManager.addToHistory(profileName, contentId);
        }


        public void deleteProfileAndData(String profileName) {
            historyManager.clearHistory(profileName);
            profileManager.deleteProfile(profileName);
        }
    }

