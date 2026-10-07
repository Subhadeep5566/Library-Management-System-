package com.library.management.desktop.ui.common;

import com.library.management.desktop.dto.AuthorResponse;
import com.library.management.desktop.dto.BookResponse;
import com.library.management.desktop.dto.BookUpdateRequest;
import com.library.management.desktop.dto.CategoryResponse;
import com.library.management.desktop.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

import java.math.BigDecimal;

public class BookEditDialog extends Dialog<Boolean> {

    private final ApiService apiService;
    private final BookResponse book;
    private ComboBox<AuthorResponse> authorCombo;
    private ComboBox<CategoryResponse> categoryCombo;
    private TextField titleField;
    private TextField isbnField;
    private Spinner<Integer> totalCopiesSpinner;
    private Spinner<Integer> availableCopiesSpinner;
    private Spinner<Integer> publicationYearSpinner;
    private TextField publisherField;
    private TextField languageField;
    private Spinner<Integer> pageCountSpinner;
    private TextField priceField;
    private TextField shelfLocationField;
    private TextArea descriptionArea;
    private ComboBox<String> statusCombo;

    private final ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    public BookEditDialog(ApiService apiService, BookResponse book) {
        this.apiService = apiService;
        this.book = book;
        setTitle("Edit Book");
        setHeaderText("Edit Book: " + book.getTitle());
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        getDialogPane().setPrefWidth(500);

        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        createForm();
        loadReferenceData();
        populateFields();

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

        // Title
        titleField = new TextField();
        titleField.setPromptText("Enter book title");
        grid.add(new Label("Title *:"), 0, 0);
        grid.add(titleField, 1, 0);

        // ISBN
        isbnField = new TextField();
        isbnField.setPromptText("Enter ISBN");
        grid.add(new Label("ISBN *:"), 0, 1);
        grid.add(isbnField, 1, 1);

        // Author
        authorCombo = new ComboBox<>();
        authorCombo.setPromptText("Select author");
        authorCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(AuthorResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        authorCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(AuthorResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        grid.add(new Label("Author *:"), 0, 2);
        grid.add(authorCombo, 1, 2);

        // Category
        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Select category");
        categoryCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CategoryResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        categoryCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CategoryResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        grid.add(new Label("Category *:"), 0, 3);
        grid.add(categoryCombo, 1, 3);

        // Total Copies
        totalCopiesSpinner = new Spinner<>(1, 1000, 1);
        grid.add(new Label("Total Copies *:"), 0, 4);
        grid.add(totalCopiesSpinner, 1, 4);

        // Available Copies
        availableCopiesSpinner = new Spinner<>(0, 1000, 1);
        grid.add(new Label("Available Copies *:"), 0, 5);
        grid.add(availableCopiesSpinner, 1, 5);

        // Status
        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("AVAILABLE", "BORROWED", "RESERVED", "LOST", "DAMAGED", "UNDER_MAINTENANCE");
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusCombo, 1, 6);

        // Publication Year
        publicationYearSpinner = new Spinner<>(1000, java.time.Year.now().getValue() + 1, java.time.Year.now().getValue());
        grid.add(new Label("Publication Year:"), 0, 7);
        grid.add(publicationYearSpinner, 1, 7);

        // Publisher
        publisherField = new TextField();
        publisherField.setPromptText("Publisher name");
        grid.add(new Label("Publisher:"), 0, 8);
        grid.add(publisherField, 1, 8);

        // Language
        languageField = new TextField();
        languageField.setPromptText("e.g., English");
        grid.add(new Label("Language:"), 0, 9);
        grid.add(languageField, 1, 9);

        // Page Count
        pageCountSpinner = new Spinner<>(0, 10000, 0);
        grid.add(new Label("Page Count:"), 0, 10);
        grid.add(pageCountSpinner, 1, 10);

        // Price
        priceField = new TextField();
        priceField.setPromptText("e.g., 29.99");
        grid.add(new Label("Price:"), 0, 11);
        grid.add(priceField, 1, 11);

        // Shelf Location
        shelfLocationField = new TextField();
        shelfLocationField.setPromptText("e.g., A-1-1");
        grid.add(new Label("Shelf Location:"), 0, 12);
        grid.add(shelfLocationField, 1, 12);

        // Description
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Book description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        grid.add(new Label("Description:"), 0, 13);
        grid.add(descriptionArea, 1, 13);

        getDialogPane().setContent(new ScrollPane(grid));
    }

    private void loadReferenceData() {
        apiService.getAuthors(0, 100, "name", "asc")
                .thenAccept(page -> Platform.runLater(() -> {
                    authorCombo.getItems().setAll(page.getContent());
                    if (book.getAuthorId() != null) {
                        authorCombo.getItems().stream()
                                .filter(a -> a.getId().equals(book.getAuthorId()))
                                .findFirst()
                                .ifPresent(authorCombo::setValue);
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Failed to load authors: " + ex.getMessage()));
                    return null;
                });

        apiService.getCategories(0, 100, "name", "asc")
                .thenAccept(page -> Platform.runLater(() -> {
                    categoryCombo.getItems().setAll(page.getContent());
                    if (book.getCategoryId() != null) {
                        categoryCombo.getItems().stream()
                                .filter(c -> c.getId().equals(book.getCategoryId()))
                                .findFirst()
                                .ifPresent(categoryCombo::setValue);
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Failed to load categories: " + ex.getMessage()));
                    return null;
                });
    }

    private void populateFields() {
        titleField.setText(book.getTitle());
        isbnField.setText(book.getIsbn());
        totalCopiesSpinner.getValueFactory().setValue(book.getTotalCopies());
        availableCopiesSpinner.getValueFactory().setValue(book.getAvailableCopies());
        statusCombo.setValue(book.getStatus());
        publicationYearSpinner.getValueFactory().setValue(book.getPublicationYear() != null ? book.getPublicationYear() : java.time.Year.now().getValue());
        publisherField.setText(book.getPublisher());
        languageField.setText(book.getLanguage());
        pageCountSpinner.getValueFactory().setValue(book.getPageCount() != null ? book.getPageCount() : 0);
        priceField.setText(book.getPrice() != null ? book.getPrice().toString() : "");
        shelfLocationField.setText(book.getShelfLocation());
        descriptionArea.setText(book.getDescription());
    }

    private boolean validateAndSave() {
        if (titleField.getText().trim().isEmpty()) {
            showError("Title is required");
            return false;
        }
        if (isbnField.getText().trim().isEmpty()) {
            showError("ISBN is required");
            return false;
        }
        if (authorCombo.getValue() == null) {
            showError("Author is required");
            return false;
        }
        if (categoryCombo.getValue() == null) {
            showError("Category is required");
            return false;
        }

        BookUpdateRequest request = new BookUpdateRequest();
        request.setTitle(titleField.getText().trim());
        request.setIsbn(isbnField.getText().trim());
        request.setAuthorId(authorCombo.getValue().getId());
        request.setCategoryId(categoryCombo.getValue().getId());
        request.setTotalCopies(totalCopiesSpinner.getValue());
        request.setAvailableCopies(availableCopiesSpinner.getValue());
        request.setStatus(statusCombo.getValue());
        request.setPublicationYear(publicationYearSpinner.getValue());
        request.setPublisher(publisherField.getText().trim().isEmpty() ? null : publisherField.getText().trim());
        request.setLanguage(languageField.getText().trim().isEmpty() ? null : languageField.getText().trim());
        request.setPageCount(pageCountSpinner.getValue());
        request.setPrice(priceField.getText().trim().isEmpty() ? null : new BigDecimal(priceField.getText().trim()));
        request.setShelfLocation(shelfLocationField.getText().trim().isEmpty() ? null : shelfLocationField.getText().trim());
        request.setDescription(descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        apiService.updateBook(book.getId(), request)
                .thenAccept(response -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setResult(true);
                    close();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveButton.setDisable(false);
                        String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        showError("Failed to update book: " + errorMsg);
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