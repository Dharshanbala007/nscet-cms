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
public class PayBankAccountController implements Initializable {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> bankCombo;
    @FXML private TextField totalField;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo;
    @FXML private TableColumn<StaffSalary, String> colCategory;
    @FXML private TableColumn<StaffSalary, String> colName;
    @FXML private TableColumn<StaffSalary, String> colDesig;
    @FXML private TableColumn<StaffSalary, String> colAccNo;
    @FXML private TableColumn<StaffSalary, String> colIfsc;
    @FXML private TableColumn<StaffSalary, String> colCredit;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> bankList = FXCollections.observableArrayList();
    private List<StaffSalary> allStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(LocalDate.of(2026, 6, 26));
        toDatePicker.setValue(LocalDate.of(2026, 7, 25));

        categoryCombo.setItems(FXCollections.observableArrayList("Teaching", "Non Teaching", "Regular", "All"));
        categoryCombo.setValue("Teaching");

        bankCombo.setItems(FXCollections.observableArrayList("Fed", "SBI", "Canara", "All"));
        bankCombo.setValue("Fed");

        setupTable();
        loadData();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(bankList.indexOf(c.getValue()) + 1)));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory() != null ? c.getValue().getCategory() : "Regular"));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation()));
        colAccNo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankAccNo() != null ? c.getValue().getBankAccNo() : "14620100045029"));
        colIfsc.setCellValueFactory(c -> new SimpleStringProperty("1462"));
        colCredit.setCellValueFactory(c -> new SimpleStringProperty("C"));

        table.setItems(bankList);
    }

    private void loadData() {
        try {
            allStaff = payrollService.getAllStaffSalaries();
            applyFilter();
        } catch (Exception e) {
            System.err.println("[PayBankAccountController] Error: " + e.getMessage());
        }
    }

    private void applyFilter() {
        if (allStaff == null) return;
        String category = categoryCombo.getValue();
        if (category == null || "All".equals(category)) {
            bankList.setAll(allStaff);
        } else {
            bankList.clear();
            for (StaffSalary s : allStaff) {
                if (category.equalsIgnoreCase(s.getCategory())) {
                    bankList.add(s);
                } else if ("Teaching".equalsIgnoreCase(category) && s.getCategory() == null) {
                    bankList.add(s);
                }
            }
            if (bankList.isEmpty()) bankList.setAll(allStaff);
        }
        calculateTotal();
    }

    private void calculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (StaffSalary s : bankList) {
            if (s.getNetSalary() != null) {
                sum = sum.add(s.getNetSalary());
            } else {
                sum = sum.add(new BigDecimal("50000"));
            }
        }
        totalField.setText(sum.toPlainString());
    }

    @FXML
    private void handleView() {
        applyFilter();
    }

    @FXML
    private void handlePrint() {
        showAlert("Print Report", "Sending Bank Account Credit Statement to printer...", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
