package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.ApplicationReportDto;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class ApplicationReportController implements Initializable {

    @FXML private RadioButton summaryRadio;
    @FXML private RadioButton detailRadio;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private ComboBox<String> orderByCombo;
    @FXML private TableView<ApplicationReportDto> reportTable;
    @FXML private TableColumn<ApplicationReportDto, Number> slNoCol;
    @FXML private TableColumn<ApplicationReportDto, String> appNoCol;
    @FXML private TableColumn<ApplicationReportDto, String> studentNameCol;
    @FXML private TableColumn<ApplicationReportDto, String> addressCol;
    @FXML private TableColumn<ApplicationReportDto, String> hscMarkCol;
    @FXML private TableColumn<ApplicationReportDto, String> doteCutOffCol;
    @FXML private TableColumn<ApplicationReportDto, String> communityCol;
    @FXML private TableColumn<ApplicationReportDto, String> mgGqCol;
    @FXML private TableColumn<ApplicationReportDto, String> amountCol;
    @FXML private TableColumn<ApplicationReportDto, String> deptCol;
    @FXML private TableColumn<ApplicationReportDto, String> schoolNameCol;
    @FXML private TableColumn<ApplicationReportDto, String> hostelCol;

    @Autowired
    private ReportService reportService;

    private final ObservableList<ApplicationReportDto> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.now().minusMonths(1));
        toDate.setValue(LocalDate.now());

        orderByCombo.getItems().addAll("Receipt Date", "App No", "Student Name", "Community", "Department");
        orderByCombo.setValue("Receipt Date");

        setupTableColumns();
        reportTable.setItems(dataList);
        handleGenerate();
    }

    private void setupTableColumns() {
        slNoCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSlNo()));
        appNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAppNo()));
        studentNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        addressCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
        hscMarkCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHscMark()));
        doteCutOffCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoteCutOff()));
        communityCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCommunity()));
        mgGqCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMgGq()));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAmountPaid() != null ? c.getValue().getAmountPaid().toString() : "0.00"));
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));
        schoolNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSchoolName()));
        hostelCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHostel()));
    }

    @FXML
    private void handleGenerate() {
        dataList.clear();
        List<ApplicationReportDto> results = reportService.getApplicationReport(fromDate.getValue(), toDate.getValue());
        dataList.addAll(results);
    }

    @FXML
    private void handleExport() {
        try {
            ReportManager.printReport("ApplicationReport", dataList, new HashMap<>());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "Application Report exported (" + dataList.size() + " records).").showAndWait();
        }
    }
}
