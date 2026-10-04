package com.guesstheworld.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuessAttempt {
    private Long id;
    private Long sessionId;
    private int attemptNumber;
    private String guessWord;
    private LocalDateTime guessedAt;
    @Builder.Default
    private List<LetterEvaluation> evaluations = new ArrayList<>();

    public GuessAttempt(Long id, Long sessionId, int attemptNumber, String guessWord, LocalDateTime guessedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.attemptNumber = attemptNumber;
        this.guessWord = guessWord != null ? guessWord.toUpperCase() : null;
        this.guessedAt = guessedAt;
        this.evaluations = new ArrayList<>();
    }
}
