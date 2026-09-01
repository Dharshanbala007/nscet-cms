package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
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
public class StaffTransferController implements Initializable {

    @FXML private ComboBox<String> staffCombo;
    @FXML private DatePicker transferDatePicker;
    @FXML private TextField currentDeptField;
    @FXML private ComboBox<String> newDeptCombo;
    @FXML private TextField currentDesigField;
    @FXML private TextField newDesigField;
    @FXML private TextField orderNoField;
    @FXML private TextField remarksField;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colDate, colCode, colName, colFromDept, colToDept, colNewDesig, colOrderNo;

    @Autowired private PayrollService payrollService;

    private ObservableList<StaffSalary> transferList = FXCollections.observableArrayList();
    private List<StaffSalary> staffList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transferDatePicker.setValue(LocalDate.now());
        newDeptCombo.setItems(FXCollections.observableArrayList("CSE", "ECE", "EEE", "MECH", "CIVIL", "IT", "S&H", "ADMIN"));
        setupTable();
        loadStaffCombo();
    }

    private void setupTable() {
        colDate.setCellValueFactory(c -> new SimpleStringProperty(LocalDate.now().toString()));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colFromDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colToDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colNewDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation()));
        colOrderNo.setCellValueFactory(c -> new SimpleStringProperty("TR-" + c.getValue().getStaffCode()));

        table.setItems(transferList);
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
                updateFields(0);
            }

            staffCombo.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
                if (newIdx != null && newIdx.intValue() >= 0 && newIdx.intValue() < staffList.size()) {
                    updateFields(newIdx.intValue());
                }
            });
            transferList.setAll(staffList);
        } catch (Exception e) {
            System.err.println("[StaffTransferController] Error: " + e.getMessage());
        }
    }

    private void updateFields(int index) {
        StaffSalary s = staffList.get(index);
        currentDeptField.setText(s.getDepartment() != null ? s.getDepartment() : "");
        currentDesigField.setText(s.getDesignation() != null ? s.getDesignation() : "");
    }

    @FXML
    private void handleClear() {
        transferDatePicker.setValue(LocalDate.now());
        newDesigField.clear();
        orderNoField.clear();
        remarksField.clear();
    }

    @FXML
    private void handleSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Staff Transfer");
        alert.setHeaderText(null);
        alert.setContentText("Staff transfer record updated successfully!");
        alert.showAndWait();
    }
}
