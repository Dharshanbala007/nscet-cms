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
public class ReceiptReprintController implements Initializable {

    @FXML private TextField receiptNoField;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private TableView<?> reportTable;
    @FXML private TableColumn<?, ?> receiptNoCol;
    @FXML private TableColumn<?, ?> studentNameCol;
    @FXML private TableColumn<?, ?> amountCol;
    @FXML private TableColumn<?, ?> dateCol;
    @FXML private TableColumn<?, ?> actionsCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML private void handleSearch() {
    }
}
