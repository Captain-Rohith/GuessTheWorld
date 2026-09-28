package com.guesstheworld.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private Long id;
    private Long userId;
    private String targetWord;
    private int maxAttempts = 5;
    private int attemptsUsed = 0;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private LocalDate sessionDate;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<GuessAttempt> attempts = new ArrayList<>();

    public GameSession() {
    }

    public GameSession(Long id, Long userId, String targetWord, int maxAttempts, int attemptsUsed,
                       GameStatus status, LocalDate sessionDate, LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.userId = userId;
        this.targetWord = targetWord != null ? targetWord.toUpperCase() : null;
        this.maxAttempts = maxAttempts;
        this.attemptsUsed = attemptsUsed;
        this.status = status;
        this.sessionDate = sessionDate;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(String targetWord) {
        this.targetWord = targetWord != null ? targetWord.toUpperCase() : null;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getAttemptsUsed() {
        return attemptsUsed;
    }

    public void setAttemptsUsed(int attemptsUsed) {
        this.attemptsUsed = attemptsUsed;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<GuessAttempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<GuessAttempt> attempts) {
        this.attempts = attempts != null ? attempts : new ArrayList<>();
    }

    public void addAttempt(GuessAttempt attempt) {
        if (attempts == null) {
            attempts = new ArrayList<>();
        }
        attempts.add(attempt);
        this.attemptsUsed = attempts.size();
    }
}
