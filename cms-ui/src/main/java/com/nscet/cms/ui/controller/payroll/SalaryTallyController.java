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
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class SalaryTallyController implements Initializable {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField totalField;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo, colCategory, colDept, colGross, colEpf, colEsi, colLop, colNet;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> tallyList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        toDatePicker.setValue(LocalDate.now());

        categoryCombo.setItems(FXCollections.observableArrayList("Teaching", "Non-Teaching", "Regular", "All"));
        categoryCombo.setValue("All");

        setupTable();
        loadData();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(tallyList.indexOf(c.getValue()) + 1)));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory() != null ? c.getValue().getCategory() : "Regular"));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colGross.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrossSalary() != null ? c.getValue().getGrossSalary().toString() : "0.00"));
        colEpf.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEpfDeduction() != null ? c.getValue().getEpfDeduction().toString() : "0.00"));
        colEsi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEsiDeduction() != null ? c.getValue().getEsiDeduction().toString() : "0.00"));
        colLop.setCellValueFactory(c -> new SimpleStringProperty("0.00"));
        colNet.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNetSalary() != null ? c.getValue().getNetSalary().toString() : "0.00"));

        table.setItems(tallyList);
    }

    private void loadData() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            tallyList.setAll(list);
            calculateTotal();
        } catch (Exception e) {
            System.err.println("[SalaryTallyController] Error: " + e.getMessage());
        }
    }

    private void calculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (StaffSalary s : tallyList) {
            if (s.getNetSalary() != null) sum = sum.add(s.getNetSalary());
        }
        totalField.setText(sum.toPlainString());
    }

    @FXML
    private void handleView() {
        loadData();
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Tally");
        alert.setHeaderText(null);
        alert.setContentText("Sending Salary Tally Statement to printer...");
        alert.showAndWait();
    }
}
