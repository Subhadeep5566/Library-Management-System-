package com.library.management.desktop.ui.login;

import com.library.management.desktop.service.AuthService;
import com.library.management.desktop.theme.Theme;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class LoginView {

    private final StackPane root;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final TextField plainPasswordField;
    private final Button togglePasswordBtn;
    private final Button loginButton;
    private final Label messageLabel;
    private final ProgressIndicator progressIndicator;
    private final Label titleLabel;
    private final Label subtitleLabel;
    private final Node logoNode;
    private final VBox cardBox;

    private final AuthService authService;
    private final Runnable onLoginSuccess;
    private boolean isPasswordVisible = false;

    public LoginView(AuthService authService, Runnable onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;

        this.usernameField = new TextField();
        this.passwordField = new PasswordField();
        this.plainPasswordField = new TextField();
        this.togglePasswordBtn = new Button("👁");
        this.loginButton = new Button("Sign In");
        this.messageLabel = new Label();
        this.progressIndicator = new ProgressIndicator();
        this.titleLabel = new Label("My Library");
        this.subtitleLabel = new Label("Your library, beautifully organized.");
        this.logoNode = Theme.createLogoMark(56);
        this.cardBox = new VBox(22);

        this.root = createLoginLayout();
        animateEntrance();
    }

    private StackPane createLoginLayout() {
        StackPane outerPane = new StackPane();
        outerPane.setBackground(new Background(new BackgroundFill(Theme.BG_GRADIENT, CornerRadii.EMPTY, Insets.EMPTY)));

        // Ambient radial glow behind the card
        Circle ambientGlow = new Circle(260);
        ambientGlow.setFill(Theme.LOGIN_RADIAL_GLOW);
        DropShadow glowSpread = new DropShadow();
        glowSpread.setColor(Color.web("#7C3AED", 0.35));
        glowSpread.setRadius(80);
        glowSpread.setSpread(0.2);
        ambientGlow.setEffect(glowSpread);

        // Login Card Surface
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setPadding(new Insets(42, 40, 42, 40));
        cardBox.setMaxWidth(420);
        cardBox.setMinWidth(360);
        Theme.styleCard(cardBox);

        // Header Section (Logo, Title, Subtitle)
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Theme.TEXT_PRIMARY);
        titleLabel.setAlignment(Pos.CENTER);

        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignment(Pos.CENTER);

        headerBox.getChildren().addAll(logoNode, titleLabel, subtitleLabel);

        // Form Fields
        VBox formBox = new VBox(16);
        formBox.setAlignment(Pos.CENTER_LEFT);

        // Username
        VBox usernameGroup = new VBox(6);
        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        userLabel.setTextFill(Theme.TEXT_SECONDARY);

        usernameField.setPromptText("Enter username");
        usernameField.setPrefHeight(44);
        usernameField.setFont(Font.font("System", 14));
        Theme.styleTextField(usernameField);
        usernameField.setOnAction(e -> handleLogin());
        usernameGroup.getChildren().addAll(userLabel, usernameField);

        // Password with visibility toggle
        VBox passwordGroup = new VBox(6);
        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        passLabel.setTextFill(Theme.TEXT_SECONDARY);

        passwordField.setPromptText("Enter password");
        passwordField.setPrefHeight(44);
        passwordField.setFont(Font.font("System", 14));
        Theme.stylePasswordField(passwordField);
        passwordField.setOnAction(e -> handleLogin());

        plainPasswordField.setPromptText("Enter password");
        plainPasswordField.setPrefHeight(44);
        plainPasswordField.setFont(Font.font("System", 14));
        Theme.styleTextField(plainPasswordField);
        plainPasswordField.setVisible(false);
        plainPasswordField.setManaged(false);
        plainPasswordField.setOnAction(e -> handleLogin());

        // Sync text between password and plain password
        passwordField.textProperty().bindBidirectional(plainPasswordField.textProperty());

        togglePasswordBtn.setFont(Font.font("System", 14));
        togglePasswordBtn.setTextFill(Theme.TEXT_MUTED);
        togglePasswordBtn.setCursor(javafx.scene.Cursor.HAND);
        togglePasswordBtn.setBackground(Background.EMPTY);
        togglePasswordBtn.setBorder(Border.EMPTY);
        togglePasswordBtn.setPadding(new Insets(0, 12, 0, 0));
        togglePasswordBtn.setOnAction(e -> togglePasswordVisibility());
        togglePasswordBtn.setOnMouseEntered(e -> togglePasswordBtn.setTextFill(Theme.TEXT_SECONDARY));
        togglePasswordBtn.setOnMouseExited(e -> togglePasswordBtn.setTextFill(Theme.TEXT_MUTED));

        StackPane passwordFieldContainer = new StackPane();
        passwordFieldContainer.getChildren().addAll(passwordField, plainPasswordField, togglePasswordBtn);
        StackPane.setAlignment(togglePasswordBtn, Pos.CENTER_RIGHT);

        passwordGroup.getChildren().addAll(passLabel, passwordFieldContainer);

        // Login Button
        loginButton.setText("Sign In");
        loginButton.setPrefHeight(46);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        Theme.stylePrimaryButton(loginButton);
        loginButton.setOnAction(e -> handleLogin());

        // Progress Indicator inside button container
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        progressIndicator.setPrefSize(22, 22);
        progressIndicator.setProgress(-1);

        StackPane buttonStack = new StackPane(loginButton, progressIndicator);
        StackPane.setAlignment(progressIndicator, Pos.CENTER);

        // Status Message Banner
        messageLabel.setFont(Font.font("System", 13));
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        messageLabel.setMaxWidth(340);

        formBox.getChildren().addAll(usernameGroup, passwordGroup, buttonStack, messageLabel);

        cardBox.getChildren().addAll(headerBox, formBox);

        outerPane.getChildren().addAll(ambientGlow, cardBox);
        StackPane.setAlignment(cardBox, Pos.CENTER);
        StackPane.setAlignment(ambientGlow, Pos.CENTER);

        return outerPane;
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            plainPasswordField.setVisible(true);
            plainPasswordField.setManaged(true);
            togglePasswordBtn.setText("🔒");
            plainPasswordField.requestFocus();
            plainPasswordField.positionCaret(plainPasswordField.getText().length());
        } else {
            plainPasswordField.setVisible(false);
            plainPasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordBtn.setText("👁");
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }

    private void animateEntrance() {
        // Logo subtle breath animation
        ScaleTransition logoScale = new ScaleTransition(Duration.millis(600), logoNode);
        logoScale.setFromX(0.85);
        logoScale.setFromY(0.85);
        logoScale.setToX(1.0);
        logoScale.setToY(1.0);

        FadeTransition cardFade = new FadeTransition(Duration.millis(500), cardBox);
        cardFade.setFromValue(0.0);
        cardFade.setToValue(1.0);

        ScaleTransition cardScale = new ScaleTransition(Duration.millis(500), cardBox);
        cardScale.setFromX(0.96);
        cardScale.setFromY(0.96);
        cardScale.setToX(1.0);
        cardScale.setToY(1.0);

        cardFade.play();
        cardScale.play();
        logoScale.play();
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password.", "error");
            return;
        }

        setLoading(true);
        clearMessage();

        authService.authenticate(username, password)
                .thenAccept(result -> Platform.runLater(() -> {
                    setLoading(false);
                    if (result.isSuccess()) {
                        showMessage("Welcome back!", "success");
                        onLoginSuccess.run();
                    } else {
                        showMessage(result.getMessage(), "error");
                        passwordField.clear();
                        plainPasswordField.clear();
                    }
                }));
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        usernameField.setDisable(loading);
        passwordField.setDisable(loading);
        plainPasswordField.setDisable(loading);
        togglePasswordBtn.setDisable(loading);

        if (loading) {
            progressIndicator.setVisible(true);
            progressIndicator.setManaged(true);
            loginButton.setText("");
        } else {
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);
            loginButton.setText("Sign In");
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);

        if ("error".equals(type)) {
            messageLabel.setTextFill(Color.web("#FCA5A5"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#3A131C"), new CornerRadii(8), Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#7F1D1D"), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))
            ));
        } else if ("success".equals(type)) {
            messageLabel.setTextFill(Color.web("#A7F3D0"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#064E3B", 0.6), new CornerRadii(8), Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#059669"), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))
            ));
        } else {
            messageLabel.setTextFill(Theme.TEXT_SECONDARY);
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#1E1538"), new CornerRadii(8), Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Theme.BORDER_SUBTLE, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))
            ));
        }

        messageLabel.setPadding(new Insets(10, 16, 10, 16));
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        FadeTransition ft = new FadeTransition(Duration.millis(200), messageLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void clearMessage() {
        if (!messageLabel.isVisible()) return;
        FadeTransition ft = new FadeTransition(Duration.millis(150), messageLabel);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            messageLabel.setVisible(false);
            messageLabel.setManaged(false);
        });
        ft.play();
    }

    public StackPane getRoot() {
        return root;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getLoginButton() {
        return loginButton;
    }
}