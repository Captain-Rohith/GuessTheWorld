package com.guesstheworld.ui.view;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.Role;
import com.guesstheworld.model.User;
import com.guesstheworld.ui.NavigationManager;
import com.guesstheworld.util.AlertUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RegisterView {

    private final ScrollPane scrollPane;
    private final VBox root;

    public RegisterView() {
        root = new VBox();
        root.getStyleClass().addAll("app-container");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #ffffff; -fx-background-color: #ffffff;");

        buildUI();
    }

    private void buildUI() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card-panel");
        card.setMaxWidth(420);
        card.setAlignment(Pos.CENTER);

        // Header Title
        Label titleLabel = new Label("Register Player Account");
        titleLabel.getStyleClass().add("heading-1");

        Label subtitleLabel = new Label("Create an account to play the guessing game");
        subtitleLabel.getStyleClass().add("sub-text");

        // Error message label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-wrap-text: true;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Username Field
        VBox usernameBox = new VBox(4);
        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("heading-3");
        TextField usernameField = new TextField();
        usernameField.setPromptText("At least 5 letters (upper and lower case)");
        Label userHint = new Label("Must have at least 5 letters with upper and lower case");
        userHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #787c7e;");
        usernameBox.getChildren().addAll(userLabel, usernameField, userHint);

        // Password Field with View Checkbox
        VBox passwordBox = new VBox(4);
        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("heading-3");

        PasswordField passwordHidden = new PasswordField();
        passwordHidden.setPromptText("Min 5 chars (alpha, numeric, and $, %, *, &)");

        TextField passwordVisible = new TextField();
        passwordVisible.setPromptText("Min 5 chars (alpha, numeric, and $, %, *, &)");
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);

        passwordHidden.textProperty().bindBidirectional(passwordVisible.textProperty());

        StackPane passStack = new StackPane(passwordHidden, passwordVisible);

        Label passHint = new Label("Min 5 chars, with alpha, numeric and special char ($, %, *, &)");
        passHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #787c7e;");
        passwordBox.getChildren().addAll(passLabel, passStack, passHint);

        // Confirm Password Field
        VBox confirmPasswordBox = new VBox(4);
        Label confirmPassLabel = new Label("Confirm Password");
        confirmPassLabel.getStyleClass().add("heading-3");

        PasswordField confirmHidden = new PasswordField();
        confirmHidden.setPromptText("Re-type password");

        TextField confirmVisible = new TextField();
        confirmVisible.setPromptText("Re-type password");
        confirmVisible.setVisible(false);
        confirmVisible.setManaged(false);

        confirmHidden.textProperty().bindBidirectional(confirmVisible.textProperty());

        StackPane confirmStack = new StackPane(confirmHidden, confirmVisible);
        confirmPasswordBox.getChildren().addAll(confirmPassLabel, confirmStack);

        // View/Eye Checkbox to toggle password visibility for both fields
        CheckBox showPasswordCheck = new CheckBox("Show passwords");
        showPasswordCheck.setStyle("-fx-font-size: 12px; -fx-text-fill: #787c7e;");
        showPasswordCheck.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                passwordHidden.setVisible(false);
                passwordHidden.setManaged(false);
                passwordVisible.setVisible(true);
                passwordVisible.setManaged(true);

                confirmHidden.setVisible(false);
                confirmHidden.setManaged(false);
                confirmVisible.setVisible(true);
                confirmVisible.setManaged(true);
            } else {
                passwordVisible.setVisible(false);
                passwordVisible.setManaged(false);
                passwordHidden.setVisible(true);
                passwordHidden.setManaged(true);

                confirmVisible.setVisible(false);
                confirmVisible.setManaged(false);
                confirmHidden.setVisible(true);
                confirmHidden.setManaged(true);
            }
        });

        // Register Button
        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().addAll("button", "btn-primary");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setDefaultButton(true);

        registerBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            String username = usernameField.getText();
            String password = passwordHidden.getText();
            String confirmPass = confirmHidden.getText();

            if (!password.equals(confirmPass)) {
                errorLabel.setText("Passwords do not match.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            try {
                User created = AppContext.getInstance().getAuthService().register(username, password, Role.PLAYER);
                AlertUtil.showInfo(
                        "Registration Successful",
                        "Account Created",
                        "Player user '" + created.getUsername() + "' has been registered successfully."
                );
                NavigationManager.getInstance().showLogin();
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        });

        // Back to Login Switch
        HBox loginBox = new HBox(6);
        loginBox.setAlignment(Pos.CENTER);
        Label haveAccountLabel = new Label("Already have an account?");
        haveAccountLabel.getStyleClass().add("sub-text");
        Hyperlink loginLink = new Hyperlink("Sign In");
        loginLink.setStyle("-fx-text-fill: #1a1a1b; -fx-font-weight: 700;");
        loginLink.setOnAction(e -> NavigationManager.getInstance().showLogin());
        loginBox.getChildren().addAll(haveAccountLabel, loginLink);

        card.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                errorLabel,
                usernameBox,
                passwordBox,
                confirmPasswordBox,
                showPasswordCheck,
                registerBtn,
                loginBox
        );

        StackPane centerWrapper = new StackPane(card);
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(30));

        scrollPane.setContent(centerWrapper);
    }

    public Parent getView() {
        return scrollPane;
    }
}
