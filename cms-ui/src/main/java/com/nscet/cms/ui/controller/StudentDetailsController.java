package com.nscet.cms.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StudentDetailsController implements Initializable {

    @FXML private TableView<?> table;
    @FXML private TableColumn<?, ?> rollNoCol;
    @FXML private TableColumn<?, ?> nameCol;
    @FXML private TableColumn<?, ?> deptCol;
    @FXML private TableColumn<?, ?> semesterCol;
    @FXML private TableColumn<?, ?> admissionTypeCol;
    @FXML private TableColumn<?, ?> actionsCol;
    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private TextField rollNoField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<String> admissionTypeCombo;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    private ObservableList<?> tableData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setItems((ObservableList) tableData);
    }

    @FXML private void handleSearch() { currentPage = 0; }
    @FXML private void handleAdd() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }
    @FXML private void handleSave() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }
    @FXML private void handleCancel() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }
    @FXML private void handlePrevious() { currentPage--; }
    @FXML private void handleNext() { currentPage++; }
}
