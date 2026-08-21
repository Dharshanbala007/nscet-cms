package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.StudentDetails;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.StudentDetailsRepository;
import com.nscet.cms.db.repository.StudentMasterRepository;
import com.nscet.cms.reports.ReportManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class TcPrintController implements Initializable {

    @FXML private ComboBox<String> academicYearCombo;
    @FXML private TextField serialNoField;
    @FXML private TextField studentSearchField;
    @FXML private DatePicker tcDateField;
    @FXML private DatePicker dateLeftField;
    @FXML private DatePicker applicationDateField;
    @FXML private TextField characterField;
    @FXML private TextArea tcPreviewArea;

    @Autowired private StudentMasterRepository studentMasterRepository;
    @Autowired private StudentDetailsRepository studentDetailsRepository;

    private StudentMaster currentStudent = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        academicYearCombo.getItems().addAll("2025-26", "2024-25", "2023-24");
        academicYearCombo.setValue("2025-26");

        serialNoField.setText("TC-2025-084");
        characterField.setText("GOOD");
        tcDateField.setValue(LocalDate.now());
        dateLeftField.setValue(LocalDate.now().minusDays(10));
        applicationDateField.setValue(LocalDate.now().minusDays(15));
    }

    @FXML
    private void handleSearch() {
        String roll = studentSearchField.getText();
        if (roll == null || roll.trim().isEmpty()) {
            showAlert("Search Error", "Please enter a Roll Number or Name.", Alert.AlertType.WARNING);
            return;
        }

        try {
            String query = roll.trim();
            Optional<StudentMaster> studentOpt = studentMasterRepository.findByRollNumber(query);
            if (studentOpt.isEmpty()) {
                var page = studentMasterRepository.search(query, org.springframework.data.domain.PageRequest.of(0, 1));
                if (page.hasContent()) {
                    studentOpt = Optional.of(page.getContent().get(0));
                }
            }

            if (studentOpt.isEmpty()) {
                showAlert("Not Found", "No student found matching: " + query, Alert.AlertType.INFORMATION);
                return;
            }

            currentStudent = studentOpt.get();
            String deptName = "N/A";
            String degree = "B.E.";
            String semStr = "N/A";

            try {
                List<StudentDetails> details = studentDetailsRepository.findByStudentIdAndAcademicYear(
                        currentStudent.getId(), academicYearCombo.getValue());
                if (details != null && !details.isEmpty()) {
                    StudentDetails sd = details.get(0);
                    if (sd.getDepartment() != null) deptName = sd.getDepartment().getName();
                    if (sd.getDegree() != null) degree = sd.getDegree();
                    if (sd.getSemester() != null) semStr = romanNumeral(sd.getSemester());
                }
            } catch (Exception ignored) {}

            StringBuilder sb = new StringBuilder();
            sb.append("===============================================================\n");
            sb.append("         NADAR SARASWATHI COLLEGE OF ENGG & TECH, THENI        \n");
            sb.append("                TRANSFER & CONDUCT CERTIFICATE                  \n");
            sb.append("===============================================================\n");
            sb.append("TC Serial No      : ").append(serialNoField.getText()).append("\n");
            sb.append("Academic Year     : ").append(academicYearCombo.getValue()).append("\n");
            sb.append("Student Roll No   : ").append(currentStudent.getRollNumber()).append("\n");
            sb.append("Student Name      : ").append(currentStudent.getName() != null ? currentStudent.getName() : "N/A").append("\n");
            sb.append("Father's Name     : ").append(currentStudent.getFatherName() != null ? currentStudent.getFatherName() : "N/A").append("\n");
            sb.append("Date of Birth     : ").append(currentStudent.getDateOfBirth() != null
                    ? currentStudent.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A").append("\n");
            sb.append("Course & Dept     : ").append(degree).append(" ").append(deptName).append("\n");
            sb.append("Date of Leaving   : ").append(dateLeftField.getValue()).append("\n");
            sb.append("TC Application Dt : ").append(applicationDateField.getValue()).append("\n");
            sb.append("TC Issue Date     : ").append(tcDateField.getValue()).append("\n");
            sb.append("Character & Conduct: ").append(characterField.getText().toUpperCase()).append("\n");
            sb.append("===============================================================\n");

            tcPreviewArea.setText(sb.toString());
        } catch (Exception e) {
            showAlert("Error", "Error searching student: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String romanNumeral(int sem) {
        switch (sem) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            default: return String.valueOf(sem);
        }
    }

    @FXML
    private void handlePrint() {
        try {
            ReportManager.printReport("TransferCertificate", Collections.emptyList(), new HashMap<>());
            new Alert(Alert.AlertType.INFORMATION, "Transfer & Conduct Certificate print job sent.").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Print failed: " + e.getMessage()).showAndWait();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
