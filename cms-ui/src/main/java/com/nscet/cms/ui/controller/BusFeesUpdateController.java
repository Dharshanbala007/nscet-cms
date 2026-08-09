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
public class BusFeesUpdateController implements Initializable {

    @FXML private ComboBox<String> routeCombo;
    @FXML private TextField busFeeField;
    @FXML private TableView<?> studentTable;
    @FXML private TableColumn<?, ?> selectCol;
    @FXML private TableColumn<?, ?> rollNoCol;
    @FXML private TableColumn<?, ?> nameCol;
    @FXML private TableColumn<?, ?> routeCol;
    @FXML private TableColumn<?, ?> currentFeeCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML private void handleLoadStudents() {
    }

    @FXML private void handleUpdateFees() {
    }
}
