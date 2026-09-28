package com.guesstheworld.service;

import com.guesstheworld.model.LetterEvaluation;
import com.guesstheworld.model.TileStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineServiceTest {

    private GameEngineService gameEngine;

    @BeforeEach
    void setUp() {
        gameEngine = new GameEngineService();
    }

    @Test
    void testExactMatchWinningGuess() {
        List<LetterEvaluation> evals = gameEngine.evaluateGuess("APPLE", "APPLE");

        assertEquals(5, evals.size());
        for (LetterEvaluation eval : evals) {
            assertEquals(TileStatus.CORRECT, eval.getStatus());
        }
        assertTrue(gameEngine.isWinningGuess(evals));
    }

    @Test
    void testCompleteMismatch() {
        List<LetterEvaluation> evals = gameEngine.evaluateGuess("APPLE", "SHORT");

        assertEquals(5, evals.size());
        for (LetterEvaluation eval : evals) {
            assertEquals(TileStatus.ABSENT, eval.getStatus());
        }
        assertFalse(gameEngine.isWinningGuess(evals));
    }

    @Test
    void testMisplacedLetters() {
        List<LetterEvaluation> evals = gameEngine.evaluateGuess("CRANE", "REACT");

        assertEquals(TileStatus.PRESENT, evals.get(0).getStatus());
        assertEquals(TileStatus.PRESENT, evals.get(1).getStatus());
        assertEquals(TileStatus.CORRECT, evals.get(2).getStatus());
        assertEquals(TileStatus.PRESENT, evals.get(3).getStatus());
        assertEquals(TileStatus.ABSENT, evals.get(4).getStatus());
        assertFalse(gameEngine.isWinningGuess(evals));
    }

    @Test
    void testDuplicateLetterFrequencyConstraint() {
        List<LetterEvaluation> evals = gameEngine.evaluateGuess("CRANE", "SPEED");

        assertEquals(TileStatus.ABSENT, evals.get(0).getStatus());
        assertEquals(TileStatus.ABSENT, evals.get(1).getStatus());
        assertEquals(TileStatus.PRESENT, evals.get(2).getStatus());
        assertEquals(TileStatus.ABSENT, evals.get(3).getStatus());
        assertEquals(TileStatus.ABSENT, evals.get(4).getStatus());
    }

    @Test
    void testGreenPriorityOverOrange() {
        List<LetterEvaluation> evals = gameEngine.evaluateGuess("APPLE", "PUPIL");

        assertEquals(TileStatus.PRESENT, evals.get(0).getStatus());
        assertEquals(TileStatus.ABSENT, evals.get(1).getStatus());
        assertEquals(TileStatus.CORRECT, evals.get(2).getStatus());
        assertEquals(TileStatus.ABSENT, evals.get(3).getStatus());
        assertEquals(TileStatus.PRESENT, evals.get(4).getStatus());
    }
}
