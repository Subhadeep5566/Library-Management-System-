package com.library.management.desktop.ui.books;

import com.library.management.desktop.dto.BookResponse;
import com.library.management.desktop.dto.PageResponse;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.ui.common.BookCreateDialog;
import com.library.management.desktop.ui.common.BookEditDialog;
import com.library.management.desktop.ui.common.LoadingOverlay;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.concurrent.CompletableFuture;

public class BooksView {

    private final VBox root;
    private final ApiService apiService;
    private final ApiClient apiClient;
    private final LoadingOverlay loadingOverlay;

    private TableView<BookResponse> table;
    private TextField searchField;
    private ComboBox<Integer> pageSizeCombo;
    private Label pageInfoLabel;
    private Button prevButton;
    private Button nextButton;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button refreshButton;

    private int currentPage = 0;
    private int pageSize = 10;
    private String currentSearch = "";
    private String sortBy = "id";
    private String sortDir = "asc";

    public BooksView(ApiService apiService, ApiClient apiClient) {
        this.apiService = apiService;
        this.apiClient = apiClient;
        this.loadingOverlay = new LoadingOverlay();
        this.root = createLayout();
    }

    private VBox createLayout() {
        VBox container = new VBox(15);
        container.getStyleClass().add("entity-view-container");
        container.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 15, 0));

        Label titleLabel = new Label("Books");
        titleLabel.getStyleClass().add("entity-view-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search books...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(250);
        searchField.setOnAction(e -> onSearch());

        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> loadData());

        addButton = new Button("Add Book");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddDialog());

        editButton = new Button("Edit");
        editButton.getStyleClass().add("secondary-button");
        editButton.setDisable(true);
        editButton.setOnAction(e -> showEditDialog());

        deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setDisable(true);
        deleteButton.setOnAction(e -> onDelete());

        header.getChildren().addAll(titleLabel, spacer, searchField, refreshButton, addButton, editButton, deleteButton);

        // Table
        table = new TableView<>();
        table.getStyleClass().add("entity-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });

        setupTableColumns();

        // Pagination
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER);
        pagination.setPadding(new Insets(15, 0, 0, 0));

        prevButton = new Button("Previous");
        prevButton.getStyleClass().add("pagination-button");
        prevButton.setDisable(true);
        prevButton.setOnAction(e -> goToPage(currentPage - 1));

        pageInfoLabel = new Label("Page 1 of 1");
        pageInfoLabel.getStyleClass().add("page-info");

        nextButton = new Button("Next");
        nextButton.getStyleClass().add("pagination-button");
        nextButton.setDisable(true);
        nextButton.setOnAction(e -> goToPage(currentPage + 1));

        pageSizeCombo = new ComboBox<>();
        pageSizeCombo.getItems().addAll(10, 25, 50, 100);
        pageSizeCombo.setValue(pageSize);
        pageSizeCombo.setOnAction(e -> {
            pageSize = pageSizeCombo.getValue();
            currentPage = 0;
            loadData();
        });

        pagination.getChildren().addAll(prevButton, pageInfoLabel, nextButton, new Label("Page Size:"), pageSizeCombo);

        // Main layout
        VBox mainContent = new VBox(15);
        mainContent.getChildren().addAll(header, table, pagination);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Loading overlay
        StackPane wrapper = new StackPane(mainContent, loadingOverlay);
        StackPane.setAlignment(loadingOverlay, Pos.CENTER);

        VBox rootWrapper = new VBox(wrapper);
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        com.library.management.desktop.theme.Theme.styleEntityView(
            container, titleLabel, searchField, refreshButton, addButton, editButton, deleteButton,
            table, prevButton, pageInfoLabel, nextButton, pageSizeCombo, rootWrapper
        );

        return rootWrapper;
    }

    private void setupTableColumns() {
        TableColumn<BookResponse, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTitle()));
        titleCol.setMinWidth(200);

        TableColumn<BookResponse, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getIsbn()));
        isbnCol.setMinWidth(140);

        TableColumn<BookResponse, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAuthorName()));
        authorCol.setMinWidth(150);

        TableColumn<BookResponse, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategoryName()));
        categoryCol.setMinWidth(150);

        TableColumn<BookResponse, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        statusCol.setMinWidth(120);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: " + getStatusColor(item) + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<BookResponse, Integer> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getAvailableCopies()));
        availableCol.setMinWidth(80);

        TableColumn<BookResponse, Integer> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getTotalCopies()));
        totalCol.setMinWidth(80);

        table.getColumns().addAll(titleCol, isbnCol, authorCol, categoryCol, statusCol, availableCol, totalCol);
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "AVAILABLE" -> "#10B981";
            case "BORROWED" -> "#F59E0B";
            case "RESERVED" -> "#A78BFA";
            case "LOST" -> "#EF4444";
            case "DAMAGED" -> "#F97316";
            case "UNDER_MAINTENANCE" -> "#8B7FA3";
            default -> "#8B7FA3";
        };
    }

    public void loadData() {
        loadingOverlay.show();

        CompletableFuture<PageResponse<BookResponse>> future = apiService.getBooks(
                currentPage, pageSize, sortBy, sortDir);

        future.thenAccept(page -> {
            Platform.runLater(() -> {
                table.getItems().setAll(page.getContent());
                updatePagination(page);
                loadingOverlay.hide();
                updateEmptyState(page.getContent().isEmpty());
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                loadingOverlay.hide();
                showError("Failed to load books: " + ex.getMessage());
            });
            return null;
        });
    }

    private void onSearch() {
        currentSearch = searchField.getText().trim();
        currentPage = 0;
        if (!currentSearch.isEmpty()) {
            searchBooks();
        } else {
            loadData();
        }
    }

    private void searchBooks() {
        loadingOverlay.show();
        apiService.searchBooks(currentSearch, currentPage, pageSize)
                .thenAccept(page -> {
                    Platform.runLater(() -> {
                        table.getItems().setAll(page.getContent());
                        updatePagination(page);
                        loadingOverlay.hide();
                        updateEmptyState(page.getContent().isEmpty());
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        loadingOverlay.hide();
                        showError("Failed to search books: " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void goToPage(int page) {
        if (page >= 0) {
            currentPage = page;
            if (!currentSearch.isEmpty()) {
                searchBooks();
            } else {
                loadData();
            }
        }
    }

    private void updatePagination(PageResponse<?> page) {
        pageInfoLabel.setText("Page " + (page.getPageNumber() + 1) + " of " + Math.max(1, page.getTotalPages()));
        prevButton.setDisable(page.isFirst());
        nextButton.setDisable(page.isLast());
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            Label emptyLabel = new Label(currentSearch.isEmpty() ? "No books found" : "No books match your search");
            emptyLabel.getStyleClass().add("empty-state-label");
            table.setPlaceholder(emptyLabel);
        } else {
            table.setPlaceholder(new Label("No data available"));
        }
    }

    private void showAddDialog() {
        BookCreateDialog dialog = new BookCreateDialog(apiService);
        dialog.showAndWait().ifPresent(result -> {
            if (result) loadData();
        });
    }

    private void showEditDialog() {
        BookResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            BookEditDialog dialog = new BookEditDialog(apiService, selected);
            dialog.showAndWait().ifPresent(result -> {
                if (result) loadData();
            });
        }
    }

    private void onDelete() {
        BookResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete \"" + selected.getTitle() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Book");
        com.library.management.desktop.theme.Theme.styleDialog(confirm);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loadingOverlay.show();
                apiService.deleteBook(selected.getId())
                        .thenRun(() -> Platform.runLater(() -> {
                            loadingOverlay.hide();
                            loadData();
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingOverlay.hide();
                                showError("Failed to delete book: " + ex.getMessage());
                            });
                            return null;
                        });
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        com.library.management.desktop.theme.Theme.styleDialog(alert);
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}