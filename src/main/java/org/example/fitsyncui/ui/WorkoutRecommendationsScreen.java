package org.example.fitsyncui.ui;

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
            String difficulty = lowBtn.isSelected() ? "Easy"
                    : modBtn.isSelected() ? "Medium"
                    : highBtn.isSelected() ? "Hard"
                    : null;
            String goalText = goalField.getText().trim();
            Integer goalCal = null;
            if (!goalText.isEmpty()) {
                try {
                    goalCal = Integer.parseInt(goalText);
                } catch (NumberFormatException ex) {
                    status.setTextFill(Color.web("#E74C3C"));
                    status.setText("Invalid calorie input.");
                    return;
                }
            }

            ObservableList<String> recs = FXCollections.observableArrayList();
            if ("Easy".equals(difficulty)) {
                recs.addAll(
                        "Yoga (Flexibility) - 4.0 cal/min [Easy]",
                        "Walking (Cardio)   - 5.0 cal/min [Easy]"
                );
            } else if ("Medium".equals(difficulty)) {
                recs.addAll(
                        "Cycling (Cardio)           - 7.5 cal/min [Medium]",
                        "Bodyweight Training (Strength) - 6.5 cal/min [Medium]"
                );
            } else if ("Hard".equals(difficulty)) {
                recs.addAll(
                        "HIIT (Cardio)      - 10.0 cal/min [Hard]",
                        "CrossFit (Strength) - 9.0 cal/min [Hard]"
                );
            }

            // Optional goal filter: keep only those ≥ goalCal if specified
            if (goalCal != null) {
                Integer finalGoalCal = goalCal;
                recs.removeIf(item -> {
                    // parse first numeric part before space
                    String num = item.split(" ")[1];
                    double rate = Double.parseDouble(num);
                    return rate * 30 < finalGoalCal;
                });
            }

            if (recs.isEmpty()) {
                status.setTextFill(Color.web("#E74C3C"));
                status.setText("No matching workouts found.");
            } else {
                status.setTextFill(Color.web("#27AE60"));
                status.setText("Recommended workouts:");
            }
            resultList.setItems(recs);
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(12,
                title,
                levelBox,
                goalField,
                recommendButton,
                status,
                resultList,
                backButton
        );
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
        control.setStyle(
                "-fx-background-color: #ECF0F1; " +
                        "-fx-border-color: #BDC3C7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
    }

    private void styleButton(Button button, String color) {
        button.setPrefSize(35, 160);
        button.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );
    }
}
