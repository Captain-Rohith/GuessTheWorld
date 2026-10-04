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
public class DailyReportDto {
    private LocalDate reportDate;
    private int numberOfUsers;
    private int numberOfWordsTried;
    private int numberOfCorrectGuesses;
    private double successRate;

    public DailyReportDto(LocalDate reportDate, int numberOfUsers, int numberOfWordsTried, int numberOfCorrectGuesses) {
        this.reportDate = reportDate;
        this.numberOfUsers = numberOfUsers;
        this.numberOfWordsTried = numberOfWordsTried;
        this.numberOfCorrectGuesses = numberOfCorrectGuesses;
        this.successRate = numberOfWordsTried > 0 ? ((double) numberOfCorrectGuesses / numberOfWordsTried) * 100.0 : 0.0;
    }
}
