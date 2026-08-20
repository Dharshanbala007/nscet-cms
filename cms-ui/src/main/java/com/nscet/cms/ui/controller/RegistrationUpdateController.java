package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import com.nscet.cms.db.repository.StudentMasterRepository;
import com.nscet.cms.ui.navigation.NavigationManager;
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

import java.net.URL;
import java.util.*;

@Component
@Scope("prototype")
public class RegistrationUpdateController implements Initializable {

    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private TextField startRegField;

    @FXML private TableView<RegistrationRow> table;
    @FXML private TableColumn<RegistrationRow, String> sNoCol;
    @FXML private TableColumn<RegistrationRow, String> rollNoCol;
    @FXML private TableColumn<RegistrationRow, String> nameCol;
    @FXML private TableColumn<RegistrationRow, String> currentRegNoCol;
    @FXML private TableColumn<RegistrationRow, String> newRegNoCol;

    @Autowired private StudentMasterRepository studentMasterRepository;
    @Autowired private DepartmentMasterRepository departmentRepository;

    private ObservableList<RegistrationRow> tableData = FXCollections.observableArrayList();

    public static class RegistrationRow {
        private int sNo;
        private StudentMaster student;
        private String newRegNo;

        public RegistrationRow(int sNo, StudentMaster student, String newRegNo) {
            this.sNo = sNo;
            this.student = student;
            this.newRegNo = newRegNo;
        }

        public int getSNo() { return sNo; }
        public StudentMaster getStudent() { return student; }
        public String getNewRegNo() { return newRegNo; }
        public void setNewRegNo(String newRegNo) { this.newRegNo = newRegNo; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
        setupTableColumns();
        table.setItems(tableData);
        table.setEditable(true);
    }

    private void setupCombos() {
        try {
            List<DepartmentMaster> depts = departmentRepository.findAll();
            deptCombo.setItems(FXCollections.observableArrayList(depts));
            deptCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select Department" : item.getName());
                }
            });
            deptCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select Department" : item.getName());
                }
            });
            startRegField.setText("9210251001");
        } catch (Exception e) {
            System.err.println("[RegistrationUpdateController] Error loading combos: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        sNoCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getSNo())));
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudent().getRollNumber() != null ? c.getValue().getStudent().getRollNumber() : "N/A"));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudent().getName() != null ? c.getValue().getStudent().getName() : "N/A"));
        currentRegNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudent().getRegistrationNo() != null ? c.getValue().getStudent().getRegistrationNo() : "N/A"));

        newRegNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNewRegNo()));
        newRegNoCol.setCellFactory(TextFieldTableCell.forTableColumn());
        newRegNoCol.setOnEditCommit(event -> {
            RegistrationRow row = event.getRowValue();
            row.setNewRegNo(event.getNewValue());
        });
    }

    @FXML
    private void handleView() {
        String startReg = startRegField.getText() != null ? startRegField.getText().trim() : "";
        if (startReg.isEmpty()) {
            showAlert("Validation Error", "Please enter a Starting Registration Number.", Alert.AlertType.WARNING);
            return;
        }

        try {
            List<StudentMaster> students = studentMasterRepository.findAll();
            // Sort alphabetically by name
            students.sort(Comparator.comparing(s -> s.getName() != null ? s.getName() : ""));

            tableData.clear();
            long baseNum;
            try {
                baseNum = Long.parseLong(startReg);
            } catch (NumberFormatException e) {
                baseNum = 9210251001L;
            }

            int index = 1;
            for (StudentMaster s : students) {
                String genRegNo = String.valueOf(baseNum + index - 1);
                tableData.add(new RegistrationRow(index, s, genRegNo));
                index++;
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load students: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        if (tableData.isEmpty()) {
            showAlert("Validation Error", "No student records to update. Click View first.", Alert.AlertType.WARNING);
            return;
        }

        try {
            List<StudentMaster> toUpdate = new ArrayList<>();
            for (RegistrationRow row : tableData) {
                StudentMaster s = row.getStudent();
                s.setRegistrationNo(row.getNewRegNo());
                toUpdate.add(s);
            }

            studentMasterRepository.saveAll(toUpdate);
            showAlert("Save Success", "Successfully updated Registration Numbers for " + toUpdate.size() + " students.", Alert.AlertType.INFORMATION);
            handleView();
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save registration numbers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClose() {
        tableData.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
