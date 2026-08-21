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
    @FXML private TableColumn<StaffSalary, String> colCode, colName, colDept, colDesig, colDoj, colOdCount, colRemarks;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> reportData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.now().withDayOfMonth(1));
        toDate.setValue(LocalDate.now());

        deptCombo.getItems().setAll("ALL", "COMPUTER SCIENCE", "ELECTRONICS", "MECHANICAL", "CIVIL", "ADMIN");
        deptCombo.getSelectionModel().selectFirst();

        setupTable();
        handleGenerate();
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation()));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty("01/08/2023"));
        colOdCount.setCellValueFactory(c -> new SimpleStringProperty("2"));
        colRemarks.setCellValueFactory(c -> new SimpleStringProperty("Admission Counselling Duty / Campus Verification"));

        table.setItems(reportData);
    }

    @FXML
    private void handleGenerate() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            String dept = deptCombo.getValue();
            List<StaffSalary> filtered = list.stream().filter(s -> "ALL".equalsIgnoreCase(dept) || dept.equalsIgnoreCase(s.getDepartment())).toList();
            reportData.setAll(filtered);
        } catch (Exception e) {
            System.err.println("[OdAdmissionReportController] Error: " + e.getMessage());
        }
    }
}
