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
public class PfEsiToolsController implements Initializable {

    @FXML private ComboBox<String> monthCombo;
    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colCode, colName, colBasic, colEpfEmp, colEpfComp, colEsiEmp, colEsiComp;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> staffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthCombo.getItems().setAll("Jul-2026", "Jun-2026", "May-2026");
        monthCombo.getSelectionModel().selectFirst();

        setupTable();
        handleGenerate();
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colBasic.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getBasicPay())));
        colEpfEmp.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getEpfDeduction())));
        colEpfComp.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getEpfDeduction())));
        colEsiEmp.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getEsiDeduction())));
        colEsiComp.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getEsiDeduction())));

        table.setItems(staffList);
    }

    @FXML
    private void handleGenerate() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            staffList.setAll(list);
        } catch (Exception e) {
            System.err.println("[PfEsiToolsController] Error: " + e.getMessage());
        }
    }
}
