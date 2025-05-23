package org.example.fitsyncui.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.example.fitsyncui.model.User;


import java.util.Objects;

public class LoginScreen {

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        // Logo
        ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/fitsyncLogo.png"))
        ));
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);

        Label title = new Label("FitSync - Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2C3E50"));

        TextField emailField = new TextField();
        styleField(emailField);
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        styleField(passwordField);
        passwordField.setPromptText("Password");

        TextField visiblePasswordField = new TextField();
        styleField(visiblePasswordField);
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);

        CheckBox showPassword = new CheckBox("Show Password");
        showPassword.setTextFill(Color.web("#34495E"));
        showPassword.setOnAction(e -> {
            if (showPassword.isSelected()) {
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            } else {
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
            }
        });

        Button loginButton = new Button("Login");
        loginButton.setPrefSize(200, 35);
        loginButton.setStyle(
                "-fx-background-color: #2ECC71; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Button registerButton = new Button("Register");
        registerButton.setPrefSize(200, 35);
        registerButton.setStyle(
                "-fx-background-color: #3498DB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#E74C3C"));

        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pwd = showPassword.isSelected()
                    ? visiblePasswordField.getText().trim()
                    : passwordField.getText().trim();

            if (email.isEmpty() || pwd.isEmpty()) {
                messageLabel.setText("Please enter email and password.");
                return;
            }

            try {
                // Build URL
                String urlStr = String.format(
                        "http://localhost:8080/api/users/login?email=%s&password=%s",
                        URLEncoder.encode(email, StandardCharsets.UTF_8),
                        URLEncoder.encode(pwd, StandardCharsets.UTF_8)
                );

                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() == 200) {
                    // Parse JSON into User via Jackson
                    ObjectMapper mapper = new ObjectMapper();
                    User user = mapper.readValue(conn.getInputStream(), User.class);

                    new DashboardScreen(user).start(stage);
                    stage.setFullScreen(wasFullScreen);

                } else {
                    messageLabel.setText("Invalid email or password.");
                }
                conn.disconnect();

            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Login failed. Try again.");
            }
        });

        registerButton.setOnAction(e -> {
            new RegisterScreen().start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12,
                logo,
                title,
                emailField,
                passwordField,
                visiblePasswordField,
                showPassword,
                loginButton,
                registerButton,
                messageLabel
        );
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(350);

        VBox root = new VBox(form);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #FDFEFE;");
        root.prefWidthProperty().bind(stage.widthProperty());
        root.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("FitSync - Login");
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleField(TextField field) {
        field.setPrefHeight(40);
        field.setMaxWidth(300);
        field.setStyle(
                "-fx-background-color: #ECF0F1; " +
                        "-fx-border-color: #BDC3C7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
    }
}
