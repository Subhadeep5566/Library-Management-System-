package com.library.management.desktop.ui.authors;

import com.library.management.desktop.dto.AuthorResponse;
import com.library.management.desktop.dto.PageResponse;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.ui.common.AuthorCreateDialog;
import com.library.management.desktop.ui.common.AuthorEditDialog;
import com.library.management.desktop.ui.common.LoadingOverlay;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class AuthorsView {

    private final VBox root;
    private final ApiService apiService;
    private final ApiClient apiClient;
    private final LoadingOverlay loadingOverlay;

    private TableView<AuthorResponse> table;
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

    public AuthorsView(ApiService apiService, ApiClient apiClient) {
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

        Label titleLabel = new Label("Authors");
        titleLabel.getStyleClass().add("entity-view-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search authors...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(250);
        searchField.setOnAction(e -> onSearch());

        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> loadData());

        addButton = new Button("Add Author");
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
        TableColumn<AuthorResponse, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        nameCol.setMinWidth(200);

        TableColumn<AuthorResponse, String> nationalityCol = new TableColumn<>("Nationality");
        nationalityCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNationality()));
        nationalityCol.setMinWidth(150);

        TableColumn<AuthorResponse, LocalDate> birthDateCol = new TableColumn<>("Birth Date");
        birthDateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getBirthDate()));
        birthDateCol.setMinWidth(120);

        TableColumn<AuthorResponse, LocalDate> deathDateCol = new TableColumn<>("Death Date");
        deathDateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getDeathDate()));
        deathDateCol.setMinWidth(120);

        TableColumn<AuthorResponse, Integer> bookCountCol = new TableColumn<>("Books");
        bookCountCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getBookCount()));
        bookCountCol.setMinWidth(80);

        TableColumn<AuthorResponse, String> biographyCol = new TableColumn<>("Biography");
        biographyCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getBiography()));
        biographyCol.setMinWidth(200);

        table.getColumns().addAll(nameCol, nationalityCol, birthDateCol, deathDateCol, bookCountCol, biographyCol);
    }

    public void loadData() {
        loadingOverlay.show();

        CompletableFuture<PageResponse<AuthorResponse>> future = apiService.getAuthors(
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
                showError("Failed to load authors: " + ex.getMessage());
            });
            return null;
        });
    }

    private void onSearch() {
        currentSearch = searchField.getText().trim();
        currentPage = 0;
        if (!currentSearch.isEmpty()) {
            searchAuthors();
        } else {
            loadData();
        }
    }

    private void searchAuthors() {
        loadingOverlay.show();
        apiService.searchAuthors(currentSearch, currentPage, pageSize)
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
                        showError("Failed to search authors: " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void goToPage(int page) {
        if (page >= 0) {
            currentPage = page;
            if (!currentSearch.isEmpty()) {
                searchAuthors();
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
                currentSearch.isEmpty() ? "No Authors Found" : "No Matching Authors",
                "Try searching with another keyword or add a new author to the archives."
            ));
        } else {
            table.setPlaceholder(new Label("No data available"));
        }
    }

    private void showAddDialog() {
        AuthorCreateDialog dialog = new AuthorCreateDialog(apiService);
        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                loadData();
                showSuccess("Author added successfully.");
            }
        });
    }

    private void showEditDialog() {
        AuthorResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            AuthorEditDialog dialog = new AuthorEditDialog(apiService, selected);
            dialog.showAndWait().ifPresent(result -> {
                if (result) {
                    loadData();
                    showSuccess("Author details updated successfully.");
                }
            });
        }
    }

    private void onDelete() {
        AuthorResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete \"" + selected.getName() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Author");
        com.library.management.desktop.theme.Theme.styleDialog(confirm);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loadingOverlay.show();
                apiService.deleteAuthor(selected.getId())
                        .thenRun(() -> Platform.runLater(() -> {
                            loadingOverlay.hide();
                            loadData();
                            showSuccess("Author removed successfully.");
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingOverlay.hide();
                                showError("Failed to delete author: " + ex.getMessage());
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