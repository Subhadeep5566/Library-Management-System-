package com.library.management.desktop.ui.main;

import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.AuthService;
import com.library.management.desktop.theme.Theme;
import com.library.management.desktop.ui.authors.AuthorsView;
import com.library.management.desktop.ui.books.BooksView;
import com.library.management.desktop.ui.borrows.BorrowsView;
import com.library.management.desktop.ui.categories.CategoriesView;
import com.library.management.desktop.ui.dashboard.DashboardView;
import com.library.management.desktop.ui.fines.FinesView;
import com.library.management.desktop.ui.login.LoginView;
import com.library.management.desktop.ui.returns.ReturnsView;
import com.library.management.desktop.ui.users.UsersView;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class MainView {

    private final BorderPane root;
    private VBox sidebar;
    private BorderPane contentArea;
    private Label headerTitle;
    private Label headerSubtitle;
    private Label userLabel;
    private Button logoutButton;

    private final ApiClient apiClient;
    private final ApiService apiService;
    private final AuthService authService;
    private final Runnable onLogoutCallback;

    private final LoginView loginView;
    private final DashboardView dashboardView;
    private final BooksView booksView;
    private final AuthorsView authorsView;
    private final CategoriesView categoriesView;
    private final UsersView usersView;
    private final BorrowsView borrowsView;
    private final ReturnsView returnsView;
    private final FinesView finesView;

    private String currentUsername = "Admin";
    private String currentView = "dashboard";
    private final Map<String, Button> navButtons = new HashMap<>();

    public MainView(ApiClient apiClient, ApiService apiService, AuthService authService) {
        this(apiClient, apiService, authService, null);
    }

    public MainView(ApiClient apiClient, ApiService apiService, AuthService authService, Runnable onLogoutCallback) {
        this.apiClient = apiClient;
        this.apiService = apiService;
        this.authService = authService;
        this.onLogoutCallback = onLogoutCallback;

        this.loginView = new LoginView(authService, this::onLoginSuccess);
        this.dashboardView = new DashboardView(authService, apiService, this::handleLogout);
        this.booksView = new BooksView(apiService, apiClient);
        this.authorsView = new AuthorsView(apiService, apiClient);
        this.categoriesView = new CategoriesView(apiService, apiClient);
        this.usersView = new UsersView(apiService, apiClient);
        this.borrowsView = new BorrowsView(apiService, apiClient);
        this.returnsView = new ReturnsView(apiService, apiClient);
        this.finesView = new FinesView(apiService, apiClient);

        this.root = createMainLayout();
    }

    private BorderPane createMainLayout() {
        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(new Background(new BackgroundFill(Theme.BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Header
        HBox header = createHeader();
        borderPane.setTop(header);

        // Sidebar
        sidebar = createSidebar();
        borderPane.setLeft(sidebar);

        // Content area
        contentArea = new BorderPane();
        contentArea.setBackground(new Background(new BackgroundFill(Theme.BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));
        borderPane.setCenter(contentArea);

        // Show dashboard by default
        showDashboard();

        return borderPane;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(68);
        header.setPadding(new Insets(0, 28, 0, 28));
        header.setBackground(new Background(new BackgroundFill(Theme.BG_DEEPEST, CornerRadii.EMPTY, Insets.EMPTY)));
        header.setBorder(new Border(new BorderStroke(
            null, null, Theme.BORDER_SUBTLE, null,
            BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
            CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY
        )));

        // Left: Page Title & Subtitle
        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        headerTitle = new Label("Dashboard");
        headerTitle.setFont(Font.font("System", FontWeight.BOLD, 19));
        headerTitle.setTextFill(Theme.TEXT_PRIMARY);

        headerSubtitle = new Label("Real-time metrics & library operations overview");
        headerSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 12));
        headerSubtitle.setTextFill(Theme.TEXT_MUTED);

        titleBox.getChildren().addAll(headerTitle, headerSubtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right: User Capsule
        HBox userCapsule = new HBox(8);
        userCapsule.setAlignment(Pos.CENTER);
        userCapsule.setPadding(new Insets(6, 14, 6, 12));
        userCapsule.setBackground(new Background(new BackgroundFill(Theme.BG_CARD, new CornerRadii(18), Insets.EMPTY)));
        userCapsule.setBorder(new Border(new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, new CornerRadii(18), new BorderWidths(1))));

        Label userIcon = new Label("👤");
        userIcon.setFont(Font.font("System", 13));

        userLabel = new Label(currentUsername);
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        userLabel.setTextFill(Theme.TEXT_SECONDARY);

        userCapsule.getChildren().addAll(userIcon, userLabel);

        // Logout Button
        logoutButton = new Button("Sign Out");
        logoutButton.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        Theme.styleSecondaryButton(logoutButton);
        logoutButton.setOnAction(e -> handleLogout());

        header.getChildren().addAll(titleBox, spacer, userCapsule, logoutButton);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebarBox = new VBox(6);
        sidebarBox.setPrefWidth(230);
        sidebarBox.setMinWidth(230);
        sidebarBox.setMaxWidth(230);
        sidebarBox.setPadding(new Insets(20, 12, 20, 12));
        sidebarBox.setBackground(new Background(new BackgroundFill(Theme.SIDEBAR_GRADIENT, CornerRadii.EMPTY, Insets.EMPTY)));
        sidebarBox.setBorder(new Border(new BorderStroke(
            null, Theme.BORDER_SUBTLE, null, null,
            BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
            CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY
        )));

        // Top Brand Header: Logo + "My Library"
        HBox brandBox = new HBox(12);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(4, 12, 24, 12));

        Node logo = Theme.createLogoMark(28);

        Label brandTitle = new Label("My Library");
        brandTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        brandTitle.setTextFill(Theme.TEXT_PRIMARY);

        brandBox.getChildren().addAll(logo, brandTitle);
        sidebarBox.getChildren().add(brandBox);

        // Navigation Category Label
        Label navTitle = new Label("MAIN NAVIGATION");
        navTitle.setFont(Font.font("System", FontWeight.BOLD, 11));
        navTitle.setTextFill(Theme.TEXT_MUTED);
        navTitle.setPadding(new Insets(4, 12, 8, 12));
        sidebarBox.getChildren().add(navTitle);

        String[][] navItems = {
            {"Dashboard", "dashboard", "📊"},
            {"Books", "books", "📚"},
            {"Authors", "authors", "✍️"},
            {"Categories", "categories", "📂"},
            {"Users", "users", "👥"},
            {"Borrows", "borrows", "📖"},
            {"Returns", "returns", "📦"},
            {"Fines", "fines", "💰"}
        };

        for (String[] item : navItems) {
            Button btn = createNavButton(item[0], item[1], item[2]);
            navButtons.put(item[1], btn);
            sidebarBox.getChildren().add(btn);
        }

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
        sidebarBox.getChildren().add(bottomSpacer);

        // Sidebar Footer Note
        Label versionLabel = new Label("My Library  •  Desktop v1.0");
        versionLabel.setFont(Font.font("System", 10));
        versionLabel.setTextFill(Color.web("#5E5275"));
        versionLabel.setPadding(new Insets(8, 12, 4, 12));
        sidebarBox.getChildren().add(versionLabel);

        return sidebarBox;
    }

    private Button createNavButton(String text, String viewName, String icon) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(42);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 14, 0, 14));
        btn.setCursor(javafx.scene.Cursor.HAND);

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font("System", 15));

        Label textLbl = new Label(text);
        textLbl.setFont(Font.font("System", FontWeight.MEDIUM, 13));

        content.getChildren().addAll(iconLbl, textLbl);
        btn.setGraphic(content);

        // Default inactive styling
        styleNavButtonInactive(btn, textLbl);

        btn.setOnAction(e -> navigateTo(viewName));
        return btn;
    }

    private void styleNavButtonActive(Button btn, Label textLbl) {
        btn.setBackground(new Background(new BackgroundFill(Color.web("#281447"), Theme.RADII_MEDIUM, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(
            Color.web("#8B5CF6"), Theme.BORDER_SUBTLE, Theme.BORDER_SUBTLE, Theme.BORDER_SUBTLE,
            BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
            Theme.RADII_MEDIUM, new BorderWidths(1.5, 1, 1, 1), Insets.EMPTY
        )));
        textLbl.setTextFill(Theme.TEXT_PRIMARY);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#7C3AED", 0.45));
        glow.setRadius(12);
        glow.setSpread(0.1);
        btn.setEffect(glow);
    }

    private void styleNavButtonInactive(Button btn, Label textLbl) {
        btn.setBackground(Background.EMPTY);
        btn.setBorder(Border.EMPTY);
        btn.setEffect(null);
        textLbl.setTextFill(Theme.TEXT_MUTED);

        btn.setOnMouseEntered(e -> {
            if (!viewNameMatchesActive(btn)) {
                btn.setBackground(new Background(new BackgroundFill(Color.web("#171026"), Theme.RADII_MEDIUM, Insets.EMPTY)));
                textLbl.setTextFill(Theme.TEXT_SECONDARY);
            }
        });

        btn.setOnMouseExited(e -> {
            if (!viewNameMatchesActive(btn)) {
                btn.setBackground(Background.EMPTY);
                textLbl.setTextFill(Theme.TEXT_MUTED);
            }
        });
    }

    private boolean viewNameMatchesActive(Button btn) {
        Button activeBtn = navButtons.get(currentView);
        return btn == activeBtn;
    }

    private void updateNavSelection(String activeView) {
        currentView = activeView;
        for (Map.Entry<String, Button> entry : navButtons.entrySet()) {
            Button btn = entry.getValue();
            HBox content = (HBox) btn.getGraphic();
            Label textLbl = (Label) content.getChildren().get(1);

            if (entry.getKey().equalsIgnoreCase(activeView)) {
                styleNavButtonActive(btn, textLbl);
            } else {
                styleNavButtonInactive(btn, textLbl);
            }
        }
    }

    public void navigateTo(String viewName) {
        updateNavSelection(viewName);

        switch (viewName.toLowerCase()) {
            case "dashboard":
                showDashboard();
                break;
            case "books":
                showBooks();
                break;
            case "authors":
                showAuthors();
                break;
            case "categories":
                showCategories();
                break;
            case "users":
                showUsers();
                break;
            case "borrows":
                showBorrows();
                break;
            case "returns":
                showReturns();
                break;
            case "fines":
                showFines();
                break;
            default:
                showDashboard();
                break;
        }
    }

    private void switchContent(Node newView, String title, String subtitle) {
        headerTitle.setText(title);
        headerSubtitle.setText(subtitle);

        FadeTransition ft = new FadeTransition(Duration.millis(220), newView);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        contentArea.setCenter(newView);
        ft.play();
    }

    private void showDashboard() {
        dashboardView.setUsername(currentUsername);
        dashboardView.loadData();
        switchContent(dashboardView.getRoot(), "Dashboard", "Real-time metrics & library operations overview");
    }

    private void showBooks() {
        booksView.loadData();
        switchContent(booksView.getRoot(), "Books", "Catalog collection, availability & inventory control");
    }

    private void showAuthors() {
        authorsView.loadData();
        switchContent(authorsView.getRoot(), "Authors", "Registered authors & bibliographic attribution");
    }

    private void showCategories() {
        categoriesView.loadData();
        switchContent(categoriesView.getRoot(), "Categories", "Genre taxonomy, subjects & classifications");
    }

    private void showUsers() {
        usersView.loadData();
        switchContent(usersView.getRoot(), "Users", "Library members, patron accounts & staff roles");
    }

    private void showBorrows() {
        borrowsView.loadData();
        switchContent(borrowsView.getRoot(), "Borrows", "Circulation desk & active book loan management");
    }

    private void showReturns() {
        returnsView.loadData();
        switchContent(returnsView.getRoot(), "Returns", "Book check-in logs & item condition assessment");
    }

    private void showFines() {
        finesView.loadData();
        switchContent(finesView.getRoot(), "Fines", "Overdue fee records, payment collection & waivers");
    }

    public void setCurrentUser(String username) {
        if (username != null && !username.trim().isEmpty()) {
            this.currentUsername = username.trim();
        }
        if (userLabel != null) {
            userLabel.setText(this.currentUsername);
        }
        if (dashboardView != null) {
            dashboardView.setUsername(this.currentUsername);
        }
    }

    public void showLoginScreen() {
        if (onLogoutCallback != null) {
            onLogoutCallback.run();
        } else {
            contentArea.setCenter(loginView.getRoot());
            sidebar.setVisible(false);
            sidebar.setManaged(false);
            headerTitle.setText("Sign In");
            headerSubtitle.setText("Enter your credentials to access My Library");
        }
    }

    private void onLoginSuccess() {
        currentUsername = loginView.getUsernameField().getText().trim();
        setCurrentUser(currentUsername);
        sidebar.setVisible(true);
        sidebar.setManaged(true);
        showDashboard();
    }

    private void handleLogout() {
        authService.clearCredentials();
        apiClient.clearCredentials();
        if (onLogoutCallback != null) {
            onLogoutCallback.run();
        } else {
            showLoginScreen();
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}