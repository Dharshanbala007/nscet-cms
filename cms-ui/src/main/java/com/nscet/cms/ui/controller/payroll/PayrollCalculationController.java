package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.MonthlyPayrollRun;
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
public class PayrollCalculationController implements Initializable {

    @FXML private ComboBox<String> payPeriodCombo;
    @FXML private TextField workingDaysField;
    @FXML private Label periodSummaryLabel;
    @FXML private Label totalNetLabel;

    @FXML private TableView<MonthlyPayrollRun> table;
    @FXML private TableColumn<MonthlyPayrollRun, String> colCode, colName, colDept, colWorkingDays, colPaidDays, colLopDays, colBasic, colSpl, colGross, colDeductions, colNet;

    @Autowired private PayrollService payrollService;

    private ObservableList<MonthlyPayrollRun> runList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        payPeriodCombo.getItems().setAll("Jul-2026", "Jun-2026", "May-2026", "Apr-2026", "Mar-2026");
        payPeriodCombo.getSelectionModel().selectFirst();

        payPeriodCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) handleCalculate();
        });

        setupTable();
        handleCalculate();
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colWorkingDays.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWorkingDays() != null ? c.getValue().getWorkingDays().toString() : "30"));
        colPaidDays.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaidDays() != null ? c.getValue().getPaidDays().toString() : "30"));
        colLopDays.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLopDays() != null ? c.getValue().getLopDays().toString() : "0"));
        colBasic.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getBasicPay())));
        colSpl.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getSpecialAllowance())));
        colGross.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getGrossPay())));
        colDeductions.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getTotalDeductions())));
        colNet.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getNetPay())));

        table.setItems(runList);
    }

    @FXML
    private void handleCalculate() {
        String period = payPeriodCombo.getValue() != null ? payPeriodCombo.getValue() : "Jul-2026";
        int days = 30;
        try {
            days = Integer.parseInt(workingDaysField.getText().trim());
        } catch (Exception e) {
            days = 30;
        }

        try {
            List<MonthlyPayrollRun> list = payrollService.calculateMonthlyRun(period, days);
            runList.setAll(list);

            BigDecimal totalNet = BigDecimal.ZERO;
            for (MonthlyPayrollRun r : list) {
                if (r.getNetPay() != null) totalNet = totalNet.add(r.getNetPay());
            }

            periodSummaryLabel.setText("Pay Period: " + period + " (" + list.size() + " Staff Processed)");
            totalNetLabel.setText("Total Net Salary: ₹" + String.format("%.2f", totalNet));
        } catch (Exception e) {
            System.err.println("[PayrollCalculationController] Engine error: " + e.getMessage());
        }
    }
}
