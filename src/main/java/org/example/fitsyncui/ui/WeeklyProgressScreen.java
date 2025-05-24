package org.example.fitsyncui.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.fitsyncui.model.User;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class WeeklyProgressScreen {
    private final User user;

    public WeeklyProgressScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Calories");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Weekly Calorie Summary");
        barChart.setLegendVisible(true);
        barChart.setCategoryGap(10);
        barChart.setBarGap(4);
        barChart.setStyle("-fx-background-color: #FDFEFE;");

        XYChart.Series<String, Number> consumedSeries = new XYChart.Series<>();
        consumedSeries.setName("Calories Consumed");

        XYChart.Series<String, Number> burnedSeries = new XYChart.Series<>();
        burnedSeries.setName("Calories Burned");

        try {
            URL url = new URL("http://localhost:8080/api/v1/weekly-progress?userId=" + user.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                List<Map<String, Object>> progressList = new ObjectMapper().readValue(in, new TypeReference<>() {});
                for (Map<String, Object> entry : progressList) {
                    String date = entry.get("date").toString();
                    double calIn = ((Number) entry.get("caloriesConsumed")).doubleValue();
                    double calOut = ((Number) entry.get("caloriesBurned")).doubleValue();

                    consumedSeries.getData().add(new XYChart.Data<>(date, calIn));
                    burnedSeries.getData().add(new XYChart.Data<>(date, calOut));
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        barChart.getData().addAll(consumedSeries, burnedSeries);

        Button backButton = new Button("Back");
        backButton.setPrefSize(160, 35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );
        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(20, barChart, backButton);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        stage.setScene(new Scene(layout));
        stage.setTitle("Weekly Progress");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
