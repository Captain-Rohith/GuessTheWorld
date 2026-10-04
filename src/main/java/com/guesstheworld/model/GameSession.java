package com.guesstheworld.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {
    private Long id;
    private Long userId;
    private String targetWord;
    @Builder.Default
    private int maxAttempts = 5;
    @Builder.Default
    private int attemptsUsed = 0;
    @Builder.Default
    private GameStatus status = GameStatus.IN_PROGRESS;
    private LocalDate sessionDate;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    @Builder.Default
    private List<GuessAttempt> attempts = new ArrayList<>();

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
        this.attempts = new ArrayList<>();
    }

    public void addAttempt(GuessAttempt attempt) {
        if (attempts == null) {
            attempts = new ArrayList<>();
        }
        attempts.add(attempt);
        this.attemptsUsed = attempts.size();
    }
}
