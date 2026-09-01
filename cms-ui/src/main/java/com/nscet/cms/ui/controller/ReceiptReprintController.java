package com.nscet.cms.ui.controller;

import com.nscet.cms.db.repository.FeeReceiptRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class ReceiptReprintController implements Initializable {

    @FXML private RadioButton appReceiptRadio;
    @FXML private RadioButton nameReceiptRadio;
    @FXML private RadioButton noReceiptRadio;
    @FXML private ToggleGroup reprintGroup;

    @FXML private DatePicker receiptDate;
    @FXML private ComboBox<String> recTypeCombo;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> periodCombo;
    @FXML private TextField noField;

    @Autowired private FeeReceiptRepository feeReceiptRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (receiptDate != null) receiptDate.setValue(LocalDate.now());

        if (recTypeCombo != null) {
            recTypeCombo.getItems().clear();
            recTypeCombo.getItems().addAll("Select", "General Receipt", "Term Receipt", "Exam Receipt", "Bus Fee Receipt");
            recTypeCombo.getSelectionModel().selectFirst();
        }

        if (periodCombo != null) {
            periodCombo.getItems().clear();
            periodCombo.getItems().addAll("Select", "2023-24", "2024-25", "2025-26");
            periodCombo.getSelectionModel().select("Select");
        }

        // Interactivity: toggle field focus/enable states based on radio selection
        if (reprintGroup != null) {
            reprintGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> {
                if (noReceiptRadio != null && noReceiptRadio.isSelected()) {
                    if (noField != null) noField.requestFocus();
                } else if (nameReceiptRadio != null && nameReceiptRadio.isSelected()) {
                    if (nameField != null) nameField.requestFocus();
                }
            });
        }
    }

    @FXML
    private void handlePrint() {
        String num = noField != null ? noField.getText().trim() : "";
        String name = nameField != null ? nameField.getText().trim() : "";
        String recType = recTypeCombo != null ? recTypeCombo.getValue() : "Select";
        String period = periodCombo != null ? periodCombo.getValue() : "Select";

        String target = !num.isEmpty() ? "Receipt No: " + num : (!name.isEmpty() ? "Name: " + name : "Receipt");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Receipt");
        alert.setHeaderText(null);
        alert.setContentText("Sending " + target + " (Type: " + recType + ", Period: " + period + ") to printer.");
        alert.showAndWait();
    }

    @FXML
    private void handleClose() {
        if (noField != null && noField.getScene() != null && noField.getScene().getWindow() != null) {
            noField.getScene().getWindow().hide();
        }
    }
}
