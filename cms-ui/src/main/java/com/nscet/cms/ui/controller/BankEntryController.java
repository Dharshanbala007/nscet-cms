package com.nscet.cms.ui.controller;

import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class BankEntryController implements Initializable {

    @FXML private ComboBox<String> accountNoCombo;
    @FXML private DatePicker transactionDatePicker;
    @FXML private TextField bankField;
    @FXML private TextField preBalanceField;
    @FXML private ComboBox<String> modeOfPayCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField chequeNoField;
    @FXML private DatePicker collectionDatePicker;
    @FXML private TextField amountField;
    @FXML private TextField balanceField;
    @FXML private TextField accountField;
    @FXML private TextArea remarksArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFormDefaults();
    }

    private void setupFormDefaults() {
        if (transactionDatePicker != null) transactionDatePicker.setValue(LocalDate.now());
        if (collectionDatePicker != null) collectionDatePicker.setValue(LocalDate.now());

        if (accountNoCombo != null) {
            accountNoCombo.setItems(FXCollections.observableArrayList(
                    "14620100026821", "14620100015930", "14620100037145", "04820100008421"
            ));
            accountNoCombo.getSelectionModel().selectFirst();
        }

        if (modeOfPayCombo != null) {
            modeOfPayCombo.setItems(FXCollections.observableArrayList(
                    "By Cash", "By Cheque", "By TC", "To Cheque", "To Cash"
            ));
            modeOfPayCombo.getSelectionModel().selectFirst();
        }

        if (typeCombo != null) {
            typeCombo.setItems(FXCollections.observableArrayList("Dr", "Cr"));
            typeCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleAdd() {
        clearForm();
    }

    @FXML
    private void handleModify() {
        showAlert("Information", "Selected bank entry loaded for modification.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDelete() {
        showAlert("Information", "Bank entry removed.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleSave() {
        try {
            String acc = accountNoCombo != null ? accountNoCombo.getValue() : "";
            String mode = modeOfPayCombo != null ? modeOfPayCombo.getValue() : "";
            String amtStr = amountField != null && amountField.getText() != null ? amountField.getText().trim().replaceAll("[^0-9.]", "") : "";

            if (!amtStr.isEmpty()) {
                BigDecimal amt = new BigDecimal(amtStr);
                String preStr = preBalanceField != null && preBalanceField.getText() != null ? preBalanceField.getText().trim().replaceAll("[^0-9.]", "") : "0";
                BigDecimal pre = preStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(preStr);
                BigDecimal bal = (typeCombo != null && "Dr".equals(typeCombo.getValue())) ? pre.subtract(amt) : pre.add(amt);
                if (balanceField != null) balanceField.setText(bal.toPlainString());
            }

            showAlert("Success", "Bank Entry saved successfully for Account: " + acc + " (" + mode + ")", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Failed to save bank entry: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
    }

    @FXML
    private void handleClose() {
        if (NavigationManager.getActiveContentArea() != null) {
            NavigationManager.loadModule("dashboard", NavigationManager.getActiveContentArea());
        }
    }

    private void clearForm() {
        if (chequeNoField != null) chequeNoField.clear();
        if (amountField != null) amountField.clear();
        if (balanceField != null) balanceField.clear();
        if (accountField != null) accountField.clear();
        if (remarksArea != null) remarksArea.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
