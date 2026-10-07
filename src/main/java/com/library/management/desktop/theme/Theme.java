package com.library.management.desktop.theme;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Centralized Theme for My Library Desktop Application.
 * Pure JavaFX implementation delivering a premium, minimal, responsive desktop experience.
 */
public final class Theme {

    // --- Core Palette (Near-black with refined dark plum undertones) ---
    public static final Color BG_DEEPEST = Color.web("#08060D");
    public static final Color BG_BASE = Color.web("#0B0813");
    public static final Color BG_DEEP = Color.web("#0E0A17");
    public static final Color BG_ELEVATED = Color.web("#120E1C");
    public static final Color BG_CARD = Color.web("#140F20");
    public static final Color BG_CARD_HOVER = Color.web("#1A142A");
    public static final Color BG_HOVER = Color.web("#1D162F");
    public static final Color BG_INPUT = Color.web("#100C1A");

    // --- Violet / Accent Palette ---
    public static final Color ACCENT_VIOLET = Color.web("#7C3AED");
    public static final Color PURPLE = Color.web("#6D28D9");
    public static final Color PURPLE_LIGHT = Color.web("#8B5CF6");
    public static final Color PURPLE_DARK = Color.web("#221538");
    public static final Color PURPLE_GLOW = Color.web("#A78BFA");
    public static final Color LAVENDER = Color.web("#C4B5FD");
    public static final Color LAVENDER_LIGHT = Color.web("#EDE9FE");

    // --- Typography (Clean Slate Hierarchy) ---
    public static final Color TEXT_PRIMARY = Color.web("#F8FAFC");
    public static final Color TEXT_SECONDARY = Color.web("#94A3B8");
    public static final Color TEXT_MUTED = Color.web("#64748B");
    public static final Color TEXT_ON_PURPLE = Color.web("#FFFFFF");

    // --- Borders (Restrained & Subtle) ---
    public static final Color BORDER_SUBTLE = Color.web("#221A33");
    public static final Color BORDER_MUTED = Color.web("#2A203F");
    public static final Color BORDER_HOVER = Color.web("#47366B");
    public static final Color BORDER_FOCUS = Color.web("#7C3AED");

    // --- Scholarly Reading Room Palette (Plum, Burgundy, Leather, Parchment, Cream, Gold) ---
    public static final Color BURGUNDY_DARK = Color.web("#260E1E");
    public static final Color BURGUNDY_ACCENT = Color.web("#4A152A");
    public static final Color WARM_BROWN = Color.web("#251914");
    public static final Color LEATHER = Color.web("#38231B");
    public static final Color PARCHMENT = Color.web("#F4EBD9");
    public static final Color PARCHMENT_MUTED = Color.web("#CFC4B0");
    public static final Color CREAM = Color.web("#FAF7F2");
    public static final Color GOLD_MUTED = Color.web("#C5A059");
    public static final Color GOLD_ACCENT = Color.web("#D4AF37");
    public static final Color GOLD_BRIGHT = Color.web("#E6C875");
    public static final Color BORDER_GOLD = Color.web("#45361D");
    public static final Color BORDER_BURGUNDY = Color.web("#361726");

    // --- Status ---
    public static final Color SUCCESS = Color.web("#10B981");
    public static final Color WARNING = Color.web("#F59E0B");
    public static final Color ERROR = Color.web("#EF4444");
    public static final Color INFO = Color.web("#38BDF8");

    // --- Gradients (Subtle & Restrained) ---
    public static final LinearGradient BG_GRADIENT = new LinearGradient(
        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#08060D")),
        new Stop(1, Color.web("#0B0813"))
    );

    public static final LinearGradient CARD_GRADIENT = new LinearGradient(
        0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#140F20")),
        new Stop(1, Color.web("#120E1C"))
    );

    public static final LinearGradient BUTTON_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#7C3AED")),
        new Stop(1, Color.web("#6D28D9"))
    );

    public static final LinearGradient BUTTON_HOVER_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#8B5CF6")),
        new Stop(1, Color.web("#7C3AED"))
    );

    public static final LinearGradient BUTTON_PRESSED_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#5B21B6")),
        new Stop(1, Color.web("#4C1D95"))
    );

    public static final LinearGradient SIDEBAR_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#08060D")),
        new Stop(1, Color.web("#0A0711"))
    );

    // --- Corner Radii (Compact & Modern) ---
    public static final CornerRadii RADII_SMALL = new CornerRadii(4);
    public static final CornerRadii RADII_MEDIUM = new CornerRadii(6);
    public static final CornerRadii RADII_LARGE = new CornerRadii(8);

    // --- Subtle Elevation Shadows (Restrained) ---
    public static DropShadow createGlow(Color color, double radius, double spread) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(Math.min(radius, 8));
        glow.setSpread(0.0);
        return glow;
    }

    public static DropShadow createCardShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        shadow.setRadius(6);
        shadow.setOffsetY(2);
        shadow.setSpread(0.0);
        return shadow;
    }

    // --- Crisp Logo Mark (Clean Vector Geometry, No Blurry Glow) ---
    public static Node createLogoMark(double size) {
        StackPane container = new StackPane();
        container.setPrefSize(size, size);
        container.setMaxSize(size, size);
        container.setMinSize(size, size);

        SVGPath bookPath = new SVGPath();
        bookPath.setContent("M 2 4 C 6 2, 10 2, 12 5 C 14 2, 18 2, 22 4 L 22 19 C 18 17, 14 17, 12 20 C 10 17, 6 17, 2 19 Z M 12 5 L 12 20");
        bookPath.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#C4B5FD")),
            new Stop(1, Color.web("#7C3AED"))
        ));
        bookPath.setStroke(Color.web("#DDD6FE", 0.6));
        bookPath.setStrokeWidth(0.75);

        double scale = size / 26.0;
        bookPath.setScaleX(scale);
        bookPath.setScaleY(scale);

        container.getChildren().add(bookPath);
        return container;
    }

    // --- Button Stylers ---
    public static void stylePrimaryButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        btn.setTextFill(TEXT_ON_PURPLE);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(BUTTON_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(Color.web("#A78BFA", 0.25), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        btn.setPadding(new Insets(0, 16, 0, 16));

        DropShadow subtleElevation = new DropShadow();
        subtleElevation.setColor(Color.color(0, 0, 0, 0.25));
        subtleElevation.setRadius(4);
        subtleElevation.setOffsetY(1.5);
        btn.setEffect(subtleElevation);

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BUTTON_HOVER_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(Color.web("#C4B5FD", 0.4), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BUTTON_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(Color.web("#A78BFA", 0.25), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
            }
        });

        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BUTTON_PRESSED_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
            }
        });

        btn.setOnMouseReleased(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BUTTON_HOVER_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
            }
        });
    }

    public static void styleSecondaryButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        btn.setTextFill(TEXT_SECONDARY);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(BG_CARD, RADII_MEDIUM, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        btn.setPadding(new Insets(0, 14, 0, 14));
        btn.setEffect(null);

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BG_CARD_HOVER, RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(BORDER_HOVER, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                btn.setTextFill(TEXT_PRIMARY);
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BG_CARD, RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                btn.setTextFill(TEXT_SECONDARY);
            }
        });

        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BG_DEEPEST, RADII_MEDIUM, Insets.EMPTY)));
            }
        });

        btn.setOnMouseReleased(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BG_CARD_HOVER, RADII_MEDIUM, Insets.EMPTY)));
            }
        });
    }

    public static void styleDangerButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        btn.setTextFill(Color.web("#FCA5A5"));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(Color.web("#2E1017"), RADII_MEDIUM, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(Color.web("#5C1921"), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        btn.setPadding(new Insets(0, 14, 0, 14));
        btn.setEffect(null);

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(Color.web("#3F1620"), RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(Color.web("#7F1D1D"), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                btn.setTextFill(Color.web("#FEE2E2"));
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(Color.web("#2E1017"), RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(Color.web("#5C1921"), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                btn.setTextFill(Color.web("#FCA5A5"));
            }
        });
    }

    public static void stylePaginationButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        btn.setTextFill(TEXT_SECONDARY);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setPrefHeight(30);
        btn.setPadding(new Insets(3, 12, 3, 12));
        btn.setBackground(new Background(new BackgroundFill(BG_CARD, RADII_SMALL, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_SMALL, new BorderWidths(1))));
        btn.setEffect(null);

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BG_CARD_HOVER, RADII_SMALL, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(BORDER_HOVER, BorderStrokeStyle.SOLID, RADII_SMALL, new BorderWidths(1))));
                btn.setTextFill(TEXT_PRIMARY);
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BG_CARD, RADII_SMALL, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_SMALL, new BorderWidths(1))));
                btn.setTextFill(TEXT_SECONDARY);
            }
        });
    }

    // --- Input Field Stylers (Clean & Compact) ---
    public static void styleTextField(TextField field) {
        field.setBackground(new Background(new BackgroundFill(BG_INPUT, RADII_MEDIUM, Insets.EMPTY)));
        field.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        field.setPadding(new Insets(0, 12, 0, 12));
        field.setStyle("-fx-text-fill: #F8FAFC; -fx-prompt-text-fill: #64748B; -fx-highlight-fill: #7C3AED; -fx-highlight-text-fill: white;");

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setBorder(new Border(new BorderStroke(BORDER_FOCUS, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1.5))));
            } else {
                field.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
            }
        });
    }

    public static void stylePasswordField(PasswordField field) {
        field.setBackground(new Background(new BackgroundFill(BG_INPUT, RADII_MEDIUM, Insets.EMPTY)));
        field.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        field.setPadding(new Insets(0, 12, 0, 12));
        field.setStyle("-fx-text-fill: #F8FAFC; -fx-prompt-text-fill: #64748B; -fx-highlight-fill: #7C3AED; -fx-highlight-text-fill: white;");

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setBorder(new Border(new BorderStroke(BORDER_FOCUS, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1.5))));
            } else {
                field.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
            }
        });
    }

    // --- Root Scene Baseline Theme ---
    public static void applyRootTheme(javafx.scene.Parent root) {
        if (root == null) return;
        root.setStyle(
            "-fx-base: #0B0813; " +
            "-fx-background: #0B0813; " +
            "-fx-control-inner-background: #100C1A; " +
            "-fx-control-inner-background-alt: #130E20; " +
            "-fx-focus-color: #7C3AED; " +
            "-fx-faint-focus-color: rgba(124, 58, 237, 0.2); " +
            "-fx-accent: #7C3AED; " +
            "-fx-selection-bar: #4C1D95; " +
            "-fx-selection-bar-non-focused: #3B1675; " +
            "-fx-text-base-color: #F8FAFC; " +
            "-fx-font-family: 'Segoe UI', 'Inter', 'System', sans-serif;"
        );
    }

    public static Node createLoginLogoMark(double size) {
        StackPane container = new StackPane();
        container.setPrefSize(size, size);
        container.setMaxSize(size, size);
        container.setMinSize(size, size);

        SVGPath bookPath = new SVGPath();
        bookPath.setContent("M 2 4 C 6 2, 10 2, 12 5 C 14 2, 18 2, 22 4 L 22 19 C 18 17, 14 17, 12 20 C 10 17, 6 17, 2 19 Z M 12 5 L 12 20");
        bookPath.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#8B5CF6")),
            new Stop(1, Color.web("#6D28D9"))
        ));
        bookPath.setStroke(Color.web("#7C3AED", 0.9));
        bookPath.setStrokeWidth(0.85);

        double scale = size / 26.0;
        bookPath.setScaleX(scale);
        bookPath.setScaleY(scale);

        container.getChildren().add(bookPath);
        return container;
    }

    // --- Login Scene Scholarly Theme ---
    public static void applyLoginTheme(javafx.scene.Parent root) {
        if (root == null) return;
        root.setStyle(
            "-fx-base: #0B0813; " +
            "-fx-background: #0B0813; " +
            "-fx-background-color: #0B0813; " +
            "-fx-control-inner-background: #140F20; " +
            "-fx-control-inner-background-alt: #171125; " +
            "-fx-focus-color: #7C3AED; " +
            "-fx-faint-focus-color: rgba(124, 58, 237, 0.2); " +
            "-fx-accent: #7C3AED; " +
            "-fx-selection-bar: #4C1D95; " +
            "-fx-text-base-color: #FAF7F2; " +
            "-fx-font-family: 'Segoe UI', 'Inter', 'System', sans-serif;"
        );
    }

    public static void styleLoginTextField(TextField field) {
        field.setBackground(new Background(new BackgroundFill(Color.web("#140F22"), RADII_MEDIUM, Insets.EMPTY)));
        field.setPadding(new Insets(0, 12, 0, 12));
        field.setStyle(
            "-fx-background-color: #140F22; " +
            "-fx-text-fill: #FAF7F2; " +
            "-fx-prompt-text-fill: #736787; " +
            "-fx-highlight-fill: #7C3AED; " +
            "-fx-highlight-text-fill: #FFFFFF; " +
            "-fx-border-color: #2E2042; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 13px;"
        );
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: #18122B; " +
                    "-fx-text-fill: #FAF7F2; " +
                    "-fx-prompt-text-fill: #736787; " +
                    "-fx-highlight-fill: #7C3AED; " +
                    "-fx-highlight-text-fill: #FFFFFF; " +
                    "-fx-border-color: #7C3AED; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-width: 1.5px; " +
                    "-fx-font-size: 13px;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: #140F22; " +
                    "-fx-text-fill: #FAF7F2; " +
                    "-fx-prompt-text-fill: #736787; " +
                    "-fx-highlight-fill: #7C3AED; " +
                    "-fx-highlight-text-fill: #FFFFFF; " +
                    "-fx-border-color: #2E2042; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-width: 1px; " +
                    "-fx-font-size: 13px;"
                );
            }
        });
    }

    public static void styleLoginPasswordField(PasswordField field) {
        field.setBackground(new Background(new BackgroundFill(Color.web("#140F22"), RADII_MEDIUM, Insets.EMPTY)));
        field.setPadding(new Insets(0, 12, 0, 12));
        field.setStyle(
            "-fx-background-color: #140F22; " +
            "-fx-text-fill: #FAF7F2; " +
            "-fx-prompt-text-fill: #736787; " +
            "-fx-highlight-fill: #7C3AED; " +
            "-fx-highlight-text-fill: #FFFFFF; " +
            "-fx-border-color: #2E2042; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 13px;"
        );
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: #18122B; " +
                    "-fx-text-fill: #FAF7F2; " +
                    "-fx-prompt-text-fill: #736787; " +
                    "-fx-highlight-fill: #7C3AED; " +
                    "-fx-highlight-text-fill: #FFFFFF; " +
                    "-fx-border-color: #7C3AED; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-width: 1.5px; " +
                    "-fx-font-size: 13px;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: #140F22; " +
                    "-fx-text-fill: #FAF7F2; " +
                    "-fx-prompt-text-fill: #736787; " +
                    "-fx-highlight-fill: #7C3AED; " +
                    "-fx-highlight-text-fill: #FFFFFF; " +
                    "-fx-border-color: #2E2042; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-width: 1px; " +
                    "-fx-font-size: 13px;"
                );
            }
        });
    }

    public static void styleLoginButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        btn.setTextFill(Color.WHITE);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setStyle(
            "-fx-background-color: linear-gradient(to right, #7C3AED, #6D28D9); " +
            "-fx-text-fill: #FFFFFF; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: rgba(196, 181, 253, 0.3); " +
            "-fx-border-radius: 8px; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold;"
        );
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#7C3AED", 0.35));
        glow.setRadius(8);
        btn.setEffect(glow);

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #8B5CF6, #7C3AED); " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: rgba(221, 214, 254, 0.5); " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-width: 1px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-font-weight: bold;"
                );
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #7C3AED, #6D28D9); " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: rgba(196, 181, 253, 0.3); " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-width: 1px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-font-weight: bold;"
                );
            }
        });
        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #5B21B6, #4C1D95); " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: rgba(196, 181, 253, 0.4); " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-width: 1px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-font-weight: bold;"
                );
            }
        });
        btn.setOnMouseReleased(e -> {
            if (!btn.isDisabled()) {
                btn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #8B5CF6, #7C3AED); " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-border-color: rgba(221, 214, 254, 0.5); " +
                    "-fx-border-radius: 8px; " +
                    "-fx-border-width: 1px; " +
                    "-fx-font-size: 13px; " +
                    "-fx-font-weight: bold;"
                );
            }
        });
    }

    // --- Scholarly Status Badge ---
    public static Label createStatusBadge(String text, String statusType) {
        Label badge = new Label(text);
        badge.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        badge.setPadding(new Insets(3, 10, 3, 10));

        String bgCol, borderCol, textCol;
        String s = (statusType != null ? statusType : "").toUpperCase();

        if (s.contains("AVAILABLE") || s.contains("PAID") || s.contains("ACTIVE")) {
            bgCol = "#052E20"; borderCol = "#059669"; textCol = "#6EE7B7";
        } else if (s.contains("BORROWED") || s.contains("PENDING")) {
            bgCol = "#3B2106"; borderCol = "#D97706"; textCol = "#FCD34D";
        } else if (s.contains("OVERDUE") || s.contains("LOST") || s.contains("ERROR")) {
            bgCol = "#3D0C15"; borderCol = "#E11D48"; textCol = "#FDA4AF";
        } else if (s.contains("RETURN") || s.contains("ADMIN")) {
            bgCol = "#231145"; borderCol = "#7C3AED"; textCol = "#DDD6FE";
        } else if (s.contains("DAMAGED") || s.contains("WARNING")) {
            bgCol = "#361608"; borderCol = "#EA580C"; textCol = "#FDBA74";
        } else if (s.contains("WAIVED") || s.contains("INACTIVE")) {
            bgCol = "#1B1724"; borderCol = "#473C5B"; textCol = "#A098B5";
        } else {
            bgCol = "#1E162D"; borderCol = "#5B4482"; textCol = "#D6CCE6";
        }

        badge.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 12px; " +
            "-fx-background-radius: 12px; -fx-border-width: 1px; -fx-text-fill: %s;",
            bgCol, borderCol, textCol
        ));
        return badge;
    }

    // --- Ornamental Literary Separator ---
    public static Node createOrnamentalSeparator() {
        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(6, 0, 6, 0));

        Region leftLine = new Region();
        HBox.setHgrow(leftLine, Priority.ALWAYS);
        leftLine.setPrefHeight(1);
        leftLine.setMaxHeight(1);
        leftLine.setBackground(new Background(new BackgroundFill(
            new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.web("#C5A059", 0.4))
            ),
            CornerRadii.EMPTY, Insets.EMPTY
        )));

        Label diamond = new Label("✦");
        diamond.setFont(Font.font("Georgia", 11));
        diamond.setTextFill(Color.web("#C5A059", 0.75));

        Region rightLine = new Region();
        HBox.setHgrow(rightLine, Priority.ALWAYS);
        rightLine.setPrefHeight(1);
        rightLine.setMaxHeight(1);
        rightLine.setBackground(new Background(new BackgroundFill(
            new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#C5A059", 0.4)),
                new Stop(1, Color.TRANSPARENT)
            ),
            CornerRadii.EMPTY, Insets.EMPTY
        )));

        box.getChildren().addAll(leftLine, diamond, rightLine);
        return box;
    }

    // --- Muted Gold Accent Button ---
    public static void styleGoldButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        btn.setTextFill(Color.web("#1A1208"));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(
            new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#E5C378")),
                new Stop(1, Color.web("#C5A059"))
            ),
            RADII_MEDIUM, Insets.EMPTY
        )));
        btn.setBorder(new Border(new BorderStroke(Color.web("#F3D899", 0.6), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        btn.setPadding(new Insets(0, 16, 0, 16));

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#C5A059", 0.3));
        shadow.setRadius(5);
        btn.setEffect(shadow);

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(
                    new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#F3D899")),
                        new Stop(1, Color.web("#D8B468"))
                    ),
                    RADII_MEDIUM, Insets.EMPTY
                )));
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(
                    new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#E5C378")),
                        new Stop(1, Color.web("#C5A059"))
                    ),
                    RADII_MEDIUM, Insets.EMPTY
                )));
            }
        });
    }

    // --- Standard Scholarly Empty State ---
    public static VBox createEmptyStateNode(String title, String subtitle) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40, 20, 40, 20));

        Label bookIcon = new Label("📖");
        bookIcon.setFont(Font.font("Segoe UI Emoji", 32));
        bookIcon.setOpacity(0.5);

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
        titleLbl.setTextFill(PARCHMENT);

        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subLbl.setTextFill(TEXT_MUTED);

        box.getChildren().addAll(bookIcon, titleLbl, subLbl);
        return box;
    }

    public static void styleScrollPane(ScrollPane scrollPane) {
        if (scrollPane == null) return;
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: transparent; " +
            "-fx-background-color: transparent; " +
            "-fx-base: #0B0813; " +
            "-fx-control-inner-background: #0B0813;"
        );
    }

    public static void styleComboBox(ComboBox<?> combo) {
        combo.setStyle(
            "-fx-base: #100C1A; " +
            "-fx-background-color: #100C1A; " +
            "-fx-control-inner-background: #140F20; " +
            "-fx-border-color: #221A33; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-border-width: 1; " +
            "-fx-accent: #7C3AED; " +
            "-fx-selection-bar: #4C1D95; " +
            "-fx-text-fill: #F8FAFC;"
        );
        combo.setCursor(javafx.scene.Cursor.HAND);
    }

    // --- Card Styler (Clean Minimalist Surface) ---
    public static void styleCard(Region card) {
        card.setBackground(new Background(new BackgroundFill(BG_CARD, RADII_MEDIUM, Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        card.setEffect(null);
    }

    // --- Table Styler (Clean Desktop Table) ---
    public static <T> void styleTableView(TableView<T> table) {
        if (table == null) return;
        table.setStyle(
            "-fx-base: #0B0813; " +
            "-fx-background-color: #0E0A17; " +
            "-fx-control-inner-background: #0B0813; " +
            "-fx-control-inner-background-alt: #100C1A; " +
            "-fx-table-cell-border-color: #1A1326; " +
            "-fx-accent: #7C3AED; " +
            "-fx-selection-bar: #3B1D6E; " +
            "-fx-selection-bar-non-focused: #2C1652; " +
            "-fx-selection-bar-text: #FFFFFF; " +
            "-fx-border-color: #221A33; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-border-width: 1; " +
            "-fx-font-size: 13px;"
        );
        table.setEffect(null);

        table.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("-fx-background-color: transparent;");
                    } else if (isSelected()) {
                        setStyle("-fx-background-color: #3B1D6E; -fx-text-fill: #FFFFFF;");
                    } else if (getIndex() % 2 == 1) {
                        setStyle("-fx-background-color: #100C1A; -fx-text-fill: #F8FAFC;");
                    } else {
                        setStyle("-fx-background-color: #0B0813; -fx-text-fill: #F8FAFC;");
                    }
                }
            };
            row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    row.setStyle("-fx-background-color: #3B1D6E; -fx-text-fill: #FFFFFF;");
                } else if (row.getIndex() % 2 == 1) {
                    row.setStyle("-fx-background-color: #100C1A; -fx-text-fill: #F8FAFC;");
                } else {
                    row.setStyle("-fx-background-color: #0B0813; -fx-text-fill: #F8FAFC;");
                }
            });
            row.setOnMouseEntered(e -> {
                if (!row.isSelected() && !row.isEmpty()) {
                    row.setStyle("-fx-background-color: #1A142A; -fx-text-fill: #F8FAFC; -fx-cursor: hand;");
                }
            });
            row.setOnMouseExited(e -> {
                if (!row.isSelected() && !row.isEmpty()) {
                    if (row.getIndex() % 2 == 1) {
                        row.setStyle("-fx-background-color: #100C1A; -fx-text-fill: #F8FAFC;");
                    } else {
                        row.setStyle("-fx-background-color: #0B0813; -fx-text-fill: #F8FAFC;");
                    }
                }
            });
            return row;
        });
    }

    public static void styleEntityView(
            VBox container,
            Label titleLabel,
            TextField searchField,
            Button refreshButton,
            Button addButton,
            Button editButton,
            Button deleteButton,
            TableView<?> table,
            Button prevButton,
            Label pageInfoLabel,
            Button nextButton,
            ComboBox<?> pageSizeCombo,
            Pane rootWrapper
    ) {
        if (container != null) {
            container.setBackground(new Background(new BackgroundFill(BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));
            container.setPadding(new Insets(20, 24, 20, 24));
        }
        if (titleLabel != null) {
            titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            titleLabel.setTextFill(TEXT_PRIMARY);
        }
        if (searchField != null) {
            searchField.setPrefHeight(34);
            styleTextField(searchField);
        }
        if (refreshButton != null) {
            refreshButton.setPrefHeight(34);
            styleSecondaryButton(refreshButton);
        }
        if (addButton != null) {
            addButton.setPrefHeight(34);
            stylePrimaryButton(addButton);
        }
        if (editButton != null) {
            editButton.setPrefHeight(34);
            styleSecondaryButton(editButton);
        }
        if (deleteButton != null) {
            deleteButton.setPrefHeight(34);
            styleDangerButton(deleteButton);
        }
        if (table != null) {
            styleTableView(table);
        }
        if (prevButton != null) {
            stylePaginationButton(prevButton);
        }
        if (pageInfoLabel != null) {
            pageInfoLabel.setFont(Font.font("System", FontWeight.MEDIUM, 12));
            pageInfoLabel.setTextFill(TEXT_SECONDARY);
        }
        if (nextButton != null) {
            stylePaginationButton(nextButton);
        }
        if (pageSizeCombo != null) {
            styleComboBox(pageSizeCombo);
            pageSizeCombo.setPrefHeight(30);
        }
        if (rootWrapper != null) {
            rootWrapper.setBackground(new Background(new BackgroundFill(BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    // --- Responsive Dialog Styler ---
    public static void styleDialog(Dialog<?> dialog) {
        dialog.setResizable(true);
        DialogPane pane = dialog.getDialogPane();
        pane.setBackground(new Background(new BackgroundFill(BG_DEEP, RADII_LARGE, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(BORDER_MUTED, BorderStrokeStyle.SOLID, RADII_LARGE, new BorderWidths(1))));
        pane.setStyle(
            "-fx-background-color: #0E0A17; " +
            "-fx-border-color: #2A203F; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #F8FAFC;"
        );

        pane.setPrefWidth(500);
        pane.setMaxHeight(640);

        if (pane.getContent() != null) {
            if (pane.getContent() instanceof ScrollPane) {
                ScrollPane sp = (ScrollPane) pane.getContent();
                sp.setFitToWidth(true);
                styleScrollPane(sp);
            }
            styleNodeHierarchy(pane.getContent());
        }
        if (pane.getHeader() != null) {
            styleNodeHierarchy(pane.getHeader());
        }
        for (Node n : pane.lookupAll(".label")) {
            if (n instanceof Label) {
                ((Label) n).setTextFill(TEXT_PRIMARY);
            }
        }

        // Style OK/Save/YES button and Cancel/NO button
        for (ButtonType bt : pane.getButtonTypes()) {
            Button btn = (Button) pane.lookupButton(bt);
            if (btn != null) {
                if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE
                        || bt.getButtonData() == ButtonBar.ButtonData.APPLY
                        || bt.getButtonData() == ButtonBar.ButtonData.YES) {
                    stylePrimaryButton(btn);
                    btn.setPrefHeight(34);
                    btn.setPadding(new Insets(0, 18, 0, 18));
                } else {
                    styleSecondaryButton(btn);
                    btn.setPrefHeight(34);
                    btn.setPadding(new Insets(0, 16, 0, 16));
                }
            }
        }
    }

    public static void styleNodeHierarchy(Node node) {
        if (node instanceof Label) {
            Label lbl = (Label) node;
            lbl.setTextFill(TEXT_PRIMARY);
            lbl.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        } else if (node instanceof TextField) {
            if (node instanceof PasswordField) {
                stylePasswordField((PasswordField) node);
            } else {
                styleTextField((TextField) node);
            }
        } else if (node instanceof ComboBox) {
            styleComboBox((ComboBox<?>) node);
        } else if (node instanceof Spinner) {
            node.setStyle("-fx-background-color: #100C1A; -fx-border-color: #221A33; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #F8FAFC;");
        } else if (node instanceof TextArea) {
            TextArea ta = (TextArea) node;
            ta.setBackground(new Background(new BackgroundFill(BG_INPUT, RADII_MEDIUM, Insets.EMPTY)));
            ta.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
            ta.setStyle("-fx-text-fill: #F8FAFC; -fx-prompt-text-fill: #64748B;");
        } else if (node instanceof ScrollPane) {
            ScrollPane sp = (ScrollPane) node;
            sp.setFitToWidth(true);
            styleScrollPane(sp);
        }

        if (node instanceof javafx.scene.Parent) {
            for (Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                styleNodeHierarchy(child);
            }
        }
    }

    private Theme() {}
}