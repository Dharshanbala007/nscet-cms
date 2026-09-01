package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.SalaryIncrement;
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
public class SalaryIncrementController implements Initializable {

    @FXML private TableView<SalaryIncrement> table;
    @FXML private TableColumn<SalaryIncrement, String> colEffDate, colCode, colName, colDept, colOldBasic, colOldGross, colIncrement, colNewSalary;

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField codeField;
    @FXML private TextField departmentField;
    @FXML private TextField basicPayField;
    @FXML private TextField hraField;
    @FXML private TextField splAllowanceField;
    @FXML private TextField grossSalaryField;
    @FXML private TextField revisedSalaryField;
    @FXML private TextField incrementField;
    @FXML private DatePicker effFromDatePicker;
    @FXML private TextField newSalaryField;
    @FXML private TextField newBasicPayField;
    @FXML private TextField newHraField;
    @FXML private TextField newSplAllowanceField;

    @Autowired private PayrollService payrollService;

    private ObservableList<SalaryIncrement> logList = FXCollections.observableArrayList();
    private SalaryIncrement selectedEntity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        effFromDatePicker.setValue(LocalDate.now());
        setupTable();
        loadIncrements();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedEntity = newSel;
                populateForm(newSel);
            }
        });
    }

    private void setupTable() {
        colEffDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEffectiveDate() != null ? c.getValue().getEffectiveDate().toString() : ""));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colOldBasic.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOldBasic() != null ? c.getValue().getOldBasic().toString() : "0.00"));
        colOldGross.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNewGross() != null ? c.getValue().getNewGross().toString() : "0.00"));
        colIncrement.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIncrementAmount() != null ? c.getValue().getIncrementAmount().toString() : "0.00"));
        colNewSalary.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNewGross() != null ? c.getValue().getNewGross().toString() : "0.00"));

        table.setItems(logList);
    }

    private void loadIncrements() {
        try {
            List<SalaryIncrement> list = payrollService.getAllIncrements();
            logList.setAll(list);
        } catch (Exception e) {
            System.err.println("[SalaryIncrementController] Error loading logs: " + e.getMessage());
        }
    }

    private void populateForm(SalaryIncrement inc) {
        codeField.setText(inc.getStaffCode());
        nameField.setText(inc.getStaffName());
        departmentField.setText(inc.getDepartment());
        basicPayField.setText(inc.getOldBasic() != null ? inc.getOldBasic().toPlainString() : "0.00");
        splAllowanceField.setText(inc.getOldSpecialAllowance() != null ? inc.getOldSpecialAllowance().toPlainString() : "0.00");
        incrementField.setText(inc.getIncrementAmount() != null ? inc.getIncrementAmount().toPlainString() : "0.00");
        newSalaryField.setText(inc.getNewGross() != null ? inc.getNewGross().toPlainString() : "0.00");
        newBasicPayField.setText(inc.getNewBasic() != null ? inc.getNewBasic().toPlainString() : "0.00");
        newSplAllowanceField.setText(inc.getNewSpecialAllowance() != null ? inc.getNewSpecialAllowance().toPlainString() : "0.00");
        if (inc.getEffectiveDate() != null) effFromDatePicker.setValue(inc.getEffectiveDate());
    }

    @FXML
    private void handleAdd() {
        handleCancel();
    }

    @FXML
    private void handleModify() {
        if (selectedEntity == null) {
            showAlert("Selection Required", "Please select a record from the table to modify.", Alert.AlertType.WARNING);
            return;
        }
        handleSave();
    }

    @FXML
    private void handleDelete() {
        if (selectedEntity == null) {
            showAlert("Selection Required", "Please select a record from the table to delete.", Alert.AlertType.WARNING);
            return;
        }
        logList.remove(selectedEntity);
        handleCancel();
        showAlert("Deleted", "Salary increment record removed.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleSave() {
        String code = codeField.getText() != null ? codeField.getText().trim() : "";
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        if (code.isEmpty() || name.isEmpty()) {
            showAlert("Validation Error", "Please enter Staff Code and Staff Name.", Alert.AlertType.WARNING);
            return;
        }

        BigDecimal oldBasic = parseDecimal(basicPayField.getText());
        BigDecimal newBasic = parseDecimal(newBasicPayField.getText());
        BigDecimal oldSpl = parseDecimal(splAllowanceField.getText());
        BigDecimal newSpl = parseDecimal(newSplAllowanceField.getText());
        BigDecimal totalInc = parseDecimal(incrementField.getText());
        if (totalInc.compareTo(BigDecimal.ZERO) == 0) {
            totalInc = newBasic.subtract(oldBasic).add(newSpl.subtract(oldSpl));
        }

        SalaryIncrement inc = selectedEntity != null ? selectedEntity : new SalaryIncrement();
        inc.setStaffCode(code);
        inc.setStaffName(name);
        inc.setDepartment(departmentField.getText());
        inc.setEffectiveDate(effFromDatePicker.getValue() != null ? effFromDatePicker.getValue() : LocalDate.now());
        inc.setOldBasic(oldBasic);
        inc.setNewBasic(newBasic);
        inc.setOldSpecialAllowance(oldSpl);
        inc.setNewSpecialAllowance(newSpl);
        inc.setIncrementAmount(totalInc);
        inc.setNewGross(parseDecimal(newSalaryField.getText()));

        try {
            payrollService.applyIncrement(inc);
            showAlert("Saved", "Salary Increment details saved successfully!", Alert.AlertType.INFORMATION);
            handleCancel();
            loadIncrements();
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save increment: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        selectedEntity = null;
        nameField.clear();
        categoryField.clear();
        codeField.clear();
        departmentField.clear();
        basicPayField.clear();
        hraField.clear();
        splAllowanceField.clear();
        grossSalaryField.clear();
        revisedSalaryField.clear();
        incrementField.clear();
        newSalaryField.clear();
        newBasicPayField.clear();
        newHraField.clear();
        newSplAllowanceField.clear();
        effFromDatePicker.setValue(LocalDate.now());
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClose() {
        handleCancel();
    }

    private BigDecimal parseDecimal(String text) {
        if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(text.trim().replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
