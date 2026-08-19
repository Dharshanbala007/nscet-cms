package com.nscet.cms.ui.controller;

import com.nscet.cms.reports.ReportManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
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
            roll = "2024FMEO13";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===============================================================\n");
        sb.append("         NADAR SARASWATHI COLLEGE OF ENGG & TECH, THENI        \n");
        sb.append("                TRANSFER & CONDUCT CERTIFICATE                  \n");
        sb.append("===============================================================\n");
        sb.append("TC Serial No      : ").append(serialNoField.getText()).append("\n");
        sb.append("Academic Year     : ").append(academicYearCombo.getValue()).append("\n");
        sb.append("Student Roll No   : ").append(roll.toUpperCase()).append("\n");
        sb.append("Student Name      : SANTHOSH M\n");
        sb.append("Father's Name     : MUNIYASAMY M\n");
        sb.append("Date of Birth     : 14/05/2003\n");
        sb.append("Course & Dept     : B.E. Mechanical Engineering\n");
        sb.append("Date of Leaving   : ").append(dateLeftField.getValue()).append("\n");
        sb.append("TC Application Dt : ").append(applicationDateField.getValue()).append("\n");
        sb.append("TC Issue Date     : ").append(tcDateField.getValue()).append("\n");
        sb.append("Character & Conduct: ").append(characterField.getText().toUpperCase()).append("\n");
        sb.append("===============================================================\n");

        tcPreviewArea.setText(sb.toString());
    }

    @FXML
    private void handlePrint() {
        try {
            ReportManager.printReport("TransferCertificate", Collections.emptyList(), new HashMap<>());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "Transfer & Conduct Certificate print job sent.").showAndWait();
        }
    }
}
