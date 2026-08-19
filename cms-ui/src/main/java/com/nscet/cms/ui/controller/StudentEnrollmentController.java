package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.StudentMasterRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StudentEnrollmentController implements Initializable {

    @FXML private TextField rollNoField;
    @FXML private Label nameLabel;
    @FXML private Label deptLabel;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<String> academicYearCombo;

    @FXML private TableView<StudentMaster> enrollmentTable;
    @FXML private TableColumn<StudentMaster, String> slNoCol;
    @FXML private TableColumn<StudentMaster, String> rollNoCol;
    @FXML private TableColumn<StudentMaster, String> nameCol;
    @FXML private TableColumn<StudentMaster, String> deptCol;
    @FXML private TableColumn<StudentMaster, String> semesterCol;
    @FXML private TableColumn<StudentMaster, String> enrollmentDateCol;

    @Autowired private StudentMasterRepository studentMasterRepository;

    private ObservableList<StudentMaster> tableData = FXCollections.observableArrayList();
    private StudentMaster selectedStudent = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
        setupTableColumns();
        enrollmentTable.setItems(tableData);
        loadEnrollmentHistory();
    }

    private void setupCombos() {
        try {
            semesterCombo.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8"));
            semesterCombo.setValue("1");
            academicYearCombo.setItems(FXCollections.observableArrayList("2024-25", "2025-26", "2026-27"));
            academicYearCombo.setValue("2025-26");
        } catch (Exception e) {
            System.err.println("[StudentEnrollmentController] Error loading combos: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        slNoCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(tableData.indexOf(c.getValue()) + 1)));
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber() != null ? c.getValue().getRollNumber() : "N/A"));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName() != null ? c.getValue().getName() : "N/A"));
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCommunity() != null ? c.getValue().getCommunity() : "CSE"));
        semesterCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedium() != null ? c.getValue().getMedium() : "1"));
        enrollmentDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
    }

    private void loadEnrollmentHistory() {
        try {
            List<StudentMaster> students = studentMasterRepository.findAll();
            tableData.clear();
            tableData.addAll(students);
        } catch (Exception e) {
            System.err.println("[StudentEnrollmentController] Error loading enrollment history: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String rollNo = rollNoField.getText() != null ? rollNoField.getText().trim() : "";
        if (rollNo.isEmpty()) {
            showAlert("Search Error", "Please enter a Roll Number.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Optional<StudentMaster> studentOpt = studentMasterRepository.findByRollNumber(rollNo);
            if (studentOpt.isPresent()) {
                selectedStudent = studentOpt.get();
                nameLabel.setText(selectedStudent.getName());
                deptLabel.setText(selectedStudent.getCommunity() != null ? selectedStudent.getCommunity() : "CSE");
            } else {
                showAlert("Not Found", "No student found with Roll Number: " + rollNo, Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("Search Error", "Error searching student: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEnroll() {
        String rollNo = rollNoField.getText() != null ? rollNoField.getText().trim() : "";
        if (rollNo.isEmpty()) {
            showAlert("Enrollment Error", "Please search and select a student first.", Alert.AlertType.WARNING);
            return;
        }

        String sem = semesterCombo.getValue();
        String year = academicYearCombo.getValue();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Enrollment Complete");
        alert.setHeaderText(null);
        alert.setContentText("Student " + (selectedStudent != null ? selectedStudent.getName() : rollNo) + " successfully enrolled in Semester " + sem + " for Academic Year " + year + ".");
        alert.showAndWait();

        loadEnrollmentHistory();
    }

    @FXML
    private void handleClear() {
        rollNoField.clear();
        nameLabel.setText("--");
        deptLabel.setText("--");
        selectedStudent = null;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
