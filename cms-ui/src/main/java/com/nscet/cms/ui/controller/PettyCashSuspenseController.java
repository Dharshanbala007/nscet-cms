package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.PettyCashSuspenseService;
import com.nscet.cms.db.entity.PettyCashSuspense;
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
    @FXML private TextField deptField;
    @FXML private TextField designationField;
    @FXML private TextField amountField;
    @FXML private TextField amountWordsField;
    @FXML private TextArea purposeField;
    @FXML private TableView<PettyCashSuspense> dataTable;

    @FXML private TableColumn<PettyCashSuspense, Long> colSlNo;
    @FXML private TableColumn<PettyCashSuspense, String> colVNo;
    @FXML private TableColumn<PettyCashSuspense, String> colDate;
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
        datePicker.setValue(LocalDate.now());
        voucherNoField.setText(service.generateNextVoucherNo());
        setupTable();

        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                BigDecimal amt = new BigDecimal(newVal);
                amountWordsField.setText(numberToWords(amt));
            } catch (Exception e) {
                amountWordsField.clear();
            }
        });

        loadTableData();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVNo.setCellValueFactory(new PropertyValueFactory<>("voucherNo"));
        colDate.setCellValueFactory(cellData -> {
            LocalDate d = cellData.getValue().getVoucherDate();
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    d != null ? d.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "");
        });
        colStaff.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        dataTable.setItems(dataList);

        dataTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedRecord = newVal;
                fillForm(newVal);
            }
        });
    }

    private void loadTableData() {
        dataList.setAll(service.getAll());
    }

    private void fillForm(PettyCashSuspense rec) {
        voucherNoField.setText(rec.getVoucherNo());
        datePicker.setValue(rec.getVoucherDate());
        collegeRadio.setSelected("College".equals(rec.getCollegeOrHostel()));
        hostelRadio.setSelected("Hostel".equals(rec.getCollegeOrHostel()));
        staffNameField.setText(rec.getStaffName());
        deptField.setText(rec.getDepartment());
        designationField.setText(rec.getDesignation());
        amountField.setText(rec.getAmount() != null ? rec.getAmount().toString() : "");
        amountWordsField.setText(rec.getAmountInWords());
        purposeField.setText(rec.getPurpose());
    }

    private void clearForm() {
        selectedRecord = null;
        voucherNoField.setText(service.generateNextVoucherNo());
        datePicker.setValue(LocalDate.now());
        collegeRadio.setSelected(true);
        staffNameField.clear();
        deptField.clear();
        designationField.clear();
        amountField.clear();
        amountWordsField.clear();
        purposeField.clear();
        dataTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAdd() {
        clearForm();
    }

    @FXML
    private void handleModify() {
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a record to modify");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a record to delete");
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
        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a date");
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

        PettyCashSuspense entity;
        if (selectedRecord != null) {
            entity = selectedRecord;
        } else {
            entity = new PettyCashSuspense();
            entity.setVoucherNo(voucherNoField.getText());
            entity.setIsActive(true);
        }

        entity.setVoucherDate(datePicker.getValue());
        entity.setCollegeOrHostel(hostelRadio.isSelected() ? "Hostel" : "College");
        entity.setStaffName(staffNameField.getText().trim());
        entity.setDepartment(deptField.getText().trim());
        entity.setDesignation(designationField.getText().trim());
        entity.setAmount(amount);
        entity.setAmountInWords(amountWordsField.getText());
        entity.setPurpose(purposeField.getText().trim());

        service.create(entity);
        showAlert(Alert.AlertType.INFORMATION, "Record saved successfully");
        clearForm();
        loadTableData();
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
