package com.guesstheworld.ui.component;

import com.guesstheworld.model.TileStatus;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class GameTile extends Label {

    private TileStatus status = TileStatus.EMPTY;

    public GameTile() {
        setAlignment(Pos.CENTER);
        getStyleClass().addAll("tile", "tile-empty");
        setText("");
    }

    public void setLetter(char c) {
        if (c == ' ' || c == '\0') {
            setText("");
            setStatus(TileStatus.EMPTY);
        } else {
            setText(String.valueOf(Character.toUpperCase(c)));
            setStatus(TileStatus.FILLED_UNSUBMITTED);
        }
    }

    public void setEvaluatedLetter(char c, TileStatus tileStatus) {
        setText(String.valueOf(Character.toUpperCase(c)));
        setStatus(tileStatus);
    }

    public void clear() {
        setText("");
        setStatus(TileStatus.EMPTY);
    }

    public TileStatus getStatus() {
        return status;
    }

    public void setStatus(TileStatus status) {
        this.status = status;
        getStyleClass().removeAll("tile-empty", "tile-filled", "tile-correct", "tile-present", "tile-absent");

        switch (status) {
            case CORRECT -> getStyleClass().add("tile-correct");
            case PRESENT -> getStyleClass().add("tile-present");
            case ABSENT -> getStyleClass().add("tile-absent");
            case FILLED_UNSUBMITTED -> getStyleClass().add("tile-filled");
            case EMPTY -> getStyleClass().add("tile-empty");
        }
    }
}
