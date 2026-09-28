package com.guesstheworld.ui.view;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.Role;
import com.guesstheworld.model.User;
import com.guesstheworld.ui.NavigationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final VBox root;

    public LoginView() {
        root = new VBox();
        root.getStyleClass().addAll("app-container");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        buildUI();
    }

    private void buildUI() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card-panel");
        card.setMaxWidth(400);
        card.setAlignment(Pos.CENTER);

        // Header Title
        Label titleLabel = new Label("GUESS THE WORLD");
        titleLabel.getStyleClass().add("heading-1");

        Label subtitleLabel = new Label("Sign in to your account");
        subtitleLabel.getStyleClass().add("sub-text");

        // Error message label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-wrap-text: true;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Username Field
        VBox usernameBox = new VBox(6);
        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("heading-3");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameBox.getChildren().addAll(userLabel, usernameField);

        // Password Field with Show Password Checkbox
        VBox passwordBox = new VBox(6);
        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("heading-3");

        PasswordField passwordHidden = new PasswordField();
        passwordHidden.setPromptText("Enter password");

        TextField passwordVisible = new TextField();
        passwordVisible.setPromptText("Enter password");
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);

        // Synchronize text between hidden and visible fields
        passwordHidden.textProperty().bindBidirectional(passwordVisible.textProperty());

        StackPane passFieldStack = new StackPane(passwordHidden, passwordVisible);

        CheckBox showPasswordCheck = new CheckBox("Show password");
        showPasswordCheck.setStyle("-fx-font-size: 12px; -fx-text-fill: #787c7e;");
        showPasswordCheck.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                passwordHidden.setVisible(false);
                passwordHidden.setManaged(false);
                passwordVisible.setVisible(true);
                passwordVisible.setManaged(true);
            } else {
                passwordVisible.setVisible(false);
                passwordVisible.setManaged(false);
                passwordHidden.setVisible(true);
                passwordHidden.setManaged(true);
            }
        });

        passwordBox.getChildren().addAll(passLabel, passFieldStack, showPasswordCheck);

        // Login Button
        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().addAll("button", "btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setDefaultButton(true);

        loginBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            String u = usernameField.getText();
            String p = passwordHidden.getText();

            try {
                User user = AppContext.getInstance().getAuthService().login(u, p);
                if (user.getRole() == Role.ADMIN) {
                    NavigationManager.getInstance().showAdminDashboard();
                } else {
                    NavigationManager.getInstance().showPlayerDashboard();
                }
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        });

        // Register Switch Link
        HBox registerBox = new HBox(6);
        registerBox.setAlignment(Pos.CENTER);
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.getStyleClass().add("sub-text");
        Hyperlink registerLink = new Hyperlink("Register");
        registerLink.setStyle("-fx-text-fill: #1a1a1b; -fx-font-weight: 700;");
        registerLink.setOnAction(e -> NavigationManager.getInstance().showRegister());
        registerBox.getChildren().addAll(noAccountLabel, registerLink);

        card.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                errorLabel,
                usernameBox,
                passwordBox,
                loginBtn,
                registerBox
        );

        root.getChildren().add(card);
    }

    public Parent getView() {
        return root;
    }
}
