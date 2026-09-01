package com.nscet.cms.ui.controller;

import com.nscet.cms.ui.util.ExportUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FunctionExpenseController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> functionNameCombo;
    @FXML private ComboBox<String> transTypeCombo;
    @FXML private TextField balanceField;
    @FXML private TextField amountField;
    @FXML private TextField curBalanceField;
    @FXML private ComboBox<String> fundTransferCombo;
    @FXML private ComboBox<String> transferToCombo;
    @FXML private TextField remarksField;

    @FXML private TableView<FunctionExpenseRow> table;
    @FXML private TableColumn<FunctionExpenseRow, String> colSlNo;
    @FXML private TableColumn<FunctionExpenseRow, String> colDate;
    @FXML private TableColumn<FunctionExpenseRow, String> colFunctionName;
    @FXML private TableColumn<FunctionExpenseRow, String> colTransType;
    @FXML private TableColumn<FunctionExpenseRow, String> colAmount;
    @FXML private TableColumn<FunctionExpenseRow, String> colBalance;
    @FXML private TableColumn<FunctionExpenseRow, String> colFundTransfer;
    @FXML private TableColumn<FunctionExpenseRow, String> colRemarks;

    private final ObservableList<FunctionExpenseRow> tableData = FXCollections.observableArrayList();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (datePicker != null) datePicker.setValue(LocalDate.now());

        if (functionNameCombo != null) {
            functionNameCombo.getItems().setAll(
                "Select",
                "WORKSHOP - BANGALORE",
                "ADMISSION 2020-2021",
                "PASSPORT OFFICE MADURAI",
                "GRADUATION DAY 2020",
                "ANNUAL DAY 2020",
                "PROJECT EXPO - 2020",
                "SPORTS DAY 2020",
                "NSCET BOYS HOSTEL DAY"
            );
            functionNameCombo.setValue("Select");
        }

        if (transTypeCombo != null) {
            transTypeCombo.getItems().setAll("Select", "Expense", "Advance", "Refund", "Income");
            transTypeCombo.setValue("Select");
        }

        if (fundTransferCombo != null) {
            fundTransferCombo.getItems().setAll("Select", "Yes", "No");
            fundTransferCombo.setValue("Select");
        }

        if (transferToCombo != null) {
            transferToCombo.getItems().setAll("Select", "General Account", "Exam Cell Account", "Hostel Account");
            transferToCombo.setValue("Select");
        }

        setupTable();
        loadSampleData();
    }

    private void setupTable() {
        if (table == null) return;
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (colSlNo != null) colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(tableData.indexOf(c.getValue()) + 1)));
        if (colDate != null) colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));
        if (colFunctionName != null) colFunctionName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFunctionName()));
        if (colTransType != null) colTransType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTransType()));
        if (colAmount != null) colAmount.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount()));
        if (colBalance != null) colBalance.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBalance()));
        if (colFundTransfer != null) colFundTransfer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFundTransfer()));
        if (colRemarks != null) colRemarks.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks()));

        table.setItems(tableData);
    }

    private void loadSampleData() {
        tableData.clear();
        tableData.add(new FunctionExpenseRow("24-08-2026", "WORKSHOP - BANGALORE", "Expense", "15000.00", "35000.00", "No", "Travel & Stay Advance for Faculty"));
        tableData.add(new FunctionExpenseRow("20-08-2026", "GRADUATION DAY 2020", "Expense", "42000.00", "18000.00", "No", "Stage decoration and audio system"));
        tableData.add(new FunctionExpenseRow("15-08-2026", "ANNUAL DAY 2020", "Expense", "25000.00", "75000.00", "Yes", "Prizes and Momento Purchase"));
    }

    @FXML
    private void handleSearch() {
        ExportUtils.showAlert("Search", "Showing active Function Expense logs.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleSave() {
        String funcName = (functionNameCombo != null && functionNameCombo.getValue() != null) ? functionNameCombo.getValue() : "Select";
        String amount = (amountField != null && amountField.getText() != null) ? amountField.getText().trim() : "";

        if ("Select".equals(funcName) || amount.isEmpty()) {
            ExportUtils.showAlert("Validation Error", "Please select Function Name and enter Amount.", Alert.AlertType.WARNING);
            return;
        }

        String dateStr = (datePicker != null && datePicker.getValue() != null) ? datePicker.getValue().format(fmt) : LocalDate.now().format(fmt);
        String tType = (transTypeCombo != null && transTypeCombo.getValue() != null) ? transTypeCombo.getValue() : "Expense";
        String fTrans = (fundTransferCombo != null && fundTransferCombo.getValue() != null) ? fundTransferCombo.getValue() : "No";
        String rem = (remarksField != null && remarksField.getText() != null) ? remarksField.getText().trim() : "";

        tableData.add(new FunctionExpenseRow(dateStr, funcName, tType, amount, "0.00", fTrans, rem));
        ExportUtils.showAlert("Success", "Function Expense record saved successfully!", Alert.AlertType.INFORMATION);
        handleClear();
    }

    @FXML
    private void handlePrint() {
        ExportUtils.showAlert("Print", "Printing Function Expense details...", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExit() {
        handleClear();
    }

    private void handleClear() {
        if (datePicker != null) datePicker.setValue(LocalDate.now());
        if (functionNameCombo != null) functionNameCombo.setValue("Select");
        if (transTypeCombo != null) transTypeCombo.setValue("Select");
        if (fundTransferCombo != null) fundTransferCombo.setValue("Select");
        if (transferToCombo != null) transferToCombo.setValue("Select");
        if (balanceField != null) balanceField.clear();
        if (amountField != null) amountField.clear();
        if (curBalanceField != null) curBalanceField.clear();
        if (remarksField != null) remarksField.clear();
        if (table != null) table.getSelectionModel().clearSelection();
    }

    public static class FunctionExpenseRow {
        private String date;
        private String functionName;
        private String transType;
        private String amount;
        private String balance;
        private String fundTransfer;
        private String remarks;

        public FunctionExpenseRow(String date, String functionName, String transType, String amount, String balance, String fundTransfer, String remarks) {
            this.date = date;
            this.functionName = functionName;
            this.transType = transType;
            this.amount = amount;
            this.balance = balance;
            this.fundTransfer = fundTransfer;
            this.remarks = remarks;
        }

        public String getDate() { return date; }
        public String getFunctionName() { return functionName; }
        public String getTransType() { return transType; }
        public String getAmount() { return amount; }
        public String getBalance() { return balance; }
        public String getFundTransfer() { return fundTransfer; }
        public String getRemarks() { return remarks; }
    }
}
