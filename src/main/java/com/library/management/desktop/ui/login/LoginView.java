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
        this.loginButton = new Button("Sign In");
        this.messageLabel = new Label();
        this.progressIndicator = new ProgressIndicator();
        this.titleLabel = new Label("My Library");
        this.subtitleLabel = new Label("Your books. Your knowledge. Your library.");
        this.logoNode = Theme.createLoginLogoMark(38);
        this.loginPanel = new VBox(18);

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
                new Stop(0.00, Color.color(0.04, 0.02, 0.07, 0.00)),   // Leftmost edge: completely transparent
                new Stop(0.35, Color.color(0.04, 0.02, 0.07, 0.04)),   // Chariot & divine horses fully clear
                new Stop(0.52, Color.color(0.05, 0.03, 0.08, 0.28)),   // Gentle atmospheric blend begins
                new Stop(0.68, Color.color(0.06, 0.03, 0.10, 0.68)),   // Atmosphere deepens smoothly
                new Stop(0.85, Color.color(0.06, 0.02, 0.09, 0.88)),   // Darker backdrop for form contrast
                new Stop(1.00, Color.color(0.05, 0.02, 0.08, 0.94))    // Far right margin
            ),
            CornerRadii.EMPTY, Insets.EMPTY
        )));
        return overlay;
    }

    private Node createContentLayout() {
        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(Background.EMPTY);

        // Center / Left area is left open so the Mahabharata chariot artwork dominates
        Region spacer = new Region();
        borderPane.setCenter(spacer);

        // Right side container for the login panel
        VBox rightColumn = new VBox();
        rightColumn.setAlignment(Pos.CENTER);
        rightColumn.setPrefWidth(460);
        rightColumn.setMaxWidth(480);
        rightColumn.setMinWidth(350);
        rightColumn.setPadding(new Insets(24, 72, 24, 24));

        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setMaxWidth(380);
        loginPanel.setMinWidth(320);
        loginPanel.setPadding(new Insets(32, 28, 32, 28));
        loginPanel.setBackground(new Background(new BackgroundFill(
            Color.web("#100A1A", 0.58), new CornerRadii(12), Insets.EMPTY
        )));
        loginPanel.setBorder(new Border(new BorderStroke(
            Color.web("#C5A059", 0.22), BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(1.0)
        )));

        DropShadow panelShadow = new DropShadow();
        panelShadow.setColor(Color.color(0, 0, 0, 0.40));
        panelShadow.setRadius(20);
        panelShadow.setOffsetY(4);
        loginPanel.setEffect(panelShadow);

        // 1. Header: Book Icon + Title + Subtitle + Ornamental Separator
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 6, 0));

        titleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #FAF7F2; -fx-font-family: 'Georgia', serif; -fx-font-size: 28px; -fx-font-weight: bold;");

        subtitleLabel.setFont(Font.font("Georgia", FontPosture.ITALIC, 12.5));
        subtitleLabel.setStyle("-fx-text-fill: #D4C7B5; -fx-font-family: 'Georgia', serif; -fx-font-size: 12.5px; -fx-font-style: italic;");

        Node separator = Theme.createOrnamentalSeparator();

        headerBox.getChildren().addAll(logoNode, titleLabel, subtitleLabel, separator);

        // 2. Username Field Group
        VBox usernameGroup = new VBox(6);
        usernameGroup.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        userLabel.setStyle("-fx-text-fill: #E2D9CC; -fx-font-size: 12px; -fx-font-weight: 600;");

        usernameField.setPromptText("Enter username");
        usernameField.setPrefHeight(42);
        usernameField.setFont(Font.font("System", 13));
        Theme.styleLoginTextField(usernameField);
        usernameField.setOnAction(e -> handleLogin());
        usernameGroup.getChildren().addAll(userLabel, usernameField);

        // 3. Password Field Group with Visibility Toggle
        VBox passwordGroup = new VBox(6);
        passwordGroup.setAlignment(Pos.CENTER_LEFT);
        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        passLabel.setStyle("-fx-text-fill: #E2D9CC; -fx-font-size: 12px; -fx-font-weight: 600;");

        passwordField.setPromptText("Enter password");
        passwordField.setPrefHeight(42);
        passwordField.setFont(Font.font("System", 13));
        Theme.styleLoginPasswordField(passwordField);
        passwordField.setOnAction(e -> handleLogin());

        plainPasswordField.setPromptText("Enter password");
        plainPasswordField.setPrefHeight(42);
        plainPasswordField.setFont(Font.font("System", 13));
        Theme.styleLoginTextField(plainPasswordField);
        plainPasswordField.setVisible(false);
        plainPasswordField.setManaged(false);
        plainPasswordField.setOnAction(e -> handleLogin());

        passwordField.textProperty().bindBidirectional(plainPasswordField.textProperty());

        togglePasswordBtn.setFont(Font.font("System", 13));
        togglePasswordBtn.setStyle("-fx-text-fill: #C5A059; -fx-background-color: transparent; -fx-cursor: hand;");
        togglePasswordBtn.setPadding(new Insets(0, 12, 0, 0));
        togglePasswordBtn.setOnAction(e -> togglePasswordVisibility());
        togglePasswordBtn.setOnMouseEntered(e -> togglePasswordBtn.setStyle("-fx-text-fill: #F3E5AB; -fx-background-color: transparent; -fx-cursor: hand;"));
        togglePasswordBtn.setOnMouseExited(e -> togglePasswordBtn.setStyle("-fx-text-fill: #C5A059; -fx-background-color: transparent; -fx-cursor: hand;"));

        StackPane passwordFieldContainer = new StackPane(passwordField, plainPasswordField, togglePasswordBtn);
        StackPane.setAlignment(togglePasswordBtn, Pos.CENTER_RIGHT);

        passwordGroup.getChildren().addAll(passLabel, passwordFieldContainer);

        // 4. Primary Sign In Button
        loginButton.setText("Sign In");
        loginButton.setPrefHeight(42);
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

        loginPanel.getChildren().addAll(headerBox, usernameGroup, passwordGroup, buttonStack, messageLabel);
        rightColumn.getChildren().add(loginPanel);

        // Root ScrollPane with transparent styling so the background artwork seamlessly shows through
        ScrollPane rightScrollPane = new ScrollPane(rightColumn);
        rightScrollPane.setFitToWidth(true);
        rightScrollPane.setFitToHeight(true);
        rightScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScrollPane.setStyle(
            "-fx-background: transparent; " +
            "-fx-background-color: transparent; " +
            "-fx-control-inner-background: transparent; " +
            "-fx-background-insets: 0; " +
            "-fx-padding: 0;"
        );

        borderPane.setRight(rightScrollPane);
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
        FadeTransition formFade = new FadeTransition(Duration.millis(260), loginPanel);
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
                new BackgroundFill(Color.web("#3D0C15", 0.90), Theme.RADII_MEDIUM, Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#E11D48"), BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))
            ));
        } else if ("success".equals(type)) {
            messageLabel.setTextFill(Color.web("#A7F3D0"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#052E20", 0.90), Theme.RADII_MEDIUM, Insets.EMPTY)
            ));
            messageLabel.setBorder(new Border(
                new BorderStroke(Color.web("#059669"), BorderStrokeStyle.SOLID, Theme.RADII_MEDIUM, new BorderWidths(1))
            ));
        } else {
            messageLabel.setTextFill(Color.web("#DDD6FE"));
            messageLabel.setBackground(new Background(
                new BackgroundFill(Color.web("#231145", 0.90), Theme.RADII_MEDIUM, Insets.EMPTY)
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

    // --- Cinematic Background Pane with Cover-Fit and Left-Center Framing ---
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

            // Anchor left-center so the chariot, steeds, and warriors are framed prominently
            double extraX = w - fitW;
            double posX = extraX * 0.15;
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