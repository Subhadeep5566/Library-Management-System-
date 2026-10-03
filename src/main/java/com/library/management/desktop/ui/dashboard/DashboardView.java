package com.library.management.desktop.ui.dashboard;

import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.AuthService;
import com.library.management.desktop.theme.Theme;
import com.library.management.desktop.ui.common.LoadingOverlay;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
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

    public DashboardView(AuthService authService, ApiService apiService, Runnable onLogout) {
        this.authService = authService;
        this.apiService = apiService;
        this.onLogout = onLogout;
        this.loadingOverlay = new LoadingOverlay();

        this.welcomeLabel = new Label("Welcome back");
        this.subtitleLabel = new Label("Here is the latest snapshot of your library operations.");
        this.totalBooksLabel = createStatValueLabel("0");
        this.totalAuthorsLabel = createStatValueLabel("0");
        this.totalCategoriesLabel = createStatValueLabel("0");
        this.totalUsersLabel = createStatValueLabel("0");
        this.activeBorrowsLabel = createStatValueLabel("0");
        this.pendingReturnsLabel = createStatValueLabel("0");
        this.overdueBooksLabel = createStatValueLabel("0");
        this.totalFinesLabel = createStatValueLabel("$0.00");

        this.root = createDashboardLayout();
    }

    private VBox createDashboardLayout() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(28, 32, 28, 32));
        container.setBackground(new Background(new BackgroundFill(Theme.BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- Welcome Header Banner ---
        HBox headerBanner = new HBox(16);
        headerBanner.setAlignment(Pos.CENTER_LEFT);
        headerBanner.setPadding(new Insets(16, 24, 16, 24));
        headerBanner.setBackground(new Background(new BackgroundFill(Theme.CARD_GRADIENT, Theme.RADII_LARGE, Insets.EMPTY)));
        headerBanner.setBorder(new Border(new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, Theme.RADII_LARGE, new BorderWidths(1))));
        headerBanner.setEffect(new DropShadow(16, 0, 4, Color.color(0, 0, 0, 0.25)));

        VBox welcomeTextBox = new VBox(4);
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        welcomeLabel.setTextFill(Theme.TEXT_PRIMARY);

        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        subtitleLabel.setTextFill(Theme.TEXT_SECONDARY);

        welcomeTextBox.getChildren().addAll(welcomeLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshButton = new Button("↻  Refresh Data");
        Theme.styleSecondaryButton(refreshButton);
        refreshButton.setOnAction(e -> loadData());

        headerBanner.getChildren().addAll(welcomeTextBox, spacer, refreshButton);

        // --- Section Title: Overview ---
        Label statsSectionTitle = new Label("METRICS OVERVIEW");
        statsSectionTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        statsSectionTitle.setTextFill(Theme.TEXT_MUTED);
        statsSectionTitle.setPadding(new Insets(8, 0, 0, 4));

        // --- Stats Grid ---
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(18);
        statsGrid.setVgap(18);

        // Make all 4 columns expand equally
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25.0);
            col.setHgrow(Priority.ALWAYS);
            statsGrid.getColumnConstraints().add(col);
        }

        // Row 1: Core Catalog & Community
        Node card1 = createStatCard("Total Books", totalBooksLabel, "📚", "Catalog collection", Theme.STAT_CARD_GRADIENT_1, Theme.PURPLE_LIGHT);
        Node card2 = createStatCard("Total Authors", totalAuthorsLabel, "✍️", "Registered authors", Theme.STAT_CARD_GRADIENT_2, Theme.ACCENT_VIOLET);
        Node card3 = createStatCard("Total Categories", totalCategoriesLabel, "📂", "Genres & classifications", Theme.STAT_CARD_GRADIENT_3, Theme.LAVENDER);
        Node card4 = createStatCard("Total Users", totalUsersLabel, "👥", "Patrons & staff", Theme.STAT_CARD_GRADIENT_4, Theme.PURPLE_GLOW);

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 2, 0);
        statsGrid.add(card4, 3, 0);

        // Row 2: Circulation & Financials
        Node card5 = createStatCard("Active Borrows", activeBorrowsLabel, "📖", "Currently with readers", Theme.STAT_CARD_GRADIENT_1, Theme.SUCCESS);
        Node card6 = createStatCard("Pending Returns", pendingReturnsLabel, "📦", "Expected returns", Theme.STAT_CARD_GRADIENT_2, Theme.WARNING);
        Node card7 = createStatCard("Overdue Books", overdueBooksLabel, "⚠️", "Needs immediate attention", Theme.STAT_CARD_GRADIENT_3, Theme.ERROR);
        Node card8 = createStatCard("Total Fines", totalFinesLabel, "💰", "Accrued penalty balance", Theme.STAT_CARD_GRADIENT_4, Theme.INFO);

        statsGrid.add(card5, 0, 1);
        statsGrid.add(card6, 1, 1);
        statsGrid.add(card7, 2, 1);
        statsGrid.add(card8, 3, 1);

        // --- Quick Highlights / Status Card ---
        HBox statusCard = new HBox(20);
        statusCard.setAlignment(Pos.CENTER_LEFT);
        statusCard.setPadding(new Insets(16, 24, 16, 24));
        statusCard.setBackground(new Background(new BackgroundFill(Theme.BG_CARD, Theme.RADII_MEDIUM, Insets.EMPTY)));
        statusCard.setBorder(new Border(new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))));

        Label liveDot = new Label("●");
        liveDot.setFont(Font.font("System", 16));
        liveDot.setTextFill(Theme.SUCCESS);

        Label statusText = new Label("Backend Services Active  •  Database Synchronized");
        statusText.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        statusText.setTextFill(Theme.TEXT_MUTED);

        statusCard.getChildren().addAll(liveDot, statusText);

        container.getChildren().addAll(headerBanner, statsSectionTitle, statsGrid, statusCard);

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        StackPane wrapper = new StackPane(scrollPane, loadingOverlay);
        StackPane.setAlignment(loadingOverlay, Pos.CENTER);

        VBox finalRoot = new VBox(wrapper);
        VBox.setVgrow(wrapper, Priority.ALWAYS);
        finalRoot.setBackground(new Background(new BackgroundFill(Theme.BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));

        animateEntrance();
        return finalRoot;
    }

    private Label createStatValueLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 26));
        label.setTextFill(Theme.TEXT_PRIMARY);
        return label;
    }

    private Node createStatCard(String title, Label valueLabel, String icon, String subtitle, LinearGradient gradient, Color accentColor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setBackground(new Background(new BackgroundFill(gradient, Theme.RADII_MEDIUM, Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))));

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.color(0, 0, 0, 0.25));
        cardShadow.setRadius(14);
        cardShadow.setOffsetY(3);
        card.setEffect(cardShadow);

        // Header inside card: Icon and Title
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        titleLabel.setTextFill(Theme.TEXT_SECONDARY);

        topRow.getChildren().addAll(iconLabel, titleLabel);

        // Subtitle note
        Label descLabel = new Label(subtitle);
        descLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        descLabel.setTextFill(Theme.TEXT_MUTED);

        card.getChildren().addAll(topRow, valueLabel, descLabel);

        // Subtle interactive hover effect
        card.setOnMouseEntered(e -> {
            card.setBorder(new Border(new BorderStroke(accentColor, BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))));
            cardShadow.setColor(Color.color(0.48, 0.22, 0.93, 0.35));
            cardShadow.setRadius(18);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), card);
            st.setToX(1.02);
            st.setToY(1.02);
            st.play();
        });

        card.setOnMouseExited(e -> {
            card.setBorder(new Border(new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))));
            cardShadow.setColor(Color.color(0, 0, 0, 0.25));
            cardShadow.setRadius(14);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        return card;
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
        FadeTransition ft = new FadeTransition(Duration.millis(350), root);
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