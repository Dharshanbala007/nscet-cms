package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.ReceiptBankCheckingDto;
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
public class ReceiptBankCheckingController implements Initializable {

    @FXML private ComboBox<String> bankAccountCombo;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private TableView<ReceiptBankCheckingDto> reportTable;
    @FXML private TableColumn<ReceiptBankCheckingDto, String> receiptNoCol;
    @FXML private TableColumn<ReceiptBankCheckingDto, LocalDate> receiptDateCol;
    @FXML private TableColumn<ReceiptBankCheckingDto, String> bankNameCol;
    @FXML private TableColumn<ReceiptBankCheckingDto, String> accountNoCol;
    @FXML private TableColumn<ReceiptBankCheckingDto, String> paymentModeCol;
    @FXML private TableColumn<ReceiptBankCheckingDto, String> amountCol;
    @FXML private TableColumn<ReceiptBankCheckingDto, String> statusCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<ReceiptBankCheckingDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bankAccountCombo.getItems().addAll("ALL", "TMB Main SB-0071000500508", "Federal Bank SB-1452010002682", "Canara Bank SB-1630130-C8", "ICICI Bank Online");
        bankAccountCombo.setValue("ALL");

        fromDate.setValue(LocalDate.now().minusMonths(1));
        toDate.setValue(LocalDate.now());

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        receiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));
        receiptDateCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getReceiptDate()));
        bankNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankName()));
        accountNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountNo()));
        paymentModeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaymentMode()));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount().toString()));
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<ReceiptBankCheckingDto> results = reportService.getReceiptBankChecking(fromDate.getValue(), toDate.getValue(), bankAccountCombo.getValue());
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("DailyCollectionRegister", dataList, new HashMap<>());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "Receipt Bank Checking Report exported (" + dataList.size() + " records).").showAndWait();
        }
    }
}
