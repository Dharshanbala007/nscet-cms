package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.FunctionExpenseService;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.FunctionExpense;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Scope("prototype")
public class FunctionExpenseController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Label totalBudgetLabel, totalExpenseLabel, totalBalanceLabel;

    @FXML private TableView<FunctionExpense> table;
    @FXML private TableColumn<FunctionExpense, String> colSlNo;
    @FXML private TableColumn<FunctionExpense, String> colFunctionName;
    @FXML private TableColumn<FunctionExpense, String> colDept;
    @FXML private TableColumn<FunctionExpense, String> colDate;
    @FXML private TableColumn<FunctionExpense, String> colBudget;
    @FXML private TableColumn<FunctionExpense, String> colExpense;
    @FXML private TableColumn<FunctionExpense, String> colBalance;
    @FXML private TableColumn<FunctionExpense, String> colStatus;
    @FXML private TableColumn<FunctionExpense, String> colRemarks;

    @FXML private TitledPane formPane;
    @FXML private TextField functionNameField, budgetField, expenseField, remarksField;
    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private DatePicker datePicker;

    @Autowired private FunctionExpenseService service;
    @Autowired private DepartmentMasterRepository departmentRepository;

    private ObservableList<FunctionExpense> tableData = FXCollections.observableArrayList();
    private FunctionExpense selectedEntity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
        setupTable();
        loadData();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
    }

    private void setupCombos() {
        statusCombo.getItems().clear();
        statusCombo.getItems().addAll("Completed", "In Progress", "Pending Approval");
        statusCombo.getSelectionModel().selectFirst();

        deptCombo.getItems().clear();
        try {
            List<DepartmentMaster> depts = departmentRepository.findAllActiveList();
            for (DepartmentMaster d : depts) {
                if (d.getName() != null) {
                    deptCombo.getItems().add(d.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("[FunctionExpenseController] Error loading departments: " + e.getMessage());
        }
        deptCombo.getSelectionModel().selectFirst();
        datePicker.setValue(LocalDate.now());
    }

    private void setupTable() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(tableData.indexOf(c.getValue()) + 1)));
        colFunctionName.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFunctionName() != null ? c.getValue().getFunctionName() : ""));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDepartment() != null ? c.getValue().getDepartment() : ""));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getExpenseDate() != null ? c.getValue().getExpenseDate().format(fmt) : ""));
        colBudget.setCellValueFactory(c -> new SimpleStringProperty(
                "₹" + String.format("%.2f", c.getValue().getAllocatedBudget() != null ? c.getValue().getAllocatedBudget() : BigDecimal.ZERO)));
        colExpense.setCellValueFactory(c -> new SimpleStringProperty(
                "₹" + String.format("%.2f", c.getValue().getTotalExpense() != null ? c.getValue().getTotalExpense() : BigDecimal.ZERO)));
        colBalance.setCellValueFactory(c -> new SimpleStringProperty(
                "₹" + String.format("%.2f", c.getValue().getBalanceAmount() != null ? c.getValue().getBalanceAmount() : BigDecimal.ZERO)));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatus() != null ? c.getValue().getStatus() : "Completed"));
        colRemarks.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getRemarks() != null ? c.getValue().getRemarks() : ""));

        table.setItems(tableData);
    }

    private void loadData() {
        try {
            List<FunctionExpense> list = service.getAllActive();
            tableData.setAll(list);
            calculateMetrics(list);
        } catch (Exception e) {
            System.err.println("[FunctionExpenseController] Error loading data: " + e.getMessage());
        }
    }

    private void calculateMetrics(List<FunctionExpense> list) {
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (FunctionExpense f : list) {
            if (f.getAllocatedBudget() != null) totalBudget = totalBudget.add(f.getAllocatedBudget());
            if (f.getTotalExpense() != null) totalExpense = totalExpense.add(f.getTotalExpense());
        }
        BigDecimal totalBalance = totalBudget.subtract(totalExpense);

        totalBudgetLabel.setText("₹" + String.format("%.2f", totalBudget));
        totalExpenseLabel.setText("₹" + String.format("%.2f", totalExpense));
        totalBalanceLabel.setText("₹" + String.format("%.2f", totalBalance));
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText() != null ? searchField.getText().trim() : "";
        try {
            List<FunctionExpense> results = service.search(query);
            tableData.setAll(results);
            calculateMetrics(results);
        } catch (Exception e) {
            System.err.println("[FunctionExpenseController] Error searching: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddNew() {
        handleClear();
        functionNameField.requestFocus();
    }

    private void populateForm(FunctionExpense entity) {
        selectedEntity = entity;
        functionNameField.setText(entity.getFunctionName());
        deptCombo.setValue(entity.getDepartment());
        if (entity.getExpenseDate() != null) datePicker.setValue(entity.getExpenseDate());
        budgetField.setText(entity.getAllocatedBudget() != null ? entity.getAllocatedBudget().toString() : "0.00");
        expenseField.setText(entity.getTotalExpense() != null ? entity.getTotalExpense().toString() : "0.00");
        statusCombo.setValue(entity.getStatus() != null ? entity.getStatus() : "Completed");
        remarksField.setText(entity.getRemarks() != null ? entity.getRemarks() : "");
    }

    @FXML
    private void handleSave() {
        String name = functionNameField.getText() != null ? functionNameField.getText().trim() : "";
        if (name.isEmpty()) {
            showAlert("Validation Error", "Please enter a Function/Event Name.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (selectedEntity == null) {
                selectedEntity = new FunctionExpense();
            }

            selectedEntity.setFunctionName(name);
            selectedEntity.setDepartment(deptCombo.getValue());
            selectedEntity.setExpenseDate(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());

            BigDecimal budget = new BigDecimal(budgetField.getText().trim().replaceAll("[^0-9.]", ""));
            BigDecimal expense = new BigDecimal(expenseField.getText().trim().replaceAll("[^0-9.]", ""));
            selectedEntity.setAllocatedBudget(budget);
            selectedEntity.setTotalExpense(expense);
            selectedEntity.setStatus(statusCombo.getValue());
            selectedEntity.setRemarks(remarksField.getText());

            service.save(selectedEntity);
            showAlert("Save Success", "Function Expense saved successfully!", Alert.AlertType.INFORMATION);
            handleClear();
            loadData();
        } catch (NumberFormatException e) {
            showAlert("Format Error", "Please enter valid numeric amounts for Budget and Expense.", Alert.AlertType.WARNING);
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save Function Expense: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEntity == null || selectedEntity.getId() == null) {
            showAlert("Validation Error", "Please select a Function Expense to delete.", Alert.AlertType.WARNING);
            return;
        }

        try {
            service.softDelete(selectedEntity.getId());
            showAlert("Deleted", "Function Expense record deleted.", Alert.AlertType.INFORMATION);
            handleClear();
            loadData();
        } catch (Exception e) {
            showAlert("Delete Error", "Failed to delete: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        selectedEntity = null;
        functionNameField.clear();
        budgetField.clear();
        expenseField.clear();
        remarksField.clear();
        deptCombo.getSelectionModel().selectFirst();
        statusCombo.getSelectionModel().selectFirst();
        datePicker.setValue(LocalDate.now());
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
