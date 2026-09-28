package com.guesstheworld.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;

public final class AlertUtil {

    private AlertUtil() {
    }

    public static void showInfo(String title, String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void showWarning(String title, String header, String content) {
        showAlert(Alert.AlertType.WARNING, title, header, content);
    }

    public static void showError(String title, String header, String content) {
        showAlert(Alert.AlertType.ERROR, title, header, content);
    }

    /**
     * Shows a congratulatory or game over modal that blocks until OK is clicked.
     */
    public static void showGameResultDialog(boolean isWin, String targetWord, int attemptsUsed, Runnable onOkClicked) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(isWin ? "Congratulations" : "Game Over");
        
        if (isWin) {
            alert.setHeaderText("Congratulations!");
            alert.setContentText("You guessed the word '" + targetWord + "' in " + attemptsUsed + " attempt" + (attemptsUsed > 1 ? "s" : "") + ".\n\nClick OK to return to the dashboard.");
        } else {
            alert.setHeaderText("Better luck next time!");
            alert.setContentText("You tried all 5 guesses and could not guess correctly.\nThe hidden word was: " + targetWord + "\n\nClick OK to return to the dashboard.");
        }

        applyDialogStyles(alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (onOkClicked != null) {
                onOkClicked.run();
            }
        }
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        applyDialogStyles(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        applyDialogStyles(alert);
        alert.showAndWait();
    }

    private static void applyDialogStyles(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(
                    AlertUtil.class.getResource("/styles/app.css").toExternalForm()
            );
        } catch (Exception ignored) {
        }
    }
}
