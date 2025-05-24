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

        try {
            URL url = new URL("http://localhost:8080/api/meals/user/" + user.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> list = mapper.readValue(in, new TypeReference<>() {
                });
                for (Map<String, Object> m : list) {
                    String name = m.get("foodName").toString();
                    mealNames.add(name);
                    mealCalories.put(name, ((Number) m.get("calories")).doubleValue());
                    mealTypes.put(name, m.get("mealType").toString());
                }
            }
            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
        logButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        updateButton.setStyle("-fx-background-color: #F1C40F; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        backButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

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
            if (name.isEmpty() || type == null || calText.isEmpty()) {
                messageLabel.setText("Please complete all fields.");
                return;
            }
            double cal;
            try {
                cal = Double.parseDouble(calText);
            } catch (NumberFormatException ex) {
                messageLabel.setText("Invalid calories input.");
                return;
            }
            if (!mealNames.contains(name)) mealNames.add(name);
            mealCalories.put(name, cal);
            mealTypes.put(name, type);
            messageLabel.setTextFill(Color.web("#27AE60"));
            messageLabel.setText("Meal logged (mocked)!");
            mealDropdown.setItems(mealNames);
            mealDropdown.setValue(null);
            customMealField.clear();
            caloriesField.clear();
            mealTypeBox.setValue(null);
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
            double newCal;
            try {
                newCal = Double.parseDouble(calText);
            } catch (NumberFormatException ex) {
                messageLabel.setText("Invalid calories input.");
                return;
            }
            mealNames.remove(selected);
            mealNames.add(newName);
            mealCalories.remove(selected);
            mealTypes.remove(selected);
            mealCalories.put(newName, newCal);
            mealTypes.put(newName, type);
            messageLabel.setTextFill(Color.web("#27AE60"));
            messageLabel.setText("Meal updated (mocked).");
            mealDropdown.setItems(mealNames);
            mealDropdown.setValue(null);
        });

        deleteButton.setOnAction(e -> {
            String selected = mealDropdown.getValue();
            if (selected == null) {
                messageLabel.setText("Select a meal to delete.");
                return;
            }
            mealNames.remove(selected);
            mealCalories.remove(selected);
            mealTypes.remove(selected);
            messageLabel.setTextFill(Color.web("#27AE60"));
            messageLabel.setText("Meal deleted (mocked).");
            mealDropdown.setItems(mealNames);
            mealDropdown.setValue(null);
        });

        backButton.setOnAction(e -> {
            stage.setFullScreen(wasFullScreen);
        });

        VBox form = new VBox(12,
                title,
                mealDropdown,
                customMealField,
                caloriesField,
                mealTypeBox,
                datePicker,
                logButton,
                updateButton,
                deleteButton,
                backButton,
                messageLabel
        );
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);
        form.setPadding(new Insets(25));

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
        input.setStyle(
                "-fx-background-color: #ECF0F1; " +
                        "-fx-border-color: #BDC3C7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
    }
}
