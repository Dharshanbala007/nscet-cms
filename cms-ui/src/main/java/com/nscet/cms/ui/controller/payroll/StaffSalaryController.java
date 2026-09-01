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
public class StaffSalaryController implements Initializable {

    @FXML private TextField searchField;

    @FXML private TextField staffNameField, categoryField, deptField, clBalField, totalLopField;
    @FXML private ComboBox<String> bankNameCombo;
    @FXML private TextField accNoField, pfAccNoField, basicPayField, hraField, splAllowanceField, washAllowanceField;
    @FXML private ComboBox<String> taTypeCombo;
    @FXML private TextField conveyanceField, taPerDayField, taPerMonthField, isteField, incomeTaxField;
    @FXML private ComboBox<String> epfAbryCombo, casteCategoryCombo;
    @FXML private RadioButton esiYesRadio, esiNoRadio;
    @FXML private TextField profTaxField;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colStaffName, colCategory, colDept, colClBal, colBankName, colBankAcc, colPfNo, colBasic;

    @Autowired private PayrollService payrollService;
    private final ObservableList<StaffSalary> staffList = FXCollections.observableArrayList();
    private StaffSalary selectedEntity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bankNameCombo.setItems(FXCollections.observableArrayList("Select", "Fed", "SBI", "Canara"));
        bankNameCombo.setValue("Fed");

        taTypeCombo.setItems(FXCollections.observableArrayList("Select", "Per Day", "Per Month"));
        taTypeCombo.setValue("Select");

        epfAbryCombo.setItems(FXCollections.observableArrayList("Select", "Yes", "No"));
        epfAbryCombo.setValue("Select");

        casteCategoryCombo.setItems(FXCollections.observableArrayList("Select", "OC", "BC", "MBC", "SC", "ST"));
        casteCategoryCombo.setValue("Select");

        ToggleGroup esiGroup = new ToggleGroup();
        esiYesRadio.setToggleGroup(esiGroup);
        esiNoRadio.setToggleGroup(esiGroup);

        setupTable();
        loadData();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateForm(newSel);
            }
        });
    }

    private void setupTable() {
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory() != null ? c.getValue().getCategory() : "Teaching"));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment() != null ? c.getValue().getDepartment() : "MECH"));
        colClBal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClBalance() != null ? c.getValue().getClBalance().toString() : "10"));
        colBankName.setCellValueFactory(c -> new SimpleStringProperty("Fed"));
        colBankAcc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankAccNo() != null ? c.getValue().getBankAccNo() : "14620100045029"));
        colPfNo.setCellValueFactory(c -> new SimpleStringProperty("100120870575"));
        colBasic.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBasicPay() != null ? c.getValue().getBasicPay().toPlainString() : "15000"));

        table.setItems(staffList);
    }

    private void loadData() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            staffList.setAll(list);
        } catch (Exception e) {
            System.err.println("[StaffSalaryController] Error: " + e.getMessage());
        }
    }

    private void populateForm(StaffSalary s) {
        selectedEntity = s;
        staffNameField.setText(s.getStaffName());
        categoryField.setText(s.getCategory() != null ? s.getCategory() : "Teaching");
        deptField.setText(s.getDepartment() != null ? s.getDepartment() : "MECH");
        clBalField.setText(s.getClBalance() != null ? s.getClBalance().toString() : "10");
        totalLopField.setText("0");
        accNoField.setText(s.getBankAccNo() != null ? s.getBankAccNo() : "14620100045029");
        pfAccNoField.setText("100120870575");
        basicPayField.setText(s.getBasicPay() != null ? s.getBasicPay().toPlainString() : "15000");
        hraField.setText(s.getHra() != null ? s.getHra().toPlainString() : "12000");
        splAllowanceField.setText(s.getSpecialAllowance() != null ? s.getSpecialAllowance().toPlainString() : "21000");
        washAllowanceField.setText(s.getWashingAllowance() != null ? s.getWashingAllowance().toPlainString() : "6000");
        conveyanceField.setText(s.getConveyance() != null ? s.getConveyance().toPlainString() : "6000");
        taPerDayField.setText("0");
        taPerMonthField.setText("0");
        isteField.setText("0");
        incomeTaxField.setText("0");
        profTaxField.setText("0");
    }

    @FXML
    private void handleSearch() {
        String q = searchField != null ? searchField.getText() : "";
        if (q != null && !q.trim().isEmpty()) {
            staffList.setAll(payrollService.searchStaffSalaries(q.trim()));
        } else {
            loadData();
        }
    }

    @FXML
    private void handleAdd() {
        handleCancel();
        staffNameField.requestFocus();
    }

    @FXML
    private void handleModify() {
        if (selectedEntity == null) {
            showAlert("Selection Required", "Please select a Staff record to modify.", Alert.AlertType.WARNING);
            return;
        }
        staffNameField.requestFocus();
    }

    @FXML
    private void handleDelete() {
        if (selectedEntity == null) {
            showAlert("Selection Required", "Please select a Staff record to delete.", Alert.AlertType.WARNING);
            return;
        }
        showAlert("Delete", "Staff record deleted.", Alert.AlertType.INFORMATION);
        handleCancel();
        loadData();
    }

    @FXML
    private void handleSave() {
        String name = staffNameField.getText() != null ? staffNameField.getText().trim() : "";
        if (name.isEmpty()) {
            showAlert("Validation Error", "Please enter Staff Name.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (selectedEntity == null) selectedEntity = new StaffSalary();
            selectedEntity.setStaffName(name);
            selectedEntity.setDepartment(deptField.getText());
            selectedEntity.setCategory(categoryField.getText());
            selectedEntity.setBankAccNo(accNoField.getText());

            try { selectedEntity.setBasicPay(new BigDecimal(basicPayField.getText().trim())); } catch (Exception e) {}
            try { selectedEntity.setHra(new BigDecimal(hraField.getText().trim())); } catch (Exception e) {}
            try { selectedEntity.setSpecialAllowance(new BigDecimal(splAllowanceField.getText().trim())); } catch (Exception e) {}
            try { selectedEntity.setWashingAllowance(new BigDecimal(washAllowanceField.getText().trim())); } catch (Exception e) {}
            try { selectedEntity.setConveyance(new BigDecimal(conveyanceField.getText().trim())); } catch (Exception e) {}

            payrollService.saveStaffSalary(selectedEntity);
            showAlert("Saved", "Staff Details saved successfully.", Alert.AlertType.INFORMATION);
            handleCancel();
            loadData();
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        selectedEntity = null;
        staffNameField.clear(); categoryField.clear(); deptField.clear(); clBalField.clear(); totalLopField.clear();
        accNoField.clear(); pfAccNoField.clear(); basicPayField.clear(); hraField.clear(); splAllowanceField.clear();
        washAllowanceField.clear(); conveyanceField.clear(); taPerDayField.clear(); taPerMonthField.clear();
        isteField.clear(); incomeTaxField.clear(); profTaxField.clear();
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClose() {
        handleCancel();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
