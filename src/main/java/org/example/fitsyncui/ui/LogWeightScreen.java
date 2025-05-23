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

import java.time.LocalDate;

public class LogWeightScreen {

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen(); // store fullscreen state

        Label title = new Label("Log Today’s Weight");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        styleInput(datePicker);

        TextField weightField = new TextField();
        weightField.setPromptText("Enter your weight (kg)");
        styleInput(weightField);

        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(160);
        saveButton.setPrefHeight(35);
        saveButton.setStyle(
                "-fx-background-color: #2ECC71; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Button backButton = new Button("Back");
        backButton.setPrefWidth(160);
        backButton.setPrefHeight(35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        Label status = new Label();
        status.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        status.setTextFill(Color.web("#E74C3C"));

        saveButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightField.getText().trim());
                LocalDate date = datePicker.getValue();

                // Mock saving logic
                status.setTextFill(Color.web("#27AE60"));
                status.setText(String.format("Weight logged for %s: %.1f kg (mocked)", date, weight));
            } catch (NumberFormatException ex) {
                status.setTextFill(Color.web("#E74C3C"));
                status.setText("Please enter a valid weight.");
            }
        });

        backButton.setOnAction(e -> {
            // Simply restore fullscreen state (mock back navigation)
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12,
                title,
                datePicker,
                weightField,
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
        stage.setTitle("Log Weight");
        stage.setScene(scene);
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
}
