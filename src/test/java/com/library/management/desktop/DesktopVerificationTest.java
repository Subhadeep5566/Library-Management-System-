package com.library.management.desktop;

import com.library.management.desktop.service.ApiClient;
import com.library.management.desktop.service.ApiService;
import com.library.management.desktop.service.AuthService;
import com.library.management.desktop.theme.Theme;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Desktop UI & Real Backend Integration Verification")
@Tag("integration")
public class DesktopVerificationTest {

    private static boolean javaFxStarted = false;

    @BeforeAll
    static void initJavaFx() {
        try {
            Platform.startup(() -> {});
            javaFxStarted = true;
        } catch (IllegalStateException e) {
            // Platform already started
            javaFxStarted = true;
        } catch (Exception e) {
            System.err.println("Could not initialize JavaFX toolkit: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Verify Theme Palette Constants and Styling Engine")
    void testThemeEngine() throws InterruptedException {
        assertNotNull(Theme.BG_DEEPEST, "BG_DEEPEST should be defined");
        assertNotNull(Theme.BG_BASE, "BG_BASE should be defined");
        assertNotNull(Theme.BG_CARD, "BG_CARD should be defined");
        assertNotNull(Theme.ACCENT_VIOLET, "ACCENT_VIOLET should be defined");
        assertNotNull(Theme.PURPLE_LIGHT, "PURPLE_LIGHT should be defined");
        assertNotNull(Theme.LAVENDER, "LAVENDER should be defined");
        assertNotNull(Theme.BORDER_SUBTLE, "BORDER_SUBTLE should be defined");
        assertNotNull(Theme.BURGUNDY_DARK, "BURGUNDY_DARK should be defined");
        assertNotNull(Theme.LEATHER, "LEATHER should be defined");
        assertNotNull(Theme.PARCHMENT, "PARCHMENT should be defined");
        assertNotNull(Theme.CREAM, "CREAM should be defined");
        assertNotNull(Theme.GOLD_MUTED, "GOLD_MUTED should be defined");
        assertNotNull(Theme.BORDER_GOLD, "BORDER_GOLD should be defined");

        if (javaFxStarted) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    Button btn = new Button("Login");
                    Theme.stylePrimaryButton(btn);
                    assertNotNull(btn.getBackground());

                    Button secBtn = new Button("Cancel");
                    Theme.styleSecondaryButton(secBtn);
                    assertNotNull(secBtn.getBackground());

                    Button dangerBtn = new Button("Delete");
                    Theme.styleDangerButton(dangerBtn);
                    assertNotNull(dangerBtn.getBackground());

                    Button goldBtn = new Button("Catalog");
                    Theme.styleGoldButton(goldBtn);
                    assertNotNull(goldBtn.getBackground());

                    TextField tf = new TextField();
                    Theme.styleTextField(tf);
                    assertNotNull(tf.getBackground());

                    PasswordField pf = new PasswordField();
                    Theme.stylePasswordField(pf);
                    assertNotNull(pf.getBackground());

                    javafx.scene.Node logo = Theme.createLogoMark(40);
                    assertNotNull(logo);

                    javafx.scene.Node badge = Theme.createStatusBadge("AVAILABLE", "Available");
                    assertNotNull(badge);

                    javafx.scene.Node emptyState = Theme.createEmptyStateNode("No Books", "Try searching again");
                    assertNotNull(emptyState);

                    javafx.scene.Node separator = Theme.createOrnamentalSeparator();
                    assertNotNull(separator);

                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION, "Test message", javafx.scene.control.ButtonType.OK
                    );
                    Theme.styleDialog(alert);
                    assertNotNull(alert.getDialogPane().getBackground());
                } finally {
                    latch.countDown();
                }
            });
            assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    @DisplayName("Verify Real Backend Authentication (Valid Credentials)")
    void testRealAuthenticationSuccess() throws Exception {
        AuthService authService = new AuthService();
        var future = authService.authenticate("demo", "demo123");
        var result = future.get(10, TimeUnit.SECONDS);

        assertNotNull(result, "AuthResult should not be null");
        assertTrue(result.isSuccess(), "Authentication with valid demo:demo123 should succeed: " + result.getMessage());
        assertEquals("Basic " + java.util.Base64.getEncoder().encodeToString("demo:demo123".getBytes()), authService.getAuthHeader());
        assertEquals("demo", authService.getCurrentUsername());
        assertEquals("demo123", authService.getCurrentPassword());
        assertTrue(authService.hasCredentials());
    }

    @Test
    @DisplayName("Verify Real Backend Authentication (Invalid Credentials)")
    void testRealAuthenticationFailure() throws Exception {
        AuthService authService = new AuthService();
        var future = authService.authenticate("wronguser_xyz", "badpass123");
        var result = future.get(10, TimeUnit.SECONDS);

        assertNotNull(result, "AuthResult should not be null");
        assertFalse(result.isSuccess(), "Authentication with invalid credentials should fail");
        assertEquals("Invalid username or password", result.getMessage());
    }

    @Test
    @DisplayName("Verify ApiService Queries with Real Backend Dev Endpoints")
    void testApiServiceQueries() throws Exception {
        ApiClient apiClient = new ApiClient();
        apiClient.setCredentials("demo", "demo123");
        ApiService apiService = new ApiService(apiClient);

        // Books
        var books = apiService.getBooks(0, 10, "id", "asc").get(10, TimeUnit.SECONDS);
        assertNotNull(books);
        assertNotNull(books.getContent());

        // Authors
        var authors = apiService.getAuthors(0, 10, "id", "asc").get(10, TimeUnit.SECONDS);
        assertNotNull(authors);
        assertNotNull(authors.getContent());

        // Categories
        var categories = apiService.getCategories(0, 10, "id", "asc").get(10, TimeUnit.SECONDS);
        assertNotNull(categories);
        assertNotNull(categories.getContent());

        // Users
        var users = apiService.getUsers(0, 10, "id", "asc").get(10, TimeUnit.SECONDS);
        assertNotNull(users);
        assertNotNull(users.getContent());

        // Borrows
        var borrows = apiService.getBorrows(0, 10, "id", "asc").get(10, TimeUnit.SECONDS);
        assertNotNull(borrows);
        assertNotNull(borrows.getContent());

        // Returns
        var returns = apiService.getReturns(0, 10, "id", "asc").get(10, TimeUnit.SECONDS);
        assertNotNull(returns);
        assertNotNull(returns.getContent());

        // Fines
        var fines = apiService.getFines(0, 10, "id", "asc").get(10, TimeUnit.SECONDS);
        assertNotNull(fines);
        assertNotNull(fines.getContent());

        // Verify username retention
        assertEquals("demo", apiClient.getUsername());
        assertEquals("demo", apiService.getCurrentUsername());
    }
}
