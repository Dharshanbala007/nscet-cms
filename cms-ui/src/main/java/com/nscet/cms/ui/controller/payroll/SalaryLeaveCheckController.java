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
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class SalaryLeaveCheckController implements Initializable {

    @FXML private ComboBox<String> staffCombo;
    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colCode, colName, colDept, colClAvail, colClTaken, colLopCount, colLopDeduction, colStatus;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> staffList = FXCollections.observableArrayList();
    private List<StaffSalary> rawList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colClAvail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClBalance() != null ? c.getValue().getClBalance().toString() : "12"));
        colClTaken.setCellValueFactory(c -> {
            int bal = c.getValue().getClBalance() != null ? c.getValue().getClBalance() : 12;
            return new SimpleStringProperty(String.valueOf(Math.max(0, 12 - bal)));
        });
        colLopCount.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colLopDeduction.setCellValueFactory(c -> new SimpleStringProperty("₹0.00"));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty("VERIFIED"));

        table.setItems(staffList);
    }

    private void loadData() {
        try {
            rawList = payrollService.getAllStaffSalaries();
            staffCombo.getItems().clear();
            staffCombo.getItems().add("ALL STAFF MEMBERS");
            for (StaffSalary s : rawList) {
                staffCombo.getItems().add(s.getStaffCode() + " - " + s.getStaffName());
            }
            staffCombo.getSelectionModel().selectFirst();
            staffList.setAll(rawList);
        } catch (Exception e) {
            System.err.println("[SalaryLeaveCheckController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleVerify() {
        int idx = staffCombo.getSelectionModel().getSelectedIndex();
        if (idx <= 0 || rawList == null) {
            staffList.setAll(rawList);
        } else {
            staffList.setAll(rawList.get(idx - 1));
        }
    }
}
