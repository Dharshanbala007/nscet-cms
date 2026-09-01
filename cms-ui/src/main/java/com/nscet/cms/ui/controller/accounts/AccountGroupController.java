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
public class AccountGroupController implements Initializable {

    @FXML private TextField groupNameField;
    @FXML private ComboBox<String> parentGroupCombo;
    @FXML private TableView<AccountGroupRow> table;
    @FXML private TableColumn<AccountGroupRow, String> colParentGroup;
    @FXML private TableColumn<AccountGroupRow, String> colGroupName;

    @FXML private TitledPane tableContainer;

    private final ObservableList<AccountGroupRow> tableData = FXCollections.observableArrayList();
    private AccountGroupRow selectedRow = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
        setupTable();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedRow = newV;
                groupNameField.setText(newV.getGroupName());
                parentGroupCombo.setValue(newV.getParentGroup());
            }
        });

        if (tableContainer != null) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
        }
    }

    private void setupCombos() {
        parentGroupCombo.getItems().setAll("Select", "Primary Group", "Asset", "Liability", "Income", "Expenses");
        parentGroupCombo.setValue("Select");
    }

    private void setupTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colParentGroup.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getParentGroup()));
        colGroupName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGroupName()));
        table.setItems(tableData);
    }

    private void loadSampleDataFromScreenshot() {
        tableData.clear();
        tableData.add(new AccountGroupRow("Primary Group", "Asset"));
        tableData.add(new AccountGroupRow("Asset", "Lab Equipments"));
        tableData.add(new AccountGroupRow("Liability", "Sundry Crs Others"));
        tableData.add(new AccountGroupRow("Liability", "Capital"));
        tableData.add(new AccountGroupRow("Income", "Exam Fee"));
        tableData.add(new AccountGroupRow("Income", "Tution Fee"));
        tableData.add(new AccountGroupRow("Primary Group", "Liability"));
        tableData.add(new AccountGroupRow("Asset", "Library Journals"));
        tableData.add(new AccountGroupRow("Liability", "God"));
        tableData.add(new AccountGroupRow("Primary Group", "Income"));
        tableData.add(new AccountGroupRow("Asset", "Furniture Fittings"));
        tableData.add(new AccountGroupRow("Liability", "Advances"));
        tableData.add(new AccountGroupRow("Primary Group", "Expenses"));
        tableData.add(new AccountGroupRow("Asset", "TNEB Deposits"));
        tableData.add(new AccountGroupRow("Liability", "Sundry Crs"));
        tableData.add(new AccountGroupRow("Primary Group", "Trading dr"));
        tableData.add(new AccountGroupRow("Asset", "Sundry Drs"));
        tableData.add(new AccountGroupRow("Asset", "Sundry Drs Others"));
        tableData.add(new AccountGroupRow("Primary Group", "Trading cr"));
        tableData.add(new AccountGroupRow("Asset", "Cash at Bank"));
        tableData.add(new AccountGroupRow("Asset", "Cash on hand"));
    }

    @FXML
    private void handleAdd() {
        handleClear();
        groupNameField.requestFocus();
    }

    @FXML
    private void handleModify() {
        if (selectedRow == null) {
            ExportUtils.showAlert("Warning", "Please select an Account Group to modify.", Alert.AlertType.WARNING);
            return;
        }
        groupNameField.requestFocus();
    }

    @FXML
    private void handleSave() {
        String name = groupNameField.getText() != null ? groupNameField.getText().trim() : "";
        String parent = parentGroupCombo.getValue() != null ? parentGroupCombo.getValue() : "Select";
        if (name.isEmpty() || "Select".equals(parent)) {
            ExportUtils.showAlert("Validation Error", "Please enter Account Group name and select Parent Group.", Alert.AlertType.WARNING);
            return;
        }

        if (selectedRow != null) {
            selectedRow.setGroupName(name);
            selectedRow.setParentGroup(parent);
            table.refresh();
            ExportUtils.showAlert("Success", "Account Group updated successfully!", Alert.AlertType.INFORMATION);
        } else {
            tableData.add(new AccountGroupRow(parent, name));
            ExportUtils.showAlert("Success", "Account Group saved successfully!", Alert.AlertType.INFORMATION);
        }
        handleClear();
    }

    @FXML
    private void handleDelete() {
        if (selectedRow == null) {
            ExportUtils.showAlert("Warning", "Please select an Account Group to delete.", Alert.AlertType.WARNING);
            return;
        }
        tableData.remove(selectedRow);
        ExportUtils.showAlert("Deleted", "Account Group deleted.", Alert.AlertType.INFORMATION);
        handleClear();
    }

    @FXML
    private void handleClear() {
        selectedRow = null;
        groupNameField.clear();
        parentGroupCombo.setValue("Select");
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClose() {
        handleClear();
    }

    @FXML
    private void handleFind() {
        loadSampleDataFromScreenshot();
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
        ExportUtils.exportToCsv(table, "Account_Group_Master", table.getScene().getWindow());
    }

    @FXML
    private void handleExportPdf() {
        ExportUtils.exportToPdf(table, "Account Group Master Report", table.getScene().getWindow());
    }

    public static class AccountGroupRow {
        private String parentGroup;
        private String groupName;

        public AccountGroupRow(String parentGroup, String groupName) {
            this.parentGroup = parentGroup;
            this.groupName = groupName;
        }

        public String getParentGroup() { return parentGroup; }
        public void setParentGroup(String parentGroup) { this.parentGroup = parentGroup; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
    }
}
