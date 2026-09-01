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
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class ParentsMeetingController implements Initializable {

    @FXML private ComboBox<String> deptCombo, batchCombo, sectionCombo;
    @FXML private DatePicker meetingDatePicker, fromDatePicker, toDatePicker;
    @FXML private RadioButton parentsMeetingRadio, internalTestRadio;
    @FXML private ToggleGroup meetingTypeGroup;
    @FXML private CheckBox univIntMarkCheck;
    
    @FXML private TableView<ParentsMeetingItem> studentTable;
    @FXML private TableColumn<ParentsMeetingItem, String> slNoCol, rollNoCol, nameCol, fatherNameCol;
    @FXML private TableColumn<ParentsMeetingItem, String> attendanceCol, internalMarksCol, remarksCol;

    private final ObservableList<ParentsMeetingItem> studentList = FXCollections.observableArrayList();

    public static class ParentsMeetingItem {
        private String slNo, rollNo, name, fatherName, attendance, internalMarks, remarks;

        public ParentsMeetingItem(String slNo, String rollNo, String name, String fatherName, String attendance, String internalMarks, String remarks) {
            this.slNo = slNo;
            this.rollNo = rollNo;
            this.name = name;
            this.fatherName = fatherName;
            this.attendance = attendance;
            this.internalMarks = internalMarks;
            this.remarks = remarks;
        }

        public String getSlNo() { return slNo; }
        public String getRollNo() { return rollNo; }
        public String getName() { return name; }
        public String getFatherName() { return fatherName; }
        public String getAttendance() { return attendance; }
        public String getInternalMarks() { return internalMarks; }
        public String getRemarks() { return remarks; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (deptCombo != null) {
            deptCombo.getItems().clear();
            deptCombo.getItems().addAll("CSE", "ECE", "MECH", "CE", "EEE", "IT", "AI");
            deptCombo.setValue("CSE");
        }

        if (batchCombo != null) {
            batchCombo.getItems().clear();
            batchCombo.getItems().addAll("2022-2026", "2021-2025", "2023-2027", "2024-2028");
            batchCombo.setValue("2022-2026");
        }

        if (sectionCombo != null) {
            sectionCombo.getItems().clear();
            sectionCombo.getItems().addAll("A", "B", "C", "ALL");
            sectionCombo.setValue("A");
        }

        if (meetingDatePicker != null) meetingDatePicker.setValue(LocalDate.of(2026, 8, 7));
        if (fromDatePicker != null) fromDatePicker.setValue(LocalDate.of(2026, 8, 7));
        if (toDatePicker != null) toDatePicker.setValue(LocalDate.of(2026, 8, 7));

        setupTableColumns();
        studentTable.setItems(studentList);
        loadSampleData();
    }

    private void setupTableColumns() {
        if (slNoCol != null) slNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlNo()));
        if (rollNoCol != null) rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNo()));
        if (nameCol != null) nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        if (fatherNameCol != null) fatherNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFatherName()));
        if (attendanceCol != null) attendanceCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAttendance() + "%"));
        if (internalMarksCol != null) internalMarksCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInternalMarks()));
        if (remarksCol != null) remarksCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks()));
    }

    private void loadSampleData() {
        studentList.clear();
        studentList.add(new ParentsMeetingItem("1", "2025FCS001", "AFREEN FATHIMA M", "MOHAMMED A", "92.5", "85/100", "Good Performance"));
        studentList.add(new ParentsMeetingItem("2", "2025FCS002", "ANITHA A", "ARUMUGAM P", "88.0", "78/100", "Satisfactory"));
        studentList.add(new ParentsMeetingItem("3", "2025FCS003", "ARAVIND KUMAR T", "THANGAVEL M", "95.0", "91/100", "Excellent"));
        studentList.add(new ParentsMeetingItem("4", "2025FCS004", "ARO NIRANJAN S", "SUNDARAM K", "81.5", "70/100", "Needs Improvement"));
        studentList.add(new ParentsMeetingItem("5", "2025FCS005", "ATCHAYA S", "SELVAM R", "90.0", "82/100", "Good Performance"));
        studentList.add(new ParentsMeetingItem("6", "2025FCS006", "DEEPAN RAJ E", "ELANGOVAN", "94.0", "89/100", "Excellent"));
        studentList.add(new ParentsMeetingItem("7", "2025FCS007", "HARSHINI S", "SUBBIAH P", "89.5", "80/100", "Good Performance"));
    }

    @FXML private void handleView() { loadSampleData(); }
    @FXML private void handleSave() { showAlert("Saved", "Parents meeting record saved successfully!"); }
    @FXML private void handlePrint() { showAlert("Print", "Parents meeting report sent to printer."); }
    @FXML private void handleAddressPrint() { showAlert("Address Print", "Printing parent addresses..."); }
    @FXML private void handleUnivMarkLetter() { showAlert("Letter", "University Mark Letter generated for selected students."); }
    @FXML private void handleInternalMarkLetter() { showAlert("Letter", "Internal Mark Letter generated for selected students."); }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
