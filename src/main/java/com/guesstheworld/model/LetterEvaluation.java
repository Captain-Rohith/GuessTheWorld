package com.guesstheworld.model;

import java.util.Objects;

public class LetterEvaluation {
    private final char letter;
    private final TileStatus status;
    private final int position;

    public LetterEvaluation(char letter, TileStatus status, int position) {
        this.letter = Character.toUpperCase(letter);
        this.status = status;
        this.position = position;
    }

    public char getLetter() {
        return letter;
    }

    public TileStatus getStatus() {
        return status;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LetterEvaluation that = (LetterEvaluation) o;
        return letter == that.letter && position == that.position && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, status, position);
    }

    @Override
    public String toString() {
        return "LetterEvaluation{" +
                "letter=" + letter +
                ", status=" + status +
                ", position=" + position +
                '}';
    }
}
