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

public class GoalScreen {

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen(); // keep fullscreen state

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
        saveButton.setPrefWidth(160);
        saveButton.setPrefHeight(35);
        saveButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        Button backButton = new Button("Back");
        backButton.setPrefWidth(160);
        backButton.setPrefHeight(35);
        backButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        Label status = new Label();
        status.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        status.setTextFill(Color.web("#E74C3C"));

        saveButton.setOnAction(e -> {
            try {
                double intakeGoal = Double.parseDouble(intakeGoalField.getText().trim());
                double burnGoal = Double.parseDouble(burnGoalField.getText().trim());

                // Mock saving logic:
                status.setTextFill(Color.web("#27AE60"));
                status.setText("Goals saved! (mocked)");

            } catch (NumberFormatException ex) {
                status.setTextFill(Color.web("#E74C3C"));
                status.setText("Please enter valid numbers.");
            }
        });

        backButton.setOnAction(e -> {
            // Go back to dashboard (mocked)
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12,
                title,
                intakeGoalField,
                burnGoalField,
                saveButton,
                backButton,
                status
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
        stage.setTitle("Set Daily Goals");
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleInput(Control input) {
        input.setPrefHeight(40);
        input.setMaxWidth(300);
        input.setStyle(
                "-fx-background-color: #ECF0F1; " +
                        "-fx-border-color: #BDC3C7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
    }
}
