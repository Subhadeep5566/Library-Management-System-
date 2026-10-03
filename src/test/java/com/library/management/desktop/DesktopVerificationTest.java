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

                    TextField tf = new TextField();
                    Theme.styleTextField(tf);
                    assertNotNull(tf.getBackground());

                    PasswordField pf = new PasswordField();
                    Theme.stylePasswordField(pf);
                    assertNotNull(pf.getBackground());

                    javafx.scene.Node logo = Theme.createLogoMark(40);
                    assertNotNull(logo);
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
        var future = authService.authenticate("testuser", "password123");
        var result = future.get(10, TimeUnit.SECONDS);

        assertNotNull(result, "AuthResult should not be null");
        assertTrue(result.isSuccess(), "Authentication with valid testuser:password123 should succeed: " + result.getMessage());
        assertEquals("Basic " + java.util.Base64.getEncoder().encodeToString("testuser:password123".getBytes()), authService.getAuthHeader());
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
        apiClient.setCredentials("testuser", "password123");
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
    }
}
