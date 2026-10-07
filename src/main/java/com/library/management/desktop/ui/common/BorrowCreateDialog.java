package com.library.management.desktop.ui.common;

import com.library.management.desktop.dto.BookResponse;
import com.library.management.desktop.dto.BorrowCreateRequest;
import com.library.management.desktop.dto.PageResponse;
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

public class BorrowCreateDialog extends Dialog<Boolean> {

    private final ApiService apiService;
    private ComboBox<UserResponse> userCombo;
    private ComboBox<BookResponse> bookCombo;
    private javafx.scene.control.DatePicker borrowDatePicker;
    private javafx.scene.control.DatePicker dueDatePicker;

    private final ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    public BorrowCreateDialog(ApiService apiService) {
        this.apiService = apiService;
        setTitle("Add Borrow");
        setHeaderText("Create New Borrow Record");
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

        // User
        userCombo = new ComboBox<>();
        userCombo.setPromptText("Select user");
        userCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(UserResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername() + " (" + item.getFullName() + ")");
            }
        });
        userCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UserResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername() + " (" + item.getFullName() + ")");
            }
        });
        grid.add(new Label("User *:"), 0, 0);
        grid.add(userCombo, 1, 0);

        // Book
        bookCombo = new ComboBox<>();
        bookCombo.setPromptText("Select book");
        bookCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BookResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle() + " (ISBN: " + item.getIsbn() + ") - Avail: " + item.getAvailableCopies());
            }
        });
        bookCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(BookResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle() + " (ISBN: " + item.getIsbn() + ") - Avail: " + item.getAvailableCopies());
            }
        });
        grid.add(new Label("Book *:"), 0, 1);
        grid.add(bookCombo, 1, 1);

        // Borrow Date
        borrowDatePicker = new javafx.scene.control.DatePicker();
        borrowDatePicker.setValue(LocalDate.now());
        grid.add(new Label("Borrow Date *:"), 0, 2);
        grid.add(borrowDatePicker, 1, 2);

        // Due Date
        dueDatePicker = new javafx.scene.control.DatePicker();
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
        grid.add(new Label("Due Date *:"), 0, 3);
        grid.add(dueDatePicker, 1, 3);

        getDialogPane().setContent(new ScrollPane(grid));
    }

    private void loadReferenceData() {
        // Load users (only ACTIVE members)
        apiService.getUsers(0, 100, "username", "asc")
                .thenAccept(page -> Platform.runLater(() -> {
                    userCombo.getItems().setAll(page.getContent().stream()
                            .filter(u -> "ACTIVE".equals(u.getStatus()) && "MEMBER".equals(u.getRole()))
                            .toList());
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Failed to load users: " + ex.getMessage()));
                    return null;
                });

        // Load available books
        apiService.getBooks(0, 100, "title", "asc")
                .thenAccept(page -> Platform.runLater(() -> {
                    bookCombo.getItems().setAll(page.getContent().stream()
                            .filter(b -> "AVAILABLE".equals(b.getStatus()) && b.getAvailableCopies() > 0)
                            .toList());
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Failed to load books: " + ex.getMessage()));
                    return null;
                });
    }

    private boolean validateAndSave() {
        if (userCombo.getValue() == null) {
            showError("User is required");
            return false;
        }
        if (bookCombo.getValue() == null) {
            showError("Book is required");
            return false;
        }
        if (borrowDatePicker.getValue() == null) {
            showError("Borrow date is required");
            return false;
        }
        if (dueDatePicker.getValue() == null) {
            showError("Due date is required");
            return false;
        }
        if (dueDatePicker.getValue().isBefore(borrowDatePicker.getValue())) {
            showError("Due date must be after borrow date");
            return false;
        }
        if (borrowDatePicker.getValue().isAfter(LocalDate.now())) {
            showError("Borrow date cannot be in the future");
            return false;
        }

        BookResponse selectedBook = bookCombo.getValue();
        if (selectedBook.getAvailableCopies() <= 0) {
            showError("Selected book has no available copies");
            return false;
        }

        BorrowCreateRequest request = new BorrowCreateRequest();
        request.setUserId(userCombo.getValue().getId());
        request.setBookId(bookCombo.getValue().getId());
        request.setBorrowDate(borrowDatePicker.getValue());
        request.setDueDate(dueDatePicker.getValue());

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        apiService.createBorrow(request)
                .thenAccept(response -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setResult(true);
                    close();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveButton.setDisable(false);
                        String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        showError("Failed to create borrow: " + errorMsg);
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