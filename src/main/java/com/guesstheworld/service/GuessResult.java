package com.guesstheworld.service;

import com.guesstheworld.model.GameSession;
import com.guesstheworld.model.GuessAttempt;
import com.guesstheworld.model.LetterEvaluation;

import java.util.List;

public class GuessResult {
    private final GameSession session;
    private final GuessAttempt attempt;
    private final List<LetterEvaluation> evaluations;
    private final boolean win;
    private final boolean gameOver;
    private final int remainingAttempts;
    private final String message;

    public GuessResult(GameSession session, GuessAttempt attempt, List<LetterEvaluation> evaluations,
                       boolean win, boolean gameOver, int remainingAttempts, String message) {
        this.session = session;
        this.attempt = attempt;
        this.evaluations = evaluations;
        this.win = win;
        this.gameOver = gameOver;
        this.remainingAttempts = remainingAttempts;
        this.message = message;
    }

    public GameSession getSession() {
        return session;
    }

    public GuessAttempt getAttempt() {
        return attempt;
    }

    public List<LetterEvaluation> getEvaluations() {
        return evaluations;
    }

    public boolean isWin() {
        return win;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public String getMessage() {
        return message;
    }
}
