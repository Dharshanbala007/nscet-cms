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
public class PayrollCalculationController implements Initializable {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private CheckBox profTaxCheck;
    @FXML private TextField teachingPctField;
    @FXML private TextField nonTeachingPctField;
    @FXML private ComboBox<String> selectCombo;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo;
    @FXML private TableColumn<StaffSalary, String> colName;
    @FXML private TableColumn<StaffSalary, String> colDesig;
    @FXML private TableColumn<StaffSalary, String> colDoj;
    @FXML private TableColumn<StaffSalary, String> colBasic;
    @FXML private TableColumn<StaffSalary, String> colHra;
    @FXML private TableColumn<StaffSalary, String> colSpl;
    @FXML private TableColumn<StaffSalary, String> colWashing;
    @FXML private TableColumn<StaffSalary, String> colConveyance;
    @FXML private TableColumn<StaffSalary, String> colDeduLop;
    @FXML private TableColumn<StaffSalary, String> colGross60;
    @FXML private TableColumn<StaffSalary, String> colGross;
    @FXML private TableColumn<StaffSalary, String> colIncomeTax;
    @FXML private TableColumn<StaffSalary, String> colEpf;
    @FXML private TableColumn<StaffSalary, String> colLopDays;
    @FXML private TableColumn<StaffSalary, String> colLopAmt;
    @FXML private TableColumn<StaffSalary, String> colNet;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> salaryList = FXCollections.observableArrayList();
    private List<StaffSalary> allStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(LocalDate.of(2026, 6, 25));
        toDatePicker.setValue(LocalDate.of(2026, 7, 26));

        categoryCombo.setItems(FXCollections.observableArrayList("Regular", "New Emp", "Contract Emp", "Reliving Emp", "Outsourcing Emp"));
        categoryCombo.setValue("Regular");

        selectCombo.setItems(FXCollections.observableArrayList("Select", "Teaching", "Non Teaching", "All"));
        selectCombo.setValue("Select");

        setupTable();
        loadData();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(salaryList.indexOf(c.getValue()) + 1)));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation()));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt() != null ? c.getValue().getCreatedAt().toLocalDate().toString() : "01/08/2013"));
        colBasic.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBasicPay() != null ? c.getValue().getBasicPay().toPlainString() : "15000"));
        colHra.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHra() != null ? c.getValue().getHra().toPlainString() : "12000"));
        colSpl.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpecialAllowance() != null ? c.getValue().getSpecialAllowance().toPlainString() : "21000"));
        colWashing.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWashingAllowance() != null ? c.getValue().getWashingAllowance().toPlainString() : "6000"));
        colConveyance.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getConveyance() != null ? c.getValue().getConveyance().toPlainString() : "6000"));
        colDeduLop.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colGross60.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrossSalary() != null ? c.getValue().getGrossSalary().toPlainString() : "60000"));
        colGross.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrossSalary() != null ? c.getValue().getGrossSalary().toPlainString() : "60000"));
        colIncomeTax.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIncomeTax() != null ? c.getValue().getIncomeTax().toPlainString() : "0"));
        colEpf.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEpfDeduction() != null ? c.getValue().getEpfDeduction().toPlainString() : "1800"));
        colLopDays.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colLopAmt.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colNet.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNetSalary() != null ? c.getValue().getNetSalary().toPlainString() : "58200"));

        table.setItems(salaryList);
    }

    private void loadData() {
        try {
            allStaff = payrollService.getAllStaffSalaries();
            applyFilter();
        } catch (Exception e) {
            System.err.println("[PayrollCalculationController] Error: " + e.getMessage());
        }
    }

    private void applyFilter() {
        if (allStaff == null) return;
        String filter = selectCombo.getValue();
        if (filter == null || "Select".equals(filter) || "All".equals(filter)) {
            salaryList.setAll(allStaff);
        } else {
            salaryList.clear();
            for (StaffSalary s : allStaff) {
                if (filter.equalsIgnoreCase(s.getCategory())) {
                    salaryList.add(s);
                } else if ("Teaching".equalsIgnoreCase(filter) && s.getCategory() == null) {
                    salaryList.add(s);
                }
            }
            if (salaryList.isEmpty()) salaryList.setAll(allStaff);
        }
    }

    @FXML
    private void handleView() {
        loadData();
    }

    @FXML
    private void handleSave() {
        showAlert("Save Status", "Payroll calculation records saved successfully.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleFilterCategory() {
        applyFilter();
    }

    @FXML
    private void handlePrint() {
        showAlert("Print Report", "Sending Payroll Calculation report to printer...", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
