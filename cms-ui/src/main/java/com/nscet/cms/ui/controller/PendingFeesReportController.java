package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.PendingFeesDto;
import com.nscet.cms.reports.ReportManager;
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
public class PendingFeesReportController implements Initializable {

    @FXML private ComboBox<String> academicYearCombo;
    @FXML private ComboBox<String> termCombo;
    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private TableView<PendingFeesDto> reportTable;
    @FXML private TableColumn<PendingFeesDto, String> deptCol;
    @FXML private TableColumn<PendingFeesDto, String> rollNoCol;
    @FXML private TableColumn<PendingFeesDto, String> nameCol;
    @FXML private TableColumn<PendingFeesDto, String> quotaCol;
    @FXML private TableColumn<PendingFeesDto, String> admissionTypeCol;
    @FXML private TableColumn<PendingFeesDto, String> communityCol;
    @FXML private TableColumn<PendingFeesDto, String> prevPendingCol;
    @FXML private TableColumn<PendingFeesDto, String> tuitionFeesCol;
    @FXML private TableColumn<PendingFeesDto, String> otherFeesCol;
    @FXML private TableColumn<PendingFeesDto, String> totalCol;
    @FXML private TableColumn<PendingFeesDto, String> paidCol;
    @FXML private TableColumn<PendingFeesDto, String> balanceCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<PendingFeesDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        academicYearCombo.getItems().addAll("2025-26", "2024-25", "2023-24");
        academicYearCombo.setValue("2025-26");

        termCombo.getItems().addAll("Odd", "Even", "ALL");
        termCombo.setValue("Odd");

        deptCombo.getItems().addAll("ALL", "MECH", "CSE", "ECE", "CE", "EEE", "IT", "AI");
        deptCombo.setValue("MECH");

        semesterCombo.getItems().addAll("ALL", "1", "2", "3", "4", "5", "6", "7", "8");
        semesterCombo.setValue("3");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        quotaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getQuota()));
        admissionTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionType()));
        communityCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCommunity()));
        prevPendingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPreviousPending() != null ? c.getValue().getPreviousPending().toString() : "0.00"));
        tuitionFeesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTuitionFees() != null ? c.getValue().getTuitionFees().toString() : "0.00"));
        otherFeesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOtherFees() != null ? c.getValue().getOtherFees().toString() : "0.00"));
        totalCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotal() != null ? c.getValue().getTotal().toString() : "0.00"));
        paidCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaidAmount() != null ? c.getValue().getPaidAmount().toString() : "0.00"));
        balanceCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBalanceAmount() != null ? c.getValue().getBalanceAmount().toString() : "0.00"));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<PendingFeesDto> results = reportService.getPendingFees(
                academicYearCombo.getValue(),
                semesterCombo.getValue() != null && !semesterCombo.getValue().equals("ALL") ? Integer.parseInt(semesterCombo.getValue()) : null,
                deptCombo.getValue()
        );
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("PendingFeesReport", dataList, new HashMap<>());
            new Alert(Alert.AlertType.INFORMATION, "Pending Fees Report exported (" + dataList.size() + " records).").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
        }
    }
}
