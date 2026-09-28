package com.guesstheworld.service;

import com.guesstheworld.config.DatabaseConfig;
import com.guesstheworld.model.GameSession;
import com.guesstheworld.model.GameStatus;
import com.guesstheworld.model.Role;
import com.guesstheworld.model.User;
import com.guesstheworld.repository.GameSessionRepository;
import com.guesstheworld.repository.UserRepository;
import com.guesstheworld.repository.WordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayServiceTest {

    private static final String TEST_DB = "jdbc:sqlite:test_gameplay.db";
    private GamePlayService gamePlayService;
    private GameSessionRepository sessionRepository;
    private UserRepository userRepository;
    private WordRepository wordRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        DatabaseConfig.setDatabaseUrl(TEST_DB);
        DatabaseConfig.initializeDatabase();

        userRepository = new UserRepository();
        wordRepository = new WordRepository();
        sessionRepository = new GameSessionRepository();
        ValidationService validationService = new ValidationService();
        GameEngineService gameEngineService = new GameEngineService();
        WordService wordService = new WordService(wordRepository, validationService);

        gamePlayService = new GamePlayService(sessionRepository, wordService, gameEngineService, validationService);

        AuthService authService = new AuthService(userRepository, validationService);
        testUser = authService.register("GamerOne", "Gamer123$", Role.PLAYER);
    }

    @AfterEach
    void tearDown() {
        DatabaseConfig.resetToDefaultUrl();
        new File("test_gameplay.db").delete();
    }

    @Test
    void testDailyGameLimitOfThree() {
        GameSession game1 = gamePlayService.startNewGame(testUser.getId());
        assertEquals(1, gamePlayService.getWordsPlayedTodayCount(testUser.getId()));
        assertEquals(2, gamePlayService.getRemainingGamesToday(testUser.getId()));
        gamePlayService.submitGuess(game1.getId(), game1.getTargetWord());

        GameSession game2 = gamePlayService.startNewGame(testUser.getId());
        assertEquals(2, gamePlayService.getWordsPlayedTodayCount(testUser.getId()));
        assertEquals(1, gamePlayService.getRemainingGamesToday(testUser.getId()));
        gamePlayService.submitGuess(game2.getId(), game2.getTargetWord());

        GameSession game3 = gamePlayService.startNewGame(testUser.getId());
        assertEquals(3, gamePlayService.getWordsPlayedTodayCount(testUser.getId()));
        assertEquals(0, gamePlayService.getRemainingGamesToday(testUser.getId()));
        assertFalse(gamePlayService.canPlayToday(testUser.getId()));
        gamePlayService.submitGuess(game3.getId(), game3.getTargetWord());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                gamePlayService.startNewGame(testUser.getId())
        );
        assertTrue(ex.getMessage().contains("Daily limit reached"));
    }

    @Test
    void testWinningGame() {
        GameSession session = gamePlayService.startNewGame(testUser.getId());
        String target = session.getTargetWord();

        GuessResult result = gamePlayService.submitGuess(session.getId(), target);

        assertTrue(result.isWin());
        assertTrue(result.isGameOver());
        assertEquals(GameStatus.WON, result.getSession().getStatus());
        assertNotNull(result.getSession().getCompletedAt());
        assertEquals(1, result.getSession().getAttemptsUsed());
    }

    @Test
    void testLosingGameAfterFiveAttempts() {
        GameSession session = gamePlayService.startNewGame(testUser.getId());
        String wrongGuess = session.getTargetWord().equals("MUSIC") ? "ZEBRA" : "MUSIC";

        for (int i = 1; i <= 4; i++) {
            GuessResult res = gamePlayService.submitGuess(session.getId(), wrongGuess);
            assertFalse(res.isGameOver());
            assertFalse(res.isWin());
            assertEquals(5 - i, res.getRemainingAttempts());
        }

        GuessResult finalResult = gamePlayService.submitGuess(session.getId(), wrongGuess);
        assertTrue(finalResult.isGameOver());
        assertFalse(finalResult.isWin());
        assertEquals(GameStatus.LOST, finalResult.getSession().getStatus());
        assertTrue(finalResult.getMessage().contains("Better luck next time"));
    }
}
