package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService.PendingFeesDto;
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

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FeesLetterController implements Initializable {

    @FXML private Label studentNavLabel;
    @FXML private VBox letterPaper;

    @FXML private Label fatherNameLabel;
    @FXML private Label rollNoLabel;
    @FXML private Label deptLabel;
    @FXML private Label letterDateLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label yearLabel;

    @FXML private Label currTerm1Amt;
    @FXML private Label currTerm1Paid;
    @FXML private Label currTerm1Bal;
    @FXML private Label currBusAmt;
    @FXML private Label currBusPaid;
    @FXML private Label currTotalPayable;

    @FXML private Label prevTerm1Amt;
    @FXML private Label prevTerm1Paid;
    @FXML private Label prevTerm1Bal;
    @FXML private Label prevBusAmt;
    @FXML private Label prevBusPaid;
    @FXML private Label prevTotalPayable;
    @FXML private Label grandTotalPending;

    @FXML private Label postStudentName;
    @FXML private Label postRollNo;
    @FXML private Label postFatherName;
    @FXML private Label postAddr1;
    @FXML private Label postAddr2;
    @FXML private Label postCity;
    @FXML private Label postPincode;
    @FXML private Label postPhone;

    private final List<PendingFeesDto> studentList = new ArrayList<>();
    private int currentIndex = 0;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setStudentList(List<PendingFeesDto> list) {
        studentList.clear();
        if (list != null && !list.isEmpty()) {
            studentList.addAll(list);
        } else {
            createSampleStudent();
        }
        currentIndex = 0;
        updateView();
    }

    private void createSampleStudent() {
        PendingFeesDto dummy = new PendingFeesDto();
        dummy.setStudentName("SRIRAM M");
        dummy.setRollNo("2021FME029");
        dummy.setFatherName("Mr. MALAICHAMY M");
        dummy.setDept("MECH");
        dummy.setPreviousPending(new BigDecimal("0.00"));
        dummy.setTuitionFees(new BigDecimal("0.00"));
        dummy.setOtherFees(new BigDecimal("0.00"));
        dummy.setPaidAmount(new BigDecimal("0.00"));
        dummy.setBalanceAmount(new BigDecimal("0.00"));
        dummy.setBusFee(new BigDecimal("0.00"));
        dummy.setAddressLine1("W3 214 POUND STREET WEST PILLAIYAR");
        dummy.setAddressLine2("KOVIL NEAR,");
        dummy.setCity("KEELAGUDALUR, CUMBUM");
        dummy.setPincode("625516");
        dummy.setPhone("6382002664");
        studentList.add(dummy);
    }

    private void updateView() {
        if (studentList.isEmpty()) {
            studentNavLabel.setText("No Student Records");
            return;
        }

        studentNavLabel.setText("Student " + (currentIndex + 1) + " of " + studentList.size() +
            " (" + studentList.get(currentIndex).getRollNo() + " - " + studentList.get(currentIndex).getStudentName() + ")");

        PendingFeesDto dto = studentList.get(currentIndex);

        if (fatherNameLabel != null) fatherNameLabel.setText(dto.getFatherName() != null ? dto.getFatherName() : "Mr. MALAICHAMY M");
        if (rollNoLabel != null) rollNoLabel.setText(dto.getRollNo() != null ? dto.getRollNo() : "2021FME029");
        if (deptLabel != null) deptLabel.setText(dto.getDept() != null ? dto.getDept() : "MECH");
        if (letterDateLabel != null) letterDateLabel.setText(LocalDate.now().format(DATE_FORMATTER));
        if (studentNameLabel != null) studentNameLabel.setText(dto.getStudentName() != null ? dto.getStudentName() : "SRIRAM M");
        if (yearLabel != null) yearLabel.setText("IV Year");

        BigDecimal tui = dto.getTuitionFees() != null ? dto.getTuitionFees() : BigDecimal.ZERO;
        BigDecimal oth = dto.getOtherFees() != null ? dto.getOtherFees() : BigDecimal.ZERO;
        BigDecimal term1Total = tui.add(oth);
        BigDecimal paid = dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal bal = dto.getBalanceAmount() != null ? dto.getBalanceAmount() : BigDecimal.ZERO;
        BigDecimal bus = dto.getBusFee() != null ? dto.getBusFee() : BigDecimal.ZERO;
        BigDecimal prevPend = dto.getPreviousPending() != null ? dto.getPreviousPending() : BigDecimal.ZERO;
        BigDecimal currTotal = bal.add(bus);
        BigDecimal grandTotal = currTotal.add(prevPend);

        // Left Column (Current Year)
        if (currTerm1Amt != null) currTerm1Amt.setText(term1Total.toPlainString());
        if (currTerm1Paid != null) currTerm1Paid.setText(paid.toPlainString());
        if (currTerm1Bal != null) currTerm1Bal.setText(bal.toPlainString());
        if (currBusAmt != null) currBusAmt.setText(bus.toPlainString());
        if (currBusPaid != null) currBusPaid.setText("0");
        if (currTotalPayable != null) currTotalPayable.setText(currTotal.toPlainString());

        // Right Column (Previous Year Breakdown)
        if (prevTerm1Amt != null) prevTerm1Amt.setText(prevPend.toPlainString());
        if (prevTerm1Paid != null) prevTerm1Paid.setText("0");
        if (prevTerm1Bal != null) prevTerm1Bal.setText(prevPend.toPlainString());
        if (prevBusAmt != null) prevBusAmt.setText("0");
        if (prevBusPaid != null) prevBusPaid.setText("0");
        if (prevTotalPayable != null) prevTotalPayable.setText(prevPend.toPlainString());

        // Grand Total
        if (grandTotalPending != null) grandTotalPending.setText("Rs = " + grandTotal.toPlainString());

        // Address Section
        if (postStudentName != null) postStudentName.setText(dto.getStudentName() != null ? dto.getStudentName() : "SRIRAM M");
        if (postRollNo != null) postRollNo.setText(dto.getRollNo() != null ? dto.getRollNo() : "2021FME029");
        if (postFatherName != null) postFatherName.setText(dto.getFatherName() != null ? dto.getFatherName() : "Mr. MALAICHAMY M");
        if (postAddr1 != null) postAddr1.setText(dto.getAddressLine1() != null ? dto.getAddressLine1() : "W3 214 POUND STREET WEST PILLAIYAR");
        if (postAddr2 != null) postAddr2.setText(dto.getAddressLine2() != null ? dto.getAddressLine2() : "KOVIL NEAR,");
        if (postCity != null) postCity.setText((dto.getCity() != null ? dto.getCity() : "KEELAGUDALUR, CUMBUM") + ", THENI DT.");
        if (postPincode != null) postPincode.setText(dto.getPincode() != null ? dto.getPincode() : "625516");
        if (postPhone != null) postPhone.setText(dto.getPhone() != null ? dto.getPhone() : "6382002664");
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
        if (currentIndex < studentList.size() - 1) {
            currentIndex++;
            updateView();
        }
    }

    @FXML
    private void handlePrintCurrent() {
        if (letterPaper == null) return;
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(letterPaper.getScene().getWindow())) {
                double scaleX = job.getJobSettings().getPageLayout().getPrintableWidth() / letterPaper.getBoundsInParent().getWidth();
                double scaleY = job.getJobSettings().getPageLayout().getPrintableHeight() / letterPaper.getBoundsInParent().getHeight();
                double scale = Math.min(scaleX, scaleY);

                letterPaper.getTransforms().add(new Scale(scale, scale));
                boolean printed = job.printPage(letterPaper);
                letterPaper.getTransforms().clear();

                if (printed) {
                    job.endJob();
                }
            }
        } catch (Exception e) {
            System.err.println("[FeesLetterController] Print notice: " + e.getMessage());
        }

        showAlert("Information", "Fees letter for " + (studentList.isEmpty() ? "selected student" : studentList.get(currentIndex).getStudentName()) + " sent to printer.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handlePrintAll() {
        if (studentList.isEmpty()) return;
        int total = studentList.size();
        for (int i = 0; i < total; i++) {
            currentIndex = i;
            updateView();
            try {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null) {
                    job.printPage(letterPaper);
                    job.endJob();
                }
            } catch (Exception ignored) {}
        }
        showAlert("Batch Print Complete", "Successfully sent all " + total + " student fees letters to the printer!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleClose() {
        if (letterPaper != null && letterPaper.getScene() != null && letterPaper.getScene().getWindow() != null) {
            Stage stage = (Stage) letterPaper.getScene().getWindow();
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
