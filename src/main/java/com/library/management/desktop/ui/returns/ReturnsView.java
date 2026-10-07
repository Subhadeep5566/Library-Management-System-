package com.library.management.desktop.ui.returns;

import com.library.management.desktop.dto.ReturnResponse;
import com.library.management.desktop.dto.PageResponse;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.ui.common.LoadingOverlay;
import com.library.management.desktop.ui.common.ReturnCreateDialog;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class ReturnsView {

    private final VBox root;
    private final ApiService apiService;
    private final ApiClient apiClient;
    private final LoadingOverlay loadingOverlay;

    private TableView<ReturnResponse> table;
    private TextField searchField;
    private ComboBox<Integer> pageSizeCombo;
    private Label pageInfoLabel;
    private Button prevButton;
    private Button nextButton;
    private Button refreshButton;
    private Button addButton;

    private int currentPage = 0;
    private int pageSize = 10;
    private String currentSearch = "";
    private String sortBy = "id";
    private String sortDir = "asc";

    public ReturnsView(ApiService apiService, ApiClient apiClient) {
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

        Label titleLabel = new Label("Returns");
        titleLabel.getStyleClass().add("entity-view-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search returns...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(250);
        searchField.setOnAction(e -> onSearch());

        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> loadData());

        addButton = new Button("Add Return");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddDialog());

        header.getChildren().addAll(titleLabel, spacer, searchField, refreshButton, addButton);

        // Table
        table = new TableView<>();
        table.getStyleClass().add("entity-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

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
            container, titleLabel, searchField, refreshButton, addButton, null, null,
            table, prevButton, pageInfoLabel, nextButton, pageSizeCombo, rootWrapper
        );

        return rootWrapper;
    }

    private void setupTableColumns() {
        TableColumn<ReturnResponse, Long> borrowCol = new TableColumn<>("Borrow ID");
        borrowCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getBorrowId()));
        borrowCol.setMinWidth(100);

        TableColumn<ReturnResponse, LocalDate> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getReturnDate()));
        returnDateCol.setMinWidth(120);

        TableColumn<ReturnResponse, String> conditionCol = new TableColumn<>("Condition");
        conditionCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCondition()));
        conditionCol.setMinWidth(130);
        conditionCol.setCellFactory(col -> new TableCell<>() {
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

        TableColumn<ReturnResponse, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNotes()));
        notesCol.setMinWidth(200);

        TableColumn<ReturnResponse, LocalDateTime> createdAtCol = new TableColumn<>("Created At");
        createdAtCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCreatedAt()));
        createdAtCol.setMinWidth(180);

        table.getColumns().addAll(borrowCol, returnDateCol, conditionCol, notesCol, createdAtCol);
    }

    public void loadData() {
        loadingOverlay.show();

        CompletableFuture<PageResponse<ReturnResponse>> future = apiService.getReturns(
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
                showError("Failed to load returns: " + ex.getMessage());
            });
            return null;
        });
    }

    private void onSearch() {
        currentSearch = searchField.getText().trim();
        currentPage = 0;
        if (!currentSearch.isEmpty()) {
            searchReturns();
        } else {
            loadData();
        }
    }

    private void searchReturns() {
        loadingOverlay.show();
        apiService.searchReturns(currentSearch, currentPage, pageSize)
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
                        showError("Failed to search returns: " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void goToPage(int page) {
        if (page >= 0) {
            currentPage = page;
            if (!currentSearch.isEmpty()) {
                searchReturns();
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
            table.setPlaceholder(com.library.management.desktop.theme.Theme.createEmptyStateNode(
                currentSearch.isEmpty() ? "No Return Records" : "No Matching Returns",
                "Returned volumes and their inspected conditions will appear here."
            ));
        } else {
            table.setPlaceholder(new Label("No data available"));
        }
    }

    private void showAddDialog() {
        ReturnCreateDialog dialog = new ReturnCreateDialog(apiService);
        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                loadData();
                showSuccess("Return processed successfully.");
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