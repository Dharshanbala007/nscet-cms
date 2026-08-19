package com.nscet.cms.ui.controller;

import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class PortalSelectionController implements Initializable {

    @FXML private ImageView bgImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/nscet.png"));
            bgImage.setImage(img);
        } catch (Exception e) {
            System.out.println("nscet.png not found: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdmin() {
        System.out.println("[PortalSelection] Admin clicked, opening main shell...");
        try {
            NavigationManager.openMainShell();
        } catch (Exception e) {
            System.err.println("[PortalSelection] Failed to open main shell");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccounts() {
        System.out.println("[PortalSelection] Accounts clicked, opening accounts shell...");
        try {
            NavigationManager.openAccountsShell();
        } catch (Exception e) {
            System.err.println("[PortalSelection] Failed to open accounts shell");
            e.printStackTrace();
        }
    }
}
