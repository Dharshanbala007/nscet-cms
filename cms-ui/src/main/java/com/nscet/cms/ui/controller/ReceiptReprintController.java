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

import java.math.BigDecimal;
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

            currentFeeReceipt = fr;
            receiptPreviewArea.setText(sb.toString());
        } catch (Exception e) {
            showAlert("Error", "Error searching receipt: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private FeeReceipt currentFeeReceipt;

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
        if (currentFeeReceipt == null) {
            new Alert(Alert.AlertType.WARNING, "Search and load a receipt first before printing.").showAndWait();
            return;
        }

        try {
            StudentMaster s = currentFeeReceipt.getStudent();
            Map<String, Object> params = new HashMap<>();
            params.put("COLLEGE_NAME", "Nadar Saraswathi College of Engineering and Technology");
            params.put("COLLEGE_LOCATION", "Vadapudupatti, Annanji (P.O), Theni - 625 531");
            params.put("ACADEMIC_YEAR", "2025-26");
            params.put("RECEIPT_NO", currentFeeReceipt.getReceiptNumber());
            params.put("RECEIPT_DATE", currentFeeReceipt.getReceiptDate() != null
                    ? currentFeeReceipt.getReceiptDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            params.put("STUDENT_NAME", s != null ? s.getName() : "N/A");
            params.put("ROLL_NO", s != null ? s.getRollNumber() : "N/A");
            params.put("REG_NO", s != null && s.getRegistrationNo() != null ? s.getRegistrationNo() : "");
            params.put("DEPARTMENT", s != null ? extractDept(s.getRollNumber()) : "N/A");
            params.put("SEMESTER", "3");
            params.put("PAYMENT_MODE", currentFeeReceipt.getPaymentMode() != null ? currentFeeReceipt.getPaymentMode() : "CASH");
            params.put("BANK_ACCOUNT", currentFeeReceipt.getBaseAccount() != null ? currentFeeReceipt.getBaseAccount() : "TMB Main");
            BigDecimal totalAmt = currentFeeReceipt.getTotalAmount() != null ? currentFeeReceipt.getTotalAmount() : java.math.BigDecimal.ZERO;
            params.put("TOTAL_AMOUNT", String.format("%.2f", totalAmt));
            params.put("AMOUNT_IN_WORDS", numberToWords(totalAmt));
            params.put("REMARKS", currentFeeReceipt.getPaymentMode() != null ? currentFeeReceipt.getPaymentMode() : "-");

            List<ReceiptPrintItemDto> itemsList = new ArrayList<>();
            if (currentFeeReceipt.getItems() != null && !currentFeeReceipt.getItems().isEmpty()) {
                int i = 1;
                for (FeeReceiptItem item : currentFeeReceipt.getItems()) {
                    String name = item.getFeesName() != null ? item.getFeesName().getName() : "College Fee";
                    BigDecimal amt = item.getAmount() != null ? item.getAmount() : java.math.BigDecimal.ZERO;
                    itemsList.add(new ReceiptPrintItemDto(i++, name, amt));
                }
            } else {
                itemsList.add(new ReceiptPrintItemDto(1, "Tuition Fee", totalAmt));
            }

            ReportManager.printReport("FeeReceipt", itemsList, params);
            new Alert(Alert.AlertType.INFORMATION, "Receipt print job sent for Receipt No: " + currentFeeReceipt.getReceiptNumber()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Print failed: " + e.getMessage()).showAndWait();
        }
    }

    private String numberToWords(java.math.BigDecimal num) {
        if (num == null || num.compareTo(java.math.BigDecimal.ZERO) == 0) return "Zero Rupees Only";
        long wholePart = num.longValue();
        StringBuilder sb = new StringBuilder();
        if (wholePart >= 10000000) { sb.append(wholePart / 10000000).append(" Crore "); wholePart %= 10000000; }
        if (wholePart >= 100000) { sb.append(wholePart / 100000).append(" Lakh "); wholePart %= 100000; }
        if (wholePart >= 1000) { sb.append(wholePart / 1000).append(" Thousand "); wholePart %= 1000; }
        if (wholePart >= 100) { sb.append(wholePart / 100).append(" Hundred "); wholePart %= 100; }
        String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
                "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        if (wholePart >= 20) { sb.append(tens[(int)(wholePart/10)]).append(" "); wholePart %= 10; }
        if (wholePart > 0) { sb.append(ones[(int)wholePart]).append(" "); }
        return sb.toString().trim() + " Rupees Only";
    }

    public static class ReceiptPrintItemDto {
        private Integer slNo;
        private String feeName;
        private java.math.BigDecimal feeAmount;

        public ReceiptPrintItemDto(Integer slNo, String feeName, java.math.BigDecimal feeAmount) {
            this.slNo = slNo;
            this.feeName = feeName;
            this.feeAmount = feeAmount;
        }

        public Integer getSlNo() { return slNo; }
        public String getFeeName() { return feeName; }
        public java.math.BigDecimal getFeeAmount() { return feeAmount; }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
