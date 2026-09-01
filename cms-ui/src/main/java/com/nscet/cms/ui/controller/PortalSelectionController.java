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

    private String targetPortal; // "ADMIN", "ACCOUNTS", or "PAYROLL"

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (bgImage != null) {
            try {
                Image img = new Image(getClass().getResourceAsStream("/images/nscet.png"));
                bgImage.setImage(img);
            } catch (Exception e) {
                System.out.println("nscet.png not found: " + e.getMessage());
            }
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

    @FXML
    private void handlePayroll() {
        promptPortalLogin("PAYROLL", "payroll");
    }

    private void promptPortalLogin(String portal, String defaultUsername) {
        this.targetPortal = portal;
        if (verifyTitleLabel != null) verifyTitleLabel.setText(portal + " Portal Login");
        if (verifyUsernameField != null) verifyUsernameField.setText(defaultUsername);
        if (verifyPasswordField != null) verifyPasswordField.clear();
        if (verifyErrorLabel != null) verifyErrorLabel.setVisible(false);

        if (verifyOverlay != null) {
            verifyOverlay.setVisible(true);
            verifyOverlay.setManaged(true);
        }
        if (verifyPasswordField != null) verifyPasswordField.requestFocus();
    }

    @FXML
    private void handleVerifyConfirm() {
        String username = (verifyUsernameField != null && verifyUsernameField.getText() != null) 
                ? verifyUsernameField.getText().trim() : "user";

        if (verifyOverlay != null) {
            verifyOverlay.setVisible(false);
            verifyOverlay.setManaged(false);
        }

        System.out.println("[PortalSelection] Logging into target portal: " + targetPortal + " as user: " + username);

        if ("ADMIN".equalsIgnoreCase(targetPortal)) {
            NavigationManager.openMainShell();
        } else if ("PAYROLL".equalsIgnoreCase(targetPortal)) {
            NavigationManager.openPayrollShell();
        } else {
            // Default to Accounts portal for ACCOUNTS or any fallback
            NavigationManager.openAccountsShell();
        }
    }

    @FXML
    private void handleVerifyCancel() {
        if (verifyOverlay != null) {
            verifyOverlay.setVisible(false);
            verifyOverlay.setManaged(false);
        }
        if (verifyUsernameField != null) verifyUsernameField.clear();
        if (verifyPasswordField != null) verifyPasswordField.clear();
        if (verifyErrorLabel != null) verifyErrorLabel.setVisible(false);
    }
}
