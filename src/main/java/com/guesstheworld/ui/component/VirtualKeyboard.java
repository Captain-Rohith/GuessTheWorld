package com.guesstheworld.ui.component;

import com.guesstheworld.model.TileStatus;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class VirtualKeyboard extends VBox {

    private final Map<Character, Button> keyButtons = new HashMap<>();
    private final Map<Character, TileStatus> keyStatuses = new HashMap<>();
    private Consumer<String> keyListener;

    private static final String[][] ROWS = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "BACKSPACE"}
    };

    public VirtualKeyboard() {
        setAlignment(Pos.CENTER);
        setSpacing(6);
        buildKeyboard();
    }

    public void setOnKeyAction(Consumer<String> listener) {
        this.keyListener = listener;
    }

    private void buildKeyboard() {
        getChildren().clear();
        keyButtons.clear();
        keyStatuses.clear();

        for (String[] rowKeys : ROWS) {
            HBox rowBox = new HBox(5);
            rowBox.setAlignment(Pos.CENTER);

            for (String keyText : rowKeys) {
                Button btn = new Button();
                btn.getStyleClass().add("keyboard-key");

                if (keyText.equals("ENTER")) {
                    btn.setText("SUBMIT");
                    btn.getStyleClass().add("keyboard-key-action");
                    btn.setOnAction(e -> triggerAction("ENTER"));
                } else if (keyText.equals("BACKSPACE")) {
                    btn.setText("BACKSPACE");
                    btn.getStyleClass().add("keyboard-key-action");
                    btn.setOnAction(e -> triggerAction("BACKSPACE"));
                } else {
                    btn.setText(keyText);
                    char c = keyText.charAt(0);
                    keyButtons.put(c, btn);
                    btn.setOnAction(e -> triggerAction(keyText));
                }

                rowBox.getChildren().add(btn);
            }

            getChildren().add(rowBox);
        }
    }

    private void triggerAction(String action) {
        if (keyListener != null) {
            keyListener.accept(action);
        }
    }

    public void updateKeyStatus(char letter, TileStatus newStatus) {
        char upper = Character.toUpperCase(letter);
        Button btn = keyButtons.get(upper);
        if (btn == null) return;

        TileStatus current = keyStatuses.getOrDefault(upper, TileStatus.EMPTY);

        // Precedence: CORRECT > PRESENT > ABSENT
        if (current == TileStatus.CORRECT) {
            return;
        }
        if (current == TileStatus.PRESENT && newStatus != TileStatus.CORRECT) {
            return;
        }

        keyStatuses.put(upper, newStatus);
        btn.getStyleClass().removeAll("key-correct", "key-present", "key-absent");

        switch (newStatus) {
            case CORRECT -> btn.getStyleClass().add("key-correct");
            case PRESENT -> btn.getStyleClass().add("key-present");
            case ABSENT -> btn.getStyleClass().add("key-absent");
            default -> {}
        }
    }

    public void reset() {
        keyStatuses.clear();
        for (Button btn : keyButtons.values()) {
            btn.getStyleClass().removeAll("key-correct", "key-present", "key-absent");
        }
    }
}
