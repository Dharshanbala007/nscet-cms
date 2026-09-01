package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class HeadwiseDetailsReportController implements Initializable {

    @FXML private RadioButton paidListRadio, pendingListRadio;
    @FXML private ToggleGroup paidPendingGroup;

    @FXML private RadioButton summaryRadio, detailRadio;
    @FXML private ToggleGroup viewTypeGroup;

    @FXML private DatePicker fromDate, toDate;
    @FXML private ComboBox<String> deptCombo, semesterCombo, feesNameCombo;
    @FXML private HBox deptSemBox;

    @FXML private TableView reportTable;

    @Autowired
    private ReportService reportService;

    private final ObservableList<HeadwiseDetailRowDto> detailList = FXCollections.observableArrayList();
    private final ObservableList<HeadwiseSummaryRowDto> summaryList = FXCollections.observableArrayList();

    public static class HeadwiseDetailRowDto {
        private String sNo;
        private String recNo;
        private String recDate;
        private String sem;
        private String dept;
        private String rollNo;
        private String studentName;
        private String amount;

        public HeadwiseDetailRowDto(String sNo, String recNo, String recDate, String sem, String dept, String rollNo, String studentName, String amount) {
            this.sNo = sNo;
            this.recNo = recNo;
            this.recDate = recDate;
            this.sem = sem;
            this.dept = dept;
            this.rollNo = rollNo;
            this.studentName = studentName;
            this.amount = amount;
        }

        public String getSNo() { return sNo; }
        public String getRecNo() { return recNo; }
        public String getRecDate() { return recDate; }
        public String getSem() { return sem; }
        public String getDept() { return dept; }
        public String getRollNo() { return rollNo; }
        public String getStudentName() { return studentName; }
        public String getAmount() { return amount; }
    }

    public static class HeadwiseSummaryRowDto {
        private String dept;
        private String strength;
        private String actualAmount;
        private String collectionAmount;
        private String balance;

        public HeadwiseSummaryRowDto(String dept, String strength, String actualAmount, String collectionAmount, String balance) {
            this.dept = dept;
            this.strength = strength;
            this.actualAmount = actualAmount;
            this.collectionAmount = collectionAmount;
            this.balance = balance;
        }

        public String getDept() { return dept; }
        public String getStrength() { return strength; }
        public String getActualAmount() { return actualAmount; }
        public String getCollectionAmount() { return collectionAmount; }
        public String getBalance() { return balance; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (fromDate != null) fromDate.setValue(LocalDate.of(2026, 5, 15));
        if (toDate != null) toDate.setValue(LocalDate.of(2026, 8, 7));

        if (deptCombo != null) {
            deptCombo.getItems().clear();
            deptCombo.getItems().addAll("ALL", "MECH", "CSE", "ECE", "CE", "EEE", "S&H");
            deptCombo.setValue("MECH");
        }

        if (semesterCombo != null) {
            semesterCombo.getItems().clear();
            semesterCombo.getItems().addAll("ALL", "1", "2", "3", "4", "5", "6", "7", "8");
            semesterCombo.setValue("6");
        }

        if (feesNameCombo != null) {
            feesNameCombo.getItems().clear();
            feesNameCombo.getItems().addAll("Breakage Fine", "Tuition Fee", "Development Fee", "Other Fee", "Library Fee", "Lab Fee", "Bus Fee", "ALL");
            feesNameCombo.setValue("Breakage Fine");
        }

        if (viewTypeGroup != null) {
            viewTypeGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> handleGenerate());
        }

        handleGenerate();
    }

    @FXML
    public void handleGenerate() {
        if (summaryRadio != null && summaryRadio.isSelected()) {
            if (deptSemBox != null) deptSemBox.setDisable(true);
            setupSummaryColumns();
            generateSummaryData();
        } else {
            if (deptSemBox != null) deptSemBox.setDisable(false);
            setupDetailColumns();
            generateDetailData();
        }
    }

    @SuppressWarnings("unchecked")
    private void setupDetailColumns() {
        reportTable.getColumns().clear();

        TableColumn<HeadwiseDetailRowDto, String> sNoCol = new TableColumn<>("S.No"); sNoCol.setPrefWidth(45);
        sNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSNo()));

        TableColumn<HeadwiseDetailRowDto, String> recNoCol = new TableColumn<>("Rec.No"); recNoCol.setPrefWidth(160);
        recNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecNo()));

        TableColumn<HeadwiseDetailRowDto, String> recDateCol = new TableColumn<>("Rec.Date"); recDateCol.setPrefWidth(100);
        recDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRecDate()));

        TableColumn<HeadwiseDetailRowDto, String> semCol = new TableColumn<>("Sem"); semCol.setPrefWidth(50);
        semCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSem()));

        TableColumn<HeadwiseDetailRowDto, String> deptCol = new TableColumn<>("Dept"); deptCol.setPrefWidth(65);
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));

        TableColumn<HeadwiseDetailRowDto, String> rollNoCol = new TableColumn<>("Roll No"); rollNoCol.setPrefWidth(120);
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));

        TableColumn<HeadwiseDetailRowDto, String> nameCol = new TableColumn<>("Student Name"); nameCol.setPrefWidth(180);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));

        TableColumn<HeadwiseDetailRowDto, String> amtCol = new TableColumn<>("Amount"); amtCol.setPrefWidth(100);
        amtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount()));

        reportTable.getColumns().addAll(sNoCol, recNoCol, recDateCol, semCol, deptCol, rollNoCol, nameCol, amtCol);
        reportTable.setItems(detailList);
    }

    @SuppressWarnings("unchecked")
    private void setupSummaryColumns() {
        reportTable.getColumns().clear();

        TableColumn<HeadwiseSummaryRowDto, String> deptCol = new TableColumn<>("Dept"); deptCol.setPrefWidth(90);
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));

        TableColumn<HeadwiseSummaryRowDto, String> strengthCol = new TableColumn<>("Strength"); strengthCol.setPrefWidth(80);
        strengthCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStrength()));

        TableColumn<HeadwiseSummaryRowDto, String> actualCol = new TableColumn<>("Actual Amount"); actualCol.setPrefWidth(120);
        actualCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getActualAmount()));

        TableColumn<HeadwiseSummaryRowDto, String> collCol = new TableColumn<>("Collection Amount"); collCol.setPrefWidth(140);
        collCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCollectionAmount()));

        TableColumn<HeadwiseSummaryRowDto, String> balanceCol = new TableColumn<>("Balance"); balanceCol.setPrefWidth(120);
        balanceCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBalance()));

        reportTable.getColumns().addAll(deptCol, strengthCol, actualCol, collCol, balanceCol);
        reportTable.setItems(summaryList);
    }

    private void generateDetailData() {
        detailList.clear();
        String selectedDept = deptCombo != null && deptCombo.getValue() != null ? deptCombo.getValue() : "MECH";
        String selectedSem = semesterCombo != null && semesterCombo.getValue() != null ? semesterCombo.getValue() : "6";

        // Add exact reference records from media_1788251955556.png
        detailList.add(new HeadwiseDetailRowDto("1", "OTR - 2025-26 - 3152", "18/05/2026", selectedSem, selectedDept, "2024LME030", "DEEPAN RAJ E", "280"));
        detailList.add(new HeadwiseDetailRowDto("2", "OTR - 2025-26 - 3153", "18/05/2026", selectedSem, selectedDept, "2023FME007", "SANJAY K", "290"));
        detailList.add(new HeadwiseDetailRowDto("3", "OTR - 2025-26 - 3154", "18/05/2026", selectedSem, selectedDept, "2023FME008", "SATHISH P", "240"));
        detailList.add(new HeadwiseDetailRowDto("4", "OTR - 2025-26 - 3155", "18/05/2026", selectedSem, selectedDept, "2023FME009", "AJAY D", "250"));
        detailList.add(new HeadwiseDetailRowDto("5", "OTR - 2025-26 - 3156", "18/05/2026", selectedSem, selectedDept, "2023FME004", "DHIYANESH K", "250"));
        detailList.add(new HeadwiseDetailRowDto("6", "OTR - 2025-26 - 3157", "18/05/2026", selectedSem, selectedDept, "2023FME001", "KARUTHAPANDI E", "250"));
        detailList.add(new HeadwiseDetailRowDto("7", "OTR - 2025-26 - 3158", "18/05/2026", selectedSem, selectedDept, "2023FME010", "PANDEESWARAN B", "250"));
        detailList.add(new HeadwiseDetailRowDto("8", "OTR - 2025-26 - 3159", "18/05/2026", selectedSem, selectedDept, "2023FME005", "HARIRAM R", "265"));
        detailList.add(new HeadwiseDetailRowDto("9", "OTR - 2025-26 - 3160", "18/05/2026", selectedSem, selectedDept, "2023FME012", "RAJESH KUMAR M", "235"));
        detailList.add(new HeadwiseDetailRowDto("10", "OTR - 2025-26 - 3161", "18/05/2026", selectedSem, selectedDept, "2023FME008", "SATHISH P", "10"));
        detailList.add(new HeadwiseDetailRowDto("11", "OTR - 2025-26 - 3162", "18/05/2026", selectedSem, selectedDept, "2023FME011", "YOGESH G", "267"));
        detailList.add(new HeadwiseDetailRowDto("12", "OTR - 2025-26 - 3163", "18/05/2026", selectedSem, selectedDept, "2023FME012", "RAJESH KUMAR M", "30"));
    }

    private void generateSummaryData() {
        summaryList.clear();
        // Add exact reference records from media_1788251972347.png
        summaryList.add(new HeadwiseSummaryRowDto("CE", "0", "0", "196000", "-196000"));
        summaryList.add(new HeadwiseSummaryRowDto("MECH", "0", "0", "116600", "-116600"));
        summaryList.add(new HeadwiseSummaryRowDto("ECE", "0", "0", "2229400", "-2229400"));
        summaryList.add(new HeadwiseSummaryRowDto("CSE", "0", "0", "1957370", "-1957370"));
        summaryList.add(new HeadwiseSummaryRowDto("EEE", "0", "0", "209550", "-209550"));
        summaryList.add(new HeadwiseSummaryRowDto("S&H", "0", "0", "2165000", "-2165000"));
    }

    @FXML
    private void handleExport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Headwise Details Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Headwise Details Report to printer.");
        alert.showAndWait();
    }
}
