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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class LogMealScreen {
    private final User user;
    private final ObservableList<String> mealNames = FXCollections.observableArrayList();
    private final Map<String, Integer> mealIds = new HashMap<>();
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

        ComboBox<String> mealTypeBox = new ComboBox<>(FXCollections.observableArrayList("Breakfast", "Lunch", "Dinner", "Snack"));
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
                int mealId = createMeal(name, calories, type); // Always create new
                logMeal(mealId, date);

                mealNames.add(name);
                mealCalories.put(name, calories);
                mealTypes.put(name, type);
                mealDropdown.setItems(mealNames);

                messageLabel.setTextFill(Color.web("#27AE60"));
                messageLabel.setText("Meal logged successfully!");
                resetInputs(mealDropdown, customMealField, caloriesField, mealTypeBox);
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
                int id = mealIds.getOrDefault(selected, -1);
                if (id == -1) {
                    messageLabel.setText("Meal ID not found.");
                    return;
                }

                String url = String.format("http://localhost:8080/api/meals/update?id=%d&foodName=%s&calories=%s&mealType=%s",
                        id,
                        URLEncoder.encode(newName, StandardCharsets.UTF_8),
                        URLEncoder.encode(String.valueOf(newCalories), StandardCharsets.UTF_8),
                        URLEncoder.encode(type, StandardCharsets.UTF_8));
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("PUT");
                conn.getInputStream().close();
                conn.disconnect();

                mealNames.remove(selected);
                mealNames.add(newName);
                mealCalories.put(newName, newCalories);
                mealTypes.put(newName, type);
                mealDropdown.setItems(mealNames);
                mealDropdown.setValue(null);

                messageLabel.setTextFill(Color.web("#27AE60"));
                messageLabel.setText("Meal updated.");
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
                int id = mealIds.getOrDefault(selected, -1);
                if (id == -1) {
                    messageLabel.setText("Meal ID not found.");
                    return;
                }

                String url = "http://localhost:8080/api/meals/delete/" + id;
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("DELETE");
                conn.getInputStream().close();
                conn.disconnect();

                mealNames.remove(selected);
                mealIds.remove(selected);
                mealCalories.remove(selected);
                mealTypes.remove(selected);
                mealDropdown.setItems(mealNames);
                mealDropdown.setValue(null);

                messageLabel.setTextFill(Color.web("#27AE60"));
                messageLabel.setText("Meal deleted.");
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
                int id = (int) m.get("id");
                mealIds.put(name, id);
                mealNames.add(name);
                mealCalories.put(name, ((Number) m.get("calories")).doubleValue());
                mealTypes.put(name, m.get("mealType").toString());
            }
            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int createMeal(String name, double cal, String type) throws Exception {
        String url = String.format("http://localhost:8080/api/meals/create?foodName=%s&calories=%s&mealType=%s",
                URLEncoder.encode(name, StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(cal), StandardCharsets.UTF_8),
                URLEncoder.encode(type, StandardCharsets.UTF_8));
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        InputStream in = conn.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> meal = mapper.readValue(in, new TypeReference<>() {});
        conn.disconnect();
        return (int) meal.get("id");
    }

    private void logMeal(int mealId, LocalDate date) throws Exception {
        String url = String.format("http://localhost:8080/api/meals/log?userId=%d&mealId=%d&date=%s",
                user.getId(), mealId, date);
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.getInputStream().close();
        conn.disconnect();
    }

    private void resetInputs(ComboBox<String> mealDropdown, TextField custom, TextField calories, ComboBox<String> type) {
        mealDropdown.setValue(null);
        custom.clear();
        calories.clear();
        type.setValue(null);
    }
}
