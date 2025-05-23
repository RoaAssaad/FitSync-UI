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

public class WeightChartScreen {

    public WeightChartScreen() {
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen(); // save fullscreen state

        // Axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Weight (kg)");

        // Line chart setup
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Weight Progress Over Time");
        chart.setLegendVisible(false);
        chart.setCreateSymbols(true);
        chart.setStyle("-fx-background-color: #FDFEFE;");
        chart.setMaxWidth(600);

        // Series with mocked data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        LocalDate today = LocalDate.now();
        Random rnd = new Random();
        double baseWeight = 75.0;
        for (int i = 9; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            double w = baseWeight + Math.sin(i) * 0.8;  // mock fluctuation
            series.getData().add(new XYChart.Data<>(d.toString(), w));
        }
        chart.getData().add(series);

        // Back button
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

        // Layout
        VBox layout = new VBox(20, chart, backButton);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        // Scene & show
        stage.setTitle("Weight Chart");
        stage.setScene(new Scene(layout));
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
