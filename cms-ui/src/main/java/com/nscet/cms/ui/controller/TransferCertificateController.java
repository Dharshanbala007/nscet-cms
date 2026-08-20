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

    @FXML private TextField searchField, tcNumberField, serialNoField, admissionNoField;
    @FXML private Label studentNameLabel, fatherNameLabel, dobLabel, courseLabel, semLabel;
    @FXML private TextField idMarksField, courseCompletionField, promotionStatusField, feeStatusField;
    @FXML private TextField batchField, umisField, remarksField;
    @FXML private ComboBox<String> characterConductCombo;
    @FXML private DatePicker tcDatePicker, leftDatePicker, appDatePicker;
    @FXML private VBox formPane;

    @Autowired private StudentService studentService;
    @Autowired private TransferCertificateService tcService;

    private StudentMaster selectedStudent;
    private TransferCertificate currentTc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        characterConductCombo.getItems().clear();
        characterConductCombo.getItems().addAll("Select", "Good", "Very Good", "Excellent", "Satisfactory");
        characterConductCombo.getSelectionModel().selectFirst();
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText() != null ? searchField.getText().trim() : "";
        if (query.isEmpty()) {
            showAlert("Search Error", "Please enter a Roll Number to search.", Alert.AlertType.WARNING);
            return;
        }

        try {
            selectedStudent = studentService.getByRollNumber(query);
            studentNameLabel.setText(selectedStudent.getName());
            fatherNameLabel.setText(selectedStudent.getFatherName() != null ? selectedStudent.getFatherName() : "N/A");
            dobLabel.setText(selectedStudent.getDateOfBirth() != null ? selectedStudent.getDateOfBirth().toString() : "N/A");
            courseLabel.setText(selectedStudent.getAdmissionType() != null ? selectedStudent.getAdmissionType() : "B.E");
            semLabel.setText("8");
            admissionNoField.setText(selectedStudent.getAdmissionNo() != null ? selectedStudent.getAdmissionNo() : "N/A");

            // Check if TC already exists for student
            Optional<TransferCertificate> tcOpt = tcService.findByStudent(selectedStudent);
            if (tcOpt.isPresent()) {
                currentTc = tcOpt.get();
                tcNumberField.setText(currentTc.getTcNumber());
                serialNoField.setText(currentTc.getSerialNo());
                if (currentTc.getTcDate() != null) tcDatePicker.setValue(currentTc.getTcDate());
                if (currentTc.getDateOfLeft() != null) leftDatePicker.setValue(currentTc.getDateOfLeft());
                if (currentTc.getTcApplicationDate() != null) appDatePicker.setValue(currentTc.getTcApplicationDate());
                idMarksField.setText(currentTc.getIdMarks());
                courseCompletionField.setText(currentTc.getCourseCompletion());
                promotionStatusField.setText(currentTc.getPromotionStatus());
                feeStatusField.setText(currentTc.getFeeStatus());
                if (currentTc.getCharacterConduct() != null) characterConductCombo.setValue(currentTc.getCharacterConduct());
                batchField.setText(currentTc.getBatch());
                umisField.setText(currentTc.getUmisNo());
                remarksField.setText(currentTc.getRemarks());
            } else {
                currentTc = new TransferCertificate();
                currentTc.setStudent(selectedStudent);
                currentTc.setAcademicYear("2025-26");
                tcNumberField.setText(tcService.generateTcNumber("2025-26"));
                tcDatePicker.setValue(LocalDate.now());
                leftDatePicker.setValue(LocalDate.now());
                appDatePicker.setValue(LocalDate.now());
                courseCompletionField.setText("COURSE COMPLETED");
                promotionStatusField.setText("REFER MARKSHEET");
                feeStatusField.setText("NO DUES");
                batchField.setText("2022-2026");
                characterConductCombo.setValue("Good");
            }

            formPane.setVisible(true);
            formPane.setManaged(true);
        } catch (Exception e) {
            showAlert("Student Not Found", "No student found with Roll Number: " + query, Alert.AlertType.WARNING);
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

            currentTc.setAcademicYear("2025-26");
            currentTc.setAdmissionNo(admissionNoField.getText());
            currentTc.setSerialNo(serialNoField.getText());
            currentTc.setTcNumber(tcNumberField.getText());
            currentTc.setTcDate(tcDatePicker.getValue());
            currentTc.setDateOfLeft(leftDatePicker.getValue());
            currentTc.setTcApplicationDate(appDatePicker.getValue());
            currentTc.setIdMarks(idMarksField.getText());
            currentTc.setCourseCompletion(courseCompletionField.getText());
            currentTc.setPromotionStatus(promotionStatusField.getText());
            currentTc.setFeeStatus(feeStatusField.getText());
            currentTc.setCharacterConduct(characterConductCombo.getValue());
            currentTc.setBatch(batchField.getText());
            currentTc.setUmisNo(umisField.getText());
            currentTc.setRemarks(remarksField.getText());

            TransferCertificate saved = tcService.saveTransferCertificate(currentTc);
            currentTc = saved;
            tcNumberField.setText(saved.getTcNumber());

            showAlert("TC Saved", "Transfer Certificate saved successfully!\nTC Number: " + saved.getTcNumber(), Alert.AlertType.INFORMATION);
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
            params.put("TC_NUMBER", tcNumberField.getText());
            params.put("STUDENT_NAME", selectedStudent.getName());
            params.put("FATHER_NAME", selectedStudent.getFatherName());
            params.put("DOB", selectedStudent.getDateOfBirth() != null ? selectedStudent.getDateOfBirth().toString() : "");
            params.put("CHARACTER", characterConductCombo.getValue());

            List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("rollNumber", selectedStudent.getRollNumber());
            row.put("admissionNo", admissionNoField.getText());
            row.put("tcDate", tcDatePicker.getValue() != null ? tcDatePicker.getValue().toString() : "");
            row.put("remarks", remarksField.getText());
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
        tcNumberField.clear();
        serialNoField.clear();
        admissionNoField.clear();
        idMarksField.clear();
        courseCompletionField.clear();
        promotionStatusField.clear();
        feeStatusField.clear();
        batchField.clear();
        umisField.clear();
        remarksField.clear();
        characterConductCombo.getSelectionModel().selectFirst();
        tcDatePicker.setValue(null);
        leftDatePicker.setValue(null);
        appDatePicker.setValue(null);

        studentNameLabel.setText("--");
        fatherNameLabel.setText("--");
        dobLabel.setText("--");
        courseLabel.setText("--");
        semLabel.setText("--");

        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
