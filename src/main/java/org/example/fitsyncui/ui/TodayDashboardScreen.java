package org.example.fitsyncui.ui;

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

import java.time.LocalDate;

public class TodayDashboardScreen {

    public TodayDashboardScreen() {
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen(); // remember fullscreen
        LocalDate today = LocalDate.now();

        Label title = new Label("Today at a Glance - " + today);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        // Mocked summary values
        Label caloriesInLabel = new Label("Calories Consumed: 1800");
        Label caloriesOutLabel = new Label("Calories Burned: 500");
        Label netCaloriesLabel = new Label("Net Calories: 1300");
        Label intakeGoalLabel = new Label("Intake Goal: 2000");
        Label burnGoalLabel = new Label("Burn Goal: 400");
        Label intakeStatus = new Label("200 cal left to reach intake goal.");
        Label burnStatus = new Label("You hit your burn goal!");

        // Meals list
        Label mealsTitle = new Label("Meals Logged:");
        ListView<String> mealsList = new ListView<>();
        ObservableList<String> meals = FXCollections.observableArrayList(
                "Oatmeal (Breakfast) - 250 cal",
                "Chicken Salad (Lunch)   - 450 cal",
                "Apple (Snack)           - 95 cal",
                "Grilled Fish (Dinner)   - 600 cal"
        );
        mealsList.setItems(meals);
        mealsList.setPrefHeight(100);

        // Workouts list
        Label workoutsTitle = new Label("Workouts Logged:");
        ListView<String> workoutsList = new ListView<>();
        ObservableList<String> workouts = FXCollections.observableArrayList(
                "Morning Run",
                "Yoga"
        );
        workoutsList.setItems(workouts);
        workoutsList.setPrefHeight(100);

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(160, 35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );
        backButton.setOnAction(e -> {
            // Return to dashboard (mock)
            stage.setFullScreen(wasFullScreen);
        });

        // Style all labels
        for (Label lbl : new Label[]{
                caloriesInLabel, caloriesOutLabel, netCaloriesLabel,
                intakeGoalLabel, burnGoalLabel, intakeStatus, burnStatus,
                mealsTitle, workoutsTitle
        }) {
            lbl.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            lbl.setTextFill(Color.web("#34495E"));
        }
        caloriesInLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        caloriesOutLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        netCaloriesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        intakeStatus.setTextFill(Color.web("#27AE60"));
        burnStatus.setTextFill(Color.web("#27AE60"));

        // Layout
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

        Scene scene = new Scene(layout);
        stage.setTitle("Today at a Glance");
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
