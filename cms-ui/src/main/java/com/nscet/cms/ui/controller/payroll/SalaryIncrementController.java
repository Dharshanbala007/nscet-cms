package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.SalaryIncrement;
import com.nscet.cms.db.entity.payroll.StaffSalary;
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
public class SalaryIncrementController implements Initializable {

    @FXML private TableView<SalaryIncrement> table;
    @FXML private TableColumn<SalaryIncrement, String> colEffDate, colCode, colName, colDept, colOldBasic, colNewBasic, colIncrement, colNewGross, colRemarks;

    @FXML private ComboBox<String> staffCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField oldBasicField, newBasicField, oldSplField, newSplField, remarksField;

    @Autowired private PayrollService payrollService;

    private ObservableList<SalaryIncrement> logList = FXCollections.observableArrayList();
    private List<StaffSalary> staffList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        setupTable();
        loadStaffCombo();
        loadIncrements();

        staffCombo.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            if (newIdx != null && newIdx.intValue() >= 0 && staffList != null && newIdx.intValue() < staffList.size()) {
                StaffSalary s = staffList.get(newIdx.intValue());
                oldBasicField.setText(s.getBasicPay() != null ? s.getBasicPay().toString() : "0.00");
                oldSplField.setText(s.getSpecialAllowance() != null ? s.getSpecialAllowance().toString() : "0.00");
                newBasicField.setText(s.getBasicPay() != null ? s.getBasicPay().toString() : "0.00");
                newSplField.setText(s.getSpecialAllowance() != null ? s.getSpecialAllowance().toString() : "0.00");
            }
        });
    }

    private void setupTable() {
        colEffDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEffectiveDate() != null ? c.getValue().getEffectiveDate().toString() : ""));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colOldBasic.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getOldBasic())));
        colNewBasic.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getNewBasic())));
        colIncrement.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getIncrementAmount())));
        colNewGross.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getNewGross())));
        colRemarks.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks() != null ? c.getValue().getRemarks() : ""));

        table.setItems(logList);
    }

    private void loadStaffCombo() {
        try {
            staffList = payrollService.getAllStaffSalaries();
            staffCombo.getItems().clear();
            for (StaffSalary s : staffList) {
                staffCombo.getItems().add(s.getStaffCode() + " - " + s.getStaffName());
            }
            if (!staffCombo.getItems().isEmpty()) staffCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.err.println("[SalaryIncrementController] Error loading staff combo: " + e.getMessage());
        }
    }

    private void loadIncrements() {
        try {
            List<SalaryIncrement> list = payrollService.getAllIncrements();
            logList.setAll(list);
        } catch (Exception e) {
            System.err.println("[SalaryIncrementController] Error loading logs: " + e.getMessage());
        }
    }

    @FXML
    private void handleApply() {
        int idx = staffCombo.getSelectionModel().getSelectedIndex();
        if (idx < 0 || staffList == null || idx >= staffList.size()) {
            showAlert("Validation Error", "Please select a staff member.", Alert.AlertType.WARNING);
            return;
        }

        StaffSalary staff = staffList.get(idx);
        BigDecimal newBasic = parseDecimal(newBasicField.getText());
        BigDecimal newSpl = parseDecimal(newSplField.getText());

        BigDecimal oldBasic = staff.getBasicPay() != null ? staff.getBasicPay() : BigDecimal.ZERO;
        BigDecimal oldSpl = staff.getSpecialAllowance() != null ? staff.getSpecialAllowance() : BigDecimal.ZERO;

        BigDecimal diffBasic = newBasic.subtract(oldBasic);
        BigDecimal diffSpl = newSpl.subtract(oldSpl);
        BigDecimal totalInc = diffBasic.add(diffSpl);

        SalaryIncrement inc = new SalaryIncrement();
        inc.setStaffCode(staff.getStaffCode());
        inc.setStaffName(staff.getStaffName());
        inc.setDepartment(staff.getDepartment());
        inc.setEffectiveDate(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());
        inc.setOldBasic(oldBasic);
        inc.setNewBasic(newBasic);
        inc.setOldSpecialAllowance(oldSpl);
        inc.setNewSpecialAllowance(newSpl);
        inc.setIncrementAmount(totalInc);
        inc.setNewGross(staff.getGrossSalary().add(totalInc));
        inc.setRemarks(remarksField.getText());

        try {
            payrollService.applyIncrement(inc);
            showAlert("Applied", "Salary increment applied successfully!", Alert.AlertType.INFORMATION);
            handleClear();
            loadStaffCombo();
            loadIncrements();
        } catch (Exception e) {
            showAlert("Error", "Failed to apply increment: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        datePicker.setValue(LocalDate.now());
        remarksField.clear();
        newBasicField.clear();
        newSplField.clear();
        if (!staffCombo.getItems().isEmpty()) staffCombo.getSelectionModel().selectFirst();
    }

    private BigDecimal parseDecimal(String text) {
        if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(text.trim().replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
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
