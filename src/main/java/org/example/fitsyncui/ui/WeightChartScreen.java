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
import java.util.List;
import java.util.Map;

public class WeightChartScreen {
    private final User user;

    public WeightChartScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Weight (kg)");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Weight Progress Over Time");
        chart.setLegendVisible(false);
        chart.setCreateSymbols(true);
        chart.setStyle("-fx-background-color: #FDFEFE;");
        chart.setMaxWidth(600);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            URL url = new URL("http://localhost:8080/api/weights/" + user.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                List<Map<String, Object>> list = mapper.readValue(in, new TypeReference<>() {});
                list.sort((a, b) -> a.get("date").toString().compareTo(b.get("date").toString()));
                for (Map<String, Object> m : list) {
                    String date = m.get("date").toString().substring(0, 10);
                    double weight = ((Number) m.get("weight")).doubleValue();
                    series.getData().add(new XYChart.Data<>(date, weight));
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        chart.getData().add(series);

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

        VBox layout = new VBox(20, chart, backButton);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        stage.setScene(new Scene(layout));
        stage.setTitle("Weight Chart");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
