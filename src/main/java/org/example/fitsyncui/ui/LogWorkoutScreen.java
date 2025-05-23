package org.example.fitsyncui.ui;

import org.example.fitsyncui.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LogWorkoutScreen {
    private final User user;
    private final ObservableList<String> workoutNames = FXCollections.observableArrayList();

    public LogWorkoutScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        // 1) FETCH workouts for this user
        try {
            URL url = new URL("http://localhost:8080/api/workouts/user/" + user.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                // assumes each workout JSON has at least a "name" field
                List<Map<String, Object>> list = mapper.readValue(
                        in, new TypeReference<>() {
                        }
                );
                for (Map<String, Object> w : list) {
                    String name = w.get("name").toString();
                    workoutNames.add(name);
                }
            } else {
                System.err.println("Failed to fetch workouts: " + conn.getResponseCode());
            }
            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 2) BUILD UI
        Label title = new Label("Log a Workout");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        ComboBox<String> workoutDropdown = new ComboBox<>(workoutNames);
        workoutDropdown.setPromptText("Select Workout");
        styleInput(workoutDropdown);

        TextField workoutNameField = new TextField();
        workoutNameField.setPromptText("Workout name");
        styleInput(workoutNameField);

        TextField caloriesField = new TextField();
        caloriesField.setPromptText("Calories Burned");
        styleInput(caloriesField);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        styleInput(datePicker);

        Button logButton = new Button("Log Workout");
        Button updateButton = new Button("Update Workout");
        Button deleteButton = new Button("Delete Workout");
        Button backButton = new Button("Back");

        styleButton(logButton, "#2ECC71");
        styleButton(updateButton, "#F1C40F");
        styleButton(deleteButton, "#E74C3C");
        styleButton(backButton, "#3498DB");

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#E74C3C"));

        // when selecting from dropdown, populate name field
        workoutDropdown.setOnAction(e -> {
            String sel = workoutDropdown.getValue();
            if (sel != null) {
                workoutNameField.setText(sel);
                caloriesField.clear();
            }
        });

        // your existing mock handlers:
        logButton.setOnAction(e -> {
            String name = workoutNameField.getText().trim();
            String cal = caloriesField.getText().trim();
            LocalDate date = datePicker.getValue();
            if (name.isEmpty() || cal.isEmpty() || date == null) {
                messageLabel.setText("Fill in all fields.");
                return;
            }
            try {
                Integer.parseInt(cal);
                if (!workoutNames.contains(name)) {
                    workoutNames.add(name);
                    workoutDropdown.setItems(workoutNames);
                }
                messageLabel.setTextFill(Color.web("#27AE60"));
                messageLabel.setText("Workout logged (mocked)!");
                workoutDropdown.setValue(null);
                workoutNameField.clear();
                caloriesField.clear();
            } catch (NumberFormatException ex) {
                messageLabel.setText("Invalid calories input.");
            }
        });

        updateButton.setOnAction(e -> {
            String selected = workoutDropdown.getValue();
            String newName = workoutNameField.getText().trim();
            String newCal = caloriesField.getText().trim();
            if (selected == null || newName.isEmpty() || newCal.isEmpty()) {
                messageLabel.setText("Select workout and enter new values.");
                return;
            }
            try {
                Integer.parseInt(newCal);
                workoutNames.remove(selected);
                workoutNames.add(newName);
                workoutDropdown.setItems(workoutNames);
                messageLabel.setTextFill(Color.web("#27AE60"));
                messageLabel.setText("Workout updated (mocked).");
                workoutDropdown.setValue(null);
                workoutNameField.clear();
                caloriesField.clear();
            } catch (NumberFormatException ex) {
                messageLabel.setText("Invalid calorie input.");
            }
        });

        deleteButton.setOnAction(e -> {
            String selected = workoutDropdown.getValue();
            if (selected == null) {
                messageLabel.setText("Select a workout to delete.");
                return;
            }
            workoutNames.remove(selected);
            workoutDropdown.setItems(workoutNames);
            messageLabel.setTextFill(Color.web("#27AE60"));
            messageLabel.setText("Workout deleted (mocked).");
            workoutDropdown.setValue(null);
            workoutNameField.clear();
            caloriesField.clear();
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(12,
                title,
                workoutDropdown,
                workoutNameField,
                caloriesField,
                datePicker,
                logButton,
                updateButton,
                deleteButton,
                backButton,
                messageLabel
        );
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setTitle("Log Workout");
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

    private void styleButton(Button button, String color) {
        button.setPrefWidth(160);
        button.setPrefHeight(35);
        button.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );
    }
}
