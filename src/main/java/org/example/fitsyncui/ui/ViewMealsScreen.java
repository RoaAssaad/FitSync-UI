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

public class ViewMealsScreen {

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen(); // save fullscreen state

        Label title = new Label("View Logged Meals");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefHeight(40);
        datePicker.setMaxWidth(300);
        datePicker.setStyle(
                "-fx-background-color: #ECF0F1; " +
                        "-fx-border-color: #BDC3C7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );

        Button viewButton = new Button("View Meals");
        viewButton.setPrefSize(140, 35);
        viewButton.setStyle(
                "-fx-background-color: #2ECC71; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Button backButton = new Button("Back");
        backButton.setPrefSize(140, 35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );
        backButton.setOnAction(e -> {
            // mock back: just restore fullscreen
            stage.setFullScreen(wasFullScreen);
        });

        ListView<String> mealList = new ListView<>();
        mealList.setPrefHeight(150);
        mealList.setMaxWidth(300);

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#34495E"));

        viewButton.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            ObservableList<String> meals = FXCollections.observableArrayList();

            if (selectedDate.equals(LocalDate.now())) {
                meals.addAll(
                        "Oatmeal (Breakfast): 250 cal",
                        "Chicken Wrap (Lunch): 430 cal",
                        "Grilled Salmon (Dinner): 550 cal"
                );
                messageLabel.setText("Meals logged:");
                messageLabel.setTextFill(Color.web("#27AE60"));
            } else {
                messageLabel.setText("No meals found for this date.");
                messageLabel.setTextFill(Color.web("#E74C3C"));
            }

            mealList.setItems(meals);
        });

        VBox layout = new VBox(12,
                title,
                datePicker,
                viewButton,
                messageLabel,
                mealList,
                backButton
        );
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setTitle("View Meals");
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
