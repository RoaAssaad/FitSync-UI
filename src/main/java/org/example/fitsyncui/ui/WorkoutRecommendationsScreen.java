package org.example.fitsyncui.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.fitsyncui.model.User;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class WorkoutRecommendationsScreen {

    private final User user;

    public WorkoutRecommendationsScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        Label title = new Label("Workout Recommendations");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        Label levelLabel = new Label("Choose Fitness Level:");
        levelLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        levelLabel.setTextFill(Color.web("#34495E"));

        RadioButton lowBtn = new RadioButton("Low");
        RadioButton modBtn = new RadioButton("Moderate");
        RadioButton highBtn = new RadioButton("High");
        ToggleGroup levelGroup = new ToggleGroup();
        lowBtn.setToggleGroup(levelGroup);
        modBtn.setToggleGroup(levelGroup);
        highBtn.setToggleGroup(levelGroup);
        HBox radioRow = new HBox(20, lowBtn, modBtn, highBtn);
        radioRow.setAlignment(Pos.CENTER);
        VBox levelBox = new VBox(5, levelLabel, radioRow);
        levelBox.setAlignment(Pos.CENTER);

        TextField goalField = new TextField();
        goalField.setPromptText("Calories to Burn (optional)");
        styleInput(goalField);

        Button recommendButton = new Button("Get Recommendations");
        styleButton(recommendButton, "#2ECC71");

        Button backButton = new Button("Back");
        styleButton(backButton, "#3498DB");

        Label status = new Label();
        status.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        status.setTextFill(Color.web("#34495E"));

        ListView<String> resultList = new ListView<>();
        resultList.setPrefHeight(200);

        recommendButton.setOnAction(e -> {
            try {
                String difficulty = lowBtn.isSelected() ? "Easy"
                        : modBtn.isSelected() ? "Medium"
                        : highBtn.isSelected() ? "Hard"
                        : null;

                String goalText = goalField.getText().trim();
                Double goalCal = null;
                if (!goalText.isEmpty()) {
                    try {
                        goalCal = Double.parseDouble(goalText);
                    } catch (NumberFormatException ex) {
                        status.setTextFill(Color.web("#E74C3C"));
                        status.setText("Invalid calorie input.");
                        return;
                    }
                }

                StringBuilder urlBuilder = new StringBuilder("http://localhost:8080/api/workout-recommendations?");
                if (difficulty != null) {
                    urlBuilder.append("difficulty=").append(difficulty);
                }
                if (goalCal != null) {
                    if (urlBuilder.toString().contains("=")) urlBuilder.append("&");
                    urlBuilder.append("calories=").append(goalCal);
                }

                URL url = new URL(urlBuilder.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                ObservableList<String> recs = FXCollections.observableArrayList();
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    List<Map<String, Object>> exercises = new ObjectMapper().readValue(in, new TypeReference<>() {
                    });
                    for (Map<String, Object> ex : exercises) {
                        recs.add(String.format(
                                "%s (%s) - %.1f cal/min [%s]",
                                ex.get("name"),
                                ex.get("category"),
                                ((Number) ex.get("caloriesPerMinute")).doubleValue(),
                                ex.get("difficultyLevel")
                        ));
                    }
                }
                conn.disconnect();

                if (recs.isEmpty()) {
                    status.setTextFill(Color.web("#E74C3C"));
                    status.setText("No matching workouts found.");
                } else {
                    status.setTextFill(Color.web("#27AE60"));
                    status.setText("Recommended workouts:");
                }
                resultList.setItems(recs);

            } catch (Exception ex) {
                ex.printStackTrace();
                status.setTextFill(Color.web("#E74C3C"));
                status.setText("Error fetching data.");
            }
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(12, title, levelBox, goalField, recommendButton, status, resultList, backButton);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setTitle("Workout Recommendations");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleInput(Control control) {
        control.setPrefHeight(40);
        control.setMaxWidth(300);
        control.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    private void styleButton(Button button, String color) {
        button.setPrefSize(160, 35);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }
}
