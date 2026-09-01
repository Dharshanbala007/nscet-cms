package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import com.nscet.cms.core.service.ReportService.ApplicationReportDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class ApplicationReportController implements Initializable {

    @FXML private RadioButton summaryRadio, detailRadio, advanceRadio, customizeRadio;
    @FXML private ToggleGroup reportModeGroup;
    @FXML private ComboBox<String> typeCombo, orderByCombo, custField1Combo, custField2Combo;
    @FXML private DatePicker fromDate, toDate;
    @FXML private TextField custValueField;

    @FXML private TableView reportTable;

    @Autowired
    private ReportService reportService;

    private final ObservableList<ApplicationReportDto> detailList = FXCollections.observableArrayList();
    private final ObservableList<ApplicationSummaryDto> summaryList = FXCollections.observableArrayList();

    public static class ApplicationSummaryDto {
        private String dept;
        private String appMq;
        private String appGq;
        private String nyd;
        private String total;

        public ApplicationSummaryDto(String dept, String appMq, String appGq, String nyd, String total) {
            this.dept = dept;
            this.appMq = appMq;
            this.appGq = appGq;
            this.nyd = nyd;
            this.total = total;
        }

        public String getDept() { return dept; }
        public String getAppMq() { return appMq; }
        public String getAppGq() { return appGq; }
        public String getNyd() { return nyd; }
        public String getTotal() { return total; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (fromDate != null) fromDate.setValue(LocalDate.of(2026, 6, 10));
        if (toDate != null) toDate.setValue(LocalDate.of(2026, 6, 30));

        if (typeCombo != null) {
            typeCombo.getItems().clear();
            typeCombo.getItems().addAll("Select", "Fresh", "Lateral", "Admission Fresh", "Admission Lateral");
            typeCombo.setValue("Fresh");
        }

        if (orderByCombo != null) {
            orderByCombo.getItems().clear();
            orderByCombo.getItems().addAll("Select", "receiptDate", "dept");
            orderByCombo.setValue("receiptDate");
        }

        if (custField1Combo != null) {
            custField1Combo.getItems().clear();
            custField1Combo.getItems().addAll("Select", "studentname", "applicationno", "gender", "caste", "schoolname", "uravinmuraletter", "dept", "quota");
            custField1Combo.setValue("Select");
        }

        if (custField2Combo != null) {
            custField2Combo.getItems().clear();
            custField2Combo.getItems().addAll("Select", "Equals", "Contains");
            custField2Combo.setValue("Select");
        }

        // Setup Mode Switch Listeners
        if (reportModeGroup != null) {
            reportModeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> handleGenerate());
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

        TableColumn<ApplicationSummaryDto, String> deptCol = new TableColumn<>("Dept");
        deptCol.setPrefWidth(90);
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));

        TableColumn<ApplicationSummaryDto, String> appMqCol = new TableColumn<>("App.MQ");
        appMqCol.setPrefWidth(80);
        appMqCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAppMq()));

        TableColumn<ApplicationSummaryDto, String> appGqCol = new TableColumn<>("App.GQ");
        appGqCol.setPrefWidth(80);
        appGqCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAppGq()));

        TableColumn<ApplicationSummaryDto, String> nydCol = new TableColumn<>("NYD");
        nydCol.setPrefWidth(70);
        nydCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNyd()));

        TableColumn<ApplicationSummaryDto, String> totalCol = new TableColumn<>("Total");
        totalCol.setPrefWidth(80);
        totalCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotal()));

        reportTable.getColumns().addAll(deptCol, appMqCol, appGqCol, nydCol, totalCol);
        reportTable.setItems(summaryList);
    }

    @SuppressWarnings("unchecked")
    private void setupDetailColumns() {
        reportTable.getColumns().clear();

        TableColumn<ApplicationReportDto, String> receiptNoCol = new TableColumn<>("Receipt No"); receiptNoCol.setPrefWidth(80);
        receiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));

        TableColumn<ApplicationReportDto, String> appNoCol = new TableColumn<>("App.No"); appNoCol.setPrefWidth(75);
        appNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAppNo()));

        TableColumn<ApplicationReportDto, String> nameCol = new TableColumn<>("Student Name"); nameCol.setPrefWidth(150);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));

        TableColumn<ApplicationReportDto, String> addrCol = new TableColumn<>("Address"); addrCol.setPrefWidth(200);
        addrCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));

        TableColumn<ApplicationReportDto, String> hscCol = new TableColumn<>("HSC Mark"); hscCol.setPrefWidth(75);
        hscCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHscMark()));

        TableColumn<ApplicationReportDto, String> doteCol = new TableColumn<>("DOTE Cut"); doteCol.setPrefWidth(70);
        doteCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoteCutOff()));

        TableColumn<ApplicationReportDto, String> annaCol = new TableColumn<>("Anna Univ Cut off"); annaCol.setPrefWidth(110);
        annaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAnnaUnivCutOff()));

        TableColumn<ApplicationReportDto, String> commCol = new TableColumn<>("Community"); commCol.setPrefWidth(85);
        commCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCommunity()));

        TableColumn<ApplicationReportDto, String> mgGqCol = new TableColumn<>("MQ\\GQ"); mgGqCol.setPrefWidth(95);
        mgGqCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMgGq()));

        TableColumn<ApplicationReportDto, String> amtCol = new TableColumn<>("Amount Paid"); amtCol.setPrefWidth(90);
        amtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmountPaid() != null ? c.getValue().getAmountPaid().toPlainString() : "0"));

        TableColumn<ApplicationReportDto, String> deptCol = new TableColumn<>("Dept"); deptCol.setPrefWidth(65);
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));

        TableColumn<ApplicationReportDto, String> schoolCol = new TableColumn<>("School Name"); schoolCol.setPrefWidth(160);
        schoolCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSchoolName()));

        TableColumn<ApplicationReportDto, String> hostelCol = new TableColumn<>("Hostel"); hostelCol.setPrefWidth(65);
        hostelCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHostel()));

        reportTable.getColumns().addAll(receiptNoCol, appNoCol, nameCol, addrCol, hscCol, doteCol, annaCol, commCol, mgGqCol, amtCol, deptCol, schoolCol, hostelCol);
        reportTable.setItems(detailList);
    }

    private void generateSummaryData() {
        summaryList.clear();
        summaryList.add(new ApplicationSummaryDto("MECH", "1", "50", "", "51"));
        summaryList.add(new ApplicationSummaryDto("ECE", "", "76", "", "76"));
        summaryList.add(new ApplicationSummaryDto("CE", "1", "37", "", "38"));
        summaryList.add(new ApplicationSummaryDto("CSE", "6", "146", "", "152"));
        summaryList.add(new ApplicationSummaryDto("EEE", "2", "28", "", "30"));
        summaryList.add(new ApplicationSummaryDto("NYD", "", "", "0", "0"));
        summaryList.add(new ApplicationSummaryDto("Total", "", "", "", "347"));
    }

    private void generateDetailData() {
        detailList.clear();
        createSampleData();

        // Optional filtering by Customize Report if active
        if (customizeRadio != null && customizeRadio.isSelected()) {
            String field = custField1Combo != null ? custField1Combo.getValue() : null;
            String searchVal = custValueField != null ? custValueField.getText().trim().toLowerCase() : "";

            if (searchVal != null && !searchVal.isEmpty() && field != null && !"Select".equals(field)) {
                List<ApplicationReportDto> filtered = detailList.stream().filter(item -> {
                    if ("studentname".equalsIgnoreCase(field)) return item.getStudentName() != null && item.getStudentName().toLowerCase().contains(searchVal);
                    if ("applicationno".equalsIgnoreCase(field)) return item.getAppNo() != null && item.getAppNo().toLowerCase().contains(searchVal);
                    if ("dept".equalsIgnoreCase(field)) return item.getDept() != null && item.getDept().toLowerCase().contains(searchVal);
                    if ("schoolname".equalsIgnoreCase(field)) return item.getSchoolName() != null && item.getSchoolName().toLowerCase().contains(searchVal);
                    if ("caste".equalsIgnoreCase(field) || "community".equalsIgnoreCase(field)) return item.getCommunity() != null && item.getCommunity().toLowerCase().contains(searchVal);
                    return true;
                }).collect(Collectors.toList());
                detailList.setAll(filtered);
            }
        }

        // Sorting by Order By combo
        if (orderByCombo != null && "dept".equalsIgnoreCase(orderByCombo.getValue())) {
            detailList.sort(Comparator.comparing(ApplicationReportDto::getDept, Comparator.nullsLast(String::compareTo)));
        }
    }

    private void createSampleData() {
        detailList.clear();
        addDetail("428", "FY498", "SIJITHA BANU", "3 133 PULLAVAR STREET", "N/A", "N/A", "180.0", "MBC", "Counseling", "0", "AI", "S THANGAPAZAM SC", "No");
        addDetail("429", "FY405", "HARSHINI S", "20 1 EAST STREET UPPA", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "CSE", "NSGHSS", "No");
        addDetail("430", "FY499", "MENAGASRI P", "5 H 1 MACHHD STREET A", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "CSE", "NSBHSS", "No");
        addDetail("431", "FY476", "MOHAMED AADHIL A", "A 8 SUNGAM ST", "N/A", "N/A", "180.0", "BC (M)", "Counseling", "0", "AI", "SRI MBM HR SEC SC", "No");
        addDetail("432", "FY477", "NATHIRA R", "GNANAMMAN KOVIL ST", "N/A", "N/A", "180.0", "MBC", "Counseling", "0", "ECE", "THE CRESCENT MAT", "No");
        addDetail("433", "FY500", "DEEPA M", "12 EAST SAVADI STREET", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "CSE", "PANKAJAM GIRLS H", "No");
        addDetail("434", "FY432", "ANBUKARASI S", "D O SEKAR 15 15 9D PHIL", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "AI", "MAHALAKSHMI GIRL", "No");
        addDetail("435", "ME09", "DINESH V", "W 17 50F BHARATHI NAG", "N/A", "N/A", "180.0", "BC", "Management", "0", "SE", "NSCET", "No");
        addDetail("436", "FY10", "SWATHI K", "132 VAITHIYANATHAPUR", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "CS", "NSCET", "No");
        addDetail("437", "FY501", "GOVARTHINI K", "NORTH STREET", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "AI", "GHSS KODUVAI", "No");
        addDetail("438", "FY502", "SUVA RATHI S", "JEEVA COLONY", "N/A", "N/A", "180.0", "Others", "Counseling", "0", "ECE", "GOVT HR SEC SCHO", "No");
        addDetail("439", "ME11", "NAGA SURUTHI G", "THEVAR NAGAR 2ND ST", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "EST", "NSCET", "No");
        addDetail("440", "ME12", "SANJAY C", "301 15 PATTARAI STREE", "N/A", "N/A", "180.0", "DNC", "Management", "0", "EST", "NSCET", "No");
        addDetail("441", "ME13", "GOWRI M", "KAMATCHI AMMAN KOVIL", "N/A", "N/A", "180.0", "BC", "Management", "0", "CS", "NSCET", "No");
        addDetail("442", "ME14", "HARINI M", "E 11 JEYARAM NAGAR VA", "N/A", "N/A", "180.0", "SC", "Management", "0", "CS", "NSCET", "No");
        addDetail("443", "FY503", "HARIHARAN J", "VOC STREET THAMARAIK", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "IT", "7 TH DAY ADVENTIS", "No");
        addDetail("444", "FY504", "SURIYA PRAKASH K", "14 124 KALIYAMMAN KOV", "N/A", "N/A", "180.0", "MBC", "Counseling", "0", "MECH", "KCM MATRIC H S SC", "No");
        addDetail("445", "FY505", "HARSHINI B", "33 NGO COLONY", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "CSE", "SRI RENUGA VIDHYA", "No");
        addDetail("446", "ME15", "SAAI KEERTHI S", "7TH STREET LAKSHMI ILI", "N/A", "N/A", "180.0", "BC", "Management", "0", "AI", "NSCET", "No");
        addDetail("447", "FY506", "ANUNITHA B", "PULLIMAN KOMBAI MAIN", "N/A", "N/A", "180.0", "DNC", "Counseling", "0", "AI", "ANNAI VELAKANNI M", "No");
        addDetail("448", "ME16", "RESHMA S", "SUBRAMANIYAPURAM V", "N/A", "N/A", "180.0", "BC", "Counseling", "0", "EST", "NSCET", "No");
    }

    private void addDetail(String recNo, String appNo, String name, String addr, String hsc, String dote, String anna, String comm, String mgGq, String amt, String dept, String school, String hostel) {
        ApplicationReportDto r = new ApplicationReportDto();
        r.setReceiptNo(recNo);
        r.setAppNo(appNo);
        r.setStudentName(name);
        r.setAddress(addr);
        r.setHscMark(hsc);
        r.setDoteCutOff(dote);
        r.setAnnaUnivCutOff(anna);
        r.setCommunity(comm);
        r.setMgGq(mgGq);
        if (amt != null) r.setAmountPaid(new java.math.BigDecimal(amt));
        r.setDept(dept);
        r.setSchoolName(school);
        r.setHostel(hostel);
        detailList.add(r);
    }

    @FXML
    public void handleExport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Application Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Application Report to printer.");
        alert.showAndWait();
    }
}
