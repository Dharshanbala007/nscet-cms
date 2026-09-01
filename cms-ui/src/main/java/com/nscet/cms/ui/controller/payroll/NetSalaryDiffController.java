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
public class NetSalaryDiffController implements Initializable {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> typeCombo;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo, colCode, colName, colDept, colPreviousAmt, colCurrentAmt, colDiff, colReason;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> diffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(LocalDate.of(2026, 8, 20));
        toDatePicker.setValue(LocalDate.of(2026, 8, 20));

        typeCombo.setItems(FXCollections.observableArrayList("Select", "Grosspay", "Netpay"));
        typeCombo.setValue("Select");

        setupTable();
        loadData();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(diffList.indexOf(c.getValue()) + 1)));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colPreviousAmt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNetSalary() != null ? c.getValue().getNetSalary().toString() : "58000.00"));
        colCurrentAmt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNetSalary() != null ? c.getValue().getNetSalary().toString() : "58000.00"));
        colDiff.setCellValueFactory(c -> new SimpleStringProperty("0.00"));
        colReason.setCellValueFactory(c -> new SimpleStringProperty("No Difference"));

        table.setItems(diffList);
    }

    private void loadData() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            diffList.setAll(list);
        } catch (Exception e) {
            System.err.println("[NetSalaryDiffController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleView() {
        loadData();
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Netpay Difference Report to printer...");
        alert.showAndWait();
    }
}
