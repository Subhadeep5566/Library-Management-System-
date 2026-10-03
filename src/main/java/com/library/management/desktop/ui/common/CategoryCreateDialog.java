package com.library.management.desktop.ui.common;

import com.library.management.desktop.dto.CategoryResponse;
import com.library.management.desktop.dto.CategoryCreateRequest;
import com.library.management.desktop.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

public class CategoryCreateDialog extends Dialog<Boolean> {

    private final ApiService apiService;
    private TextField nameField;
    private TextArea descriptionArea;

    public CategoryCreateDialog(ApiService apiService) {
        this.apiService = apiService;
        setTitle("Add Category");
        setHeaderText("Create New Category");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        getDialogPane().setPrefWidth(500);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
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
        nameField.setPromptText("Enter category name");
        grid.add(new Label("Name *:"), 0, 0);
        grid.add(nameField, 1, 0);

        // Description
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Category description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        getDialogPane().setContent(new ScrollPane(grid));
    }

    private boolean validateAndSave() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Name is required");
            return false;
        }

        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName(nameField.getText().trim());
        request.setDescription(descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        apiService.createCategory(request)
                .thenAccept(response -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    showSuccess("Category created successfully!");
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveButton.setDisable(false);
                        showError("Failed to create category: " + ex.getMessage());
                    });
                    return null;
                });

        return false;
    }

    private ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Validation Error");
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}