package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.core.service.TransferCertificateService;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.entity.TransferCertificate;
import com.nscet.cms.reports.ReportManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Component
@Scope("prototype")
public class TransferCertificateController implements Initializable {

    @FXML private TextField searchField, serialNoField, studentNameField, regNoField, admissionNoField, fatherNameField;
    @FXML private TextField nationalityField, religionField, casteField, dobInWordsField, courseField, branchField;
    @FXML private TextField idMarksField, idMarks2Field, courseCompletedField, qualifiedPromotionField, paidAllFeesField;
    @FXML private TextField characterField, batchDetailField, umisField;
    @FXML private ComboBox<String> academicYearCombo, communityCombo, batchCombo;
    @FXML private DatePicker dobPicker, leftDatePicker, appDatePicker, tcDatePicker;
    @FXML private Label studentNameLabel, fatherNameLabel, dobLabel, courseLabel, semLabel;
    @FXML private VBox formPane;

    @Autowired private StudentService studentService;
    @Autowired private TransferCertificateService tcService;

    private StudentMaster selectedStudent;
    private TransferCertificate currentTc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (academicYearCombo != null) {
            academicYearCombo.getItems().clear();
            academicYearCombo.getItems().addAll("Select", "2025-26", "2024-25", "2023-24", "2022-23");
            academicYearCombo.getSelectionModel().select(1);
        }
        if (communityCombo != null) {
            communityCombo.getItems().clear();
            communityCombo.getItems().addAll("Select", "OC", "BC", "BC(M)", "MBC", "OBC", "DNC", "SC", "ST");
            communityCombo.getSelectionModel().select(2);
        }
        if (batchCombo != null) {
            batchCombo.getItems().clear();
            batchCombo.getItems().addAll("Select", "2022-2026", "2021-2025", "2020-2024", "2019-2023");
            batchCombo.getSelectionModel().select(1);
        }
        if (dobPicker != null) dobPicker.setValue(LocalDate.of(2004, 5, 15));
        if (leftDatePicker != null) leftDatePicker.setValue(LocalDate.now());
        if (appDatePicker != null) appDatePicker.setValue(LocalDate.now());
        if (tcDatePicker != null) tcDatePicker.setValue(LocalDate.now());

        formPane.setVisible(true);
        formPane.setManaged(true);

        try {
            org.springframework.data.domain.Page<StudentMaster> page = studentService.getAll("", 0, 1, "id", "asc");
            if (page.hasContent()) {
                StudentMaster s = page.getContent().get(0);
                searchField.setText(s.getRollNumber() != null ? s.getRollNumber() : "");
                populateStudentDetails(s);
            }
        } catch (Exception e) {
            System.err.println("[TransferCertificateController] Pre-load student info: " + e.getMessage());
        }
    }

    private void populateStudentDetails(StudentMaster student) {
        if (student == null) return;
        selectedStudent = student;
        studentNameLabel.setText(selectedStudent.getName());
        fatherNameLabel.setText(selectedStudent.getFatherName() != null ? selectedStudent.getFatherName() : "N/A");
        dobLabel.setText(selectedStudent.getDateOfBirth() != null ? selectedStudent.getDateOfBirth().toString() : "N/A");
        courseLabel.setText(selectedStudent.getAdmissionType() != null ? selectedStudent.getAdmissionType() : "B.E");
        semLabel.setText("8");

        studentNameField.setText(selectedStudent.getName());
        fatherNameField.setText(selectedStudent.getFatherName() != null ? selectedStudent.getFatherName() : "");
        regNoField.setText(selectedStudent.getRegistrationNo() != null ? selectedStudent.getRegistrationNo() : "");
        admissionNoField.setText(selectedStudent.getAdmissionNo() != null ? selectedStudent.getAdmissionNo() : "");
        casteField.setText(selectedStudent.getCaste() != null ? selectedStudent.getCaste() : "");
        courseField.setText("B.E - Bachelor of Engineering");
        branchField.setText("Computer Science and Engineering");
        courseCompletedField.setText("Passed - First Class with Distinction");
        qualifiedPromotionField.setText("Yes - Qualified for Higher Studies");
        paidAllFeesField.setText("Yes");

        try {
            Optional<TransferCertificate> tcOpt = tcService.findByStudent(selectedStudent);
            if (tcOpt.isPresent()) {
                currentTc = tcOpt.get();
                serialNoField.setText(currentTc.getSerialNo());
                if (currentTc.getTcDate() != null) tcDatePicker.setValue(currentTc.getTcDate());
                if (currentTc.getDateOfLeft() != null) leftDatePicker.setValue(currentTc.getDateOfLeft());
                if (currentTc.getTcApplicationDate() != null) appDatePicker.setValue(currentTc.getTcApplicationDate());
                idMarksField.setText(currentTc.getIdMarks());
                characterField.setText(currentTc.getCharacterConduct() != null ? currentTc.getCharacterConduct() : "Good");
                umisField.setText(currentTc.getUmisNo());
            } else {
                currentTc = new TransferCertificate();
                currentTc.setStudent(selectedStudent);
                currentTc.setAcademicYear("2025-26");
                tcDatePicker.setValue(LocalDate.now());
                leftDatePicker.setValue(LocalDate.now());
                appDatePicker.setValue(LocalDate.now());
                characterField.setText("Good");
            }
        } catch (Exception e) {
            System.err.println("[TransferCertificateController] Error populating TC: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText() != null ? searchField.getText().trim() : "";
        if (query.isEmpty()) {
            showAlert("Search Error", "Please enter a Roll Number to search.", Alert.AlertType.WARNING);
            return;
        }

        try {
            StudentMaster s = studentService.getByRollNumber(query);
            populateStudentDetails(s);
        } catch (Exception e) {
            showAlert("Student Not Found", "No student found matching: " + query, Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleSave() {
        if (selectedStudent == null) {
            showAlert("Validation Error", "Please search for a student first.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (currentTc == null) {
                currentTc = new TransferCertificate();
                currentTc.setStudent(selectedStudent);
            }

            currentTc.setAcademicYear(academicYearCombo.getValue());
            currentTc.setAdmissionNo(admissionNoField.getText());
            currentTc.setSerialNo(serialNoField.getText());
            currentTc.setTcDate(tcDatePicker.getValue());
            currentTc.setDateOfLeft(leftDatePicker.getValue());
            currentTc.setTcApplicationDate(appDatePicker.getValue());
            currentTc.setIdMarks(idMarksField.getText());
            currentTc.setCourseCompletion(courseCompletedField.getText());
            currentTc.setPromotionStatus(qualifiedPromotionField.getText());
            currentTc.setFeeStatus(paidAllFeesField.getText());
            currentTc.setCharacterConduct(characterField.getText());
            currentTc.setBatch(batchCombo.getValue());
            currentTc.setUmisNo(umisField.getText());

            TransferCertificate saved = tcService.saveTransferCertificate(currentTc);
            currentTc = saved;

            showAlert("TC Saved", "Transfer Certificate saved successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save Transfer Certificate: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrint() {
        if (selectedStudent == null) {
            showAlert("Validation Error", "Please search and save Transfer Certificate first.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("COLLEGE_NAME", "Nadar Saraswathi College of Engineering and Technology");
            params.put("COLLEGE_LOCATION", "Theni");
            params.put("TC_NUMBER", serialNoField.getText());
            params.put("STUDENT_NAME", selectedStudent.getName());
            params.put("FATHER_NAME", selectedStudent.getFatherName());
            params.put("DOB", selectedStudent.getDateOfBirth() != null ? selectedStudent.getDateOfBirth().toString() : "");
            params.put("CHARACTER", characterField.getText());

            List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("rollNumber", selectedStudent.getRollNumber());
            row.put("admissionNo", admissionNoField.getText());
            row.put("tcDate", tcDatePicker.getValue() != null ? tcDatePicker.getValue().toString() : "");
            row.put("remarks", umisField.getText());
            data.add(row);

            ReportManager.printReport("TransferCertificate", data, params);
        } catch (Exception e) {
            showAlert("Print Error", "Failed to print Transfer Certificate: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        selectedStudent = null;
        currentTc = null;
        searchField.clear();
        serialNoField.clear();
        studentNameField.clear();
        regNoField.clear();
        admissionNoField.clear();
        fatherNameField.clear();
        casteField.clear();
        dobInWordsField.clear();
        courseField.clear();
        branchField.clear();
        idMarksField.clear();
        idMarks2Field.clear();
        courseCompletedField.clear();
        qualifiedPromotionField.clear();
        paidAllFeesField.clear();
        characterField.clear();
        batchDetailField.clear();
        umisField.clear();

        academicYearCombo.getSelectionModel().select(1);
        communityCombo.getSelectionModel().select(2);
        batchCombo.getSelectionModel().select(1);
        dobPicker.setValue(null);
        tcDatePicker.setValue(null);
        leftDatePicker.setValue(null);
        appDatePicker.setValue(null);

        studentNameLabel.setText("--");
        fatherNameLabel.setText("--");
        dobLabel.setText("--");
        courseLabel.setText("--");
        semLabel.setText("--");

        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
