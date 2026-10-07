package com.library.management.desktop.ui.login;

import com.library.management.desktop.service.AuthService;
import com.library.management.desktop.theme.Theme;
import com.library.management.desktop.ui.common.BookishBackground;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class LoginView {

    private final StackPane rootContainer;
    private final BookishBackground bookishBackground;
    private final ScrollPane rootScrollPane;
    private final StackPane centerWrapper;
    private final VBox formContainer;

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
        this.subtitleLabel = new Label("Where stories and knowledge reside.");
        this.logoNode = Theme.createLogoMark(42);
        this.formContainer = new VBox(16);
        this.centerWrapper = new StackPane();
        this.bookishBackground = new BookishBackground();

        this.rootScrollPane = createLoginLayout();

        this.rootContainer = new StackPane();
        this.rootContainer.getChildren().addAll(bookishBackground, rootScrollPane);

        animateEntrance();
    }

    private ScrollPane createLoginLayout() {
        // Form Container: Elegant Scholarly Card
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(360);
        formContainer.setMinWidth(320);
        formContainer.setPadding(new Insets(36, 28, 36, 28));
        formContainer.setBackground(new Background(new BackgroundFill(Color.web("#140E22", 0.92), new CornerRadii(12), Insets.EMPTY)));
        formContainer.setBorder(new Border(new BorderStroke(
            Color.web("#36254F"), BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1.2)
        )));
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.color(0, 0, 0, 0.55));
        cardShadow.setRadius(24);
        cardShadow.setOffsetY(10);
        formContainer.setEffect(cardShadow);

        // 1. Header: Small Book Icon + Title + Subtitle
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 8, 0));

        titleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: #FAF7F2; -fx-font-size: 26px; -fx-font-weight: bold;");

        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitleLabel.setStyle("-fx-text-fill: #CFC4B0; -fx-font-size: 12px;");

        Node separator = Theme.createOrnamentalSeparator();

        headerBox.getChildren().addAll(logoNode, titleLabel, subtitleLabel, separator);

        // 2. Username Field
        VBox usernameGroup = new VBox(6);
        usernameGroup.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        userLabel.setStyle("-fx-text-fill: #E2D9CC; -fx-font-size: 12px; -fx-font-weight: 600;");

        usernameField.setPromptText("Enter username (e.g. demo)");
        usernameField.setPrefHeight(40);
        usernameField.setFont(Font.font("System", 13));
        Theme.styleLoginTextField(usernameField);
        usernameField.setOnAction(e -> handleLogin());
        usernameGroup.getChildren().addAll(userLabel, usernameField);

        // 3. Password Field with Toggle
        VBox passwordGroup = new VBox(6);
        passwordGroup.setAlignment(Pos.CENTER_LEFT);
        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        passLabel.setStyle("-fx-text-fill: #E2D9CC; -fx-font-size: 12px; -fx-font-weight: 600;");

        passwordField.setPromptText("Enter password");
        passwordField.setPrefHeight(40);
        passwordField.setFont(Font.font("System", 13));
        Theme.styleLoginPasswordField(passwordField);
        passwordField.setOnAction(e -> handleLogin());

        plainPasswordField.setPromptText("Enter password");
        plainPasswordField.setPrefHeight(40);
        plainPasswordField.setFont(Font.font("System", 13));
        Theme.styleLoginTextField(plainPasswordField);
        plainPasswordField.setVisible(false);
        plainPasswordField.setManaged(false);
        plainPasswordField.setOnAction(e -> handleLogin());

        passwordField.textProperty().bindBidirectional(plainPasswordField.textProperty());

        togglePasswordBtn.setFont(Font.font("System", 13));
        togglePasswordBtn.setStyle("-fx-text-fill: #94A3B8; -fx-background-color: transparent; -fx-cursor: hand;");
        togglePasswordBtn.setPadding(new Insets(0, 12, 0, 0));
        togglePasswordBtn.setOnAction(e -> togglePasswordVisibility());
        togglePasswordBtn.setOnMouseEntered(e -> togglePasswordBtn.setStyle("-fx-text-fill: #F8FAFC; -fx-background-color: transparent; -fx-cursor: hand;"));
        togglePasswordBtn.setOnMouseExited(e -> togglePasswordBtn.setStyle("-fx-text-fill: #94A3B8; -fx-background-color: transparent; -fx-cursor: hand;"));

        StackPane passwordFieldContainer = new StackPane(passwordField, plainPasswordField, togglePasswordBtn);
        StackPane.setAlignment(togglePasswordBtn, Pos.CENTER_RIGHT);

        passwordGroup.getChildren().addAll(passLabel, passwordFieldContainer);

        // 4. Primary Sign In Button
        loginButton.setText("Sign In");
        loginButton.setPrefHeight(40);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        Theme.styleLoginButton(loginButton);
        loginButton.setOnAction(e -> handleLogin());

        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        progressIndicator.setPrefSize(18, 18);
        progressIndicator.setProgress(-1);

        StackPane buttonStack = new StackPane(loginButton, progressIndicator);
        StackPane.setAlignment(progressIndicator, Pos.CENTER);
        buttonStack.setPadding(new Insets(6, 0, 0, 0));

        // 5. Status Message Banner
        messageLabel.setFont(Font.font("System", 12));
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        messageLabel.setMaxWidth(340);

        formContainer.getChildren().addAll(headerBox, usernameGroup, passwordGroup, buttonStack, messageLabel);

        // Center Wrapper (transparent)
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setBackground(Background.EMPTY);
        centerWrapper.getChildren().add(formContainer);

        // Root ScrollPane with transparent styling so MotionBackground shows through
        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(
            "-fx-background: transparent; " +
            "-fx-background-color: transparent; " +
            "-fx-control-inner-background: transparent; " +
            "-fx-background-insets: 0; " +
            "-fx-padding: 0;"
        );

        return scrollPane;
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
        FadeTransition formFade = new FadeTransition(Duration.millis(240), formContainer);
        formFade.setFromValue(0.0);
        formFade.setToValue(1.0);
        formFade.play();
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
            messageLabel.setTextFill(Color.web("#FECDD3"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#3D0C15"), Theme.RADII_MEDIUM, Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#E11D48"), BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))
            ));
        } else if ("success".equals(type)) {
            messageLabel.setTextFill(Color.web("#A7F3D0"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#052E20"), Theme.RADII_MEDIUM, Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#059669"), BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))
            ));
        } else {
            messageLabel.setTextFill(Color.web("#DDD6FE"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#231145"), Theme.RADII_MEDIUM, Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#7C3AED"), BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))
            ));
        }

        messageLabel.setPadding(new Insets(8, 14, 8, 14));
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        FadeTransition ft = new FadeTransition(Duration.millis(160), messageLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void clearMessage() {
        if (!messageLabel.isVisible()) return;
        FadeTransition ft = new FadeTransition(Duration.millis(120), messageLabel);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            messageLabel.setVisible(false);
            messageLabel.setManaged(false);
        });
        ft.play();
    }

    public StackPane getRoot() {
        return rootContainer;
    }

    public void startAnimation() {
        if (bookishBackground != null) {
            bookishBackground.renderBackground();
        }
    }

    public void stopAnimation() {
        // No heavy background animation running
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