package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.PettyCashService;
import com.nscet.cms.db.entity.PettyCash;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class PettyCashController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField balanceField;
    @FXML private TextField amountField;

    @Autowired
    private PettyCashService pettyCashService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        typeCombo.setItems(FXCollections.observableArrayList("Cash", "Voucher"));
        typeCombo.getSelectionModel().selectFirst();

        loadBalance();
    }

    private void loadBalance() {
        BigDecimal totalCash = pettyCashService.getTotalCashBalance();
        balanceField.setText("Rs. " + String.format("%.2f", totalCash != null ? totalCash : BigDecimal.ZERO));
    }

    @FXML
    private void handleSave() {
        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a date");
            return;
        }
        if (typeCombo.getSelectionModel().getSelectedItem() == null
                || "Select".equals(typeCombo.getSelectionModel().getSelectedItem())) {
            showAlert(Alert.AlertType.WARNING, "Please select a type");
            return;
        }
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter an amount");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid amount");
            return;
        }

        PettyCash entity = new PettyCash();
        entity.setVoucherNo(pettyCashService.generateNextVoucherNo());
        entity.setVoucherDate(datePicker.getValue());
        entity.setTransactionType(typeCombo.getSelectionModel().getSelectedItem());
        entity.setAmount(amount);
        entity.setIsActive(true);

        pettyCashService.create(entity);
        showAlert(Alert.AlertType.INFORMATION, "Petty Cash entry saved successfully");
        handleClear();
    }

    private void handleClear() {
        datePicker.setValue(LocalDate.now());
        typeCombo.getSelectionModel().selectFirst();
        amountField.clear();
        loadBalance();
    }

    @FXML
    private void handleClose() {
        StackPane contentArea = com.nscet.cms.ui.navigation.NavigationManager.getActiveContentArea();
        if (contentArea != null) {
            Label placeholder = new Label("Select a module from the menu");
            placeholder.getStyleClass().add("placeholder-label");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(placeholder);
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Petty Cash");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
