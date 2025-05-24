package org.example.fitsyncui.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DailySummaryScreen {

    private final User user;

    public DailySummaryScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        Label title = new Label("Your Daily Summary");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2C3E50"));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        styleInput(datePicker);

        Button viewButton = new Button("View Summary");
        styleButton(viewButton, "#2ECC71");

        Button backButton = new Button("Back");
        styleButton(backButton, "#3498DB");

        Label caloriesInLabel = new Label("Calories Consumed: -");
        Label caloriesOutLabel = new Label("Calories Burned: -");
        for (Label label : new Label[]{caloriesInLabel, caloriesOutLabel}) {
            label.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            label.setTextFill(Color.web("#34495E"));
        }

        viewButton.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            double totalIn = 0;
            double totalOut = 0;
            try {
                ObjectMapper mapper = new ObjectMapper();
                URL mealsUrl = new URL("http://localhost:8080/api/meals/user/" + user.getId());
                HttpURLConnection mealsConn = (HttpURLConnection) mealsUrl.openConnection();
                mealsConn.setRequestMethod("GET");
                mealsConn.setRequestProperty("Accept", "application/json");
                if (mealsConn.getResponseCode() == 200) {
                    InputStream in = mealsConn.getInputStream();
                    List<Map<String, Object>> meals = mapper.readValue(in, new TypeReference<>() {
                    });
                    for (Map<String, Object> m : meals) {
                        String dateStr = m.containsKey("mealDate") ? m.get("mealDate").toString() : m.get("meal_date").toString();
                        if (dateStr.startsWith(selectedDate.toString())) {
                            totalIn += ((Number) m.get("calories")).doubleValue();
                        }
                    }
                }
                mealsConn.disconnect();

                URL workoutsUrl = new URL("http://localhost:8080/api/workouts/user/" + user.getId());
                HttpURLConnection workoutsConn = (HttpURLConnection) workoutsUrl.openConnection();
                workoutsConn.setRequestMethod("GET");
                workoutsConn.setRequestProperty("Accept", "application/json");
                if (workoutsConn.getResponseCode() == 200) {
                    InputStream in = workoutsConn.getInputStream();
                    List<Map<String, Object>> workouts = mapper.readValue(in, new TypeReference<>() {
                    });
                    for (Map<String, Object> m : workouts) {
                        String dateStr = m.containsKey("completionDate") ? m.get("completionDate").toString() : m.get("completion_date").toString();
                        if (dateStr.startsWith(selectedDate.toString())) {
                            totalOut += ((Number) m.get("duration")).doubleValue();
                        }
                    }
                }
                workoutsConn.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            caloriesInLabel.setText("Calories Consumed: " + (int) totalIn);
            caloriesOutLabel.setText("Calories Burned: " + (int) totalOut);
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(14,
                title,
                datePicker,
                viewButton,
                caloriesInLabel,
                caloriesOutLabel,
                backButton
        );
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.setPadding(new Insets(25));
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setTitle("Daily Summary");
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
