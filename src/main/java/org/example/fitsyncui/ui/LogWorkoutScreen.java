package org.example.fitsyncui.ui;

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
import org.example.fitsyncui.model.User;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogWorkoutScreen {
    private final User user;
    private final ObservableList<String> workoutNames = FXCollections.observableArrayList();
    private final Map<String, Integer> workoutNameToId = new HashMap<>();
    private final Map<String, Integer> workoutCalories = new HashMap<>();

    public LogWorkoutScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();
        stage.setTitle("Log Workout");

        Label title = new Label("Log a Workout");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        ComboBox<String> workoutDropdown = new ComboBox<>();
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
        styleButton(logButton, "#2ECC71");

        Button backButton = new Button("Back");
        styleButton(backButton, "#3498DB");

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#E74C3C"));

        loadWorkouts(workoutDropdown);

        workoutDropdown.setOnAction(e -> {
            String selected = workoutDropdown.getValue();
            if (selected != null) {
                workoutNameField.setText(selected);
                Integer cal = workoutCalories.get(selected);
                caloriesField.setText(cal != null ? cal.toString() : "");
            }
        });

        logButton.setOnAction(e -> {
            String workoutName = workoutNameField.getText().trim();
            String caloriesText = caloriesField.getText().trim();
            LocalDate date = datePicker.getValue();

            if (workoutName.isEmpty() || caloriesText.isEmpty() || date == null) {
                messageLabel.setText("Fill in all fields.");
                return;
            }

            try {
                int duration = Integer.parseInt(caloriesText);

                // 1. Create or update workout
                URL createUrl = new URL("http://localhost:8080/api/workouts");
                HttpURLConnection createConn = (HttpURLConnection) createUrl.openConnection();
                createConn.setRequestMethod("POST");
                createConn.setDoOutput(true);
                createConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String createBody = "name=" + workoutName + "&duration=" + duration;
                try (OutputStream os = createConn.getOutputStream()) {
                    os.write(createBody.getBytes());
                }

                if (createConn.getResponseCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> response = mapper.readValue(createConn.getInputStream(), new TypeReference<>() {});
                    int workoutId = (Integer) response.get("id");

                    // 2. Log workout
                    URL logUrl = new URL("http://localhost:8080/api/workouts/log");
                    HttpURLConnection logConn = (HttpURLConnection) logUrl.openConnection();
                    logConn.setRequestMethod("POST");
                    logConn.setDoOutput(true);
                    logConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String logBody = "userId=" + user.getId() + "&workoutId=" + workoutId + "&date=" + date;
                    try (OutputStream os = logConn.getOutputStream()) {
                        os.write(logBody.getBytes());
                    }

                    if (logConn.getResponseCode() == 200) {
                        messageLabel.setTextFill(Color.web("#27AE60"));
                        messageLabel.setText("Workout logged successfully!");
                        loadWorkouts(workoutDropdown);
                        workoutDropdown.setValue(null);
                        workoutNameField.clear();
                        caloriesField.clear();
                    } else {
                        messageLabel.setText("Failed to log workout.");
                    }
                } else {
                    messageLabel.setText("Failed to save workout.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Error occurred.");
            }
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(12,
                title, workoutDropdown, workoutNameField, caloriesField, datePicker,
                logButton, backButton, messageLabel
        );
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void loadWorkouts(ComboBox<String> dropdown) {
        workoutNames.clear();
        workoutNameToId.clear();
        workoutCalories.clear();
        try {
            URL url = new URL("http://localhost:8080/api/workouts/all");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> list = mapper.readValue(conn.getInputStream(), new TypeReference<>() {});
                for (Map<String, Object> w : list) {
                    String name = w.get("name").toString();
                    int id = (Integer) w.get("id");
                    int duration = (Integer) w.get("duration");

                    if (!workoutNames.contains(name)) {
                        workoutNames.add(name);
                        workoutNameToId.put(name, id);
                        workoutCalories.put(name, duration);
                    }
                }
            }
            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dropdown.setItems(workoutNames);
    }

    private void styleInput(Control control) {
        control.setPrefHeight(40);
        control.setMaxWidth(300);
        control.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    private void styleButton(Button button, String color) {
        button.setPrefWidth(160);
        button.setPrefHeight(35);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }
}
