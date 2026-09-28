package com.guesstheworld.ui.view;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.GameSession;
import com.guesstheworld.model.GuessAttempt;
import com.guesstheworld.model.LetterEvaluation;
import com.guesstheworld.model.User;
import com.guesstheworld.service.GuessResult;
import com.guesstheworld.ui.NavigationManager;
import com.guesstheworld.ui.component.GameGrid;
import com.guesstheworld.ui.component.VirtualKeyboard;
import com.guesstheworld.util.AlertUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class GameBoardView {

    private final BorderPane root;
    private GameGrid gameGrid;
    private VirtualKeyboard keyboard;
    private Label statusLabel;
    private Label attemptsBadge;
    private StringBuilder currentInput = new StringBuilder();
    private GameSession currentSession;

    public GameBoardView() {
        root = new BorderPane();
        root.getStyleClass().add("app-container");

        buildUI();
        loadActiveGame();
    }

    private void buildUI() {
        User currentUser = AppContext.getInstance().getAuthService().getCurrentUser();
        if (currentUser == null) return;

        // Top Navigation Bar
        HBox topBar = new HBox(15);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("GUESS THE WORLD");
        title.getStyleClass().add("heading-2");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        attemptsBadge = new Label("Attempt 1 of 5");
        attemptsBadge.getStyleClass().addAll("badge-text", "badge-info");

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().addAll("button", "btn-secondary");
        backBtn.setOnAction(e -> NavigationManager.getInstance().showPlayerDashboard());

        topBar.getChildren().addAll(title, spacer, attemptsBadge, backBtn);
        root.setTop(topBar);

        // Center Content Box (Centered when maximized)
        VBox centerBox = new VBox(16);
        centerBox.setMaxWidth(520);
        centerBox.setAlignment(Pos.CENTER);

        // Status Label
        statusLabel = new Label("Enter a 5-letter word");
        statusLabel.setStyle("-fx-text-fill: #787c7e; -fx-font-size: 14px; -fx-font-weight: 600;");

        // 5x5 Game Grid
        gameGrid = new GameGrid();

        // Virtual Keyboard
        keyboard = new VirtualKeyboard();
        keyboard.setOnKeyAction(this::handleKeyInput);

        centerBox.getChildren().addAll(statusLabel, gameGrid, keyboard);

        StackPane centerWrapper = new StackPane(centerBox);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #ffffff; -fx-background-color: #ffffff;");
        root.setCenter(scrollPane);

        // Physical Keyboard listener
        root.setFocusTraversable(true);
        root.addEventFilter(KeyEvent.KEY_PRESSED, this::handlePhysicalKeyPress);
        Platform.runLater(root::requestFocus);
    }

    private void loadActiveGame() {
        User currentUser = AppContext.getInstance().getAuthService().getCurrentUser();
        if (currentUser == null) return;

        Optional<GameSession> activeOpt = AppContext.getInstance().getGamePlayService().getActiveSession(currentUser.getId());
        if (activeOpt.isEmpty()) {
            try {
                currentSession = AppContext.getInstance().getGamePlayService().startNewGame(currentUser.getId());
            } catch (Exception e) {
                AlertUtil.showError("Game Error", "Cannot start game", e.getMessage());
                NavigationManager.getInstance().showPlayerDashboard();
                return;
            }
        } else {
            currentSession = activeOpt.get();
        }

        // Restore past attempts in sequence
        List<GuessAttempt> pastAttempts = currentSession.getAttempts();
        for (int i = 0; i < pastAttempts.size(); i++) {
            GuessAttempt attempt = pastAttempts.get(i);
            List<LetterEvaluation> evals = AppContext.getInstance().getGameEngineService().evaluateGuess(
                    currentSession.getTargetWord(),
                    attempt.getGuessWord()
            );
            gameGrid.revealRow(i, evals);
            for (LetterEvaluation eval : evals) {
                keyboard.updateKeyStatus(eval.getLetter(), eval.getStatus());
            }
        }

        updateStatusDisplay();
    }

    private void handlePhysicalKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.ENTER) {
            handleKeyInput("ENTER");
            event.consume();
        } else if (code == KeyCode.BACK_SPACE) {
            handleKeyInput("BACKSPACE");
            event.consume();
        } else if (code.isLetterKey()) {
            handleKeyInput(event.getText().toUpperCase());
            event.consume();
        }
    }

    private void handleKeyInput(String key) {
        if (currentSession == null || !currentSession.getStatus().name().equals("IN_PROGRESS")) {
            return;
        }

        int currentRow = currentSession.getAttempts().size();
        if (currentRow >= 5) {
            return;
        }

        if (key.equals("ENTER")) {
            submitCurrentGuess();
        } else if (key.equals("BACKSPACE")) {
            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
                gameGrid.updateCurrentInput(currentRow, currentInput.toString());
            }
        } else if (key.length() == 1 && Character.isLetter(key.charAt(0))) {
            if (currentInput.length() < 5) {
                currentInput.append(Character.toUpperCase(key.charAt(0)));
                gameGrid.updateCurrentInput(currentRow, currentInput.toString());
            }
        }
    }

    private void submitCurrentGuess() {
        if (currentInput.length() < 5) {
            statusLabel.setText("Word must be exactly 5 letters!");
            statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 14px; -fx-font-weight: 600;");
            return;
        }

        String guess = currentInput.toString().toUpperCase();
        try {
            int currentRow = currentSession.getAttempts().size();
            GuessResult result = AppContext.getInstance().getGamePlayService().submitGuess(currentSession.getId(), guess);

            // Update UI grid row
            gameGrid.revealRow(currentRow, result.getEvaluations());

            // Update keyboard indicators
            for (LetterEvaluation eval : result.getEvaluations()) {
                keyboard.updateKeyStatus(eval.getLetter(), eval.getStatus());
            }

            currentInput.setLength(0);
            currentSession = result.getSession();
            updateStatusDisplay();

            if (result.isGameOver()) {
                AlertUtil.showGameResultDialog(
                        result.isWin(),
                        currentSession.getTargetWord(),
                        currentSession.getAttemptsUsed(),
                        () -> NavigationManager.getInstance().showPlayerDashboard()
                );
            } else {
                statusLabel.setText(result.getMessage());
                statusLabel.setStyle("-fx-text-fill: #1a1a1b; -fx-font-size: 14px; -fx-font-weight: 600;");
            }

        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 14px; -fx-font-weight: 600;");
        }
    }

    private void updateStatusDisplay() {
        int attemptNum = currentSession.getAttempts().size() + 1;
        if (attemptNum <= 5) {
            attemptsBadge.setText("Attempt " + attemptNum + " of 5");
        } else {
            attemptsBadge.setText("Completed (5 of 5)");
        }
    }

    public Parent getView() {
        return root;
    }
}
