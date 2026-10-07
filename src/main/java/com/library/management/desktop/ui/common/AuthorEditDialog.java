package com.library.management.desktop.ui.common;

import com.library.management.desktop.dto.AuthorResponse;
import com.library.management.desktop.dto.AuthorCreateRequest;
import com.library.management.desktop.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

import java.time.LocalDate;

public class AuthorEditDialog extends Dialog<Boolean> {

    private final ApiService apiService;
    private final AuthorResponse author;

    private TextField nameField;
    private TextArea biographyArea;
    private javafx.scene.control.DatePicker birthDatePicker;
    private javafx.scene.control.DatePicker deathDatePicker;
    private TextField nationalityField;

    private final ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    public AuthorEditDialog(ApiService apiService, AuthorResponse author) {
        this.apiService = apiService;
        this.author = author;
        setTitle("Edit Author");
        setHeaderText("Edit Author: " + author.getName());
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        getDialogPane().setPrefWidth(500);

        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        createForm();

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateAndSave()) {
                event.consume();
            }
        });
        com.library.management.desktop.theme.Theme.styleDialog(this);
    }

    private void createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER_LEFT);

        // Name
        nameField = new TextField();
        nameField.setPromptText("Enter author name");
        nameField.setText(author.getName());
        grid.add(new Label("Name *:"), 0, 0);
        grid.add(nameField, 1, 0);

        // Biography
        biographyArea = new TextArea();
        biographyArea.setPromptText("Author biography");
        biographyArea.setPrefRowCount(3);
        biographyArea.setWrapText(true);
        biographyArea.setText(author.getBiography() != null ? author.getBiography() : "");
        grid.add(new Label("Biography:"), 0, 1);
        grid.add(biographyArea, 1, 1);

        // Birth Date
        birthDatePicker = new javafx.scene.control.DatePicker();
        birthDatePicker.setPromptText("Select birth date");
        birthDatePicker.setValue(author.getBirthDate());
        grid.add(new Label("Birth Date:"), 0, 2);
        grid.add(birthDatePicker, 1, 2);

        // Death Date
        deathDatePicker = new javafx.scene.control.DatePicker();
        deathDatePicker.setPromptText("Select death date (if applicable)");
        deathDatePicker.setValue(author.getDeathDate());
        grid.add(new Label("Death Date:"), 0, 3);
        grid.add(deathDatePicker, 1, 3);

        // Nationality
        nationalityField = new TextField();
        nationalityField.setPromptText("e.g., American, British");
        nationalityField.setText(author.getNationality() != null ? author.getNationality() : "");
        grid.add(new Label("Nationality:"), 0, 4);
        grid.add(nationalityField, 1, 4);

        getDialogPane().setContent(new ScrollPane(grid));
    }

    private boolean validateAndSave() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Name is required");
            return false;
        }

        AuthorCreateRequest request = new AuthorCreateRequest();
        request.setName(nameField.getText().trim());
        request.setBiography(biographyArea.getText().trim().isEmpty() ? null : biographyArea.getText().trim());
        request.setBirthDate(birthDatePicker.getValue());
        request.setDeathDate(deathDatePicker.getValue());
        request.setNationality(nationalityField.getText().trim().isEmpty() ? null : nationalityField.getText().trim());

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        apiService.updateAuthor(author.getId(), request)
                .thenAccept(response -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setResult(true);
                    close();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveButton.setDisable(false);
                        String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        showError("Failed to update author: " + errorMsg);
                    });
                    return null;
                });

        return false;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Validation Error");
        com.library.management.desktop.theme.Theme.styleDialog(alert);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        com.library.management.desktop.theme.Theme.styleDialog(alert);
        alert.showAndWait();
    }
}