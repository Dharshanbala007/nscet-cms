package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.ExamFeesReportDto;
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
public class ExamFeesReportController implements Initializable {

    @FXML private ComboBox<String> academicYearCombo;
    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private TableView<ExamFeesReportDto> reportTable;
    @FXML private TableColumn<ExamFeesReportDto, String> deptCol;
    @FXML private TableColumn<ExamFeesReportDto, String> rollNoCol;
    @FXML private TableColumn<ExamFeesReportDto, String> studentNameCol;
    @FXML private TableColumn<ExamFeesReportDto, String> examNameCol;
    @FXML private TableColumn<ExamFeesReportDto, String> amountCol;
    @FXML private TableColumn<ExamFeesReportDto, String> paidCol;
    @FXML private TableColumn<ExamFeesReportDto, String> balanceCol;
    @FXML private TableColumn<ExamFeesReportDto, String> statusCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<ExamFeesReportDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        academicYearCombo.getItems().addAll("2025-26", "2024-25", "2023-24");
        academicYearCombo.setValue("2025-26");

        deptCombo.getItems().addAll("ALL", "CSE", "ECE", "MECH", "CE", "EEE", "IT", "AI");
        deptCombo.setValue("ALL");

        semesterCombo.getItems().addAll("ALL", "1", "2", "3", "4", "5", "6", "7", "8");
        semesterCombo.setValue("ALL");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));
        studentNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        examNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExamName()));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount() != null ? c.getValue().getAmount().toString() : "0.00"));
        paidCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaidAmount() != null ? c.getValue().getPaidAmount().toString() : "0.00"));
        balanceCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBalanceAmount() != null ? c.getValue().getBalanceAmount().toString() : "0.00"));
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<ExamFeesReportDto> results = reportService.getExamFeesReport(deptCombo.getValue(), null);
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("PendingFeesReport", dataList, new HashMap<>());
            new Alert(Alert.AlertType.INFORMATION, "Exam Fees Overall Report exported (" + dataList.size() + " records).").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
        }
    }
}
