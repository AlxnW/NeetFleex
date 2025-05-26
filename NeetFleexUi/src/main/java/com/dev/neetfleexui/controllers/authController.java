package com.dev.neetfleexui.controllers;

import com.dev.neetfleexui.SessionManager;
import com.dev.neetfleexui.entities.UserSession;
import com.dev.neetfleexui.singleton.UserSessionSingleton;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;



public class authController implements Initializable {
    @FXML
    public TextField login;
    @FXML
    public PasswordField password;

    @FXML
    public TextField email;




    private final String BASE_URL = "http://localhost:8080/auth"; // Schimbă dacă e necesar



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public boolean loginHandler(javafx.scene.input.MouseEvent actionEvent) {
        String username = login.getText();
        String pass = password.getText();

        if (username.isEmpty() || pass.isEmpty()) {
            System.out.println("Username și parola sunt necesare!");
            return false;
        }

        try {
            // Crearea obiectului JSON
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", username);
            credentials.put("password", pass);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(credentials);

            // Configurarea conexiunii HTTP
            URL url = new URL(BASE_URL + "/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Trimiterea datelor
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Citirea răspunsului
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Citim răspunsul JSON
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Parsăm JSON-ul
                JsonNode jsonNode = objectMapper.readTree(response.toString());
                String authToken = jsonNode.get("token").asText(); // Extragem token-ul JWT
                String name = jsonNode.get("name").asText();

                UserSession userSession =  UserSessionSingleton.getInstance(authToken, name);


                // **** USE SESSION MANAGER TO SAVE ****
                SessionManager.saveSession(userSession);

                System.out.println("Login reușit! Session saved.");


                System.out.println("Login reușit! Token: " + authToken);

                boolean result = SubscriptionController.checkIfCurrentUserIsSubscribed();


                if(!result) {
                    pageSwitcher.switchToSubscriptionPage(actionEvent);
                }

                pageSwitcher.switchToMainPage(actionEvent);

                return true;
            } else {
                System.out.println("Login eșuat! Cod răspuns: " + responseCode);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Eroare la conectarea cu serverul!");
        }
        return false;
    }

    public void signHandler(MouseEvent actionEvent) throws IOException {
        String username = login.getText();
        String emailInput = email.getText();
        String pass = password.getText();

        if (username.isEmpty() || pass.isEmpty() || emailInput.isEmpty()) {
            System.out.println("Nu sunt completate toate campurile!");
            return;
        }

        try {
            // Crearea obiectului JSON
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", username);
            credentials.put("password", pass);
            credentials.put("email", emailInput);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(credentials);

            // Configurarea conexiunii HTTP
            URL url = new URL(BASE_URL + "/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Trimiterea datelor
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Citirea răspunsului
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Citim răspunsul JSON
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();



                System.out.println("Inregistrare reușita! Token: " );

                pageSwitcher.switchToSignIn(actionEvent);
            } else {
                System.out.println("Inregistrare eșuata! Cod răspuns: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Eroare la conectarea cu serverul!");
        }
    }









    private final PageSwitcherController pageSwitcher = new PageSwitcherController();

    @FXML
    private void handleSignUp(ActionEvent event) throws IOException {
        pageSwitcher.switchToSignUp(event);
    }

    @FXML
    private void handleSignIn(MouseEvent event) throws IOException {
        pageSwitcher.switchToSignIn(event);
    }


}
