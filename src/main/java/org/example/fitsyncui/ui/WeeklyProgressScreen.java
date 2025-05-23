package org.example.fitsyncui.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Random;

public class WeeklyProgressScreen {

    public WeeklyProgressScreen() {
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen(); // remember fullscreen state

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
        barChart.setMaxWidth(600);

        XYChart.Series<String, Number> consumedSeries = new XYChart.Series<>();
        consumedSeries.setName("Calories Consumed");
        XYChart.Series<String, Number> burnedSeries = new XYChart.Series<>();
        burnedSeries.setName("Calories Burned");

        LocalDate today = LocalDate.now();
        Random rnd = new Random();
        // Mock data for last 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            int in = 1800 + rnd.nextInt(401); // 1800–2200
            int out = 300 + rnd.nextInt(301); // 300–600
            consumedSeries.getData().add(new XYChart.Data<>(dateStr, in));
            burnedSeries.getData().add(new XYChart.Data<>(dateStr, out));
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
            new DashboardScreen().start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        VBox layout = new VBox(20, barChart, backButton);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        Scene scene = new Scene(layout);
        stage.setTitle("Weekly Progress");
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
