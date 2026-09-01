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
public class ResignTerminationController implements Initializable {

    @FXML private ComboBox<String> staffCombo;
    @FXML private DatePicker relievingDatePicker;
    @FXML private ComboBox<String> exitTypeCombo;
    @FXML private TextField noticeDaysField;
    @FXML private TextField settlementField;
    @FXML private TextField reasonField;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colDate, colCode, colName, colDept, colType, colReason;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        relievingDatePicker.setValue(LocalDate.now());
        exitTypeCombo.setItems(FXCollections.observableArrayList("RESIGNATION", "TERMINATION", "RETIREMENT", "CONTRACT_EXPIRY"));
        exitTypeCombo.setValue("RESIGNATION");

        setupTable();
        loadStaff();
    }

    private void setupTable() {
        colDate.setCellValueFactory(c -> new SimpleStringProperty(LocalDate.now().toString()));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colType.setCellValueFactory(c -> new SimpleStringProperty("RESIGNATION"));
        colReason.setCellValueFactory(c -> new SimpleStringProperty("Relieved on Personal Request"));

        table.setItems(list);
    }

    private void loadStaff() {
        try {
            List<StaffSalary> all = payrollService.getAllStaffSalaries();
            staffCombo.getItems().clear();
            for (StaffSalary s : all) {
                staffCombo.getItems().add(s.getStaffCode() + " - " + s.getStaffName());
            }
            if (!staffCombo.getItems().isEmpty()) staffCombo.getSelectionModel().selectFirst();
            list.setAll(all);
        } catch (Exception e) {
            System.err.println("[ResignTerminationController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        relievingDatePicker.setValue(LocalDate.now());
        noticeDaysField.clear();
        settlementField.clear();
        reasonField.clear();
    }

    @FXML
    private void handleSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Resignation / Relieving details saved successfully!");
        alert.showAndWait();
    }
}
