package com.nscet.cms.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StudentReceiptDetailsController implements Initializable {

    @FXML private ComboBox<String> deptCombo;
    @FXML private TextField studentNameField, rollNoField, regNoField, estimatedAmtField, totalAmtField;
    @FXML private TableView<StudentReceiptRow> receiptTable;
    @FXML private TableColumn<StudentReceiptRow, String> receiptNoCol, receiptDateCol, rollNoCol, particularsCol, amountCol, semesterCol, remarksCol;

    private ObservableList<StudentReceiptRow> rowList = FXCollections.observableArrayList();

    public static class StudentReceiptRow {
        private String receiptNo, receiptDate, rollNo, particulars, amount, semester, remarks;

        public StudentReceiptRow(String receiptNo, String receiptDate, String rollNo, String particulars, String amount, String semester, String remarks) {
            this.receiptNo = receiptNo;
            this.receiptDate = receiptDate;
            this.rollNo = rollNo;
            this.particulars = particulars;
            this.amount = amount;
            this.semester = semester;
            this.remarks = remarks;
        }

        public String getReceiptNo() { return receiptNo; }
        public String getReceiptDate() { return receiptDate; }
        public String getRollNo() { return rollNo; }
        public String getParticulars() { return particulars; }
        public String getAmount() { return amount; }
        public String getSemester() { return semester; }
        public String getRemarks() { return remarks; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deptCombo.getItems().addAll("CE", "CSE", "ECE", "MECH", "EEE", "IT", "AI", "SE");
        deptCombo.getSelectionModel().selectFirst();

        setupTableColumns();
        receiptTable.setItems(rowList);
        loadSampleReceipts();
    }

    private void setupTableColumns() {
        receiptNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptNo()));
        receiptDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReceiptDate()));
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));
        particularsCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getParticulars()));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty("₹" + c.getValue().getAmount()));
        semesterCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemester()));
        remarksCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks()));
    }

    private void loadSampleReceipts() {
        rowList.clear();
        rowList.add(new StudentReceiptRow("MIS - 2025-26 - 389", "08/08/2025", "2025FCE006", "Admission Fees", "1000", "1", "UPI 558656778919 DT: 8/8/2025 AT: 26530"));
        rowList.add(new StudentReceiptRow("OTR - 2025-26 - 772", "08/08/2025", "2025FCE006", "Student Donor A/C", "200", "1", ""));
        rowList.add(new StudentReceiptRow("OTR - 2025-26 - 772", "08/08/2025", "2025FCE006", "Student Insurance", "300", "1", ""));
        rowList.add(new StudentReceiptRow("OTR - 2025-26 - 772", "08/08/2025", "2025FCE006", "Students Association", "300", "1", ""));
        rowList.add(new StudentReceiptRow("OTR - 2025-26 - 772", "08/08/2025", "2025FCE006", "Sports Uniform - Girls", "600", "1", ""));
        rowList.add(new StudentReceiptRow("OTR - 2025-26 - 772", "08/08/2025", "2025FCE006", "Value Added Courses", "1000", "1", ""));
        rowList.add(new StudentReceiptRow("TUF - 2025-26 - 929", "25/10/2025", "2025FCE006", "Tuition Fee", "25000", "1", "RTG/THE PRINCIPALA/TMBLR520251007001"));
        rowList.add(new StudentReceiptRow("BUS - 2025-26 - 1890", "03/07/2026", "2025FCE006", "Bus Fees-CHINNAMANUR TO COLLI", "11135", "3", ""));
        totalAmtField.setText("121025");
    }

    @FXML private void handleView() { loadSampleReceipts(); }
    @FXML private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Receipt Details");
        alert.setHeaderText(null);
        alert.setContentText("Sending Student Receipt Details for " + studentNameField.getText() + " to printer.");
        alert.showAndWait();
    }
}
