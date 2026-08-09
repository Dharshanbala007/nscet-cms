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
public class StudentEnrollmentController implements Initializable {

    @FXML private TextField rollNoField;
    @FXML private Label nameLabel;
    @FXML private Label deptLabel;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<String> academicYearCombo;
    @FXML private TableView<?> enrollmentTable;
    @FXML private TableColumn<?, ?> slNoCol;
    @FXML private TableColumn<?, ?> rollNoCol;
    @FXML private TableColumn<?, ?> nameCol;
    @FXML private TableColumn<?, ?> deptCol;
    @FXML private TableColumn<?, ?> semesterCol;
    @FXML private TableColumn<?, ?> enrollmentDateCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML private void handleSearch() {
    }

    @FXML private void handleEnroll() {
    }

    @FXML private void handleClear() {
    }
}
