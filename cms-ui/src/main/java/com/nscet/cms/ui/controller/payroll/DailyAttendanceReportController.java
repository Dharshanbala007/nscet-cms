package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class DailyAttendanceReportController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> sessionCombo;
    @FXML private Label lblSummaryTitle;

    @FXML private Label lblTeachingTotal, lblTeachingPresent, lblTeachingCl, lblTeachingLop, lblTeachingAb, lblTeachingSpl, lblTeachingOd, lblTeachingOther, lblTeachingSum;
    @FXML private Label lblNonTeachingTotal, lblNonTeachingPresent, lblNonTeachingCl, lblNonTeachingLop, lblNonTeachingAb, lblNonTeachingSpl, lblNonTeachingOd, lblNonTeachingOther, lblNonTeachingSum;

    @FXML private TableView<AbsenteeRow> teachingTable;
    @FXML private TableColumn<AbsenteeRow, String> colTchSlNo, colTchName, colTchDept;

    @FXML private TableView<AbsenteeRow> nonTeachingTable;
    @FXML private TableColumn<AbsenteeRow, String> colNonTchSlNo, colNonTchName, colNonTchDept;

    @Autowired private PayrollService payrollService;

    private final ObservableList<AbsenteeRow> teachingList = FXCollections.observableArrayList();
    private final ObservableList<AbsenteeRow> nonTeachingList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.of(2026, 7, 22));

        sessionCombo.getItems().setAll("Fore Noon", "After Noon");
        sessionCombo.setValue("Fore Noon");

        setupTables();
        handleView();
    }

    private void setupTables() {
        colTchSlNo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlNo()));
        colTchName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colTchDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));
        teachingTable.setItems(teachingList);

        colNonTchSlNo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlNo()));
        colNonTchName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colNonTchDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDept()));
        nonTeachingTable.setItems(nonTeachingList);
    }

    @FXML
    private void handleView() {
        if (datePicker.getValue() != null && sessionCombo.getValue() != null) {
            lblSummaryTitle.setText(datePicker.getValue().toString() + " — " + sessionCombo.getValue());
        }

        teachingList.setAll(createTeachingAbsentees());
        nonTeachingList.setAll(createNonTeachingAbsentees());
    }

    private List<AbsenteeRow> createTeachingAbsentees() {
        List<AbsenteeRow> list = new ArrayList<>();
        list.add(new AbsenteeRow("1", "VIGNESH.L.S (CL)", "AI"));
        list.add(new AbsenteeRow("2", "PRATHAP.S (CL)", "ECE"));
        list.add(new AbsenteeRow("3", "DR.T.VENISH KUMAR (CL)", "ECE"));
        list.add(new AbsenteeRow("4", "RAJESHSHREE S (LOP)", "ECE"));
        list.add(new AbsenteeRow("5", "SNEGA PRIYANKA.J.S (LOP)", "CSE"));
        return list;
    }

    private List<AbsenteeRow> createNonTeachingAbsentees() {
        List<AbsenteeRow> list = new ArrayList<>();
        list.add(new AbsenteeRow("1", "MUTHURAJA.P (CL)", "Admin"));
        list.add(new AbsenteeRow("2", "AMBARISH S (CL)", "MECH"));
        list.add(new AbsenteeRow("3", "GOPINATHAN P (LOP)", "ECE"));
        list.add(new AbsenteeRow("4", "DEEPIKA P (LOP)", "ECE"));
        list.add(new AbsenteeRow("5", "LAWRENCE. S (LOP)", "CSE"));
        return list;
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Daily Attendance Summary to printer...");
        alert.showAndWait();
    }

    public static class AbsenteeRow {
        private final String slNo, staffName, dept;
        public AbsenteeRow(String slNo, String staffName, String dept) {
            this.slNo = slNo; this.staffName = staffName; this.dept = dept;
        }
        public String getSlNo() { return slNo; }
        public String getStaffName() { return staffName; }
        public String getDept() { return dept; }
    }
}
