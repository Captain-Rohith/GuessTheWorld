package com.guesstheworld.ui.view;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.Word;
import com.guesstheworld.ui.component.StatCard;
import com.guesstheworld.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class WordManagementView {

    private final VBox root;
    private TableView<Word> tableView;
    private TextField newWordField;
    private StatCard totalWordsCard;
    private StatCard activeWordsCard;

    public WordManagementView() {
        root = new VBox(16);
        root.setMaxWidth(960);
        root.setAlignment(Pos.TOP_CENTER);

        buildUI();
        loadWords();
    }

    private void buildUI() {
        // Add Word Bar
        HBox addBar = new HBox(12);
        addBar.setAlignment(Pos.CENTER_LEFT);
        addBar.getStyleClass().add("card-panel");

        VBox titleBox = new VBox(2);
        Label title = new Label("Word Pool");
        title.getStyleClass().add("heading-2");
        Label subtitle = new Label("Manage 5-letter words available for gameplay");
        subtitle.getStyleClass().add("sub-text");
        titleBox.getChildren().addAll(title, subtitle);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        newWordField = new TextField();
        newWordField.setPromptText("5-letter word (e.g. SOLAR)");
        newWordField.setMinWidth(180);

        Button addBtn = new Button("Add Word");
        addBtn.getStyleClass().addAll("button", "btn-primary");
        addBtn.setOnAction(e -> handleAddWord());

        addBar.getChildren().addAll(titleBox, spacer, newWordField, addBtn);

        // Stats Row
        HBox statsRow = new HBox(12);
        statsRow.setAlignment(Pos.CENTER);

        totalWordsCard = new StatCard("Total Words", "0", "#1a1a1b");
        activeWordsCard = new StatCard("Active Words", "0", "#6aaa64");

        statsRow.getChildren().addAll(totalWordsCard, activeWordsCard);

        // Table
        VBox tableBox = new VBox(8);
        tableBox.getStyleClass().add("card-panel");

        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(340);

        TableColumn<Word, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));
        idCol.setMaxWidth(60);

        TableColumn<Word, String> wordCol = new TableColumn<>("Word");
        wordCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getWord()));

        TableColumn<Word, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isActive() ? "ACTIVE" : "INACTIVE"));

        TableColumn<Word, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button toggleBtn = new Button();
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(6, toggleBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER);
                toggleBtn.getStyleClass().addAll("button", "btn-secondary");
                deleteBtn.getStyleClass().addAll("button", "btn-danger");
                deleteBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");
                toggleBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3px 8px;");

                deleteBtn.setOnAction(event -> {
                    Word word = getTableView().getItems().get(getIndex());
                    boolean confirmed = AlertUtil.showConfirmation(
                            "Delete Word",
                            "Delete Word",
                            "Are you sure you want to delete '" + word.getWord() + "'?"
                    );
                    if (confirmed) {
                        AppContext.getInstance().getWordService().deleteWord(word.getId());
                        loadWords();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Word word = getTableView().getItems().get(getIndex());
                    toggleBtn.setText(word.isActive() ? "Disable" : "Enable");
                    toggleBtn.setOnAction(e -> {
                        AppContext.getInstance().getWordService().toggleWordStatus(word.getId(), !word.isActive());
                        loadWords();
                    });
                    setGraphic(pane);
                }
            }
        });

        tableView.getColumns().addAll(idCol, wordCol, statusCol, actionsCol);
        tableBox.getChildren().add(tableView);

        root.getChildren().addAll(addBar, statsRow, tableBox);
    }

    private void handleAddWord() {
        String input = newWordField.getText();
        try {
            Word added = AppContext.getInstance().getWordService().addWord(input);
            newWordField.clear();
            loadWords();
            AlertUtil.showInfo("Word Added", "Success", "Added word '" + added.getWord() + "'.");
        } catch (Exception ex) {
            AlertUtil.showError("Validation Error", "Cannot add word", ex.getMessage());
        }
    }

    private void loadWords() {
        List<Word> words = AppContext.getInstance().getWordService().getAllWords();
        tableView.setItems(FXCollections.observableArrayList(words));

        long activeCount = words.stream().filter(Word::isActive).count();
        totalWordsCard.setValue(String.valueOf(words.size()));
        activeWordsCard.setValue(String.valueOf(activeCount));
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
