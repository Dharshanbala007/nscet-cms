package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.LeaveMaster;
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
public class LeaveMasterController implements Initializable {

    @FXML private TableView<LeaveMaster> table;
    @FXML private TableColumn<LeaveMaster, String> colSlNo, colCode, colName, colShortName;

    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField shortNameField;

    @Autowired private PayrollService payrollService;
    private ObservableList<LeaveMaster> leaveList = FXCollections.observableArrayList();
    private LeaveMaster selectedEntity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedEntity = newSel;
                codeField.setText(newSel.getLeaveCode());
                nameField.setText(newSel.getLeaveName());
                shortNameField.setText(newSel.getShortName());
            }
        });
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(leaveList.indexOf(c.getValue()) + 1)));
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLeaveCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLeaveName()));
        colShortName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShortName()));
        table.setItems(leaveList);
    }

    private void loadData() {
        try {
            List<LeaveMaster> list = payrollService.getAllLeaves();
            leaveList.setAll(list);
        } catch (Exception e) {
            System.err.println("[LeaveMasterController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        handleCancel();
        codeField.requestFocus();
    }

    @FXML
    private void handleModify() {
        if (selectedEntity == null) {
            showAlert("Selection Required", "Please select a Leave Master record to modify.", Alert.AlertType.WARNING);
            return;
        }
        codeField.requestFocus();
    }

    @FXML
    private void handleDelete() {
        if (selectedEntity == null) {
            showAlert("Selection Required", "Please select a Leave Master record to delete.", Alert.AlertType.WARNING);
            return;
        }
        try {
            payrollService.deleteLeave(selectedEntity.getId());
            showAlert("Deleted", "Leave Master record deleted.", Alert.AlertType.INFORMATION);
            handleCancel();
            loadData();
        } catch (Exception e) {
            showAlert("Delete Error", "Failed to delete: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        String code = codeField.getText() != null ? codeField.getText().trim() : "";
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        if (code.isEmpty() || name.isEmpty()) {
            showAlert("Validation Error", "Please enter Leave Code and Leave Name.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (selectedEntity == null) selectedEntity = new LeaveMaster();
            selectedEntity.setLeaveCode(code);
            selectedEntity.setLeaveName(name);
            selectedEntity.setShortName(shortNameField.getText() != null ? shortNameField.getText().trim() : code);
            selectedEntity.setMaxAllowed(12);

            payrollService.saveLeave(selectedEntity);
            showAlert("Saved", "Leave Master record saved.", Alert.AlertType.INFORMATION);
            handleCancel();
            loadData();
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        selectedEntity = null;
        codeField.clear();
        nameField.clear();
        shortNameField.clear();
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
