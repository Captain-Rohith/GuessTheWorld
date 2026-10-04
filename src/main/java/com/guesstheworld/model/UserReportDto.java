package com.guesstheworld.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReportDto {
    private String username;
    private LocalDate reportDate;
    private int wordsTried;
    private int correctGuesses;
    private double successRate;

    public UserReportDto(String username, LocalDate reportDate, int wordsTried, int correctGuesses) {
        this.username = username;
        this.reportDate = reportDate;
        this.wordsTried = wordsTried;
        this.correctGuesses = correctGuesses;
        this.successRate = wordsTried > 0 ? ((double) correctGuesses / wordsTried) * 100.0 : 0.0;
    }
}
