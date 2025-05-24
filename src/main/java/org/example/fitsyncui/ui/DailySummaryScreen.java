package org.example.fitsyncui.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fitsyncui.model.User;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
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
            if (selectedDate == null) return;

            try {
                ObjectMapper mapper = new ObjectMapper();

                // Trigger calculation
                URL postUrl = new URL("http://localhost:8080/api/v1/summary/calculate?userId=" + user.getId() + "&date=" + selectedDate);
                HttpURLConnection postConn = (HttpURLConnection) postUrl.openConnection();
                postConn.setRequestMethod("POST");
                postConn.setDoOutput(true);
                postConn.setRequestProperty("Accept", "application/json");
                postConn.getResponseCode(); // trigger

                // Then fetch summary
                URL getUrl = new URL("http://localhost:8080/api/v1/summary?userId=" + user.getId() + "&date=" + selectedDate);
                HttpURLConnection getConn = (HttpURLConnection) getUrl.openConnection();
                getConn.setRequestMethod("GET");
                getConn.setRequestProperty("Accept", "application/json");

                if (getConn.getResponseCode() == 200) {
                    InputStream in = getConn.getInputStream();
                    Map<String, Object> result = mapper.readValue(in, Map.class);
                    double caloriesIn = ((Number) result.getOrDefault("caloriesConsumed", 0)).doubleValue();
                    double caloriesOut = ((Number) result.getOrDefault("caloriesBurned", 0)).doubleValue();
                    caloriesInLabel.setText("Calories Consumed: " + (int) caloriesIn);
                    caloriesOutLabel.setText("Calories Burned: " + (int) caloriesOut);
                } else {
                    caloriesInLabel.setText("Calories Consumed: -");
                    caloriesOutLabel.setText("Calories Burned: -");
                }

                getConn.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(14, title, datePicker, viewButton, caloriesInLabel, caloriesOutLabel, backButton);
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
        control.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    private void styleButton(Button button, String color) {
        button.setPrefWidth(160);
        button.setPrefHeight(35);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    }
}
