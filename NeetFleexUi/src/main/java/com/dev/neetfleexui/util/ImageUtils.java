package com.dev.neetfleexui.util; // Example package

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageUtils {

    private static final Logger LOGGER = Logger.getLogger(ImageUtils.class.getName());

    // Make the loadImage method static so you don't need an instance
    public static void loadImage(String imageUrl, ImageView imageView, String imageDescription) {
        if (imageView == null) {
            LOGGER.log(Level.WARNING, "ImageView provided is null for {0}.", imageDescription);
            return;
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            LOGGER.log(Level.WARNING, "No image URL provided for {0}. Clearing image.", imageDescription);
            imageView.setImage(null); // Set to null or a placeholder
            return;
        }

        LOGGER.log(Level.INFO, "Attempting to load {0} from path: {1}", new Object[]{imageDescription, imageUrl});
        try {
            // 1. Remove quotes if present
            String cleanPath = imageUrl.replace("\"", "");

            // 2. Replace backslashes with forward slashes
            String correctedPath = cleanPath.replace("\\", "/");

            // 3. Construct the file URI
            String fileUri;
            // Basic check if it looks like an absolute Windows path (e.g., C:/...)
            if (correctedPath.matches("^[a-zA-Z]:/.*")) {
                fileUri = "file:///" + correctedPath; // Use three slashes for absolute paths
            } else if (correctedPath.startsWith("/")) {
                // Assume absolute Unix-like path
                fileUri = "file://" + correctedPath;
            }
            else {
                // Assume relative path or maybe a URL - let Image handle it, but log potential issue
                LOGGER.log(Level.FINE, "Path for {0} doesn't look absolute, treating as potential URL or relative path: {1}", new Object[]{imageDescription, correctedPath});
                // Try creating URI directly - might fail for relative file paths depending on context
                // For web URLs (http/https), new Image(imageUrl) works directly.
                // For local files, the file:/// prefix is generally needed.
                // Let's stick to the file URI assumption for now based on your original code.
                // If you expect HTTP URLs, you'd need different logic here.
                // Reverting to the original logic assuming file paths:
                if (correctedPath.matches("^[a-zA-Z]:/.*")) {
                    fileUri = "file:///" + correctedPath;
                } else {
                    // If not an absolute Windows path, assume it might be relative or non-Windows absolute
                    // Using "file://" might be safer default for non-Windows absolute paths starting with /
                    // Using "file:///" for drive letters ensures correctness on Windows.
                    // Let's refine:
                    if (correctedPath.startsWith("/")) { // Unix absolute path
                        fileUri = "file://" + correctedPath;
                    } else { // Assume relative or needs context - this part is tricky without knowing base path
                        // Simplest approach if always absolute paths are expected:
                        LOGGER.log(Level.WARNING, "Path for {0} is not absolute: {1}. Loading might fail.", new Object[]{imageDescription, correctedPath});
                        // Fallback attempt, might not work reliably for relative paths:
                        fileUri = "file:" + correctedPath; // Or potentially need to resolve against a base path
                        // Safest might be to enforce absolute paths in your data source.
                        // For this example, let's assume the original logic was mostly right for absolute paths:
                        if (correctedPath.matches("^[a-zA-Z]:/.*")) {
                            fileUri = "file:///" + correctedPath;
                        } else {
                            fileUri = "file://" + correctedPath; // Hope for the best for non-Windows absolute
                        }
                    }
                }
            }


            LOGGER.log(Level.INFO, "Attempting final URI for {0}: {1}", new Object[]{imageDescription, fileUri});

            Image image = new Image(fileUri, true); // Load in background

            String finalFileUri = fileUri;
            image.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    LOGGER.log(Level.WARNING, "Failed to load {0} from URI: {1}. Error: {2}",
                            new Object[]{imageDescription, finalFileUri,
                                    image.getException() != null ? image.getException().getMessage() : "Unknown image loading error"});
                    imageView.setImage(null); // Clear on error
                    // Optionally set a placeholder error image
                } else {
                    LOGGER.log(Level.INFO, "Successfully loaded {0} from URI: {1}",
                            new Object[]{imageDescription, finalFileUri});
                }
            });
            // Set image only if no immediate error, listener handles background loading errors
            if (!image.isError()) {
                imageView.setImage(image);
            } else {
                LOGGER.log(Level.WARNING, "Immediate error loading {0} from URI: {1}. Error: {2}",
                        new Object[]{imageDescription, fileUri,
                                image.getException() != null ? image.getException().getMessage() : "Unknown image loading error"});
                imageView.setImage(null);
            }


        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid URI syntax for {0} from path: {1}. Error: {2}",
                    new Object[]{imageDescription, imageUrl, e.getMessage()});
            imageView.setImage(null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error loading {0} from path: {1}",
                    new Object[]{imageDescription, imageUrl, e});
            imageView.setImage(null);
        }
    }
}