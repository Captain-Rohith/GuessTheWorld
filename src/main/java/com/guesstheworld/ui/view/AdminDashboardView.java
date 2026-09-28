package com.guesstheworld.ui.view;

import com.guesstheworld.config.AppContext;
import com.guesstheworld.model.User;
import com.guesstheworld.ui.NavigationManager;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class AdminDashboardView {

    private final BorderPane root;

    public AdminDashboardView() {
        root = new BorderPane();
        root.getStyleClass().add("app-container");

        buildUI();
    }

    private void buildUI() {
        User currentUser = AppContext.getInstance().getAuthService().getCurrentUser();
        if (currentUser == null) return;

        // Top Navigation Bar
        HBox topBar = new HBox(15);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label logoLabel = new Label("GUESS THE WORLD - ADMIN");
        logoLabel.getStyleClass().add("heading-2");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userBadge = new Label("Admin: " + currentUser.getUsername());
        userBadge.getStyleClass().addAll("badge-text", "badge-warning");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().addAll("button", "btn-secondary");
        logoutBtn.setOnAction(e -> NavigationManager.getInstance().showLogin());

        topBar.getChildren().addAll(logoLabel, spacer, userBadge, logoutBtn);
        root.setTop(topBar);

        // Center Tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab dailyReportTab = new Tab("Daily Report");
        dailyReportTab.setContent(new DailyReportView().getView());

        Tab userReportTab = new Tab("User Report");
        userReportTab.setContent(new UserReportView().getView());

        Tab wordMgmtTab = new Tab("Word Pool");
        wordMgmtTab.setContent(new WordManagementView().getView());

        tabPane.getTabs().addAll(dailyReportTab, userReportTab, wordMgmtTab);
        root.setCenter(tabPane);
    }

    public Parent getView() {
        return root;
    }
}
