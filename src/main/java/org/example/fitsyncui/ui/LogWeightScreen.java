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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

public class LogWeightScreen {
    private final User user;

    public LogWeightScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        Label title = new Label("Log Today’s Weight");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        styleInput(datePicker);

        TextField weightField = new TextField();
        weightField.setPromptText("Enter your weight (kg)");
        styleInput(weightField);

        Button saveButton = new Button("Save");
        saveButton.setPrefSize(160, 35);
        saveButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        Button backButton = new Button("Back");
        backButton.setPrefSize(160, 35);
        backButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        Label status = new Label();
        status.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        status.setTextFill(Color.web("#E74C3C"));

        ObjectMapper mapper = new ObjectMapper();

        Runnable fetchExisting = () -> {
            try {
                LocalDate date = datePicker.getValue();
                String endpoint = String.format("http://localhost:8080/api/weights/%d/by-date?date=%s", user.getId(), date);
                HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    Map<String, Object> weightData = mapper.readValue(in, Map.class);
                    weightField.setText(weightData.get("weight").toString());
                }
                conn.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        datePicker.setOnAction(e -> fetchExisting.run());
        fetchExisting.run();

        saveButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightField.getText().trim());
                LocalDate date = datePicker.getValue();

                URL url = new URL("http://localhost:8080/api/weights");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String body = String.format("userId=%d&date=%s&weight=%.1f", user.getId(), date, weight);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                if (code == 200 || code == 201) {
                    status.setTextFill(Color.web("#27AE60"));
                    status.setText(String.format("Weight logged for %s: %.1f kg", date, weight));
                } else {
                    status.setText("Save failed (" + code + ")");
                }

                conn.disconnect();
            } catch (NumberFormatException ex) {
                status.setTextFill(Color.web("#E74C3C"));
                status.setText("Please enter a valid weight.");
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setTextFill(Color.web("#E74C3C"));
                status.setText("Error saving weight.");
            }
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12, title, datePicker, weightField, saveButton, backButton, status);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);
        form.setPadding(new Insets(25));

        VBox layout = new VBox(form);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        stage.setScene(new Scene(layout));
        stage.setTitle("Log Weight");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleInput(Control control) {
        control.setPrefHeight(40);
        control.setMaxWidth(300);
        control.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");
    }
}
