package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.StaffSalary;
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
public class PayslipPrintController implements Initializable {

    @FXML private ComboBox<String> staffCombo;
    @FXML private ComboBox<String> monthCombo;

    @FXML private Label payslipMonthTitle;
    @FXML private Label lblEmpName, lblEmpCode, lblDesig, lblDept, lblBankName, lblBankAcc;
    @FXML private Label lblBasic, lblEpf, lblSpl, lblEsi, lblHra, lblIT, lblWashing, lblPT, lblConveyance, lblOthers;
    @FXML private Label lblGrossPay, lblTotalDeduction, lblNetPay;

    @Autowired private PayrollService payrollService;

    private List<StaffSalary> staffList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthCombo.getItems().setAll("Jul-2026", "Jun-2026", "May-2026", "Apr-2026", "Mar-2026");
        monthCombo.getSelectionModel().selectFirst();

        loadStaffCombo();
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
                handleGenerate();
            }
        } catch (Exception e) {
            System.err.println("[PayslipPrintController] Error loading staff combo: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerate() {
        int idx = staffCombo.getSelectionModel().getSelectedIndex();
        if (idx < 0 || staffList == null || idx >= staffList.size()) return;

        StaffSalary s = staffList.get(idx);
        String month = monthCombo.getValue() != null ? monthCombo.getValue() : "Jul-2026";
        payslipMonthTitle.setText("PAYSLIP FOR THE MONTH OF " + month.toUpperCase());

        lblEmpName.setText(s.getStaffName());
        lblEmpCode.setText(s.getStaffCode());
        lblDesig.setText(s.getDesignation());
        lblDept.setText(s.getDepartment());
        lblBankName.setText(s.getBankName() != null ? s.getBankName() : "Federal Bank");
        lblBankAcc.setText(s.getBankAccNo() != null ? s.getBankAccNo() : "N/A");

        lblBasic.setText(String.format("₹%,.2f", s.getBasicPay()));
        lblSpl.setText(String.format("₹%,.2f", s.getSpecialAllowance()));
        lblHra.setText(String.format("₹%,.2f", s.getHra()));
        lblWashing.setText(String.format("₹%,.2f", s.getWashingAllowance()));
        lblConveyance.setText(String.format("₹%,.2f", s.getConveyance()));

        lblEpf.setText(String.format("₹%,.2f", s.getEpfDeduction()));
        lblEsi.setText(String.format("₹%,.2f", s.getEsiDeduction()));
        lblIT.setText(String.format("₹%,.2f", s.getIncomeTax()));
        lblPT.setText(String.format("₹%,.2f", s.getProfessionalTax()));
        lblOthers.setText(String.format("₹%,.2f", s.getStaffClub()));

        BigDecimal gross = s.getGrossSalary() != null ? s.getGrossSalary() : BigDecimal.ZERO;
        BigDecimal net = s.getNetSalary() != null ? s.getNetSalary() : BigDecimal.ZERO;
        BigDecimal ded = gross.subtract(net);

        lblGrossPay.setText(String.format("₹%,.2f", gross));
        lblTotalDeduction.setText(String.format("₹%,.2f", ded));
        lblNetPay.setText(String.format("₹%,.2f", net));
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Payslip");
        alert.setHeaderText(null);
        alert.setContentText("Sending Payslip for " + lblEmpName.getText() + " to printer...");
        alert.showAndWait();
    }
}
