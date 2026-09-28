package com.guesstheworld.service;

import com.guesstheworld.model.GameSession;
import com.guesstheworld.model.GameStatus;
import com.guesstheworld.model.GuessAttempt;
import com.guesstheworld.model.LetterEvaluation;
import com.guesstheworld.model.Word;
import com.guesstheworld.repository.GameSessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class GamePlayService {

    public static final int MAX_DAILY_WORDS = 3;
    public static final int MAX_ATTEMPTS = 5;

    private final GameSessionRepository gameSessionRepository;
    private final WordService wordService;
    private final GameEngineService gameEngineService;
    private final ValidationService validationService;

    public GamePlayService(GameSessionRepository gameSessionRepository,
                           WordService wordService,
                           GameEngineService gameEngineService,
                           ValidationService validationService) {
        this.gameSessionRepository = gameSessionRepository;
        this.wordService = wordService;
        this.gameEngineService = gameEngineService;
        this.validationService = validationService;
    }

    public boolean canPlayToday(Long userId) {
        int playedCount = gameSessionRepository.countSessionsForUserOnDate(userId, LocalDate.now());
        return playedCount < MAX_DAILY_WORDS;
    }

    public int getWordsPlayedTodayCount(Long userId) {
        return gameSessionRepository.countSessionsForUserOnDate(userId, LocalDate.now());
    }

    public int getRemainingGamesToday(Long userId) {
        int played = getWordsPlayedTodayCount(userId);
        return Math.max(0, MAX_DAILY_WORDS - played);
    }

    public Optional<GameSession> getActiveSession(Long userId) {
        return gameSessionRepository.findActiveSessionForUser(userId);
    }

    public GameSession startNewGame(Long userId) {
        Optional<GameSession> existingActive = gameSessionRepository.findActiveSessionForUser(userId);
        if (existingActive.isPresent()) {
            return existingActive.get();
        }

        if (!canPlayToday(userId)) {
            throw new IllegalStateException("Daily limit reached! You can only guess up to 3 words per day.");
        }

        Optional<Word> randomWordOpt = wordService.getRandomWord();
        if (randomWordOpt.isEmpty()) {
            throw new IllegalStateException("No active words available in the database.");
        }

        String targetWord = randomWordOpt.get().getWord().toUpperCase();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        GameSession newSession = new GameSession(
                null,
                userId,
                targetWord,
                MAX_ATTEMPTS,
                0,
                GameStatus.IN_PROGRESS,
                today,
                now,
                null
        );

        return gameSessionRepository.createSession(newSession);
    }

    public GuessResult submitGuess(Long sessionId, String guessWord) {
        Optional<GameSession> sessionOpt = gameSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Game session not found.");
        }

        GameSession session = sessionOpt.get();
        if (session.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("This game session is already finished.");
        }

        ValidationResult guessVal = validationService.validateGuess(guessWord);
        if (!guessVal.isValid()) {
            throw new IllegalArgumentException(guessVal.getFirstError());
        }

        String normalizedGuess = guessWord.trim().toUpperCase();
        int attemptNumber = session.getAttempts().size() + 1;

        if (attemptNumber > MAX_ATTEMPTS) {
            throw new IllegalStateException("Maximum number of 5 guesses already reached for this word.");
        }

        List<LetterEvaluation> evaluations = gameEngineService.evaluateGuess(session.getTargetWord(), normalizedGuess);
        boolean isWin = gameEngineService.isWinningGuess(evaluations);

        GuessAttempt attempt = new GuessAttempt(null, sessionId, attemptNumber, normalizedGuess, LocalDateTime.now());
        attempt.setEvaluations(evaluations);
        gameSessionRepository.saveGuessAttempt(attempt);
        session.addAttempt(attempt);

        boolean isGameOver = false;
        String message = "";

        if (isWin) {
            session.setStatus(GameStatus.WON);
            session.setCompletedAt(LocalDateTime.now());
            gameSessionRepository.updateSessionStatus(sessionId, GameStatus.WON, session.getCompletedAt(), attemptNumber);
            isGameOver = true;
            message = "Congratulations! You guessed the word correctly!";
        } else if (attemptNumber >= MAX_ATTEMPTS) {
            session.setStatus(GameStatus.LOST);
            session.setCompletedAt(LocalDateTime.now());
            gameSessionRepository.updateSessionStatus(sessionId, GameStatus.LOST, session.getCompletedAt(), attemptNumber);
            isGameOver = true;
            message = "Better luck next time! The word was: " + session.getTargetWord();
        } else {
            int remaining = MAX_ATTEMPTS - attemptNumber;
            message = "You have " + remaining + " guess" + (remaining > 1 ? "es" : "") + " remaining.";
        }

        return new GuessResult(session, attempt, evaluations, isWin, isGameOver, MAX_ATTEMPTS - attemptNumber, message);
    }
}
