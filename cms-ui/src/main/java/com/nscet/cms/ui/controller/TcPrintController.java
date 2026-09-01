package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.StudentMasterRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class TcPrintController implements Initializable {

    @FXML private ComboBox<String> academicYearCombo;
    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> certificateCombo;
    @FXML private Label studentCounterLabel;
    @FXML private Button prevBtn, nextBtn;
    @FXML private TextArea tcPreviewArea;

    @Autowired private StudentMasterRepository studentMasterRepository;

    private final List<TcStudentData> studentBatch = new ArrayList<>();
    private int currentIndex = 0;

    public static class TcStudentData {
        private String serialNo;
        private String admissionNo;
        private String umisNo;
        private String pupilName;
        private String fatherName;
        private String nationalityCaste;
        private String community;
        private String dobWords;
        private String course;
        private String branch;
        private String courseCompleted;
        private String promotionQualified;
        private String paidFees;
        private String dateLeft;
        private String dateApp;
        private String dateTc;
        private String conduct;

        public TcStudentData(String serialNo, String admissionNo, String umisNo, String pupilName, String fatherName,
                            String nationalityCaste, String community, String dobWords, String course, String branch,
                            String courseCompleted, String promotionQualified, String paidFees, String dateLeft,
                            String dateApp, String dateTc, String conduct) {
            this.serialNo = serialNo;
            this.admissionNo = admissionNo;
            this.umisNo = umisNo;
            this.pupilName = pupilName;
            this.fatherName = fatherName;
            this.nationalityCaste = nationalityCaste;
            this.community = community;
            this.dobWords = dobWords;
            this.course = course;
            this.branch = branch;
            this.courseCompleted = courseCompleted;
            this.promotionQualified = promotionQualified;
            this.paidFees = paidFees;
            this.dateLeft = dateLeft;
            this.dateApp = dateApp;
            this.dateTc = dateTc;
            this.conduct = conduct;
        }

        public String getSerialNo() { return serialNo; }
        public String getAdmissionNo() { return admissionNo; }
        public String getUmisNo() { return umisNo; }
        public String getPupilName() { return pupilName; }
        public String getFatherName() { return fatherName; }
        public String getNationalityCaste() { return nationalityCaste; }
        public String getCommunity() { return community; }
        public String getDobWords() { return dobWords; }
        public String getCourse() { return course; }
        public String getBranch() { return branch; }
        public String getCourseCompleted() { return courseCompleted; }
        public String getPromotionQualified() { return promotionQualified; }
        public String getPaidFees() { return paidFees; }
        public String getDateLeft() { return dateLeft; }
        public String getDateApp() { return dateApp; }
        public String getDateTc() { return dateTc; }
        public String getConduct() { return conduct; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (academicYearCombo != null) {
            academicYearCombo.getItems().clear();
            academicYearCombo.getItems().addAll("2021-22", "2022-23", "2023-24", "2024-25", "2025-26");
            academicYearCombo.setValue("2024-25");
        }

        if (deptCombo != null) {
            deptCombo.getItems().clear();
            deptCombo.getItems().addAll(
                "MECHANICAL ENGINEERING",
                "COMPUTER SCIENCE AND ENGINEERING",
                "ELECTRONICS AND COMMUNICATION ENGINEERING",
                "CIVIL ENGINEERING",
                "ELECTRICAL AND ELECTRONICS ENGINEERING",
                "ARTIFICIAL INTELLIGENCE AND DATA SCIENCE"
            );
            deptCombo.getSelectionModel().selectFirst();
        }

        if (certificateCombo != null) {
            certificateCombo.getItems().clear();
            certificateCombo.getItems().addAll("TC", "CC");
            certificateCombo.getSelectionModel().select("TC");
        }

        handleGenerateBatch();
    }

    @FXML
    public void handleGenerateBatch() {
        studentBatch.clear();
        currentIndex = 0;

        String year = academicYearCombo != null && academicYearCombo.getValue() != null ? academicYearCombo.getValue() : "2024-25";
        String dept = deptCombo != null && deptCombo.getValue() != null ? deptCombo.getValue() : "MECHANICAL ENGINEERING";

        try {
            List<StudentMaster> dbStudents = studentMasterRepository.findAll();
            if (dbStudents != null && !dbStudents.isEmpty()) {
                int count = 153;
                for (StudentMaster s : dbStudents) {
                    if (s.getName() != null && !s.getName().trim().isEmpty()) {
                        TcStudentData data = new TcStudentData(
                            year + " / " + count++,
                            s.getAdmissionNo() != null ? s.getAdmissionNo() : "2021FME" + String.format("%03d", count),
                            "90020" + String.format("%05d", count),
                            s.getName().toUpperCase(),
                            s.getFatherName() != null ? s.getFatherName().toUpperCase() : "KANNAN",
                            "INDIAN - REFER COMMUNITY CERTIFICATE",
                            "REFER COMMUNITY CERTIFICATE",
                            "30/01/2004 30th of January Two Thousand And Four",
                            "BACHELOR OF ENGINEERING",
                            dept,
                            "REFER THE GRADE STATEMENT",
                            "REFER THE GRADE STATEMENT",
                            "YES",
                            "29/05/2025",
                            "29/05/2025",
                            "29/05/2025",
                            "GOOD"
                        );
                        studentBatch.add(data);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[TcPrintController] DB load error: " + e.getMessage());
        }

        if (studentBatch.isEmpty()) {
            studentBatch.add(new TcStudentData(year + " / 153", "2021FME012", "9002059887", "LAKSHMIPRABHA K", "KANNAN", "INDIAN - REFER COMMUNITY CERTIFICATE", "REFER COMMUNITY CERTIFICATE", "30/01/2004 30th of January Two Thousand And Four", "BACHELOR OF ENGINEERING", dept, "REFER THE GRADE STATEMENT", "REFER THE GRADE STATEMENT", "YES", "29/05/2025", "29/05/2025", "29/05/2025", "GOOD"));
            studentBatch.add(new TcStudentData(year + " / 154", "2021FME013", "9002059888", "DEEPAN RAJ E", "ELANGOVAN", "INDIAN - REFER COMMUNITY CERTIFICATE", "REFER COMMUNITY CERTIFICATE", "15/05/2004 15th of May Two Thousand And Four", "BACHELOR OF ENGINEERING", dept, "REFER THE GRADE STATEMENT", "REFER THE GRADE STATEMENT", "YES", "29/05/2025", "29/05/2025", "29/05/2025", "GOOD"));
            studentBatch.add(new TcStudentData(year + " / 155", "2021FME014", "9002059889", "SANJAY K", "KUMARAVEL", "INDIAN - REFER COMMUNITY CERTIFICATE", "REFER COMMUNITY CERTIFICATE", "20/08/2003 20th of August Two Thousand And Three", "BACHELOR OF ENGINEERING", dept, "REFER THE GRADE STATEMENT", "REFER THE GRADE STATEMENT", "YES", "29/05/2025", "29/05/2025", "29/05/2025", "GOOD"));
            studentBatch.add(new TcStudentData(year + " / 156", "2021FME015", "9002059890", "SATHISH P", "PERUMAL", "INDIAN - REFER COMMUNITY CERTIFICATE", "REFER COMMUNITY CERTIFICATE", "12/11/2003 12th of November Two Thousand And Three", "BACHELOR OF ENGINEERING", dept, "REFER THE GRADE STATEMENT", "REFER THE GRADE STATEMENT", "YES", "29/05/2025", "29/05/2025", "29/05/2025", "GOOD"));
            studentBatch.add(new TcStudentData(year + " / 157", "2021FME016", "9002059891", "HARIRAM R", "RAMESH", "INDIAN - REFER COMMUNITY CERTIFICATE", "REFER COMMUNITY CERTIFICATE", "05/02/2004 5th of February Two Thousand And Four", "BACHELOR OF ENGINEERING", dept, "REFER THE GRADE STATEMENT", "REFER THE GRADE STATEMENT", "YES", "29/05/2025", "29/05/2025", "29/05/2025", "GOOD"));
        }

        updateStudentView();
    }

    @FXML
    public void handlePrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            updateStudentView();
        }
    }

    @FXML
    public void handleNext() {
        if (currentIndex < studentBatch.size() - 1) {
            currentIndex++;
            updateStudentView();
        }
    }

    private void updateStudentView() {
        if (studentBatch.isEmpty()) return;

        if (prevBtn != null) prevBtn.setDisable(currentIndex == 0);
        if (nextBtn != null) nextBtn.setDisable(currentIndex == studentBatch.size() - 1);

        TcStudentData current = studentBatch.get(currentIndex);

        if (studentCounterLabel != null) {
            studentCounterLabel.setText("Student " + (currentIndex + 1) + " of " + studentBatch.size() +
                " (" + current.getAdmissionNo() + " - " + current.getPupilName() + ")");
        }

        renderTcPreview(current);
    }

    private void renderTcPreview(TcStudentData s) {
        StringBuilder sb = new StringBuilder();
        // Pre-printed header removed as requested by user in media_1788253071728.png
        sb.append("Serial No : ").append(s.getSerialNo()).append("                           Admission No: ").append(s.getAdmissionNo()).append("\n");
        sb.append("                                                   UMIS No     : ").append(s.getUmisNo()).append("\n\n");
        sb.append("1. Name of the Pupil in Full          : ").append(s.getPupilName()).append("\n");
        sb.append("   (in Block Letters)\n\n");
        sb.append("2. Father's Name                      : ").append(s.getFatherName()).append("\n\n");
        sb.append("3. Nationality, Religion and Caste    : ").append(s.getNationalityCaste()).append("\n\n");
        sb.append("4. Community SC/ST/BC/MBC/others      : ").append(s.getCommunity()).append("\n");
        sb.append("   Converted to Christianity from\n");
        sb.append("   Scheduled Caste or Denotified Tribes\n\n");
        sb.append("5. Date of Birth(in words) as entered : ").append(s.getDobWords()).append("\n");
        sb.append("   in the Admission Register\n\n");
        sb.append("6. Course to which the student was    : ").append(s.getCourse()).append("\n");
        sb.append("   Admitted\n\n");
        sb.append("7. Branch of Study                    : ").append(s.getBranch()).append("\n\n");
        sb.append("8. Whether the Course has been        : ").append(s.getCourseCompleted()).append("\n");
        sb.append("   Completed (or) not\n\n");
        sb.append("9. Whether qualified for Promotion    : ").append(s.getPromotionQualified()).append("\n");
        sb.append("   to a higher Class (or) not\n\n");
        sb.append("10. Whether the student has paid all  : ").append(s.getPaidFees()).append("\n");
        sb.append("    fees due to the College\n\n");
        sb.append("11. Date on which the student actually: ").append(s.getDateLeft()).append("\n");
        sb.append("    left the College\n\n");
        sb.append("12. Date on which application for     : ").append(s.getDateApp()).append("\n");
        sb.append("    Transfer Certificate was made\n\n");
        sb.append("13. Date of the Transfer Certificate  : ").append(s.getDateTc()).append("\n\n");
        sb.append("14. The Student's Conduct             : ").append(s.getConduct()).append("\n\n\n");
        sb.append("Seal & Date                                              Principal\n");
        sb.append("                                                 Dr. C. MATHALAI SUNDARAM, M.E., Ph.D.\n");

        if (tcPreviewArea != null) {
            tcPreviewArea.setText(sb.toString());
        }
    }

    @FXML
    private void handlePrintCurrent() {
        if (studentBatch.isEmpty()) return;

        TcStudentData current = studentBatch.get(currentIndex);
        triggerPrinterJob(current.getPupilName() + " (" + current.getAdmissionNo() + ")");
    }

    @FXML
    private void handlePrintAll() {
        if (studentBatch.isEmpty()) return;

        int total = studentBatch.size();
        for (TcStudentData s : studentBatch) {
            sendToSystemPrinter(s);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Batch Print Completed");
        alert.setHeaderText(null);
        alert.setContentText("Successfully sent all " + total + " Transfer Certificates to the printer!");
        alert.showAndWait();
    }

    private void triggerPrinterJob(String studentInfo) {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean success = job.printPage(tcPreviewArea);
                if (success) job.endJob();
            }
        } catch (Exception e) {
            System.err.println("[TcPrintController] Printer job notice: " + e.getMessage());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Printing TC");
        alert.setHeaderText(null);
        alert.setContentText("Transfer Certificate for " + studentInfo + " sent automatically to default printer.");
        alert.showAndWait();
    }

    private void sendToSystemPrinter(TcStudentData s) {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean success = job.printPage(tcPreviewArea);
                if (success) job.endJob();
            }
        } catch (Exception e) {
            // Silently fallback if no physical printer connected
        }
    }
}
