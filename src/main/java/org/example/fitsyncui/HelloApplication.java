package org.example.fitsyncui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.fitsyncui.ui.LoginScreen;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        new LoginScreen().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

