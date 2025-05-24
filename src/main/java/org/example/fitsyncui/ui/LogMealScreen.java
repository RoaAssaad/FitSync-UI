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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogMealScreen {
    private final User user;
    private final ObservableList<String> mealNames = FXCollections.observableArrayList();
    private final Map<String, Double> mealCalories = new HashMap<>();
    private final Map<String, String> mealTypes = new HashMap<>();

    public LogMealScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();
        fetchAllMealsFromBackend();

        Label title = new Label("Log a Meal");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        ComboBox<String> mealDropdown = new ComboBox<>(mealNames);
        mealDropdown.setPromptText("Select Meal");
        styleInput(mealDropdown);

        TextField customMealField = new TextField();
        customMealField.setPromptText("Or enter custom meal name");
        styleInput(customMealField);

        TextField caloriesField = new TextField();
        caloriesField.setPromptText("Calories");
        styleInput(caloriesField);

        ComboBox<String> mealTypeBox = new ComboBox<>(
                FXCollections.observableArrayList("Breakfast", "Lunch", "Dinner", "Snack")
        );
        mealTypeBox.setPromptText("Meal Type");
        styleInput(mealTypeBox);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        styleInput(datePicker);

        Button logButton = new Button("Log Meal");
        Button updateButton = new Button("Update Meal");
        Button deleteButton = new Button("Delete Meal");
        Button backButton = new Button("Back");

        for (Button b : new Button[]{logButton, updateButton, deleteButton, backButton}) {
            b.setPrefSize(200, 35);
        }

        styleButton(logButton, "#2ECC71");
        styleButton(updateButton, "#F1C40F");
        styleButton(deleteButton, "#E74C3C");
        styleButton(backButton, "#3498DB");

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#E74C3C"));

        mealDropdown.setOnAction(e -> {
            String sel = mealDropdown.getValue();
            if (sel != null) {
                customMealField.setText(sel);
                caloriesField.setText(mealCalories.getOrDefault(sel, 0.0).toString());
                mealTypeBox.setValue(mealTypes.getOrDefault(sel, ""));
            }
        });

        logButton.setOnAction(e -> {
            String name = customMealField.getText().trim();
            String type = mealTypeBox.getValue();
            String calText = caloriesField.getText().trim();
            LocalDate date = datePicker.getValue();
            if (name.isEmpty() || type == null || calText.isEmpty() || date == null) {
                messageLabel.setText("Please complete all fields.");
                return;
            }
            try {
                double calories = Double.parseDouble(calText);

                // Create or update the meal
                String createUrl = String.format("http://localhost:8080/api/meals?foodName=%s&calories=%s&mealType=%s",
                        URLEncoder.encode(name, StandardCharsets.UTF_8),
                        URLEncoder.encode(String.valueOf(calories), StandardCharsets.UTF_8),
                        URLEncoder.encode(type, StandardCharsets.UTF_8));
                HttpURLConnection createConn = (HttpURLConnection) new URL(createUrl).openConnection();
                createConn.setRequestMethod("POST");
                createConn.setRequestProperty("Accept", "application/json");
                InputStream createIn = createConn.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> mealData = mapper.readValue(createIn, new TypeReference<>() {});
                int mealId = (int) mealData.get("id");
                createConn.disconnect();

                // Log the meal
                String logUrl = String.format("http://localhost:8080/api/meals/log?userId=%d&mealId=%d&date=%s",
                        user.getId(), mealId, date);
                HttpURLConnection logConn = (HttpURLConnection) new URL(logUrl).openConnection();
                logConn.setRequestMethod("POST");
                logConn.getInputStream().close(); // trigger the request
                logConn.disconnect();

                mealNames.add(name);
                mealCalories.put(name, calories);
                mealTypes.put(name, type);
                messageLabel.setTextFill(Color.web("#27AE60"));
                messageLabel.setText("Meal logged successfully!");
                mealDropdown.setItems(mealNames);
                mealDropdown.setValue(null);
                customMealField.clear();
                caloriesField.clear();
                mealTypeBox.setValue(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Error logging meal.");
            }
        });

        updateButton.setOnAction(e -> {
            String selected = mealDropdown.getValue();
            String newName = customMealField.getText().trim();
            String type = mealTypeBox.getValue();
            String calText = caloriesField.getText().trim();
            if (selected == null || newName.isEmpty() || type == null || calText.isEmpty()) {
                messageLabel.setText("Select meal & fill new data.");
                return;
            }
            try {
                double newCalories = Double.parseDouble(calText);
                String updateUrl = String.format("http://localhost:8080/api/meals?foodName=%s&calories=%s&mealType=%s",
                        URLEncoder.encode(newName, StandardCharsets.UTF_8),
                        URLEncoder.encode(String.valueOf(newCalories), StandardCharsets.UTF_8),
                        URLEncoder.encode(type, StandardCharsets.UTF_8));
                HttpURLConnection updateConn = (HttpURLConnection) new URL(updateUrl).openConnection();
                updateConn.setRequestMethod("POST");
                updateConn.getInputStream().close();
                updateConn.disconnect();

                mealNames.remove(selected);
                mealNames.add(newName);
                mealCalories.put(newName, newCalories);
                mealTypes.put(newName, type);

                messageLabel.setTextFill(Color.web("#27AE60"));
                messageLabel.setText("Meal updated.");
                mealDropdown.setItems(mealNames);
                mealDropdown.setValue(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Update failed.");
            }
        });

        deleteButton.setOnAction(e -> {
            String selected = mealDropdown.getValue();
            if (selected == null) {
                messageLabel.setText("Select a meal to delete.");
                return;
            }
            try {
                // You need an endpoint to get meal ID by name or use internal state
                messageLabel.setText("Delete functionality requires meal ID or an endpoint for ID lookup.");
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Delete failed.");
            }
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12,
                title, mealDropdown, customMealField, caloriesField,
                mealTypeBox, datePicker,
                logButton, updateButton, deleteButton, backButton, messageLabel);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(25));
        form.setMaxWidth(400);

        VBox layout = new VBox(form);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        stage.setScene(new Scene(layout));
        stage.setTitle("Log Meal");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }

    private void styleInput(Control input) {
        input.setPrefHeight(40);
        input.setMaxWidth(300);
        input.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }

    private void fetchAllMealsFromBackend() {
        try {
            URL url = new URL("http://localhost:8080/api/meals/all");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            InputStream in = conn.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> meals = mapper.readValue(in, new TypeReference<>() {});
            for (Map<String, Object> m : meals) {
                String name = m.get("foodName").toString();
                mealNames.add(name);
                mealCalories.put(name, ((Number) m.get("calories")).doubleValue());
                mealTypes.put(name, m.get("mealType").toString());
            }
            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
