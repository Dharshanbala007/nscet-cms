package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.HeadwiseDetailsDto;
import com.nscet.cms.reports.ReportManager;
import javafx.beans.property.SimpleObjectProperty;
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
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class HeadwiseDetailsReportController implements Initializable {

    @FXML private RadioButton paidListRadio;
    @FXML private RadioButton pendingListRadio;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private TableView<HeadwiseDetailsDto> reportTable;
    @FXML private TableColumn<HeadwiseDetailsDto, String> receiptNoCol;
    @FXML private TableColumn<HeadwiseDetailsDto, LocalDate> receiptDateCol;
    @FXML private TableColumn<HeadwiseDetailsDto, String> deptCol;
    @FXML private TableColumn<HeadwiseDetailsDto, String> rollNoCol;
    @FXML private TableColumn<HeadwiseDetailsDto, String> studentNameCol;
    @FXML private TableColumn<HeadwiseDetailsDto, String> feeHeadCol;
    @FXML private TableColumn<HeadwiseDetailsDto, String> amountCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<HeadwiseDetailsDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.now().minusMonths(1));
        toDate.setValue(LocalDate.now());

        deptCombo.getItems().addAll("ALL", "MECH", "CSE", "ECE", "CE", "EEE", "IT", "AI");
        deptCombo.setValue("ALL");

        semesterCombo.getItems().addAll("ALL", "1", "2", "3", "4", "5", "6", "7", "8");
        semesterCombo.setValue("ALL");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        receiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));
        receiptDateCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getReceiptDate()));
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));
        studentNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        feeHeadCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFeeHead()));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount() != null ? c.getValue().getAmount().toString() : "0.00"));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        String type = paidListRadio.isSelected() ? "PAID" : "PENDING";
        String dept = deptCombo.getValue();
        String sem = semesterCombo.getValue();
        List<HeadwiseDetailsDto> results = reportService.getHeadwiseDetails(fromDate.getValue(), toDate.getValue(), type, dept, sem);
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("FeeReceipt", dataList, new HashMap<>());
            new Alert(Alert.AlertType.INFORMATION, "Headwise Details Report exported (" + dataList.size() + " records).").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
        }
    }
}
