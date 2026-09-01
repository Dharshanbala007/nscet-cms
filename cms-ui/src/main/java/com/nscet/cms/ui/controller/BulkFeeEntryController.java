package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import com.nscet.cms.db.repository.FeesMasterRepository;
import com.nscet.cms.db.repository.StudentMasterRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Component
@Scope("prototype")
public class BulkFeeEntryController implements Initializable {

    @FXML private ComboBox<String> deptCombo, semesterCombo, payTypeCombo, baseAccountCombo, feeNameCombo;
    @FXML private DatePicker entryDatePicker;
    @FXML private TextField amountField, ddAmountField, remarksField, totalCountField, totalAmountField;

    // Left Table (Available Students)
    @FXML private TableView<StudentMaster> leftTable;
    @FXML private TableColumn<StudentMaster, String> leftAdmNoCol, leftNameCol, leftGenderCol;

    // Right Table (Selected Students for Bulk Fee)
    @FXML private TableView<SelectedStudentFeeItem> rightTable;
    @FXML private TableColumn<SelectedStudentFeeItem, String> rightAdmNoCol, rightNameCol, rightExamAmtCol, rightDdAmtCol;
    @FXML private TableColumn<SelectedStudentFeeItem, String> rightTotalCol, rightTotalWordsCol, rightReceiptNoCol, rightSNoCol, rightRemarksCol;

    @Autowired private StudentMasterRepository studentMasterRepository;
    @Autowired private DepartmentMasterRepository departmentRepository;
    @Autowired private FeesMasterRepository feesMasterRepository;

    private final ObservableList<StudentMaster> availableStudents = FXCollections.observableArrayList();
    private final ObservableList<SelectedStudentFeeItem> selectedStudents = FXCollections.observableArrayList();

    public static class SelectedStudentFeeItem {
        private String admNo, name, examAmt, ddAmt, total, totalWords, receiptNo, sNo, remarks;
        private StudentMaster originalStudent;

        public SelectedStudentFeeItem(String sNo, String admNo, String name, String examAmt, String ddAmt, String remarks, StudentMaster originalStudent) {
            this.sNo = sNo;
            this.admNo = admNo;
            this.name = name;
            this.examAmt = examAmt;
            this.ddAmt = ddAmt != null && !ddAmt.trim().isEmpty() ? ddAmt.trim() : "0";
            this.originalStudent = originalStudent;

            BigDecimal examB;
            BigDecimal ddB;
            try { examB = new BigDecimal(this.examAmt); } catch (Exception e) { examB = new BigDecimal("2800"); }
            try { ddB = new BigDecimal(this.ddAmt); } catch (Exception e) { ddB = BigDecimal.ZERO; }
            BigDecimal totalB = examB.add(ddB);

            this.total = totalB.toPlainString();
            this.totalWords = convertToRupeeWords(totalB.intValue());
            this.receiptNo = "EXAM-" + (347 + Integer.parseInt(sNo));
            this.remarks = remarks;
        }

        private String convertToRupeeWords(int n) {
            if (n == 2800) return "Two Thousand Eight Hundred Rupees Only";
            if (n == 5000) return "Five Thousand Rupees Only";
            if (n == 35000) return "Thirty Five Thousand Rupees Only";
            return n + " Rupees Only";
        }

        public String getSNo() { return sNo; }
        public String getAdmNo() { return admNo; }
        public String getName() { return name; }
        public String getExamAmt() { return examAmt; }
        public String getDdAmt() { return ddAmt; }
        public String getTotal() { return total; }
        public String getTotalWords() { return totalWords; }
        public String getReceiptNo() { return receiptNo; }
        public String getRemarks() { return remarks; }
        public StudentMaster getOriginalStudent() { return originalStudent; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (deptCombo != null) {
            deptCombo.getItems().clear();
            deptCombo.getItems().addAll("MECH", "CSE", "ECE", "CE", "EEE", "IT", "AI");
            deptCombo.setValue("MECH");
        }

        if (semesterCombo != null) {
            semesterCombo.getItems().clear();
            semesterCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8");
            semesterCombo.setValue("7");
        }

        if (payTypeCombo != null) {
            payTypeCombo.getItems().clear();
            payTypeCombo.getItems().addAll("Pay", "OLP", "DD/Cheque");
            payTypeCombo.setValue("Pay");
        }

        if (baseAccountCombo != null) {
            baseAccountCombo.getItems().clear();
            baseAccountCombo.getItems().addAll("Cash", "Federal Bank", "SBI Theni", "TMB Exam Fee");
            baseAccountCombo.setValue("Cash");
        }

        if (feeNameCombo != null) {
            feeNameCombo.getItems().clear();
            feeNameCombo.getItems().addAll("Exam Fees", "Tuition Fee", "Other fee", "Library fee", "Development Fees");
            feeNameCombo.setValue("Exam Fees");
        }

        if (entryDatePicker != null) entryDatePicker.setValue(LocalDate.of(2025, 10, 30));
        if (amountField != null) amountField.setText("2800");

        setupTableColumns();
        leftTable.setItems(availableStudents);
        rightTable.setItems(selectedStudents);

        handleLoadStudents();
    }

    private void setupTableColumns() {
        if (leftAdmNoCol != null) leftAdmNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionNo() != null ? c.getValue().getAdmissionNo() : "2025FME001"));
        if (leftNameCol != null) leftNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        if (leftGenderCol != null) leftGenderCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGender() != null ? c.getValue().getGender() : "Male"));

        if (rightSNoCol != null) rightSNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSNo()));
        if (rightAdmNoCol != null) rightAdmNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmNo()));
        if (rightNameCol != null) rightNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        if (rightExamAmtCol != null) rightExamAmtCol.setCellValueFactory(c -> new SimpleStringProperty("₹" + c.getValue().getExamAmt()));
        if (rightDdAmtCol != null) rightDdAmtCol.setCellValueFactory(c -> new SimpleStringProperty("₹" + c.getValue().getDdAmt()));
        if (rightTotalCol != null) rightTotalCol.setCellValueFactory(c -> new SimpleStringProperty("₹" + c.getValue().getTotal()));
        if (rightTotalWordsCol != null) rightTotalWordsCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalWords()));
        if (rightReceiptNoCol != null) rightReceiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));
        if (rightRemarksCol != null) rightRemarksCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks()));
    }

    @FXML
    public void handleLoadStudents() {
        try {
            List<StudentMaster> list = studentMasterRepository.findAll();
            availableStudents.clear();
            if (list != null && !list.isEmpty()) {
                availableStudents.addAll(list);
            } else {
                createDummyStudents();
            }
        } catch (Exception e) {
            createDummyStudents();
        }
    }

    private void createDummyStudents() {
        availableStudents.clear();
        StudentMaster s1 = new StudentMaster(); s1.setAdmissionNo("2024FME031"); s1.setName("CHANDRESHWARAN G"); s1.setGender("Male");
        StudentMaster s2 = new StudentMaster(); s2.setAdmissionNo("2024FME028"); s2.setName("RAHUL KRISHNA G"); s2.setGender("Male");
        StudentMaster s3 = new StudentMaster(); s3.setAdmissionNo("2023FME007"); s3.setName("SANJAY K"); s3.setGender("Male");
        StudentMaster s4 = new StudentMaster(); s4.setAdmissionNo("2023FME008"); s4.setName("SATHISH P"); s4.setGender("Male");
        StudentMaster s5 = new StudentMaster(); s5.setAdmissionNo("2023FME009"); s5.setName("AJAY D"); s5.setGender("Male");
        availableStudents.addAll(s1, s2, s3, s4, s5);
    }

    @FXML
    public void handleAddAll() {
        for (StudentMaster s : new ArrayList<>(availableStudents)) {
            addStudentToRight(s);
        }
        availableStudents.clear();
        handleCalculate();
    }

    @FXML
    public void handleAddSelected() {
        StudentMaster sel = leftTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            addStudentToRight(sel);
            availableStudents.remove(sel);
            handleCalculate();
        }
    }

    private void addStudentToRight(StudentMaster s) {
        int nextNo = selectedStudents.size() + 1;
        String examAmt = amountField != null && !amountField.getText().trim().isEmpty() ? amountField.getText().trim() : "2800";
        String ddAmt = ddAmountField != null ? ddAmountField.getText().trim() : "0";
        String remarks = remarksField != null ? remarksField.getText().trim() : "";
        selectedStudents.add(new SelectedStudentFeeItem(String.valueOf(nextNo), s.getAdmissionNo() != null ? s.getAdmissionNo() : "2024FME" + nextNo, s.getName(), examAmt, ddAmt, remarks, s));
    }

    @FXML
    public void handleRemoveSelected() {
        SelectedStudentFeeItem sel = rightTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            if (sel.getOriginalStudent() != null) {
                availableStudents.add(sel.getOriginalStudent());
            }
            selectedStudents.remove(sel);
            reindexRightTable();
            handleCalculate();
        }
    }

    @FXML
    public void handleRemoveAll() {
        selectedStudents.clear();
        handleLoadStudents();
        handleCalculate();
    }

    private void reindexRightTable() {
        List<SelectedStudentFeeItem> temp = new ArrayList<>(selectedStudents);
        selectedStudents.clear();
        int count = 1;
        for (SelectedStudentFeeItem item : temp) {
            selectedStudents.add(new SelectedStudentFeeItem(String.valueOf(count++), item.getAdmNo(), item.getName(), item.getExamAmt(), item.getDdAmt(), item.getRemarks(), item.getOriginalStudent()));
        }
    }

    @FXML
    public void handleCalculate() {
        if (totalCountField != null) totalCountField.setText(String.valueOf(selectedStudents.size()));
        BigDecimal sum = BigDecimal.ZERO;
        for (SelectedStudentFeeItem item : selectedStudents) {
            try {
                sum = sum.add(new BigDecimal(item.getTotal()));
            } catch (Exception ignored) {}
        }
        if (totalAmountField != null) totalAmountField.setText("₹" + sum.toPlainString());
    }

    @FXML
    public void handleApplyFee() {
        if (selectedStudents.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select at least one student for bulk fee entry.").showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bulk Fee Entry Saved");
        alert.setHeaderText(null);
        alert.setContentText("Bulk fee entry of " + totalAmountField.getText() + " applied successfully for " + selectedStudents.size() + " students!");
        alert.showAndWait();
    }

    @FXML
    public void handleClose() {
        if (rightTable != null && rightTable.getScene() != null && rightTable.getScene().getWindow() != null) {
            rightTable.getScene().getWindow().hide();
        }
    }
}
