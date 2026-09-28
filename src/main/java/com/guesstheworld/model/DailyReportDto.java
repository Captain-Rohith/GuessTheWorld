package com.guesstheworld.model;

import java.time.LocalDate;

public class DailyReportDto {
    private LocalDate reportDate;
    private int numberOfUsers;
    private int numberOfWordsTried;
    private int numberOfCorrectGuesses;
    private double successRate;

    public DailyReportDto() {
    }

    public DailyReportDto(LocalDate reportDate, int numberOfUsers, int numberOfWordsTried, int numberOfCorrectGuesses) {
        this.reportDate = reportDate;
        this.numberOfUsers = numberOfUsers;
        this.numberOfWordsTried = numberOfWordsTried;
        this.numberOfCorrectGuesses = numberOfCorrectGuesses;
        this.successRate = numberOfWordsTried > 0 ? ((double) numberOfCorrectGuesses / numberOfWordsTried) * 100.0 : 0.0;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public int getNumberOfWordsTried() {
        return numberOfWordsTried;
    }

    public void setNumberOfWordsTried(int numberOfWordsTried) {
        this.numberOfWordsTried = numberOfWordsTried;
        this.successRate = numberOfWordsTried > 0 ? ((double) numberOfCorrectGuesses / numberOfWordsTried) * 100.0 : 0.0;
    }

    public int getNumberOfCorrectGuesses() {
        return numberOfCorrectGuesses;
    }

    public void setNumberOfCorrectGuesses(int numberOfCorrectGuesses) {
        this.numberOfCorrectGuesses = numberOfCorrectGuesses;
        this.successRate = numberOfWordsTried > 0 ? ((double) numberOfCorrectGuesses / numberOfWordsTried) * 100.0 : 0.0;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
}
