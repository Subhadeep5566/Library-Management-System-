package com.library.management.desktop.ui.common;

import com.library.management.desktop.dto.UserResponse;
import com.library.management.desktop.dto.UserUpdateRequest;
import com.library.management.desktop.service.ApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

import java.util.concurrent.CompletableFuture;

public class UserEditDialog extends Dialog<Boolean> {

    private final ApiService apiService;
    private final UserResponse user;

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

    public UserEditDialog(ApiService apiService, UserResponse user) {
        this.apiService = apiService;
        this.user = user;
        setTitle("Edit User");
        setHeaderText("Edit User: " + user.getUsername());
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        getDialogPane().setPrefWidth(500);

        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        createForm();
        populateFields();

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

        // Username (read-only)
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setEditable(false);
        usernameField.setStyle("-fx-background-color: #f5f5f5;");
        grid.add(new Label("Username:"), 0, 0);
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

        // Password (optional for edit)
        passwordField = new PasswordField();
        passwordField.setPromptText("Leave blank to keep current password");
        grid.add(new Label("New Password:"), 0, 4);
        grid.add(passwordField, 1, 4);

        // Phone
        phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");
        grid.add(new Label("Phone:"), 0, 5);
        grid.add(phoneField, 1, 5);

        // Role
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("MEMBER", "LIBRARIAN", "ADMIN");
        grid.add(new Label("Role *:"), 0, 6);
        grid.add(roleCombo, 1, 6);

        // Status
        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("ACTIVE", "INACTIVE", "SUSPENDED", "EXPIRED");
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

    private void populateFields() {
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        roleCombo.setValue(user.getRole());
        statusCombo.setValue(user.getStatus());
        addressArea.setText(user.getAddress() != null ? user.getAddress() : "");
    }

    private boolean validateAndSave() {
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
        if (!passwordField.getText().isEmpty() && passwordField.getText().length() < 6) {
            showError("Password must be at least 6 characters");
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

        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail(emailField.getText().trim());
        request.setFirstName(firstNameField.getText().trim());
        request.setLastName(lastNameField.getText().trim());
        request.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
        request.setAddress(addressArea.getText().trim().isEmpty() ? null : addressArea.getText().trim());

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Update basic info
        CompletableFuture<Void> updateChain = apiService.updateUser(user.getId(), request)
                .thenCompose(response -> {
                    // Update status if changed
                    if (!user.getStatus().equals(statusCombo.getValue())) {
                        return apiService.updateUserStatus(user.getId(), statusCombo.getValue())
                                .thenApply(r -> null);
                    }
                    return CompletableFuture.completedFuture(null);
                })
                .thenCompose(v -> {
                    // Update role if changed
                    if (!user.getRole().equals(roleCombo.getValue())) {
                        return apiService.updateUserRole(user.getId(), roleCombo.getValue())
                                .thenApply(r -> null);
                    }
                    return CompletableFuture.completedFuture(null);
                });

        updateChain.thenAccept(v -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setResult(true);
                    close();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveButton.setDisable(false);
                        String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        showError("Failed to update user: " + errorMsg);
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