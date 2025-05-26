package com.dev.neetfleexui.controllers;

import com.dev.neetfleexui.dto.ContentDto;
import com.dev.neetfleexui.dto.ContentType;
import com.dev.neetfleexui.dto.EpisodeDto;
import com.dev.neetfleexui.dto.RequestDetails; // You need to create this DTO
import com.dev.neetfleexui.singleton.PageSwitcherControllerSingleton;
import com.dev.neetfleexui.SessionManager; // You need to create/update this

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
// Alert import removed
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieDetailsController {

    private static final Logger LOGGER = Logger.getLogger(MovieDetailsController.class.getName());
    private final PageSwitcherController pageSwitcherController = PageSwitcherControllerSingleton.getInstance();

    // --- API Configuration ---
    private static final String API_BASE_URL = "http://localhost:8080";
    // POST Endpoints
    private static final String ENDPOINT_ADD_TO_WATCHLIST = "/addToWatchlist";
    private static final String ENDPOINT_ADD_TO_PLAYBACK = "/addToPlayback";
    private static final String ENDPOINT_DELETE_FROM_PLAYBACK = "/remove/from/Playback";
    private static final String ENDPOINT_DELETE_FROM_WATCHLIST = "/remove/from/Watchlist";

    // GET Endpoints (with request body)
    private static final String ENDPOINT_STATUS_LIKED = "/get/status/liked-button";
    private static final String ENDPOINT_STATUS_MY_LIST = "/get/status/my-list-button";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();


    // --- FXML Fields ---
    @FXML private StackPane heroSection;
    @FXML private Label contentTitle;
    @FXML private Label contentDescription;
    @FXML private FlowPane contentMetaPane;
    @FXML private Label metaYear;
    @FXML private Label metaGenre;
    @FXML private Label metaDirector;
    @FXML private ToggleButton likeButton;
    @FXML private ToggleButton addToListButton;
    @FXML private Button playButton;

    private ContentDto currentContent;

    @FXML
    public void initialize() {
        if (heroSection == null) LOGGER.severe("CRITICAL: heroSection StackPane is null. Check FXML mapping.");
        else clearHeroBackground();
        if (contentTitle == null) LOGGER.severe("contentTitle Label is null.");
        if (contentDescription == null) LOGGER.severe("contentDescription Label is null.");
        if (likeButton == null) LOGGER.severe("likeButton is null.");
        if (addToListButton == null) LOGGER.severe("addToListButton is null.");
        if (playButton == null) LOGGER.severe("playButton is null.");

        if (likeButton != null) likeButton.setDisable(true);
        if (addToListButton != null) addToListButton.setDisable(true);
        if (playButton != null) playButton.setDisable(true);
    }

    public void setContentData(ContentDto contentDto) {
        if (contentDto == null) {
            LOGGER.severe("setContentData called with null ContentDto. Displaying error state.");
            displayErrorState();
            return;
        }
        this.currentContent = contentDto;
        LOGGER.info("Initializing details for " + contentDto.getType() + ": " + contentDto.getTitle());

        contentTitle.setText(currentContent.getTitle() != null ? currentContent.getTitle() : "No Title");
        contentDescription.setText(currentContent.getDescription() != null ? currentContent.getDescription() : "No description available.");
        metaYear.setText(currentContent.getYearReleased() > 0 ? String.valueOf(currentContent.getYearReleased()) : "N/A");
        metaGenre.setText(currentContent.getGenre() != null && !currentContent.getGenre().isBlank() ? currentContent.getGenre() : "N/A");
        metaDirector.setText(currentContent.getDirector() != null && !currentContent.getDirector().isBlank() ? currentContent.getDirector() : "N/A");

        updateHeroBackground(currentContent.getImageUrl());

        if (playButton != null) playButton.setDisable(false);

        if (this.currentContent.getTitle() != null && SessionManager.getUserName() != null) {
            fetchInitialButtonStatus(ENDPOINT_STATUS_LIKED, likeButton, "Like");
            fetchInitialButtonStatus(ENDPOINT_STATUS_MY_LIST, addToListButton, "My List");
        } else {
            LOGGER.warning("Cannot fetch button statuses: current content title or username is null.");
            if (likeButton != null) { likeButton.setSelected(false); likeButton.setDisable(true); }
            if (addToListButton != null) { addToListButton.setSelected(false); addToListButton.setDisable(true); }
        }
    }

    private void fetchInitialButtonStatus(String endpoint, ToggleButton button, String buttonName) {
        if (button == null) {
            LOGGER.warning("Cannot fetch " + buttonName + " status: button is null.");
            return;
        }
        if (currentContent == null || currentContent.getTitle() == null || SessionManager.getUserName() == null) {
            LOGGER.warning("Cannot fetch " + buttonName + " status: missing content details or username for " + (currentContent != null ? currentContent.getTitle() : "unknown content") + ".");
            Platform.runLater(() -> {
                button.setSelected(false);
                button.setDisable(true);
            });
            return;
        }

        String bearerToken = SessionManager.getCurrentToken();
        if (bearerToken == null || bearerToken.isBlank()) {
            LOGGER.severe("Cannot fetch " + buttonName + " status for '" + currentContent.getTitle() + "': Bearer token is missing or empty. User: " + SessionManager.getUserName());
            Platform.runLater(() -> {
                button.setSelected(false);
                button.setDisable(true); // Or false, and let the request fail
            });
            return;
        }

        RequestDetails requestDetails = new RequestDetails(
                currentContent.getTitle(),
                SessionManager.getUserName(),
                currentContent.getType()
        );

        try {
            String jsonRequestBody = objectMapper.writeValueAsString(requestDetails);
            // This is a GET request with a body, as per backend signature @GetMapping @RequestBody
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + bearerToken)
                    .method("GET", HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            try {
                                Boolean isActive = objectMapper.readValue(response.body(), Boolean.class);
                                Platform.runLater(() -> {
                                    button.setSelected(isActive);
                                    button.setDisable(false);
                                    LOGGER.info(buttonName + " button status for '" + currentContent.getTitle() + "': " + isActive);
                                });
                            } catch (Exception e) {
                                LOGGER.log(Level.SEVERE, "Failed to parse " + buttonName + " status response for '" + currentContent.getTitle() + "': " + response.body(), e);
                                Platform.runLater(() -> {
                                    button.setSelected(false); // Default to false on parse error
                                    button.setDisable(false);  // Enable button but show default state
                                });
                            }
                        } else {
                            LOGGER.warning("Failed to fetch " + buttonName + " status for '" + currentContent.getTitle() + "'. Status: " + response.statusCode() + ", Body: " + response.body());
                            Platform.runLater(() -> {
                                button.setSelected(false); // Default to false on HTTP error
                                button.setDisable(false);  // Enable button but show default state
                            });
                        }
                    })
                    .exceptionally(e -> {
                        LOGGER.log(Level.SEVERE, "Exception fetching " + buttonName + " status for '" + currentContent.getTitle() + "'", e);
                        Platform.runLater(() -> {
                            button.setSelected(false); // Default to false on exception
                            button.setDisable(false);  // Enable button but show default state
                        });
                        return null;
                    });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error preparing request for " + buttonName + " status for '" + currentContent.getTitle() + "'", e);
            Platform.runLater(() -> {
                button.setSelected(false);
                button.setDisable(true); // Keep disabled if request prep fails
            });
        }
    }

        private void handleToggleButtonAction(ToggleButton button, String endpointToAdd, String endpointToRemove ,String listNameLog, String buttonNameUser) {
            if (currentContent == null || currentContent.getTitle() == null || SessionManager.getUserName() == null) {
                LOGGER.warning("Action on " + buttonNameUser + " button aborted: Content, title, or user not available for " + (currentContent != null ? currentContent.getTitle() : "unknown content") + ".");
                if (button != null) Platform.runLater(() -> button.setSelected(!button.isSelected())); // Revert UI change
                return;
            }

            String bearerToken = SessionManager.getCurrentToken();
            if (bearerToken == null || bearerToken.isBlank()) {
                LOGGER.severe("Action on " + buttonNameUser + " for '" + currentContent.getTitle() + "' aborted: Bearer token is missing or empty. User: " + SessionManager.getUserName());
                if (button != null) Platform.runLater(() -> button.setSelected(!button.isSelected())); // Revert UI
                return;
            }

            boolean isSelected = button.isSelected(); // This is the desired state *after* the click
            LOGGER.info(buttonNameUser + " button toggled. Desired state: " + (isSelected ? "ADD" : "REMOVE (No backend remove op)") + " for '" + currentContent.getTitle() + "'.");

            if (isSelected) { // User wants to ADD to the list (button is now selected)
                RequestDetails requestDetails = new RequestDetails(
                        currentContent.getTitle(), SessionManager.getUserName(),  // Note: Order was different in fetch, ensure backend consistency or DTO handles it
                        currentContent.getType()
                );

                try {
                    String jsonRequestBody = objectMapper.writeValueAsString(requestDetails);
                    // This is a POST request, as per backend signature @PostMapping
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(API_BASE_URL + endpointToAdd))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + bearerToken)
                            .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                            .build();

                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenAccept(response -> {
                                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                                    LOGGER.info("Successfully added '" + currentContent.getTitle() + "' to " + listNameLog + ". Status: " + response.statusCode());
                                    // UI is already selected, no change needed.
                                } else {
                                    LOGGER.warning("Failed to add '" + currentContent.getTitle() + "' to " + listNameLog + ". Status: " + response.statusCode() + ", Body: " + response.body());
                                    // Revert UI selection on failure
                                    Platform.runLater(() -> button.setSelected(false));
                                }
                            })
                            .exceptionally(e -> {
                                LOGGER.log(Level.SEVERE, "Exception sending 'add to " + listNameLog + "' request for '" + currentContent.getTitle() + "'", e);
                                // Revert UI selection on exception
                                Platform.runLater(() -> button.setSelected(false));
                            return null;
                        });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error preparing 'add to " + listNameLog + "' request for '" + currentContent.getTitle() + "'", e);
                // Revert UI selection on preparation error
                Platform.runLater(() -> button.setSelected(false));
            }
        } else {
            // User wants to REMOVE from the list (button is now deselected)
            // ** LIMITATION: No backend endpoint provided for removing. **
            // The UI will show it as removed, but the backend state is not changed by this action.
            // A
                // 'DELETE /removeFrom...' or similar endpoint would be needed for full functionality.


                RequestDetails requestDetails = new RequestDetails(
                        currentContent.getTitle(), SessionManager.getUserName(),
                        currentContent.getType()
                );

                try {
                    String jsonRequestBody = objectMapper.writeValueAsString(requestDetails);
                    // This is a POST request, as per backend signature @PostMapping
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(API_BASE_URL + endpointToRemove))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + bearerToken)
                            .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                            .build();

                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenAccept(response -> {
                                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                                    LOGGER.info("Successfully added '" + currentContent.getTitle() + "' to " + listNameLog + ". Status: " + response.statusCode());
                                    // UI is already selected, no change needed.
                                } else {
                                    LOGGER.warning("Failed to add '" + currentContent.getTitle() + "' to " + listNameLog + ". Status: " + response.statusCode() + ", Body: " + response.body());
                                    // Revert UI selection on failure
                                    Platform.runLater(() -> button.setSelected(false));
                                }
                            })
                            .exceptionally(e -> {
                                LOGGER.log(Level.SEVERE, "Exception sending 'add to " + listNameLog + "' request for '" + currentContent.getTitle() + "'", e);
                                // Revert UI selection on exception
                                Platform.runLater(() -> button.setSelected(false));
                                return null;
                            });
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error preparing 'add to " + listNameLog + "' request for '" + currentContent.getTitle() + "'", e);
                    // Revert UI selection on preparation error
                    Platform.runLater(() -> button.setSelected(false));
                }


            LOGGER.info("User deselected " + buttonNameUser + " for '" + currentContent.getTitle() + "'. UI updated, no backend 'remove' call implemented for this action.");
            // UI is already deselected, no change needed unless there was a remove endpoint that failed.
        }
    }


    // --- Background Image Handling ---
    private void updateHeroBackground(String imageUrl) {
        if (heroSection == null) {
            LOGGER.warning("Cannot update background, heroSection is null.");
            return;
        }
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                String cssUrl = formatUrlForCss(imageUrl);
                heroSection.setStyle(
                        "-fx-background-image: url('" + cssUrl + "'); " +
                                "-fx-background-size: cover; " +
                                "-fx-background-position: center center; " +
                                "-fx-background-repeat: no-repeat;"
                );
                LOGGER.fine("Successfully set background image style to: " + cssUrl);
            } catch (MalformedURLException | URISyntaxException e) {
                LOGGER.log(Level.SEVERE, "Failed to create a valid CSS URL from: " + imageUrl, e);
                clearHeroBackground();
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Invalid image URL provided: " + imageUrl, e);
                clearHeroBackground();
            }
        } else {
            LOGGER.warning("Image URL is null or blank. Clearing background.");
            clearHeroBackground();
        }
    }

    private void clearHeroBackground() {
        if (heroSection != null) {
            heroSection.setStyle("-fx-background-image: none;");
            LOGGER.fine("Cleared hero section background image style.");
        }
    }

    private String formatUrlForCss(String urlOrPath) throws MalformedURLException, URISyntaxException, IllegalArgumentException {
        if (urlOrPath == null || urlOrPath.isBlank()) {
            throw new IllegalArgumentException("URL/Path cannot be null or blank");
        }
        URL url;
        String cleanedPath = urlOrPath.trim().replace("\"", "");
        if (cleanedPath.matches("^[a-zA-Z]+:/{1,2}.*")) {
            try {
                URI uri = new URI(cleanedPath);
                url = uri.toURL();
                LOGGER.finest("Treating as absolute URI: " + cleanedPath);
            } catch (URISyntaxException | MalformedURLException e) {
                LOGGER.log(Level.WARNING, "Could not parse as absolute URI/URL, attempting file path: " + cleanedPath, e);
                url = pathToUrl(cleanedPath);
            }
        } else {
            LOGGER.finest("Treating as file path: " + cleanedPath);
            url = pathToUrl(cleanedPath);
        }
        return url.toExternalForm();
    }

    private URL pathToUrl(String path) throws MalformedURLException {
        File file = new File(path);
        return file.toURI().toURL();
    }


    // --- UI Event Handlers ---
    @FXML
    private void handlePlay(ActionEvent event) {
        LOGGER.info("Play button clicked.");
        if (currentContent == null) {
            LOGGER.warning("Play clicked, but no content data is available.");
            // No UI alert
            return;
        }

        String videoUrl = null;
        String title = currentContent.getTitle() != null ? currentContent.getTitle() : "Content";
        ContentType type = currentContent.getType();

        if (type == ContentType.MOVIE) {
            videoUrl = currentContent.getVideoUrl();
            LOGGER.info("Play requested for MOVIE: " + title + (videoUrl == null || videoUrl.isBlank() ? " (No URL)" : ""));
        } else if (type == ContentType.SERIES) {
            LOGGER.info("Play requested for SERIES: " + title);
            videoUrl = currentContent.getVideoUrl();
            if (videoUrl == null || videoUrl.isBlank()) {
                LOGGER.info("Series main videoUrl is empty, checking episodes...");
                List<EpisodeDto> episodes = currentContent.getEpisodes();
                if (episodes != null && !episodes.isEmpty()) {
                    EpisodeDto firstEpisode = episodes.get(0);
                    if (firstEpisode != null && firstEpisode.getVideoUrl() != null && !firstEpisode.getVideoUrl().isBlank()) {
                        videoUrl = firstEpisode.getVideoUrl();
                        LOGGER.info("Using first episode URL for playback: " + videoUrl);
                    } else {
                        LOGGER.warning("First episode found, but its video URL is missing for series: " + title);
                    }
                } else {
                    LOGGER.warning("No episodes list found or list is empty for series: " + title);
                }
            } else {
                LOGGER.info("Using main videoUrl provided for series: " + videoUrl);
            }
        } else {
            LOGGER.warning("Play clicked for unsupported content type: " + type);
            // No UI alert
            return;
        }

        if (videoUrl != null && !videoUrl.isBlank()) {
            Node sourceNode = (Node) event.getSource();
            try {
                pageSwitcherController.switchToPlayer(sourceNode, videoUrl, title);
                LOGGER.info("Attempting navigation to player for: '" + title + "' URL: " + videoUrl);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error switching to player for " + title, e);
                // No UI alert
            }
        } else {
            LOGGER.warning("No valid video URL could be determined to play for: '" + title + "' (Type: " + type + ")");
            // No UI alert
        }
    }

    @FXML
    private void handleLike(ActionEvent event) {
        handleToggleButtonAction(likeButton, ENDPOINT_ADD_TO_WATCHLIST, ENDPOINT_DELETE_FROM_WATCHLIST, "watchlist", "Like");
    }

    @FXML
    private void handleAddToList(ActionEvent event) {
        handleToggleButtonAction(addToListButton, ENDPOINT_ADD_TO_PLAYBACK, ENDPOINT_DELETE_FROM_PLAYBACK,"playback list", "My List");
    }

    @FXML
    public void switchToHome(MouseEvent event) { // Renamed from handleBack
        LOGGER.fine("switchToHome (Acasă) triggered by MouseEvent on: " + event.getSource());
        pageSwitcherController.switchToMainPage(event);
    }

    private void displayErrorState() {
        LOGGER.info("Displaying error state in UI for movie details.");
        contentTitle.setText("Error Loading Content");
        contentDescription.setText("Details could not be loaded. Please try again later.");
        metaYear.setText("-");
        metaGenre.setText("-");
        metaDirector.setText("-");
        clearHeroBackground();
        if (likeButton != null) { likeButton.setSelected(false); likeButton.setDisable(true); }
        if (addToListButton != null) { addToListButton.setSelected(false); addToListButton.setDisable(true); }
        if (playButton != null) playButton.setDisable(true);
    }

    // showErrorAlert and showInfoAlert methods are removed.
    // findAndSetOwnerWindow is also removed as it was only used by the alert methods.
}