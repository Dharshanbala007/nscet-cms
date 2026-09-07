package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.LatePermission;
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
public class LatePermissionController implements Initializable {

    @FXML private ComboBox<String> staffCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField durationField;
    @FXML private TextField reasonField;

    @FXML private TableView<LatePermission> table;
    @FXML private TableColumn<LatePermission, String> colDate, colCode, colName, colType, colDuration, colReason;

    @Autowired private PayrollService payrollService;
    private final ObservableList<LatePermission> list = FXCollections.observableArrayList();
    private List<StaffSalary> staffList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        typeCombo.setItems(FXCollections.observableArrayList("LATE_ENTRY", "EARLY_EXIT", "PERMISSION_1HR", "PERMISSION_2HR"));
        typeCombo.setValue("LATE_ENTRY");

        setupTable();
        loadStaff();
        loadLatePermissions();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });
    }

    private void setupTable() {
        colDate.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPermissionDate() != null ? c.getValue().getPermissionDate().toString() : ""));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStaffCode() != null ? c.getValue().getStaffCode() : ""));
        colName.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStaffName() != null ? c.getValue().getStaffName() : ""));
        colType.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPermissionType() != null ? c.getValue().getPermissionType() : ""));
        colDuration.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDurationMins() != null ? c.getValue().getDurationMins() : "0 Mins"));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getReason() != null ? c.getValue().getReason() : ""));

        table.setItems(list);
    }

    private void loadStaff() {
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
            System.err.println("[LatePermissionController] Error loading staff: " + e.getMessage());
        }
    }

    private void loadLatePermissions() {
        try {
            List<LatePermission> permissions = payrollService.getAllLatePermissions();
            list.setAll(permissions);
        } catch (Exception e) {
            System.err.println("[LatePermissionController] Error loading late/permissions: " + e.getMessage());
        }
    }

    private void populateForm(LatePermission lp) {
        if (lp == null) return;
        if (lp.getPermissionDate() != null) {
            datePicker.setValue(lp.getPermissionDate());
        }
        if (lp.getPermissionType() != null) {
            typeCombo.setValue(lp.getPermissionType());
        }
        if (lp.getDurationMins() != null) {
            durationField.setText(String.valueOf(lp.getDurationMins()));
        } else {
            durationField.clear();
        }
        reasonField.setText(lp.getReason() != null ? lp.getReason() : "");

        if (lp.getStaffCode() != null) {
            String match = lp.getStaffCode() + (lp.getStaffName() != null ? " - " + lp.getStaffName() : "");
            boolean found = false;
            for (String item : staffCombo.getItems()) {
                if (item.startsWith(lp.getStaffCode())) {
                    staffCombo.setValue(item);
                    found = true;
                    break;
                }
            }
            if (!found) {
                staffCombo.setValue(match);
            }
        }
    }

    @FXML
    private void handleAdd() {
        handleClear();
        staffCombo.requestFocus();
    }

    @FXML
    private void handleDelete() {
        LatePermission selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a record to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete record for " + selected.getStaffName() + " on " + selected.getPermissionDate() + "?");
        if (confirm.showAndWait().orElse(null) != ButtonType.OK) {
            return;
        }

        try {
            payrollService.deleteLatePermission(selected.getId());
            loadLatePermissions();
            handleClear();
            showAlert("Deleted", "Record deleted successfully.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Delete Error", "Failed to delete: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        datePicker.setValue(LocalDate.now());
        typeCombo.setValue("LATE_ENTRY");
        durationField.clear();
        reasonField.clear();
        staffCombo.getSelectionModel().clearSelection();
        staffCombo.setValue(null);
        if (!staffCombo.getItems().isEmpty()) {
            staffCombo.getSelectionModel().selectFirst();
        }
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSave() {
        String selectedStaff = staffCombo.getValue();
        if (selectedStaff == null || selectedStaff.trim().isEmpty()) {
            showAlert("Validation Error", "Please select a staff member.", Alert.AlertType.WARNING);
            return;
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            showAlert("Validation Error", "Please choose a valid date.", Alert.AlertType.WARNING);
            return;
        }

        String type = typeCombo.getValue();
        String durationStr = durationField.getText() != null ? durationField.getText().trim() : "";
        Integer duration = 0;
        if (!durationStr.isEmpty()) {
            try {
                duration = Integer.parseInt(durationStr);
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "Duration must be a valid number in minutes.", Alert.AlertType.WARNING);
                return;
            }
        } else {
            if ("PERMISSION_1HR".equals(type)) duration = 60;
            else if ("PERMISSION_2HR".equals(type)) duration = 120;
            else duration = 30;
        }

        String reason = reasonField.getText() != null ? reasonField.getText().trim() : "";

        String staffCode = "";
        String staffName = "";
        int dashIdx = selectedStaff.indexOf(" - ");
        if (dashIdx > 0) {
            staffCode = selectedStaff.substring(0, dashIdx).trim();
            staffName = selectedStaff.substring(dashIdx + 3).trim();
        } else {
            staffCode = selectedStaff.trim();
            staffName = selectedStaff.trim();
        }

        LatePermission lp = new LatePermission();
        lp.setStaffCode(staffCode);
        lp.setStaffName(staffName);
        lp.setPermissionDate(date);
        lp.setPermissionType(type);
        lp.setDurationMins(duration + " Mins");
        lp.setReason(reason);
        lp.setIsActive(true);

        try {
            payrollService.saveLatePermission(lp);
            if (!staffCombo.getItems().contains(selectedStaff.trim())) {
                staffCombo.getItems().add(selectedStaff.trim());
            }
            loadLatePermissions();
            showAlert("Success", "Late / Permission record added successfully!", Alert.AlertType.INFORMATION);
            handleClear();
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save record: " + e.getMessage(), Alert.AlertType.ERROR);
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

