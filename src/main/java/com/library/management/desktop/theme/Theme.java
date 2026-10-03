package com.library.management.desktop.theme;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public final class Theme {

    // --- Core Palette (Dark Atmosphere with Plum/Purple Undertones) ---
    public static final Color BG_DEEPEST = Color.web("#07050A");
    public static final Color BG_BASE = Color.web("#0C0916");
    public static final Color BG_DEEP = Color.web("#0D0917");
    public static final Color BG_ELEVATED = Color.web("#140E24");
    public static final Color BG_CARD = Color.web("#18122B");
    public static final Color BG_CARD_HOVER = Color.web("#22183D");
    public static final Color BG_HOVER = Color.web("#251A40");
    public static final Color BG_INPUT = Color.web("#100C1C");

    // --- Purple & Lavender Accents ---
    public static final Color PURPLE_DARK = Color.web("#281447");
    public static final Color PURPLE = Color.web("#6D28D9");
    public static final Color PURPLE_LIGHT = Color.web("#8B5CF6");
    public static final Color PURPLE_GLOW = Color.web("#A78BFA");
    public static final Color ACCENT_VIOLET = Color.web("#7C3AED");

    public static final Color LAVENDER = Color.web("#C4B5FD");
    public static final Color LAVENDER_LIGHT = Color.web("#EDE9FE");

    // --- Typography ---
    public static final Color TEXT_PRIMARY = Color.web("#F8FAFC");
    public static final Color TEXT_SECONDARY = Color.web("#C4B5FD");
    public static final Color TEXT_MUTED = Color.web("#8B7FA3");
    public static final Color TEXT_ON_PURPLE = Color.web("#FFFFFF");

    // --- Borders ---
    public static final Color BORDER_SUBTLE = Color.web("#291E3F");
    public static final Color BORDER_MUTED = Color.web("#382956");
    public static final Color BORDER_HOVER = Color.web("#5B438A");
    public static final Color BORDER_FOCUS = Color.web("#8B5CF6");

    // --- Status ---
    public static final Color SUCCESS = Color.web("#10B981");
    public static final Color WARNING = Color.web("#F59E0B");
    public static final Color ERROR = Color.web("#EF4444");
    public static final Color INFO = Color.web("#38BDF8");

    // --- Gradients ---
    public static final LinearGradient BG_GRADIENT = new LinearGradient(
        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#07050A")),
        new Stop(0.45, Color.web("#0D0917")),
        new Stop(1, Color.web("#140E24"))
    );

    public static final LinearGradient CARD_GRADIENT = new LinearGradient(
        0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#1C1532")),
        new Stop(1, Color.web("#140E26"))
    );

    public static final LinearGradient PURPLE_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#7C3AED")),
        new Stop(1, Color.web("#8B5CF6"))
    );

    public static final LinearGradient BUTTON_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#7C3AED")),
        new Stop(1, Color.web("#8B5CF6"))
    );

    public static final LinearGradient BUTTON_HOVER_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#8B5CF6")),
        new Stop(1, Color.web("#A78BFA"))
    );

    public static final LinearGradient BUTTON_PRESSED_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#6D28D9")),
        new Stop(1, Color.web("#7C3AED"))
    );

    public static final LinearGradient SIDEBAR_GRADIENT = new LinearGradient(
        0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#08050D")),
        new Stop(1, Color.web("#0D0917"))
    );

    public static final LinearGradient STAT_CARD_GRADIENT_1 = new LinearGradient(
        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#2B1749")),
        new Stop(1, Color.web("#160E27"))
    );

    public static final LinearGradient STAT_CARD_GRADIENT_2 = new LinearGradient(
        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#361B58")),
        new Stop(1, Color.web("#190F2C"))
    );

    public static final LinearGradient STAT_CARD_GRADIENT_3 = new LinearGradient(
        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#24153F")),
        new Stop(1, Color.web("#130B22"))
    );

    public static final LinearGradient STAT_CARD_GRADIENT_4 = new LinearGradient(
        0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#1F1E4A")),
        new Stop(1, Color.web("#100F29"))
    );

    public static final RadialGradient LOGIN_RADIAL_GLOW = new RadialGradient(
        0, 0, 0.5, 0.42, 0.65, true, CycleMethod.NO_CYCLE,
        new Stop(0, Color.web("#6D28D9", 0.35)),
        new Stop(0.5, Color.web("#3B1768", 0.45)),
        new Stop(1, Color.web("#07050A", 0.95))
    );

    // --- Corner Radii ---
    public static final CornerRadii RADII_SMALL = new CornerRadii(6);
    public static final CornerRadii RADII_MEDIUM = new CornerRadii(10);
    public static final CornerRadii RADII_LARGE = new CornerRadii(16);

    // --- Shadows ---
    public static DropShadow createGlow(Color color, double radius, double spread) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(radius);
        glow.setSpread(spread);
        return glow;
    }

    public static DropShadow createCardShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.45));
        shadow.setRadius(24);
        shadow.setOffsetY(8);
        shadow.setSpread(0.06);
        return shadow;
    }

    // --- Logo Mark Factory ---
    public static Node createLogoMark(double size) {
        StackPane container = new StackPane();
        container.setPrefSize(size, size);
        container.setMaxSize(size, size);
        container.setMinSize(size, size);

        // Elegant geometric open book symbol
        SVGPath bookPath = new SVGPath();
        bookPath.setContent("M 2 4 C 6 2, 10 2, 12 5 C 14 2, 18 2, 22 4 L 22 19 C 18 17, 14 17, 12 20 C 10 17, 6 17, 2 19 Z M 12 5 L 12 20");
        bookPath.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#C4B5FD")),
            new Stop(0.5, Color.web("#8B5CF6")),
            new Stop(1, Color.web("#7C3AED"))
        ));
        bookPath.setStroke(Color.web("#DDD6FE", 0.5));
        bookPath.setStrokeWidth(0.75);

        double scale = size / 26.0;
        bookPath.setScaleX(scale);
        bookPath.setScaleY(scale);

        DropShadow markGlow = new DropShadow();
        markGlow.setColor(Color.web("#8B5CF6", 0.6));
        markGlow.setRadius(size * 0.45);
        markGlow.setSpread(0.15);
        bookPath.setEffect(markGlow);

        container.getChildren().add(bookPath);
        return container;
    }

    // --- Button Stylers ---
    public static void stylePrimaryButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        btn.setTextFill(TEXT_ON_PURPLE);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(BUTTON_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(Color.web("#A78BFA", 0.3), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));

        DropShadow btnGlow = new DropShadow();
        btnGlow.setColor(Color.web("#7C3AED", 0.4));
        btnGlow.setRadius(12);
        btnGlow.setOffsetY(3);
        btn.setEffect(btnGlow);

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BUTTON_HOVER_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
                btnGlow.setColor(Color.web("#8B5CF6", 0.65));
                btnGlow.setRadius(16);
                ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
                st.setToX(1.02);
                st.setToY(1.02);
                st.play();
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BUTTON_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
                btnGlow.setColor(Color.web("#7C3AED", 0.4));
                btnGlow.setRadius(12);
                ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        });

        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BUTTON_PRESSED_GRADIENT, RADII_MEDIUM, Insets.EMPTY)));
                ScaleTransition st = new ScaleTransition(Duration.millis(60), btn);
                st.setToX(0.98);
                st.setToY(0.98);
                st.play();
            }
        });

        btn.setOnMouseReleased(e -> {
            if (!btn.isDisabled()) {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        });
    }

    public static void styleSecondaryButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        btn.setTextFill(TEXT_SECONDARY);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(BG_CARD, RADII_MEDIUM, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BG_CARD_HOVER, RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(BORDER_HOVER, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                btn.setTextFill(TEXT_PRIMARY);
                ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
                st.setToX(1.02);
                st.setToY(1.02);
                st.play();
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(BG_CARD, RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                btn.setTextFill(TEXT_SECONDARY);
                ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        });

        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                ScaleTransition st = new ScaleTransition(Duration.millis(60), btn);
                st.setToX(0.98);
                st.setToY(0.98);
                st.play();
            }
        });

        btn.setOnMouseReleased(e -> {
            if (!btn.isDisabled()) {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        });
    }

    public static void styleDangerButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        btn.setTextFill(Color.web("#FCA5A5"));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(Color.web("#3A131C"), RADII_MEDIUM, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(Color.web("#7F1D1D"), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(Color.web("#541B26"), RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(Color.web("#991B1B"), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                btn.setTextFill(Color.web("#FEE2E2"));
                ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
                st.setToX(1.02);
                st.setToY(1.02);
                st.play();
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) {
                btn.setBackground(new Background(new BackgroundFill(Color.web("#3A131C"), RADII_MEDIUM, Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(Color.web("#7F1D1D"), BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                btn.setTextFill(Color.web("#FCA5A5"));
                ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        });

        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                ScaleTransition st = new ScaleTransition(Duration.millis(60), btn);
                st.setToX(0.98);
                st.setToY(0.98);
                st.play();
            }
        });

        btn.setOnMouseReleased(e -> {
            if (!btn.isDisabled()) {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        });
    }

    public static void stylePaginationButton(Button btn) {
        btn.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        btn.setTextFill(TEXT_SECONDARY);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setPrefHeight(32);
        btn.setPadding(new Insets(4, 14, 4, 14));
        btn.setBackground(new Background(new BackgroundFill(BG_CARD, RADII_SMALL, Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_SMALL, new BorderWidths(1))));

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

    // --- Input Field Stylers ---
    public static void styleTextField(TextField field) {
        field.setBackground(new Background(new BackgroundFill(BG_INPUT, RADII_MEDIUM, Insets.EMPTY)));
        field.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        field.setPadding(new Insets(0, 14, 0, 14));
        field.setStyle("-fx-text-fill: #F8FAFC; -fx-prompt-text-fill: #786D94; -fx-highlight-fill: #7C3AED; -fx-highlight-text-fill: white;");

        DropShadow focusGlow = new DropShadow();
        focusGlow.setColor(Color.web("#8B5CF6", 0.4));
        focusGlow.setRadius(12);
        focusGlow.setSpread(0.1);

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setBorder(new Border(new BorderStroke(BORDER_FOCUS, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1.5))));
                field.setEffect(focusGlow);
            } else {
                field.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                field.setEffect(null);
            }
        });
    }

    public static void stylePasswordField(PasswordField field) {
        field.setBackground(new Background(new BackgroundFill(BG_INPUT, RADII_MEDIUM, Insets.EMPTY)));
        field.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
        field.setPadding(new Insets(0, 14, 0, 14));
        field.setStyle("-fx-text-fill: #F8FAFC; -fx-prompt-text-fill: #786D94; -fx-highlight-fill: #7C3AED; -fx-highlight-text-fill: white;");

        DropShadow focusGlow = new DropShadow();
        focusGlow.setColor(Color.web("#8B5CF6", 0.4));
        focusGlow.setRadius(12);
        focusGlow.setSpread(0.1);

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setBorder(new Border(new BorderStroke(BORDER_FOCUS, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1.5))));
                field.setEffect(focusGlow);
            } else {
                field.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
                field.setEffect(null);
            }
        });
    }

    // --- Root Scene Theme (Java-only baseline replacing external CSS) ---
    public static void applyRootTheme(javafx.scene.Parent root) {
        if (root == null) return;
        root.setStyle(
            "-fx-base: #0D0917; " +
            "-fx-background: #0D0917; " +
            "-fx-control-inner-background: #120D22; " +
            "-fx-control-inner-background-alt: #140E26; " +
            "-fx-focus-color: #8B5CF6; " +
            "-fx-faint-focus-color: rgba(139, 92, 246, 0.25); " +
            "-fx-accent: #7C3AED; " +
            "-fx-selection-bar: #4C1D95; " +
            "-fx-selection-bar-non-focused: #3B1675; " +
            "-fx-text-base-color: #F8FAFC; " +
            "-fx-font-family: 'Segoe UI', 'Inter', 'System', sans-serif;"
        );
    }

    public static void styleScrollPane(ScrollPane scrollPane) {
        if (scrollPane == null) return;
        scrollPane.setStyle(
            "-fx-background: transparent; " +
            "-fx-background-color: transparent; " +
            "-fx-base: #0D0917; " +
            "-fx-control-inner-background: #0D0917;"
        );
    }

    public static void styleComboBox(ComboBox<?> combo) {
        combo.setStyle(
            "-fx-base: #100C1C; " +
            "-fx-background-color: #100C1C; " +
            "-fx-control-inner-background: #150F28; " +
            "-fx-border-color: #291E3F; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-border-width: 1; " +
            "-fx-accent: #7C3AED; " +
            "-fx-selection-bar: #4C1D95; " +
            "-fx-text-fill: #F8FAFC;"
        );
        combo.setCursor(javafx.scene.Cursor.HAND);
    }

    // --- Card Styler ---
    public static void styleCard(Region card) {
        card.setBackground(new Background(new BackgroundFill(CARD_GRADIENT, RADII_LARGE, Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_LARGE, new BorderWidths(1))));
        card.setEffect(createCardShadow());
    }

    // --- Table Styler (Java-only replacing external CSS table definitions) ---
    public static <T> void styleTableView(TableView<T> table) {
        if (table == null) return;
        table.setStyle(
            "-fx-base: #0D0917; " +
            "-fx-background-color: #120D22; " +
            "-fx-control-inner-background: #120D22; " +
            "-fx-control-inner-background-alt: #140E26; " +
            "-fx-table-cell-border-color: #1F1735; " +
            "-fx-accent: #7C3AED; " +
            "-fx-selection-bar: #4C1D95; " +
            "-fx-selection-bar-non-focused: #3B1675; " +
            "-fx-selection-bar-text: #FFFFFF; " +
            "-fx-border-color: #291E3F; " +
            "-fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-border-width: 1; " +
            "-fx-font-size: 13px;"
        );
        table.setEffect(new DropShadow(16, 0, 4, Color.color(0, 0, 0, 0.3)));

        table.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("-fx-background-color: transparent;");
                    } else if (isSelected()) {
                        setStyle("-fx-background-color: #4C1D95; -fx-text-fill: #FFFFFF;");
                    } else if (getIndex() % 2 == 1) {
                        setStyle("-fx-background-color: #140E26; -fx-text-fill: #F8FAFC;");
                    } else {
                        setStyle("-fx-background-color: #120D22; -fx-text-fill: #F8FAFC;");
                    }
                }
            };
            row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    row.setStyle("-fx-background-color: #4C1D95; -fx-text-fill: #FFFFFF;");
                } else if (row.getIndex() % 2 == 1) {
                    row.setStyle("-fx-background-color: #140E26; -fx-text-fill: #F8FAFC;");
                } else {
                    row.setStyle("-fx-background-color: #120D22; -fx-text-fill: #F8FAFC;");
                }
            });
            row.setOnMouseEntered(e -> {
                if (!row.isSelected() && !row.isEmpty()) {
                    row.setStyle("-fx-background-color: #22163D; -fx-text-fill: #F8FAFC; -fx-cursor: hand;");
                }
            });
            row.setOnMouseExited(e -> {
                if (!row.isSelected() && !row.isEmpty()) {
                    if (row.getIndex() % 2 == 1) {
                        row.setStyle("-fx-background-color: #140E26; -fx-text-fill: #F8FAFC;");
                    } else {
                        row.setStyle("-fx-background-color: #120D22; -fx-text-fill: #F8FAFC;");
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
            container.setPadding(new Insets(24, 28, 24, 28));
        }
        if (titleLabel != null) {
            titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
            titleLabel.setTextFill(TEXT_PRIMARY);
        }
        if (searchField != null) {
            searchField.setPrefHeight(38);
            styleTextField(searchField);
        }
        if (refreshButton != null) {
            refreshButton.setPrefHeight(38);
            styleSecondaryButton(refreshButton);
        }
        if (addButton != null) {
            addButton.setPrefHeight(38);
            stylePrimaryButton(addButton);
        }
        if (editButton != null) {
            editButton.setPrefHeight(38);
            styleSecondaryButton(editButton);
        }
        if (deleteButton != null) {
            deleteButton.setPrefHeight(38);
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
            pageSizeCombo.setPrefHeight(32);
        }
        if (rootWrapper != null) {
            rootWrapper.setBackground(new Background(new BackgroundFill(BG_BASE, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    // --- Dialog Styler ---
    public static void styleDialog(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        pane.setBackground(new Background(new BackgroundFill(BG_DEEP, RADII_LARGE, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(BORDER_MUTED, BorderStrokeStyle.SOLID, RADII_LARGE, new BorderWidths(1))));
        pane.setStyle(
            "-fx-background-color: #0D0917; " +
            "-fx-border-color: #382956; " +
            "-fx-border-radius: 14; " +
            "-fx-background-radius: 14;"
        );

        if (pane.getContent() != null) {
            styleNodeHierarchy(pane.getContent());
        }
        if (pane.getHeader() != null) {
            styleNodeHierarchy(pane.getHeader());
        }

        // Style OK/Save button and Cancel button
        for (ButtonType bt : pane.getButtonTypes()) {
            Button btn = (Button) pane.lookupButton(bt);
            if (btn != null) {
                if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE || bt.getButtonData() == ButtonBar.ButtonData.APPLY) {
                    stylePrimaryButton(btn);
                    btn.setPrefHeight(38);
                    btn.setPadding(new Insets(0, 20, 0, 20));
                } else {
                    styleSecondaryButton(btn);
                    btn.setPrefHeight(38);
                    btn.setPadding(new Insets(0, 20, 0, 20));
                }
            }
        }
    }

    public static void styleNodeHierarchy(Node node) {
        if (node instanceof Label) {
            Label lbl = (Label) node;
            lbl.setTextFill(TEXT_PRIMARY);
            lbl.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        } else if (node instanceof TextField) {
            if (node instanceof PasswordField) {
                stylePasswordField((PasswordField) node);
            } else {
                styleTextField((TextField) node);
            }
        } else if (node instanceof ComboBox) {
            styleComboBox((ComboBox<?>) node);
        } else if (node instanceof Spinner) {
            node.setStyle("-fx-background-color: #100C1C; -fx-border-color: #291E3F; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #F8FAFC;");
        } else if (node instanceof TextArea) {
            TextArea ta = (TextArea) node;
            ta.setBackground(new Background(new BackgroundFill(BG_INPUT, RADII_MEDIUM, Insets.EMPTY)));
            ta.setBorder(new Border(new BorderStroke(BORDER_SUBTLE, BorderStrokeStyle.SOLID, RADII_MEDIUM, new BorderWidths(1))));
            ta.setStyle("-fx-text-fill: #F8FAFC; -fx-prompt-text-fill: #786D94;");
        }

        if (node instanceof javafx.scene.Parent) {
            for (Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                styleNodeHierarchy(child);
            }
        }
    }

    private Theme() {}
}