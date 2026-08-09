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
public class BulkFeeEntryController implements Initializable {

    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<String> feeNameCombo;
    @FXML private TextField amountField;
    @FXML private TableView<?> studentTable;
    @FXML private TableColumn<?, ?> selectCol;
    @FXML private TableColumn<?, ?> rollNoCol;
    @FXML private TableColumn<?, ?> nameCol;
    @FXML private TableColumn<?, ?> deptCol;
    @FXML private TableColumn<?, ?> feeAmountCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML private void handleLoadStudents() {
    }

    @FXML private void handleApplyFee() {
    }
}
