package com.dev.neetfleexui.controllers; // Or your actual package

import com.dev.neetfleexui.SessionManager;
import com.dev.neetfleexui.dto.RequestDetailsForSubscription;
import com.dev.neetfleexui.dto.SubscriptionType;
import com.dev.neetfleexui.singleton.PageSwitcherControllerSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField; // If you have other fields
import javafx.scene.control.Button;  // If you have buttons
import javafx.scene.control.CheckBox; // If you have checkboxes
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.YearMonth;


public class SubscriptionController {

    private static final String API_BASE_URL = "http://localhost:8080";
    private static final String API_SUBSCRIBE = "/subscribe";
    private static final String API_CheckIfSubscribed = "/isUser/subscribed?username=";

    @FXML private ToggleGroup planToggleGroup;
    @FXML private VBox basicPlanCard, premiumPlanCard, familyPlanCard;
    @FXML private RadioButton basicPlanRadio, premiumPlanRadio, familyPlanRadio;

    // Add FXML injections for your ComboBoxes
    @FXML private ComboBox<String> expiryMonthCombo;
    @FXML private ComboBox<String> expiryYearCombo;

    // ... other FXML fields like fullNameField, emailField, cvvField, subscribeButton etc.
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField cardNumberField;
    @FXML private TextField cvvField;
    @FXML private CheckBox termsCheckbox;
    @FXML private Button subscribeButton;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    public void initialize() {
        // --- Existing plan card click logic ---
        basicPlanCard.setOnMouseClicked(event -> {
            if (!basicPlanRadio.isSelected()) {
                basicPlanRadio.setSelected(true);
            }
        });
        premiumPlanCard.setOnMouseClicked(event -> {
            if (!premiumPlanRadio.isSelected()) {
                premiumPlanRadio.setSelected(true);
            }
        });
        familyPlanCard.setOnMouseClicked(event -> {
            if (!familyPlanRadio.isSelected()) {
                familyPlanRadio.setSelected(true);
            }
        });

        planToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updatePlanCardStyles();
        });
        updatePlanCardStyles();
        // --- End of existing plan card click logic ---

        // --- Populate Expiry Date ComboBoxes ---
        populateExpiryMonths();
        populateExpiryYears();
        // --- End of population logic ---

        // Add other initialization logic if needed
    }

    private void updatePlanCardStyles() {
        basicPlanCard.getStyleClass().remove("selected-plan");
        premiumPlanCard.getStyleClass().remove("selected-plan");
        familyPlanCard.getStyleClass().remove("selected-plan");

        RadioButton selectedRadio = (RadioButton) planToggleGroup.getSelectedToggle();
        if (selectedRadio != null) {
            if (selectedRadio == basicPlanRadio) {
                basicPlanCard.getStyleClass().add("selected-plan");
            } else if (selectedRadio == premiumPlanRadio) {
                premiumPlanCard.getStyleClass().add("selected-plan");
            } else if (selectedRadio == familyPlanRadio) {
                familyPlanCard.getStyleClass().add("selected-plan");
            }
        }
    }

    private void populateExpiryMonths() {
        ObservableList<String> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i)); // "01", "02", ..., "12"
        }
        expiryMonthCombo.setItems(months);
    }

    private void populateExpiryYears() {
        int currentYear = YearMonth.now().getYear();
        ObservableList<String> years = FXCollections.observableArrayList();
        for (int i = 0; i < 15; i++) { // Populate for the next 15 years
            years.add(String.valueOf(currentYear + i));
        }
        expiryYearCombo.setItems(years);
    }

    public void subscribe(MouseEvent event) {
        try {
            // Verificare validitate date
            if (fullNameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    cardNumberField.getText().isEmpty() || cvvField.getText().isEmpty() ||
                    expiryMonthCombo.getValue() == null || expiryYearCombo.getValue() == null ||
                    !termsCheckbox.isSelected()) {
                // Afișați un mesaj de eroare pentru câmpuri obligatorii
                System.out.println("Toate câmpurile sunt obligatorii și trebuie să acceptați termenii și condițiile.");
                return;
            }

            // Verificare tip abonament selectat
            String selectedPlan = "";
            RadioButton selectedRadio = (RadioButton) planToggleGroup.getSelectedToggle();

            if (selectedRadio == null) {
                System.out.println("Vă rugăm să selectați un plan de abonament.");
                return;
            } else if (selectedRadio == basicPlanRadio) {
                selectedPlan = "Basic";
            } else if (selectedRadio == premiumPlanRadio) {
                selectedPlan = "Premium";
            } else if (selectedRadio == familyPlanRadio) {
                selectedPlan = "Family";
            }

            // Crearea obiectului pentru request
            RequestDetailsForSubscription requestDetailsForSub = new RequestDetailsForSubscription(
                    SessionManager.getUserName(),
                    SubscriptionType.valueOf(selectedPlan)


            );

            // Conversia în JSON și trimiterea cererii
            String jsonRequestBody = objectMapper.writeValueAsString(requestDetailsForSub);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + API_SUBSCRIBE))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getCurrentToken())
                    .method("GET", HttpRequest.BodyPublishers.ofString(jsonRequestBody)) // Schimbat din GET în POST
                    .build();

            // Implementarea trimiterii cererii și procesării răspunsului
            // Exemplu:
            // HttpClient client = HttpClient.newHttpClient();
            // HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Procesare răspuns...

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
             if(response.statusCode() == 200) {
                 PageSwitcherControllerSingleton.getInstance().switchToMainPage(event);
             }
            System.out.println("Abonament selectat: " + selectedPlan);
            System.out.println("Cerere trimisă cu succes!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Eroare la procesarea cererii: " + e.getMessage());
        }
    }


    public static boolean checkIfCurrentUserIsSubscribed() throws IOException, InterruptedException {
        String name = SessionManager.getUserName();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + API_CheckIfSubscribed+name))
                .header("Authorization", "Bearer " + SessionManager.getCurrentToken())
                .method("GET", HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200) {
            return true;
        }

        return false;


    }

    // Add other event handlers for buttons, etc.
    // e.g., @FXML private void handleSubscribeButtonAction() { ... }
}