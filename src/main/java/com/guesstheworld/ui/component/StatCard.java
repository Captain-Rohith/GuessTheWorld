package com.guesstheworld.ui.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StatCard extends VBox {

    private final Label valueLabel;
    private final Label titleLabel;

    public StatCard(String title, String initialValue, String valueColorHex) {
        setAlignment(Pos.CENTER);
        setSpacing(4);
        getStyleClass().add("stat-card");
        setMinWidth(150);
        setMinHeight(80);

        valueLabel = new Label(initialValue);
        valueLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 700; -fx-text-fill: " + valueColorHex + ";");

        titleLabel = new Label(title);
        titleLabel.getStyleClass().add("sub-text");

        getChildren().addAll(valueLabel, titleLabel);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
