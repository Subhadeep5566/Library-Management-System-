package com.library.management.desktop.ui.common;

import com.library.management.desktop.dto.BorrowResponse;
import com.library.management.desktop.dto.PageResponse;
import com.library.management.desktop.dto.ReturnCreateRequest;
import com.library.management.desktop.dto.UserResponse;
import com.library.management.desktop.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class ReturnCreateDialog extends Dialog<Boolean> {

    private final ApiService apiService;
    private ComboBox<BorrowResponse> borrowCombo;
    private ComboBox<UserResponse> processedByCombo;
    private javafx.scene.control.DatePicker returnDatePicker;
    private ComboBox<String> conditionCombo;
    private TextArea notesArea;

    private final ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    public ReturnCreateDialog(ApiService apiService) {
        this.apiService = apiService;
        setTitle("Add Return");
        setHeaderText("Create New Return Record");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        getDialogPane().setPrefWidth(550);

        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        createForm();
        loadReferenceData();

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

        // Borrow
        borrowCombo = new ComboBox<>();
        borrowCombo.setPromptText("Select active borrow");
        borrowCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BorrowResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getBookTitle() + " - " + item.getUserName() + " (Due: " + item.getDueDate() + ")");
                }
            }
        });
        borrowCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(BorrowResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getBookTitle() + " - " + item.getUserName() + " (Due: " + item.getDueDate() + ")");
                }
            }
        });
        grid.add(new Label("Borrow *:"), 0, 0);
        grid.add(borrowCombo, 1, 0);

        // Processed By
        processedByCombo = new ComboBox<>();
        processedByCombo.setPromptText("Select staff member");
        processedByCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(UserResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername() + " (" + item.getFullName() + ")");
            }
        });
        processedByCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UserResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername() + " (" + item.getFullName() + ")");
            }
        });
        grid.add(new Label("Processed By *:"), 0, 1);
        grid.add(processedByCombo, 1, 1);

        // Return Date
        returnDatePicker = new javafx.scene.control.DatePicker();
        returnDatePicker.setValue(LocalDate.now());
        grid.add(new Label("Return Date *:"), 0, 2);
        grid.add(returnDatePicker, 1, 2);

        // Condition
        conditionCombo = new ComboBox<>();
        conditionCombo.getItems().addAll("GOOD", "DAMAGED", "LOST");
        conditionCombo.setValue("GOOD");
        grid.add(new Label("Condition *:"), 0, 3);
        grid.add(conditionCombo, 1, 3);

        // Notes
        notesArea = new TextArea();
        notesArea.setPromptText("Optional notes about the return");
        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);
        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notesArea, 1, 4);

        getDialogPane().setContent(new ScrollPane(grid));
    }

    private void loadReferenceData() {
        // Load active borrows (BORROWED or OVERDUE status)
        apiService.getBorrows(0, 100, "borrowDate", "desc")
                .thenAccept(page -> Platform.runLater(() -> {
                    borrowCombo.getItems().setAll(page.getContent().stream()
                            .filter(b -> "BORROWED".equals(b.getStatus()) || "OVERDUE".equals(b.getStatus()))
                            .toList());
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Failed to load borrows: " + ex.getMessage()));
                    return null;
                });

        // Load librarians and admins
        apiService.getUsers(0, 100, "username", "asc")
                .thenAccept(page -> Platform.runLater(() -> {
                    var staffList = page.getContent().stream()
                            .filter(u -> "ACTIVE".equals(u.getStatus()) && ("LIBRARIAN".equals(u.getRole()) || "ADMIN".equals(u.getRole())))
                            .toList();
                    processedByCombo.getItems().setAll(staffList);
                    String current = apiService.getCurrentUsername();
                    if (current != null) {
                        staffList.stream()
                                .filter(u -> current.equalsIgnoreCase(u.getUsername()))
                                .findFirst()
                                .ifPresent(processedByCombo::setValue);
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Failed to load staff: " + ex.getMessage()));
                    return null;
                });
    }

    private boolean validateAndSave() {
        if (borrowCombo.getValue() == null) {
            showError("Borrow record is required");
            return false;
        }
        if (processedByCombo.getValue() == null) {
            showError("Processed by staff member is required");
            return false;
        }
        if (returnDatePicker.getValue() == null) {
            showError("Return date is required");
            return false;
        }
        if (returnDatePicker.getValue().isAfter(LocalDate.now())) {
            showError("Return date cannot be in the future");
            return false;
        }
        if (conditionCombo.getValue() == null) {
            showError("Condition is required");
            return false;
        }

        ReturnCreateRequest request = new ReturnCreateRequest();
        request.setBorrowId(borrowCombo.getValue().getId());
        request.setProcessedById(processedByCombo.getValue().getId());
        request.setReturnDate(returnDatePicker.getValue());
        request.setCondition(conditionCombo.getValue());
        request.setNotes(notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim());

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        apiService.createReturn(request)
                .thenAccept(response -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setResult(true);
                    close();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveButton.setDisable(false);
                        String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        showError("Failed to create return: " + errorMsg);
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