package com.dev.neetfleexui.controllers;

import com.dev.neetfleexui.SessionManager;
import com.dev.neetfleexui.dto.ContentDto;
import com.dev.neetfleexui.dto.ContentType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener; // Ensure this is imported
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D; // Import for Viewport
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors; // For Java 8+ stream filtering

public class MainController {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    // --- Configuration Constants ---
    private static final String API_BASE_URL = "http://localhost:8080";
    private static final String API_ENDPOINT_POPULAR_MOVIES = "/get/Movies";
    private static final String API_ENDPOINT_POPULAR_SERIES = "/get/popularSeries";
    private static final String API_ENDPOINT_PLAYBACK = "/get/playback/";
    private static final String API_ENDPOINT_MY_LIST = "/get/myList/";

    private static final String API_FULL_URL_POPULAR_MOVIES = API_BASE_URL + API_ENDPOINT_POPULAR_MOVIES;
    private static final String API_FULL_URL_POPULAR_SERIES = API_BASE_URL + API_ENDPOINT_POPULAR_SERIES;
    private static final String API_FULL_URL_PLAYBACK_CONTENT = API_BASE_URL + API_ENDPOINT_PLAYBACK + (SessionManager.getUserName() != null ? SessionManager.getUserName() : "defaultUser"); // Handle potential null user
    private static final String API_FULL_URL_MyList_CONTENT = API_BASE_URL + API_ENDPOINT_MY_LIST + SessionManager.getUserName();

    // --- Card Styling Constants ---
    private static final double CARD_WIDTH = 220;
    private static final double CARD_HEIGHT = 330;
    private static final double HBOX_SPACING = 15.0;
    private static final double TARGET_IMAGE_CONTAINER_HEIGHT = CARD_HEIGHT * 0.9; // Image takes 90% height

    // --- FXML Injectable UI Elements (Must remain the same names) ---
    @FXML private HBox popularMoviesContainer;
    @FXML private HBox popularSeriesContainer;
    @FXML private HBox continueWatchingContainer;
    @FXML private HBox myListContainer;
    @FXML private ImageView featuredImage;
    @FXML private Label featuredTitle;
    // Inject the ScrollPanes using the fx:id you added
    @FXML private ScrollPane popularMoviesScrollPane;
    @FXML private ScrollPane popularSeriesScrollPane;
    @FXML private ScrollPane continueWatchingScrollPane;
    @FXML private ScrollPane myListScrollPane;
    // @FXML private HBox myListContainer; // Uncomment if you have this in FXML

    // --- Reusable HTTP Client and ObjectMapper ---
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PageSwitcherController pageSwitcherController = new PageSwitcherController();

    // --- Initialization ---
    @FXML
    public void initialize() {
        LOGGER.info("MainController initializing...");
        configureHBoxSpacing();
        fetchAllContent();
        LOGGER.info("Content fetching initiated in background.");

    }



    /**
     * Configures the spacing and alignment for the HBox containers.
     */
    private void configureHBoxSpacing() {
        if (popularMoviesContainer != null) {
            popularMoviesContainer.setSpacing(HBOX_SPACING);
            popularMoviesContainer.setAlignment(Pos.CENTER_LEFT);
        }
        if (popularSeriesContainer != null) {
            popularSeriesContainer.setSpacing(HBOX_SPACING);
            popularSeriesContainer.setAlignment(Pos.CENTER_LEFT);
        }
        if (continueWatchingContainer != null) {
            continueWatchingContainer.setSpacing(HBOX_SPACING);
            continueWatchingContainer.setAlignment(Pos.CENTER_LEFT);
        }

        if (myListContainer != null) {
            myListContainer.setSpacing(HBOX_SPACING);
            myListContainer.setAlignment(Pos.CENTER_LEFT);
        }
        // Configure other HBoxes if they exist
    }

    // --- Content Fetching Logic ---

    /**
     * Fetches all necessary content sections concurrently and updates the UI upon completion.
     */
    private void fetchAllContent() {
        CompletableFuture<List<ContentDto>> popularMoviesFuture = fetchContentDataAsync(API_FULL_URL_POPULAR_MOVIES, "Popular Movies");
        CompletableFuture<List<ContentDto>> popularSeriesFuture = fetchContentDataAsync(API_FULL_URL_POPULAR_SERIES, "Popular Series");
        CompletableFuture<List<ContentDto>> continueWatchingFuture = fetchContentDataAsync(API_FULL_URL_PLAYBACK_CONTENT, "Continue Watching");
        CompletableFuture<List<ContentDto>> myListFuture = fetchContentDataAsync(API_FULL_URL_MyList_CONTENT, "My List");
        // Add more futures here for other sections like myListContainer

        CompletableFuture.allOf(popularMoviesFuture, popularSeriesFuture, continueWatchingFuture, myListFuture  /*, add more futures here */)
                .thenAcceptAsync(v -> { // Use thenAcceptAsync to run UI updates on FX thread
                    List<ContentDto> popularMovies = popularMoviesFuture.join(); // .join() is safe here after allOf
                    List<ContentDto> popularSeries = popularSeriesFuture.join();
                    List<ContentDto> continueWatching = continueWatchingFuture.join();
                    List<ContentDto> myList = myListFuture.join();
                    // List<ContentDto> myList = myListFuture.join(); // If added

                    // Update UI on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        updateFeaturedSection(popularMovies); // Update featured based on popular movies (or other logic)
                        populateContentSection(popularMoviesContainer, popularMovies, "Popular Movies");
                        populateContentSection(popularSeriesContainer, popularSeries, "Popular Series");
                        populateContentSection(continueWatchingContainer, continueWatching, "Continue Watching");
                         populateContentSection(myListContainer, myList, "My List"); // If added
                        LOGGER.info("All UI sections updated.");
                    });
                }, Platform::runLater) // Ensure UI updates happen on the FX thread
                .exceptionally(ex -> { // Handle exceptions from any of the fetches
                    LOGGER.log(Level.SEVERE, "Failed to fetch one or more content sections.", ex);
                    Platform.runLater(() -> showErrorAlert("Content Fetch Error", "Failed to load content. Please check your connection and try again.\nDetails: " + ex.getMessage()));
                    return null; // Return null for exceptionally
                });
    }

    /**
     * Asynchronously fetches a list of ContentDto from a given API URL,
     * parses the response, and filters out invalid entries.
     *
     * @param apiUrl            The full URL to fetch content from.
     * @param contentDescription A description for logging purposes (e.g., "Popular Movies").
     * @return A CompletableFuture containing the list of valid ContentDto, or an empty list if an error occurs.
     */
    private CompletableFuture<List<ContentDto>> fetchContentDataAsync(String apiUrl, String contentDescription) {
        String bearerToken = SessionManager.getCurrentToken();
        if (bearerToken == null || bearerToken.isBlank()) {
            LOGGER.log(Level.SEVERE, "Authorization token is missing for fetching {0}.", contentDescription);
            return CompletableFuture.failedFuture(new IllegalStateException("Authorization token missing. Cannot fetch " + contentDescription));
        }
        // Specific check for playback URL with null user
        if (apiUrl.contains(API_ENDPOINT_PLAYBACK) && apiUrl.endsWith("/null")) {
            LOGGER.log(Level.SEVERE, "Username is null for fetching {0}. Skipping fetch.", contentDescription);
            return CompletableFuture.completedFuture(Collections.emptyList()); // Return empty list immediately
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .header("Authorization", "Bearer " + bearerToken)
                    .GET()
                    .build();

            LOGGER.log(Level.INFO, "Sending API request for {0} to: {1}", new Object[]{contentDescription, apiUrl});

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> { // Process the response
                        int statusCode = response.statusCode();
                        LOGGER.log(Level.INFO, "{0} API Response Status Code: {1}", new Object[]{contentDescription, statusCode});

                        if (statusCode >= 200 && statusCode < 300) { // Success
                            String responseBody = response.body();
                            if (responseBody == null || responseBody.isBlank() || responseBody.equalsIgnoreCase("null")) {
                                LOGGER.log(Level.WARNING, "{0} API returned successful status code but empty or null body.", contentDescription);
                                return Collections.<ContentDto>emptyList(); // Return empty list for empty body
                            }
                            try {
                                // Parse JSON
                                List<ContentDto> contentList = objectMapper.readValue(responseBody, new TypeReference<List<ContentDto>>() {});

                                // Filter out invalid entries (null objects or objects missing critical data)
                                List<ContentDto> validContentList = contentList.stream()
                                        .filter(content -> content != null && content.getType() != null && content.getImageUrl() != null && content.getTitle() != null)
                                        .collect(Collectors.toList()); // Use collect for wider Java version compatibility

                                if (validContentList.size() < contentList.size()) {
                                    LOGGER.log(Level.WARNING, "Filtered out {0} invalid entries (null or missing type/image/title) from {1} response.",
                                            new Object[]{contentList.size() - validContentList.size(), contentDescription});
                                }

                                LOGGER.log(Level.INFO, "Successfully parsed and validated {0} items for {1}.", new Object[]{validContentList.size(), contentDescription});
                                return validContentList; // Return the filtered list
                            } catch (IOException e) {
                                LOGGER.log(Level.SEVERE, "Failed to parse JSON response for " + contentDescription + " from " + apiUrl, e);
                                throw new RuntimeException("JSON Parsing Error for " + contentDescription, e); // Propagate for exceptionally block
                            }
                        } else { // Handle API errors
                            String errorBody = response.body() != null ? response.body() : "(No response body)";
                            LOGGER.log(Level.SEVERE, "{0} API request failed with status code: {1} and body: {2}", new Object[]{contentDescription, statusCode, errorBody});
                            throw new RuntimeException(contentDescription + " API request failed. Status: " + statusCode); // Propagate for exceptionally block
                        }
                    })
                    .exceptionally(ex -> { // Catch exceptions during the async chain (network, parsing, status errors)
                        LOGGER.log(Level.SEVERE, "Error fetching or processing " + contentDescription + " from " + apiUrl, ex);
                        // Return an empty list to allow other sections to potentially load successfully
                        return Collections.<ContentDto>emptyList();
                    });

        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Invalid API URL syntax for {0}: {1}", new Object[]{contentDescription, apiUrl});
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid API URL for " + contentDescription, e)); // Fail fast
        } catch (Exception e) { // Catch other potential setup errors
            LOGGER.log(Level.SEVERE, "Unexpected error preparing request for {0}: {1}", new Object[]{contentDescription, apiUrl});
            return CompletableFuture.failedFuture(new RuntimeException("Unexpected error preparing request for " + contentDescription, e)); // Fail fast
        }
    }

    // --- UI Update Logic ---

    /**
     * Updates the featured section (image and title) based on the first item
     * in the provided list (assumed to be valid after filtering).
     *
     * @param contentList List of valid content, the first item will be featured.
     */
    private void updateFeaturedSection(List<ContentDto> contentList) {
        if (contentList != null && !contentList.isEmpty()) {
            ContentDto featuredContent = contentList.get(0); // Get first valid item
            featuredTitle.setText(featuredContent.getTitle());

            // Determine appropriate target width/height for the featured image container
            // Use FitWidth/FitHeight if set in FXML, otherwise provide sensible defaults
            double featuredTargetWidth = featuredImage.getFitWidth() > 0 ? featuredImage.getFitWidth() : 600; // Example default width
            double featuredTargetHeight = featuredImage.getFitHeight() > 0 ? featuredImage.getFitHeight() : 338; // Example default height (16:9 ratio)

            // Load featured image using the updated loadImage method with viewport logic
            loadImage(featuredContent.getImageUrl(), featuredImage, "featured image", featuredTargetWidth, featuredTargetHeight);
            LOGGER.info("Featured section updated with: " + featuredContent.getTitle());
        } else {
            featuredTitle.setText("No content available");
            featuredImage.setImage(null); // Clear featured image
            LOGGER.warning("No valid content available to feature.");
        }
    }

    /**
     * Populates a given HBox container with cards created from a list of valid ContentDto.
     * This method MUST be called on the JavaFX Application Thread.
     *
     * @param container          The HBox to populate (e.g., popularMoviesContainer).
     * @param contentList        The list of valid ContentDto objects to display.
     * @param sectionDescription Description for logging (e.g., "Popular Movies").
     */
    private void populateContentSection(HBox container, List<ContentDto> contentList, String sectionDescription) {
        if (container == null) {
            LOGGER.log(Level.SEVERE, "UI container for {0} is null. Cannot populate.", sectionDescription);
            return;
        }
        container.getChildren().clear(); // Clear previous items

        // List is assumed to contain only valid DTOs due to filtering in fetch method
        if (contentList == null || contentList.isEmpty()) {
            LOGGER.log(Level.INFO, "{0} list is null or empty after filtering. Container will be empty.", sectionDescription);
            return;
        }

        LOGGER.info("Populating UI section: " + sectionDescription + " with " + contentList.size() + " items...");

        int index = 0;
        for (ContentDto content : contentList) {
            try {
                VBox contentCard = createContentCard(content); // Create card
                if (contentCard != null) {
                    // --- Entrance Animation Setup ---
                    // Initial state: transparent and slightly pushed down
                    contentCard.setOpacity(0.0);
                    contentCard.setTranslateY(20.0);

                    container.getChildren().add(contentCard);

                    // Create Fade In + Slide Up animation
                    FadeTransition fade = new FadeTransition(Duration.millis(500), contentCard);
                    fade.setFromValue(0.0);
                    fade.setToValue(1.0);

                    TranslateTransition slide = new TranslateTransition(Duration.millis(500), contentCard);
                    slide.setFromY(20.0);
                    slide.setToY(0.0);

                    // Delay based on index to create staggered effect
                    double delay = index * 50; // 50ms delay per item
                    fade.setDelay(Duration.millis(delay));
                    slide.setDelay(Duration.millis(delay));

                    // Play animations
                    fade.play();
                    slide.play();

                    index++;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error creating UI card VBox for content: " + content.getTitle(), e);
            }
        }
        LOGGER.info("Finished populating UI section: " + sectionDescription);
    }

    // --- Card Creation Logic ---

    /**
     * Creates a visually styled card (VBox) for a single ContentDto.
     * Enforces consistent size, applies effects, and sets up click handling.
     * Assumes the input ContentDto is valid (non-null with required fields).
     *
     * @param content The valid ContentDto object.
     * @return A VBox representing the content card.
     */
    private VBox createContentCard(ContentDto content) {
        // --- Create the Card Container (VBox) ---
        VBox card = new VBox(); // VBox contains image container and title
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT); // Enforce consistent card size
        card.setMaxSize(CARD_WIDTH, CARD_HEIGHT); // Prevent growing
        card.setMinSize(CARD_WIDTH, CARD_HEIGHT); // Prevent shrinking
        card.setAlignment(Pos.TOP_CENTER); // Align items to top-center
        card.setStyle("-fx-background-color: transparent;"); // Clear background

        // --- Create Image Container (StackPane) ---
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(CARD_WIDTH, TARGET_IMAGE_CONTAINER_HEIGHT); // Use constant height
        imageContainer.setMinSize(CARD_WIDTH, TARGET_IMAGE_CONTAINER_HEIGHT); // Prevent shrinking

        // --- Configure ImageView ---
        ImageView imageView = new ImageView();
        // Set Fit dimensions - viewport will clip content to this size
        imageView.setFitWidth(CARD_WIDTH);
        imageView.setFitHeight(TARGET_IMAGE_CONTAINER_HEIGHT);
        imageView.setSmooth(true);

        // --- Rounded Corners Clip ---
        Rectangle clip = new Rectangle(CARD_WIDTH, TARGET_IMAGE_CONTAINER_HEIGHT);
        clip.setArcWidth(15); // Adjust roundness
        clip.setArcHeight(15);
        imageView.setClip(clip); // Apply clipping mask

        // --- Load Image with Viewport Logic ---
        loadImage(content.getImageUrl(), imageView, content.getTitle(), CARD_WIDTH, TARGET_IMAGE_CONTAINER_HEIGHT);

        // --- Effects (Shadow and Glow) ---
        DropShadow dropShadow = new DropShadow(20, Color.rgb(0, 0, 0, 0.7));
        Glow glow = new Glow(0.0); // Initial glow level is 0
        imageView.setEffect(dropShadow); // Apply shadow initially

        imageContainer.getChildren().add(imageView);
        StackPane.setAlignment(imageView, Pos.CENTER); // Center image within the container

        // --- Create Title Label ---
        Label titleLabel = new Label(content.getTitle()); // Title is assumed valid
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPrefWidth(CARD_WIDTH);
        titleLabel.setPadding(new Insets(8, 5, 5, 5)); // Padding (top, right, bottom, left)
        titleLabel.setWrapText(true); // Allow wrapping
        // Calculate remaining height for title and set preference/max
        double titleHeight = CARD_HEIGHT - TARGET_IMAGE_CONTAINER_HEIGHT;
        titleLabel.setPrefHeight(titleHeight);
        titleLabel.setMaxHeight(titleHeight);

        // --- Add Components to Card ---
        card.getChildren().addAll(imageContainer, titleLabel); // Image container first, then title

        // --- Interactive Effects ---
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        card.setOnMouseEntered(e -> {
            scaleIn.play();
            // Apply glow on top of existing effect
            glow.setInput(dropShadow); // Ensure glow wraps the shadow
            imageView.setEffect(glow);
            glow.setLevel(0.3); // Make glow visible
            card.setCursor(Cursor.HAND);
        });

        card.setOnMouseExited(e -> {
            scaleOut.play();
            // Restore the effect that was present before glow was added
            imageView.setEffect(dropShadow); // Fallback to just shadow
            glow.setLevel(0.0); // Ensure glow is turned off
            card.setCursor(Cursor.DEFAULT);
        });

        // --- Click Handler ---
        // Pass the valid content object
        card.setOnMouseClicked(event -> handleCardClick(event, content));

        return card;
    }

    // --- Click Handler Logic ---

    /**
     * Handles the click event on a content card. Determines the content type
     * and navigates to the appropriate details page using PageSwitcherController.
     * Assumes input ContentDto is valid.
     *
     * @param event   The MouseEvent from the click.
     * @param content The valid ContentDto associated with the clicked card.
     */
    private void handleCardClick(MouseEvent event, ContentDto content) {
        // Defensive check (should not be needed due to prior filtering)
        if (content == null || content.getType() == null) {
            LOGGER.log(Level.SEVERE, "BUG: handleCardClick called with null content or type despite filtering. Content: {0}", content);
            showErrorAlert("Internal Error", "Could not process click. Invalid content data.");
            return;
        }

        LOGGER.info("Clicked on " + content.getType() + ": " + content.getTitle());
        ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());

        try {
            switch (content.getType()) {
                case MOVIE:
                    // Assuming PageSwitcherController.switchToMovieDetails accepts ContentDto
                    pageSwitcherController.switchToMovieDetails(actionEvent, content);
                    break;
                case SERIES:
                    // Assuming PageSwitcherController.switchToSeriesDetails accepts ContentDto
                    pageSwitcherController.switchToSeriesDetails(actionEvent, content);
                    break;
                default:
                    // Should not happen if ContentType enum is exhaustive and API is consistent
                    LOGGER.warning("Unhandled content type clicked: " + content.getType());
                    showInfoAlert("Navigation", "Cannot navigate to details for this content type: " + content.getType());
                    break;
            }
        } catch (Exception e) { // Catch any other unexpected errors during navigation
            LOGGER.log(Level.SEVERE, "Unexpected error during page switch for " + content.getTitle(), e);
            showErrorAlert("Unexpected Error", "An error occurred while trying to navigate: " + e.getMessage());
        }
    }

    // --- Helper Methods ---

    /**
     * Loads an image asynchronously from a URL or local path into an ImageView.
     * Applies a calculated viewport after loading to make the image "cover"
     * the specified target dimensions, cropping if necessary.
     *
     * @param imageUrl        URL or local file path of the image.
     * @param imageView       The ImageView to display the image in.
     * @param imageDescription A description for logging purposes.
     * @param targetWidth     The desired width of the visible image area (in the ImageView).
     * @param targetHeight    The desired height of the visible image area (in the ImageView).
     */
    private void loadImage(String imageUrl, ImageView imageView, String imageDescription, double targetWidth, double targetHeight) {
        // Basic validation
        if (imageUrl == null || imageUrl.isBlank()) {
            LOGGER.log(Level.WARNING, "No image URL/Path provided for {0}. Clearing image.", imageDescription);
            imageView.setImage(null);
            imageView.setViewport(null);
            return;
        }
        if (targetWidth <= 0 || targetHeight <= 0) {
            LOGGER.log(Level.WARNING, "Invalid target dimensions ({0}x{1}) for {2}. Cannot apply viewport.", new Object[]{targetWidth, targetHeight, imageDescription});
            loadImage(imageUrl, imageView, imageDescription); // Fallback to basic loading without viewport
            return;
        }

        // Determine if it's a URL or local path and create the final URI string
        String potentialPath = imageUrl.trim().replace("\"", "");
        String sourceUri;
        try {
            if (potentialPath.startsWith("http://") || potentialPath.startsWith("https://")) {
                sourceUri = potentialPath; // It's already a URL
                LOGGER.log(Level.FINE, "Attempting to load {0} from URL: {1}", new Object[]{imageDescription, sourceUri});
            } else {
                // Assume local path, convert to file URI
                String correctedPath = potentialPath.replace("\\", "/");
                sourceUri = new java.io.File(correctedPath).toURI().toString();
                LOGGER.log(Level.FINE, "Attempting to load {0} from file URI: {1}", new Object[]{imageDescription, sourceUri});
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating image source URI for {0} from path: {1}", new Object[]{imageDescription, potentialPath, e});
            imageView.setImage(null);
            imageView.setViewport(null);
            return;
        }

        // Load the image asynchronously
        try {
            Image image = new Image(sourceUri, true); // true -> background loading

            // Use an array holder to store the listener reference so it can be removed inside the lambda
            final ChangeListener<Number>[] listenerHolder = new ChangeListener[1];

            // Define the listener that applies the viewport once dimensions are known
            ChangeListener<Number> viewportUpdater = (observable, oldValue, newValue) -> {
                // Check if image is fully loaded (width and height are positive)
                if (image.getWidth() > 0 && image.getHeight() > 0) {
                    // Ensure viewport calculation/application happens on the FX thread
                    Platform.runLater(() -> applyViewport(imageView, image, targetWidth, targetHeight));

                    // Remove the listener from both properties to prevent multiple calls
                    if (listenerHolder[0] != null) {
                        image.widthProperty().removeListener(listenerHolder[0]);
                        image.heightProperty().removeListener(listenerHolder[0]);
                        LOGGER.finest("Viewport listener removed for " + imageDescription);
                    }
                }
            };
            listenerHolder[0] = viewportUpdater; // Store the listener instance

            // Add the listener to be notified when width/height properties change (i.e., image loads)
            image.widthProperty().addListener(viewportUpdater);
            image.heightProperty().addListener(viewportUpdater);

            // Add an error listener
            image.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    LOGGER.log(Level.WARNING, "Failed to load {0} from: {1}. Error: {2}",
                            new Object[]{imageDescription, sourceUri, image.getException() != null ? image.getException().getMessage() : "Unknown error"});
                    // Clear image and viewport on the FX thread
                    Platform.runLater(() -> {
                        imageView.setImage(null);
                        imageView.setViewport(null);
                    });
                    // Remove dimension listeners if an error occurs before loading completes
                    if (listenerHolder[0] != null) {
                        image.widthProperty().removeListener(listenerHolder[0]);
                        image.heightProperty().removeListener(listenerHolder[0]);
                        LOGGER.finest("Viewport listener removed due to error for " + imageDescription);
                    }
                }
                // No 'else' needed here; success logging happens via the viewportUpdater
            });

            // Set the image on the ImageView immediately.
            // It might be blank initially, the listeners will update it.
            imageView.setImage(image);

        } catch (IllegalArgumentException e) { // Catch errors from new Image(uri) if URI is invalid
            LOGGER.log(Level.WARNING, "Invalid image source syntax for {0}: {1}", new Object[]{imageDescription, sourceUri});
            imageView.setImage(null);
            imageView.setViewport(null);
        } catch (Exception e) { // Catch unexpected errors during image loading setup
            LOGGER.log(Level.SEVERE, "Unexpected error loading {0} from: {1}", new Object[]{imageDescription, sourceUri, e});
            imageView.setImage(null);
            imageView.setViewport(null);
        }
    }

    /**
     * Calculates and applies a viewport Rectangle2D to an ImageView to make the associated Image
     * effectively "cover" the target width and height, centering the crop.
     * MUST be called on the JavaFX Application thread.
     *
     * @param imageView     The ImageView to apply the viewport to.
     * @param image         The fully loaded Image object.
     * @param targetWidth   The desired width of the visible area.
     * @param targetHeight  The desired height of the visible area.
     */
    private void applyViewport(ImageView imageView, Image image, double targetWidth, double targetHeight) {
        // Pre-condition checks
        if (imageView == null || image == null || image.getWidth() <= 0 || image.getHeight() <= 0 || targetWidth <= 0 || targetHeight <= 0) {
            LOGGER.finest("Skipping viewport application due to null or invalid dimensions.");
            if(imageView != null) imageView.setViewport(null); // Ensure viewport is cleared
            return;
        }

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        // Calculate aspect ratios
        double targetRatio = targetWidth / targetHeight;
        double imageRatio = imageWidth / imageHeight;

        double scaleFactor; // How much to scale the original image
        double scaledWidth; // Width of the scaled image
        double scaledHeight; // Height of the scaled image

        // Determine how to scale to cover the target area
        if (imageRatio >= targetRatio) {
            // Image is wider than or equal aspect ratio to target -> Scale based on height
            scaleFactor = targetHeight / imageHeight;
            scaledHeight = targetHeight;
            scaledWidth = imageWidth * scaleFactor; // Scaled width might be >= targetWidth
        } else {
            // Image is taller than target -> Scale based on width
            scaleFactor = targetWidth / imageWidth;
            scaledWidth = targetWidth;
            scaledHeight = imageHeight * scaleFactor; // Scaled height will be >= targetHeight
        }

        // Calculate the top-left corner of the viewport rectangle WITHIN the scaled image
        // This calculation centers the target area within the potentially larger scaled image
        double viewPortX_Scaled = (scaledWidth - targetWidth) / 2.0;
        double viewPortY_Scaled = (scaledHeight - targetHeight) / 2.0;

        // Convert the viewport origin and dimensions from the scaled image coordinate system
        // back to the original image's coordinate system, as required by Rectangle2D
        double originalViewPortX = Math.max(0, viewPortX_Scaled / scaleFactor); // Ensure non-negative
        double originalViewPortY = Math.max(0, viewPortY_Scaled / scaleFactor); // Ensure non-negative
        double originalViewPortWidth = targetWidth / scaleFactor;
        double originalViewPortHeight = targetHeight / scaleFactor;

        // Clip viewport dimensions to ensure they don't exceed the original image boundaries
        // (can happen due to floating point inaccuracies)
        originalViewPortWidth = Math.min(originalViewPortWidth, imageWidth - originalViewPortX);
        originalViewPortHeight = Math.min(originalViewPortHeight, imageHeight - originalViewPortY);

        // Create the viewport
        Rectangle2D viewport = new Rectangle2D(originalViewPortX, originalViewPortY, originalViewPortWidth, originalViewPortHeight);

        LOGGER.finest(String.format("Applying Viewport to %s: Img(%.0fx%.0f) Target(%.0fx%.0f) VP(x:%.1f, y:%.1f, w:%.1f, h:%.1f)",
                imageView.getImage().getUrl().substring(imageView.getImage().getUrl().lastIndexOf('/') + 1), // Log filename
                imageWidth, imageHeight, targetWidth, targetHeight,
                originalViewPortX, originalViewPortY, originalViewPortWidth, originalViewPortHeight));

        // Apply the viewport and ensure preserveRatio is false for 'cover' effect
        imageView.setViewport(viewport);
        imageView.setPreserveRatio(false);
    }

    /**
     * Basic image loading without viewport logic (overload for fallback).
     * Uses preserveRatio = true.
     */
    private void loadImage(String imageUrl, ImageView imageView, String imageDescription) {
        if (imageUrl == null || imageUrl.isBlank()) { imageView.setImage(null); return; }
        try {
            String path = imageUrl.trim().replace("\"", "");
            String uri = (path.startsWith("http://") || path.startsWith("https://")) ? path : new java.io.File(path.replace("\\", "/")).toURI().toString();
            Image image = new Image(uri, true); // Background load
            image.errorProperty().addListener((obs, ov, nv) -> { // Basic error logging
                if(nv) LOGGER.log(Level.WARNING, "Failed basic load for {0}: {1}", new Object[]{imageDescription, image.getException() != null ? image.getException().getMessage() : "Unknown"});
            });
            imageView.setImage(image);
            imageView.setViewport(null); // Ensure no old viewport is active
            imageView.setPreserveRatio(true); // Use preserveRatio for simple fitting
        } catch (Exception e){
            LOGGER.log(Level.SEVERE, "Error basic loading image {0}: {1}", new Object[]{imageDescription, e.getMessage()});
            imageView.setImage(null); // Clear on error
        }
    }

    /**
     * Shows an error alert dialog. Ensures it runs on the JavaFX Application Thread.
     *
     * @param title   The title of the alert window.
     * @param message The error message content.
     */
    private void showErrorAlert(String title, String message) {
        Runnable alertTask = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            // Attempt to find an owner window for modality
            Node node = popularMoviesContainer != null ? popularMoviesContainer : (popularSeriesContainer != null ? popularSeriesContainer : featuredImage);
            if(node != null && node.getScene() != null && node.getScene().getWindow() != null) {
                alert.initOwner(node.getScene().getWindow());
            }
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            alertTask.run();
        } else {
            Platform.runLater(alertTask);
        }
    }

    /**
     * Shows an informational alert dialog. Ensures it runs on the JavaFX Application Thread.
     *
     * @param title   The title of the alert window.
     * @param message The informational message content.
     */
    private void showInfoAlert(String title, String message) {
        Runnable alertTask = () -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            Node node = popularMoviesContainer != null ? popularMoviesContainer : (popularSeriesContainer != null ? popularSeriesContainer : featuredImage);
            if(node != null && node.getScene() != null && node.getScene().getWindow() != null) {
                alert.initOwner(node.getScene().getWindow());
            }
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            alertTask.run();
        } else {
            Platform.runLater(alertTask);
        }
    }

    public void logout(MouseEvent event) {

        pageSwitcherController.switchToSignIn(event);
        SessionManager.clearSession();
    }
} // End of MainController class