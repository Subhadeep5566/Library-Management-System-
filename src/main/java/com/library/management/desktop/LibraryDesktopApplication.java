package com.library.management.desktop;

import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.AuthService;
import com.library.management.desktop.ui.login.LoginView;
import com.library.management.desktop.ui.main.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LibraryDesktopApplication extends Application {

    private final AuthService authService = new AuthService();
    private final ApiClient apiClient = new ApiClient();
    private Stage primaryStage;
    private Scene loginScene;
    private Scene mainScene;
    private LoginView loginView;
    private MainView mainView;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("My Library");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(680);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(800);
        primaryStage.setResizable(true);

        initializeViews();
        showLoginScreen();

        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void initializeViews() {
        loginView = new LoginView(authService, this::onLoginSuccess);
        com.library.management.desktop.theme.Theme.applyRootTheme(loginView.getRoot());
        loginScene = new Scene(loginView.getRoot(), 1280, 800);

        ApiService apiService = new ApiService(apiClient);
        mainView = new MainView(apiClient, apiService, authService, this::onLogout);
        com.library.management.desktop.theme.Theme.applyRootTheme(mainView.getRoot());
        mainScene = new Scene(mainView.getRoot(), 1280, 800);
    }

    private void showLoginScreen() {
        loginView.getUsernameField().clear();
        loginView.getPasswordField().clear();
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("My Library");
    }

    private void showMainApp(String username) {
        mainView.setCurrentUser(username);
        mainView.navigateTo("dashboard");
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("My Library");
    }

    private void onLoginSuccess() {
        String username = authService.getCurrentUsername();
        String password = authService.getCurrentPassword();
        if (username != null && password != null) {
            apiClient.setCredentials(username, password);
        }
        showMainApp(username != null && !username.isEmpty() ? username : "Admin");
    }

    private void onLogout() {
        authService.clearCredentials();
        apiClient.clearCredentials();
        showLoginScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}