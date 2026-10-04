package com.guesstheworld.model;

import lombok.Data;

@Data
public class LetterEvaluation {
    private final char letter;
    private final TileStatus status;
    private final int position;

    public LetterEvaluation(char letter, TileStatus status, int position) {
        this.letter = Character.toUpperCase(letter);
        this.status = status;
        this.position = position;
    }
}
