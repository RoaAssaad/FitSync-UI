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
import java.util.List;
import java.util.Map;

public class ViewMealsScreen {

    private final User user;

    public ViewMealsScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();

        Label title = new Label("View Logged Meals");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefSize(300, 40);
        datePicker.setStyle(
                "-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;"
        );

        Button viewButton = new Button("View Meals");
        viewButton.setPrefSize(140, 35);
        viewButton.setStyle(
                "-fx-background-color: #2ECC71; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 8;"
        );

        Button backButton = new Button("Back");
        backButton.setPrefSize(140, 35);
        backButton.setStyle(
                "-fx-background-color: #3498DB; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 8;"
        );
        backButton.setOnAction(e -> {
            new DashboardScreen(user).start(stage);
            stage.setFullScreen(wasFullScreen);
        });

        ListView<String> mealList = new ListView<>();
        mealList.setPrefSize(300, 150);

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        messageLabel.setTextFill(Color.web("#34495E"));

        ObjectMapper mapper = new ObjectMapper();

        viewButton.setOnAction(e -> {
            try {
                LocalDate selectedDate = datePicker.getValue();
                URL url = new URL("http://localhost:8080/api/meals/user/" + user.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                ObservableList<String> items = FXCollections.observableArrayList();
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    List<Map<String, Object>> list = mapper.readValue(in, new TypeReference<>() {
                    });
                    for (Map<String, Object> m : list) {
                        String date = m.get("mealDate").toString().substring(0, 10);
                        if (LocalDate.parse(date).equals(selectedDate)) {
                            items.add(String.format(
                                    "%s (%s): %.0f cal",
                                    m.get("foodName"),
                                    m.get("mealType"),
                                    ((Number) m.get("calories")).doubleValue()
                            ));
                        }
                    }
                }
                conn.disconnect();
                if (items.isEmpty()) {
                    messageLabel.setText("No meals found for this date.");
                    messageLabel.setTextFill(Color.web("#E74C3C"));
                } else {
                    messageLabel.setText("Meals logged:");
                    messageLabel.setTextFill(Color.web("#27AE60"));
                }
                mealList.setItems(items);
            } catch (Exception ex) {
                messageLabel.setText("Error fetching meals.");
                messageLabel.setTextFill(Color.web("#E74C3C"));
            }
        });

        VBox layout = new VBox(12,
                title,
                datePicker,
                viewButton,
                messageLabel,
                mealList,
                backButton
        );
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        stage.setScene(new Scene(layout));
        stage.setTitle("View Meals");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
