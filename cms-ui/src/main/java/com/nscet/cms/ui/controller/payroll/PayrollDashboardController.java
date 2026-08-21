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

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class PayrollDashboardController implements Initializable {

    @FXML private Label totalStaffLabel;
    @FXML private Label grossPayrollLabel;
    @FXML private Label deductionsLabel;
    @FXML private Label netPayrollLabel;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colCode;
    @FXML private TableColumn<StaffSalary, String> colName;
    @FXML private TableColumn<StaffSalary, String> colDept;
    @FXML private TableColumn<StaffSalary, String> colDesig;
    @FXML private TableColumn<StaffSalary, String> colBasic;
    @FXML private TableColumn<StaffSalary, String> colGross;
    @FXML private TableColumn<StaffSalary, String> colDeductions;
    @FXML private TableColumn<StaffSalary, String> colNet;
    @FXML private TableColumn<StaffSalary, String> colBank;

    @Autowired private PayrollService payrollService;

    private ObservableList<StaffSalary> staffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadDashboardData();
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation()));
        colBasic.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getBasicPay())));
        colGross.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getGrossSalary())));
        
        colDeductions.setCellValueFactory(c -> {
            BigDecimal ded = c.getValue().getEpfDeduction()
                    .add(c.getValue().getEsiDeduction())
                    .add(c.getValue().getIncomeTax())
                    .add(c.getValue().getProfessionalTax())
                    .add(c.getValue().getStaffClub());
            return new SimpleStringProperty("₹" + String.format("%.2f", ded));
        });
        
        colNet.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getNetSalary())));
        colBank.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankAccNo() != null ? c.getValue().getBankAccNo() : "N/A"));

        table.setItems(staffList);
    }

    private void loadDashboardData() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            staffList.setAll(list);

            int count = list.size();
            BigDecimal gross = BigDecimal.ZERO;
            BigDecimal net = BigDecimal.ZERO;

            for (StaffSalary s : list) {
                if (s.getGrossSalary() != null) gross = gross.add(s.getGrossSalary());
                if (s.getNetSalary() != null) net = net.add(s.getNetSalary());
            }
            BigDecimal deductions = gross.subtract(net);

            totalStaffLabel.setText(String.valueOf(count));
            grossPayrollLabel.setText("₹" + String.format("%.2f", gross));
            deductionsLabel.setText("₹" + String.format("%.2f", deductions));
            netPayrollLabel.setText("₹" + String.format("%.2f", net));
        } catch (Exception e) {
            System.err.println("[PayrollDashboard] Error loading data: " + e.getMessage());
        }
    }
}
