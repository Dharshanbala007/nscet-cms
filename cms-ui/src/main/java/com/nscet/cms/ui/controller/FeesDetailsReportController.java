package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.FeesDetailsReportDto;
import com.nscet.cms.reports.ReportManager;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FeesDetailsReportController implements Initializable {

    @FXML private ComboBox<String> academicPeriodCombo;
    @FXML private ComboBox<String> branchCombo;
    @FXML private RadioButton semwiseRadio;
    @FXML private RadioButton branchwiseRadio;
    @FXML private TableView<FeesDetailsReportDto> reportTable;
    @FXML private TableColumn<FeesDetailsReportDto, String> branchCol;
    @FXML private TableColumn<FeesDetailsReportDto, Number> semesterCol;
    @FXML private TableColumn<FeesDetailsReportDto, Number> strengthCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> prePendingCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> tuitionFeeCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> otherFeesCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> busFeesCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> totalAmountCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> paidAmountCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> pendingAmountCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> colAmtCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> karAmtCol;
    @FXML private TableColumn<FeesDetailsReportDto, String> busAmtCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<FeesDetailsReportDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        academicPeriodCombo.getItems().addAll("2025-26", "2024-25", "2023-24");
        academicPeriodCombo.setValue("2025-26");

        branchCombo.getItems().addAll("ALL", "MECH", "CSE", "ECE", "CE", "EEE", "IT", "AI");
        branchCombo.setValue("ALL");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranch()));
        semesterCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSemester() != null ? c.getValue().getSemester() : 0));
        strengthCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStrength() != null ? c.getValue().getStrength() : 0));
        prePendingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrePending() != null ? c.getValue().getPrePending().toString() : "0.00"));
        tuitionFeeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTuitionFee() != null ? c.getValue().getTuitionFee().toString() : "0.00"));
        otherFeesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOtherFees() != null ? c.getValue().getOtherFees().toString() : "0.00"));
        busFeesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusFees() != null ? c.getValue().getBusFees().toString() : "0.00"));
        totalAmountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalAmount() != null ? c.getValue().getTotalAmount().toString() : "0.00"));
        paidAmountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaidAmount() != null ? c.getValue().getPaidAmount().toString() : "0.00"));
        pendingAmountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPendingAmount() != null ? c.getValue().getPendingAmount().toString() : "0.00"));
        colAmtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getColAmt() != null ? c.getValue().getColAmt().toString() : "0.00"));
        karAmtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKarAmt() != null ? c.getValue().getKarAmt().toString() : "0.00"));
        busAmtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusAmt() != null ? c.getValue().getBusAmt().toString() : "0.00"));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<FeesDetailsReportDto> results = reportService.getFeesDetailsReport(
                academicPeriodCombo.getValue(), branchCombo.getValue());
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("FeeReceipt", dataList, new HashMap<>());
            new Alert(Alert.AlertType.INFORMATION, "Fees Paid and Pending Report exported (" + dataList.size() + " records).").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
        }
    }
}
