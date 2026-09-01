package com.nscet.cms.ui.controller.accounts;

import com.nscet.cms.ui.navigation.NavigationManager;
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
public class PendingBillDetailsController implements Initializable {

    @FXML private TextField supplierField;
    @FXML private DatePicker datePicker;
    @FXML private TextField itemField;
    @FXML private ComboBox<String> deptCombo;
    @FXML private TextField detailsField;
    @FXML private TextField billNoField;
    @FXML private DatePicker billDatePicker;
    @FXML private TextField amountField;

    @FXML private TableView<PendingBillRow> table;
    @FXML private TableColumn<PendingBillRow, String> colBillNo;
    @FXML private TableColumn<PendingBillRow, String> colBillDate;
    @FXML private TableColumn<PendingBillRow, String> colSupplier;
    @FXML private TableColumn<PendingBillRow, String> colItem;
    @FXML private TableColumn<PendingBillRow, String> colDetails;
    @FXML private TableColumn<PendingBillRow, String> colAmount;

    private final ObservableList<PendingBillRow> tableData = FXCollections.observableArrayList();
    private PendingBillRow selectedRow = null;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (datePicker != null) datePicker.setValue(LocalDate.now());
        if (billDatePicker != null) billDatePicker.setValue(LocalDate.now());

        if (deptCombo != null) {
            deptCombo.getItems().setAll("Select", "COMPUTER SCIENCE", "ELECTRONICS & COMM", "MECHANICAL", "CIVIL", "GENERAL ADMIN");
            deptCombo.setValue("Select");
        }

        setupTable();
        loadSampleData();

        if (table != null) {
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    selectedRow = newV;
                    if (billNoField != null) billNoField.setText(newV.getBillNo());
                    if (supplierField != null) supplierField.setText(newV.getSupplier());
                    if (itemField != null) itemField.setText(newV.getItem());
                    if (detailsField != null) detailsField.setText(newV.getDetails());
                    if (amountField != null) amountField.setText(newV.getAmount());
                }
            });
        }
    }

    private void setupTable() {
        if (table == null) return;
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (colBillNo != null) colBillNo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBillNo()));
        if (colBillDate != null) colBillDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBillDate()));
        if (colSupplier != null) colSupplier.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSupplier()));
        if (colItem != null) colItem.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getItem()));
        if (colDetails != null) colDetails.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDetails()));
        if (colAmount != null) colAmount.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount()));
        table.setItems(tableData);
    }

    private void loadSampleData() {
        tableData.clear();
        tableData.add(new PendingBillRow("BL-201", "24-08-2026", "N.S. Store", "Stationery Supply", "Paper & Files for Exam Cell", "12400.00"));
        tableData.add(new PendingBillRow("BL-202", "22-08-2026", "Sri Bhathrakaliamman Achagam", "Printing", "Student Prospectus Printing", "45000.00"));
        tableData.add(new PendingBillRow("BL-203", "20-08-2026", "Vectra Computer Solution", "Lab Consumables", "RAM & SSD Upgrade", "38500.00"));
    }

    @FXML
    private void handleAdd() {
        String billNo = billNoField != null ? billNoField.getText().trim() : "";
        String supplier = supplierField != null ? supplierField.getText().trim() : "";
        String item = itemField != null ? itemField.getText().trim() : "";
        String details = detailsField != null ? detailsField.getText().trim() : "";
        String amount = amountField != null ? amountField.getText().trim() : "";

        if (billNo.isEmpty() || amount.isEmpty()) {
            ExportUtils.showAlert("Validation Error", "Please enter Bill No and Amount.", Alert.AlertType.WARNING);
            return;
        }

        String bDate = (billDatePicker != null && billDatePicker.getValue() != null) 
                ? billDatePicker.getValue().format(fmt) : LocalDate.now().format(fmt);

        tableData.add(new PendingBillRow(billNo, bDate, supplier, item, details, amount));
        handleClear();
    }

    @FXML
    private void handleModify() {
        if (selectedRow == null) {
            ExportUtils.showAlert("Warning", "Please select a bill from the table to modify.", Alert.AlertType.WARNING);
            return;
        }
        if (supplierField != null) supplierField.requestFocus();
    }

    @FXML
    private void handleSave() {
        ExportUtils.showAlert("Success", "Pending Bill record saved successfully!", Alert.AlertType.INFORMATION);
        handleClear();
    }

    @FXML
    private void handleDelete() {
        if (selectedRow == null) {
            ExportUtils.showAlert("Warning", "Please select a bill from the table to delete.", Alert.AlertType.WARNING);
            return;
        }
        tableData.remove(selectedRow);
        ExportUtils.showAlert("Deleted", "Pending Bill record deleted.", Alert.AlertType.INFORMATION);
        handleClear();
    }

    @FXML
    private void handleClear() {
        selectedRow = null;
        if (supplierField != null) supplierField.clear();
        if (itemField != null) itemField.clear();
        if (detailsField != null) detailsField.clear();
        if (deptCombo != null) deptCombo.setValue("Select");
        if (billNoField != null) billNoField.clear();
        if (amountField != null) amountField.clear();
        if (billDatePicker != null) billDatePicker.setValue(LocalDate.now());
        if (table != null) table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClose() {
        handleClear();
    }

    public static class PendingBillRow {
        private String billNo;
        private String billDate;
        private String supplier;
        private String item;
        private String details;
        private String amount;

        public PendingBillRow(String billNo, String billDate, String supplier, String item, String details, String amount) {
            this.billNo = billNo;
            this.billDate = billDate;
            this.supplier = supplier;
            this.item = item;
            this.details = details;
            this.amount = amount;
        }

        public String getBillNo() { return billNo; }
        public String getBillDate() { return billDate; }
        public String getSupplier() { return supplier; }
        public String getItem() { return item; }
        public String getDetails() { return details; }
        public String getAmount() { return amount; }
    }
}
