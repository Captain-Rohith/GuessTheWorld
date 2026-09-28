package com.guesstheworld.ui;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.Role;
import com.guesstheworld.model.User;
import com.guesstheworld.ui.view.AdminDashboardView;
import com.guesstheworld.ui.view.GameBoardView;
import com.guesstheworld.ui.view.LoginView;
import com.guesstheworld.ui.view.PlayerDashboardView;
import com.guesstheworld.ui.view.RegisterView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationManager {

    private static NavigationManager instance;
    private Stage primaryStage;
    private Scene mainScene;

    private NavigationManager() {
    }

    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
        showLogin();
    }

    public void setRoot(Parent root) {
        if (mainScene == null) {
            mainScene = new Scene(root, 1024, 768);
            mainScene.getStylesheets().add(
                    getClass().getResource("/styles/app.css").toExternalForm()
            );
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Guess The World - Wordle Challenge");
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(650);
            primaryStage.show();
        } else {
            mainScene.setRoot(root);
        }
    }

    public void showLogin() {
        AppContext.getInstance().getAuthService().logout();
        setRoot(new LoginView().getView());
    }

    public void showRegister() {
        setRoot(new RegisterView().getView());
    }

    public void showPlayerDashboard() {
        User user = AppContext.getInstance().getAuthService().getCurrentUser();
        if (user == null) {
            showLogin();
            return;
        }
        setRoot(new PlayerDashboardView().getView());
    }

    public void showGameBoard() {
        User user = AppContext.getInstance().getAuthService().getCurrentUser();
        if (user == null) {
            showLogin();
            return;
        }
        setRoot(new GameBoardView().getView());
    }

    public void showAdminDashboard() {
        User user = AppContext.getInstance().getAuthService().getCurrentUser();
        if (user == null || user.getRole() != Role.ADMIN) {
            showLogin();
            return;
        }
        setRoot(new AdminDashboardView().getView());
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
