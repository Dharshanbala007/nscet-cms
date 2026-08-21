package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.AttendanceRecord;
import com.nscet.cms.db.entity.payroll.StaffSalary;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class AttendanceSingleController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> staffCombo;
    @FXML private ComboBox<String> sessionCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField remarksField;

    @Autowired private PayrollService payrollService;

    private List<StaffSalary> staffList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        sessionCombo.getItems().setAll("FULL_DAY", "FORENOON", "AFTERNOON");
        sessionCombo.getSelectionModel().selectFirst();

        typeCombo.getItems().setAll("PRESENT", "LOP", "CL", "OD", "LATE");
        typeCombo.getSelectionModel().selectFirst();

        loadStaffCombo();
    }

    private void loadStaffCombo() {
        try {
            staffList = payrollService.getAllStaffSalaries();
            staffCombo.getItems().clear();
            for (StaffSalary s : staffList) {
                staffCombo.getItems().add(s.getStaffCode() + " - " + s.getStaffName() + " (" + s.getDepartment() + ")");
            }
            if (!staffCombo.getItems().isEmpty()) staffCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.err.println("[AttendanceSingleController] Error loading staff: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        int idx = staffCombo.getSelectionModel().getSelectedIndex();
        if (idx < 0 || staffList == null || idx >= staffList.size()) {
            showAlert("Validation Error", "Please select a staff member.", Alert.AlertType.WARNING);
            return;
        }

        StaffSalary staff = staffList.get(idx);
        AttendanceRecord rec = new AttendanceRecord();
        rec.setAttendanceDate(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());
        rec.setStaffCode(staff.getStaffCode());
        rec.setStaffName(staff.getStaffName());
        rec.setDepartment(staff.getDepartment());
        rec.setSessionType(sessionCombo.getValue());
        rec.setAttendanceType(typeCombo.getValue());
        rec.setRemarks(remarksField.getText());

        try {
            payrollService.saveAttendance(rec);
            showAlert("Saved", "Attendance entry saved for " + staff.getStaffName() + "!", Alert.AlertType.INFORMATION);
            handleClear();
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        datePicker.setValue(LocalDate.now());
        remarksField.clear();
        sessionCombo.getSelectionModel().selectFirst();
        typeCombo.getSelectionModel().selectFirst();
        if (!staffCombo.getItems().isEmpty()) staffCombo.getSelectionModel().selectFirst();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
