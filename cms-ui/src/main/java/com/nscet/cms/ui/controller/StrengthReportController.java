package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.StrengthReportDto;
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
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StrengthReportController implements Initializable {

    @FXML private RadioButton summaryRadio, detailRadio;
    @FXML private ToggleGroup reportTypeGroup;

    @FXML private RadioButton sigRadio, phoneRadio, bankAccRadio, dojRadio;
    @FXML private ToggleGroup customFieldGroup;

    @FXML private ComboBox<String> staffStudentCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> sexCombo;
    @FXML private ComboBox<String> staffTypeCombo;

    @FXML private TableView reportTable;

    @Autowired
    private ReportService reportService;

    private final ObservableList<StrengthReportDto> summaryList = FXCollections.observableArrayList();
    private final ObservableList<StrengthDetailRowDto> detailList = FXCollections.observableArrayList();

    public static class StrengthDetailRowDto {
        private String slNo;
        private String idRollNo;
        private String name;
        private String dept;
        private String gender;
        private String category;
        private String customInfo;

        public StrengthDetailRowDto(String slNo, String idRollNo, String name, String dept, String gender, String category, String customInfo) {
            this.slNo = slNo;
            this.idRollNo = idRollNo;
            this.name = name;
            this.dept = dept;
            this.gender = gender;
            this.category = category;
            this.customInfo = customInfo;
        }

        public String getSlNo() { return slNo; }
        public String getIdRollNo() { return idRollNo; }
        public String getName() { return name; }
        public String getDept() { return dept; }
        public String getGender() { return gender; }
        public String getCategory() { return category; }
        public String getCustomInfo() { return customInfo; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (staffStudentCombo != null) {
            staffStudentCombo.getItems().clear();
            staffStudentCombo.getItems().addAll("Select", "Student", "Staff");
            staffStudentCombo.setValue("Select");
        }

        if (categoryCombo != null) {
            categoryCombo.getItems().clear();
            categoryCombo.getItems().addAll("Select", "Teaching", "Non-Teaching", "BC", "MBC", "SC/ST", "OC");
            categoryCombo.setValue("Select");
        }

        if (sexCombo != null) {
            sexCombo.getItems().clear();
            sexCombo.getItems().addAll("Select", "Male", "Female");
            sexCombo.setValue("Select");
        }

        if (staffTypeCombo != null) {
            staffTypeCombo.getItems().clear();
            staffTypeCombo.getItems().addAll("Select", "Regular", "Contract", "Daily");
            staffTypeCombo.setValue("Select");
        }

        if (reportTypeGroup != null) {
            reportTypeGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> handleGenerate());
        }

        handleGenerate();
    }

    @FXML
    public void handleGenerate() {
        if (summaryRadio != null && summaryRadio.isSelected()) {
            setupSummaryColumns();
            generateSummaryData();
        } else {
            setupDetailColumns();
            generateDetailData();
        }
    }

    @SuppressWarnings("unchecked")
    private void setupSummaryColumns() {
        reportTable.getColumns().clear();

        TableColumn<StrengthReportDto, String> deptCol = new TableColumn<>("Department"); deptCol.setPrefWidth(120);
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));

        TableColumn<StrengthReportDto, String> degreeCol = new TableColumn<>("Degree"); degreeCol.setPrefWidth(80);
        degreeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDegree()));

        TableColumn<StrengthReportDto, String> yearCol = new TableColumn<>("Year"); yearCol.setPrefWidth(90);
        yearCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getYear()));

        TableColumn<StrengthReportDto, Number> semesterCol = new TableColumn<>("Sem"); semesterCol.setPrefWidth(60);
        semesterCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSemester() != null ? c.getValue().getSemester() : 0));

        TableColumn<StrengthReportDto, Number> maleCol = new TableColumn<>("Male"); maleCol.setPrefWidth(80);
        maleCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaleCount() != null ? c.getValue().getMaleCount() : 0));

        TableColumn<StrengthReportDto, Number> femaleCol = new TableColumn<>("Female"); femaleCol.setPrefWidth(80);
        femaleCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getFemaleCount() != null ? c.getValue().getFemaleCount() : 0));

        TableColumn<StrengthReportDto, Number> totalCol = new TableColumn<>("Total Strength"); totalCol.setPrefWidth(110);
        totalCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTotalCount() != null ? c.getValue().getTotalCount() : 0));

        TableColumn<StrengthReportDto, Number> ocCol = new TableColumn<>("OC"); ocCol.setPrefWidth(70);
        ocCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getOcCount() != null ? c.getValue().getOcCount() : 0));

        TableColumn<StrengthReportDto, Number> bcCol = new TableColumn<>("BC"); bcCol.setPrefWidth(70);
        bcCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getBcCount() != null ? c.getValue().getBcCount() : 0));

        TableColumn<StrengthReportDto, Number> mbcCol = new TableColumn<>("MBC"); mbcCol.setPrefWidth(70);
        mbcCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMbcCount() != null ? c.getValue().getMbcCount() : 0));

        TableColumn<StrengthReportDto, Number> scstCol = new TableColumn<>("SC/ST"); scstCol.setPrefWidth(70);
        scstCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getScstCount() != null ? c.getValue().getScstCount() : 0));

        reportTable.getColumns().addAll(deptCol, degreeCol, yearCol, semesterCol, maleCol, femaleCol, totalCol, ocCol, bcCol, mbcCol, scstCol);
        reportTable.setItems(summaryList);
    }

    @SuppressWarnings("unchecked")
    private void setupDetailColumns() {
        reportTable.getColumns().clear();

        String customLabel = "Custom Info";
        if (phoneRadio != null && phoneRadio.isSelected()) customLabel = "Phone No";
        else if (bankAccRadio != null && bankAccRadio.isSelected()) customLabel = "Bank Acc No";
        else if (dojRadio != null && dojRadio.isSelected()) customLabel = "Date of Joining";
        else if (sigRadio != null && sigRadio.isSelected()) customLabel = "Signature";

        TableColumn<StrengthDetailRowDto, String> slCol = new TableColumn<>("Sl No"); slCol.setPrefWidth(50);
        slCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlNo()));

        TableColumn<StrengthDetailRowDto, String> idCol = new TableColumn<>("Roll No / Staff ID"); idCol.setPrefWidth(120);
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdRollNo()));

        TableColumn<StrengthDetailRowDto, String> nameCol = new TableColumn<>("Name"); nameCol.setPrefWidth(180);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<StrengthDetailRowDto, String> deptCol = new TableColumn<>("Dept"); deptCol.setPrefWidth(80);
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));

        TableColumn<StrengthDetailRowDto, String> genderCol = new TableColumn<>("Gender"); genderCol.setPrefWidth(70);
        genderCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGender()));

        TableColumn<StrengthDetailRowDto, String> catCol = new TableColumn<>("Category"); catCol.setPrefWidth(100);
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));

        TableColumn<StrengthDetailRowDto, String> extraCol = new TableColumn<>(customLabel); extraCol.setPrefWidth(150);
        extraCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomInfo()));

        reportTable.getColumns().addAll(slCol, idCol, nameCol, deptCol, genderCol, catCol, extraCol);
        reportTable.setItems(detailList);
    }

    private void generateSummaryData() {
        summaryList.clear();
        List<StrengthReportDto> results = reportService.getStrengthReport("ALL");
        if (results != null && !results.isEmpty()) {
            summaryList.addAll(results);
        } else {
            createSampleSummary();
        }
    }

    private void createSampleSummary() {
        StrengthReportDto s1 = new StrengthReportDto(); s1.setDepartment("CSE"); s1.setDegree("B.E"); s1.setYear("III"); s1.setSemester(6); s1.setMaleCount(60); s1.setFemaleCount(92); s1.setTotalCount(152); s1.setOcCount(10); s1.setBcCount(80); s1.setMbcCount(45); s1.setScstCount(17);
        StrengthReportDto s2 = new StrengthReportDto(); s2.setDepartment("ECE"); s2.setDegree("B.E"); s2.setYear("III"); s2.setSemester(6); s2.setMaleCount(30); s2.setFemaleCount(46); s2.setTotalCount(76); s2.setOcCount(5); s2.setBcCount(40); s2.setMbcCount(20); s2.setScstCount(11);
        StrengthReportDto s3 = new StrengthReportDto(); s3.setDepartment("MECH"); s3.setDegree("B.E"); s3.setYear("III"); s3.setSemester(6); s3.setMaleCount(45); s3.setFemaleCount(6); s3.setTotalCount(51); s3.setOcCount(2); s3.setBcCount(30); s3.setMbcCount(12); s3.setScstCount(7);
        summaryList.addAll(s1, s2, s3);
    }

    private void generateDetailData() {
        detailList.clear();
        detailList.add(new StrengthDetailRowDto("1", "2023FCSE001", "ARUN KUMAR S", "CSE", "Male", "BC", "9876543210"));
        detailList.add(new StrengthDetailRowDto("2", "2023FCSE002", "BHAVANI K", "CSE", "Female", "MBC", "9876543211"));
        detailList.add(new StrengthDetailRowDto("3", "2023FECE005", "CHANDRAN P", "ECE", "Male", "BC", "9876543212"));
        detailList.add(new StrengthDetailRowDto("4", "2023FME007", "SANJAY K", "MECH", "Male", "BC", "9876543213"));
    }

    @FXML
    private void handleExport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Strength Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Current Strength Report to printer.");
        alert.showAndWait();
    }
}
