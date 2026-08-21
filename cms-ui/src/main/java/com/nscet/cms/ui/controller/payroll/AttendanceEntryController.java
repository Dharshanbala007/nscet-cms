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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class AttendanceEntryController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> sessionCombo;

    @FXML private TableView<AttendanceRecord> table;
    @FXML private TableColumn<AttendanceRecord, String> colCode;
    @FXML private TableColumn<AttendanceRecord, String> colName;
    @FXML private TableColumn<AttendanceRecord, String> colDept;
    @FXML private TableColumn<AttendanceRecord, String> colDesig;
    @FXML private TableColumn<AttendanceRecord, String> colStatus;
    @FXML private TableColumn<AttendanceRecord, String> colRemarks;

    @Autowired private PayrollService payrollService;

    private ObservableList<AttendanceRecord> attendanceList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        sessionCombo.getItems().setAll("FULL_DAY", "FORENOON", "AFTERNOON");
        sessionCombo.getSelectionModel().selectFirst();

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) handleLoadStaff();
        });

        setupTable();
        handleLoadStaff();
    }

    private void setupTable() {
        table.setEditable(true);
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty("Active Staff"));

        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAttendanceType()));
        colStatus.setCellFactory(ComboBoxTableCell.forTableColumn("PRESENT", "LOP", "CL", "OD", "LATE"));
        colStatus.setOnEditCommit(e -> e.getRowValue().setAttendanceType(e.getNewValue()));

        colRemarks.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks() != null ? c.getValue().getRemarks() : ""));
        colRemarks.setCellFactory(TextFieldTableCell.forTableColumn());
        colRemarks.setOnEditCommit(e -> e.getRowValue().setRemarks(e.getNewValue()));

        table.setItems(attendanceList);
    }

    @FXML
    private void handleLoadStaff() {
        LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
        String session = sessionCombo.getValue() != null ? sessionCombo.getValue() : "FULL_DAY";

        try {
            List<AttendanceRecord> existing = payrollService.getAttendanceByDate(date);
            java.util.Map<String, AttendanceRecord> uniqueAttendance = new java.util.LinkedHashMap<>();
            for (AttendanceRecord r : existing) {
                if (r.getStaffCode() != null && !uniqueAttendance.containsKey(r.getStaffCode())) {
                    uniqueAttendance.put(r.getStaffCode(), r);
                }
            }

            if (!uniqueAttendance.isEmpty()) {
                attendanceList.setAll(uniqueAttendance.values());
            } else {
                List<StaffSalary> staffList = payrollService.getAllStaffSalaries();
                java.util.Map<String, StaffSalary> uniqueStaff = new java.util.LinkedHashMap<>();
                for (StaffSalary s : staffList) {
                    if (s.getStaffCode() != null && !uniqueStaff.containsKey(s.getStaffCode())) {
                        uniqueStaff.put(s.getStaffCode(), s);
                    }
                }

                attendanceList.clear();
                for (StaffSalary s : uniqueStaff.values()) {
                    AttendanceRecord rec = new AttendanceRecord();
                    rec.setAttendanceDate(date);
                    rec.setStaffCode(s.getStaffCode());
                    rec.setStaffName(s.getStaffName());
                    rec.setDepartment(s.getDepartment());
                    rec.setSessionType(session);
                    rec.setAttendanceType("PRESENT");
                    rec.setRemarks("On time");
                    attendanceList.add(rec);
                }
            }
        } catch (Exception e) {
            System.err.println("[AttendanceEntryController] Error loading staff: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveAll() {
        try {
            for (AttendanceRecord rec : attendanceList) {
                payrollService.saveAttendance(rec);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Attendance records saved successfully for " + datePicker.getValue() + "!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save attendance: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
