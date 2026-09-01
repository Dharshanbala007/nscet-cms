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
public class SalaryStructureController implements Initializable {

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSNo, colStaffName, colDesig, colDoj, colGrossPay, colBasicPay, colHra, colWashing, colTravelling, colSpl, colDiff;

    @Autowired private PayrollService payrollService;
    private final ObservableList<StaffSalary> structureList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        handleView();
    }

    private void setupTable() {
        colSNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(structureList.indexOf(c.getValue()) + 1)));
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation() != null ? c.getValue().getDesignation() : "AP - CE"));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty("01/08/2013"));

        colGrossPay.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrossSalary() != null ? c.getValue().getGrossSalary().toString() : "60000.00"));
        colBasicPay.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBasicPay() != null ? c.getValue().getBasicPay().toString() : "15000.00"));
        colHra.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHra() != null ? c.getValue().getHra().toString() : "12000.00"));
        colWashing.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWashingAllowance() != null ? c.getValue().getWashingAllowance().toString() : "6000.00"));
        colTravelling.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getConveyance() != null ? c.getValue().getConveyance().toString() : "6000.00"));
        colSpl.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpecialAllowance() != null ? c.getValue().getSpecialAllowance().toString() : "21000.00"));
        colDiff.setCellValueFactory(c -> new SimpleStringProperty("0"));

        table.setItems(structureList);
    }

    @FXML
    private void handleView() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            structureList.setAll(list);
        } catch (Exception e) {
            System.err.println("[SalaryStructureController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Salary Structure");
        alert.setHeaderText(null);
        alert.setContentText("Salary Structure details saved successfully!");
        alert.showAndWait();
    }
}
