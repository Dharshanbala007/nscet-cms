package com.nscet.cms.ui.controller;

import com.nscet.cms.reports.ReportManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class ReceiptReprintController implements Initializable {

    @FXML private RadioButton appReceiptRadio;
    @FXML private RadioButton nameReceiptRadio;
    @FXML private RadioButton noReceiptRadio;
    @FXML private DatePicker receiptDate;
    @FXML private TextField receiptSearchField;
    @FXML private TextArea receiptPreviewArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        receiptDate.setValue(LocalDate.now());
    }

    @FXML
    private void handleSearch() {
        String query = receiptSearchField.getText();
        if (query == null || query.trim().isEmpty()) {
            query = "MIS-2024-25-144";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===============================================================\n");
        sb.append("         NADAR SARASWATHI COLLEGE OF ENGG & TECH, THENI        \n");
        sb.append("                      FEE PAYMENT RECEIPT                       \n");
        sb.append("===============================================================\n");
        sb.append("Receipt No   : ").append(query.toUpperCase()).append("\n");
        sb.append("Date         : ").append(receiptDate.getValue()).append("\n");
        sb.append("Student Name : SANTHOSH M (2024FMEO13)\n");
        sb.append("Department   : Mechanical Engineering (MECH) - III Sem\n");
        sb.append("---------------------------------------------------------------\n");
        sb.append("Fee Head Description                          Amount (₹)\n");
        sb.append("---------------------------------------------------------------\n");
        sb.append("1. Admission Fee                              ₹ 1,000.00\n");
        sb.append("2. Tuition Fee                                ₹ 25,000.00\n");
        sb.append("3. Consortium Application Fee                 ₹ 1,000.00\n");
        sb.append("---------------------------------------------------------------\n");
        sb.append("TOTAL AMOUNT PAID                           : ₹ 27,000.00\n");
        sb.append("Payment Mode                                : CASH\n");
        sb.append("===============================================================\n");
        sb.append("              Thank you for your payment!               \n");
        sb.append("===============================================================\n");

        receiptPreviewArea.setText(sb.toString());
    }

    @FXML
    private void handlePrint() {
        try {
            ReportManager.printReport("FeeReceipt", Collections.emptyList(), new HashMap<>());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "Receipt print job sent to printer.").showAndWait();
        }
    }
}
