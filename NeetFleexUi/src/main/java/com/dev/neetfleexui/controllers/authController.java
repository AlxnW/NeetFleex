package com.dev.neetfleexui.controllers;

import com.dev.neetfleexui.SessionManager;
import com.dev.neetfleexui.entities.UserSession;
import com.dev.neetfleexui.singleton.UserSessionSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException; // Import for specific network errors
import java.net.ConnectException; // Import for specific network errors
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class authController implements Initializable {
    // ... (all other @FXML fields and existing code remain the same) ...
    @FXML public TextField login;
    @FXML public PasswordField password;
    @FXML public TextField email;
    @FXML public PasswordField confirmPassword;
    @FXML private Text usernameErrorText;
    @FXML private Text emailErrorText;
    @FXML private Text passwordErrorText;
    @FXML private Text confirmPasswordErrorText;

    // Login specific error Text fields
    @FXML private Text loginFieldErrorText;
    @FXML private Text passwordFieldErrorText;
    @FXML private Text generalLoginErrorText;

    private final String BASE_URL = "http://localhost:8080/auth";
    private final PageSwitcherController pageSwitcher = new PageSwitcherController();

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final int MIN_PASSWORD_LENGTH = 8;


    // ... (initialize, showError, clearError, all validation methods for sign-up and login remain the same) ...
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // --- Sign-up Page Listeners (only if elements are present) ---
        if (login != null && usernameErrorText != null) { // Sign-up username field
            login.textProperty().addListener((obs, oldVal, newVal) -> validateUsernameFieldForSignUp(newVal, true));
        }
        if (email != null && emailErrorText != null) { // Sign-up email field
            email.textProperty().addListener((obs, oldVal, newVal) -> validateEmailFieldForSignUp(newVal, true));
        }
        if (password != null && passwordErrorText != null) { // Sign-up password field
            password.textProperty().addListener((obs, oldVal, newVal) -> {
                validatePasswordFieldForSignUp(newVal, true);
                if (confirmPassword != null && confirmPasswordErrorText != null) {
                    validateConfirmPasswordFieldForSignUp(confirmPassword.getText(), newVal, true);
                }
            });
        }
        if (confirmPassword != null && confirmPasswordErrorText != null && password != null) { // Sign-up confirm password
            confirmPassword.textProperty().addListener((obs, oldVal, newVal) -> validateConfirmPasswordFieldForSignUp(newVal, password.getText(), true));
        }

        // --- Login Page Listeners (only if elements are present) ---
        if (login != null && loginFieldErrorText != null) { // Login page username/email field
            login.textProperty().addListener((obs, oldVal, newVal) -> validateLoginFieldForLogin(newVal, true));
        }
        if (password != null && passwordFieldErrorText != null) { // Login page password field
            password.textProperty().addListener((obs, oldVal, newVal) -> validatePasswordFieldForLogin(newVal, true));
        }
    }

    // --- UI Update Helper Methods (reusable) ---
    private void showError(Control field, Text errorText, String message) {
        if (field == null || errorText == null) return;

        // Special handling for generalLoginErrorText on login page,
        // and potentially a similar generalSignUpErrorText if you add one.
        // We don't want to put a red border on an input field for a *general* form error.
        if (errorText == generalLoginErrorText || (usernameErrorText != null && errorText == usernameErrorText && message.toLowerCase().contains("server"))) {
            // Don't add error-field style to the input 'field' if it's a general message
            // or a server message being displayed in a field-specific error text area.
        } else if (field != null && !field.getStyleClass().contains("error-field")) {
            field.getStyleClass().add("error-field");
        }

        errorText.setText(message);
        errorText.setVisible(true);
        errorText.setManaged(true);
    }

    private void clearError(Control field, Text errorText) {
        if (field == null || errorText == null) return;
        field.getStyleClass().remove("error-field");
        errorText.setText("");
        errorText.setVisible(false);
        errorText.setManaged(false);
    }

    // --- Sign-Up Specific Validation Methods ---
    private boolean validateUsernameFieldForSignUp(String usernameValue, boolean isInteractive) {
        if (usernameErrorText == null || login == null) return true;
        if (usernameValue == null || usernameValue.trim().isEmpty()) {
            if (!isInteractive) showError(login, usernameErrorText, "Username cannot be empty.");
            else clearError(login, usernameErrorText);
            return false;
        } else {
            clearError(login, usernameErrorText);
            return true;
        }
    }

    private boolean validateEmailFieldForSignUp(String emailValue, boolean isInteractive) {
        if (emailErrorText == null || email == null) return true;
        if (emailValue == null || emailValue.trim().isEmpty()) {
            if (!isInteractive) showError(email, emailErrorText, "Email cannot be empty.");
            else clearError(email, emailErrorText);
            return false;
        } else if (!EMAIL_PATTERN.matcher(emailValue).matches()) {
            showError(email, emailErrorText, "Invalid email format (e.g., user@example.com).");
            return false;
        } else {
            clearError(email, emailErrorText);
            return true;
        }
    }

    private boolean validatePasswordFieldForSignUp(String passwordValue, boolean isInteractive) {
        if (passwordErrorText == null || password == null) return true;
        if (passwordValue == null || passwordValue.isEmpty()) {
            if (!isInteractive) showError(password, passwordErrorText, "Password cannot be empty.");
            else clearError(password, passwordErrorText);
            return false;
        } else if (passwordValue.length() < MIN_PASSWORD_LENGTH) {
            showError(password, passwordErrorText, "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
            return false;
        } else {
            clearError(password, passwordErrorText);
            return true;
        }
    }

    private boolean validateConfirmPasswordFieldForSignUp(String confirmPasswordValue, String originalPasswordValue, boolean isInteractive) {
        if (confirmPasswordErrorText == null || confirmPassword == null) return true;
        if (isInteractive && (originalPasswordValue == null || originalPasswordValue.isEmpty() || originalPasswordValue.length() < MIN_PASSWORD_LENGTH)) {
            clearError(confirmPassword, confirmPasswordErrorText);
            return false;
        }
        if (confirmPasswordValue == null || confirmPasswordValue.isEmpty()) {
            if (!isInteractive || (originalPasswordValue != null && !originalPasswordValue.isEmpty())) {
                showError(confirmPassword, confirmPasswordErrorText, "Please confirm your password.");
            } else {
                clearError(confirmPassword, confirmPasswordErrorText);
            }
            return false;
        } else if (originalPasswordValue == null || !originalPasswordValue.equals(confirmPasswordValue)) {
            showError(confirmPassword, confirmPasswordErrorText, "Passwords do not match.");
            return false;
        } else {
            clearError(confirmPassword, confirmPasswordErrorText);
            return true;
        }
    }

    // --- Login Specific Validation Methods ---
    private boolean validateLoginFieldForLogin(String loginValue, boolean isInteractive) {
        if (loginFieldErrorText == null || login == null) return true;
        if (loginValue == null || loginValue.trim().isEmpty()) {
            if (!isInteractive) showError(login, loginFieldErrorText, "Username or Email cannot be empty.");
            else clearError(login, loginFieldErrorText);
            return false;
        } else {
            clearError(login, loginFieldErrorText);
            return true;
        }
    }

    private boolean validatePasswordFieldForLogin(String passwordValue, boolean isInteractive) {
        if (passwordFieldErrorText == null || password == null) return true;
        if (passwordValue == null || passwordValue.isEmpty()) {
            if (!isInteractive) showError(password, passwordFieldErrorText, "Password cannot be empty.");
            else clearError(password, passwordFieldErrorText);
            return false;
        } else {
            clearError(password, passwordFieldErrorText);
            return true;
        }
    }


    public boolean loginHandler(MouseEvent actionEvent) {
        // ... (loginHandler remains the same as previous version) ...
        if (login == null || password == null) {
            System.out.println("Login or password field is missing from the FXML for loginHandler.");
            return false;
        }
        if (generalLoginErrorText != null) { // Clear previous general error
            generalLoginErrorText.setText("");
            generalLoginErrorText.setVisible(false);
            generalLoginErrorText.setManaged(false);
        }


        String usernameOrEmail = login.getText();
        String pass = password.getText();

        boolean isLoginFieldValid = validateLoginFieldForLogin(usernameOrEmail, false);
        boolean isPasswordFieldValid = validatePasswordFieldForLogin(pass, false);

        if (!isLoginFieldValid || !isPasswordFieldValid) {
            return false;
        }

        try {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", usernameOrEmail);
            credentials.put("password", pass);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(credentials);

            URL url = new URL(BASE_URL + "/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000); // 5 seconds connection timeout
            conn.setReadTimeout(5000);    // 5 seconds read timeout
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                if (loginFieldErrorText != null) clearError(login, loginFieldErrorText);
                if (passwordFieldErrorText != null) clearError(password, passwordFieldErrorText);
                if (generalLoginErrorText != null) {
                    generalLoginErrorText.setVisible(false);
                    generalLoginErrorText.setManaged(false);
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JsonNode jsonNode = objectMapper.readTree(response.toString());
                String authToken = jsonNode.get("token").asText();
                String name = jsonNode.get("name").asText();

                UserSession userSession = UserSessionSingleton.getInstance(authToken, name);
                SessionManager.saveSession(userSession);

                System.out.println("Login reușit! Session saved. Token: " + authToken);

                boolean result = SubscriptionController.checkIfCurrentUserIsSubscribed();

                if (!result) {
                    pageSwitcher.switchToSubscriptionPage(actionEvent);
                } else {
                    pageSwitcher.switchToMainPage(actionEvent);
                }
                return true;
            } else {
                System.out.println("Login eșuat! Cod răspuns: " + responseCode);
                String errorMessage = "Login failed. Please check your credentials.";
                if (responseCode >= 500) {
                    errorMessage = "Server error (" + responseCode + "). Please try again later.";
                } else if (responseCode == 401 || responseCode == 400) {
                    errorMessage = "Invalid username/email or password.";
                } else {
                    errorMessage = "Login failed (Error: " + responseCode + ").";
                }
                if (generalLoginErrorText != null) {
                    showError(login, generalLoginErrorText, errorMessage);
                }
                return false;
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            System.out.println("Connection timed out while trying to log in.");
            if (generalLoginErrorText != null) {
                showError(login, generalLoginErrorText, "Connection timed out. Please check your internet and try again.");
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            System.out.println("Connection refused. Server might be down.");
            if (generalLoginErrorText != null) {
                showError(login, generalLoginErrorText, "Could not connect to the server. Please try again later.");
            }
        }
        catch (IOException e) { // Catch other IOExceptions related to network
            e.printStackTrace();
            System.out.println("Network error during login: " + e.getMessage());
            if (generalLoginErrorText != null) {
                showError(login, generalLoginErrorText, "A network error occurred. Please check your connection.");
            }
        }
        catch (Exception e) { // Catch all other exceptions
            e.printStackTrace();
            System.out.println("Eroare la conectarea cu serverul (login)!");
            if (generalLoginErrorText != null) {
                showError(login, generalLoginErrorText, "An unexpected error occurred. Please try again.");
            }
        }
        return false;
    }


    public void signHandler(MouseEvent actionEvent) throws IOException {
        // Ensure all required fields for sign-up are present
        if (login == null || email == null || password == null || confirmPassword == null ||
                usernameErrorText == null || emailErrorText == null || passwordErrorText == null || confirmPasswordErrorText == null) {
            System.out.println("One or more required UI elements are missing from the FXML for signHandler.");
            // Optionally display a very generic error if possible, or just log and return
            return;
        }

        // Clear previous errors displayed on sign-up form fields
        clearError(login, usernameErrorText);
        clearError(email, emailErrorText);
        clearError(password, passwordErrorText);
        clearError(confirmPassword, confirmPasswordErrorText);

        String usernameInput = login.getText();
        String emailInput = email.getText();
        String passInput = password.getText();
        String confirmPassInput = confirmPassword.getText();

        // Perform all client-side validations. 'isInteractive' is false for submit.
        boolean isUsernameValid = validateUsernameFieldForSignUp(usernameInput, false);
        boolean isEmailValid = validateEmailFieldForSignUp(emailInput, false);
        boolean isPasswordValid = validatePasswordFieldForSignUp(passInput, false);
        boolean isConfirmPasswordValid = false; // Initialize
        if (isPasswordValid) { // Only validate confirm if password itself meets basic criteria
            isConfirmPasswordValid = validateConfirmPasswordFieldForSignUp(confirmPassInput, passInput, false);
        } else {
            // If password field itself is invalid, also trigger validation for confirmPass
            // This ensures "confirm password cannot be empty" or "passwords do not match" (if filled) is shown
            validateConfirmPasswordFieldForSignUp(confirmPassInput, passInput, false);
        }

        if (!isUsernameValid || !isEmailValid || !isPasswordValid || !isConfirmPasswordValid) {
            System.out.println("Client-side validation failed for sign-up. Please check the fields.");
            // Errors are already shown by the validation methods. Do not proceed.
            return;
        }

        // If all client-side validations pass, proceed with API call
        try {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", usernameInput);
            credentials.put("password", passInput);
            credentials.put("email", emailInput);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(credentials);

            URL url = new URL(BASE_URL + "/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000); // 5 seconds connection timeout
            conn.setReadTimeout(5000);    // 5 seconds read timeout
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // HTTP_OK
                System.out.println("Inregistrare reușita!");
                // Clear any errors from the form on success before navigating
                clearError(login, usernameErrorText);
                clearError(email, emailErrorText);
                clearError(password, passwordErrorText);
                clearError(confirmPassword, confirmPasswordErrorText);

                pageSwitcher.switchToSignIn(actionEvent); // Navigate ONLY on success
            } else {
                // Handle server-side registration errors (e.g., user/email exists, server validation failed)
                String errorMessage = "Registration failed. Please try again.";
                if (responseCode == 400) { // Bad Request (often validation errors or user exists)
                    // Try to parse error from server if possible, otherwise generic
                    // For now, assume a generic "user/email might exist" or "invalid data"
                    errorMessage = "Registration failed. Username or email might already exist, or data is invalid.";
                } else if (responseCode >= 500) { // Server error
                    errorMessage = "Server error (" + responseCode + ") during registration. Please try again later.";
                } else {
                    errorMessage = "Registration failed with error code: " + responseCode;
                }
                System.out.println("Inregistrare eșuata! Cod răspuns: " + responseCode + ". Message: " + errorMessage);
                // Display the error message. Using usernameErrorText as a general spot on sign-up form.
                showError(login, usernameErrorText, errorMessage);
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            System.out.println("Connection timed out during registration.");
            showError(login, usernameErrorText, "Connection timed out. Check your internet and try again.");
        } catch (ConnectException e) {
            e.printStackTrace();
            System.out.println("Connection refused during registration. Server might be down.");
            showError(login, usernameErrorText, "Could not connect to the server. Please try again later.");
        }
        catch (IOException e) { // Catch other IOExceptions related to network
            e.printStackTrace();
            System.out.println("Network error during registration: " + e.getMessage());
            showError(login, usernameErrorText, "A network error occurred. Please check your connection.");
        }
        catch (Exception e) { // Catch-all for other unexpected errors
            e.printStackTrace();
            System.out.println("Eroare la conectarea cu serverul (sign-up)! " + e.getMessage());
            // Display a generic server error message on the UI
            showError(login, usernameErrorText, "An unexpected error occurred. Please try again.");
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) throws IOException {
        pageSwitcher.switchToSignUp(event);
    }

    @FXML
    private void handleSignIn(MouseEvent event) throws IOException {
        pageSwitcher.switchToSignIn(event);
    }
}