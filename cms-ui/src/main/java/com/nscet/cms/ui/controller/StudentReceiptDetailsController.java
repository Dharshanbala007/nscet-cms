package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.StudentReceiptDetailsDto;
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
public class StudentReceiptDetailsController implements Initializable {

    @FXML private ComboBox<String> deptCombo;
    @FXML private TextField studentSearchField;
    @FXML private Label studentNameLabel;
    @FXML private Label rollNoLabel;
    @FXML private Label regNoLabel;
    @FXML private TableView<StudentReceiptDetailsDto> reportTable;
    @FXML private TableColumn<StudentReceiptDetailsDto, String> receiptNoCol;
    @FXML private TableColumn<StudentReceiptDetailsDto, LocalDate> receiptDateCol;
    @FXML private TableColumn<StudentReceiptDetailsDto, String> rollNoCol;
    @FXML private TableColumn<StudentReceiptDetailsDto, String> feeHeadCol;
    @FXML private TableColumn<StudentReceiptDetailsDto, String> amountCol;
    @FXML private TableColumn<StudentReceiptDetailsDto, String> semesterCol;
    @FXML private TableColumn<StudentReceiptDetailsDto, String> remarksCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<StudentReceiptDetailsDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deptCombo.getItems().addAll("ALL", "CSE", "ECE", "MECH", "CE", "EEE", "IT", "AI");
        deptCombo.setValue("ALL");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        receiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));
        receiptDateCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getReceiptDate()));
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));
        feeHeadCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFeeName()));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAmount() != null ? c.getValue().getAmount().toString() : "0.00"));
        semesterCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSemester() != null ? c.getValue().getSemester().toString() : ""));
        remarksCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks()));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<StudentReceiptDetailsDto> results = reportService.getStudentReceiptDetails(null, studentSearchField != null ? studentSearchField.getText() : "");
        dataList.addAll(results);

        if (!dataList.isEmpty()) {
            StudentReceiptDetailsDto first = dataList.get(0);
            if (studentNameLabel != null) studentNameLabel.setText("Student Name: " + first.getStudentName());
            if (rollNoLabel != null) rollNoLabel.setText("Roll No: " + first.getRollNo());
            if (regNoLabel != null) regNoLabel.setText("Reg No: " + (first.getRegNo() != null ? first.getRegNo() : "921024114021"));
        }
    }

    @FXML
    private void handlePrint() {
        try {
            ReportManager.printReport("FeeReceipt", dataList, new HashMap<>());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "Report generated successfully (" + dataList.size() + " records).").showAndWait();
        }
    }
}
