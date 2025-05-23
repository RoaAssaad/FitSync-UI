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

public class UserProfileScreen {

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen(); // preserve fullscreen

        Label title = new Label("Your Profile");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        TextField nameField = new TextField("John Doe");
        styleField(nameField);

        TextField emailField = new TextField("johndoe@example.com");
        emailField.setDisable(true);
        emailField.setPrefHeight(40);
        emailField.setMaxWidth(300);
        emailField.setStyle(
                "-fx-background-color: #E0E0E0; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password (leave blank to keep current)");
        styleField(passwordField);

        TextField ageField = new TextField("28");
        styleField(ageField);

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("M", "F");
        genderBox.setValue("M");
        styleField(genderBox);

        TextField weightField = new TextField("75.5");
        styleField(weightField);

        TextField heightField = new TextField("180");
        styleField(heightField);

        Button saveButton = new Button("Save Changes");
        saveButton.setPrefSize(200, 35);
        saveButton.setStyle(
                "-fx-background-color: #2ECC71; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#E74C3C"));

        saveButton.setOnAction(e -> {
            try {
                // Validate fields
                String name = nameField.getText().trim();
                String pwd = passwordField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String gender = genderBox.getValue();
                double weight = Double.parseDouble(weightField.getText().trim());
                double height = Double.parseDouble(heightField.getText().trim());

                if (name.isEmpty() || gender == null) throw new IllegalArgumentException();

                // Mock success
                messageLabel.setText("Profile updated successfully! (mock)");
                messageLabel.setTextFill(Color.web("#27AE60"));
            } catch (Exception ex) {
                messageLabel.setText("Please enter valid input.");
                messageLabel.setTextFill(Color.web("#E74C3C"));
            }
        });

        backButton.setOnAction(e -> {
            // Mock back navigation
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12,
                title,
                nameField,
                emailField,
                passwordField,
                ageField,
                genderBox,
                weightField,
                heightField,
                saveButton,
                backButton,
                messageLabel
        );
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);

        VBox layout = new VBox(form);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.setPadding(new Insets(25));
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setTitle("User Profile");
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleField(Control c) {
        c.setPrefSize(40, 300);
        c.setStyle(
                "-fx-background-color: #ECF0F1; " +
                        "-fx-border-color: #BDC3C7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
    }
}
