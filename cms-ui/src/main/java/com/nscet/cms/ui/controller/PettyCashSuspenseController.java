package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.PettyCashSuspenseService;
import com.nscet.cms.db.entity.PettyCashSuspense;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class PettyCashSuspenseController implements Initializable {

    @FXML private RadioButton collegeRadio;
    @FXML private RadioButton hostelRadio;
    @FXML private TextField voucherNoField;
    @FXML private DatePicker datePicker;
    @FXML private TextField staffNameField;
    @FXML private TextField staffCodeField;
    @FXML private TextField deptField;
    @FXML private TextField designationField;
    @FXML private TextField amountField;
    @FXML private TextField amountWordsField;
    @FXML private TextArea purposeField;
    @FXML private TableView<PettyCashSuspense> dataTable;

    @FXML private TableColumn<PettyCashSuspense, Long> colSlNo;
    @FXML private TableColumn<PettyCashSuspense, String> colVNo;
    @FXML private TableColumn<PettyCashSuspense, String> colDate;
    @FXML private TableColumn<PettyCashSuspense, String> colStaffCode;
    @FXML private TableColumn<PettyCashSuspense, String> colStaff;
    @FXML private TableColumn<PettyCashSuspense, String> colDept;
    @FXML private TableColumn<PettyCashSuspense, BigDecimal> colAmount;
    @FXML private TableColumn<PettyCashSuspense, String> colPurpose;

    @Autowired
    private PettyCashSuspenseService service;

    private final ObservableList<PettyCashSuspense> dataList = FXCollections.observableArrayList();
    private PettyCashSuspense selectedRecord;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (datePicker != null) datePicker.setValue(LocalDate.now());
        if (voucherNoField != null) voucherNoField.setText(service.generateNextVoucherNo());
        setupTable();

        if (amountField != null) {
            amountField.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    BigDecimal amt = new BigDecimal(newVal);
                    if (amountWordsField != null) amountWordsField.setText(numberToWords(amt));
                } catch (Exception e) {
                    if (amountWordsField != null) amountWordsField.clear();
                }
            });
        }

        loadTableData();
    }

    private void setupTable() {
        if (colSlNo != null) colSlNo.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colVNo != null) colVNo.setCellValueFactory(new PropertyValueFactory<>("voucherNo"));
        if (colDate != null) {
            colDate.setCellValueFactory(cellData -> {
                LocalDate d = cellData.getValue().getVoucherDate();
                return javafx.beans.binding.Bindings.createStringBinding(() ->
                        d != null ? d.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "");
            });
        }
        if (colStaffCode != null) {
            colStaffCode.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStaffCode() != null ? cellData.getValue().getStaffCode() : ""));
        }
        if (colStaff != null) colStaff.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        if (colDept != null) colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        if (colAmount != null) colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        if (colPurpose != null) colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        
        if (dataTable != null) {
            dataTable.setItems(dataList);
            dataTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectedRecord = newVal;
                    fillForm(newVal);
                }
            });
        }
    }

    private void loadTableData() {
        dataList.setAll(service.getAll());
    }

    private void fillForm(PettyCashSuspense rec) {
        if (voucherNoField != null) voucherNoField.setText(rec.getVoucherNo());
        if (datePicker != null) datePicker.setValue(rec.getVoucherDate());
        if (collegeRadio != null) collegeRadio.setSelected("College".equals(rec.getCollegeOrHostel()));
        if (hostelRadio != null) hostelRadio.setSelected("Hostel".equals(rec.getCollegeOrHostel()));
        if (staffNameField != null) staffNameField.setText(rec.getStaffName());
        if (staffCodeField != null) staffCodeField.setText(rec.getStaffCode() != null ? rec.getStaffCode() : "");
        if (deptField != null) deptField.setText(rec.getDepartment());
        if (designationField != null) designationField.setText(rec.getDesignation());
        if (amountField != null) amountField.setText(rec.getAmount() != null ? rec.getAmount().toString() : "");
        if (amountWordsField != null) amountWordsField.setText(rec.getAmountInWords());
        if (purposeField != null) purposeField.setText(rec.getPurpose());
    }

    private void clearForm() {
        selectedRecord = null;
        if (voucherNoField != null) voucherNoField.setText(service.generateNextVoucherNo());
        if (datePicker != null) datePicker.setValue(LocalDate.now());
        if (collegeRadio != null) collegeRadio.setSelected(true);
        if (staffNameField != null) staffNameField.clear();
        if (staffCodeField != null) staffCodeField.clear();
        if (deptField != null) deptField.clear();
        if (designationField != null) designationField.clear();
        if (amountField != null) amountField.clear();
        if (amountWordsField != null) amountWordsField.clear();
        if (purposeField != null) purposeField.clear();
        if (dataTable != null) dataTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAdd() {
        clearForm();
        if (staffNameField != null) staffNameField.requestFocus();
    }

    @FXML
    private void handleModify() {
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a record from the table to modify");
            return;
        }
        if (staffNameField != null) staffNameField.requestFocus();
    }

    @FXML
    private void handleDelete() {
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a record from the table to delete");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete record " + selectedRecord.getVoucherNo() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.softDelete(selectedRecord.getId());
                clearForm();
                loadTableData();
            }
        });
    }

    @FXML
    private void handleCancel() {
        clearForm();
    }

    @FXML
    private void handleSave() {
        if (datePicker != null && datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a date");
            return;
        }
        String amountText = amountField != null ? amountField.getText().trim() : "";
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

        PettyCashSuspense entity;
        if (selectedRecord != null) {
            entity = selectedRecord;
        } else {
            entity = new PettyCashSuspense();
            if (voucherNoField != null) entity.setVoucherNo(voucherNoField.getText());
            entity.setIsActive(true);
        }

        if (datePicker != null) entity.setVoucherDate(datePicker.getValue());
        entity.setCollegeOrHostel((hostelRadio != null && hostelRadio.isSelected()) ? "Hostel" : "College");
        if (staffNameField != null) entity.setStaffName(staffNameField.getText().trim());
        if (staffCodeField != null) entity.setStaffCode(staffCodeField.getText().trim());
        if (deptField != null) entity.setDepartment(deptField.getText().trim());
        if (designationField != null) entity.setDesignation(designationField.getText().trim());
        entity.setAmount(amount);
        if (amountWordsField != null) entity.setAmountInWords(amountWordsField.getText());
        if (purposeField != null) entity.setPurpose(purposeField.getText().trim());

        service.create(entity);
        showAlert(Alert.AlertType.INFORMATION, "Record saved successfully");
        clearForm();
        loadTableData();
    }

    @FXML
    private void handleClose() {
        clearForm();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Petty Cash Suspense");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String numberToWords(BigDecimal num) {
        if (num.compareTo(BigDecimal.ZERO) == 0) return "Zero";
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
}
