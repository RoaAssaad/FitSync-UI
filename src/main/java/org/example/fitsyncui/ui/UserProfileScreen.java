package org.example.fitsyncui.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.fitsyncui.model.User;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UserProfileScreen {
    private final User user;

    public UserProfileScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        Label title = new Label("Your Profile");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        TextField nameField = new TextField(user.getName());
        styleField(nameField);

        TextField emailField = new TextField(user.getEmail());
        emailField.setDisable(true);
        emailField.setStyle("-fx-background-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5;");
        emailField.setPrefHeight(40);
        emailField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password (leave blank to keep current)");
        styleField(passwordField);

        TextField ageField = new TextField(String.valueOf(user.getAge()));
        styleField(ageField);

        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("M", "F"));
        genderBox.setValue(user.getGender());
        styleField(genderBox);

        TextField weightField = new TextField(String.valueOf(user.getWeight()));
        styleField(weightField);

        TextField heightField = new TextField(String.valueOf(user.getHeight()));
        styleField(heightField);

        Button saveButton = new Button("Save Changes");
        styleButton(saveButton, "#2ECC71");

        Button deleteButton = new Button("Delete Account");
        styleButton(deleteButton, "#E74C3C");

        Button backButton = new Button("Back");
        styleButton(backButton, "#3498DB");

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#E74C3C"));

        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String pwd = passwordField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String gender = genderBox.getValue();
                double weight = Double.parseDouble(weightField.getText().trim());
                double height = Double.parseDouble(heightField.getText().trim());

                if (name.isEmpty() || gender == null) throw new IllegalArgumentException();

                User updated = new User(
                        user.getId(),
                        name,
                        user.getEmail(),
                        pwd.isEmpty() ? user.getPassword() : pwd,
                        age,
                        gender,
                        weight,
                        height
                );

                URL url = new URL("http://localhost:8080/api/users/" + user.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(updated);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                if (code == 200) {
                    messageLabel.setTextFill(Color.web("#27AE60"));
                    messageLabel.setText("Profile updated successfully!");
                } else {
                    messageLabel.setText("Update failed. (" + code + ")");
                }

                conn.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Please enter valid input.");
            }
        });

        deleteButton.setOnAction(e -> {
            try {
                URL url = new URL("http://localhost:8080/api/users/" + user.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                int code = conn.getResponseCode();
                conn.disconnect();

                if (code == 200 || code == 204) {
                    new LoginScreen().start(stage);
                    stage.setFullScreen(false);
                } else {
                    messageLabel.setText("Delete failed. (" + code + ")");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Delete failed.");
            }
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
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
                deleteButton,
                backButton,
                messageLabel
        );
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);
        form.setPadding(new Insets(25));

        VBox layout = new VBox(form);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setTitle("User Profile");
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleField(Control c) {
        c.setPrefHeight(40);
        c.setMaxWidth(300);
        c.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    private void styleButton(Button button, String color) {
        button.setPrefSize(200, 35);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }
}
