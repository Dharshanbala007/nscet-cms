package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.ReportService;
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
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class DfcrReportController implements Initializable {

    @FXML private RadioButton detailedRadio, summaryRadio;
    @FXML private ToggleGroup viewModeGroup;
    @FXML private DatePicker reportDatePicker;
    @FXML private TableView reportTable;

    @Autowired private ReportService reportService;

    private final ObservableList<DfcrDetailRowDto> detailList = FXCollections.observableArrayList();
    private final ObservableList<DfcrSummaryRowDto> summaryList = FXCollections.observableArrayList();

    public static class DfcrDetailRowDto {
        private String slNo;
        private String rollNo;
        private String studentName;
        private String dept;
        private String semester;
        private String receiptNo;
        private String payType;
        private String particulars;
        private String collegeFees;
        private String otherFees;
        private String busFees;
        private String activity;
        private String examFees;

        public DfcrDetailRowDto(String slNo, String rollNo, String studentName, String dept, String semester,
                               String receiptNo, String payType, String particulars, String collegeFees,
                               String otherFees, String busFees, String activity, String examFees) {
            this.slNo = slNo;
            this.rollNo = rollNo;
            this.studentName = studentName;
            this.dept = dept;
            this.semester = semester;
            this.receiptNo = receiptNo;
            this.payType = payType;
            this.particulars = particulars;
            this.collegeFees = collegeFees;
            this.otherFees = otherFees;
            this.busFees = busFees;
            this.activity = activity;
            this.examFees = examFees;
        }

        public String getSlNo() { return slNo; }
        public String getRollNo() { return rollNo; }
        public String getStudentName() { return studentName; }
        public String getDept() { return dept; }
        public String getSemester() { return semester; }
        public String getReceiptNo() { return receiptNo; }
        public String getPayType() { return payType; }
        public String getParticulars() { return particulars; }
        public String getCollegeFees() { return collegeFees; }
        public String getOtherFees() { return otherFees; }
        public String getBusFees() { return busFees; }
        public String getActivity() { return activity; }
        public String getExamFees() { return examFees; }
    }

    public static class DfcrSummaryRowDto {
        private String slNo;
        private String particulars;
        private String receiptNo;
        private String collegeFees;
        private String otherFees;
        private String busFees;
        private String examFees;
        private String karnaC;

        public DfcrSummaryRowDto(String slNo, String particulars, String receiptNo, String collegeFees,
                                String otherFees, String busFees, String examFees, String karnaC) {
            this.slNo = slNo;
            this.particulars = particulars;
            this.receiptNo = receiptNo;
            this.collegeFees = collegeFees;
            this.otherFees = otherFees;
            this.busFees = busFees;
            this.examFees = examFees;
            this.karnaC = karnaC;
        }

        public String getSlNo() { return slNo; }
        public String getParticulars() { return particulars; }
        public String getReceiptNo() { return receiptNo; }
        public String getCollegeFees() { return collegeFees; }
        public String getOtherFees() { return otherFees; }
        public String getBusFees() { return busFees; }
        public String getExamFees() { return examFees; }
        public String getKarnaC() { return karnaC; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (reportDatePicker != null) reportDatePicker.setValue(LocalDate.of(2025, 8, 7));

        if (viewModeGroup != null) {
            viewModeGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> handleGenerate());
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
    private void setupDetailColumns() {
        reportTable.getColumns().clear();

        TableColumn<DfcrDetailRowDto, String> slCol = new TableColumn<>("Sl.No"); slCol.setPrefWidth(45);
        slCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlNo()));

        TableColumn<DfcrDetailRowDto, String> rollCol = new TableColumn<>("Roll No"); rollCol.setPrefWidth(100);
        rollCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));

        TableColumn<DfcrDetailRowDto, String> nameCol = new TableColumn<>("Student Name"); nameCol.setPrefWidth(180);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));

        TableColumn<DfcrDetailRowDto, String> deptCol = new TableColumn<>("Dept"); deptCol.setPrefWidth(55);
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));

        TableColumn<DfcrDetailRowDto, String> semCol = new TableColumn<>("Semester"); semCol.setPrefWidth(65);
        semCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemester()));

        TableColumn<DfcrDetailRowDto, String> recCol = new TableColumn<>("Receipt No"); recCol.setPrefWidth(160);
        recCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));

        TableColumn<DfcrDetailRowDto, String> payCol = new TableColumn<>("Pay Type"); payCol.setPrefWidth(75);
        payCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPayType()));

        TableColumn<DfcrDetailRowDto, String> partCol = new TableColumn<>("Particulars"); partCol.setPrefWidth(320);
        partCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getParticulars()));

        TableColumn<DfcrDetailRowDto, String> colFees = new TableColumn<>("College Fees"); colFees.setPrefWidth(110);
        colFees.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCollegeFees()));

        TableColumn<DfcrDetailRowDto, String> othFees = new TableColumn<>("Other Fees"); othFees.setPrefWidth(90);
        othFees.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOtherFees()));

        TableColumn<DfcrDetailRowDto, String> busFees = new TableColumn<>("Bus Fees"); busFees.setPrefWidth(90);
        busFees.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusFees()));

        TableColumn<DfcrDetailRowDto, String> actCol = new TableColumn<>("Activity"); actCol.setPrefWidth(80);
        actCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getActivity()));

        TableColumn<DfcrDetailRowDto, String> exCol = new TableColumn<>("Exam Fees"); exCol.setPrefWidth(90);
        exCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExamFees()));

        reportTable.getColumns().addAll(slCol, rollCol, nameCol, deptCol, semCol, recCol, payCol, partCol, colFees, othFees, busFees, actCol, exCol);
        reportTable.setItems(detailList);
    }

    @SuppressWarnings("unchecked")
    private void setupSummaryColumns() {
        reportTable.getColumns().clear();

        TableColumn<DfcrSummaryRowDto, String> slCol = new TableColumn<>("Sl.No"); slCol.setPrefWidth(45);
        slCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlNo()));

        TableColumn<DfcrSummaryRowDto, String> partCol = new TableColumn<>("Particulars"); partCol.setPrefWidth(220);
        partCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getParticulars()));

        TableColumn<DfcrSummaryRowDto, String> recCol = new TableColumn<>("Receipt No"); recCol.setPrefWidth(240);
        recCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));

        TableColumn<DfcrSummaryRowDto, String> colFees = new TableColumn<>("College Fees"); colFees.setPrefWidth(110);
        colFees.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCollegeFees()));

        TableColumn<DfcrSummaryRowDto, String> othFees = new TableColumn<>("Other Fees"); othFees.setPrefWidth(90);
        othFees.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOtherFees()));

        TableColumn<DfcrSummaryRowDto, String> busFees = new TableColumn<>("Bus Fees"); busFees.setPrefWidth(90);
        busFees.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusFees()));

        TableColumn<DfcrSummaryRowDto, String> exCol = new TableColumn<>("Exam Fees"); exCol.setPrefWidth(90);
        exCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExamFees()));

        TableColumn<DfcrSummaryRowDto, String> karCol = new TableColumn<>("Karna C"); karCol.setPrefWidth(80);
        karCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKarnaC()));

        reportTable.getColumns().addAll(slCol, partCol, recCol, colFees, othFees, busFees, exCol, karCol);
        reportTable.setItems(summaryList);
    }

    private void generateDetailData() {
        detailList.clear();
        // Exact reference records from media_1788253993947.png
        detailList.add(new DfcrDetailRowDto("1", "2024FIT018", "MANISHVARMA S", "IT", "3", "TUF - 2025-26 - 347", "Pay", "Tuition Fee", "14500", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("2", "2024FCE018", "PRADEEP P", "CE", "3", "TUF - 2025-26 - 348", "Pay", "Tuition Fee", "5000", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("3", "2022FEC037", "AMRISHAA S", "ECE", "7", "TUF - 2025-26 - 349", "Pay", "Tuition Fee", "20000", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("4", "2024FEC024", "HARINANTHASRI S", "ECE", "3", "TUF - 2025-26 - 350", "Pay", "Tuition Fee", "35000", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("5", "2024FAI049", "ANEESHA S", "AI", "3", "TUF - 2025-26 - 351", "Pay", "Tuition Fee", "52800", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("6", "2025FCS012", "HARSHINI S", "CSE", "1", "TUF - 2025-26 - 352", "DD\\Chequ", "Adjustments - Tuition Fee - CH NO: 056594 DT: 24/7/2025 AMT", "25000", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("7", "2022FCS055", "RESHMA SHREE L V", "CSE", "7", "TUF - 2025-26 - 353", "OLP", "Adjustments - Tuition Fee - UPI 629097129923 DT:27.07.2025", "1", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("8", "2022FCS055", "RESHMA SHREE L V", "CSE", "7", "TUF - 2025-26 - 354", "OLP", "Adjustments - Tuition Fee - UPI 222727992151 DT:01.08.2025", "22380", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("9", "2024FEE019", "RAJIYA SULTHANA A", "EEE", "3", "TUF - 2025-26 - 355", "Pay", "Tuition Fee", "4650", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("10", "2025FEC035", "DHARANI SHREE S", "ECE", "1", "TUF - 2025-26 - 356", "Pay", "Tuition Fee", "25000", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("11", "2024LCE021", "GEERI R", "CE", "5", "TUF - 2025-26 - 357", "Pay", "Tuition Fee", "40000", "", "", "", ""));
        detailList.add(new DfcrDetailRowDto("12", "2024FME031", "CHANDRESHWARAN G", "MECH", "3", "TUF - 2025-26 - 358", "Pay", "Tuition Fee", "200", "", "", "", ""));
    }

    private void generateSummaryData() {
        summaryList.clear();
        // Exact reference records from media_1788254005817.png
        summaryList.add(new DfcrSummaryRowDto("1", "07/08/2025 - Balance Amount", "", "", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("2", "Tuition Fee", "371 to 390,1701", "718700", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("3", "Admission Fees", "385 to 387,389 to 391,394,397 to", "11000", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("4", "Conference Registration", "393", "1200", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("5", "Consortium Application Fee", "392,396,400,405", "200", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("6", "Consortium DD Charge", "392,396,400,405", "400", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("7", "Consortium DD", "392,396,400,405", "1800", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("8", "Fine - Leave", "402", "100", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("9", "Development Fees", "385,388,390 to 391,395,397,399,4", "125000", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("10", "Miscellaneous", "406", "1009", "", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("11", "Anna University Reg Fee", "751,764,768,773,775,777,779,783", "", "18000", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("12", "Other fee", "744,746,748,750,752,755,757,759", "", "106200", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("13", "Sports Uniform - Girls", "745,747,749,751,756,758,763,767", "", "10200", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("14", "Lab Fee", "744,746,748,750,752,755,757,759", "", "15000", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("15", "Professional Society Membership", "744,746,748,750,753,755,757,759", "", "10000", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("16", "Student Insurance", "745 to 746,748,750,752,756,758,7", "", "6300", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("17", "Students Association", "744,747,749,751,753,755,757,759", "", "6300", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("18", "Value Added Courses", "746,748,750,755,759,761,763,765", "", "18000", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("19", "Sports Uniform", "753,760 to 761,765", "", "2400", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("20", "Sports Day, Annual Day & Other", "745,747 to 748,751 to 752,755,75", "", "56000", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("21", "Uniform - Boys", "753 to 754,759,761,765,785", "", "12300", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("22", "Uniform - Girls", "744,747,749,751,756,758,763,767", "", "43500", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("23", "Alumni Registration Fee", "744,752,757,782", "", "2000", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("24", "Placement & Training Classes", "744,746,749 to 750,752,755,757,7", "", "39000", "", "", ""));
        summaryList.add(new DfcrSummaryRowDto("25", "Bus Fees", "292 to 293,289 to 290,290 to 291", "", "", "156720", "", ""));
        summaryList.add(new DfcrSummaryRowDto("26", "Student Donor A/C", "744,746,748,750,752,755,757,759", "", "", "", "", ""));
    }

    @FXML
    public void handleSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save DFCR Report");
        alert.setHeaderText(null);
        alert.setContentText("Daily Fees Collection Register saved successfully.");
        alert.showAndWait();
    }

    @FXML
    public void handleExport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print DFCR Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Daily Fees Collection Register to printer.");
        alert.showAndWait();
    }
}
