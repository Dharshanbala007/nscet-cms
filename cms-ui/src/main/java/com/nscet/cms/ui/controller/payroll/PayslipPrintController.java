package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.StaffSalary;
import javafx.collections.FXCollections;
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
public class PayslipPrintController implements Initializable {

    @FXML private ComboBox<String> staffCombo;
    @FXML private ComboBox<String> monthCombo;

    @FXML private Label lblMonthTitle;
    @FXML private Label lblEmpName, lblUanNo, lblDesig, lblDoj, lblBankAcc;
    @FXML private Label lblBasic, lblSpl, lblHra, lblConveyance, lblWashing, lblGrossPay;
    @FXML private Label lblEpf, lblEsi, lblLopDays, lblLopAmt, lblIncomeTax, lblStaffClub, lblProfTax, lblOthers, lblTotalDeduction;
    @FXML private Label lblNetPay, lblInWords;

    @Autowired private PayrollService payrollService;
    private List<StaffSalary> rawStaffList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthCombo.setItems(FXCollections.observableArrayList("Jul-26", "Aug-26", "Jun-26", "May-26"));
        monthCombo.setValue("Jul-26");

        loadStaffCombo();
    }

    private void loadStaffCombo() {
        try {
            rawStaffList = payrollService.getAllStaffSalaries();
            staffCombo.getItems().clear();
            for (StaffSalary s : rawStaffList) {
                staffCombo.getItems().add(s.getStaffCode() + " - " + s.getStaffName());
            }

            if (!staffCombo.getItems().isEmpty()) {
                staffCombo.getSelectionModel().selectFirst();
                populatePayslip(0);
            }

            staffCombo.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
                if (newIdx != null && newIdx.intValue() >= 0 && newIdx.intValue() < rawStaffList.size()) {
                    populatePayslip(newIdx.intValue());
                }
            });
        } catch (Exception e) {
            System.err.println("[PayslipPrintController] Error loading staff combo: " + e.getMessage());
        }
    }

    private void populatePayslip(int index) {
        StaffSalary s = rawStaffList.get(index);

        lblMonthTitle.setText(monthCombo.getValue() != null ? monthCombo.getValue() : "Jul-26");
        lblEmpName.setText(s.getStaffName());
        lblUanNo.setText("100498744032");
        lblDesig.setText(s.getDesignation() != null ? s.getDesignation() : "H.O.D - MECH");
        lblDoj.setText("1-Mar-2021");
        lblBankAcc.setText(s.getBankAccNo() != null ? s.getBankAccNo() : "14620100099406");

        lblBasic.setText(s.getBasicPay() != null ? s.getBasicPay().toPlainString() : "15000.00");
        lblSpl.setText(s.getSpecialAllowance() != null ? s.getSpecialAllowance().toPlainString() : "21000.00");
        lblHra.setText(s.getHra() != null ? s.getHra().toPlainString() : "12000.00");
        lblConveyance.setText(s.getConveyance() != null ? s.getConveyance().toPlainString() : "6000.00");
        lblWashing.setText(s.getWashingAllowance() != null ? s.getWashingAllowance().toPlainString() : "6000.00");
        lblGrossPay.setText(s.getGrossSalary() != null ? s.getGrossSalary().toPlainString() : "60000.00");

        lblEpf.setText(s.getEpfDeduction() != null ? s.getEpfDeduction().toPlainString() : "1800.00");
        lblEsi.setText("0.00");
        lblLopDays.setText("0");
        lblLopAmt.setText("0.00");
        lblIncomeTax.setText("0.00");
        lblStaffClub.setText("200.00");
        lblProfTax.setText("0.00");
        lblOthers.setText("0.00");
        lblTotalDeduction.setText("2000.00");

        lblNetPay.setText(s.getNetSalary() != null ? s.getNetSalary().toPlainString() : "58000.00");
        lblInWords.setText("Rupees - Fifty-Eight Thousand Only");
    }

    @FXML
    private void handleGenerate() {
        int idx = staffCombo.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && rawStaffList != null && idx < rawStaffList.size()) {
            populatePayslip(idx);
        }
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Payslip");
        alert.setHeaderText(null);
        alert.setContentText("Sending Payslip to printer...");
        alert.showAndWait();
    }
}
