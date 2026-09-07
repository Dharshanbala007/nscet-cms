package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.StaffSalary;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StaffClubController implements Initializable {

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo;
    @FXML private TableColumn<StaffSalary, String> colStaffCode;
    @FXML private TableColumn<StaffSalary, String> colStaffName;
    @FXML private TableColumn<StaffSalary, String> colDepartment;
    @FXML private TableColumn<StaffSalary, String> colDesignation;
    @FXML private TableColumn<StaffSalary, String> colStaffClub;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> staffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        handleRefresh();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(staffList.indexOf(c.getValue()) + 1)));
        colStaffCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDepartment.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment() != null ? c.getValue().getDepartment() : ""));
        colDesignation.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation() != null ? c.getValue().getDesignation() : ""));

        colStaffClub.setCellValueFactory(c -> {
            StaffSalary s = c.getValue();
            return new SimpleStringProperty(s.getStaffClub() != null ? s.getStaffClub().toPlainString() : "0.00");
        });

        table.setEditable(true);
        colStaffClub.setEditable(true);
        colStaffClub.setCellFactory(TextFieldTableCell.forTableColumn());
        colStaffClub.setOnEditCommit(e -> {
            StaffSalary staff = e.getTableView().getItems().get(e.getTablePosition().getRow());
            try {
                staff.setStaffClub(new BigDecimal(e.getNewValue()));
            } catch (NumberFormatException ex) {
                staff.setStaffClub(BigDecimal.ZERO);
            }
        });

        table.setItems(staffList);
    }

    @FXML
    private void handleRefresh() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            staffList.setAll(list);
        } catch (Exception e) {
            System.err.println("[StaffClubController] Error loading data: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            for (StaffSalary staff : staffList) {
                payrollService.saveStaffSalary(staff);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Staff Club");
            alert.setHeaderText(null);
            alert.setContentText("Staff Club details saved successfully.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
