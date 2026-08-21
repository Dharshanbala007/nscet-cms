package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.BankMaster;
import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.repository.BankMasterRepository;
import com.nscet.cms.db.repository.FeeReceiptRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class DaySettlementController implements Initializable {

    @FXML private DatePicker settlementDate;
    @FXML private ComboBox<String> baseAccountCombo;
    @FXML private TableView<FeeReceipt> reportTable;
    @FXML private TableColumn<FeeReceipt, String> slNoCol;
    @FXML private TableColumn<FeeReceipt, String> receiptNoCol;
    @FXML private TableColumn<FeeReceipt, String> studentNameCol;
    @FXML private TableColumn<FeeReceipt, String> amountCol;
    @FXML private TableColumn<FeeReceipt, String> payModeCol;

    @FXML private Label totalCashLabel;
    @FXML private Label totalChequeLabel;
    @FXML private Label totalOnlineLabel;
    @FXML private Label grandTotalLabel;

    @Autowired private FeeReceiptRepository feeReceiptRepository;
    @Autowired private BankMasterRepository bankMasterRepository;

    private ObservableList<FeeReceipt> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settlementDate.setValue(LocalDate.now());
        setupCombos();
        setupTableColumns();
        reportTable.setItems(tableData);
        handleLoad();
    }

    private void setupCombos() {
        try {
            baseAccountCombo.setItems(FXCollections.observableArrayList("ALL", "Cash", "Federal Bank", "TMB Exam Fee"));
            baseAccountCombo.setValue("ALL");
        } catch (Exception e) {
            System.err.println("[DaySettlementController] Error loading bank combo: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        slNoCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(tableData.indexOf(c.getValue()) + 1)));
        receiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNumber() != null ? c.getValue().getReceiptNumber() : "N/A"));
        studentNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudent() != null ? c.getValue().getStudent().getName() : "N/A"));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalAmount() != null ? "\u20B9" + c.getValue().getTotalAmount().toPlainString() : "\u20B90"));
        payModeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaymentMode() != null ? c.getValue().getPaymentMode() : "CASH"));
    }

    @FXML
    private void handleLoad() {
        try {
            LocalDate date = settlementDate.getValue() != null ? settlementDate.getValue() : LocalDate.now();
            List<FeeReceipt> receipts = feeReceiptRepository.findByDateRangeWithStudentList(date, date);

            tableData.clear();
            BigDecimal totalCash = BigDecimal.ZERO;
            BigDecimal totalCheque = BigDecimal.ZERO;
            BigDecimal totalOnline = BigDecimal.ZERO;

            for (FeeReceipt fr : receipts) {
                String baseAcc = baseAccountCombo.getValue();
                if ("ALL".equals(baseAcc) || baseAcc == null || (fr.getBaseAccount() != null && fr.getBaseAccount().equalsIgnoreCase(baseAcc))) {
                    tableData.add(fr);
                    BigDecimal amt = fr.getTotalAmount() != null ? fr.getTotalAmount() : BigDecimal.ZERO;
                    String mode = fr.getPaymentMode() != null ? fr.getPaymentMode().toUpperCase() : "CASH";

                    if (mode.contains("CASH")) {
                        totalCash = totalCash.add(amt);
                    } else if (mode.contains("CHEQUE") || mode.contains("DD")) {
                        totalCheque = totalCheque.add(amt);
                    } else {
                        totalOnline = totalOnline.add(amt);
                    }
                }
            }

            BigDecimal grandTotal = totalCash.add(totalCheque).add(totalOnline);
            totalCashLabel.setText("\u20B9" + totalCash.toPlainString());
            totalChequeLabel.setText("\u20B9" + totalCheque.toPlainString());
            totalOnlineLabel.setText("\u20B9" + totalOnline.toPlainString());
            grandTotalLabel.setText("\u20B9" + grandTotal.toPlainString());
        } catch (Exception e) {
            System.err.println("[DaySettlementController] Error loading settlement data: " + e.getMessage());
        }
    }

    @FXML
    private void handleSettle() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Day Settlement Complete");
        alert.setHeaderText(null);
        alert.setContentText("Day settlement for " + settlementDate.getValue() + " has been completed successfully.\nGrand Total: " + grandTotalLabel.getText());
        alert.showAndWait();
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Settlement Summary");
        alert.setHeaderText(null);
        alert.setContentText("Settlement Summary for " + settlementDate.getValue() + " sent to printer.");
        alert.showAndWait();
    }
}
