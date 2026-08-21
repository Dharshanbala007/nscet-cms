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
public class OldSalaryStructureController implements Initializable {

    @FXML private ComboBox<String> yearCombo;
    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colCode, colName, colDept, colOldBasic, colOldSpl, colOldGross, colEffective;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        yearCombo.getItems().setAll("2025-26", "2024-25", "2023-24");
        yearCombo.getSelectionModel().selectFirst();

        yearCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) handleLoad();
        });

        setupTable();
        handleLoad();
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));

        colOldBasic.setCellValueFactory(c -> {
            BigDecimal b = c.getValue().getBasicPay() != null ? c.getValue().getBasicPay().multiply(new BigDecimal("0.85")) : BigDecimal.ZERO;
            return new SimpleStringProperty("₹" + String.format("%.2f", b));
        });

        colOldSpl.setCellValueFactory(c -> {
            BigDecimal s = c.getValue().getSpecialAllowance() != null ? c.getValue().getSpecialAllowance().multiply(new BigDecimal("0.85")) : BigDecimal.ZERO;
            return new SimpleStringProperty("₹" + String.format("%.2f", s));
        });

        colOldGross.setCellValueFactory(c -> {
            BigDecimal g = c.getValue().getGrossSalary() != null ? c.getValue().getGrossSalary().multiply(new BigDecimal("0.85")) : BigDecimal.ZERO;
            return new SimpleStringProperty("₹" + String.format("%.2f", g));
        });

        colEffective.setCellValueFactory(c -> new SimpleStringProperty(yearCombo.getValue() != null ? yearCombo.getValue() : "2025-26"));
        table.setItems(list);
    }

    @FXML
    private void handleLoad() {
        try {
            List<StaffSalary> all = payrollService.getAllStaffSalaries();
            java.util.Map<String, StaffSalary> uniqueMap = new java.util.LinkedHashMap<>();
            for (StaffSalary s : all) {
                if (s.getStaffCode() != null && !uniqueMap.containsKey(s.getStaffCode())) {
                    uniqueMap.put(s.getStaffCode(), s);
                }
            }
            list.setAll(uniqueMap.values());
        } catch (Exception e) {
            System.err.println("[OldSalaryStructureController] Error: " + e.getMessage());
        }
    }
}
