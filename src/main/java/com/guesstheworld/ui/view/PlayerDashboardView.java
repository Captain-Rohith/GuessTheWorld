package com.guesstheworld.ui.view;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.GameSession;
import com.guesstheworld.model.User;
import com.guesstheworld.ui.NavigationManager;
import com.guesstheworld.ui.component.StatCard;
import com.guesstheworld.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PlayerDashboardView {

    private final BorderPane root;

    public PlayerDashboardView() {
        root = new BorderPane();
        root.getStyleClass().add("app-container");

        buildUI();
    }

    private void buildUI() {
        User currentUser = AppContext.getInstance().getAuthService().getCurrentUser();
        if (currentUser == null) return;

        // Top Bar
        HBox topBar = new HBox(15);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label logoLabel = new Label("GUESS THE WORLD");
        logoLabel.getStyleClass().add("heading-2");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userBadge = new Label("User: " + currentUser.getUsername() + " (Player)");
        userBadge.getStyleClass().addAll("badge-text", "badge-info");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().addAll("button", "btn-secondary");
        logoutBtn.setOnAction(e -> NavigationManager.getInstance().showLogin());

        topBar.getChildren().addAll(logoLabel, spacer, userBadge, logoutBtn);
        root.setTop(topBar);

        // Center Content Box (Centered horizontally when window is maximized)
        VBox content = new VBox(20);
        content.setMaxWidth(860);
        content.setAlignment(Pos.TOP_CENTER);

        int playedToday = AppContext.getInstance().getGamePlayService().getWordsPlayedTodayCount(currentUser.getId());
        int remainingToday = AppContext.getInstance().getGamePlayService().getRemainingGamesToday(currentUser.getId());
        Optional<GameSession> activeSessionOpt = AppContext.getInstance().getGamePlayService().getActiveSession(currentUser.getId());

        // Header Panel
        VBox bannerBox = new VBox(10);
        bannerBox.getStyleClass().add("card-panel");
        bannerBox.setAlignment(Pos.CENTER_LEFT);

        Label welcomeTitle = new Label("Welcome, " + currentUser.getUsername());
        welcomeTitle.getStyleClass().add("heading-1");

        Label welcomeSub = new Label("Guess the 5-letter hidden word in 5 attempts or less (Max 3 words per day).");
        welcomeSub.getStyleClass().add("sub-text");

        // Action Button
        Button playBtn = new Button();
        playBtn.getStyleClass().addAll("button", "btn-primary");

        if (activeSessionOpt.isPresent()) {
            playBtn.setText("Continue Active Game (Attempt " + (activeSessionOpt.get().getAttempts().size() + 1) + "/5)");
            playBtn.setOnAction(e -> NavigationManager.getInstance().showGameBoard());
        } else if (remainingToday > 0) {
            playBtn.setText("Start New Word Game (" + remainingToday + " remaining today)");
            playBtn.setOnAction(e -> {
                try {
                    AppContext.getInstance().getGamePlayService().startNewGame(currentUser.getId());
                    NavigationManager.getInstance().showGameBoard();
                } catch (Exception ex) {
                    AlertUtil.showError("Game Limit", "Cannot Start Game", ex.getMessage());
                }
            });
        } else {
            playBtn.setText("Daily Limit Reached (3 of 3 words completed today)");
            playBtn.setDisable(true);
            playBtn.getStyleClass().remove("btn-primary");
            playBtn.getStyleClass().add("btn-secondary");
        }

        bannerBox.getChildren().addAll(welcomeTitle, welcomeSub, playBtn);

        // Stats Row
        HBox statsRow = new HBox(16);
        statsRow.setAlignment(Pos.CENTER);

        StatCard cardPlayed = new StatCard("Played Today", playedToday + " / 3", "#1a1a1b");
        StatCard cardRemaining = new StatCard("Remaining Quota", remainingToday + " / 3", remainingToday > 0 ? "#6aaa64" : "#dc2626");

        List<GameSession> todaySessions = AppContext.getInstance().getGameSessionRepository().findSessionsForUserOnDate(currentUser.getId(), LocalDate.now());
        long wonCount = todaySessions.stream().filter(s -> s.getStatus().name().equals("WON")).count();
        StatCard cardWon = new StatCard("Correct Guesses", wonCount + "", "#6aaa64");

        statsRow.getChildren().addAll(cardPlayed, cardRemaining, cardWon);

        // Today's Game Table
        VBox tableBox = new VBox(10);
        tableBox.getStyleClass().add("card-panel");

        Label tableTitle = new Label("Today's Game Sessions");
        tableTitle.getStyleClass().add("heading-2");

        TableView<GameSession> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(200);

        TableColumn<GameSession, String> sessionCol = new TableColumn<>("Session ID");
        sessionCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));

        TableColumn<GameSession, String> wordCol = new TableColumn<>("Target Word");
        wordCol.setCellValueFactory(cell -> {
            GameSession s = cell.getValue();
            if (s.getStatus().name().equals("IN_PROGRESS")) {
                return new SimpleStringProperty("Hidden");
            }
            return new SimpleStringProperty(s.getTargetWord());
        });

        TableColumn<GameSession, String> attemptsCol = new TableColumn<>("Attempts Used");
        attemptsCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAttemptsUsed() + " / 5"));

        TableColumn<GameSession, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus().name()));

        tableView.getColumns().addAll(sessionCol, wordCol, attemptsCol, statusCol);
        tableView.setItems(FXCollections.observableArrayList(todaySessions));

        tableBox.getChildren().addAll(tableTitle, tableView);

        // Rules Guide Card
        VBox rulesCard = new VBox(8);
        rulesCard.getStyleClass().add("card-panel");
        Label rulesTitle = new Label("Color Rules:");
        rulesTitle.getStyleClass().add("heading-3");

        Label greenDesc = new Label("• GREEN: Letter is correct and in the right position.");
        greenDesc.setStyle("-fx-text-fill: #538d4e; -fx-font-weight: 600;");

        Label orangeDesc = new Label("• ORANGE / YELLOW: Letter is in the word but in the wrong position.");
        orangeDesc.setStyle("-fx-text-fill: #b59f3b; -fx-font-weight: 600;");

        Label greyDesc = new Label("• GREY: Letter is not in the word.");
        greyDesc.setStyle("-fx-text-fill: #787c7e; -fx-font-weight: 600;");

        rulesCard.getChildren().addAll(rulesTitle, greenDesc, orangeDesc, greyDesc);

        content.getChildren().addAll(bannerBox, statsRow, tableBox, rulesCard);

        // Centering Wrapper for maximized screens
        StackPane centerWrapper = new StackPane(content);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(24));

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #ffffff; -fx-background-color: #ffffff;");
        root.setCenter(scrollPane);
    }

    public Parent getView() {
        return root;
    }
}
