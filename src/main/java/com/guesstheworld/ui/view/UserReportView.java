package com.guesstheworld.ui.view;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.UserReportDto;
import com.guesstheworld.ui.component.StatCard;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserReportView {

    private final VBox root;
    private ComboBox<String> userComboBox;
    private TableView<UserReportDto> tableView;
    private StatCard userWordsTriedCard;
    private StatCard userCorrectGuessesCard;
    private StatCard userSuccessRateCard;

    public UserReportView() {
        root = new VBox(16);
        root.setMaxWidth(960);
        root.setAlignment(Pos.TOP_CENTER);

        buildUI();
        loadUsers();
        loadAllUserReports();
    }

    private void buildUI() {
        // Filter Controls Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.getStyleClass().add("card-panel");

        VBox titleBox = new VBox(2);
        Label title = new Label("User Reports");
        title.getStyleClass().add("heading-2");
        Label subtitle = new Label("View performance metrics by user and date");
        subtitle.getStyleClass().add("sub-text");
        titleBox.getChildren().addAll(title, subtitle);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userSelectLabel = new Label("User:");
        userSelectLabel.getStyleClass().add("heading-3");

        userComboBox = new ComboBox<>();
        userComboBox.setPromptText("Select user");
        userComboBox.setMinWidth(160);

        Button filterBtn = new Button("Filter User");
        filterBtn.getStyleClass().addAll("button", "btn-primary");
        filterBtn.setOnAction(e -> filterByUser());

        Button showAllBtn = new Button("Show All Users");
        showAllBtn.getStyleClass().addAll("button", "btn-secondary");
        showAllBtn.setOnAction(e -> loadAllUserReports());

        filterBar.getChildren().addAll(titleBox, spacer, userSelectLabel, userComboBox, filterBtn, showAllBtn);

        // Stats Row
        HBox statsRow = new HBox(12);
        statsRow.setAlignment(Pos.CENTER);

        userWordsTriedCard = new StatCard("Total Words Tried", "0", "#1a1a1b");
        userCorrectGuessesCard = new StatCard("Correct Guesses", "0", "#6aaa64");
        userSuccessRateCard = new StatCard("Accuracy Rate", "0.0%", "#1a1a1b");

        statsRow.getChildren().addAll(userWordsTriedCard, userCorrectGuessesCard, userSuccessRateCard);

        // Data Table
        VBox tableBox = new VBox(8);
        tableBox.getStyleClass().add("card-panel");

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(340);

        TableColumn<UserReportDto, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));

        TableColumn<UserReportDto, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReportDate().toString()));

        TableColumn<UserReportDto, String> wordsTriedCol = new TableColumn<>("Number of Words Tried");
        wordsTriedCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getWordsTried())));

        TableColumn<UserReportDto, String> correctCol = new TableColumn<>("Number of Correct Guesses");
        correctCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getCorrectGuesses())));

        TableColumn<UserReportDto, String> rateCol = new TableColumn<>("Success Rate");
        rateCol.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.1f%%", cell.getValue().getSuccessRate())));

        tableView.getColumns().addAll(userCol, dateCol, wordsTriedCol, correctCol, rateCol);
        tableBox.getChildren().addAll(tableView);

        root.getChildren().addAll(filterBar, statsRow, tableBox);
    }

    private void loadUsers() {
        List<String> usernames = AppContext.getInstance().getReportService().getAllUsernames();
        userComboBox.setItems(FXCollections.observableArrayList(usernames));
    }

    private void filterByUser() {
        String selectedUser = userComboBox.getValue();
        if (selectedUser == null || selectedUser.trim().isEmpty()) {
            loadAllUserReports();
            return;
        }

        List<UserReportDto> reports = AppContext.getInstance().getReportService().getReportForUser(selectedUser);
        updateTableAndStats(reports);
    }

    private void loadAllUserReports() {
        List<UserReportDto> reports = AppContext.getInstance().getReportService().getAllUserReports();
        updateTableAndStats(reports);
    }

    private void updateTableAndStats(List<UserReportDto> reports) {
        tableView.setItems(FXCollections.observableArrayList(reports));

        int totalWords = reports.stream().mapToInt(UserReportDto::getWordsTried).sum();
        int totalCorrect = reports.stream().mapToInt(UserReportDto::getCorrectGuesses).sum();
        double rate = totalWords > 0 ? ((double) totalCorrect / totalWords) * 100.0 : 0.0;

        userWordsTriedCard.setValue(String.valueOf(totalWords));
        userCorrectGuessesCard.setValue(String.valueOf(totalCorrect));
        userSuccessRateCard.setValue(String.format("%.1f%%", rate));
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
