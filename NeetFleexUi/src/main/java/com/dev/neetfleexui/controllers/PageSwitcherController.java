package com.dev.neetfleexui.controllers;

import com.dev.neetfleexui.dto.ContentDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageSwitcherController {

    private static final Logger LOGGER = Logger.getLogger(PageSwitcherController.class.getName());

    private VideoPlayerController currentPlayerController = null;

    // Helper method to get the current Stage from various event sources
    private Stage getStageFromEventSource(Object eventSource) {
        if (eventSource instanceof Node) {
            Scene scene = ((Node) eventSource).getScene();
            if (scene != null) {
                return (Stage) scene.getWindow();
            }
        } else if (eventSource instanceof ActionEvent) {
            return getStageFromEvent((ActionEvent) eventSource);
        } else if (eventSource instanceof MouseEvent) {
            Scene scene = ((Node) ((MouseEvent) eventSource).getSource()).getScene();
            if (scene != null) {
                return (Stage) scene.getWindow();
            }
        }
        LOGGER.warning("Could not determine Stage from event source: " + eventSource);
        return null;
    }

    // Helper to get stage specifically from ActionEvent
    private Stage getStageFromEvent(ActionEvent event) {
        if (event != null && event.getSource() instanceof Node) {
            Scene scene = ((Node) event.getSource()).getScene();
            if (scene != null) {
                return (Stage) scene.getWindow();
            }
        }
        return null;
    }

    // Helper method to perform the scene switch, preserving size and window state
    private void switchSceneInternal(Object eventSource, String fxmlPath, String cssPath, String title, Object controllerData) {
        Stage stage = getStageFromEventSource(eventSource);
        if (stage == null || stage.getScene() == null) {
            LOGGER.severe("Cannot switch scene: Stage or current scene is null.");
            return;
        }

        // --- Capture current window state ---
        final boolean wasFullScreen = stage.isFullScreen();
        final boolean wasMaximized = stage.isMaximized();
        final boolean wasIconified = stage.isIconified();

        // --- Get current dimensions (used if not maximized/full screen) ---
        double currentWidth = stage.getScene().getWidth();
        double currentHeight = stage.getScene().getHeight();
        LOGGER.fine("Current scene dimensions: " + currentWidth + "x" + currentHeight +
                ", fullScreen: " + wasFullScreen +
                ", maximized: " + wasMaximized +
                ", minimized: " + wasIconified);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // --- Create new scene with previous dimensions ---
            Scene newScene = new Scene(root, currentWidth, currentHeight);

            // Apply CSS if provided
            if (cssPath != null && !cssPath.isEmpty()) {
                try {
                    String cssUrl = getClass().getResource(cssPath).toExternalForm();
                    newScene.getStylesheets().add(cssUrl);
                    LOGGER.fine("Applied CSS: " + cssPath);
                } catch (NullPointerException e) {
                    LOGGER.warning("Could not find CSS file: " + cssPath);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error applying CSS: " + cssPath, e);
                }
            }

            // Pass data to the controller IF needed (check type before casting)
            if (controllerData != null) {
                Object controller = loader.getController();
                if (controller instanceof MovieDetailsController && controllerData instanceof ContentDto) {
                    ((MovieDetailsController) controller).setContentData((ContentDto) controllerData);
                    LOGGER.fine("Passed ContentDto data to MovieDetailsController.");
                } else if (controller instanceof VideoPlayerController && controllerData instanceof String[]) {
                    // Assuming controllerData for player is String[]{mediaPath, title}
                    String[] playerArgs = (String[]) controllerData;
                    if (playerArgs.length == 2) {
                        // Handle potential disposal of previous player BEFORE getting new controller
                        disposePreviousPlayer(); // Call helper for cleanup
                        this.currentPlayerController = (VideoPlayerController) controller; // Store new controller
                        this.currentPlayerController.loadMedia(playerArgs[0], playerArgs[1]);
                        LOGGER.fine("Passed media path and title to VideoPlayerController.");
                    } else {
                        LOGGER.warning("Incorrect data format provided for VideoPlayerController.");
                    }
                }
                // Add more 'else if' blocks here for other controller types needing data
            }

            // Set the new scene
            stage.setScene(newScene);

            // Restore window title if provided
            if (title != null) {
                stage.setTitle(title);
            }

            // --- IMPROVED STATE RESTORATION ---
            // Use Platform.runLater to ensure full screen transition happens after scene is fully loaded
            Platform.runLater(() -> {
                // First handle full screen state - avoids the minimize-then-fullscreen effect
                if (wasFullScreen) {
                    // Set full screen immediately
                    stage.setFullScreen(true);
                    LOGGER.fine("Restored full screen state.");
                    // No need to handle maximize if we're going to full screen
                    return;
                }

                // Only if not full screen, handle maximize state
                if (wasMaximized) {
                    stage.setMaximized(true);
                    LOGGER.fine("Restored maximized state.");
                }

                // Restore iconified state only if it was specifically minimized before
                // and not going to full screen (which would make this irrelevant)
                if (wasIconified && !wasFullScreen) {
                    stage.setIconified(true);
                    LOGGER.fine("Restored minimized state.");
                }
            });

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML: " + fxmlPath, e);
            // TODO: Show user-friendly error message (e.g., Alert dialog)
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred switching scene to: " + fxmlPath, e);
            // TODO: Show user-friendly error message
        }
    }

    // Helper to dispose of the previous player instance safely
    private void disposePreviousPlayer() {
        if (currentPlayerController != null) {
            LOGGER.fine("Disposing previous video player instance.");
            try {
                currentPlayerController.disposePlayer(); // Call cleanup method
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error disposing previous player", e);
            } finally {
                currentPlayerController = null; // Clear the reference regardless
            }
        }
    }

    @FXML
    public void switchToSignUp(ActionEvent event) {
        switchSceneInternal(event,
                "/com/dev/neetfleexui/signPage.fxml",
                "/com/dev/neetfleexui/css/signPage.css",
                "NeetFleex - Sign Up",
                null); // No data needed for sign up page
    }

    @FXML
    public void switchToSignIn(MouseEvent event) {
        switchSceneInternal(event,
                "/com/dev/neetfleexui/loginPage.fxml",
                "/com/dev/neetfleexui/css/loginPage.css",
                "NeetFleex - Sign In",
                null); // No data needed for sign in page
    }

    // Keep the original signature accepting Node for flexibility if called programmatically
    public void switchToPlayer(Node eventSource, String mediaPath, String title) {
        LOGGER.info("Attempting to switch to player for: " + title + " (" + mediaPath + ")");
        // Data to pass: media path and title
        String[] playerData = {mediaPath, title};
        switchSceneInternal(eventSource, // Use the Node as the source
                "/com/dev/neetfleexui/videoPlayer.fxml",
                "/com/dev/neetfleexui/css/videoPlayer.css",
                title, // Set window title to video title
                playerData); // Pass media path and title
    }

    @FXML
    public void switchToMainPage(MouseEvent event) {
        switchSceneInternal(event,
                "/com/dev/neetfleexui/mainPage.fxml",
                "/com/dev/neetfleexui/css/mainPage.css",
                "NeetFleex", // Main page title
                null); // No specific data needed
    }

    @FXML
    public void switchToSubscriptionPage(MouseEvent event) {
        switchSceneInternal(event,
                "/com/dev/neetfleexui/subscriptionPage.fxml",
                "/com/dev/neetfleexui/css/subscriptionPage.css",
                "Subscription", // Main page title
                null); // No specific data needed
    }

    // Method to switch to Movie Details View
    public void switchToMovieDetails(ActionEvent event, ContentDto movie) {
        if (movie == null) {
            LOGGER.severe("Cannot switch to details view: MovieDto is null.");
            // TODO: Optionally show an error alert here
            return;
        }
        LOGGER.info("Switching to details view for movie: " + movie.getTitle());
        switchSceneInternal(event,
                "/com/dev/neetfleexui/contentPage.fxml",
                "/com/dev/neetfleexui/css/contentPage.css",
                movie.getTitle() + " - Details", // Window title
                movie); // Pass the movie object as data
    }

    // Method to switch to Series Details View
    public void switchToSeriesDetails(ActionEvent event, ContentDto series) {
        if (series == null) {
            LOGGER.severe("Cannot switch to details view: SeriesDto is null.");
            return;
        }
        LOGGER.info("Switching to details view for series: " + series.getTitle());
        switchSceneInternal(event,
                "/com/dev/neetfleexui/contentPage.fxml", // Assuming same FXML for now
                "/com/dev/neetfleexui/css/contentPage.css",
                series.getTitle() + " - Details", // Window title
                series); // Pass the series object as data
    }
}