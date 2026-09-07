package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.StaffSalary;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class AddPayrollEntryController implements Initializable {

    @FXML private ComboBox<String> staffCombo;
    @FXML private DatePicker dojPicker;
    @FXML private TextField nameField;
    @FXML private TextField codeField;
    @FXML private TextField desigField;
    @FXML private TextField deptField;

    @FXML private TextField basicPayField;
    @FXML private TextField lopDaysField;
    @FXML private TextField hraField;
    @FXML private TextField lopAmtField;
    @FXML private TextField splAllowanceField;
    @FXML private TextField incomeTaxField;
    @FXML private TextField washingField;
    @FXML private TextField epfField;
    @FXML private TextField conveyanceField;

    @FXML private Label lblGrossPay;
    @FXML private Label lblTotalDeduct;
    @FXML private Label lblNetPay;

    @Autowired private PayrollService payrollService;

    private List<StaffSalary> existingStaffList;
    private boolean confirmed = false;
    private StaffSalary resultSalary;
    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dojPicker.setValue(LocalDate.now());

        setupListeners();
        loadStaff();
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public StaffSalary getResultSalary() {
        return resultSalary;
    }

    private void setupListeners() {
        TextField[] numericFields = {
                basicPayField, hraField, splAllowanceField, washingField, conveyanceField,
                lopDaysField, lopAmtField, incomeTaxField, epfField
        };

        for (TextField tf : numericFields) {
            tf.textProperty().addListener((obs, oldVal, newVal) -> recalculate());
        }

        lopDaysField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int days = Integer.parseInt(newVal.trim());
                BigDecimal basic = parseDecimal(basicPayField.getText());
                if (days > 0 && basic.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal dailyRate = basic.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
                    BigDecimal lopDeduct = dailyRate.multiply(BigDecimal.valueOf(days));
                    lopAmtField.setText(lopDeduct.setScale(0, RoundingMode.HALF_UP).toPlainString());
                } else if (days == 0) {
                    lopAmtField.setText("0");
                }
            } catch (Exception ignored) {}
        });

        staffCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && existingStaffList != null) {
                for (StaffSalary s : existingStaffList) {
                    String comboLabel = s.getStaffCode() + " - " + s.getStaffName();
                    if (comboLabel.equals(newVal)) {
                        populateFromExistingStaff(s);
                        break;
                    }
                }
            }
        });
    }

    private void loadStaff() {
        try {
            existingStaffList = payrollService.getAllStaffSalaries();
            staffCombo.getItems().clear();
            for (StaffSalary s : existingStaffList) {
                staffCombo.getItems().add(s.getStaffCode() + " - " + s.getStaffName());
            }
        } catch (Exception e) {
            System.err.println("[AddPayrollEntryController] Error loading staff: " + e.getMessage());
        }
    }

    private void populateFromExistingStaff(StaffSalary s) {
        codeField.setText(s.getStaffCode() != null ? s.getStaffCode() : "");
        nameField.setText(s.getStaffName() != null ? s.getStaffName() : "");
        desigField.setText(s.getDesignation() != null ? s.getDesignation() : "");
        deptField.setText(s.getDepartment() != null ? s.getDepartment() : "");

        basicPayField.setText(s.getBasicPay() != null ? s.getBasicPay().toPlainString() : "0");
        hraField.setText(s.getHra() != null ? s.getHra().toPlainString() : "0");
        splAllowanceField.setText(s.getSpecialAllowance() != null ? s.getSpecialAllowance().toPlainString() : "0");
        washingField.setText(s.getWashingAllowance() != null ? s.getWashingAllowance().toPlainString() : "0");
        conveyanceField.setText(s.getConveyance() != null ? s.getConveyance().toPlainString() : "0");

        incomeTaxField.setText(s.getIncomeTax() != null ? s.getIncomeTax().toPlainString() : "0");
        epfField.setText(s.getEpfDeduction() != null ? s.getEpfDeduction().toPlainString() : "0");
        lopDaysField.setText("0");
        lopAmtField.setText("0");

        recalculate();
    }

    private void recalculate() {
        BigDecimal basic = parseDecimal(basicPayField.getText());
        BigDecimal hra = parseDecimal(hraField.getText());
        BigDecimal spl = parseDecimal(splAllowanceField.getText());
        BigDecimal washing = parseDecimal(washingField.getText());
        BigDecimal conveyance = parseDecimal(conveyanceField.getText());

        BigDecimal gross = basic.add(hra).add(spl).add(washing).add(conveyance);

        BigDecimal lopAmt = parseDecimal(lopAmtField.getText());
        BigDecimal tax = parseDecimal(incomeTaxField.getText());
        BigDecimal epf = parseDecimal(epfField.getText());

        BigDecimal totalDeduct = lopAmt.add(tax).add(epf);
        BigDecimal net = gross.subtract(totalDeduct);

        lblGrossPay.setText(String.format("Rs. %,.2f", gross.doubleValue()));
        lblTotalDeduct.setText(String.format("Rs. %,.2f", totalDeduct.doubleValue()));
        lblNetPay.setText(String.format("Rs. %,.2f", net.doubleValue()));
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String code = codeField.getText() != null ? codeField.getText().trim() : "";

        if (name.isEmpty() || code.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText(null);
            alert.setContentText("Staff Code and Staff Name are required.");
            alert.showAndWait();
            return;
        }

        BigDecimal basic = parseDecimal(basicPayField.getText());
        BigDecimal hra = parseDecimal(hraField.getText());
        BigDecimal spl = parseDecimal(splAllowanceField.getText());
        BigDecimal washing = parseDecimal(washingField.getText());
        BigDecimal conveyance = parseDecimal(conveyanceField.getText());
        BigDecimal gross = basic.add(hra).add(spl).add(washing).add(conveyance);

        BigDecimal tax = parseDecimal(incomeTaxField.getText());
        BigDecimal epf = parseDecimal(epfField.getText());
        BigDecimal lopAmt = parseDecimal(lopAmtField.getText());
        BigDecimal totalDeduct = tax.add(epf).add(lopAmt);
        BigDecimal net = gross.subtract(totalDeduct);

        StaffSalary s = new StaffSalary();
        s.setStaffCode(code);
        s.setStaffName(name);
        s.setDesignation(desigField.getText() != null ? desigField.getText().trim() : "");
        s.setDepartment(deptField.getText() != null ? deptField.getText().trim() : "");
        s.setBasicPay(basic);
        s.setHra(hra);
        s.setSpecialAllowance(spl);
        s.setWashingAllowance(washing);
        s.setConveyance(conveyance);
        s.setGrossSalary(gross);
        s.setIncomeTax(tax);
        s.setEpfDeduction(epf);
        s.setNetSalary(net);
        s.setIsActive(true);

        LocalDate doj = dojPicker.getValue() != null ? dojPicker.getValue() : LocalDate.now();
        s.setCreatedAt(doj.atStartOfDay());

        this.resultSalary = s;
        this.confirmed = true;

        closeStage();
    }

    @FXML
    private void handleCancel() {
        this.confirmed = false;
        closeStage();
    }

    private void closeStage() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private BigDecimal parseDecimal(String text) {
        if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(text.trim().replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
