package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.AttendanceRecord;
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

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class AttendanceSingleController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> staffCombo;
    @FXML private TextField categoryField;
    @FXML private TextField departmentField;
    @FXML private ComboBox<String> sessionCombo;
    @FXML private ComboBox<String> typeCombo;

    @FXML private TableView<AttendanceRecord> table;
    @FXML private TableColumn<AttendanceRecord, String> colDate, colCode, colName, colCategory, colDept, colSession, colType;

    @Autowired private PayrollService payrollService;

    private List<StaffSalary> staffList;
    private ObservableList<AttendanceRecord> recordsList = FXCollections.observableArrayList();
    private AttendanceRecord selectedRecord;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());

        sessionCombo.setItems(FXCollections.observableArrayList("Select", "Full Day", "Morning", "Evening"));
        sessionCombo.setValue("Select");

        typeCombo.setItems(FXCollections.observableArrayList("Select", "LOP", "CL", "ML", "SPL", "OD", "HL", "AB", "CPL"));
        typeCombo.setValue("Select");

        setupTable();
        loadStaffCombo();
        loadAttendanceRecords();

        staffCombo.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            if (newIdx != null && newIdx.intValue() >= 0 && staffList != null && newIdx.intValue() < staffList.size()) {
                StaffSalary s = staffList.get(newIdx.intValue());
                categoryField.setText(s.getCategory() != null ? s.getCategory() : "Teaching");
                departmentField.setText(s.getDepartment() != null ? s.getDepartment() : "");
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedRecord = newSel;
                populateForm(newSel);
            }
        });
    }

    private void setupTable() {
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAttendanceDate() != null ? c.getValue().getAttendanceDate().toString() : ""));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty("Teaching"));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colSession.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSessionType() != null ? c.getValue().getSessionType() : "Full Day"));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAttendanceType() != null ? c.getValue().getAttendanceType() : "P"));

        table.setItems(recordsList);
    }

    private void loadStaffCombo() {
        try {
            staffList = payrollService.getAllStaffSalaries();
            staffCombo.getItems().clear();
            for (StaffSalary s : staffList) {
                staffCombo.getItems().add(s.getStaffCode() + " - " + s.getStaffName());
            }
            if (!staffCombo.getItems().isEmpty()) {
                staffCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.err.println("[AttendanceSingleController] Error loading staff: " + e.getMessage());
        }
    }

    private void loadAttendanceRecords() {
        try {
            LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
            List<AttendanceRecord> list = payrollService.getAttendanceByDate(date);
            recordsList.setAll(list);
        } catch (Exception e) {
            System.err.println("[AttendanceSingleController] Error loading records: " + e.getMessage());
        }
    }

    private void populateForm(AttendanceRecord rec) {
        if (rec.getAttendanceDate() != null) datePicker.setValue(rec.getAttendanceDate());
        categoryField.setText("Teaching");
        departmentField.setText(rec.getDepartment() != null ? rec.getDepartment() : "");
        sessionCombo.setValue(rec.getSessionType() != null ? rec.getSessionType() : "Select");
        typeCombo.setValue(rec.getAttendanceType() != null ? rec.getAttendanceType() : "Select");

        for (int i = 0; i < staffCombo.getItems().size(); i++) {
            if (staffCombo.getItems().get(i).startsWith(rec.getStaffCode())) {
                staffCombo.getSelectionModel().select(i);
                break;
            }
        }
    }

    @FXML
    private void handleAdd() {
        handleCancel();
    }

    @FXML
    private void handleModify() {
        if (selectedRecord == null) {
            showAlert("Selection Required", "Please select a record from the table to modify.", Alert.AlertType.WARNING);
            return;
        }
        handleSave();
    }

    @FXML
    private void handleDelete() {
        if (selectedRecord == null) {
            showAlert("Selection Required", "Please select a record from the table to delete.", Alert.AlertType.WARNING);
            return;
        }
        recordsList.remove(selectedRecord);
        handleCancel();
        showAlert("Deleted", "Attendance record deleted.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleSave() {
        int idx = staffCombo.getSelectionModel().getSelectedIndex();
        if (idx < 0 || staffList == null || idx >= staffList.size()) {
            showAlert("Validation Error", "Please select a staff member.", Alert.AlertType.WARNING);
            return;
        }

        StaffSalary staff = staffList.get(idx);
        AttendanceRecord rec = selectedRecord != null ? selectedRecord : new AttendanceRecord();
        rec.setAttendanceDate(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());
        rec.setStaffCode(staff.getStaffCode());
        rec.setStaffName(staff.getStaffName());
        rec.setDepartment(departmentField.getText());
        rec.setSessionType(sessionCombo.getValue() != null ? sessionCombo.getValue() : "Full Day");
        rec.setAttendanceType(typeCombo.getValue() != null ? typeCombo.getValue() : "P");

        try {
            payrollService.saveAttendance(rec);
            showAlert("Saved", "Single attendance entry saved successfully!", Alert.AlertType.INFORMATION);
            handleCancel();
            loadAttendanceRecords();
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save attendance: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        selectedRecord = null;
        datePicker.setValue(LocalDate.now());
        categoryField.clear();
        departmentField.clear();
        sessionCombo.setValue("Select");
        typeCombo.setValue("Select");
        if (!staffCombo.getItems().isEmpty()) staffCombo.getSelectionModel().selectFirst();
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClose() {
        handleCancel();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
