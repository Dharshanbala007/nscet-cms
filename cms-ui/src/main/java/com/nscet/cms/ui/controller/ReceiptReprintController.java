package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.entity.FeeReceiptItem;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.FeeReceiptRepository;
import com.nscet.cms.reports.ReportManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Autowired private FeeReceiptRepository feeReceiptRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        receiptDate.setValue(LocalDate.now());
    }

    @FXML
    private void handleSearch() {
        String query = receiptSearchField.getText();
        if (query == null || query.trim().isEmpty()) {
            showAlert("Search Error", "Please enter a Receipt Number or Roll Number.", Alert.AlertType.WARNING);
            return;
        }

        try {
            String trimmed = query.trim();
            Optional<FeeReceipt> receiptOpt = feeReceiptRepository.findByReceiptNumber(trimmed);
            if (receiptOpt.isEmpty()) {
                List<FeeReceipt> byRoll = feeReceiptRepository.findByStudentRollNumber(trimmed);
                if (byRoll != null && !byRoll.isEmpty()) {
                    receiptOpt = Optional.of(byRoll.get(0));
                }
            }

            if (receiptOpt.isEmpty()) {
                showAlert("Not Found", "No receipt found for: " + trimmed, Alert.AlertType.INFORMATION);
                return;
            }

            FeeReceipt fr = receiptOpt.get();
            StudentMaster s = fr.getStudent();
            String studentName = s != null ? s.getName() : "N/A";
            String rollNo = s != null ? s.getRollNumber() : "N/A";
            String regNo = s != null && s.getRegistrationNo() != null ? s.getRegistrationNo() : "N/A";
            String dept = s != null ? extractDept(s.getRollNumber()) : "N/A";
            String payMode = fr.getPaymentMode() != null ? fr.getPaymentMode() : "CASH";
            String dateStr = fr.getReceiptDate() != null
                    ? fr.getReceiptDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";

            StringBuilder sb = new StringBuilder();
            sb.append("===============================================================\n");
            sb.append("         NADAR SARASWATHI COLLEGE OF ENGG & TECH, THENI        \n");
            sb.append("                      FEE PAYMENT RECEIPT                       \n");
            sb.append("===============================================================\n");
            sb.append("Receipt No   : ").append(fr.getReceiptNumber()).append("\n");
            sb.append("Date         : ").append(dateStr).append("\n");
            sb.append("Student Name : ").append(studentName).append(" (").append(rollNo).append(")\n");
            sb.append("Reg No       : ").append(regNo).append("\n");
            sb.append("Department   : ").append(dept).append("\n");
            sb.append("---------------------------------------------------------------\n");
            sb.append("Fee Head Description                          Amount (\u20B9)\n");
            sb.append("---------------------------------------------------------------\n");

            if (fr.getItems() != null && !fr.getItems().isEmpty()) {
                int i = 1;
                for (FeeReceiptItem item : fr.getItems()) {
                    String feeName = item.getFeesName() != null ? item.getFeesName().getName() : "Fee";
                    String amt = item.getAmount() != null ? item.getAmount().toPlainString() : "0";
                    sb.append(i).append(". ").append(feeName);
                    int pad = 50 - feeName.length() - String.valueOf(i).length() - 2;
                    for (int p = 0; p < Math.max(pad, 2); p++) sb.append(" ");
                    sb.append("\u20B9 ").append(amt).append("\n");
                    i++;
                }
            } else {
                sb.append("1. ").append("Tuition Fee");
                int pad = 48;
                for (int p = 0; p < pad; p++) sb.append(" ");
                sb.append("\u20B9 ").append(fr.getTotalAmount() != null ? fr.getTotalAmount().toPlainString() : "0").append("\n");
            }

            sb.append("---------------------------------------------------------------\n");
            String total = fr.getTotalAmount() != null ? fr.getTotalAmount().toPlainString() : "0";
            sb.append("TOTAL AMOUNT PAID                           : \u20B9 ").append(total).append("\n");
            sb.append("Payment Mode                                : ").append(payMode).append("\n");
            sb.append("===============================================================\n");
            sb.append("              Thank you for your payment!               \n");
            sb.append("===============================================================\n");

            receiptPreviewArea.setText(sb.toString());
        } catch (Exception e) {
            showAlert("Error", "Error searching receipt: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String extractDept(String rollNumber) {
        if (rollNumber == null) return "N/A";
        if (rollNumber.contains("CSE")) return "Computer Science";
        if (rollNumber.contains("ECE")) return "Electronics";
        if (rollNumber.contains("MECH")) return "Mechanical";
        if (rollNumber.contains("EEE")) return "EEE";
        if (rollNumber.contains("IT")) return "Information Technology";
        if (rollNumber.contains("AI")) return "AI & DS";
        if (rollNumber.contains("CE")) return "Civil";
        return "N/A";
    }

    @FXML
    private void handlePrint() {
        try {
            ReportManager.printReport("FeeReceipt", Collections.emptyList(), new HashMap<>());
            new Alert(Alert.AlertType.INFORMATION, "Receipt print job sent to printer.").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Print failed: " + e.getMessage()).showAndWait();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
