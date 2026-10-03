package com.library.management.desktop.ui.common;

import com.library.management.desktop.dto.BookResponse;
import com.library.management.desktop.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class EntityDetailDialog {

    public static Optional<ButtonType> showDetail(Stage owner, String title, String content) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(true);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setFont(new Font(14));
        contentLabel.setTextFill(com.library.management.desktop.theme.Theme.TEXT_PRIMARY);
        contentLabel.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(contentLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(600);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        com.library.management.desktop.theme.Theme.styleDialog(dialog);

        return dialog.showAndWait();
    }
}