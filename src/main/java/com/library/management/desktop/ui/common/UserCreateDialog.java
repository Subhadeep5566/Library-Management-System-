package com.library.management.desktop.ui.common;

import com.library.management.desktop.dto.UserResponse;
import com.library.management.desktop.dto.UserCreateRequest;
import com.library.management.desktop.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class UserCreateDialog extends Dialog<Boolean> {

    private final ApiService apiService;
    private TextField usernameField;
    private TextField emailField;
    private TextField firstNameField;
    private TextField lastNameField;
    private PasswordField passwordField;
    private TextField phoneField;
    private TextArea addressArea;
    private ComboBox<String> roleCombo;
    private ComboBox<String> statusCombo;

    private final ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    public UserCreateDialog(ApiService apiService) {
        this.apiService = apiService;
        setTitle("Add User");
        setHeaderText("Create New User");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        getDialogPane().setPrefWidth(500);

        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        createForm();

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateAndSave()) {
                event.consume();
            }
        });
        com.library.management.desktop.theme.Theme.styleDialog(this);
    }

    private void createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER_LEFT);

        // Username
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        grid.add(new Label("Username *:"), 0, 0);
        grid.add(usernameField, 1, 0);

        // Email
        emailField = new TextField();
        emailField.setPromptText("Enter email");
        grid.add(new Label("Email *:"), 0, 1);
        grid.add(emailField, 1, 1);

        // First Name
        firstNameField = new TextField();
        firstNameField.setPromptText("Enter first name");
        grid.add(new Label("First Name *:"), 0, 2);
        grid.add(firstNameField, 1, 2);

        // Last Name
        lastNameField = new TextField();
        lastNameField.setPromptText("Enter last name");
        grid.add(new Label("Last Name *:"), 0, 3);
        grid.add(lastNameField, 1, 3);

        // Password
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        grid.add(new Label("Password *:"), 0, 4);
        grid.add(passwordField, 1, 4);

        // Phone
        phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");
        grid.add(new Label("Phone:"), 0, 5);
        grid.add(phoneField, 1, 5);

        // Role
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("MEMBER", "LIBRARIAN", "ADMIN");
        roleCombo.setValue("MEMBER");
        grid.add(new Label("Role *:"), 0, 6);
        grid.add(roleCombo, 1, 6);

        // Status
        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("ACTIVE", "INACTIVE", "SUSPENDED");
        statusCombo.setValue("ACTIVE");
        grid.add(new Label("Status *:"), 0, 7);
        grid.add(statusCombo, 1, 7);

        // Address
        addressArea = new TextArea();
        addressArea.setPromptText("Enter address");
        addressArea.setPrefRowCount(3);
        addressArea.setWrapText(true);
        grid.add(new Label("Address:"), 0, 8);
        grid.add(addressArea, 1, 8);

        getDialogPane().setContent(new ScrollPane(grid));
    }

    private boolean validateAndSave() {
        if (usernameField.getText().trim().isEmpty()) {
            showError("Username is required");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showError("Email is required");
            return false;
        }
        if (!emailField.getText().trim().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            showError("Invalid email format");
            return false;
        }
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First name is required");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last name is required");
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            showError("Password is required");
            return false;
        }
        if (passwordField.getText().length() < 8) {
            showError("Password must be at least 8 characters");
            return false;
        }
        if (roleCombo.getValue() == null) {
            showError("Role is required");
            return false;
        }
        if (statusCombo.getValue() == null) {
            showError("Status is required");
            return false;
        }

        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(usernameField.getText().trim());
        request.setEmail(emailField.getText().trim());
        request.setFirstName(firstNameField.getText().trim());
        request.setLastName(lastNameField.getText().trim());
        request.setPassword(passwordField.getText());
        request.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
        request.setAddress(addressArea.getText().trim().isEmpty() ? null : addressArea.getText().trim());

        String selectedRole = roleCombo.getValue();
        String selectedStatus = statusCombo.getValue();

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        apiService.createUser(request)
                .thenCompose(createdUser -> {
                    CompletableFuture<Void> followUp = CompletableFuture.completedFuture(null);
                    if (selectedRole != null && !"MEMBER".equals(selectedRole)) {
                        followUp = followUp.thenCompose(v -> apiService.updateUserRole(createdUser.getId(), selectedRole).thenApply(r -> null));
                    }
                    if (selectedStatus != null && !"ACTIVE".equals(selectedStatus)) {
                        followUp = followUp.thenCompose(v -> apiService.updateUserStatus(createdUser.getId(), selectedStatus).thenApply(r -> null));
                    }
                    return followUp;
                })
                .thenAccept(v -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setResult(true);
                    close();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveButton.setDisable(false);
                        String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        showError("Failed to create user: " + errorMsg);
                    });
                    return null;
                });

        return false;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Validation Error");
        com.library.management.desktop.theme.Theme.styleDialog(alert);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        com.library.management.desktop.theme.Theme.styleDialog(alert);
        alert.showAndWait();
    }
}