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

    @FXML private RadioButton manualRadio;
    @FXML private RadioButton deviceRadio;
    @FXML private DatePicker datePicker;

    @FXML private RadioButton foreNoonRadio;
    @FXML private RadioButton afterNoonRadio;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private CheckBox allCheck;

    @FXML private TableView<AttendanceRecord> table;
    @FXML private TableColumn<AttendanceRecord, String> colCode;
    @FXML private TableColumn<AttendanceRecord, String> colName;
    @FXML private TableColumn<AttendanceRecord, String> colDesig;
    @FXML private TableColumn<AttendanceRecord, String> colAttendance;

    @Autowired private PayrollService payrollService;

    private ObservableList<AttendanceRecord> attendanceList = FXCollections.observableArrayList();
    private List<StaffSalary> allStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());

        ToggleGroup modeGroup = new ToggleGroup();
        manualRadio.setToggleGroup(modeGroup);
        deviceRadio.setToggleGroup(modeGroup);

        ToggleGroup sessionGroup = new ToggleGroup();
        foreNoonRadio.setToggleGroup(sessionGroup);
        afterNoonRadio.setToggleGroup(sessionGroup);

        categoryCombo.setItems(FXCollections.observableArrayList("Select", "Teaching", "Contract", "NT-Tech", "NT-Non Tech", "Officer"));
        categoryCombo.setValue("Teaching");

        setupTable();
        loadAttendanceData();

        categoryCombo.setOnAction(e -> handleView());
        allCheck.setOnAction(e -> handleView());
    }

    private void setupTable() {
        table.setEditable(true);
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment() != null ? c.getValue().getDepartment() + " - AP" : "Staff"));

        colAttendance.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAttendanceType() != null ? c.getValue().getAttendanceType() : "P"));
        colAttendance.setCellFactory(ComboBoxTableCell.forTableColumn("P", "LOP", "CL", "OD", "OHP", "AB", "ML", "CPL"));
        colAttendance.setOnEditCommit(e -> e.getRowValue().setAttendanceType(e.getNewValue()));

        table.setItems(attendanceList);
    }

    private void loadAttendanceData() {
        LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
        String session = foreNoonRadio.isSelected() ? "FORENOON" : "AFTERNOON";
        String selectedCategory = categoryCombo.getValue();
        boolean showAll = allCheck.isSelected();

        try {
            allStaff = payrollService.getAllStaffSalaries();
            List<AttendanceRecord> existing = payrollService.getAttendanceByDate(date);
            java.util.Map<String, AttendanceRecord> existingMap = new java.util.HashMap<>();
            for (AttendanceRecord r : existing) {
                if (r.getStaffCode() != null) existingMap.put(r.getStaffCode(), r);
            }

            attendanceList.clear();
            for (StaffSalary s : allStaff) {
                if (!showAll && selectedCategory != null && !"Select".equals(selectedCategory)
                        && s.getCategory() != null && !s.getCategory().equalsIgnoreCase(selectedCategory)) {
                    continue;
                }

                if (existingMap.containsKey(s.getStaffCode())) {
                    attendanceList.add(existingMap.get(s.getStaffCode()));
                } else {
                    AttendanceRecord rec = new AttendanceRecord();
                    rec.setAttendanceDate(date);
                    rec.setStaffCode(s.getStaffCode());
                    rec.setStaffName(s.getStaffName());
                    rec.setDepartment(s.getDepartment());
                    rec.setSessionType(session);
                    rec.setAttendanceType("P");
                    attendanceList.add(rec);
                }
            }
        } catch (Exception e) {
            System.err.println("[AttendanceEntryController] Error loading attendance: " + e.getMessage());
        }
    }

    @FXML
    private void handleView() {
        loadAttendanceData();
    }

    @FXML
    private void handleSaveAll() {
        LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
        try {
            for (AttendanceRecord rec : attendanceList) {
                rec.setAttendanceDate(date);
                payrollService.saveAttendance(rec);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saved");
            alert.setHeaderText(null);
            alert.setContentText("Attendance records saved successfully for " + date + "!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save attendance: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClose() {
        table.getItems().clear();
    }
}
