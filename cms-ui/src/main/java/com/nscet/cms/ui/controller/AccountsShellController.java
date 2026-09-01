package com.nscet.cms.ui.controller;

import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class AccountsShellController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private StackPane contentArea;
    @FXML private ImageView bgImage;
    @FXML private Label academicYearLabel;
    @FXML private Label userNameLabel;
    @FXML private Label footerInfoLabel;

    @FXML private ToggleButton mastersToggle;
    @FXML private ToggleButton transactionsToggle;
    @FXML private ToggleButton reportsToggle;

    @FXML private VBox mastersMenu;
    @FXML private VBox transactionsMenu;
    @FXML private VBox reportsMenu;

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

        try {
            if (academicYearLabel != null) academicYearLabel.setText("2025-26");
            if (userNameLabel != null) userNameLabel.setText("Accounts Administrator");
            if (footerInfoLabel != null) footerInfoLabel.setText("Logged User: ACCOUNTS ADMINISTRATOR | Username: accounts | Role: ADMIN | Academic Year: 2025-26 | Nadar Saraswathi College of Engineering and Technology, Theni - Accounts Portal");
        } catch (Exception ignored) {}

        setupNavigation();
        NavigationManager.loadModule("dailyTransaction", contentArea);
    }

    private void setupNavigation() {
        // Masters
        if (mastersMenu != null) {
            mastersMenu.getChildren().clear();
            addMenuItem(mastersMenu, "Account Group", "accountGroup");
            addMenuItem(mastersMenu, "Account Master", "accountMaster");
            addMenuItem(mastersMenu, "Function Master", "functionMaster");
        }

        // Transactions
        if (transactionsMenu != null) {
            transactionsMenu.getChildren().clear();
            addMenuItem(transactionsMenu, "Pending Bills", "pendingBillDetails");
            addMenuItem(transactionsMenu, "Daily Transaction", "dailyTransaction");
            addMenuItem(transactionsMenu, "Petty Cash", "pettyCash");
            addMenuItem(transactionsMenu, "Petty Cash (Suspense)", "pettyCashSuspense");
            addMenuItem(transactionsMenu, "Petty Voucher", "pettyVoucher");
            addMenuItem(transactionsMenu, "Function Expense", "functionExpense");
        }

        // Reports
        if (reportsMenu != null) {
            reportsMenu.getChildren().clear();
            addMenuItem(reportsMenu, "Day Book Petty Cash", "pettyCashDaybook");
        }
    }

    private Button lastActiveButton = null;

    private void addMenuItem(VBox menu, String label, String module) {
        Button button = new Button(label);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            if (lastActiveButton != null) {
                lastActiveButton.getStyleClass().remove("active");
            }
            button.getStyleClass().add("active");
            lastActiveButton = button;
            NavigationManager.loadModule(module, contentArea);
        });
        menu.getChildren().add(button);
    }

    @FXML
    private void toggleMasters() {
        if (mastersMenu != null) {
            mastersMenu.setVisible(!mastersMenu.isVisible());
            mastersMenu.setManaged(mastersMenu.isVisible());
        }
    }

    @FXML
    private void toggleTransactions() {
        if (transactionsMenu != null) {
            transactionsMenu.setVisible(!transactionsMenu.isVisible());
            transactionsMenu.setManaged(transactionsMenu.isVisible());
        }
    }

    @FXML
    private void toggleReports() {
        if (reportsMenu != null) {
            reportsMenu.setVisible(!reportsMenu.isVisible());
            reportsMenu.setManaged(reportsMenu.isVisible());
        }
    }

    @FXML
    private void handleDashboard() {
        NavigationManager.loadModule("dailyTransaction", contentArea);
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Logout");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to log out of Accounts?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NavigationManager.openPortalSelection();
        }
    }
}
