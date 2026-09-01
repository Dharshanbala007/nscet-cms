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
public class DeductionSalaryReportController implements Initializable {

    @FXML private DatePicker fromDate, toDate;
    @FXML private ComboBox<String> yearCombo;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo, colName, colDesig, colDoj, colJun, colJul, colAug, colSep, colOct, colNov, colDec, colJan, colTotal;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> deductionList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.of(2022, 1, 1));
        toDate.setValue(LocalDate.of(2022, 6, 1));

        yearCombo.setItems(FXCollections.observableArrayList("Select", "2020-21", "2021-22"));
        yearCombo.setValue("2021-22");

        setupTable();
        handleLoad();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(deductionList.indexOf(c.getValue()) + 1)));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation() != null ? c.getValue().getDesignation() : "AP - CE"));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty("01/08/2020"));

        colJun.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colJul.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colAug.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colSep.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOct.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colNov.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colDec.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colJan.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty("0"));

        table.setItems(deductionList);
    }

    @FXML
    private void handleLoad() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            deductionList.setAll(list);
        } catch (Exception e) {
            System.err.println("[DeductionSalaryReportController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Deduction Salary Details report to printer...");
        alert.showAndWait();
    }
}
