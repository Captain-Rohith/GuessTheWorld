package com.guesstheworld.config;

import com.guesstheworld.repository.GameSessionRepository;
import com.guesstheworld.repository.ReportRepository;
import com.guesstheworld.repository.UserRepository;
import com.guesstheworld.repository.WordRepository;
import com.guesstheworld.service.AuthService;
import com.guesstheworld.service.GameEngineService;
import com.guesstheworld.service.GamePlayService;
import com.guesstheworld.service.ReportService;
import com.guesstheworld.service.ValidationService;
import com.guesstheworld.service.WordService;

public class AppContext {

    private static AppContext instance;

    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final GameSessionRepository gameSessionRepository;
    private final ReportRepository reportRepository;

    private final ValidationService validationService;
    private final GameEngineService gameEngineService;
    private final AuthService authService;
    private final WordService wordService;
    private final GamePlayService gamePlayService;
    private final ReportService reportService;

    private AppContext() {
        DatabaseConfig.initializeDatabase();

        this.userRepository = new UserRepository();
        this.wordRepository = new WordRepository();
        this.gameSessionRepository = new GameSessionRepository();
        this.reportRepository = new ReportRepository();

        this.validationService = new ValidationService();
        this.gameEngineService = new GameEngineService();
        this.authService = new AuthService(userRepository, validationService);
        this.wordService = new WordService(wordRepository, validationService);
        this.gamePlayService = new GamePlayService(gameSessionRepository, wordService, gameEngineService, validationService);
        this.reportService = new ReportService(reportRepository, userRepository);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public static synchronized void reset() {
        instance = null;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public WordRepository getWordRepository() {
        return wordRepository;
    }

    public GameSessionRepository getGameSessionRepository() {
        return gameSessionRepository;
    }

    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    public ValidationService getValidationService() {
        return validationService;
    }

    public GameEngineService getGameEngineService() {
        return gameEngineService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public WordService getWordService() {
        return wordService;
    }

    public GamePlayService getGamePlayService() {
        return gamePlayService;
    }

    public ReportService getReportService() {
        return reportService;
    }
}
