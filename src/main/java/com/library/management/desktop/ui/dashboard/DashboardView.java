package com.library.management.desktop.ui.dashboard;

import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.AuthService;
import com.library.management.desktop.theme.Theme;
import com.library.management.desktop.ui.common.LoadingOverlay;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.math.BigDecimal;

public class DashboardView {

    private final VBox root;
    private final AuthService authService;
    private final ApiService apiService;
    private final Runnable onLogout;
    private String username = "Admin";

    private final Label welcomeLabel;
    private final Label subtitleLabel;
    private final Label totalBooksLabel;
    private final Label totalAuthorsLabel;
    private final Label totalCategoriesLabel;
    private final Label totalUsersLabel;
    private final Label activeBorrowsLabel;
    private final Label pendingReturnsLabel;
    private final Label overdueBooksLabel;
    private final Label totalFinesLabel;

    private final LoadingOverlay loadingOverlay;
    private final java.util.function.Consumer<String> navigationHandler;

    public DashboardView(AuthService authService, ApiService apiService, Runnable onLogout) {
        this(authService, apiService, null, onLogout);
    }

    public DashboardView(AuthService authService, ApiService apiService, java.util.function.Consumer<String> navigationHandler, Runnable onLogout) {
        this.authService = authService;
        this.apiService = apiService;
        this.navigationHandler = navigationHandler;
        this.onLogout = onLogout;
        this.loadingOverlay = new LoadingOverlay();

        this.welcomeLabel = new Label("Welcome back");
        this.subtitleLabel = new Label("Overview of current library collection, loans, and patron activities.");
        this.totalBooksLabel = createStatValueLabel("0", Theme.TEXT_PRIMARY);
        this.totalAuthorsLabel = createStatValueLabel("0", Theme.TEXT_PRIMARY);
        this.totalCategoriesLabel = createStatValueLabel("0", Theme.TEXT_PRIMARY);
        this.totalUsersLabel = createStatValueLabel("0", Theme.TEXT_PRIMARY);
        this.activeBorrowsLabel = createStatValueLabel("0", Theme.SUCCESS);
        this.pendingReturnsLabel = createStatValueLabel("0", Theme.WARNING);
        this.overdueBooksLabel = createStatValueLabel("0", Theme.ERROR);
        this.totalFinesLabel = createStatValueLabel("$0.00", Theme.TEXT_PRIMARY);

        this.root = createDashboardLayout();
    }

    private VBox createDashboardLayout() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(24, 28, 24, 28));
        container.setBackground(new Background(new BackgroundFill(Theme.BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- Minimal Header Bar ---
        HBox headerBar = new HBox(16);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(4, 0, 4, 0));

        VBox welcomeBox = new VBox(2);
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 19));
        welcomeLabel.setTextFill(Theme.TEXT_PRIMARY);

        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitleLabel.setTextFill(Theme.TEXT_MUTED);

        welcomeBox.getChildren().addAll(welcomeLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshButton = new Button("↻  Refresh");
        refreshButton.setPrefHeight(32);
        Theme.styleSecondaryButton(refreshButton);
        refreshButton.setOnAction(e -> loadData());

        headerBar.getChildren().addAll(welcomeBox, spacer, refreshButton);

        // --- Section Label ---
        Label statsTitle = new Label("OVERVIEW");
        statsTitle.setFont(Font.font("System", FontWeight.BOLD, 11));
        statsTitle.setTextFill(Theme.TEXT_MUTED);
        statsTitle.setPadding(new Insets(6, 0, 0, 0));

        // --- Stats Grid (Responsive 4-column layout) ---
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        statsGrid.setVgap(14);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25.0);
            col.setHgrow(Priority.ALWAYS);
            statsGrid.getColumnConstraints().add(col);
        }

        // Row 1: Catalog & Members
        Node card1 = createStatTile("BOOKS", totalBooksLabel, "📚", "Catalog collection", "books");
        Node card2 = createStatTile("AUTHORS", totalAuthorsLabel, "✍️", "Registered authors", "authors");
        Node card3 = createStatTile("CATEGORIES", totalCategoriesLabel, "📂", "Genres & taxonomy", "categories");
        Node card4 = createStatTile("MEMBERS", totalUsersLabel, "👥", "Patrons & staff", "users");

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 2, 0);
        statsGrid.add(card4, 3, 0);

        // Row 2: Circulation & Accounts
        Node card5 = createStatTile("ACTIVE LOANS", activeBorrowsLabel, "📖", "Currently borrowed", "borrows");
        Node card6 = createStatTile("PENDING RETURNS", pendingReturnsLabel, "📦", "Due for check-in", "returns");
        Node card7 = createStatTile("OVERDUE", overdueBooksLabel, "⚠️", "Needs follow-up", "fines");
        Node card8 = createStatTile("TOTAL FINES", totalFinesLabel, "💰", "Accrued penalties", "fines");

        statsGrid.add(card5, 0, 1);
        statsGrid.add(card6, 1, 1);
        statsGrid.add(card7, 2, 1);
        statsGrid.add(card8, 3, 1);

        // --- Quick Actions Bar ---
        HBox quickActionsBox = new HBox(12);
        quickActionsBox.setAlignment(Pos.CENTER_LEFT);
        quickActionsBox.setPadding(new Insets(6, 0, 4, 0));

        Label actionsLabel = new Label("QUICK ACTIONS");
        actionsLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        actionsLabel.setTextFill(Theme.TEXT_MUTED);

        Button quickBorrowBtn = new Button("📖 Loan / Borrow Book");
        Theme.stylePrimaryButton(quickBorrowBtn);
        quickBorrowBtn.setOnAction(e -> {
            if (navigationHandler != null) navigationHandler.accept("borrows");
        });

        Button quickAddBookBtn = new Button("📚 Catalog New Book");
        Theme.styleGoldButton(quickAddBookBtn);
        quickAddBookBtn.setOnAction(e -> {
            if (navigationHandler != null) navigationHandler.accept("books");
        });

        Button quickAddUserBtn = new Button("👤 Register Member");
        Theme.styleSecondaryButton(quickAddUserBtn);
        quickAddUserBtn.setOnAction(e -> {
            if (navigationHandler != null) navigationHandler.accept("users");
        });

        Button quickFinesBtn = new Button("💰 Manage Fines");
        Theme.styleSecondaryButton(quickFinesBtn);
        quickFinesBtn.setOnAction(e -> {
            if (navigationHandler != null) navigationHandler.accept("fines");
        });

        quickActionsBox.getChildren().addAll(actionsLabel, quickBorrowBtn, quickAddBookBtn, quickAddUserBtn, quickFinesBtn);

        // --- System Status Footer ---
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setPadding(new Insets(10, 14, 10, 14));
        statusBox.setBackground(new Background(new BackgroundFill(Theme.BG_CARD, Theme.RADII_MEDIUM, Insets.EMPTY)));
        statusBox.setBorder(new Border(new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))));

        Label liveDot = new Label("●");
        liveDot.setFont(Font.font("System", 12));
        liveDot.setTextFill(Theme.SUCCESS);

        Label statusText = new Label("Backend connected  •  Database synchronized");
        statusText.setFont(Font.font("System", FontWeight.NORMAL, 12));
        statusText.setTextFill(Theme.TEXT_MUTED);

        statusBox.getChildren().addAll(liveDot, statusText);

        container.getChildren().addAll(headerBar, statsTitle, statsGrid, quickActionsBox, statusBox);

        // Responsive ScrollPane wrapper
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        Theme.styleScrollPane(scrollPane);

        StackPane wrapper = new StackPane(scrollPane, loadingOverlay);
        StackPane.setAlignment(loadingOverlay, Pos.CENTER);

        VBox finalRoot = new VBox(wrapper);
        VBox.setVgrow(wrapper, Priority.ALWAYS);
        finalRoot.setBackground(new Background(new BackgroundFill(Theme.BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));

        animateEntrance();
        return finalRoot;
    }

    private Label createStatValueLabel(String text, Color textColor) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 24));
        label.setTextFill(textColor);
        return label;
    }

    private Node createStatTile(String title, Label valueLabel, String icon, String subtitle, String targetView) {
        VBox tile = new VBox(6);
        tile.setPadding(new Insets(14, 16, 14, 16));
        tile.setBackground(new Background(new BackgroundFill(Theme.BG_CARD, Theme.RADII_MEDIUM, Insets.EMPTY)));
        tile.setBorder(new Border(new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))));
        tile.setCursor(javafx.scene.Cursor.HAND);

        // Category Header (clean typography, no giant cards)
        HBox headerRow = new HBox(8);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 13));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        titleLabel.setTextFill(Theme.TEXT_MUTED);

        headerRow.getChildren().addAll(iconLabel, titleLabel);

        Label descLabel = new Label(subtitle);
        descLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        descLabel.setTextFill(Theme.TEXT_MUTED);

        tile.getChildren().addAll(headerRow, valueLabel, descLabel);

        // Minimal hover feedback
        tile.setOnMouseEntered(e -> {
            tile.setBackground(new Background(new BackgroundFill(Theme.BG_CARD_HOVER, Theme.RADII_MEDIUM, Insets.EMPTY)));
            tile.setBorder(new Border(new BorderStroke(Theme.BORDER_MUTED, BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))));
        });

        tile.setOnMouseExited(e -> {
            tile.setBackground(new Background(new BackgroundFill(Theme.BG_CARD, Theme.RADII_MEDIUM, Insets.EMPTY)));
            tile.setBorder(new Border(new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))));
        });

        if (targetView != null && navigationHandler != null) {
            tile.setOnMouseClicked(e -> navigationHandler.accept(targetView));
        }

        return tile;
    }

    public void setUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username.trim();
        }
        Platform.runLater(() -> welcomeLabel.setText("Welcome back, " + this.username));
    }

    public void loadData() {
        loadingOverlay.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    // 1. Total Books
                    var booksFuture = apiService.getBooks(0, 1, "id", "asc");
                    int totalBooks = (int) booksFuture.get().getTotalElements();
                    Platform.runLater(() -> totalBooksLabel.setText(String.valueOf(totalBooks)));

                    // 2. Total Authors
                    var authorsFuture = apiService.getAuthors(0, 1, "id", "asc");
                    int totalAuthors = (int) authorsFuture.get().getTotalElements();
                    Platform.runLater(() -> totalAuthorsLabel.setText(String.valueOf(totalAuthors)));

                    // 3. Total Categories
                    var categoriesFuture = apiService.getCategories(0, 1, "id", "asc");
                    int totalCategories = (int) categoriesFuture.get().getTotalElements();
                    Platform.runLater(() -> totalCategoriesLabel.setText(String.valueOf(totalCategories)));

                    // 4. Total Users
                    var usersFuture = apiService.getUsers(0, 1, "id", "asc");
                    int totalUsers = (int) usersFuture.get().getTotalElements();
                    Platform.runLater(() -> totalUsersLabel.setText(String.valueOf(totalUsers)));

                    // 5. Borrows & Overdue
                    var borrowsFuture = apiService.getBorrows(0, 100, "id", "asc");
                    var borrows = borrowsFuture.get();
                    int activeBorrows = 0;
                    int pendingReturns = 0;
                    int overdueCount = 0;

                    for (var borrow : borrows.getContent()) {
                        String status = borrow.getStatus();
                        if ("BORROWED".equalsIgnoreCase(status) || "OVERDUE".equalsIgnoreCase(status)) {
                            activeBorrows++;
                        }
                        if ("BORROWED".equalsIgnoreCase(status)) {
                            pendingReturns++;
                        }
                        if ("OVERDUE".equalsIgnoreCase(status)) {
                            overdueCount++;
                        }
                    }

                    final int fActive = activeBorrows;
                    final int fPending = pendingReturns;
                    final int fOverdue = overdueCount;
                    Platform.runLater(() -> {
                        activeBorrowsLabel.setText(String.valueOf(fActive));
                        pendingReturnsLabel.setText(String.valueOf(fPending));
                        overdueBooksLabel.setText(String.valueOf(fOverdue));
                    });

                    // 6. Fines
                    var finesFuture = apiService.getFines(0, 100, "id", "asc");
                    var fines = finesFuture.get();
                    BigDecimal totalFines = BigDecimal.ZERO;
                    for (var fine : fines.getContent()) {
                        if (fine.getAmount() != null) {
                            totalFines = totalFines.add(fine.getAmount());
                        }
                    }
                    final BigDecimal fTotalFines = totalFines;
                    Platform.runLater(() -> totalFinesLabel.setText("$" + fTotalFines.setScale(2, java.math.RoundingMode.HALF_UP)));

                } catch (Exception e) {
                    Platform.runLater(() -> showError("Failed to refresh dashboard: " + e.getMessage()));
                } finally {
                    Platform.runLater(loadingOverlay::hide);
                }
                return null;
            }
        };

        new Thread(loadTask).start();
    }

    private void animateEntrance() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        Theme.styleDialog(alert);
        alert.setTitle("Error");
        alert.setHeaderText("Dashboard Error");
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}