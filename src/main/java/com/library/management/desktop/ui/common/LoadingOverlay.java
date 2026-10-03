package com.library.management.desktop.ui.common;

import com.library.management.desktop.theme.Theme;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class LoadingOverlay extends StackPane {

    private final VBox loadingBox;
    private final ProgressIndicator progressIndicator;
    private final Label messageLabel;

    public LoadingOverlay() {
        this.progressIndicator = new ProgressIndicator(-1);
        this.messageLabel = new Label("Loading...");

        progressIndicator.setPrefSize(42, 42);
        progressIndicator.setStyle("-fx-progress-color: #8B5CF6;");

        messageLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        messageLabel.setTextFill(Theme.TEXT_SECONDARY);

        this.loadingBox = new VBox(14);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(24, 32, 24, 32));
        loadingBox.setBackground(new Background(new BackgroundFill(Theme.BG_CARD, Theme.RADII_LARGE, Insets.EMPTY)));
        loadingBox.setBorder(new Border(new BorderStroke(Theme.BORDER_MUTED, BorderStrokeStyle.SOLID, Theme.RADII_LARGE, new BorderWidths(1))));

        DropShadow boxShadow = new DropShadow();
        boxShadow.setColor(Color.color(0.48, 0.22, 0.93, 0.3));
        boxShadow.setRadius(24);
        loadingBox.setEffect(boxShadow);

        loadingBox.getChildren().addAll(progressIndicator, messageLabel);

        this.setAlignment(Pos.CENTER);
        this.setBackground(new Background(new BackgroundFill(Color.color(0.03, 0.02, 0.06, 0.72), CornerRadii.EMPTY, Insets.EMPTY)));
        this.setVisible(false);
        this.setManaged(false);
        this.getChildren().add(loadingBox);
    }

    public void show() {
        this.setVisible(true);
        this.setManaged(true);
        this.toFront();
        FadeTransition ft = new FadeTransition(Duration.millis(150), this);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    public void hide() {
        FadeTransition ft = new FadeTransition(Duration.millis(150), this);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> {
            this.setVisible(false);
            this.setManaged(false);
        });
        ft.play();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}