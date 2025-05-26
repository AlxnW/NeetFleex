package com.dev.neetfleexui;

import com.dev.neetfleexui.SessionManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {

    private boolean intendedFullScreen = true; // Flag to track if WE want full-screen

    @Override
    public void start(Stage stage) throws IOException {
        String initialView;

        // Session checking logic remains the same
        if (SessionManager.attemptAutoLogin()) {
            System.out.println("Auto-login successful. Loading main page.");
            initialView = "mainPage";

            System.out.println(SessionManager.getCurrentToken());
        } else {
            System.out.println("No valid session found. Loading auth page.");
            initialView = "loginPage";
//            initialView = "subscriptionPage";
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(initialView + ".fxml"));

        // Get screen dimensions to use instead of hardcoded values
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Use a percentage of screen size (90%) instead of hardcoded values
        Scene scene = new Scene(fxmlLoader.load(), screenWidth , screenHeight );

        // CSS loading logic remains the same
        try {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/" + initialView + ".css")).toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Warning: Could not load CSS for view: " + initialView + ".css");
        } catch (Exception e) {
            System.err.println("Error loading CSS for view: " + initialView + ".css - " + e.getMessage());
        }

        stage.setTitle("NeetFleex");
        stage.setScene(scene);

        // Better full screen setup with improved hint
        stage.setFullScreenExitHint("Press ESC to exit full screen");

        // Center the window on the screen before going full screen
        stage.centerOnScreen();

        // Use Platform.runLater to ensure full screen is applied after the window is fully initialized
        Platform.runLater(() -> {
            stage.setFullScreen(intendedFullScreen);
            System.out.println("Initial full screen state set: " + intendedFullScreen);
        });

        // Listener to manage full screen behavior
        stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && intendedFullScreen) {
                System.out.println("Exited full screen. Current intended state: " + intendedFullScreen);
                // Optional: Add logic here if you want to handle ESC key press differently
            } else if (newValue) {
                intendedFullScreen = true;
                System.out.println("Entered full screen.");
            }
        });

        // Improved maximize button listener
        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && intendedFullScreen && !stage.isFullScreen()) {
                System.out.println("Window maximized, but intended full screen. Re-engaging full screen.");
                Platform.runLater(() -> stage.setFullScreen(true));
            }
        });

        stage.show();
    }

    // Method to toggle full-screen from within the app
    public void toggleFullScreen(Stage stage) {
        intendedFullScreen = !stage.isFullScreen();
        stage.setFullScreen(intendedFullScreen);
        System.out.println("Toggled full screen. Intended state: " + intendedFullScreen);
    }

    public static void main(String[] args) {
        launch();
    }
}