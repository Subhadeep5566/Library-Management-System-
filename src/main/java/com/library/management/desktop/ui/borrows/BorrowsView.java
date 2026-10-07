package com.library.management.desktop.ui.borrows;

import com.library.management.desktop.dto.BorrowResponse;
import com.library.management.desktop.dto.PageResponse;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.ui.common.BorrowCreateDialog;
import com.library.management.desktop.ui.common.LoadingOverlay;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class BorrowsView {

    private final VBox root;
    private final ApiService apiService;
    private final ApiClient apiClient;
    private final LoadingOverlay loadingOverlay;

    private TableView<BorrowResponse> table;
    private TextField searchField;
    private ComboBox<Integer> pageSizeCombo;
    private Label pageInfoLabel;
    private Button prevButton;
    private Button nextButton;
    private Button addButton;
    private Button returnButton;
    private Button lostButton;
    private Button refreshButton;

    private int currentPage = 0;
    private int pageSize = 10;
    private String currentSearch = "";
    private String sortBy = "id";
    private String sortDir = "asc";

    public BorrowsView(ApiService apiService, ApiClient apiClient) {
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

        Label titleLabel = new Label("Borrows");
        titleLabel.getStyleClass().add("entity-view-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search borrows...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(250);
        searchField.setOnAction(e -> onSearch());

        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> loadData());

        addButton = new Button("Add Borrow");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddDialog());

        returnButton = new Button("Return");
        returnButton.getStyleClass().add("primary-button");
        returnButton.setDisable(true);
        returnButton.setOnAction(e -> onReturn());

        lostButton = new Button("Mark Lost");
        lostButton.getStyleClass().add("danger-button");
        lostButton.setDisable(true);
        lostButton.setOnAction(e -> onMarkLost());

        header.getChildren().addAll(titleLabel, spacer, searchField, refreshButton, addButton, returnButton, lostButton);

        // Table
        table = new TableView<>();
        table.getStyleClass().add("entity-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            boolean canAction = hasSelection && ("BORROWED".equals(newSel.getStatus()) || "OVERDUE".equals(newSel.getStatus()));
            returnButton.setDisable(!canAction);
            lostButton.setDisable(!canAction);
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
            container, titleLabel, searchField, refreshButton, addButton, returnButton, lostButton,
            table, prevButton, pageInfoLabel, nextButton, pageSizeCombo, rootWrapper
        );

        return rootWrapper;
    }

    private void setupTableColumns() {
        TableColumn<BorrowResponse, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUserName()));
        userCol.setMinWidth(150);

        TableColumn<BorrowResponse, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getBookTitle()));
        bookCol.setMinWidth(200);

        TableColumn<BorrowResponse, LocalDate> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getBorrowDate()));
        borrowDateCol.setMinWidth(120);

        TableColumn<BorrowResponse, LocalDate> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getDueDate()));
        dueDateCol.setMinWidth(120);

        TableColumn<BorrowResponse, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        statusCol.setMinWidth(130);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(com.library.management.desktop.theme.Theme.createStatusBadge(item, item));
                }
            }
        });

        TableColumn<BorrowResponse, LocalDate> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getReturnDate()));
        returnDateCol.setMinWidth(120);

        TableColumn<BorrowResponse, Boolean> overdueCol = new TableColumn<>("Overdue");
        overdueCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getOverdue()));
        overdueCol.setMinWidth(80);

        table.getColumns().addAll(userCol, bookCol, borrowDateCol, dueDateCol, statusCol, returnDateCol, overdueCol);
    }

    public void loadData() {
        loadingOverlay.show();

        CompletableFuture<PageResponse<BorrowResponse>> future = apiService.getBorrows(
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
                showError("Failed to load borrows: " + ex.getMessage());
            });
            return null;
        });
    }

    private void onSearch() {
        currentSearch = searchField.getText().trim();
        currentPage = 0;
        if (!currentSearch.isEmpty()) {
            searchBorrows();
        } else {
            loadData();
        }
    }

    private void searchBorrows() {
        loadingOverlay.show();
        apiService.searchBorrows(currentSearch, currentPage, pageSize)
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
                        showError("Failed to search borrows: " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void goToPage(int page) {
        if (page >= 0) {
            currentPage = page;
            if (!currentSearch.isEmpty()) {
                searchBorrows();
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
            Label emptyLabel = new Label(currentSearch.isEmpty() ? "No borrows found" : "No borrows match your search");
            emptyLabel.getStyleClass().add("empty-state-label");
            table.setPlaceholder(emptyLabel);
        } else {
            table.setPlaceholder(new Label("No data available"));
        }
    }

    private String getStatusColor(String status) {
        if (status == null) return "#8B7FA3";
        return switch (status.toUpperCase()) {
            case "BORROWED" -> "#F59E0B";
            case "RETURNED" -> "#10B981";
            case "OVERDUE" -> "#EF4444";
            case "LOST" -> "#F87171";
            default -> "#8B7FA3";
        };
    }

    private void showAddDialog() {
        BorrowCreateDialog dialog = new BorrowCreateDialog(apiService);
        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                loadData();
                showSuccess("Book borrowed successfully.");
            }
        });
    }

    private void onReturn() {
        BorrowResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Return book \"" + selected.getBookTitle() + "\" for user " + selected.getUserName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Return");
        confirm.setHeaderText("Return Book");
        com.library.management.desktop.theme.Theme.styleDialog(confirm);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loadingOverlay.show();
                apiService.returnBook(selected.getId())
                        .thenRun(() -> Platform.runLater(() -> {
                            loadingOverlay.hide();
                            loadData();
                            showSuccess("Book returned successfully.");
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingOverlay.hide();
                                showError("Failed to return book: " + ex.getMessage());
                            });
                            return null;
                        });
            }
        });
    }

    private void onMarkLost() {
        BorrowResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Mark book \"" + selected.getBookTitle() + "\" as LOST for user " + selected.getUserName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Mark Lost");
        confirm.setHeaderText("Mark Book Lost");
        com.library.management.desktop.theme.Theme.styleDialog(confirm);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loadingOverlay.show();
                apiService.markLost(selected.getId())
                        .thenRun(() -> Platform.runLater(() -> {
                            loadingOverlay.hide();
                            loadData();
                            showSuccess("Book marked as lost.");
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingOverlay.hide();
                                showError("Failed to mark book as lost: " + ex.getMessage());
                            });
                            return null;
                        });
            }
        });
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        com.library.management.desktop.theme.Theme.styleDialog(alert);
        alert.showAndWait();
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