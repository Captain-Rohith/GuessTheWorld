package com.guesstheworld;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.ui.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize AppContext and Database
        AppContext.getInstance();

        // Initialize Navigation Manager
        NavigationManager.getInstance().init(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
