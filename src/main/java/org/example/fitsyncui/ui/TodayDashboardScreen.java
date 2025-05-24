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

public class TodayDashboardScreen {
    private final User user;

    public TodayDashboardScreen(User user) {
        this.user = user;
    }

    public void start(Stage stage) {
        boolean wasFullScreen = stage.isFullScreen();
        LocalDate today = LocalDate.now();

        Label title = new Label("Today at a Glance - " + today);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2C3E50"));

        Label caloriesInLabel = new Label();
        Label caloriesOutLabel = new Label();
        Label netCaloriesLabel = new Label();
        Label intakeGoalLabel = new Label();
        Label burnGoalLabel = new Label();
        Label intakeStatus = new Label();
        Label burnStatus = new Label();

        ObjectMapper mapper = new ObjectMapper();
        double sumIn = 0, sumOut = 0, inGoal = 0, burnGoal = 0;

        try {
            URL mealsUrl = new URL("http://localhost:8080/api/meals/user/" + user.getId());
            HttpURLConnection mConn = (HttpURLConnection) mealsUrl.openConnection();
            mConn.setRequestMethod("GET");
            mConn.setRequestProperty("Accept", "application/json");
            if (mConn.getResponseCode() == 200) {
                InputStream in = mConn.getInputStream();
                List<Map<String, Object>> meals = mapper.readValue(in, new TypeReference<>() {
                });
                ObservableList<String> mList = FXCollections.observableArrayList();
                for (Map<String, Object> m : meals) {
                    String date = m.get("mealDate").toString().substring(0, 10);
                    if (date.equals(today.toString())) {
                        double c = ((Number) m.get("calories")).doubleValue();
                        sumIn += c;
                        mList.add(String.format("%s (%s) - %.0f cal",
                                m.get("foodName"), m.get("mealType"), c));
                    }
                }
                ListView<String> mealsList = new ListView<>(mList);
                mealsList.setPrefHeight(100);
                mealsList.setId("mealsList");
                mConn.disconnect();
                // temporarily store list in label ID to retrieve below
                caloriesInLabel.setUserData(mealsList);
            }
        } catch (Exception ignored) {
        }

        try {
            URL workoutsUrl = new URL("http://localhost:8080/api/workouts/user/" + user.getId());
            HttpURLConnection wConn = (HttpURLConnection) workoutsUrl.openConnection();
            wConn.setRequestMethod("GET");
            wConn.setRequestProperty("Accept", "application/json");
            if (wConn.getResponseCode() == 200) {
                InputStream in = wConn.getInputStream();
                List<Map<String, Object>> wList = mapper.readValue(in, new TypeReference<>() {
                });
                ObservableList<String> workList = FXCollections.observableArrayList();
                for (Map<String, Object> w : wList) {
                    String date = w.get("completionDate").toString().substring(0, 10);
                    if (date.equals(today.toString())) {
                        double d = ((Number) w.get("duration")).doubleValue();
                        sumOut += d;
                        workList.add(w.get("name").toString());
                    }
                }
                ListView<String> workoutsList = new ListView<>(workList);
                workoutsList.setPrefHeight(100);
                workoutsList.setId("workoutsList");
                wConn.disconnect();
                caloriesOutLabel.setUserData(workoutsList);
            }
        } catch (Exception ignored) {
        }

        try {
            URL goalsUrl = new URL("http://localhost:8080/api/goals/user/" + user.getId());
            HttpURLConnection gConn = (HttpURLConnection) goalsUrl.openConnection();
            gConn.setRequestMethod("GET");
            gConn.setRequestProperty("Accept", "application/json");
            if (gConn.getResponseCode() == 200) {
                InputStream in = gConn.getInputStream();
                Map<String, Object> g = mapper.readValue(in, new TypeReference<>() {
                });
                inGoal = ((Number) g.get("caloriesInGoal")).doubleValue();
                burnGoal = ((Number) g.get("caloriesBurnGoal")).doubleValue();
            }
            gConn.disconnect();
        } catch (Exception ignored) {
        }

        caloriesInLabel.setText("Calories Consumed: " + (int) sumIn);
        caloriesOutLabel.setText("Calories Burned: " + (int) sumOut);
        netCaloriesLabel.setText("Net Calories: " + (int) (sumIn - sumOut));

        intakeGoalLabel.setText("Intake Goal: " + (int) inGoal);
        burnGoalLabel.setText("Burn Goal: " + (int) burnGoal);

        intakeStatus.setText(sumIn >= inGoal
                ? "You reached your intake goal!"
                : String.format("%.0f cal left to reach intake goal.", inGoal - sumIn)
        );
        burnStatus.setText(sumOut >= burnGoal
                ? "You hit your burn goal!"
                : String.format("%.0f cal left to burn.", burnGoal - sumOut)
        );

        ListView<String> mealsList = (ListView<String>) caloriesInLabel.getUserData();
        ListView<String> workoutsList = (ListView<String>) caloriesOutLabel.getUserData();

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

        for (Label lbl : new Label[]{
                caloriesInLabel, caloriesOutLabel, netCaloriesLabel,
                intakeGoalLabel, burnGoalLabel, intakeStatus, burnStatus
        }) {
            lbl.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            lbl.setTextFill(Color.web("#34495E"));
        }
        caloriesInLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        caloriesOutLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        netCaloriesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        intakeStatus.setTextFill(Color.web("#27AE60"));
        burnStatus.setTextFill(Color.web("#27AE60"));

        Label mealsTitle = new Label("Meals Logged:");
        mealsTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        mealsTitle.setTextFill(Color.web("#34495E"));
        Label workoutsTitle = new Label("Workouts Logged:");
        workoutsTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        workoutsTitle.setTextFill(Color.web("#34495E"));

        VBox layout = new VBox(12,
                title,
                caloriesInLabel, caloriesOutLabel, netCaloriesLabel,
                intakeGoalLabel, intakeStatus,
                burnGoalLabel, burnStatus,
                new Separator(),
                mealsTitle, mealsList,
                workoutsTitle, workoutsList,
                backButton
        );
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #FDFEFE;");
        layout.prefWidthProperty().bind(stage.widthProperty());
        layout.prefHeightProperty().bind(stage.heightProperty());

        stage.setScene(new Scene(layout));
        stage.setTitle("Today at a Glance");
        stage.setFullScreen(wasFullScreen);
        stage.show();
    }
}
