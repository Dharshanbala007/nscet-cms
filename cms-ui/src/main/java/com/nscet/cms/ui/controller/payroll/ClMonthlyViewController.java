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
public class ClMonthlyViewController implements Initializable {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> typeCombo;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colName, colDept, colDoj, colAllowed;
    @FXML private TableColumn<StaffSalary, String> colJan, colFeb, colMar, colApr, colMay, colJun, colJul, colAug, colSep, colOct, colNov, colDec;
    @FXML private TableColumn<StaffSalary, String> colTaken, colBalance;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> gridData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryCombo.getItems().setAll("ALL", "Teaching", "Non-Teaching", "Officer");
        categoryCombo.getSelectionModel().selectFirst();

        typeCombo.getItems().setAll("CL", "LOP", "OD", "ALL");
        typeCombo.getSelectionModel().selectFirst();

        setupTable();
        handleView();
    }

    private void setupTable() {
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty("01/08/2023"));
        colAllowed.setCellValueFactory(c -> new SimpleStringProperty("12"));

        colJan.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colFeb.setCellValueFactory(c -> new SimpleStringProperty("1"));
        colMar.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colApr.setCellValueFactory(c -> new SimpleStringProperty("1"));
        colMay.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colJun.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colJul.setCellValueFactory(c -> new SimpleStringProperty("1"));
        colAug.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colSep.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOct.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colNov.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colDec.setCellValueFactory(c -> new SimpleStringProperty("0"));

        colTaken.setCellValueFactory(c -> {
            int bal = c.getValue().getClBalance() != null ? c.getValue().getClBalance() : 12;
            return new SimpleStringProperty(String.valueOf(Math.max(0, 12 - bal)));
        });

        colBalance.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClBalance() != null ? c.getValue().getClBalance().toString() : "12"));

        table.setItems(gridData);
    }

    @FXML
    private void handleView() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            String cat = categoryCombo.getValue();
            List<StaffSalary> filtered = list.stream().filter(s -> "ALL".equalsIgnoreCase(cat) || cat.equalsIgnoreCase(s.getCategory())).toList();
            gridData.setAll(filtered);
        } catch (Exception e) {
            System.err.println("[ClMonthlyViewController] Error: " + e.getMessage());
        }
    }
}
