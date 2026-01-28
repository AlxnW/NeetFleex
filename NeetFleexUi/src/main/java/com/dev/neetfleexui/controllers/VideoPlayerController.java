package com.dev.neetfleexui.controllers; // Make sure this matches your package structure

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat; // BGRA format

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VideoPlayerController {

  @FXML  public Button closeButton;
    // --- FXML Fields ---
    @FXML private BorderPane videoPlayerRoot;
    @FXML private StackPane videoContainer;
    @FXML private ImageView videoSurfaceView; // Target for VLCJ rendering
    @FXML private BorderPane overlayPane;
    @FXML private VBox controlsContainer;
    @FXML private HBox controlBar;
    @FXML private Button playPauseButton;
    @FXML private Button stopButton;
    @FXML private Button muteButton;
    @FXML private Button fullScreenButton;
    @FXML private Label timeLabel;
    @FXML private Slider volumeSlider;
    @FXML private ProgressBar progressBar;
    @FXML private ComboBox<String> qualityComboBox;
    @FXML private Label titleLabel;
    PageSwitcherController pageSwitcherController = new PageSwitcherController();






    // --- VLCJ Media Player ---
    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer embeddedMediaPlayer;
    private WritableImage videoWritableImage;
    // Using BGRA format, common for VLC callbacks on many platforms.
    private final WritablePixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraPreInstance();
    private final Lock renderLock = new ReentrantLock(); // Protects WritableImage access during pixel writing

    // --- State Variables ---
    private volatile boolean isPlaying = false; // Use volatile for visibility across threads
    private boolean isMuted = false;
    private volatile boolean isSeeking = false; // Use volatile
    private volatile long currentMediaLength = -1; // Use volatile

    // --- Overlay Visibility ---
    private ParallelTransition fadeInTransition;
    private ParallelTransition fadeOutTransition;
    private PauseTransition hideDelay;
    private boolean controlsVisible = false;
    private static final Duration FADE_DURATION = Duration.millis(300);
    private static final Duration HIDE_DELAY_DURATION = Duration.seconds(3);
    private static final double SLIDE_OFFSET = 30.0; // Pixels to slide up/down

    // --- Fullscreen ---
    private Stage stage;
    private ChangeListener<Boolean> fullScreenListener;
    // Flag to prevent listener reacting to programmatic changes
    private final AtomicBoolean isProgrammaticallyChangingFullscreen = new AtomicBoolean(false);

    // --- Formatting ---
    private final DecimalFormat volumeDecimalFormat = new DecimalFormat("#.##");


    // ========================================================================
    // Initialization and Setup
    // ========================================================================

    @FXML
    public void initialize() {
        // Defensive programming: Ensure essential components are injected
        if (videoSurfaceView == null || videoContainer == null || overlayPane == null || videoPlayerRoot == null) {
            System.err.println("CRITICAL FXML INJECTION ERROR: Core UI components are null! Cannot initialize player.");
            // Optionally disable the entire component or show an error message visually
            if (videoPlayerRoot != null) videoPlayerRoot.setDisable(true);
            return;
        }




//       closeButton.set(event -> {
//           try {
//               disposePlayer();
//
//               handleCloseAction(event);
//           } catch (IOException e) {
//               throw new RuntimeException(e);
//           }
//       });

        setupUIApplication();
        setupUIApplication();
        setupMediaControls();
        setupQualityOptions();
        setupOverlayVisibility();
        bindVolumeSliderStyle();

        // Initial State
        overlayPane.setOpacity(0.0);
        overlayPane.setMouseTransparent(true); // Don't intercept mouse events when invisible
        overlayPane.setPickOnBounds(false);   // Only interact with visible children
        setControlsDisabled(true); // Start disabled until media loads

        // Acquire Stage after the scene is potentially available
        Platform.runLater(this::findAndSetupStage);

        // Initialize VLCJ
        if (!initializeVlcj()) {
            // Initialization failed, UI should reflect this (handled in initializeVlcj)
            return;
        }

        // Add resize listeners (using runLater to ensure stage/scene might be ready)
        Platform.runLater(() -> {
            if (videoPlayerRoot.getScene() != null) {
                videoPlayerRoot.getScene().widthProperty().addListener((obs, oldVal, newVal) -> handleResize());
                videoPlayerRoot.getScene().heightProperty().addListener((obs, oldVal, newVal) -> handleResize());
            } else {
                videoPlayerRoot.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
                    if(newScene != null) {
                        newScene.widthProperty().addListener((obs, oldVal, newVal) -> handleResize());
                        newScene.heightProperty().addListener((obs, oldVal, newVal) -> handleResize());
                    }
                });
            }
        });
    }

    private void handleCloseAction(MouseEvent event) throws IOException {
        pageSwitcherController.switchToMainPage(event);

    }

    private void setupUIApplication() {
        // Set minimum sizes to prevent excessive shrinking
        if (controlBar != null) controlBar.setMinHeight(40);
        if (controlsContainer != null) controlsContainer.setMinHeight(70);
        if (videoContainer != null) {
            videoContainer.setMinWidth(240);
            videoContainer.setMinHeight(180);
        }

        // *** CRITICAL: Bind ImageView size to its container ***
        // This ensures the rendering surface matches the display area.
        videoSurfaceView.fitWidthProperty().bind(videoContainer.widthProperty());
        videoSurfaceView.fitHeightProperty().bind(videoContainer.heightProperty());
        videoSurfaceView.setPreserveRatio(true); // Maintain video aspect ratio
        videoSurfaceView.setSmooth(false); // Use false for potentially sharper video, true for smoother scaling
    }


        private boolean initializeVlcj() {
        try {
            // --- Optional VLC Arguments ---
            // Add arguments for debugging or specific configurations if needed.
            // *** SUGGESTED CHANGES START ***
            String[] vlcArgs = {
                    // Increase log verbosity to get more detailed errors from VLC itself.
                    // Check your console/logs for much more detailed VLC output.
                    "--verbose=2",

                    // Explicitly disable hardware acceleration. This is a common
                    // fix when video conversion/display fails, as hardware drivers
                    // can sometimes conflict with custom rendering paths like callbacks.
                    // Try "--avcodec-hw=none" first. If that doesn't work, you could
                    // try specific decoders like "--avcodec-hw=dxva2", "--avcodec-hw=d3d11va",
                    // "--avcodec-hw=vdpau", "--avcodec-hw=vaapi", "--avcodec-hw=videotoolbox"
                    // depending on your OS and hardware, but 'none' is the safest bet
                    // for compatibility, albeit potentially slower.
                    "--avcodec-hw=none",

                    // Optional: Sometimes helps with subtitle rendering issues or overlays,
                    // might indirectly affect the filter chain.
                    // "--spu-chroma=RV32",

                    // Optional: You could try forcing a specific video output module
                    // before the callback takes over, though usually not necessary.
                    // "--vout=dummy", // As a test - video won't display but might stop errors
                    // If the above fails, remove this line.

                    // Hide the default title shown briefly by some VLC versions
                    "--no-video-title-show"
            };
            // *** SUGGESTED CHANGES END ***

            System.out.println("Initializing VLCJ with args: " + String.join(" ", vlcArgs)); // Log arguments used
            mediaPlayerFactory = new MediaPlayerFactory(vlcArgs);

            embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
            setupVlcjListeners(); // Attach listeners BEFORE setting video surface

            // Set up the video surface for rendering onto the ImageView.
            // The CallbackVideoSurface constructor now takes the BufferFormatCallback,
            // RenderCallback, boolean forceFullScreen redraw (usually true), and adapter.
            embeddedMediaPlayer.videoSurface().set(new JavaFxCallbackVideoSurface()); // This line looks correct

            // If we reach here without an exception, initialization was successful.
            System.out.println("VLCJ Initialized and Video Surface Attached.");
            return true;

        } catch (UnsatisfiedLinkError ule) {
            // CRITICAL: Native libraries not found or incompatible
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!! FAILED TO FIND/LOAD NATIVE VLC LIBRARIES (VLCJ) !!!");
            System.err.println("!!! " + ule.getMessage());
            System.err.println("!!! Make sure VLC is installed (64-bit JRE needs 64-bit VLC).");
            System.err.println("!!! Ensure VLC install dir is in PATH or use -Djna.library.path=...");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            handleMediaError("VLC Native Library Error", ule, true);
            return false;
        } catch (NoClassDefFoundError ncdfe) { // Catching this explicitly can help diagnose classpath issues
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!! CLASS NOT FOUND ERROR (VLCJ / JNA related?)   !!!");
            System.err.println("!!! " + ncdfe.getMessage());
            System.err.println("!!! Check your project dependencies (vlcj, vlcj-natives, jna).");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            handleMediaError("VLCJ Classpath/Dependency Error", ncdfe, true);
            return false;
        }
        catch (Exception e) {
            // Other potential initialization or surface setting errors
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!! UNEXPECTED ERROR INITIALIZING VLCJ/SETTING SURFACE !!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            handleMediaError("VLCJ Initialization/Surface Error", e, true);
            return false;
        }
    }

    private void findAndSetupStage() {
        // Attempt to find the stage associated with the root pane
        if (videoPlayerRoot.getScene() != null && videoPlayerRoot.getScene().getWindow() instanceof Stage) {
            setupStage((Stage) videoPlayerRoot.getScene().getWindow());
        } else {
            // Listen for the scene/window to become available if not ready immediately
            videoPlayerRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                        if (newWindow instanceof Stage) {
                            setupStage((Stage) newWindow);
                        }
                    });
                    // Handle case where window is already set when scene listener fires
                    if(newScene.getWindow() instanceof Stage) {
                        setupStage((Stage) newScene.getWindow());
                    }
                } else {
                    // Scene detached, remove listener from old stage if necessary
                    removeFullScreenListener();
                    this.stage = null;
                }
            });
        }
    }

    // Helper to setup stage specifics once available
    private void setupStage(Stage stage) {
        if (this.stage == stage) return; // Already setup for this stage

        removeFullScreenListener(); // Remove from previous stage if any
        this.stage = stage;
        System.out.println("Stage acquired: " + this.stage);
        addFullScreenListener(); // Add to the new stage
        // Optionally add stage resize listeners if root pane listeners aren't sufficient
        // stage.widthProperty().addListener((o, old, nw) -> handleResize());
        // stage.heightProperty().addListener((o, old, nh) -> handleResize());
    }


    private void setupMediaControls() {
        // Attach actions to buttons
        if (playPauseButton != null) playPauseButton.setOnAction(event -> togglePlayPause());
        if (stopButton != null) stopButton.setOnAction(event -> stopPlayback());
        if (muteButton != null) muteButton.setOnAction(event -> toggleMute());
        if (fullScreenButton != null) fullScreenButton.setOnAction(event -> toggleFullScreen());

        // Volume Slider
        if (volumeSlider != null) {
            volumeSlider.setMin(0.0);
            volumeSlider.setMax(1.0); // Represent volume as 0.0 to 1.0
            volumeSlider.setValue(0.7); // Default volume
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (embeddedMediaPlayer != null) {
                    // VLCJ uses 0-200 range for volume
                    int vlcVolume = (int) Math.round(newVal.doubleValue() * 200);
                    embeddedMediaPlayer.audio().setVolume(vlcVolume);
                    // If user raises volume, assume they want to unmute
                    if (isMuted && vlcVolume > 0) {
                        setMute(false);
                    }
                }
            });
            // Handle mouse interactions for overlay timer reset
            volumeSlider.setOnMousePressed(event -> handleInteractionStart());
            volumeSlider.setOnMouseReleased(event -> handleInteractionEnd());
            volumeSlider.setOnDragDetected(event -> handleInteractionStart());
            volumeSlider.setOnMouseDragged(event -> { if(hideDelay!=null) hideDelay.stop(); }); // Keep showing while dragging
        }

        // Progress Bar (Seeking)
        if (progressBar != null) {
            progressBar.setProgress(0.0);
            progressBar.setOnMousePressed(event -> {
                isSeeking = true;
                handleInteractionStart();
                seekToPosition(event); // Seek on initial press
            });
            progressBar.setOnMouseDragged(event -> {
                if (isSeeking) {
                    if(hideDelay!=null) hideDelay.stop(); // Keep showing while dragging
                    seekToPosition(event);
                }
            });
            progressBar.setOnMouseReleased(event -> {
                if (isSeeking) {
                    // seekToPosition(event); // Seek to final release position (already done by drag)
                    handleInteractionEnd(); // Reset overlay timer
                    // Delay setting isSeeking to false slightly to allow final time update from player
                    PauseTransition delay = new PauseTransition(Duration.millis(150));
                    delay.setOnFinished(e -> isSeeking = false);
                    delay.play();
                }
            });
        }
    }

    private void setupQualityOptions() {
        // Placeholder - VLCJ quality switching often requires reloading media with different options.
        if (qualityComboBox == null) return;
        qualityComboBox.getItems().addAll("Auto"); // Add options if implemented
        qualityComboBox.setValue("Auto");
        qualityComboBox.setDisable(true); // Disable until properly implemented
        qualityComboBox.setOnAction(event -> {
            System.out.println("Quality selection changed: " + qualityComboBox.getValue() + " (Not Implemented)");
            handleInteractionStart();
            handleInteractionEnd();
        });
    }

    // ========================================================================
    // Media Loading and Playback (VLCJ)
    // ========================================================================

    public void loadMedia(String mediaPath, String title) {
        if (embeddedMediaPlayer == null) {
            handleMediaError("Cannot load media: Player not initialized.", null, true);
            return;
        }
        if (mediaPath == null || mediaPath.isEmpty()) {
            handleMediaError("Cannot load media: Invalid media path.", null, false);
            return;
        }

        System.out.println("VLCJ: Attempting to load media: " + mediaPath);
        Platform.runLater(() -> {
            if (titleLabel != null) titleLabel.setText(title != null ? title : "Loading...");
            setControlsDisabled(true);
            updateProgressAndTime(0, -1); // Reset UI
            // Clear previous image
            if(videoSurfaceView != null) videoSurfaceView.setImage(null);
            videoWritableImage = null; // Allow old image to be GC'd
        });

        // Stop previous playback if necessary
        if (embeddedMediaPlayer.status().isPlaying() || embeddedMediaPlayer.status().isPlaying()) {
            embeddedMediaPlayer.controls().stop(); // Ensure clean state before loading new media
        }

        // Play the new media. Use playMedia for simplicity.
        // Consider prepareMedia + play for potential pre-buffering feel.
        boolean success = embeddedMediaPlayer.media().play(mediaPath);

        if (!success) {
            handleMediaError("VLCJ failed to start media playback for: " + mediaPath, null, false);
        } else {
            System.out.println("VLCJ: Play command issued for: " + mediaPath);
            // Initial volume/mute state will be set by the 'playing' event handler for robustness
        }
    }

    // ========================================================================
    // VLCJ Player Event Listeners
    // ========================================================================

    private void setupVlcjListeners() {
        if (embeddedMediaPlayer == null) return;

        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {
                System.out.println("VLCJ Event: mediaPlayerReady");
                // Called when the media player component itself is ready, before media is necessarily loaded/parsed.
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                System.out.println("VLCJ Event: playing");
                isPlaying = true;
                currentMediaLength = mediaPlayer.status().length();
                Platform.runLater(() -> {
                    setControlsDisabled(false); // Enable controls now
                    updatePlayPauseButtonState();
                    updateVolumeFromSlider(); // Ensure volume is set correctly
                    applyMuteState();        // Ensure mute state is set correctly
                    updateMuteButtonState();
                    if (titleLabel != null && (titleLabel.getText() == null || titleLabel.getText().equals("Loading..."))) {
                        // Try to get title from media if not provided
                        String mediaTitle = mediaPlayer.media().meta().get(uk.co.caprica.vlcj.media.Meta.TITLE);
                        titleLabel.setText(mediaTitle != null ? mediaTitle : "Playing");
                    }
                    resetHideTimer(); // Start timer to hide controls
                });
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                System.out.println("VLCJ Event: paused");
                isPlaying = false;
                Platform.runLater(() -> {
                    setControlsDisabled(false); // Keep controls enabled
                    updatePlayPauseButtonState();
                    stopHideTimer(); // Stop the hide timer
                    showControls(); // Ensure controls are visible when paused
                });
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                System.out.println("VLCJ Event: stopped");
                isPlaying = false;
                currentMediaLength = -1; // Reset length
                Platform.runLater(() -> {
                    updatePlayPauseButtonState();
                    updateProgressAndTime(0, 0); // Reset progress to beginning
                    stopHideTimer();
                    showControls(); // Keep controls visible
                    // Decide if controls should remain enabled or be disabled on stop
                    setControlsDisabled(false); // Let's keep them enabled to allow replay/load
                    if (titleLabel != null) titleLabel.setText(""); // Clear title
                    // Clear the last frame? Optional.
                    // if (videoSurfaceView != null) videoSurfaceView.setImage(null);
                    // videoWritableImage = null;
                });
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                System.out.println("VLCJ Event: finished");
                // Treat 'finished' similar to 'stopped' for UI purposes
                stopped(mediaPlayer);
                // Optionally, could implement auto-replay or load next item here.
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                // Update progress bar and time label ONLY if not currently seeking
                if (!isSeeking) {
                    Platform.runLater(() -> updateProgressAndTime(newTime, currentMediaLength));
                }
            }

            @Override
            public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
                System.out.println("VLCJ Event: lengthChanged -> " + newLength + " ms");
                currentMediaLength = newLength;
                // Update UI immediately with the new length
                Platform.runLater(() -> updateProgressAndTime(mediaPlayer.status().time(), newLength));
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                // An error occurred within the native VLC player instance
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.err.println("!!! Native VLC Player Error      !!!");
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                // It's hard to get specific details here without parsing VLC logs
                handleMediaError("Native VLC player error occurred.", new RuntimeException("VLC internal error"), false);
            }

            @Override
            public void buffering(MediaPlayer mediaPlayer, float newCache) {
                // Can be used to show a buffering indicator
                // System.out.println("VLCJ Event: buffering -> " + newCache + "%");
                Platform.runLater(() -> {
                    if (newCache >= 100.0f) {
                        // Buffering complete - hide indicator if shown
                        if (titleLabel != null && titleLabel.getText().contains("Buffering")) {
                            titleLabel.setText(mediaPlayer.media().meta().get(uk.co.caprica.vlcj.media.Meta.TITLE));
                        }
                    } else {
                        // Show buffering state
                        if (titleLabel != null && !titleLabel.getText().contains("Buffering")) {
                            //titleLabel.setText("Buffering... " + (int)newCache + "%");
                        }
                        // Maybe show a spinner/indicator node
                    }
                });
            }

            // Handle video output changes - might be needed if resolution changes mid-stream
            @Override
            public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
                System.out.println("VLCJ Event: videoOutput count -> " + newCount);
                // If newCount > 0, video is likely ready or starting.
                // If newCount == 0, video output stopped.
            }
        });
    }

    // ========================================================================
    // UI Control Actions (Interacting with VLCJ)
    // ========================================================================

    private void togglePlayPause() {
        if (embeddedMediaPlayer == null) return;
        handleInteractionStart(); // Show controls, stop hide timer
        if (embeddedMediaPlayer.status().isPlaying()) {
            embeddedMediaPlayer.controls().pause();
        } else {
            // If stopped or finished, might need to replay from start or resume
            if (embeddedMediaPlayer.status().isSeekable()) {
                embeddedMediaPlayer.controls().play();
            } else {
                // If not seekable (e.g., error state or truly stopped), reload?
                // For simplicity, just try playing. The state machine should handle it.
                embeddedMediaPlayer.controls().play();
            }
        }
        // State updates (isPlaying, button icon) are handled by the event listeners
        handleInteractionEnd(); // Restart hide timer
    }

    private void stopPlayback() {
        if (embeddedMediaPlayer != null && (embeddedMediaPlayer.status().isPlaying() || embeddedMediaPlayer.status().isPlayable())) {
            handleInteractionStart();
            embeddedMediaPlayer.controls().stop();
            // State updates handled by listener
            handleInteractionEnd();
        }
    }

    private void toggleMute() {
        if (embeddedMediaPlayer == null) return;
        handleInteractionStart();
        setMute(!isMuted); // Use helper method
        handleInteractionEnd();
    }

    // Helper for setting mute state
    private void setMute(boolean mute) {
        if (embeddedMediaPlayer == null) return;
        isMuted = mute;
        applyMuteState(); // Apply to player
        updateMuteButtonState(); // Update UI
    }

    // Apply internal mute state to the player
    private void applyMuteState() {
        if (embeddedMediaPlayer != null) {
            embeddedMediaPlayer.audio().setMute(isMuted);
        }
    }

    // Update volume based on slider value
    private void updateVolumeFromSlider() {
        if (volumeSlider != null && embeddedMediaPlayer != null) {
            int vlcVolume = (int) Math.round(volumeSlider.getValue() * 200);
            embeddedMediaPlayer.audio().setVolume(vlcVolume);
        }
    }

    private void seekToPosition(MouseEvent event) {
        if (progressBar == null || embeddedMediaPlayer == null || currentMediaLength <= 0 || !embeddedMediaPlayer.status().isSeekable()) {
            if (embeddedMediaPlayer != null && !embeddedMediaPlayer.status().isSeekable()) {
                System.out.println("Seek ignored: Media not currently seekable.");
            }
            isSeeking = false; // Ensure seeking flag is reset if we can't seek
            return;
        }

        double totalWidth = progressBar.getWidth();
        if (totalWidth <= 0) return; // Avoid division by zero

        double mouseX = event.getX();
        // Clamp mouseX to be within the bounds of the progress bar
        mouseX = Math.max(0, Math.min(totalWidth, mouseX));

        float seekRatio = (float) (mouseX / totalWidth);
        seekRatio = Math.max(0.0f, Math.min(1.0f, seekRatio)); // Ensure ratio is [0.0, 1.0]

        // Update UI immediately for responsiveness while dragging
        progressBar.setProgress(seekRatio);
        long seekTimeMs = (long) (seekRatio * currentMediaLength);
        updateTimeLabel(seekTimeMs, currentMediaLength);

        // Tell VLCJ to seek
        // System.out.println("Seeking to position: " + seekRatio); // Debug
        embeddedMediaPlayer.controls().setPosition(seekRatio);
    }

    // ========================================================================
    // UI State Updates
    // ========================================================================

    private void updatePlayPauseButtonState() {
        Platform.runLater(() -> {
            if (playPauseButton == null) return;
            playPauseButton.getStyleClass().removeAll("play-button", "pause-button");
            if (isPlaying) {
                playPauseButton.getStyleClass().add("pause-button");
                playPauseButton.setTooltip(new Tooltip("Pause (Space)"));
            } else {
                playPauseButton.getStyleClass().add("play-button");
                playPauseButton.setTooltip(new Tooltip("Play (Space)"));
            }
            // Ensure base style is present
            if (!playPauseButton.getStyleClass().contains("control-button")) {
                playPauseButton.getStyleClass().add("control-button");
            }
            playPauseButton.applyCss(); // Force CSS update if needed immediately
        });
    }

    private void updateMuteButtonState() {
        Platform.runLater(() -> {
            if (muteButton == null) return;
            muteButton.getStyleClass().removeAll("volume-button", "mute-button");
            if (isMuted) {
                muteButton.getStyleClass().add("mute-button");
                muteButton.setTooltip(new Tooltip("Unmute (M)"));
            } else {
                muteButton.getStyleClass().add("volume-button");
                muteButton.setTooltip(new Tooltip("Mute (M)"));
            }
            // Ensure base style is present
            if (!muteButton.getStyleClass().contains("control-button")) {
                muteButton.getStyleClass().add("control-button");
            }
            muteButton.applyCss();
        });
    }

    private void updateProgressAndTime(long currentTimeMs, long totalTimeMs) {
        // Ensure this runs on the JFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> updateProgressAndTime(currentTimeMs, totalTimeMs));
            return;
        }

        if (progressBar == null || timeLabel == null) return;

        if (totalTimeMs <= 0) { // Media length not known or invalid
            progressBar.setProgress(0.0);
            timeLabel.setText("--:-- / --:--");
        } else {
            // Avoid updating progress bar if user is actively seeking
            if (!isSeeking) {
                double progress = (totalTimeMs > 0) ? (double) currentTimeMs / totalTimeMs : 0.0;
                // Clamp progress between 0.0 and 1.0
                progress = Math.max(0.0, Math.min(1.0, progress));
                progressBar.setProgress(progress);
            }
            // Always update the time label
            updateTimeLabel(currentTimeMs, totalTimeMs);
        }
    }

    private void updateTimeLabel(long currentTimeMs, long totalTimeMs) {
        // Ensure this runs on the JFX thread (redundant if called from updateProgressAndTime, but safe)
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> updateTimeLabel(currentTimeMs, totalTimeMs));
            return;
        }
        if (timeLabel == null) return;

        String currentTimeStr = formatTimeMs(currentTimeMs);
        String totalTimeStr = (totalTimeMs <= 0) ? "--:--" : formatTimeMs(totalTimeMs);
        timeLabel.setText(currentTimeStr + " / " + totalTimeStr);
    }

    private String formatTimeMs(long timeMs) {
        if (timeMs < 0) timeMs = 0; // Treat negative time as 0
        long totalSeconds = timeMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void setControlsDisabled(boolean disable) {
        Platform.runLater(() -> {
            // Disable playback controls
            if (playPauseButton != null) playPauseButton.setDisable(disable);
            if (stopButton != null) stopButton.setDisable(disable);
            if (muteButton != null) muteButton.setDisable(disable);
            if (volumeSlider != null) volumeSlider.setDisable(disable);
            if (progressBar != null) progressBar.setDisable(disable);

            // Keep fullscreen usable even if media fails? User choice. Let's disable if player init fails.
            // if (fullScreenButton != null) fullScreenButton.setDisable(disable);

            // Quality switching (disabled anyway for now)
            // if (qualityComboBox != null) qualityComboBox.setDisable(disable);
        });
    }

    private void handleMediaError(String context, Throwable throwable, boolean isFatal) {
        System.err.println("ERROR [" + context + "]: " + (throwable != null ? throwable.getMessage() : "Unknown error"));
        if (throwable != null && !(throwable instanceof UnsatisfiedLinkError)) { // ULE already printed details
            // Print stack trace for better debugging, unless it's the known link error
            throwable.printStackTrace(System.err);
        }

        // Update UI to reflect error state
        Platform.runLater(() -> {
            if (titleLabel != null) titleLabel.setText("Error");
            if (timeLabel != null) timeLabel.setText("Error / --:--");
            if (progressBar != null) progressBar.setProgress(0.0);

            setControlsDisabled(true); // Disable playback controls
            isPlaying = false; // Ensure state reflects reality
            updatePlayPauseButtonState(); // Update button icon

            // Make sure controls overlay is visible to show the error state
            stopHideTimer();
            showControls(true); // Force show overlay immediately

            // If the error is fatal for the player instance (e.g., init failure), disable fullscreen too.
            if (isFatal && fullScreenButton != null) {
                fullScreenButton.setDisable(true);
            }
        });

        // If fatal, consider releasing the player instance to free resources,
        // although it might prevent retrying loading other media.
        if (isFatal && embeddedMediaPlayer != null) {
            System.err.println("Fatal error encountered. Releasing VLCJ player instance.");
            // Don't call disposePlayer() which releases the factory, just the current player.
            disposePlayerInternal();
        }
    }

    // ========================================================================
    // Overlay Visibility Logic
    // ========================================================================
    private void setupOverlayVisibility() {
        if (overlayPane == null || videoPlayerRoot == null) return;

        // --- Fade In (Parallel: Fade In + Slide Up) ---
        FadeTransition fadeIn = new FadeTransition(FADE_DURATION, overlayPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        TranslateTransition slideUp = new TranslateTransition(FADE_DURATION, overlayPane);
        slideUp.setFromY(SLIDE_OFFSET);
        slideUp.setToY(0.0);

        fadeInTransition = new ParallelTransition(fadeIn, slideUp);
        fadeInTransition.setOnFinished(e -> {
            overlayPane.setMouseTransparent(false);
            overlayPane.setPickOnBounds(true);
            controlsVisible = true;
            resetHideTimer();
        });

        // --- Fade Out (Parallel: Fade Out + Slide Down) ---
        FadeTransition fadeOut = new FadeTransition(FADE_DURATION, overlayPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        TranslateTransition slideDown = new TranslateTransition(FADE_DURATION, overlayPane);
        slideDown.setFromY(0.0);
        slideDown.setToY(SLIDE_OFFSET);

        fadeOutTransition = new ParallelTransition(fadeOut, slideDown);
        fadeOutTransition.setOnFinished(e -> {
            overlayPane.setMouseTransparent(true);
            overlayPane.setPickOnBounds(false);
            controlsVisible = false;
        });

        // Pause before hiding
        hideDelay = new PauseTransition(HIDE_DELAY_DURATION);
        hideDelay.setOnFinished(event -> {
            boolean actuallyPlaying = isPlaying && embeddedMediaPlayer != null && embeddedMediaPlayer.status().isPlaying();
            if (actuallyPlaying && controlsVisible && !isMouseOverPlayer()) {
                hideControls();
            } else if (actuallyPlaying && controlsVisible) {
                resetHideTimer();
            }
        });

        // Show controls when mouse enters the player area
        videoPlayerRoot.setOnMouseEntered(event -> {
            if (embeddedMediaPlayer != null) {
                showControls();
            }
        });

        // Reset hide timer when mouse moves within the player area
        videoPlayerRoot.setOnMouseMoved(event -> {
            if (embeddedMediaPlayer != null) {
                if (!controlsVisible) {
                    showControls();
                } else {
                    resetHideTimer();
                }
            }
        });

        // Start hide timer when mouse leaves the player area
        videoPlayerRoot.setOnMouseExited(event -> {
            startHideTimer();
        });

        overlayPane.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> resetHideTimer());
        overlayPane.addEventFilter(MouseEvent.MOUSE_MOVED, event -> resetHideTimer());
    }

    // Helper to check if mouse is over the root or the overlay
    private boolean isMouseOverPlayer() {
        return (videoPlayerRoot != null && videoPlayerRoot.isHover()) || (overlayPane != null && overlayPane.isHover());
    }

    private void showControls() {
        showControls(false);
    }

    private void showControls(boolean immediate) {
        if (overlayPane == null || fadeInTransition == null || fadeOutTransition == null) return;
        if (immediate) {
            fadeOutTransition.stop();
            fadeInTransition.stop();
            overlayPane.setOpacity(1.0);
            overlayPane.setTranslateY(0.0); // Reset translation
            overlayPane.setMouseTransparent(false);
            overlayPane.setPickOnBounds(true);
            controlsVisible = true;
            resetHideTimer();
        } else if (!controlsVisible || overlayPane.getOpacity() < 1.0) {
            fadeOutTransition.stop(); // Stop hiding if in progress
            fadeInTransition.playFromStart(); // Start fading in
        } else {
            // Already fully visible, just reset the timer
            resetHideTimer();
        }
    }

    private void hideControls() {
        if (overlayPane == null || fadeInTransition == null || fadeOutTransition == null) return;
        boolean actuallyPlaying = isPlaying && embeddedMediaPlayer != null && embeddedMediaPlayer.status().isPlaying();
        // Only hide if playing and controls are currently visible
        if (controlsVisible && overlayPane.getOpacity() > 0.0 && actuallyPlaying) {
            fadeInTransition.stop(); // Stop fading in if in progress
            fadeOutTransition.playFromStart(); // Start fading out
            stopHideTimer(); // Stop the countdown timer explicitly
        } else if (controlsVisible && !actuallyPlaying) {
            // If paused/stopped, ensure timer is stopped but don't fade out
            stopHideTimer();
        }
    }

    private void resetHideTimer() {
        if (hideDelay == null) return;
        hideDelay.stop();
        boolean actuallyPlaying = isPlaying && embeddedMediaPlayer != null && embeddedMediaPlayer.status().isPlaying();
        // Only restart the timer if playing and controls are intended to be visible
        if (actuallyPlaying && controlsVisible) {
            hideDelay.playFromStart();
        }
    }

    private void startHideTimer() {
        // This is typically called on mouse exit
        if (hideDelay == null) return;
        hideDelay.stop();
        boolean actuallyPlaying = isPlaying && embeddedMediaPlayer != null && embeddedMediaPlayer.status().isPlaying();
        // Start timer only if playing and controls are visible
        if (actuallyPlaying && controlsVisible) {
            hideDelay.playFromStart();
        }
    }

    private void stopHideTimer() {
        if (hideDelay != null) {
            hideDelay.stop();
        }
    }

    // Called when user interacts with controls (buttons, sliders press/drag start)
    private void handleInteractionStart() {
        showControls(); // Ensure controls are visible
        stopHideTimer(); // Stop the timer while interacting
    }

    // Called when user finishes interacting (button click, slider release)
    private void handleInteractionEnd() {
        resetHideTimer(); // Restart the hide timer
    }


    // ========================================================================
    // Fullscreen Handling
    // ========================================================================
    private void addFullScreenListener() {
        if (stage == null) {
            System.err.println("Cannot add fullscreen listener: Stage is null.");
            return;
        }
        if (fullScreenListener != null) return; // Listener already attached

        System.out.println("Attaching fullscreen listener to stage: " + stage);
        fullScreenListener = (observable, oldValue, newValue) -> {
            // Check the flag to ignore changes triggered by our own toggleFullScreen()
            if (isProgrammaticallyChangingFullscreen.get()) {
                // System.out.println("Fullscreen listener ignored programmatic change.");
                return;
            }
            // Handle external fullscreen changes (e.g., user pressing F11, OS controls)
            Platform.runLater(() -> {
                System.out.println("Fullscreen state changed externally: " + oldValue + " -> " + newValue);
                updateFullScreenButtonState(newValue);
                if (!newValue) { // Exiting fullscreen externally
                    showControls(true); // Force show controls immediately
                    stopHideTimer();
                }
                // Request layout update after external change
                requestLayoutRefresh();
            });
        };
        stage.fullScreenProperty().addListener(fullScreenListener);
        // Update button state initially
        updateFullScreenButtonState(stage.isFullScreen());
    }

    private void removeFullScreenListener() {
        if (stage != null && fullScreenListener != null) {
            try {
                stage.fullScreenProperty().removeListener(fullScreenListener);
                System.out.println("Fullscreen listener removed from stage: " + stage);
            } catch (Exception e) {
                System.err.println("Error removing fullscreen listener: " + e.getMessage());
            }
            fullScreenListener = null;
        }
    }

    private void toggleFullScreen() {
        if (stage == null) {
            System.err.println("Cannot toggle fullscreen: Stage not available.");
            showControls(true); // Show controls if button pressed but failed
            return;
        }

        // Prevent listener reentry and rapid toggling
        if (!isProgrammaticallyChangingFullscreen.compareAndSet(false, true)) {
            System.out.println("Ignoring toggleFullScreen - already processing.");
            return;
        }

        boolean currentFullscreenState = stage.isFullScreen();
        System.out.println("Action: Toggling Fullscreen (Current: " + currentFullscreenState + ")");

        try {
            stage.setFullScreen(!currentFullscreenState);
            // Update button state immediately for responsiveness
            updateFullScreenButtonState(!currentFullscreenState);
            handleInteractionStart(); // Show controls during transition

            // Schedule resetting the flag and layout refresh *after* the transition likely completed
            PauseTransition resetFlagDelay = new PauseTransition(Duration.millis(100)); // Short delay
            resetFlagDelay.setOnFinished(e -> {
                Platform.runLater(() -> {
                    requestLayoutRefresh(); // Ask JavaFX to update layout
                    isProgrammaticallyChangingFullscreen.set(false); // Reset the flag
                    handleInteractionEnd(); // Restart hide timer if needed
                });
            });
            resetFlagDelay.play();

        } catch (Exception e) {
            System.err.println("Error toggling fullscreen programmatically: " + e.getMessage());
            isProgrammaticallyChangingFullscreen.set(false); // Ensure flag is reset on error
        }
    }

    private void updateFullScreenButtonState(boolean isFullScreen) {
        Platform.runLater(() -> {
            if (fullScreenButton == null) return;
            String enterClass = "fullscreen-enter-icon";
            String exitClass = "fullscreen-exit-icon";
            fullScreenButton.getStyleClass().removeAll(enterClass, exitClass);
            fullScreenButton.getStyleClass().add(isFullScreen ? exitClass : enterClass);
            // Ensure base styles are present
            if (!fullScreenButton.getStyleClass().contains("fullscreen-button")) {
                fullScreenButton.getStyleClass().add("fullscreen-button");
            }
            if (!fullScreenButton.getStyleClass().contains("control-button")) {
                fullScreenButton.getStyleClass().add("control-button");
            }
            fullScreenButton.setTooltip(new Tooltip(isFullScreen ? "Exit Fullscreen (Esc)" : "Fullscreen (F11)"));
            fullScreenButton.applyCss();
        });
    }

    // ========================================================================
    // Layout and Styling Refresh
    // ========================================================================

    // Called on root pane resize
    private void handleResize() {
        // Avoid refreshes during programmatic fullscreen changes
        if (isProgrammaticallyChangingFullscreen.get()) return;
        // System.out.println("HandleResize triggered"); // Debugging
        Platform.runLater(this::requestLayoutRefresh);
    }

    // Request a layout pass - less aggressive than forcing CSS on everything
    private void requestLayoutRefresh() {
        // System.out.println("Requesting layout refresh..."); // Debugging
        if (videoPlayerRoot != null) {
            videoPlayerRoot.requestLayout(); // Ask the root pane to re-layout its children
        }
        // Update volume slider gradient as its size might have changed
        if (volumeSlider != null) {
            applyVolumeGradientStyle(volumeDecimalFormat.format(volumeSlider.getValue() * 100.0) + "%");
        }
        // Re-apply button styles if they sometimes get lost (less likely with requestLayout)
        // updatePlayPauseButtonState();
        // updateMuteButtonState();
        // updateFullScreenButtonState(stage != null && stage.isFullScreen());
    }

    // --- Volume Slider Gradient Styling ---
    private void bindVolumeSliderStyle() {
        if (volumeSlider == null) return;
        volumeSlider.setMinWidth(60); // Ensure minimum clickable width
        // Update gradient when value changes
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double percentage = newVal.doubleValue() * 100.0;
            applyVolumeGradientStyle(volumeDecimalFormat.format(percentage) + "%");
        });
        // Update gradient when layout changes (size might affect gradient rendering)
        volumeSlider.layoutBoundsProperty().addListener((obs, oldB, newB) -> Platform.runLater(() -> applyVolumeGradientStyle(volumeDecimalFormat.format(volumeSlider.getValue() * 100.0) + "%")));
        // Apply initial gradient
        Platform.runLater(() -> applyVolumeGradientStyle(volumeDecimalFormat.format(volumeSlider.getValue() * 100.0) + "%"));
    }

    private void applyVolumeGradientStyle(String percentageString) {
        // Needs to run on JFX thread
        if (!Platform.isFxApplicationThread()){
            String finalPercentageString = percentageString;
            Platform.runLater(() -> applyVolumeGradientStyle(finalPercentageString));
            return;
        }
        if (volumeSlider == null || volumeSlider.getScene() == null) return; // Needed for lookup

        Node track = volumeSlider.lookup(".track");
        if (track != null) {
            // Ensure percentage is valid CSS format
            percentageString = percentageString.replace(",", "."); // Use dot for decimal
            try {
                double percValue = Double.parseDouble(percentageString.replace("%", "").trim());
                if (percValue < 0) percentageString = "0%";
                else if (percValue > 100) percentageString = "100%";
                else percentageString = String.format("%.2f%%", percValue); // Format precisely
            } catch (NumberFormatException e) {
                percentageString = "0%"; // Default on parsing error
            }

            String trackBaseColor = "rgba(255, 255, 255, 0.2)"; // Semi-transparent white for unfilled part
            String filledColor = "white"; // Solid white for filled part
            String gradientStyle = String.format(
                    "-fx-background-color: linear-gradient(to right, %s 0%%, %s %s, %s %s, %s 100%%); " +
                            "-fx-background-insets: 0; " + // Adjust insets if needed for specific looks
                            "-fx-background-radius: 0.25em;", // Match default track radius (approx)
                    filledColor, filledColor, percentageString,
                    trackBaseColor, percentageString, trackBaseColor
            );
            track.setStyle(gradientStyle);
        }
        // else { System.err.println("Volume slider '.track' node not found."); } // Debug if style isn't applying
    }


    // ========================================================================
    // Cleanup Methods
    // ========================================================================

    // Releases the current MediaPlayer instance, but keeps the factory
    private void disposePlayerInternal() {
        System.out.println("Disposing internal VLCJ MediaPlayer...");
        stopHideTimer(); // Stop timers
        if (fadeInTransition != null) fadeInTransition.stop();
        if (fadeOutTransition != null) fadeOutTransition.stop();

        if (embeddedMediaPlayer != null) {
            try {
                // It's good practice to remove listeners if possible, though release() should handle it.
                // embeddedMediaPlayer.events().removeMediaPlayerEventListener(...); // Needs reference to listener

                if (embeddedMediaPlayer.status().isPlaying() || embeddedMediaPlayer.status().isPlayable()) {
                    embeddedMediaPlayer.controls().stop(); // Stop playback first
                }
                // Release the native resources associated with this player instance
                embeddedMediaPlayer.release();
                System.out.println("VLCJ EmbeddedMediaPlayer released.");
            } catch (Exception e) {
                System.err.println("Error releasing VLCJ MediaPlayer instance: " + e.getMessage());
            } finally {
                embeddedMediaPlayer = null; // Allow GC
            }
        }

        // Reset state variables
        isPlaying = false;
        isMuted = false;
        isSeeking = false;
        currentMediaLength = -1;

        // Reset UI on the JFX thread
        Platform.runLater(() -> {
            setControlsDisabled(true);
            if (progressBar != null) progressBar.setProgress(0.0);
            if (timeLabel != null) timeLabel.setText("00:00 / --:--");
            if (titleLabel != null) titleLabel.setText("");
            updatePlayPauseButtonState();
            updateMuteButtonState();
            if (overlayPane != null) {
                overlayPane.setOpacity(0.0);
                overlayPane.setMouseTransparent(true);
                overlayPane.setPickOnBounds(false);
            }
            // Clear the ImageView and WritableImage
            if (videoSurfaceView != null) {
                videoSurfaceView.setImage(null);
            }
            renderLock.lock(); // Protect WritableImage nullification
            try {
                videoWritableImage = null;
            } finally {
                renderLock.unlock();
            }
        });
    }

    /**
     * Call this method when the video player is no longer needed (e.g., closing the window/view)
     * to release all VLCJ resources, including the factory.
     */
    public void disposePlayer() {
        System.out.println("Dispose Player called for final cleanup.");
        disposePlayerInternal(); // Release the current player instance first

        // Release the factory - ONLY do this when completely finished with VLCJ
        if (mediaPlayerFactory != null) {
            try {
                mediaPlayerFactory.release();
                System.out.println("VLCJ MediaPlayerFactory released.");
            } catch(Exception e) {
                System.err.println("Error releasing VLCJ MediaPlayerFactory: " + e.getMessage());
            } finally {
                mediaPlayerFactory = null;
            }
        }

        // Clean up JavaFX resources
        removeFullScreenListener(); // Clean up stage listener
        stage = null;
        fadeInTransition = null;
        fadeOutTransition = null;
        hideDelay = null;
        // Nullify FXML references if this controller instance is being discarded
        // (though GC should handle this if the scene graph node is removed)
    }

    public void switchToMainPage(MouseEvent mouseEvent) throws IOException {
        disposePlayer();
        pageSwitcherController.switchToMainPage(mouseEvent);
    }


    // ========================================================================
    // VLCJ Video Surface Callback Implementation
    // ========================================================================

    /**
     * Provides the necessary callbacks for VLCJ to render video frames
     * onto a JavaFX WritableImage.
     */
    private class JavaFxCallbackVideoSurface extends CallbackVideoSurface {
        JavaFxCallbackVideoSurface() {
            super(new JavaFxBufferFormatCallback(), new JavaFxRenderCallback(), true, VideoSurfaceAdapters.getVideoSurfaceAdapter());
        }
    }

    /**
     * Called by VLCJ to determine the desired buffer format and dimensions.
     */
    private class JavaFxBufferFormatCallback implements BufferFormatCallback {
        private volatile int sourceWidth = 0; // Use volatile for visibility
        private volatile int sourceHeight = 0;

        @Override
        public BufferFormat getBufferFormat(int newSourceWidth, int newSourceHeight) {
            // This callback might be called from a VLC thread.
            // Update dimensions if they have changed.
            boolean dimensionsChanged = (this.sourceWidth != newSourceWidth || this.sourceHeight != newSourceHeight);
            this.sourceWidth = newSourceWidth;
            this.sourceHeight = newSourceHeight;

            // Schedule WritableImage creation/resizing on the JavaFX thread.
            // This needs to happen *before* the first display() call for these dimensions.
            if (dimensionsChanged && sourceWidth > 0 && sourceHeight > 0) {
                final int w = this.sourceWidth; // Capture dimensions for lambda
                final int h = this.sourceHeight;
                System.out.println("VLC Dimensions changed/detected: " + w + "x" + h);
                Platform.runLater(() -> {
                    renderLock.lock(); // Protect WritableImage modification
                    try {
                        // Check again inside runLater in case of rapid changes
                        if (videoWritableImage == null || videoWritableImage.getWidth() != w || videoWritableImage.getHeight() != h) {
                            System.out.println("JavaFX Thread: Creating/Resizing WritableImage to " + w + "x" + h);
                            videoWritableImage = new WritableImage(w, h);
                            if (videoSurfaceView != null) {
                                videoSurfaceView.setImage(videoWritableImage); // Assign to ImageView
                            } else {
                                System.err.println("Warning: videoSurfaceView is null when trying to set new WritableImage.");
                            }
                        }
                    } catch (Exception e){
                        System.err.println("Error creating/resizing WritableImage: "+ e.getMessage());
                        videoWritableImage = null; // Ensure it's null on error
                    }
                    finally {
                        renderLock.unlock();
                    }
                });
            }
            // Tell VLC we want BGRA format (RV32) with the given dimensions.
            return new RV32BufferFormat(sourceWidth, sourceHeight);
        }

        @Override
        public void allocatedBuffers(ByteBuffer[] byteBuffers) {
            // Called when VLC allocates the buffers. Can be ignored for basic rendering.
            // System.out.println("VLC allocated " + (byteBuffers != null ? byteBuffers.length : 0) + " buffers.");
        }
    }

    /**
     * Called by VLCJ when a new video frame is ready to be rendered.
     */
    private class JavaFxRenderCallback implements RenderCallback {
        @Override
        public void display(MediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat) {
            // This callback is invoked on a native VLC thread.
            // We need to marshal the pixel data update to the JavaFX Application Thread.
            if (bufferFormat == null || nativeBuffers == null || nativeBuffers.length == 0 || nativeBuffers[0] == null) {
                return; // No valid data
            }

            // Capture necessary data for the JFX thread
            final ByteBuffer sourceBuffer = nativeBuffers[0]; // Get the frame buffer
            final int width = bufferFormat.getWidth();
            final int height = bufferFormat.getHeight();
            final int pitch = bufferFormat.getPitches()[0]; // Bytes per line (stride)

            // Schedule the pixel update on the JavaFX thread
            Platform.runLater(() -> {
                renderLock.lock(); // *** Lock HERE, just before accessing/writing to WritableImage ***
                try {
                    final WritableImage targetImage = videoWritableImage; // Use local ref for safety

                    // *** Crucial check: Ensure WritableImage exists and matches dimensions ***
                    if (targetImage != null && targetImage.getWidth() == width && targetImage.getHeight() == height) {
                        try {
                            // Write the pixel data from the ByteBuffer to the WritableImage
                            targetImage.getPixelWriter().setPixels(
                                    0, 0,                     // Destination X, Y
                                    width, height,            // Destination Width, Height
                                    pixelFormat,              // The WritablePixelFormat (ByteBgraPre)
                                    sourceBuffer,             // Source ByteBuffer
                                    pitch                     // Source Bytes per Line (Stride)
                            );
                        } catch (Exception e) {
                            // Catch potential errors during pixel writing (e.g., buffer issues)
                            System.err.println("Error writing pixels to WritableImage: " + e.getMessage());
                            // Consider invalidating videoWritableImage here if errors are persistent
                            // videoWritableImage = null;
                        }
                    }
                    // Optional: Log if dimensions mismatch (can happen during resize)
                    // else if (targetImage != null) {
                    //     System.err.println("Render skipped: WritableImage dimensions (" + targetImage.getWidth()+"x"+targetImage.getHeight() + ") != Buffer dimensions (" + width + "x" + height +")");
                    // }
                    // else {
                    //     System.err.println("Render skipped: WritableImage is null.");
                    // }

                } finally {
                    renderLock.unlock(); // *** Unlock HERE, after accessing WritableImage ***
                }
            });
        }
    }
}