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
public class MonthlyLeaveCreditController implements Initializable {

    @FXML private ComboBox<String> monthCombo;
    @FXML private TextField creditCountField;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colCode, colName, colDept, colClBefore, colCredited, colClAfter, colStatus;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> staffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthCombo.getItems().setAll("Aug-2026", "Jul-2026", "Jun-2026", "May-2026");
        monthCombo.getSelectionModel().selectFirst();

        setupTable();
        loadData();
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colClBefore.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClBalance() != null ? c.getValue().getClBalance().toString() : "11"));
        colCredited.setCellValueFactory(c -> new SimpleStringProperty("+1"));
        colClAfter.setCellValueFactory(c -> {
            int bal = c.getValue().getClBalance() != null ? c.getValue().getClBalance() : 11;
            return new SimpleStringProperty(String.valueOf(bal + 1));
        });
        colStatus.setCellValueFactory(c -> new SimpleStringProperty("UPDATED"));

        table.setItems(staffList);
    }

    private void loadData() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            staffList.setAll(list);
        } catch (Exception e) {
            System.err.println("[MonthlyLeaveCreditController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRunCredit() {
        try {
            for (StaffSalary s : staffList) {
                int bal = s.getClBalance() != null ? s.getClBalance() : 11;
                s.setClBalance(bal + 1);
                payrollService.saveStaffSalary(s);
            }
            loadData();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Monthly leave credit updated successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
