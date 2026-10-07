package com.library.management.desktop.ui.fines;

import com.library.management.desktop.dto.FineResponse;
import com.library.management.desktop.dto.PageResponse;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.ui.common.LoadingOverlay;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class FinesView {

    private final VBox root;
    private final ApiService apiService;
    private final ApiClient apiClient;
    private final LoadingOverlay loadingOverlay;

    private TableView<FineResponse> table;
    private TextField searchField;
    private ComboBox<Integer> pageSizeCombo;
    private Label pageInfoLabel;
    private Button prevButton;
    private Button nextButton;
    private Button refreshButton;
    private Button payButton;
    private Button waiveButton;
    private Button generateButton;

    private int currentPage = 0;
    private int pageSize = 10;
    private String currentSearch = "";
    private String sortBy = "id";
    private String sortDir = "asc";

    public FinesView(ApiService apiService, ApiClient apiClient) {
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

        Label titleLabel = new Label("Fines");
        titleLabel.getStyleClass().add("entity-view-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search fines...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(250);
        searchField.setOnAction(e -> onSearch());

        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> loadData());

        generateButton = new Button("Generate Fines");
        generateButton.getStyleClass().add("primary-button");
        generateButton.setOnAction(e -> onGenerateFines());

        payButton = new Button("Mark Paid");
        payButton.getStyleClass().add("primary-button");
        payButton.setDisable(true);
        payButton.setOnAction(e -> onPayFine());

        waiveButton = new Button("Waive");
        waiveButton.getStyleClass().add("secondary-button");
        waiveButton.setDisable(true);
        waiveButton.setOnAction(e -> onWaiveFine());

        header.getChildren().addAll(titleLabel, spacer, searchField, refreshButton, generateButton, payButton, waiveButton);

        // Table
        table = new TableView<>();
        table.getStyleClass().add("entity-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            payButton.setDisable(!hasSelection);
            waiveButton.setDisable(!hasSelection);
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
            container, titleLabel, searchField, refreshButton, generateButton, waiveButton, null,
            table, prevButton, pageInfoLabel, nextButton, pageSizeCombo, rootWrapper
        );
        com.library.management.desktop.theme.Theme.stylePrimaryButton(payButton);
        payButton.setPrefHeight(38);

        return rootWrapper;
    }

    private void setupTableColumns() {
        TableColumn<FineResponse, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setMinWidth(60);

        TableColumn<FineResponse, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUsername()));
        userCol.setMinWidth(150);

        TableColumn<FineResponse, Long> borrowCol = new TableColumn<>("Borrow ID");
        borrowCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getBorrowId()));
        borrowCol.setMinWidth(100);

        TableColumn<FineResponse, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getAmount()));
        amountCol.setMinWidth(100);
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("$" + item.toString());
                }
            }
        });

        TableColumn<FineResponse, Integer> daysOverdueCol = new TableColumn<>("Days Overdue");
        daysOverdueCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getDaysOverdue()));
        daysOverdueCol.setMinWidth(120);

        TableColumn<FineResponse, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getReason()));
        reasonCol.setMinWidth(200);

        TableColumn<FineResponse, String> statusCol = new TableColumn<>("Status");
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

        TableColumn<FineResponse, LocalDate> fineDateCol = new TableColumn<>("Fine Date");
        fineDateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getFineDate()));
        fineDateCol.setMinWidth(120);

        TableColumn<FineResponse, LocalDate> paidDateCol = new TableColumn<>("Paid Date");
        paidDateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getPaidDate()));
        paidDateCol.setMinWidth(120);

        table.getColumns().addAll(idCol, userCol, borrowCol, amountCol, daysOverdueCol, reasonCol, statusCol, fineDateCol, paidDateCol);
    }

    public void loadData() {
        loadingOverlay.show();

        CompletableFuture<PageResponse<FineResponse>> future = apiService.getFines(
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
                showError("Failed to load fines: " + ex.getMessage());
            });
            return null;
        });
    }

    private void onSearch() {
        currentSearch = searchField.getText().trim();
        currentPage = 0;
        if (!currentSearch.isEmpty()) {
            searchFines();
        } else {
            loadData();
        }
    }

    private void searchFines() {
        loadingOverlay.show();
        apiService.searchFines(currentSearch, currentPage, pageSize)
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
                        showError("Failed to search fines: " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void goToPage(int page) {
        if (page >= 0) {
            currentPage = page;
            if (!currentSearch.isEmpty()) {
                searchFines();
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
                currentSearch.isEmpty() ? "No Fines Outstanding" : "No Matching Fines",
                "Library overdue penalties and payment records will appear here."
            ));
        } else {
            table.setPlaceholder(new Label("No data available"));
        }
    }

    private void onGenerateFines() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Generate fines for all overdue books?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Generate Fines");
        confirm.setHeaderText("Generate Overdue Fines");
        com.library.management.desktop.theme.Theme.styleDialog(confirm);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loadingOverlay.show();
                apiService.generateFines()
                        .thenRun(() -> Platform.runLater(() -> {
                            loadingOverlay.hide();
                            loadData();
                            showSuccess("Fines generated successfully!");
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingOverlay.hide();
                                showError("Failed to generate fines: " + ex.getMessage());
                            });
                            return null;
                        });
            }
        });
    }

    private void onPayFine() {
        FineResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Mark fine of $" + selected.getAmount() + " as PAID for user " + selected.getUsername() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Payment");
        confirm.setHeaderText("Pay Fine");
        com.library.management.desktop.theme.Theme.styleDialog(confirm);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loadingOverlay.show();
                apiService.payFine(selected.getId())
                        .thenRun(() -> Platform.runLater(() -> {
                            loadingOverlay.hide();
                            loadData();
                            showSuccess("Fine marked as paid successfully.");
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingOverlay.hide();
                                showError("Failed to pay fine: " + ex.getMessage());
                            });
                            return null;
                        });
            }
        });
    }

    private void onWaiveFine() {
        FineResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Waive fine of $" + selected.getAmount() + " for user " + selected.getUsername() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Waive");
        confirm.setHeaderText("Waive Fine");
        com.library.management.desktop.theme.Theme.styleDialog(confirm);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loadingOverlay.show();
                apiService.waiveFine(selected.getId())
                        .thenRun(() -> Platform.runLater(() -> {
                            loadingOverlay.hide();
                            loadData();
                            showSuccess("Fine waived successfully.");
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingOverlay.hide();
                                showError("Failed to waive fine: " + ex.getMessage());
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

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        com.library.management.desktop.theme.Theme.styleDialog(alert);
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}