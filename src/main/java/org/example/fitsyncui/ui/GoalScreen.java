package org.example.fitsyncui.ui;

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
import org.example.fitsyncui.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class GoalScreen {
    private final User user;

    public GoalScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) throws Exception {
        boolean wasFullScreen = stage.isFullScreen();
        Label title = new Label("Set Daily Calorie Goals");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        TextField intakeGoalField = new TextField();
        intakeGoalField.setPromptText("Calories to Consume (e.g. 2000)");
        styleInput(intakeGoalField);

        TextField burnGoalField = new TextField();
        burnGoalField.setPromptText("Calories to Burn (e.g. 500)");
        styleInput(burnGoalField);

        Button saveButton = new Button("Save Goals");
        saveButton.setPrefSize(160, 35);
        saveButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        Button backButton = new Button("Back");
        backButton.setPrefSize(160, 35);
        backButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        Label status = new Label();
        status.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        status.setTextFill(Color.web("#E74C3C"));

        // GET existing goals
        {
            URL url = new URL("http://localhost:8080/api/goals/user/" + user.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                Map<String, Object> m = new ObjectMapper().readValue(in, Map.class);
                intakeGoalField.setText(m.get("caloriesInGoal").toString());
                burnGoalField.setText(m.get("caloriesBurnGoal").toString());
            }
            conn.disconnect();
        }

        saveButton.setOnAction(e -> {
            try {
                double inGoal = Double.parseDouble(intakeGoalField.getText().trim());
                double burnGoal = Double.parseDouble(burnGoalField.getText().trim());
                URL url = new URL("http://localhost:8080/api/goals/user/" + user.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                String body = new ObjectMapper().writeValueAsString(Map.of(
                        "caloriesInGoal", inGoal,
                        "caloriesBurnGoal", burnGoal
                ));
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }
                if (conn.getResponseCode() == 200) {
                    status.setText("Goals saved!");
                    status.setTextFill(Color.web("#27AE60"));
                } else {
                    status.setText("Save failed (" + conn.getResponseCode() + ")");
                }
                conn.disconnect();
            } catch (Exception ex) {
                status.setText("Please enter valid numbers.");
                status.setTextFill(Color.web("#E74C3C"));
            }
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12,
                title, intakeGoalField, burnGoalField,
                saveButton, backButton, status
        );
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);
        form.setPadding(new Insets(25));

        VBox root = new VBox(form);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #FDFEFE;");
        root.prefWidthProperty().bind(stage.widthProperty());
        root.prefHeightProperty().bind(stage.heightProperty());

        stage.setScene(new Scene(root));
        stage.setTitle("Set Daily Goals");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleInput(Control c) {
        c.setPrefHeight(40);
        c.setMaxWidth(300);
        c.setStyle("-fx-background-color:#ECF0F1;-fx-border-color:#BDC3C7;-fx-border-radius:5;-fx-background-radius:5;");
    }
}
