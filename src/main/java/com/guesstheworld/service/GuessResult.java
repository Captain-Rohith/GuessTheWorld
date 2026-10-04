package com.guesstheworld.service;

import com.guesstheworld.model.GameSession;
import com.guesstheworld.model.GuessAttempt;
import com.guesstheworld.model.LetterEvaluation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GuessResult {
    private final GameSession session;
    private final GuessAttempt attempt;
    private final List<LetterEvaluation> evaluations;
    private final boolean win;
    private final boolean gameOver;
    private final int remainingAttempts;
    private final String message;
}
