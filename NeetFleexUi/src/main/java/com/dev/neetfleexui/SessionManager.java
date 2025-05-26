package com.dev.neetfleexui;

import com.dev.neetfleexui.entities.UserSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.prefs.Preferences;

public class SessionManager {

    // Use Preferences API for simple persistent storage
    private static final Preferences prefs = Preferences.userNodeForPackage(SessionManager.class);
    private static final String TOKEN_KEY = "AUTH_TOKEN";
    private static final String NAME_KEY = "USER_NAME"; // Store name too for convenience

    private static UserSession currentUserSession = null;

    // --- Session State Management ---

    public static void saveSession(UserSession session) {
        if (session != null && session.getJwtToken() != null && session.getName() != null) {
            prefs.put(TOKEN_KEY, session.getJwtToken());
            prefs.put(NAME_KEY, session.getName());
            currentUserSession = session;
            System.out.println("Session saved. Token stored.");
        } else {
            System.err.println("Attempted to save null or incomplete session.");
            clearSession(); // Ensure inconsistent state isn't saved
        }
    }

    public static UserSession getCurrentUserSession() {
        // Maybe load from prefs if null? Or rely on startup logic to populate.
        return currentUserSession;
    }

    public static void clearSession() {
        prefs.remove(TOKEN_KEY);
        prefs.remove(NAME_KEY);
        currentUserSession = null;
        System.out.println("Session cleared.");
        // Optionally: Call a backend endpoint to invalidate the token server-side
    }

    // --- Startup Logic ---

    public static boolean attemptAutoLogin() {
        String storedToken = prefs.get(TOKEN_KEY, null);
        String storedName = prefs.get(NAME_KEY, null);

        if (storedToken != null && storedName != null) {
            System.out.println("Found stored token. Attempting validation...");
            if (validateTokenWithBackend(storedToken)) {
                System.out.println("Token validation successful.");
                // Recreate the session object in memory
                currentUserSession = new UserSession(storedToken, storedName);
                return true; // Auto-login successful
            } else {
                System.out.println("Token validation failed (expired, invalid, or backend error). Clearing stored token.");
                clearSession(); // Remove invalid token
                return false; // Validation failed
            }
        }
        System.out.println("No stored token found.");
        return false; // No token to attempt auto-login with
    }

    // --- Backend Validation ---

    private static boolean validateTokenWithBackend(String token) {
        // !!! IMPORTANT: Implement this backend call !!!
        // This endpoint should just verify the token validity (signature, expiration)
        // Example endpoint: GET http://localhost:8080/auth/validate or GET /user/me
        String validationUrl = "http://localhost:8080/auth/validate"; // CHANGE TO YOUR ACTUAL VALIDATION ENDPOINT

        try {
            URL url = new URL(validationUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // Or POST, depending on your backend
            conn.setRequestProperty("Authorization", token); // Standard way to send JWT
            conn.setConnectTimeout(5000); // 5 seconds
            conn.setReadTimeout(5000);    // 5 seconds

            int responseCode = conn.getResponseCode();
            boolean isTokenValid = true;
            if (responseCode == 200) { // Successful request
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();
                reader.close();

                isTokenValid = Boolean.parseBoolean(response);
                System.out.println("Token valid: " + isTokenValid);
            } else {
                System.out.println("Error: " + responseCode + " - " + conn.getResponseMessage());
            }

            // Consider 2xx codes as valid
            if (isTokenValid) {

                    System.out.println("Backend validation successful.");
                    return false;



            } else {

                // Log response body for debugging if needed (conn.getErrorStream())
                System.out.println("Backend validation successful.");
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error connecting to backend for token validation: " + e.getMessage());
            // Handle network errors - maybe treat as invalid or retry later?
            // For simplicity here, we treat network errors as validation failure.
            return false;
        }
    }

    // --- Convenience Methods ---
    public static boolean isLoggedIn() {
        return currentUserSession != null && currentUserSession.getJwtToken() != null;
    }

    public static String getCurrentToken() {
        return currentUserSession != null ? currentUserSession.getJwtToken() : null;
    }

    public static String getUserName() {
        return currentUserSession != null ? currentUserSession.getName() : null;
    }
}