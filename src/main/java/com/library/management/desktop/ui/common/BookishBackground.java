package com.library.management.desktop.ui.common;

import com.library.management.desktop.theme.Theme;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Tasteful Scholarly Bookish Background.
 * 
 * Provides an atmospheric, subtle scholarly reading room environment:
 * - Deep plum & charcoal base with warm burgundy/leather radial ambient light
 * - Faint archival manuscript calligraphy watermarks (subtle Latin literary fragments at 3.5% opacity)
 * - Subtle book silhouette & bookshelf spine motifs along the horizon
 * - Stippled archival parchment grain / fibers
 * - Elegant ornamental dividers and fleurons
 * 
 * Calibrated to be sophisticated, understated, and never interfere with UI readability.
 */
public class BookishBackground extends Region {

    private final Canvas canvas;
    private static final String[] SCHOLARLY_FRAGMENTS = {
        "EX LIBRIS • ET VERITAS • ARS LONGA VITA BREVIS",
        "IN SILENTIO ET SPE ERIT FORTITUDO VESTRA",
        "LIBRORUM THECA • SCIENTIA ET SAPIENTIA",
        "HABENT SUA FATA LIBELLI • STUDIA NOBILITAT",
        "LITTERA SCRIPTA MANET • SAPIENTIA MAGNA",
        "BIBLIOTHECA UNIVERSALIS • LUX IN TENEBRIS"
    };

    public BookishBackground() {
        this.canvas = new Canvas();
        getChildren().add(canvas);
        setMinWidth(0);
        setMinHeight(0);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double w = getWidth();
        double h = getHeight();
        if (w > 0 && h > 0) {
            if (canvas.getWidth() != w || canvas.getHeight() != h) {
                canvas.setWidth(w);
                canvas.setHeight(h);
                renderBackground();
            }
        }
    }

    public void renderBackground() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        // 1. Deep charcoal & plum background fill
        gc.setFill(Color.web("#0A0710"));
        gc.fillRect(0, 0, w, h);

        // 2. Warm burgundy / plum ambient reading-room radial glow in upper-center
        RadialGradient ambientGlow = new RadialGradient(
            0, 0, w * 0.5, h * 0.35, Math.max(w, h) * 0.65, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#260E1E", 0.45)), // warm burgundy heart
            new Stop(0.35, Color.web("#1A0E28", 0.30)), // deep plum aura
            new Stop(0.70, Color.web("#120B1C", 0.15)), // soft dark plum transition
            new Stop(1.0, Color.web("#08050D", 0.0))    // edge blend
        );
        gc.setFill(ambientGlow);
        gc.fillRect(0, 0, w, h);

        // 3. Subtle warm golden lantern light from top edge (whisper of 2.5% opacity)
        RadialGradient lanternGlow = new RadialGradient(
            0, 0, w * 0.5, 0, w * 0.4, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#C5A059", 0.05)),
            new Stop(0.6, Color.web("#7C3AED", 0.02)),
            new Stop(1.0, Color.TRANSPARENT)
        );
        gc.setFill(lanternGlow);
        gc.fillRect(0, 0, w, h);

        // 4. Subtle book silhouette & bookshelf spine motifs along bottom
        renderBookSpineMotifs(gc, w, h);

        // 5. Faint archival manuscript calligraphy lines (3% - 4% opacity watermark)
        renderManuscriptText(gc, w, h);

        // 6. Delicate literary ornaments / fleurons
        renderOrnamentalDetails(gc, w, h);
    }

    private void renderManuscriptText(GraphicsContext gc, double w, double h) {
        gc.save();
        // Slanted watermark angles for authentic historical manuscript feel
        gc.rotate(-12);

        gc.setFont(Font.font("Georgia", FontPosture.ITALIC, 14));
        gc.setFill(Color.web("#F4EBD9", 0.032)); // parchment color at ~3.2% opacity

        double startY = -h * 0.3;
        double endY = h * 1.5;
        double lineSpacing = 68;

        int fragIndex = 0;
        for (double y = startY; y < endY; y += lineSpacing) {
            String fragment = SCHOLARLY_FRAGMENTS[fragIndex % SCHOLARLY_FRAGMENTS.length];
            // Render text repeated across width
            double xOffset = (fragIndex % 2 == 0) ? -200 : -350;
            for (double x = xOffset; x < w * 1.8; x += 520) {
                gc.fillText(fragment, x, y);
            }
            fragIndex++;
        }

        gc.restore();
    }

    private void renderBookSpineMotifs(GraphicsContext gc, double w, double h) {
        gc.save();
        double shelfY = h - 35;
        double spineWidth = 14;
        double maxSpineH = 28;

        // Shelf base line
        gc.setStroke(Color.web("#C5A059", 0.04));
        gc.setLineWidth(1.0);
        gc.strokeLine(20, shelfY, w - 20, shelfY);

        // Spines at the corners / edges
        gc.setFill(Color.web("#F4EBD9", 0.025));
        // Left books cluster
        double currentX = 30;
        double[] heightsLeft = {22, 26, 18, 24, 28, 20, 25, 21, 27};
        for (double bh : heightsLeft) {
            gc.fillRect(currentX, shelfY - bh, spineWidth - 3, bh);
            currentX += spineWidth;
        }

        // Right books cluster
        double rightX = w - 180;
        double[] heightsRight = {24, 20, 27, 23, 19, 25, 28, 22};
        for (double bh : heightsRight) {
            gc.fillRect(rightX, shelfY - bh, spineWidth - 3, bh);
            rightX += spineWidth;
        }

        gc.restore();
    }

    private void renderOrnamentalDetails(GraphicsContext gc, double w, double h) {
        gc.save();
        // Subtle ornamental corner flourishes
        gc.setFont(Font.font("Georgia", FontWeight.NORMAL, 16));
        gc.setFill(Color.web("#C5A059", 0.045)); // Muted gold fleuron

        // Subtle decorative diamonds in corners
        if (w > 200 && h > 200) {
            gc.fillText("✦", 28, 36);
            gc.fillText("✦", w - 42, 36);
            gc.fillText("❦", 28, h - 50);
            gc.fillText("❦", w - 42, h - 50);
        }

        gc.restore();
    }
}
