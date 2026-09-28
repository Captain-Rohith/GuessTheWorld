package com.guesstheworld.service;

import com.guesstheworld.model.DailyReportDto;
import com.guesstheworld.model.User;
import com.guesstheworld.model.UserReportDto;
import com.guesstheworld.repository.ReportRepository;
import com.guesstheworld.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public DailyReportDto getDailyReport(LocalDate date) {
        return reportRepository.getReportForDate(date);
    }

    public List<DailyReportDto> getAllDailyReports() {
        return reportRepository.getAllDailyReports();
    }

    public List<UserReportDto> getReportForUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return reportRepository.getAllUserReports();
        }
        return reportRepository.getReportForUser(username.trim());
    }

    public List<UserReportDto> getAllUserReports() {
        return reportRepository.getAllUserReports();
    }

    public List<String> getAllUsernames() {
        return userRepository.findAllPlayers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}
