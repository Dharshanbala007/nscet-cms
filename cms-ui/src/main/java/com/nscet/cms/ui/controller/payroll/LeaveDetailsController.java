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
public class LeaveDetailsController implements Initializable {

    @FXML private DatePicker fromDate, toDate;
    @FXML private ComboBox<String> categoryCombo;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo, colName, colDept, colClAllowed, colClTaken, colClDate, colClBal;
    @FXML private TableColumn<StaffSalary, String> colLopLastMonth, colLop, colLopDate, colTotalLop, colMl, colMlDate, colSpl, colSplDate, colOd, colOdDate;

    @Autowired private PayrollService payrollService;

    private ObservableList<StaffSalary> staffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.now().withDayOfMonth(1));
        toDate.setValue(LocalDate.now());

        categoryCombo.setItems(FXCollections.observableArrayList("Select", "Regular", "New Emp Last Month", "Contract Emp", "Relived Emp"));
        categoryCombo.setValue("Regular");

        setupTable();
        handleView();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(staffList.indexOf(c.getValue()) + 1)));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colClAllowed.setCellValueFactory(c -> new SimpleStringProperty("10"));

        colClTaken.setCellValueFactory(c -> {
            int bal = c.getValue().getClBalance() != null ? c.getValue().getClBalance() : 10;
            return new SimpleStringProperty(String.valueOf(Math.max(0, 10 - bal)));
        });
        colClDate.setCellValueFactory(c -> new SimpleStringProperty("{28/07/2026}"));
        colClBal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClBalance() != null ? c.getValue().getClBalance().toString() : "9"));
        colLopLastMonth.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colLop.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colLopDate.setCellValueFactory(c -> new SimpleStringProperty("-"));
        colTotalLop.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colMl.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colMlDate.setCellValueFactory(c -> new SimpleStringProperty("-"));
        colSpl.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colSplDate.setCellValueFactory(c -> new SimpleStringProperty("{18/06/2026, 19/06/2026}"));
        colOd.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOdDate.setCellValueFactory(c -> new SimpleStringProperty("-"));

        table.setItems(staffList);
    }

    @FXML
    private void handleView() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            java.util.Map<String, StaffSalary> uniqueMap = new java.util.LinkedHashMap<>();
            for (StaffSalary s : list) {
                if (s.getStaffCode() != null && !uniqueMap.containsKey(s.getStaffCode())) {
                    uniqueMap.put(s.getStaffCode(), s);
                }
            }
            staffList.setAll(uniqueMap.values());
        } catch (Exception e) {
            System.err.println("[LeaveDetailsController] Error loading leave details: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        showAlert("Saved", "Leave details saved successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handlePrint() {
        showAlert("Print Report", "Sending Leave Details Report to printer...", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleCommit() {
        showAlert("Committed", "Leave details committed for period.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
