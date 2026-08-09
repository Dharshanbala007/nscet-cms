package com.nscet.cms.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class DaySettlementController implements Initializable {

    @FXML private DatePicker settlementDate;
    @FXML private ComboBox<String> baseAccountCombo;
    @FXML private TableView<?> reportTable;
    @FXML private TableColumn<?, ?> slNoCol;
    @FXML private TableColumn<?, ?> receiptNoCol;
    @FXML private TableColumn<?, ?> studentNameCol;
    @FXML private TableColumn<?, ?> amountCol;
    @FXML private TableColumn<?, ?> payModeCol;
    @FXML private Label totalCashLabel;
    @FXML private Label totalChequeLabel;
    @FXML private Label totalOnlineLabel;
    @FXML private Label grandTotalLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML private void handleLoad() {
    }

    @FXML private void handleSettle() {
    }

    @FXML private void handlePrint() {
    }
}
