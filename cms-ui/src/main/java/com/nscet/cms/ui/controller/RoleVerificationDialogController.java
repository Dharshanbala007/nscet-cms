package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.AuthService;
import com.nscet.cms.core.session.UserSession;
import com.nscet.cms.db.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class RoleVerificationDialogController implements Initializable {

    @FXML private Label roleTitleLabel;
    @FXML private Label userLabel;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserSession userSession;

    private String targetRole;
    private boolean verified = false;
    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorLabel.setVisible(false);
    }

    public void setTargetRole(String role, Stage stage) {
        this.targetRole = role;
        this.dialogStage = stage;
        roleTitleLabel.setText("Re-verify Password for " + role.toUpperCase());

        User current = userSession.getCurrentUser();
        if (current != null) {
            userLabel.setText("User: " + current.getUsername() + " (" + current.getFullName() + ")");
        } else {
            userLabel.setText("User: admin");
        }
    }

    public boolean isVerified() {
        return verified;
    }

    @FXML
    private void handleConfirm() {
        String password = passwordField.getText();
        if (password == null || password.trim().isEmpty()) {
            showError("Please enter your password");
            return;
        }

        User current = userSession.getCurrentUser();
        String username = current != null ? current.getUsername() : "admin";

        try {
            authService.authenticate(username, password);
            verified = true;
            if (userSession.getCurrentUser() != null) {
                userSession.setPortalType(targetRole);
            }
            if (dialogStage != null) {
                dialogStage.close();
            }
        } catch (Exception e) {
            showError("Invalid password. Secondary authentication failed.");
        }
    }

    @FXML
    private void handleCancel() {
        verified = false;
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
