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
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class DeductionSalaryReportController implements Initializable {

    @FXML private ComboBox<String> yearCombo;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colName, colDesig, colDept;
    @FXML private TableColumn<StaffSalary, String> colJun, colJul, colAug, colSep, colOct, colNov, colDec, colJan, colFeb, colMar, colApr, colMay;
    @FXML private TableColumn<StaffSalary, String> colTotal;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> matrixData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        yearCombo.getItems().setAll("2025-26", "2024-25", "2023-24");
        yearCombo.getSelectionModel().selectFirst();

        setupTable();
        handleLoad();
    }

    private void setupTable() {
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));

        colJun.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colJul.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colAug.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colSep.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOct.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colNov.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colDec.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colJan.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colFeb.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colMar.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colApr.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colMay.setCellValueFactory(c -> new SimpleStringProperty("0"));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty("₹0.00"));

        table.setItems(matrixData);
    }

    @FXML
    private void handleLoad() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            matrixData.setAll(list);
        } catch (Exception e) {
            System.err.println("[DeductionSalaryReportController] Error: " + e.getMessage());
        }
    }
}
