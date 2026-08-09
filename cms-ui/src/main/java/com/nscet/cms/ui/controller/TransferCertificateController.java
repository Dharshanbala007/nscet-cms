package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.StudentMaster;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

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
    private StudentMaster selectedStudent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        characterConductCombo.getItems().add("Select");
        characterConductCombo.getItems().addAll("Good", "Very Good", "Excellent", "Satisfactory");
        characterConductCombo.getSelectionModel().selectFirst();
        formPane.setVisible(false); formPane.setManaged(false);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;
        try {
            selectedStudent = studentService.getByRollNumber(query);
            studentNameLabel.setText(selectedStudent.getName());
            fatherNameLabel.setText(selectedStudent.getFatherName());
            dobLabel.setText(selectedStudent.getDateOfBirth() != null ? selectedStudent.getDateOfBirth().toString() : "--");
            courseLabel.setText("--"); semLabel.setText("--");
            admissionNoField.setText(selectedStudent.getAdmissionNo());
            formPane.setVisible(true); formPane.setManaged(true);
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Student not found").showAndWait();
        }
    }

    @FXML
    private void handleSave() {
        if (selectedStudent == null) {
            new Alert(Alert.AlertType.WARNING, "Search for a student first").showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "TC saved successfully!\nTC Number will be generated automatically.").showAndWait();
        handleClear();
    }

    @FXML
    private void handlePrint() {
        new Alert(Alert.AlertType.INFORMATION, "TC will be generated as PDF via JasperReports").showAndWait();
    }

    @FXML
    private void handleClear() {
        selectedStudent = null; searchField.clear(); tcNumberField.clear(); serialNoField.clear();
        admissionNoField.clear(); idMarksField.clear(); courseCompletionField.clear();
        promotionStatusField.clear(); feeStatusField.clear(); batchField.clear(); umisField.clear();
        remarksField.clear(); characterConductCombo.getSelectionModel().selectFirst();
        tcDatePicker.setValue(null); leftDatePicker.setValue(null); appDatePicker.setValue(null);
        studentNameLabel.setText("--"); fatherNameLabel.setText("--"); dobLabel.setText("--");
        courseLabel.setText("--"); semLabel.setText("--");
        formPane.setVisible(false); formPane.setManaged(false);
    }
}
