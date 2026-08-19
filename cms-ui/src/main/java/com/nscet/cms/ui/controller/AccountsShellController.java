package com.nscet.cms.ui.controller;

import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class AccountsShellController implements Initializable {

    @FXML private StackPane contentArea;
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
    private void handleBankMaster() {
        NavigationManager.loadModule("bank", contentArea);
    }

    @FXML
    private void handleDepartmentMaster() {
        NavigationManager.loadModule("department", contentArea);
    }

    @FXML
    private void handleDesignationMaster() {
        NavigationManager.loadModule("designation", contentArea);
    }

    @FXML
    private void handleStaffMaster() {
        NavigationManager.loadModule("staff", contentArea);
    }

    @FXML
    private void handlePendingBills() {
        NavigationManager.loadModule("pendingFees", contentArea);
    }

    @FXML
    private void handleDailyTransaction() {
        NavigationManager.loadModule("dailyTransaction", contentArea);
    }

    @FXML
    private void handlePettyCash() {
        NavigationManager.loadModule("pettyCash", contentArea);
    }

    @FXML
    private void handlePettyCashSuspense() {
        NavigationManager.loadModule("pettyCashSuspense", contentArea);
    }

    @FXML
    private void handlePettyVoucher() {
        NavigationManager.loadModule("pettyVoucher", contentArea);
    }

    @FXML
    private void handleFunctionExpense() {
        NavigationManager.loadModule("functionExpense", contentArea);
    }

    @FXML
    private void handleDayBookPettyCash() {
        NavigationManager.loadModule("pettyCashDaybook", contentArea);
    }

    @FXML
    private void handleDayBook() {
        NavigationManager.loadModule("daySettlement", contentArea);
    }

    @FXML
    private void handleCashBook() {
        NavigationManager.loadPlaceholder("Cash Book", contentArea);
    }

    @FXML
    private void handleDaybookReport() {
        NavigationManager.loadPlaceholder("Daybook Report", contentArea);
    }

    @FXML
    private void handleBankBook() {
        NavigationManager.loadPlaceholder("Bank Book", contentArea);
    }

    @FXML
    private void handleExpenditureSummary() {
        NavigationManager.loadPlaceholder("Expenditure Summary", contentArea);
    }

    @FXML
    private void handleTrialBalance() {
        NavigationManager.loadPlaceholder("Trial Balance", contentArea);
    }

    @FXML
    private void handleIncomeExpenditure() {
        NavigationManager.loadPlaceholder("Income and Expenditure", contentArea);
    }

    @FXML
    private void handleProfitLoss() {
        NavigationManager.loadPlaceholder("Profit Loss Ac", contentArea);
    }

    @FXML
    private void handleBalanceSheet() {
        NavigationManager.loadPlaceholder("Balance Sheet", contentArea);
    }

    @FXML
    private void handlePettyCashSuspenseReport() {
        NavigationManager.loadPlaceholder("Petty Cash Suspense Report", contentArea);
    }

    @FXML
    private void handlePendingBillDetails() {
        NavigationManager.loadPlaceholder("Pending Bill Details", contentArea);
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
