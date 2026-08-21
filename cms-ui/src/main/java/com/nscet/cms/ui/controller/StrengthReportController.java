package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.StrengthReportDto;
import com.nscet.cms.reports.ReportManager;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StrengthReportController implements Initializable {

    @FXML private ComboBox<String> deptCombo;
    @FXML private TableView<StrengthReportDto> reportTable;
    @FXML private TableColumn<StrengthReportDto, String> deptCol;
    @FXML private TableColumn<StrengthReportDto, String> degreeCol;
    @FXML private TableColumn<StrengthReportDto, String> yearCol;
    @FXML private TableColumn<StrengthReportDto, Number> semesterCol;
    @FXML private TableColumn<StrengthReportDto, Number> maleCol;
    @FXML private TableColumn<StrengthReportDto, Number> femaleCol;
    @FXML private TableColumn<StrengthReportDto, Number> totalCol;
    @FXML private TableColumn<StrengthReportDto, Number> ocCol;
    @FXML private TableColumn<StrengthReportDto, Number> bcCol;
    @FXML private TableColumn<StrengthReportDto, Number> mbcCol;
    @FXML private TableColumn<StrengthReportDto, Number> scstCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<StrengthReportDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deptCombo.getItems().addAll("ALL", "CSE", "ECE", "MECH", "CE", "EEE", "IT", "AI");
        deptCombo.setValue("ALL");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        degreeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDegree()));
        yearCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getYear()));
        semesterCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSemester() != null ? c.getValue().getSemester() : 0));
        maleCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaleCount() != null ? c.getValue().getMaleCount() : 0));
        femaleCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getFemaleCount() != null ? c.getValue().getFemaleCount() : 0));
        totalCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTotalCount() != null ? c.getValue().getTotalCount() : 0));
        ocCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getOcCount() != null ? c.getValue().getOcCount() : 0));
        bcCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getBcCount() != null ? c.getValue().getBcCount() : 0));
        mbcCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMbcCount() != null ? c.getValue().getMbcCount() : 0));
        scstCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getScstCount() != null ? c.getValue().getScstCount() : 0));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<StrengthReportDto> results = reportService.getStrengthReport(deptCombo.getValue());
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("StrengthReport", dataList, new HashMap<>());
            new Alert(Alert.AlertType.INFORMATION, "Strength Report exported (" + dataList.size() + " records).").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
        }
    }
}
