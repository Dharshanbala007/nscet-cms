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
public class LatePermissionController implements Initializable {

    @FXML private ComboBox<String> staffCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField durationField;
    @FXML private TextField reasonField;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colDate, colCode, colName, colType, colDuration, colReason;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        typeCombo.setItems(FXCollections.observableArrayList("LATE_ENTRY", "EARLY_EXIT", "PERMISSION_1HR", "PERMISSION_2HR"));
        typeCombo.setValue("LATE_ENTRY");

        setupTable();
        loadStaff();
    }

    private void setupTable() {
        colDate.setCellValueFactory(c -> new SimpleStringProperty(LocalDate.now().toString()));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colType.setCellValueFactory(c -> new SimpleStringProperty("PERMISSION_1HR"));
        colDuration.setCellValueFactory(c -> new SimpleStringProperty("60 Mins"));
        colReason.setCellValueFactory(c -> new SimpleStringProperty("Official Permission"));

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
            System.err.println("[LatePermissionController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        datePicker.setValue(LocalDate.now());
        durationField.clear();
        reasonField.clear();
    }

    @FXML
    private void handleSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Late / Permission record saved successfully!");
        alert.showAndWait();
    }
}
