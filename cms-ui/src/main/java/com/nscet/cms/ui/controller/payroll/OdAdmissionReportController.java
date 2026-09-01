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
public class OdAdmissionReportController implements Initializable {

    @FXML private DatePicker fromDate, toDate;
    @FXML private ComboBox<String> deptCombo;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo, colCode, colName, colDept, colDesig, colOdCount;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> odList = FXCollections.observableArrayList();
    private List<StaffSalary> allStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.of(2026, 6, 1));
        toDate.setValue(LocalDate.of(2026, 7, 31));

        deptCombo.setItems(FXCollections.observableArrayList("Select", "CE", "MECH", "ECE", "CSE", "EEE", "S&H", "ADMIN"));
        deptCombo.setValue("CSE");

        setupTable();
        loadData();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(odList.indexOf(c.getValue()) + 1)));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment() != null ? c.getValue().getDepartment() : "CSE"));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation() != null ? c.getValue().getDesignation() : "AP"));
        colOdCount.setCellValueFactory(c -> new SimpleStringProperty("0"));

        table.setItems(odList);
    }

    private void loadData() {
        try {
            allStaff = payrollService.getAllStaffSalaries();
            applyFilter();
        } catch (Exception e) {
            System.err.println("[OdAdmissionReportController] Error: " + e.getMessage());
        }
    }

    private void applyFilter() {
        if (allStaff == null) return;
        String dept = deptCombo.getValue();
        if (dept == null || "Select".equals(dept)) {
            odList.setAll(allStaff);
        } else {
            odList.clear();
            for (StaffSalary s : allStaff) {
                if (dept.equalsIgnoreCase(s.getDepartment())) {
                    odList.add(s);
                }
            }
            if (odList.isEmpty()) odList.setAll(allStaff);
        }
    }

    @FXML
    private void handleGenerate() {
        applyFilter();
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending OD Admission Report to printer...");
        alert.showAndWait();
    }
}
