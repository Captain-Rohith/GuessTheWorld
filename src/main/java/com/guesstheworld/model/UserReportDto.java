package com.guesstheworld.model;

import java.time.LocalDate;

public class UserReportDto {
    private String username;
    private LocalDate reportDate;
    private int wordsTried;
    private int correctGuesses;
    private double successRate;

    public UserReportDto() {
    }

    public UserReportDto(String username, LocalDate reportDate, int wordsTried, int correctGuesses) {
        this.username = username;
        this.reportDate = reportDate;
        this.wordsTried = wordsTried;
        this.correctGuesses = correctGuesses;
        this.successRate = wordsTried > 0 ? ((double) correctGuesses / wordsTried) * 100.0 : 0.0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public int getWordsTried() {
        return wordsTried;
    }

    public void setWordsTried(int wordsTried) {
        this.wordsTried = wordsTried;
        this.successRate = wordsTried > 0 ? ((double) correctGuesses / wordsTried) * 100.0 : 0.0;
    }

    public int getCorrectGuesses() {
        return correctGuesses;
    }

    public void setCorrectGuesses(int correctGuesses) {
        this.correctGuesses = correctGuesses;
        this.successRate = wordsTried > 0 ? ((double) correctGuesses / wordsTried) * 100.0 : 0.0;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
}
