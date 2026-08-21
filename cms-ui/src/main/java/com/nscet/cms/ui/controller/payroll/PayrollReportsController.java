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
public class PayrollReportsController implements Initializable {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> deptCombo;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colCode, colName, colDept, colDesig, colBankAcc, colGross, colEpf, colEsi, colIncomeTax, colProfTax, colNet;

    @FXML private Label totalGrossLabel;
    @FXML private Label totalDeductionLabel;
    @FXML private Label totalNetCreditLabel;

    @Autowired private PayrollService payrollService;

    private ObservableList<StaffSalary> reportData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryCombo.getItems().setAll("ALL", "Teaching", "Non-Teaching", "Officer");
        categoryCombo.getSelectionModel().selectFirst();

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
        colBankAcc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankAccNo() != null ? c.getValue().getBankAccNo() : "N/A"));
        colGross.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getGrossSalary())));
        colEpf.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getEpfDeduction())));
        colEsi.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getEsiDeduction())));
        colIncomeTax.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getIncomeTax())));
        colProfTax.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getProfessionalTax())));
        colNet.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getNetSalary())));

        table.setItems(reportData);
    }

    @FXML
    private void handleGenerate() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            String cat = categoryCombo.getValue();
            String dept = deptCombo.getValue();

            List<StaffSalary> filtered = list.stream().filter(s -> {
                boolean matchCat = "ALL".equalsIgnoreCase(cat) || cat.equalsIgnoreCase(s.getCategory());
                boolean matchDept = "ALL".equalsIgnoreCase(dept) || dept.equalsIgnoreCase(s.getDepartment());
                return matchCat && matchDept;
            }).toList();

            reportData.setAll(filtered);

            BigDecimal gross = BigDecimal.ZERO;
            BigDecimal net = BigDecimal.ZERO;
            for (StaffSalary s : filtered) {
                if (s.getGrossSalary() != null) gross = gross.add(s.getGrossSalary());
                if (s.getNetSalary() != null) net = net.add(s.getNetSalary());
            }
            BigDecimal ded = gross.subtract(net);

            totalGrossLabel.setText("Total Gross: ₹" + String.format("%.2f", gross));
            totalDeductionLabel.setText("Total Deductions: ₹" + String.format("%.2f", ded));
            totalNetCreditLabel.setText("Total Net Credit: ₹" + String.format("%.2f", net));
        } catch (Exception e) {
            System.err.println("[PayrollReportsController] Error: " + e.getMessage());
        }
    }
}
