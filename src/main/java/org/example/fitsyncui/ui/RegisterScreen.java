package org.example.fitsyncui.ui;

import org.example.fitsyncui.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterScreen {

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        Label title = new Label("FitSync - Register");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2C3E50"));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        styleField(nameField);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleField(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);

        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        styleField(ageField);

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("M", "F");
        genderBox.setPromptText("Gender");
        styleField(genderBox);

        TextField weightField = new TextField();
        weightField.setPromptText("Weight (kg)");
        styleField(weightField);

        TextField heightField = new TextField();
        heightField.setPromptText("Height (cm)");
        styleField(heightField);

        Button registerButton = new Button("Register");
        registerButton.setPrefSize(140, 35);
        registerButton.setStyle(
                "-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"
        );

        Button backButton = new Button("Back to Login");
        backButton.setPrefSize(140, 35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#E74C3C"));

        registerButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String gender = genderBox.getValue();
                double weight = Double.parseDouble(weightField.getText().trim());
                double height = Double.parseDouble(heightField.getText().trim());

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || gender == null) {
                    messageLabel.setText("Please fill in all fields.");
                    return;
                }

                User user = new User(name, email, password, age, gender, weight, height);

                URL url = new URL("http://localhost:8080/api/users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(user);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                if (code == 200 || code == 201) {
                    messageLabel.setText("Registration successful! You can now log in.");
                    messageLabel.setTextFill(Color.web("#27AE60"));
                } else {
                    messageLabel.setText("Registration failed (status " + code + ").");
                    messageLabel.setTextFill(Color.web("#E74C3C"));
                }

                conn.disconnect();

            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Invalid input. Please check your values.");
                messageLabel.setTextFill(Color.web("#E74C3C"));
            }
        });

        backButton.setOnAction(e -> {
            new LoginScreen().start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(12,
                title, nameField, emailField, passwordField,
                ageField, genderBox, weightField, heightField,
                registerButton, backButton, messageLabel);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleField(Control field) {
        field.setPrefHeight(40);
        field.setMaxWidth(300);
        field.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");
    }
}
