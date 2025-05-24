package org.example.fitsyncui.ui;

import org.example.fitsyncui.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        ObjectMapper mapper = new ObjectMapper();
        try {
            URL url = new URL("http://localhost:8080/api/daily-summary/user/" + user.getId());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept","application/json");
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                List<Map<String,Object>> list = mapper.readValue(in, new TypeReference<>() {});
                LocalDate today = LocalDate.now();
                LocalDate weekAgo = today.minusDays(6);
                for (Map<String,Object> m : list) {
                    LocalDate d = LocalDate.parse(m.get("date").toString().substring(0,10));
                    if (!d.isBefore(weekAgo) && !d.isAfter(today)) {
                        consumedSeries.getData().add(new XYChart.Data<>(
                                d.toString(),
                                ((Number)m.get("caloriesConsumed")).doubleValue()
                        ));
                        burnedSeries.getData().add(new XYChart.Data<>(
                                d.toString(),
                                ((Number)m.get("caloriesBurned")).doubleValue()
                        ));
                    }
                }
            }
            conn.disconnect();
        } catch (Exception ignored) {
        }

        barChart.getData().addAll(consumedSeries, burnedSeries);

        Button backButton = new Button("Back");
        backButton.setPrefSize(160,35);
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
