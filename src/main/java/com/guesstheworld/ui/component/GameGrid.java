package com.guesstheworld.ui.component;

import com.guesstheworld.model.LetterEvaluation;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class GameGrid extends VBox {

    public static final int ROWS = 5;
    public static final int COLS = 5;

    private final GameTile[][] tiles = new GameTile[ROWS][COLS];

    public GameGrid() {
        setAlignment(Pos.CENTER);
        setSpacing(6);
        buildGrid();
    }

    private void buildGrid() {
        getChildren().clear();
        for (int r = 0; r < ROWS; r++) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER);
            for (int c = 0; c < COLS; c++) {
                GameTile tile = new GameTile();
                tiles[r][c] = tile;
                row.getChildren().add(tile);
            }
            getChildren().add(row);
        }
    }

    public void updateCurrentInput(int rowIndex, String input) {
        if (rowIndex < 0 || rowIndex >= ROWS) return;
        String text = input != null ? input.toUpperCase() : "";

        for (int c = 0; c < COLS; c++) {
            if (c < text.length()) {
                tiles[rowIndex][c].setLetter(text.charAt(c));
            } else {
                tiles[rowIndex][c].clear();
            }
        }
    }

    public void revealRow(int rowIndex, List<LetterEvaluation> evaluations) {
        if (rowIndex < 0 || rowIndex >= ROWS || evaluations == null) return;

        for (int c = 0; c < COLS && c < evaluations.size(); c++) {
            LetterEvaluation eval = evaluations.get(c);
            tiles[rowIndex][c].setEvaluatedLetter(eval.getLetter(), eval.getStatus());
        }
    }

    public void reset() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                tiles[r][c].clear();
            }
        }
    }
}
