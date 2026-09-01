package com.nscet.cms.ui.controller.accounts;

import com.nscet.cms.ui.util.ExportUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FunctionMasterController implements Initializable {

    @FXML private TextField fnNameField;
    @FXML private TableView<FunctionRow> table;
    @FXML private TableColumn<FunctionRow, String> colFnName;
    @FXML private TableColumn<FunctionRow, String> colDept;
    @FXML private TableColumn<FunctionRow, String> colBudget;
    @FXML private TableColumn<FunctionRow, String> colStatus;

    @FXML private TitledPane tableContainer;

    private final ObservableList<FunctionRow> tableData = FXCollections.observableArrayList();
    private FunctionRow selectedRow = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedRow = newV;
                fnNameField.setText(newV.getFnName());
            }
        });

        if (tableContainer != null) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
        }
    }

    private void setupTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colFnName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFnName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));
        colBudget.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBudget()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        table.setItems(tableData);
    }

    private void loadSampleData() {
        tableData.clear();
        tableData.add(new FunctionRow("GRADUATION DAY 2026", "GENERAL ADMIN", "₹ 2,50,000.00", "Active"));
        tableData.add(new FunctionRow("ANNUAL DAY & SPORTS MEET", "GENERAL ADMIN", "₹ 3,00,000.00", "Active"));
        tableData.add(new FunctionRow("NATIONAL TECHNICAL SYMPOSIUM", "COMPUTER SCIENCE", "₹ 1,20,000.00", "Active"));
        tableData.add(new FunctionRow("FACULTY DEVELOPMENT PROGRAM (FDP)", "ELECTRONICS & COMM", "₹ 75,000.00", "Active"));
        tableData.add(new FunctionRow("MECH TECH EXPO 2026", "MECHANICAL ENGINEERING", "₹ 1,00,000.00", "Active"));
        tableData.add(new FunctionRow("COLLEGE DAY CELEBRATIONS", "GENERAL ADMIN", "₹ 2,00,000.00", "Active"));
    }

    @FXML
    private void handleSave() {
        String name = fnNameField.getText() != null ? fnNameField.getText().trim() : "";
        if (name.isEmpty()) {
            ExportUtils.showAlert("Validation Error", "Please enter Function Name.", Alert.AlertType.WARNING);
            return;
        }

        if (selectedRow != null) {
            selectedRow.setFnName(name);
            table.refresh();
            ExportUtils.showAlert("Success", "Function Master updated successfully!", Alert.AlertType.INFORMATION);
        } else {
            tableData.add(new FunctionRow(name, "GENERAL ADMIN", "₹ 50,000.00", "Active"));
            ExportUtils.showAlert("Success", "Function Master saved successfully!", Alert.AlertType.INFORMATION);
        }
        handleClose();
    }

    @FXML
    private void handleClose() {
        selectedRow = null;
        fnNameField.clear();
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleFind() {
        loadSampleData();
        if (tableContainer != null) {
            tableContainer.setVisible(true);
            tableContainer.setManaged(true);
        }
        table.requestFocus();
    }

    @FXML
    private void handleCloseTable() {
        if (tableContainer != null) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
        }
        tableData.clear();
    }

    @FXML
    private void handleExportCsv() {
        ExportUtils.exportToCsv(table, "Function_Master", table.getScene().getWindow());
    }

    @FXML
    private void handleExportPdf() {
        ExportUtils.exportToPdf(table, "Function Master Report", table.getScene().getWindow());
    }

    public static class FunctionRow {
        private String fnName;
        private String dept;
        private String budget;
        private String status;

        public FunctionRow(String fnName, String dept, String budget, String status) {
            this.fnName = fnName;
            this.dept = dept;
            this.budget = budget;
            this.status = status;
        }

        public String getFnName() { return fnName; }
        public void setFnName(String fnName) { this.fnName = fnName; }
        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }
        public String getBudget() { return budget; }
        public void setBudget(String budget) { this.budget = budget; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
