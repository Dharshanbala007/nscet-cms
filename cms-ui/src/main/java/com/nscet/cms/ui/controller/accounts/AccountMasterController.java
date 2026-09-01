package com.nscet.cms.ui.controller.accounts;

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
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class AccountMasterController implements Initializable {

    @FXML private TextField accGroupField;
    @FXML private TextField accCodeField;
    @FXML private TextField accNameField;
    @FXML private CheckBox chkTrial;
    @FXML private CheckBox chkIE;
    @FXML private CheckBox chkBS;
    @FXML private CheckBox chkSupplier;
    @FXML private CheckBox chkActive;

    @FXML private TableView<AccountMasterRow> table;
    @FXML private TableColumn<AccountMasterRow, String> colAccCode;
    @FXML private TableColumn<AccountMasterRow, String> colAccName;
    @FXML private TableColumn<AccountMasterRow, String> colGroupName;
    @FXML private TableColumn<AccountMasterRow, String> colSupplier;
    @FXML private TableColumn<AccountMasterRow, String> colActive;

    @FXML private TitledPane tableContainer;

    private final ObservableList<AccountMasterRow> tableData = FXCollections.observableArrayList();
    private AccountMasterRow selectedRow = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedRow = newV;
                accCodeField.setText(newV.getAccountCode());
                accNameField.setText(newV.getAccountName());
                accGroupField.setText(newV.getGroupName());
                chkSupplier.setSelected("1".equals(newV.getSupplier()));
                chkActive.setSelected("1".equals(newV.getActive()));
            }
        });

        if (tableContainer != null) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
        }
    }

    private void setupTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colAccCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountCode()));
        colAccName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountName()));
        colGroupName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGroupName()));
        colSupplier.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSupplier()));
        colActive.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getActive()));
        table.setItems(tableData);
    }

    private void loadSampleDataFromScreenshot() {
        tableData.clear();
        tableData.add(new AccountMasterRow("1", "Pooja Expenses", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("2", "N.S. Store", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("3", "Sri Bhathrakaliamman Achagam", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("4", "N.S. Tech. Canteen", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("5", "Gana Vijayam Systems", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("6", "Vectra Computer solution", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("7", "Madurai Vishwas", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("8", "Microgenesis", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("9", "S & T Engineers", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("10", "Postel courier", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("11", "Electricity Charges", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("12", "Advertisement Expenses", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("13", "Phone Charges", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("14", "Printing & Stationary", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("15", "Chemistry Lab Expenses", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("16", "TNEB Deposit", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("17", "Furniture Fittings", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("18", "Photo Expenses", "Expenses", "0", "1"));
        tableData.add(new AccountMasterRow("19", "VI Micro System Pvt Ltd", "Expenses", "1", "1"));
        tableData.add(new AccountMasterRow("20", "Supreme Scientific Corporation", "Expenses", "1", "1"));
    }

    @FXML
    private void handleAdd() {
        handleClear();
        accCodeField.requestFocus();
    }

    @FXML
    private void handleModify() {
        if (selectedRow == null) {
            ExportUtils.showAlert("Warning", "Please select an Account to modify.", Alert.AlertType.WARNING);
            return;
        }
        accNameField.requestFocus();
    }

    @FXML
    private void handleSave() {
        String code = accCodeField.getText() != null ? accCodeField.getText().trim() : "";
        String name = accNameField.getText() != null ? accNameField.getText().trim() : "";
        String group = accGroupField.getText() != null ? accGroupField.getText().trim() : "Expenses";
        String supplier = chkSupplier.isSelected() ? "1" : "0";
        String active = chkActive.isSelected() ? "1" : "0";

        if (code.isEmpty() || name.isEmpty()) {
            ExportUtils.showAlert("Validation Error", "Please enter Account Code and Account Name.", Alert.AlertType.WARNING);
            return;
        }

        if (selectedRow != null) {
            selectedRow.setAccountCode(code);
            selectedRow.setAccountName(name);
            selectedRow.setGroupName(group);
            selectedRow.setSupplier(supplier);
            selectedRow.setActive(active);
            table.refresh();
            ExportUtils.showAlert("Success", "Account Master updated successfully!", Alert.AlertType.INFORMATION);
        } else {
            tableData.add(new AccountMasterRow(code, name, group, supplier, active));
            ExportUtils.showAlert("Success", "Account Master saved successfully!", Alert.AlertType.INFORMATION);
        }
        handleClear();
    }

    @FXML
    private void handleDelete() {
        if (selectedRow == null) {
            ExportUtils.showAlert("Warning", "Please select an Account Master to delete.", Alert.AlertType.WARNING);
            return;
        }
        tableData.remove(selectedRow);
        ExportUtils.showAlert("Deleted", "Account Master deleted.", Alert.AlertType.INFORMATION);
        handleClear();
    }

    @FXML
    private void handleClear() {
        selectedRow = null;
        accGroupField.clear();
        accCodeField.clear();
        accNameField.clear();
        chkTrial.setSelected(false);
        chkIE.setSelected(false);
        chkBS.setSelected(false);
        chkSupplier.setSelected(false);
        chkActive.setSelected(true);
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClose() {
        handleClear();
    }

    @FXML
    private void handleFind() {
        loadSampleDataFromScreenshot();
        if (tableContainer != null) {
            tableContainer.setVisible(true);
            tableContainer.setManaged(true);
        }
        table.requestFocus();
    }

    @FXML
    private void handleCloseTable() {
        if (tableContainer != null) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
        }
        tableData.clear();
    }

    @FXML
    private void handleExportCsv() {
        ExportUtils.exportToCsv(table, "Account_Master", table.getScene().getWindow());
    }

    @FXML
    private void handleExportPdf() {
        ExportUtils.exportToPdf(table, "Account Master Report", table.getScene().getWindow());
    }

    public static class AccountMasterRow {
        private String accountCode;
        private String accountName;
        private String groupName;
        private String supplier;
        private String active;

        public AccountMasterRow(String accountCode, String accountName, String groupName, String supplier, String active) {
            this.accountCode = accountCode;
            this.accountName = accountName;
            this.groupName = groupName;
            this.supplier = supplier;
            this.active = active;
        }

        public String getAccountCode() { return accountCode; }
        public void setAccountCode(String accountCode) { this.accountCode = accountCode; }
        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public String getSupplier() { return supplier; }
        public void setSupplier(String supplier) { this.supplier = supplier; }
        public String getActive() { return active; }
        public void setActive(String active) { this.active = active; }
    }
}
