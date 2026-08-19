package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.AuthService;
import com.nscet.cms.core.session.UserSession;
import com.nscet.cms.db.entity.User;
import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class PortalSelectionController implements Initializable {

    @FXML private ImageView bgImage;

    // Secondary Login Overlay FXML Elements
    @FXML private StackPane verifyOverlay;
    @FXML private Label verifyTitleLabel;
    @FXML private TextField verifyUsernameField;
    @FXML private PasswordField verifyPasswordField;
    @FXML private Label verifyErrorLabel;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserSession userSession;

    private String targetPortal; // "ADMIN" or "ACCOUNTS"

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/nscet.png"));
            bgImage.setImage(img);
        } catch (Exception e) {
            System.out.println("nscet.png not found: " + e.getMessage());
        }
        if (verifyOverlay != null) {
            verifyOverlay.setVisible(false);
            verifyOverlay.setManaged(false);
        }
    }

    @FXML
    private void handleAdmin() {
        promptPortalLogin("ADMIN", "admin");
    }

    @FXML
    private void handleAccounts() {
        promptPortalLogin("ACCOUNTS", "accounts");
    }

    private void promptPortalLogin(String portal, String defaultUsername) {
        this.targetPortal = portal;
        verifyTitleLabel.setText(portal + " Portal Login");
        verifyUsernameField.setText(defaultUsername);
        verifyPasswordField.clear();
        verifyErrorLabel.setVisible(false);

        verifyOverlay.setVisible(true);
        verifyOverlay.setManaged(true);
        verifyPasswordField.requestFocus();
    }

    @FXML
    private void handleVerifyConfirm() {
        String username = verifyUsernameField.getText() != null ? verifyUsernameField.getText().trim() : "";
        String password = verifyPasswordField.getText() != null ? verifyPasswordField.getText() : "";

        if (username.isEmpty() || password.isEmpty()) {
            verifyErrorLabel.setText("Please enter username and password");
            verifyErrorLabel.setVisible(true);
            return;
        }

        try {
            User authenticatedUser = authService.authenticate(username, password);
            userSession.login(authenticatedUser);
            userSession.setPortalType(targetPortal);

            verifyOverlay.setVisible(false);
            verifyOverlay.setManaged(false);

            if ("ADMIN".equalsIgnoreCase(targetPortal)) {
                System.out.println("[PortalSelection] Admin portal login successful for user: " + username);
                NavigationManager.openMainShell();
            } else if ("ACCOUNTS".equalsIgnoreCase(targetPortal)) {
                System.out.println("[PortalSelection] Accounts portal login successful for user: " + username);
                NavigationManager.openAccountsShell();
            }
        } catch (Exception e) {
            String msg = (e.getMessage() != null && !e.getMessage().trim().isEmpty()) 
                    ? e.getMessage() 
                    : "Invalid username or password for " + targetPortal + " portal.";
            verifyErrorLabel.setText(msg);
            verifyErrorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleVerifyCancel() {
        verifyOverlay.setVisible(false);
        verifyOverlay.setManaged(false);
        verifyUsernameField.clear();
        verifyPasswordField.clear();
        verifyErrorLabel.setVisible(false);
    }
}
