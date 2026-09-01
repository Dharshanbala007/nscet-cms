package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService.TcPrintDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class TcDocumentController implements Initializable {

    @FXML private Label studentNavLabel;
    @FXML private VBox tcPaper;

    @FXML private Label certTitleLabel;
    @FXML private Label tcNoLabel;
    @FXML private Label admissionNoLabel;
    @FXML private Label rollNoLabel;
    @FXML private Label umisNoLabel;

    @FXML private Label studentNameLabel;
    @FXML private Label fatherNameLabel;
    @FXML private Label nationalityReligionCasteLabel;
    @FXML private Label communityLabel;
    @FXML private Label genderLabel;
    @FXML private Label dobLabel;
    @FXML private Label courseAdmittedLabel;
    @FXML private Label deptLabel;
    @FXML private Label courseCompletedLabel;
    @FXML private Label promotionLabel;
    @FXML private Label feesPaidLabel;
    @FXML private Label dateLeftLabel;
    @FXML private Label dateAppLabel;
    @FXML private Label dateIssuedLabel;
    @FXML private Label conductLabel;

    private final List<TcPrintDto> tcList = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setTcList(List<TcPrintDto> list) {
        tcList.clear();
        if (list != null && !list.isEmpty()) {
            tcList.addAll(list);
        }
        currentIndex = 0;
        updateView();
    }

    private void updateView() {
        if (tcList.isEmpty()) {
            studentNavLabel.setText("No Student TC Records");
            return;
        }

        studentNavLabel.setText("Student " + (currentIndex + 1) + " of " + tcList.size());
        TcPrintDto dto = tcList.get(currentIndex);

        String certType = dto.getCertType() != null ? dto.getCertType() : "TC";
        if (certTitleLabel != null) {
            if ("CC".equalsIgnoreCase(certType) || "Conduct Letter".equalsIgnoreCase(certType)) {
                certTitleLabel.setText("CONDUCT & CHARACTER CERTIFICATE");
            } else {
                certTitleLabel.setText("TRANSFER CERTIFICATE");
            }
        }

        tcNoLabel.setText(dto.getTcNo() != null ? dto.getTcNo() : "");
        studentNameLabel.setText(dto.getStudentName() != null ? dto.getStudentName() : "");
        fatherNameLabel.setText("");
        nationalityReligionCasteLabel.setText("");
        communityLabel.setText("");
        dobLabel.setText("");
        courseAdmittedLabel.setText("");
        deptLabel.setText(dto.getDept() != null ? dto.getDept() : "");
        if (courseCompletedLabel != null) courseCompletedLabel.setText("");
        promotionLabel.setText("");
        feesPaidLabel.setText("");
        dateLeftLabel.setText("");
        if (dateAppLabel != null) dateAppLabel.setText("");
        dateIssuedLabel.setText(dto.getIssueDate() != null ? dto.getIssueDate() : "");
        conductLabel.setText("");

        admissionNoLabel.setText(dto.getRollNo() != null ? dto.getRollNo() : "");
        umisNoLabel.setText("");
    }

    @FXML
    private void handlePrevStudent() {
        if (currentIndex > 0) {
            currentIndex--;
            updateView();
        }
    }

    @FXML
    private void handleNextStudent() {
        if (currentIndex < tcList.size() - 1) {
            currentIndex++;
            updateView();
        }
    }

    @FXML
    private void handlePrintCurrent() {
        if (tcPaper == null) return;
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(tcPaper.getScene().getWindow())) {
            double scaleX = job.getJobSettings().getPageLayout().getPrintableWidth() / tcPaper.getBoundsInParent().getWidth();
            double scaleY = job.getJobSettings().getPageLayout().getPrintableHeight() / tcPaper.getBoundsInParent().getHeight();
            double scale = Math.min(scaleX, scaleY);

            tcPaper.getTransforms().add(new Scale(scale, scale));
            boolean printed = job.printPage(tcPaper);
            tcPaper.getTransforms().clear();

            if (printed) {
                job.endJob();
                showAlert("Success", "Transfer Certificate printed successfully!", Alert.AlertType.INFORMATION);
            }
        }
    }

    @FXML
    private void handlePrintAll() {
        showAlert("Information", "Printing batch of " + tcList.size() + " student Transfer Certificates...", Alert.AlertType.INFORMATION);
        for (int i = 0; i < tcList.size(); i++) {
            currentIndex = i;
            updateView();
            handlePrintCurrent();
        }
    }

    @FXML
    private void handleClose() {
        if (tcPaper != null && tcPaper.getScene() != null) {
            Stage stage = (Stage) tcPaper.getScene().getWindow();
            stage.close();
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
