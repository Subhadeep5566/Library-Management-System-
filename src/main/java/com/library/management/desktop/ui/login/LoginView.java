package com.library.management.desktop.ui.login;

import com.library.management.desktop.service.AuthService;
import com.library.management.desktop.theme.Theme;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.InputStream;

public class LoginView {

    private final StackPane rootContainer;
    private final CinematicBackgroundPane cinematicBgPane;
    private final Region atmosphericOverlay;
    private final Node contentLayout;
    private final VBox loginPanel;

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
        this.loginButton = new Button("SIGN IN");
        this.messageLabel = new Label();
        this.progressIndicator = new ProgressIndicator();
        this.titleLabel = new Label("MY LIBRARY");
        this.subtitleLabel = new Label("A place for every story.");
        this.logoNode = Theme.createLoginLogoMark(42);
        this.loginPanel = new VBox(22);

        this.cinematicBgPane = new CinematicBackgroundPane();
        this.atmosphericOverlay = createAtmosphericOverlay();
        this.contentLayout = createContentLayout();

        this.rootContainer = new StackPane();
        this.rootContainer.getChildren().addAll(cinematicBgPane, atmosphericOverlay, contentLayout);

        animateEntrance();
    }

    private Region createAtmosphericOverlay() {
        Region overlay = new Region();
        overlay.setBackground(new Background(new BackgroundFill(
            new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.00, Color.color(0.06, 0.03, 0.01, 0.00)),   // Far left edge: completely transparent
                new Stop(0.44, Color.color(0.06, 0.03, 0.01, 0.00)),   // Reading room stacks, ladder & lamps fully visible
                new Stop(0.58, Color.color(0.07, 0.04, 0.02, 0.24)),   // Warm amber-mahogany atmosphere begins
                new Stop(0.72, Color.color(0.08, 0.04, 0.02, 0.68)),   // Deep walnut shade deepens smoothly
                new Stop(0.86, Color.color(0.07, 0.03, 0.01, 0.88)),   // High contrast area for integrated form controls
                new Stop(1.00, Color.color(0.05, 0.02, 0.01, 0.94))    // Far right margin
            ),
            CornerRadii.EMPTY, Insets.EMPTY
        )));
        return overlay;
    }

    private Node createContentLayout() {
        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(Background.EMPTY);

        // Center / Left area is open so the magnificent library stacks, ladder, and tables dominate
        Region spacer = new Region();
        borderPane.setCenter(spacer);

        // Right side container for the login panel - NO CARD, naturally integrated
        VBox rightColumn = new VBox();
        rightColumn.setAlignment(Pos.CENTER);
        rightColumn.setPrefWidth(470);
        rightColumn.setMaxWidth(500);
        rightColumn.setMinWidth(350);
        rightColumn.setPadding(new Insets(24, 76, 24, 20));

        // loginPanel sits directly in the scene with generous breathing room and NO card outline
        loginPanel.setAlignment(Pos.CENTER_LEFT);
        loginPanel.setMaxWidth(360);
        loginPanel.setMinWidth(320);
        loginPanel.setPadding(new Insets(16, 0, 16, 0));
        loginPanel.setBackground(Background.EMPTY);
        loginPanel.setBorder(Border.EMPTY);
        loginPanel.setEffect(null);

        // 1. Header: Elegant Book Icon + "MY LIBRARY" Title + Subtitle + Ornamental Divider
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 8, 0));

        titleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
        titleLabel.setStyle(
            "-fx-text-fill: #FAF7F2; " +
            "-fx-font-family: 'Georgia', 'Garamond', 'Baskerville', serif; " +
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-letter-spacing: 2px;"
        );

        DropShadow titleEmboss = new DropShadow();
        titleEmboss.setColor(Color.web("#C5A059", 0.35));
        titleEmboss.setRadius(8);
        titleEmboss.setOffsetY(1);
        titleLabel.setEffect(titleEmboss);

        subtitleLabel.setFont(Font.font("Georgia", FontPosture.ITALIC, 13.5));
        subtitleLabel.setStyle(
            "-fx-text-fill: #D4C7B5; " +
            "-fx-font-family: 'Georgia', serif; " +
            "-fx-font-size: 13.5px; " +
            "-fx-font-style: italic;"
        );

        Node separator = Theme.createOrnamentalSeparator();

        headerBox.getChildren().addAll(logoNode, titleLabel, subtitleLabel, separator);

        // 2. Username Field Group
        VBox usernameGroup = new VBox(7);
        usernameGroup.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label("USERNAME");
        userLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        userLabel.setStyle(
            "-fx-text-fill: #C5A059; " +
            "-fx-font-family: 'Georgia', serif; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-letter-spacing: 1.5px;"
        );

        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(44);
        usernameField.setFont(Font.font("Segoe UI", 13.5));
        Theme.styleLoginTextField(usernameField);
        usernameField.setOnAction(e -> handleLogin());
        usernameGroup.getChildren().addAll(userLabel, usernameField);

        // 3. Password Field Group with Visibility Toggle
        VBox passwordGroup = new VBox(7);
        passwordGroup.setAlignment(Pos.CENTER_LEFT);
        Label passLabel = new Label("PASSWORD");
        passLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        passLabel.setStyle(
            "-fx-text-fill: #C5A059; " +
            "-fx-font-family: 'Georgia', serif; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-letter-spacing: 1.5px;"
        );

        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(44);
        passwordField.setFont(Font.font("Segoe UI", 13.5));
        Theme.styleLoginPasswordField(passwordField);
        passwordField.setOnAction(e -> handleLogin());

        plainPasswordField.setPromptText("Enter your password");
        plainPasswordField.setPrefHeight(44);
        plainPasswordField.setFont(Font.font("Segoe UI", 13.5));
        Theme.styleLoginTextField(plainPasswordField);
        plainPasswordField.setVisible(false);
        plainPasswordField.setManaged(false);
        plainPasswordField.setOnAction(e -> handleLogin());

        passwordField.textProperty().bindBidirectional(plainPasswordField.textProperty());

        togglePasswordBtn.setFont(Font.font("System", 14));
        togglePasswordBtn.setStyle("-fx-text-fill: #C5A059; -fx-background-color: transparent; -fx-cursor: hand;");
        togglePasswordBtn.setPadding(new Insets(0, 12, 0, 0));
        togglePasswordBtn.setOnAction(e -> togglePasswordVisibility());
        togglePasswordBtn.setOnMouseEntered(e -> togglePasswordBtn.setStyle("-fx-text-fill: #F3E5AB; -fx-background-color: transparent; -fx-cursor: hand;"));
        togglePasswordBtn.setOnMouseExited(e -> togglePasswordBtn.setStyle("-fx-text-fill: #C5A059; -fx-background-color: transparent; -fx-cursor: hand;"));

        StackPane passwordFieldContainer = new StackPane(passwordField, plainPasswordField, togglePasswordBtn);
        passwordFieldContainer.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(togglePasswordBtn, Pos.CENTER_RIGHT);

        passwordGroup.getChildren().addAll(passLabel, passwordFieldContainer);

        // 4. Primary Sign In Button
        loginButton.setText("SIGN IN");
        loginButton.setPrefHeight(44);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        Theme.styleLoginButton(loginButton);
        loginButton.setOnAction(e -> handleLogin());

        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        progressIndicator.setPrefSize(18, 18);
        progressIndicator.setProgress(-1);

        StackPane buttonStack = new StackPane(loginButton, progressIndicator);
        StackPane.setAlignment(progressIndicator, Pos.CENTER);
        buttonStack.setMaxWidth(Double.MAX_VALUE);
        buttonStack.setPadding(new Insets(8, 0, 0, 0));

        // 5. Status Message Banner
        messageLabel.setFont(Font.font("Georgia", 12.5));
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        messageLabel.setMaxWidth(360);

        loginPanel.getChildren().addAll(headerBox, usernameGroup, passwordGroup, buttonStack, messageLabel);
        rightColumn.getChildren().add(loginPanel);

        borderPane.setRight(rightColumn);
        return borderPane;
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
        FadeTransition formFade = new FadeTransition(Duration.millis(500), loginPanel);
        formFade.setFromValue(0.15);
        formFade.setToValue(1.0);
        formFade.play();
    }

    public VBox getLoginPanel() {
        return loginPanel;
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
            loginButton.setText("SIGN IN");
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);

        if ("error".equals(type)) {
            messageLabel.setTextFill(Color.web("#FCE8E6"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#421815", 0.92), Theme.RADII_MEDIUM, Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#C53030"), BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))
            ));
        } else if ("success".equals(type)) {
            messageLabel.setTextFill(Color.web("#E6F4EA"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#13361E", 0.92), Theme.RADII_MEDIUM, Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#2E7D32"), BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))
            ));
        } else {
            messageLabel.setTextFill(Color.web("#FAF7F2"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#2A1C12", 0.92), Theme.RADII_MEDIUM, Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#C5A059"), BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))
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
        animateEntrance();
    }

    public void stopAnimation() {
        // No heavy background timer running
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

    // --- Cinematic Background Pane with Cover-Fit and Library Stacks Framing ---
    private static class CinematicBackgroundPane extends Pane {
        private final ImageView imageView;
        private final Image bgImage;
        private final Rectangle clipRect;

        public CinematicBackgroundPane() {
            Image loadedImage = null;
            try (InputStream is = getClass().getResourceAsStream("/images/login-background.jpg")) {
                if (is != null) {
                    loadedImage = new Image(is);
                }
            } catch (Exception e) {
                System.err.println("Could not load /images/login-background.jpg: " + e.getMessage());
            }
            this.bgImage = loadedImage;
            this.imageView = new ImageView();
            if (bgImage != null) {
                imageView.setImage(bgImage);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);
            }
            getChildren().add(imageView);

            clipRect = new Rectangle();
            setClip(clipRect);

            widthProperty().addListener((obs, oldVal, newVal) -> updateLayout());
            heightProperty().addListener((obs, oldVal, newVal) -> updateLayout());
        }

        private void updateLayout() {
            double w = getWidth();
            double h = getHeight();
            if (w <= 0 || h <= 0) return;

            clipRect.setWidth(w);
            clipRect.setHeight(h);

            if (bgImage == null) return;
            double imgW = bgImage.getWidth();
            double imgH = bgImage.getHeight();
            if (imgW <= 0 || imgH <= 0) return;

            // Cover scaling (no distortion, no letterboxing, fills entire window)
            double scale = Math.max(w / imgW, h / imgH);
            double fitW = imgW * scale;
            double fitH = imgH * scale;

            imageView.setFitWidth(fitW);
            imageView.setFitHeight(fitH);

            // Anchor left-center so the grand library stacks, rolling ladder, and amber lamps are centered and visible
            double extraX = w - fitW;
            double posX = extraX * 0.25;
            double posY = (h - fitH) * 0.5;

            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            updateLayout();
        }
    }
}