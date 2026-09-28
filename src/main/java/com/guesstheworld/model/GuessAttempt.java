package com.guesstheworld.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GuessAttempt {
    private Long id;
    private Long sessionId;
    private int attemptNumber;
    private String guessWord;
    private LocalDateTime guessedAt;
    private List<LetterEvaluation> evaluations = new ArrayList<>();

    public GuessAttempt() {
    }

    public GuessAttempt(Long id, Long sessionId, int attemptNumber, String guessWord, LocalDateTime guessedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.attemptNumber = attemptNumber;
        this.guessWord = guessWord != null ? guessWord.toUpperCase() : null;
        this.guessedAt = guessedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getGuessWord() {
        return guessWord;
    }

    public void setGuessWord(String guessWord) {
        this.guessWord = guessWord != null ? guessWord.toUpperCase() : null;
    }

    public LocalDateTime getGuessedAt() {
        return guessedAt;
    }

    public void setGuessedAt(LocalDateTime guessedAt) {
        this.guessedAt = guessedAt;
    }

    public List<LetterEvaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<LetterEvaluation> evaluations) {
        this.evaluations = evaluations != null ? evaluations : new ArrayList<>();
    }
}
