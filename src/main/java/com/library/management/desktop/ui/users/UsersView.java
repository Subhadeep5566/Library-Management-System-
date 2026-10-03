package com.library.management.desktop.ui.users;

import com.library.management.desktop.dto.UserResponse;
import com.library.management.desktop.dto.PageResponse;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.ui.common.LoadingOverlay;
import com.library.management.desktop.ui.common.UserCreateDialog;
import com.library.management.desktop.ui.common.UserEditDialog;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class UsersView {

    private final VBox root;
    private final ApiService apiService;
    private final ApiClient apiClient;
    private final LoadingOverlay loadingOverlay;

    private TableView<UserResponse> table;
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

    public UsersView(ApiService apiService, ApiClient apiClient) {
        this.apiService = apiService;
        this.apiClient = apiClient;
        this.loadingOverlay = new LoadingOverlay();
        this.root = createLayout();
        loadData();
    }

    private VBox createLayout() {
        VBox container = new VBox(15);
        container.getStyleClass().add("entity-view-container");
        container.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 15, 0));

        Label titleLabel = new Label("Users");
        titleLabel.getStyleClass().add("entity-view-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(250);
        searchField.setOnAction(e -> onSearch());

        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> loadData());

        addButton = new Button("Add User");
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
        TableColumn<UserResponse, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUsername()));
        usernameCol.setMinWidth(150);

        TableColumn<UserResponse, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
        emailCol.setMinWidth(200);

        TableColumn<UserResponse, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFullName()));
        nameCol.setMinWidth(180);

        TableColumn<UserResponse, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getRole()));
        roleCol.setMinWidth(120);
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: " + getRoleColor(item) + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<UserResponse, String> statusCol = new TableColumn<>("Status");
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

        TableColumn<UserResponse, LocalDateTime> membershipCol = new TableColumn<>("Member Since");
        membershipCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getMembershipDate()));
        membershipCol.setMinWidth(150);

        table.getColumns().addAll(usernameCol, emailCol, nameCol, roleCol, statusCol, membershipCol);
    }

    private String getRoleColor(String role) {
        return switch (role) {
            case "ADMIN" -> "#A78BFA";
            case "LIBRARIAN" -> "#8B5CF6";
            case "MEMBER" -> "#10B981";
            default -> "#8B7FA3";
        };
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "ACTIVE" -> "#10B981";
            case "INACTIVE" -> "#8B7FA3";
            case "SUSPENDED" -> "#F59E0B";
            case "EXPIRED" -> "#EF4444";
            default -> "#8B7FA3";
        };
    }

    public void loadData() {
        loadingOverlay.show();

        CompletableFuture<PageResponse<UserResponse>> future = apiService.getUsers(
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
                showError("Failed to load users: " + ex.getMessage());
            });
            return null;
        });
    }

    private void onSearch() {
        currentSearch = searchField.getText().trim();
        currentPage = 0;
        if (!currentSearch.isEmpty()) {
            searchUsers();
        } else {
            loadData();
        }
    }

    private void searchUsers() {
        loadingOverlay.show();
        apiService.searchUsers(currentSearch, currentPage, pageSize)
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
                        showError("Failed to search users: " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void goToPage(int page) {
        if (page >= 0) {
            currentPage = page;
            if (!currentSearch.isEmpty()) {
                searchUsers();
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
            Label emptyLabel = new Label(currentSearch.isEmpty() ? "No users found" : "No users match your search");
            emptyLabel.getStyleClass().add("empty-state-label");
            table.setPlaceholder(emptyLabel);
        } else {
            table.setPlaceholder(new Label("No data available"));
        }
    }

    private void showAddDialog() {
        UserCreateDialog dialog = new UserCreateDialog(apiService);
        dialog.showAndWait().ifPresent(result -> {
            if (result) loadData();
        });
    }

    private void showEditDialog() {
        UserResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            UserEditDialog dialog = new UserEditDialog(apiService, selected);
            dialog.showAndWait().ifPresent(result -> {
                if (result) loadData();
            });
        }
    }

    private void onDelete() {
        UserResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete user \"" + selected.getUsername() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loadingOverlay.show();
                apiService.deleteUser(selected.getId())
                        .thenRun(() -> Platform.runLater(() -> {
                            loadingOverlay.hide();
                            loadData();
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                loadingOverlay.hide();
                                showError("Failed to delete user: " + ex.getMessage());
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
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}