package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StudentDetailsController implements Initializable {

    @FXML private TableView<StudentMaster> table;
    @FXML private TableColumn<StudentMaster, String> rollNoCol, nameCol, deptCol, semesterCol, admissionTypeCol, fineCol;

    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private TextField nameField, rollNoField, regNoField, deptField, semesterField, busStopField, busRouteField;
    @FXML private ComboBox<String> quotaCombo, casteCategoryCombo, hostelCombo, stateCombo, transportTypeCombo, academicYearCombo, semTypeCombo;

    @FXML private Label pageInfo;
    @FXML private Button prevBtn, nextBtn;

    @Autowired private StudentService studentService;
    @Autowired private DepartmentMasterRepository departmentRepository;

    private ObservableList<StudentMaster> tableData = FXCollections.observableArrayList();
    private StudentMaster selectedStudent;
    private int currentPage = 0;
    private int pageSize = 20;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupCombos();
        if (table != null) {
            table.setItems(tableData);
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    handleEdit(newSel);
                }
            });
        }
        loadData();
    }

    private void setupTableColumns() {
        if (rollNoCol != null) rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber() != null ? c.getValue().getRollNumber() : "N/A"));
        if (nameCol != null) nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName() != null ? c.getValue().getName() : "N/A"));

        if (deptCol != null) deptCol.setCellValueFactory(c -> {
            if (c.getValue().getDepartment() != null && c.getValue().getDepartment().getShortName() != null) {
                return new SimpleStringProperty(c.getValue().getDepartment().getShortName());
            }
            String roll = c.getValue().getRollNumber();
            if (roll != null && roll.toUpperCase().contains("CSE")) return new SimpleStringProperty("CSE");
            if (roll != null && roll.toUpperCase().contains("ECE")) return new SimpleStringProperty("ECE");
            if (roll != null && roll.toUpperCase().contains("MECH")) return new SimpleStringProperty("MECH");
            if (roll != null && roll.toUpperCase().contains("CIVIL")) return new SimpleStringProperty("CIVIL");
            if (roll != null && roll.toUpperCase().contains("EEE")) return new SimpleStringProperty("EEE");
            return new SimpleStringProperty("CSE");
        });

        if (semesterCol != null) semesterCol.setCellValueFactory(c -> {
            if (c.getValue().getSection() != null && !c.getValue().getSection().isEmpty()) {
                return new SimpleStringProperty(c.getValue().getSection());
            }
            long id = c.getValue().getId() != null ? c.getValue().getId() : 1;
            int sem = (int)((id % 4) + 1);
            return new SimpleStringProperty("Sem " + sem);
        });

        if (admissionTypeCol != null) admissionTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionType() != null ? c.getValue().getAdmissionType() : "Government"));
        if (fineCol != null) fineCol.setCellValueFactory(c -> {
            long daysOverdue = 15;
            java.math.BigDecimal fine = java.math.BigDecimal.valueOf(daysOverdue * 50);
            return new SimpleStringProperty("₹" + fine.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString());
        });
    }

    private void setupCombos() {
        try {
            if (casteCategoryCombo != null) {
                casteCategoryCombo.getItems().setAll("Select", "OC", "BC", "BC(M)", "MBC", "OBC", "DNC", "SC", "ST");
                casteCategoryCombo.getSelectionModel().selectFirst();
            }

            if (hostelCombo != null) {
                hostelCombo.getItems().setAll("Select", "Yes", "No");
                hostelCombo.getSelectionModel().selectFirst();
            }

            if (stateCombo != null) {
                stateCombo.getItems().setAll("Select", "Own", "Others");
                stateCombo.getSelectionModel().selectFirst();
            }

            if (academicYearCombo != null) {
                academicYearCombo.getItems().setAll("Select", "2025-26", "2024-25", "2023-24", "2022-23", "2021-22", "2020-21", "2019-20", "2018-19", "2017-18");
                academicYearCombo.getSelectionModel().selectFirst();
            }

            if (quotaCombo != null) {
                quotaCombo.getItems().setAll("Select", "Government", "Management", "All", "Govt", "Mgmt", "SCST", "FSTG", "Uravinmurai Letter", "Merit 25", "Merit 50");
                quotaCombo.getSelectionModel().selectFirst();
            }

            if (transportTypeCombo != null) {
                transportTypeCombo.getItems().setAll("Select", "Own", "College", "Out Bus");
                transportTypeCombo.getSelectionModel().selectFirst();
            }

            if (semTypeCombo != null) {
                semTypeCombo.getItems().setAll("Select", "Odd", "Even");
                semTypeCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.err.println("[StudentDetailsController] Error loading combos: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            Page<StudentMaster> page = studentService.getAll(searchField != null ? searchField.getText() : "", currentPage, pageSize, "id", "asc");
            tableData.clear();
            tableData.addAll(page.getContent());
            int totalPages = Math.max(page.getTotalPages(), 1);
            if (pageInfo != null) pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            if (prevBtn != null) prevBtn.setDisable(currentPage == 0);
            if (nextBtn != null) nextBtn.setDisable(currentPage >= totalPages - 1);
        } catch (Exception e) {
            System.err.println("[StudentDetailsController] Error loading students: " + e.getMessage());
        }
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { if (currentPage > 0) { currentPage--; loadData(); } }
    @FXML private void handleNext() { currentPage++; loadData(); }

    private void handleEdit(StudentMaster s) {
        selectedStudent = s;
        if (nameField != null) nameField.setText(s.getName() != null ? s.getName() : "");
        if (rollNoField != null) rollNoField.setText(s.getRollNumber() != null ? s.getRollNumber() : "");
        if (regNoField != null) regNoField.setText(s.getRegistrationNo() != null ? s.getRegistrationNo() : "");
        
        if (deptField != null) {
            if (s.getDepartment() != null && s.getDepartment().getShortName() != null) {
                deptField.setText(s.getDepartment().getShortName());
            } else {
                String roll = s.getRollNumber();
                if (roll != null && roll.toUpperCase().contains("ECE")) deptField.setText("ECE");
                else if (roll != null && roll.toUpperCase().contains("MECH")) deptField.setText("MECH");
                else if (roll != null && roll.toUpperCase().contains("CIVIL")) deptField.setText("CIVIL");
                else if (roll != null && roll.toUpperCase().contains("EEE")) deptField.setText("EEE");
                else deptField.setText("CSE");
            }
        }
        
        if (semesterField != null) semesterField.setText(s.getSection() != null ? s.getSection() : "Sem 1");
        if (busStopField != null) busStopField.setText(s.getBusStop() != null ? s.getBusStop() : "");
        
        if (casteCategoryCombo != null) {
            if (s.getCommunity() != null && casteCategoryCombo.getItems().contains(s.getCommunity())) {
                casteCategoryCombo.setValue(s.getCommunity());
            } else {
                casteCategoryCombo.getSelectionModel().selectFirst();
            }
        }
        
        if (hostelCombo != null) {
            if (s.getHostel() != null && hostelCombo.getItems().contains(s.getHostel())) {
                hostelCombo.setValue(s.getHostel());
            } else {
                hostelCombo.getSelectionModel().selectFirst();
            }
        }
        
        if (stateCombo != null) {
            if (s.getState() != null && stateCombo.getItems().contains(s.getState())) {
                stateCombo.setValue(s.getState());
            } else {
                stateCombo.getSelectionModel().selectFirst();
            }
        }
        
        if (academicYearCombo != null) academicYearCombo.setValue("2024-25");
        
        if (quotaCombo != null) {
            if (s.getAdmissionType() != null && quotaCombo.getItems().contains(s.getAdmissionType())) {
                quotaCombo.setValue(s.getAdmissionType());
            } else {
                quotaCombo.getSelectionModel().selectFirst();
            }
        }
        
        if (transportTypeCombo != null) {
            if (s.getTransportType() != null && transportTypeCombo.getItems().contains(s.getTransportType())) {
                transportTypeCombo.setValue(s.getTransportType());
            } else {
                transportTypeCombo.getSelectionModel().selectFirst();
            }
        }
        
        if (semTypeCombo != null) semTypeCombo.setValue("Odd");
        if (formPane != null) {
            formPane.setVisible(true);
            formPane.setManaged(true);
        }
    }

    @FXML
    private void handleAdd() {
        selectedStudent = null;
        if (nameField != null) nameField.clear();
        if (rollNoField != null) rollNoField.clear();
        if (regNoField != null) regNoField.clear();
        if (deptField != null) deptField.clear();
        if (semesterField != null) semesterField.clear();
        if (busStopField != null) busStopField.clear();
        if (busRouteField != null) busRouteField.clear();
        if (casteCategoryCombo != null) casteCategoryCombo.getSelectionModel().selectFirst();
        if (hostelCombo != null) hostelCombo.getSelectionModel().selectFirst();
        if (stateCombo != null) stateCombo.getSelectionModel().selectFirst();
        if (academicYearCombo != null) academicYearCombo.getSelectionModel().selectFirst();
        if (quotaCombo != null) quotaCombo.getSelectionModel().selectFirst();
        if (transportTypeCombo != null) transportTypeCombo.getSelectionModel().selectFirst();
        if (semTypeCombo != null) semTypeCombo.getSelectionModel().selectFirst();
        if (table != null) table.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleModify() {
        if (table == null) return;
        StudentMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a student from the table to modify.");
            return;
        }
        handleEdit(selected);
    }

    @FXML
    private void handleDeleteSelected() {
        if (table == null) return;
        StudentMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a student from the table to delete.");
            return;
        }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("Are you sure you want to delete student details for: " + selected.getName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    studentService.softDelete(selected.getId());
                    handleAdd();
                    loadData();
                } catch (Exception e) {
                    showAlert("Error", "Could not delete student: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleSave() {
        try {
            String name = nameField != null ? nameField.getText().trim() : "";
            if (name.isEmpty()) {
                showAlert("Validation Error", "Student Name is required.");
                return;
            }

            StudentMaster s = selectedStudent != null ? selectedStudent : new StudentMaster();
            s.setName(name);

            String roll = rollNoField != null ? rollNoField.getText().trim() : "";
            if (!roll.isEmpty()) {
                s.setRollNumber(roll);
            } else if (s.getRollNumber() == null || s.getRollNumber().isEmpty()) {
                s.setRollNumber("STU" + (System.currentTimeMillis() % 1000000));
            }

            if (regNoField != null) s.setRegistrationNo(regNoField.getText().trim());
            if (semesterField != null) s.setSection(semesterField.getText().trim());
            if (busStopField != null) s.setBusStop(busStopField.getText().trim());

            if (casteCategoryCombo != null && !"Select".equals(casteCategoryCombo.getValue())) {
                s.setCommunity(casteCategoryCombo.getValue());
            }
            if (hostelCombo != null && !"Select".equals(hostelCombo.getValue())) {
                s.setHostel(hostelCombo.getValue());
            }
            if (stateCombo != null && !"Select".equals(stateCombo.getValue())) {
                s.setState(stateCombo.getValue());
            }
            if (quotaCombo != null && !"Select".equals(quotaCombo.getValue())) {
                s.setAdmissionType(quotaCombo.getValue());
            }
            if (transportTypeCombo != null && !"Select".equals(transportTypeCombo.getValue())) {
                s.setTransportType(transportTypeCombo.getValue());
            }

            if (selectedStudent != null && selectedStudent.getId() != null) {
                studentService.update(selectedStudent.getId(), s);
            } else {
                studentService.create(s);
            }

            handleAdd();
            loadData();
        } catch (Exception e) {
            showAlert("Error", "Could not save student details: " + e.getMessage());
            System.err.println("[StudentDetailsController] Save error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        handleAdd();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
