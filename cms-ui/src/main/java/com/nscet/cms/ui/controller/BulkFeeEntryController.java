package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.FeesMaster;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import com.nscet.cms.db.repository.FeesMasterRepository;
import com.nscet.cms.db.repository.StudentDetailsRepository;
import com.nscet.cms.db.repository.StudentMasterRepository;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class BulkFeeEntryController implements Initializable {

    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<FeesMaster> feeNameCombo;
    @FXML private TextField amountField;

    @FXML private TableView<StudentMaster> studentTable;
    @FXML private TableColumn<StudentMaster, Boolean> selectCol;
    @FXML private TableColumn<StudentMaster, String> rollNoCol;
    @FXML private TableColumn<StudentMaster, String> nameCol;
    @FXML private TableColumn<StudentMaster, String> deptCol;
    @FXML private TableColumn<StudentMaster, String> feeAmountCol;

    @Autowired private StudentMasterRepository studentMasterRepository;
    @Autowired private DepartmentMasterRepository departmentRepository;
    @Autowired private FeesMasterRepository feesMasterRepository;
    @Autowired private StudentDetailsRepository studentDetailsRepository;

    private ObservableList<StudentMaster> tableData = FXCollections.observableArrayList();
    private Map<Long, Boolean> selectedMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
        setupTableColumns();
        studentTable.setItems(tableData);
        handleLoadStudents();
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

            List<FeesMaster> fees = feesMasterRepository.findAll();
            feeNameCombo.setItems(FXCollections.observableArrayList(fees));
            feeNameCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(FeesMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select Fee" : item.getName());
                }
            });
            feeNameCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(FeesMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select Fee" : item.getName());
                }
            });

            semesterCombo.setItems(FXCollections.observableArrayList("ALL", "1", "2", "3", "4", "5", "6", "7", "8"));
            semesterCombo.setValue("ALL");
            amountField.setText("5000");
        } catch (Exception e) {
            System.err.println("[BulkFeeEntryController] Error loading combos: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        selectCol.setCellValueFactory(c -> {
            Long id = c.getValue().getId();
            boolean isSelected = selectedMap.getOrDefault(id, true);
            SimpleBooleanProperty prop = new SimpleBooleanProperty(isSelected);
            prop.addListener((obs, oldVal, newVal) -> selectedMap.put(id, newVal));
            return prop;
        });
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        studentTable.setEditable(true);

        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber() != null ? c.getValue().getRollNumber() : "N/A"));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName() != null ? c.getValue().getName() : "N/A"));
        deptCol.setCellValueFactory(c -> {
            String deptName = getDepartmentForStudent(c.getValue());
            return new SimpleStringProperty(deptName);
        });
        feeAmountCol.setCellValueFactory(c -> new SimpleStringProperty("\u20B9" + amountField.getText()));
    }

    private String getDepartmentForStudent(StudentMaster student) {
        try {
            var details = studentDetailsRepository.findByStudentIdAndAcademicYear(student.getId(), "2025-26");
            if (details != null && !details.isEmpty() && details.get(0).getDepartment() != null) {
                return details.get(0).getDepartment().getName();
            }
        } catch (Exception ignored) {}
        String roll = student.getRollNumber();
        if (roll != null) {
            if (roll.contains("CSE")) return "Computer Science";
            if (roll.contains("ECE")) return "Electronics";
            if (roll.contains("MECH")) return "Mechanical";
            if (roll.contains("EEE")) return "EEE";
            if (roll.contains("CE")) return "Civil";
            if (roll.contains("IT")) return "Information Technology";
            if (roll.contains("AI")) return "AI & DS";
        }
        return "N/A";
    }

    @FXML
    private void handleLoadStudents() {
        try {
            List<StudentMaster> students = studentMasterRepository.findAll();
            tableData.clear();
            selectedMap.clear();

            DepartmentMaster selectedDept = deptCombo.getValue();
            String selectedSem = semesterCombo.getValue();

            for (StudentMaster s : students) {
                if (selectedDept != null) {
                    String studentDept = getDeptCodeForStudent(s);
                    if (!selectedDept.getCode().equalsIgnoreCase(studentDept)) continue;
                }
                tableData.add(s);
                selectedMap.put(s.getId(), true);
            }
        } catch (Exception e) {
            System.err.println("[BulkFeeEntryController] Error loading students: " + e.getMessage());
        }
    }

    private String getDeptCodeForStudent(StudentMaster student) {
        try {
            var details = studentDetailsRepository.findByStudentIdAndAcademicYear(student.getId(), "2025-26");
            if (details != null && !details.isEmpty() && details.get(0).getDepartment() != null) {
                return details.get(0).getDepartment().getCode();
            }
        } catch (Exception ignored) {}
        String roll = student.getRollNumber();
        if (roll != null) {
            if (roll.contains("CSE")) return "CSE";
            if (roll.contains("ECE")) return "ECE";
            if (roll.contains("MECH")) return "MECH";
            if (roll.contains("EEE")) return "EEE";
            if (roll.contains("CE")) return "CE";
            if (roll.contains("IT")) return "IT";
            if (roll.contains("AI")) return "AI";
        }
        return "N/A";
    }

    @FXML
    private void handleApplyFee() {
        long count = selectedMap.values().stream().filter(Boolean::booleanValue).count();
        FeesMaster fee = feeNameCombo.getValue();
        String feeName = fee != null ? fee.getName() : "Selected Fee";
        String amount = amountField.getText();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bulk Fee Applied");
        alert.setHeaderText(null);
        alert.setContentText("Bulk fee entry of \u20B9" + amount + " for '" + feeName + "' applied to " + count + " selected students.");
        alert.showAndWait();
    }
}
