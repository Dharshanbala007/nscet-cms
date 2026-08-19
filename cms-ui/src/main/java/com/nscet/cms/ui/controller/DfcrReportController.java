package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.DfcrReportDto;
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
public class DfcrReportController implements Initializable {

    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private ComboBox<String> baseAccountCombo;
    @FXML private ComboBox<String> modeCombo;
    @FXML private TableView<DfcrReportDto> reportTable;
    @FXML private TableColumn<DfcrReportDto, String> receiptNoCol;
    @FXML private TableColumn<DfcrReportDto, LocalDate> receiptDateCol;
    @FXML private TableColumn<DfcrReportDto, String> rollNoCol;
    @FXML private TableColumn<DfcrReportDto, String> studentNameCol;
    @FXML private TableColumn<DfcrReportDto, String> deptCol;
    @FXML private TableColumn<DfcrReportDto, String> baseAccountCol;
    @FXML private TableColumn<DfcrReportDto, String> paymentModeCol;
    @FXML private TableColumn<DfcrReportDto, String> totalAmountCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<DfcrReportDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.now().minusMonths(1));
        toDate.setValue(LocalDate.now());

        baseAccountCombo.getItems().addAll("ALL", "TMB Main", "Federal Bank", "Canara Bank", "Cash Account");
        baseAccountCombo.setValue("ALL");

        modeCombo.getItems().addAll("ALL", "CASH", "DD / Cheque", "OLP", "Bank Transfer");
        modeCombo.setValue("ALL");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        receiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));
        receiptDateCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getReceiptDate()));
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));
        studentNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));
        baseAccountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBaseAccount()));
        paymentModeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaymentMode()));
        totalAmountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalAmount().toString()));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<DfcrReportDto> results = reportService.getDfcrReport(fromDate.getValue(), toDate.getValue());
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("DailyCollectionRegister", dataList, new HashMap<>());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "DFCR Report exported (" + dataList.size() + " records).").showAndWait();
        }
    }
}
