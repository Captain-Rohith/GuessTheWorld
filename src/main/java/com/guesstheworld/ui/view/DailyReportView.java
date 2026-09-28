package com.guesstheworld.ui.view;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.DailyReportDto;
import com.guesstheworld.ui.component.StatCard;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class DailyReportView {

    private final VBox root;
    private DatePicker datePicker;
    private TableView<DailyReportDto> tableView;
    private StatCard totalUsersCard;
    private StatCard totalWordsTriedCard;
    private StatCard totalCorrectGuessesCard;
    private StatCard successRateCard;

    public DailyReportView() {
        root = new VBox(16);
        root.setMaxWidth(960);
        root.setAlignment(Pos.TOP_CENTER);

        buildUI();
        loadAllData();
    }

    private void buildUI() {
        // Filter Controls Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.getStyleClass().add("card-panel");

        VBox titleBox = new VBox(2);
        Label title = new Label("Daily Reports");
        title.getStyleClass().add("heading-2");
        Label subtitle = new Label("View daily user engagement and correct guesses");
        subtitle.getStyleClass().add("sub-text");
        titleBox.getChildren().addAll(title, subtitle);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label("Date:");
        dateLabel.getStyleClass().add("heading-3");

        datePicker = new DatePicker(LocalDate.now());

        Button filterBtn = new Button("Filter Date");
        filterBtn.getStyleClass().addAll("button", "btn-primary");
        filterBtn.setOnAction(e -> filterByDate());

        Button viewAllBtn = new Button("Show All Dates");
        viewAllBtn.getStyleClass().addAll("button", "btn-secondary");
        viewAllBtn.setOnAction(e -> loadAllData());

        filterBar.getChildren().addAll(titleBox, spacer, dateLabel, datePicker, filterBtn, viewAllBtn);

        // Stat Cards Summary
        HBox statsRow = new HBox(12);
        statsRow.setAlignment(Pos.CENTER);

        totalUsersCard = new StatCard("Active Users", "0", "#1a1a1b");
        totalWordsTriedCard = new StatCard("Words Tried", "0", "#1a1a1b");
        totalCorrectGuessesCard = new StatCard("Correct Guesses", "0", "#6aaa64");
        successRateCard = new StatCard("Success Rate", "0.0%", "#1a1a1b");

        statsRow.getChildren().addAll(totalUsersCard, totalWordsTriedCard, totalCorrectGuessesCard, successRateCard);

        // Data Table
        VBox tableBox = new VBox(8);
        tableBox.getStyleClass().add("card-panel");

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(340);

        TableColumn<DailyReportDto, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReportDate().toString()));

        TableColumn<DailyReportDto, String> usersCol = new TableColumn<>("Number of Users");
        usersCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getNumberOfUsers())));

        TableColumn<DailyReportDto, String> wordsTriedCol = new TableColumn<>("Number of Words Tried");
        wordsTriedCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getNumberOfWordsTried())));

        TableColumn<DailyReportDto, String> correctCol = new TableColumn<>("Number of Correct Guesses");
        correctCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getNumberOfCorrectGuesses())));

        TableColumn<DailyReportDto, String> rateCol = new TableColumn<>("Success Rate");
        rateCol.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.1f%%", cell.getValue().getSuccessRate())));

        tableView.getColumns().addAll(dateCol, usersCol, wordsTriedCol, correctCol, rateCol);
        tableBox.getChildren().addAll(tableView);

        root.getChildren().addAll(filterBar, statsRow, tableBox);
    }

    private void filterByDate() {
        LocalDate selected = datePicker.getValue();
        if (selected == null) {
            loadAllData();
            return;
        }
        DailyReportDto report = AppContext.getInstance().getReportService().getDailyReport(selected);
        tableView.setItems(FXCollections.observableArrayList(report));

        totalUsersCard.setValue(String.valueOf(report.getNumberOfUsers()));
        totalWordsTriedCard.setValue(String.valueOf(report.getNumberOfWordsTried()));
        totalCorrectGuessesCard.setValue(String.valueOf(report.getNumberOfCorrectGuesses()));
        successRateCard.setValue(String.format("%.1f%%", report.getSuccessRate()));
    }

    private void loadAllData() {
        List<DailyReportDto> list = AppContext.getInstance().getReportService().getAllDailyReports();
        tableView.setItems(FXCollections.observableArrayList(list));

        int totalUsers = list.stream().mapToInt(DailyReportDto::getNumberOfUsers).sum();
        int totalWords = list.stream().mapToInt(DailyReportDto::getNumberOfWordsTried).sum();
        int totalCorrect = list.stream().mapToInt(DailyReportDto::getNumberOfCorrectGuesses).sum();
        double rate = totalWords > 0 ? ((double) totalCorrect / totalWords) * 100.0 : 0.0;

        totalUsersCard.setValue(String.valueOf(totalUsers));
        totalWordsTriedCard.setValue(String.valueOf(totalWords));
        totalCorrectGuessesCard.setValue(String.valueOf(totalCorrect));
        successRateCard.setValue(String.format("%.1f%%", rate));
    }

    public Parent getView() {
        StackPane centerWrapper = new StackPane(root);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(20));

        ScrollPane sp = new ScrollPane(centerWrapper);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #ffffff; -fx-background-color: #ffffff;");
        return sp;
    }
}
