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

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colCode, colName, colDept, colCategory, colDoj;
    @FXML private TableColumn<StaffSalary, String> colCl, colLop, colAb, colSpl, colOd, colComp, colFdp, colOdAdmis, colOdOthers, colClBalance;

    @Autowired private PayrollService payrollService;

    private ObservableList<StaffSalary> staffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.now().withDayOfMonth(1));
        toDate.setValue(LocalDate.now());

        fromDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) handleView();
        });
        toDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) handleView();
        });

        setupTable();
        handleView();
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory() != null ? c.getValue().getCategory() : "Teaching"));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty("01/08/2023"));

        colCl.setCellValueFactory(c -> {
            int bal = c.getValue().getClBalance() != null ? c.getValue().getClBalance() : 12;
            return new SimpleStringProperty(String.valueOf(Math.max(0, 12 - bal)));
        });

        colLop.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colAb.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colSpl.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOd.setCellValueFactory(c -> new SimpleStringProperty("1"));
        colComp.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colFdp.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOdAdmis.setCellValueFactory(c -> new SimpleStringProperty("2"));
        colOdOthers.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colClBalance.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClBalance() != null ? c.getValue().getClBalance().toString() : "12"));

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
}
