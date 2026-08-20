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

    @FXML private ToggleButton mastersToggle;
    @FXML private ToggleButton transactionsToggle;
    @FXML private ToggleButton reportsToggle;

    @FXML private VBox mastersMenu;
    @FXML private VBox transactionsMenu;
    @FXML private VBox reportsMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/nscet.png"));
            bgImage.setImage(img);
        } catch (Exception e) {
            System.out.println("nscet.png not found: " + e.getMessage());
        }

        try {
            academicYearLabel.setText("2025-26");
        } catch (Exception ignored) {}

        setupNavigation();
        NavigationManager.loadModule("dailyTransaction", contentArea);
    }

    private void setupNavigation() {
        // Masters
        mastersMenu.getChildren().clear();
        addMenuItem(mastersMenu, "Bank Master", "bank");
        addMenuItem(mastersMenu, "Department Master", "department");
        addMenuItem(mastersMenu, "Designation Master", "designation");
        addMenuItem(mastersMenu, "Staff Master", "staff");

        // Transactions
        transactionsMenu.getChildren().clear();
        addMenuItem(transactionsMenu, "Pending Bills", "pendingFees");
        addMenuItem(transactionsMenu, "Daily Transaction", "dailyTransaction");
        addMenuItem(transactionsMenu, "Petty Cash", "pettyCash");
        addMenuItem(transactionsMenu, "Petty Cash (Suspense)", "pettyCashSuspense");
        addMenuItem(transactionsMenu, "Petty Voucher", "pettyVoucher");
        addMenuItem(transactionsMenu, "Function Expense", "functionExpense");

        // Reports
        reportsMenu.getChildren().clear();
        addMenuItem(reportsMenu, "Day Book Petty Cash", "pettyCashDaybook");
    }

    private void addMenuHeader(VBox menu, String title) {
        Label label = new Label(title);
        label.setStyle("-fx-text-fill: #90caf9; -fx-font-weight: bold; -fx-padding: 6 0 2 10; -fx-font-size: 11px;");
        menu.getChildren().add(label);
    }

    private void addMenuItem(VBox menu, String label, String module) {
        Button button = new Button(label);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> NavigationManager.loadModule(module, contentArea));
        menu.getChildren().add(button);
    }

    @FXML
    private void toggleMasters() {
        mastersMenu.setVisible(!mastersMenu.isVisible());
        mastersMenu.setManaged(mastersMenu.isVisible());
    }

    @FXML
    private void toggleTransactions() {
        transactionsMenu.setVisible(!transactionsMenu.isVisible());
        transactionsMenu.setManaged(transactionsMenu.isVisible());
    }

    @FXML
    private void toggleReports() {
        reportsMenu.setVisible(!reportsMenu.isVisible());
        reportsMenu.setManaged(reportsMenu.isVisible());
    }

    @FXML
    private void handleDashboard() {
        NavigationManager.loadModule("dailyTransaction", contentArea);
    }

    @FXML
    private void handleFind() {
        NavigationManager.loadPlaceholder("Search / Find", contentArea);
    }

    @FXML
    private void handleExit() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Exit");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to exit the Accounts module?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NavigationManager.openPortalSelection();
        }
    }
}
