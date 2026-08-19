package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.PendingBusFeesDto;
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
public class PendingBusFeesReportController implements Initializable {

    @FXML private ComboBox<String> academicYearCombo;
    @FXML private ComboBox<String> termCombo;
    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private TableView<PendingBusFeesDto> reportTable;
    @FXML private TableColumn<PendingBusFeesDto, String> deptCol;
    @FXML private TableColumn<PendingBusFeesDto, String> rollNoCol;
    @FXML private TableColumn<PendingBusFeesDto, String> nameCol;
    @FXML private TableColumn<PendingBusFeesDto, String> quotaCol;
    @FXML private TableColumn<PendingBusFeesDto, String> busStopCol;
    @FXML private TableColumn<PendingBusFeesDto, String> prevPendingCol;
    @FXML private TableColumn<PendingBusFeesDto, String> busFeeCol;
    @FXML private TableColumn<PendingBusFeesDto, String> paidCol;
    @FXML private TableColumn<PendingBusFeesDto, String> balanceCol;
    @FXML private TableColumn<PendingBusFeesDto, String> toBePaidCol;
    @FXML private TableColumn<PendingBusFeesDto, String> receiptNoCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<PendingBusFeesDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        academicYearCombo.getItems().addAll("2025-26", "2024-25", "2023-24");
        academicYearCombo.setValue("2025-26");

        termCombo.getItems().addAll("Even", "Odd", "ALL");
        termCombo.setValue("Even");

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
        busStopCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusStopName()));
        prevPendingCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPreviousPending().toString()));
        busFeeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusFee().toString()));
        paidCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaidAmount().toString()));
        balanceCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBalanceAmount().toString()));
        toBePaidCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getToBePaid().toString()));
        receiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<PendingBusFeesDto> results = reportService.getPendingBusFees(
                academicYearCombo.getValue(),
                termCombo.getValue(),
                deptCombo.getValue()
        );
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("PendingFeesReport", dataList, new HashMap<>());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "Pending Bus Fees Report exported (" + dataList.size() + " records).").showAndWait();
        }
    }
}
