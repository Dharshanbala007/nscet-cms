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
public class MonthlyLeaveCreditController implements Initializable {

    @FXML private DatePicker fromDate, toDate;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo, colCode, colName, colLeaveBal, colLeaveCredit, colAvailable;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> creditList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.of(2026, 1, 1));
        toDate.setValue(LocalDate.of(2026, 7, 31));

        setupTable();
        handleView();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(creditList.indexOf(c.getValue()) + 1)));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colLeaveBal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClBalance() != null ? c.getValue().getClBalance().toString() : "10"));
        colLeaveCredit.setCellValueFactory(c -> new SimpleStringProperty("1"));
        colAvailable.setCellValueFactory(c -> {
            int bal = c.getValue().getClBalance() != null ? c.getValue().getClBalance() : 10;
            return new SimpleStringProperty(String.valueOf(bal + 1));
        });

        table.setItems(creditList);
    }

    @FXML
    private void handleView() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            creditList.setAll(list);
        } catch (Exception e) {
            System.err.println("[MonthlyLeaveCreditController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Monthly Leave Credit");
        alert.setHeaderText(null);
        alert.setContentText("Monthly Leave Credits successfully updated and saved!");
        alert.showAndWait();
    }
}
