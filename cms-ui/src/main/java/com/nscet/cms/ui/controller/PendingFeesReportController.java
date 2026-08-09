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
public class PendingFeesReportController implements Initializable {

    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private TableView<?> reportTable;
    @FXML private TableColumn<?, ?> slNoCol;
    @FXML private TableColumn<?, ?> rollNoCol;
    @FXML private TableColumn<?, ?> nameCol;
    @FXML private TableColumn<?, ?> deptCol;
    @FXML private TableColumn<?, ?> pendingAmountCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML private void handleGenerate() {
    }

    @FXML private void handleExport() {
    }
}
