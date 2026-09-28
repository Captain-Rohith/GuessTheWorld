package com.guesstheworld.service;

import com.guesstheworld.model.LetterEvaluation;
import com.guesstheworld.model.TileStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngineService {

    public static final int WORD_LENGTH = 5;
    public static final int MAX_ATTEMPTS = 5;
    public static final int MAX_DAILY_WORDS = 3;

    public List<LetterEvaluation> evaluateGuess(String targetWord, String guessWord) {
        if (targetWord == null || guessWord == null) {
            throw new IllegalArgumentException("Target word and guess word cannot be null");
        }

        String target = targetWord.trim().toUpperCase();
        String guess = guessWord.trim().toUpperCase();

        if (target.length() != WORD_LENGTH || guess.length() != WORD_LENGTH) {
            throw new IllegalArgumentException("Target and guess words must both be exactly " + WORD_LENGTH + " letters");
        }

        TileStatus[] statuses = new TileStatus[WORD_LENGTH];
        Map<Character, Integer> targetLetterCounts = new HashMap<>();

        for (int i = 0; i < WORD_LENGTH; i++) {
            char tc = target.charAt(i);
            targetLetterCounts.put(tc, targetLetterCounts.getOrDefault(tc, 0) + 1);
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            char gc = guess.charAt(i);
            char tc = target.charAt(i);

            if (gc == tc) {
                statuses[i] = TileStatus.CORRECT;
                targetLetterCounts.put(gc, targetLetterCounts.get(gc) - 1);
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (statuses[i] == null) {
                char gc = guess.charAt(i);
                int remaining = targetLetterCounts.getOrDefault(gc, 0);

                if (remaining > 0) {
                    statuses[i] = TileStatus.PRESENT;
                    targetLetterCounts.put(gc, remaining - 1);
                } else {
                    statuses[i] = TileStatus.ABSENT;
                }
            }
        }

        List<LetterEvaluation> evaluations = new ArrayList<>(WORD_LENGTH);
        for (int i = 0; i < WORD_LENGTH; i++) {
            evaluations.add(new LetterEvaluation(guess.charAt(i), statuses[i], i));
        }

        return evaluations;
    }

    public boolean isWinningGuess(List<LetterEvaluation> evaluations) {
        if (evaluations == null || evaluations.size() != WORD_LENGTH) {
            return false;
        }
        for (LetterEvaluation eval : evaluations) {
            if (eval.getStatus() != TileStatus.CORRECT) {
                return false;
            }
        }
        return true;
    }
}
