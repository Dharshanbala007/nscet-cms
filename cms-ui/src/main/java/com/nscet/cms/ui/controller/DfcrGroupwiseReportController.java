package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.DfcrGroupwiseDto;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class DfcrGroupwiseReportController implements Initializable {

    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private ComboBox<String> feeGroupCombo;
    @FXML private TableView<DfcrGroupwiseDto> reportTable;
    @FXML private TableColumn<DfcrGroupwiseDto, String> feeGroupCol;
    @FXML private TableColumn<DfcrGroupwiseDto, String> totalCollectedCol;
    @FXML private TableColumn<DfcrGroupwiseDto, Number> receiptCountCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<DfcrGroupwiseDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.now().minusMonths(1));
        toDate.setValue(LocalDate.now());

        feeGroupCombo.getItems().addAll("ALL Groups", "College Fees", "Exam Fees", "Bus Fees", "Hostel Fees", "Miscellaneous");
        feeGroupCombo.setValue("ALL Groups");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        feeGroupCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFeeGroup()));
        totalCollectedCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalCollected().toString()));
        receiptCountCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getReceiptCount()));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<DfcrGroupwiseDto> results = reportService.getDfcrGroupwiseReport(fromDate.getValue(), toDate.getValue());
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("DailyCollectionRegister", dataList, new HashMap<>());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "DFCR Groupwise Report exported (" + dataList.size() + " records).").showAndWait();
        }
    }
}
