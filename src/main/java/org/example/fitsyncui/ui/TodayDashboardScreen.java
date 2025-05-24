package org.example.fitsyncui.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fitsyncui.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class TodayDashboardScreen {
    private final User user;

    public TodayDashboardScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        Label title = new Label("Today at a Glance");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        Label caloriesInLabel = new Label();
        Label caloriesOutLabel = new Label();
        Label netCaloriesLabel = new Label();
        Label intakeGoalLabel = new Label();
        Label burnGoalLabel = new Label();
        Label intakeStatus = new Label();
        Label burnStatus = new Label();

        ListView<String> mealsList = new ListView<>();
        mealsList.setPrefHeight(100);

        ListView<String> workoutsList = new ListView<>();
        workoutsList.setPrefHeight(100);

        Button backButton = new Button("Back");
        backButton.setPrefSize(160, 35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );
        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        for (Label lbl : new Label[]{
                caloriesInLabel, caloriesOutLabel, netCaloriesLabel,
                intakeGoalLabel, burnGoalLabel, intakeStatus, burnStatus
        }) {
            lbl.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            lbl.setTextFill(Color.web("#34495E"));
        }
        caloriesInLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        caloriesOutLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        netCaloriesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        intakeStatus.setTextFill(Color.web("#27AE60"));
        burnStatus.setTextFill(Color.web("#27AE60"));

        Label mealsTitle = new Label("Meals Logged:");
        mealsTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        mealsTitle.setTextFill(Color.web("#34495E"));

        Label workoutsTitle = new Label("Workouts Logged:");
        workoutsTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        workoutsTitle.setTextFill(Color.web("#34495E"));

        // Fetch today dashboard summary
        try {
            URL url = new URL("http://localhost:8080/api/today/" + user.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                InputStream in = conn.getInputStream();
                Map<String, Object> data = mapper.readValue(in, Map.class);

                caloriesInLabel.setText("Calories Consumed: " + ((Number) data.get("caloriesIn")).intValue());
                caloriesOutLabel.setText("Calories Burned: " + ((Number) data.get("caloriesOut")).intValue());
                netCaloriesLabel.setText("Net Calories: " + ((Number) data.get("netCalories")).intValue());

                intakeGoalLabel.setText("Intake Goal: " + ((Number) data.get("intakeGoal")).intValue());
                burnGoalLabel.setText("Burn Goal: " + ((Number) data.get("burnGoal")).intValue());

                intakeStatus.setText(data.get("intakeStatus").toString());
                burnStatus.setText(data.get("burnStatus").toString());

                ObservableList<String> mealItems = FXCollections.observableArrayList((List<String>) data.get("meals"));
                mealsList.setItems(mealItems);

                ObservableList<String> workoutItems = FXCollections.observableArrayList((List<String>) data.get("workouts"));
                workoutsList.setItems(workoutItems);
            }

            conn.disconnect();
        } catch (Exception ex) {
            caloriesInLabel.setText("Failed to load dashboard.");
        }

        VBox layout = new VBox(12,
                title,
                caloriesInLabel, caloriesOutLabel, netCaloriesLabel,
                intakeGoalLabel, intakeStatus,
                burnGoalLabel, burnStatus,
                new Separator(),
                mealsTitle, mealsList,
                workoutsTitle, workoutsList,
                backButton
        );
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        stage.setScene(new Scene(layout));
        stage.setTitle("Today at a Glance");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
