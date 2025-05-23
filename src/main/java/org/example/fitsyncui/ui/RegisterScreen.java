package org.example.fitsyncui.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterScreen {

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen(); // save fullscreen state

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
                "-fx-background-color: #2ECC71; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Button backButton = new Button("Back to Login");
        backButton.setPrefSize(140, 35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#E74C3C"));

        registerButton.setOnAction(e -> {
            try {
                // simple validation only
                if (nameField.getText().trim().isEmpty()
                        || emailField.getText().trim().isEmpty()
                        || passwordField.getText().trim().isEmpty()
                        || ageField.getText().trim().isEmpty()
                        || genderBox.getValue() == null
                        || weightField.getText().trim().isEmpty()
                        || heightField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException();
                }
                // parse numeric fields
                Integer.parseInt(ageField.getText().trim());
                Double.parseDouble(weightField.getText().trim());
                Double.parseDouble(heightField.getText().trim());

                messageLabel.setText("Registration successful! You can now log in.");
                messageLabel.setTextFill(Color.web("#27AE60"));
            } catch (Exception ex) {
                messageLabel.setText("Please enter valid values.");
                messageLabel.setTextFill(Color.web("#E74C3C"));
            }
        });

        backButton.setOnAction(e -> {
            new LoginScreen().start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12,
                title,
                nameField, emailField, passwordField,
                ageField, genderBox, weightField, heightField,
                registerButton, backButton, messageLabel
        );
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(25));
        form.setMaxWidth(350);

        VBox root = new VBox(form);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #FDFEFE;");
        root.prefWidthProperty().bind(stage.widthProperty());
        root.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("FitSync - Register");
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleField(Control c) {
        c.setPrefHeight(40);
        c.setMaxWidth(300);
        c.setStyle(
                "-fx-background-color: #ECF0F1; " +
                        "-fx-border-color: #BDC3C7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
    }
}
