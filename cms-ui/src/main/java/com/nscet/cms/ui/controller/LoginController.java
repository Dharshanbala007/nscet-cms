package com.nscet.cms.ui.controller;

import com.nscet.cms.core.security.SecurityUtil;
import com.nscet.cms.core.service.AuditService;
import com.nscet.cms.core.service.AuthService;
import com.nscet.cms.core.session.UserSession;
import com.nscet.cms.db.entity.User;
import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ToggleButton togglePasswordBtn;
    @FXML private Label togglePasswordIcon;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private CheckBox rememberUsernameCheck;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserSession userSession;

    @Autowired(required = false)
    private AuditService auditService;

    private int failedAttempts = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressIndicator.setVisible(false);
        errorLabel.setVisible(false);
        togglePasswordBtn.setSelected(false);
        updatePasswordVisibility();
    }

    @FXML
    private void handleTogglePassword() {
        updatePasswordVisibility();
    }

    private void updatePasswordVisibility() {
        boolean show = togglePasswordBtn.isSelected();
        if (show) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordIcon.setText("\u25CB");
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            togglePasswordIcon.setText("\u25CF");
        }
        togglePasswordBtn.setSelected(show);
    }

    private String getPassword() {
        if (togglePasswordBtn.isSelected()) {
            return passwordTextField.getText();
        }
        return passwordField.getText();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        username = SecurityUtil.sanitize(username);
        progressIndicator.setVisible(true);
        loginButton.setDisable(true);

        try {
            User user = authService.authenticate(username, password);
            userSession.login(user);
            safeAuditLog(username, true, user.getId());
            NavigationManager.openPortalSelection();
        } catch (Exception e) {
            failedAttempts++;
            safeAuditLog(username, false, null);
            showError(e.getMessage());
            if (failedAttempts >= 5) {
                loginButton.setDisable(true);
                usernameField.setDisable(true);
                passwordField.setDisable(true);
                passwordTextField.setDisable(true);
                startLockoutCountdown();
            }
        } finally {
            progressIndicator.setVisible(false);
            loginButton.setDisable(false);
        }
    }

    private void safeAuditLog(String username, boolean success, Long userId) {
        try {
            if (auditService != null) {
                auditService.logLogin(username, success, userId);
            }
        } catch (Exception e) {
            System.err.println("[Login] Audit log failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void startLockoutCountdown() {
        new Thread(() -> {
            try {
                for (int i = 30; i > 0; i--) {
                    final int seconds = i;
                    javafx.application.Platform.runLater(() ->
                        errorLabel.setText("Account locked. Try again in " + seconds + " seconds"));
                    Thread.sleep(1000);
                }
                javafx.application.Platform.runLater(() -> {
                    failedAttempts = 0;
                    loginButton.setDisable(false);
                    usernameField.setDisable(false);
                    passwordField.setDisable(false);
                    passwordTextField.setDisable(false);
                    errorLabel.setVisible(false);
                    passwordField.clear();
                    passwordTextField.clear();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
