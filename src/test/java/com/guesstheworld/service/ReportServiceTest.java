package com.guesstheworld.service;

import com.guesstheworld.config.DatabaseConfig;
import com.guesstheworld.model.DailyReportDto;
import com.guesstheworld.model.Role;
import com.guesstheworld.model.User;
import com.guesstheworld.model.UserReportDto;
import com.guesstheworld.repository.GameSessionRepository;
import com.guesstheworld.repository.ReportRepository;
import com.guesstheworld.repository.UserRepository;
import com.guesstheworld.repository.WordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private static final String TEST_DB = "jdbc:sqlite:test_reports.db";
    private ReportService reportService;
    private GamePlayService gamePlayService;
    private AuthService authService;
    private User player1;
    private User player2;

    @BeforeEach
    void setUp() {
        DatabaseConfig.setDatabaseUrl(TEST_DB);
        DatabaseConfig.initializeDatabase();

        UserRepository userRepo = new UserRepository();
        WordRepository wordRepo = new WordRepository();
        GameSessionRepository sessionRepo = new GameSessionRepository();
        ReportRepository reportRepo = new ReportRepository();
        ValidationService valService = new ValidationService();
        GameEngineService gameEngine = new GameEngineService();
        WordService wordService = new WordService(wordRepo, valService);

        this.authService = new AuthService(userRepo, valService);
        this.gamePlayService = new GamePlayService(sessionRepo, wordService, gameEngine, valService);
        this.reportService = new ReportService(reportRepo, userRepo);

        player1 = authService.register("HeroPlayer", "Hero123$", Role.PLAYER);
        player2 = authService.register("StarPlayer", "Star123%", Role.PLAYER);
    }

    @AfterEach
    void tearDown() {
        DatabaseConfig.resetToDefaultUrl();
        new File("test_reports.db").delete();
    }

    @Test
    void testDailyReportAggregation() {
        var s1 = gamePlayService.startNewGame(player1.getId());
        gamePlayService.submitGuess(s1.getId(), s1.getTargetWord());

        var s2 = gamePlayService.startNewGame(player1.getId());
        String wrongGuess = s2.getTargetWord().equals("MUSIC") ? "ZEBRA" : "MUSIC";
        for (int i = 0; i < 5; i++) {
            gamePlayService.submitGuess(s2.getId(), wrongGuess);
        }

        var s3 = gamePlayService.startNewGame(player2.getId());
        gamePlayService.submitGuess(s3.getId(), s3.getTargetWord());

        DailyReportDto daily = reportService.getDailyReport(LocalDate.now());

        assertEquals(2, daily.getNumberOfUsers());
        assertEquals(3, daily.getNumberOfWordsTried());
        assertEquals(2, daily.getNumberOfCorrectGuesses());
        assertEquals(66.67, Math.round(daily.getSuccessRate() * 100.0) / 100.0);
    }

    @Test
    void testUserReportAggregation() {
        var s1 = gamePlayService.startNewGame(player1.getId());
        gamePlayService.submitGuess(s1.getId(), s1.getTargetWord());

        List<UserReportDto> p1Reports = reportService.getReportForUser("HeroPlayer");
        assertEquals(1, p1Reports.size());
        assertEquals("HeroPlayer", p1Reports.get(0).getUsername());
        assertEquals(1, p1Reports.get(0).getWordsTried());
        assertEquals(1, p1Reports.get(0).getCorrectGuesses());
        assertEquals(100.0, p1Reports.get(0).getSuccessRate());
    }
}
