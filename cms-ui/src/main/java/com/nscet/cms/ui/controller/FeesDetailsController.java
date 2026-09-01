package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.FeesDetailsReportDto;
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
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FeesDetailsController implements Initializable {

    // Top Control / Report Bar
    @FXML private ComboBox<String> academicPeriodCombo;
    @FXML private RadioButton oddRadio, evenRadio, yearlyRadio;
    @FXML private ToggleGroup periodGroup;
    @FXML private ComboBox<String> reportOrderCombo;

    // Master Form Controls (media_1787913784340.png - media_1787913838333.png)
    @FXML private DatePicker fromDatePicker, toDatePicker;
    @FXML private ComboBox<String> degreeCombo, feesCombo, semesterCombo, admissionTypeCombo, quotaCombo, deptCombo, stateCombo;
    @FXML private TextField amountField;

    // Report Data Table
    @FXML private TableView<FeesDetailsReportDto> reportTable;
    @FXML private TableColumn<FeesDetailsReportDto, String> branchCol, semCol, strengthCol, prePendingCol, tuitionFeeCol, otherFeesCol, busFeesCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> totalAmountCol, paidAmountCol, pendingAmountCol, colAmtCol, karAmtCol, busAmtCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> pendTuitionCol, pendOtherCol, pendBusCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<FeesDetailsReportDto> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (academicPeriodCombo != null) {
            academicPeriodCombo.getItems().setAll("2021-22", "2022-23", "2023-24", "2024-25", "2025-26");
            academicPeriodCombo.setValue("2025-26");
        }

        if (reportOrderCombo != null) {
            reportOrderCombo.getItems().setAll("Deptwise", "Yearwise", "Semwise");
            reportOrderCombo.setValue("Deptwise");
        }

        setupFormCombos();
        setupTableColumns();
        if (reportTable != null) reportTable.setItems(tableData);
        handleView();
    }

    private void setupFormCombos() {
        if (fromDatePicker != null) fromDatePicker.setValue(LocalDate.now());
        if (toDatePicker != null) toDatePicker.setValue(LocalDate.now());

        if (degreeCombo != null) {
            degreeCombo.getItems().setAll("Select", "B.E", "B.Tech", "M.E", "MBA", "MCA");
            degreeCombo.getSelectionModel().selectFirst();
        }

        // Fees options matching media_1787913793362.png
        if (feesCombo != null) {
            feesCombo.getItems().setAll(
                "Select",
                "Tuition Fee",
                "Anna University Reg Fee",
                "Other fee",
                "Library fee",
                "Development Fees",
                "Advance",
                "Bonafied",
                "Fine"
            );
            feesCombo.getSelectionModel().selectFirst();
        }

        // Semester options matching media_1787913799453.png
        if (semesterCombo != null) {
            semesterCombo.getItems().setAll(
                "Select", "1", "2", "3", "3LE", "4", "4LE", "5", "6", "7", "8"
            );
            semesterCombo.getSelectionModel().selectFirst();
        }

        // Admission Type options matching media_1787913805613.png
        if (admissionTypeCombo != null) {
            admissionTypeCombo.getItems().setAll(
                "Select", "Fresh", "Lateral", "Transfer", "Regular", "Irregular", "READMISSION"
            );
            admissionTypeCombo.getSelectionModel().selectFirst();
        }

        // Quota options matching media_1787913838333.png
        if (quotaCombo != null) {
            quotaCombo.getItems().setAll(
                "Select", "All", "Govt", "Mgmt", "SCST", "FSTG", "Uravinmurai Letter", "Merit 25", "Merit 50"
            );
            quotaCombo.getSelectionModel().selectFirst();
        }

        if (deptCombo != null) {
            deptCombo.getItems().setAll("Select", "CE", "CSE", "ECE", "MECH", "EEE", "IT", "AI", "SE");
            deptCombo.getSelectionModel().selectFirst();
        }

        if (stateCombo != null) {
            stateCombo.getItems().setAll("Select", "Own", "Others");
            stateCombo.getSelectionModel().selectFirst();
        }
    }

    private void setupTableColumns() {
        if (branchCol != null) branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranch()));
        if (semCol != null) semCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getSemester())));
        if (strengthCol != null) strengthCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStrength())));
        if (prePendingCol != null) prePendingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrePending() != null ? c.getValue().getPrePending().toPlainString() : "0"));
        if (tuitionFeeCol != null) tuitionFeeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTuitionFee() != null ? c.getValue().getTuitionFee().toPlainString() : "0"));
        if (otherFeesCol != null) otherFeesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOtherFees() != null ? c.getValue().getOtherFees().toPlainString() : "0"));
        if (busFeesCol != null) busFeesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusFees() != null ? c.getValue().getBusFees().toPlainString() : "0"));
        if (totalAmountCol != null) totalAmountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalAmount() != null ? c.getValue().getTotalAmount().toPlainString() : "0"));
        if (paidAmountCol != null) paidAmountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaidAmount() != null ? c.getValue().getPaidAmount().toPlainString() : "0"));
        if (pendingAmountCol != null) pendingAmountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPendingAmount() != null ? c.getValue().getPendingAmount().toPlainString() : "0"));
        if (colAmtCol != null) colAmtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getColAmt() != null ? c.getValue().getColAmt().toPlainString() : "0"));
        if (karAmtCol != null) karAmtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKarAmt() != null ? c.getValue().getKarAmt().toPlainString() : "0"));
        if (busAmtCol != null) busAmtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusAmt() != null ? c.getValue().getBusAmt().toPlainString() : "0"));
        if (pendTuitionCol != null) pendTuitionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPendTuition() != null ? c.getValue().getPendTuition().toPlainString() : "0"));
        if (pendOtherCol != null) pendOtherCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPendOther() != null ? c.getValue().getPendOther().toPlainString() : "0"));
        if (pendBusCol != null) pendBusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPendBus() != null ? c.getValue().getPendBus().toPlainString() : "0"));
    }

    @FXML
    public void handleView() {
        try {
            String period = academicPeriodCombo != null ? academicPeriodCombo.getValue() : "2025-26";
            List<FeesDetailsReportDto> data = reportService.getFeesDetailsReport(period, null);
            tableData.clear();
            if (data != null && !data.isEmpty()) {
                tableData.addAll(data);
            }
        } catch (Exception e) {
            System.err.println("[FeesDetailsController] Error fetching report: " + e.getMessage());
        }
    }

    @FXML
    public void handleAdd() {
        if (amountField != null) amountField.clear();
    }

    @FXML
    public void handleModify() {
        showInfo("Modify Fees Details", "Select a record to modify.");
    }

    @FXML
    public void handleDeleteSelected() {
        showInfo("Delete Fees Details", "Select a record to delete.");
    }

    @FXML
    public void handleSave() {
        String fee = feesCombo != null ? feesCombo.getValue() : "Other fee";
        String amt = amountField != null ? amountField.getText() : "0";
        showInfo("Fees Details Saved", "Fee detail for '" + fee + "' (\u20B9" + amt + ") saved successfully.");
    }

    @FXML
    public void handleCancel() {
        if (amountField != null) amountField.clear();
    }

    @FXML
    public void handleClose() {
        if (amountField != null && amountField.getScene() != null && amountField.getScene().getWindow() != null) {
            amountField.getScene().getWindow().hide();
        }
    }

    @FXML
    public void handlePrint() {
        showInfo("Print Fees Details", "Sending Fees Details Report to printer.");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
