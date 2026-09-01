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
public class SalaryLeaveCheckController implements Initializable {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML private TextField staffNameField;
    @FXML private TextField categoryField;
    @FXML private TextField deptField;
    @FXML private DatePicker dojPicker;

    @FXML private TableView<LeaveSummaryRow> summaryTable;
    @FXML private TableColumn<LeaveSummaryRow, String> colLeaveType, colAvail, colTaken, colBal;

    @FXML private TableView<LeaveDetailRow> detailsTable;
    @FXML private TableColumn<LeaveDetailRow, String> colLeaveDate, colMorning, colAfterNoon;

    @Autowired private PayrollService payrollService;

    private final ObservableList<LeaveSummaryRow> summaryList = FXCollections.observableArrayList();
    private final ObservableList<LeaveDetailRow> detailList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(LocalDate.of(2025, 1, 1));
        toDatePicker.setValue(LocalDate.of(2026, 8, 21));

        staffNameField.setText("KUMARAVEL.P");
        categoryField.setText("NT-Tech");
        deptField.setText("CSE");
        dojPicker.setValue(LocalDate.of(2014, 6, 16));

        setupTables();
        handleView();
    }

    private void setupTables() {
        colLeaveType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type));
        colAvail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().avail));
        colTaken.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().taken));
        colBal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().bal));
        summaryTable.setItems(summaryList);

        colLeaveDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date));
        colMorning.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().morning));
        colAfterNoon.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().afternoon));
        detailsTable.setItems(detailList);
    }

    @FXML
    private void handleView() {
        summaryList.setAll(createSummaryRows());
        detailList.setAll(createDetailRows());
    }

    private List<LeaveSummaryRow> createSummaryRows() {
        List<LeaveSummaryRow> list = new ArrayList<>();
        list.add(new LeaveSummaryRow("CL", "33", "27.5", "5.5"));
        list.add(new LeaveSummaryRow("LOP", "", "2", ""));
        list.add(new LeaveSummaryRow("AB", "", "0", ""));
        list.add(new LeaveSummaryRow("SPL", "", "0", ""));
        list.add(new LeaveSummaryRow("ODExam", "", "0", ""));
        list.add(new LeaveSummaryRow("VL", "", "22", ""));
        list.add(new LeaveSummaryRow("CPL", "", "7", ""));
        list.add(new LeaveSummaryRow("ML", "", "0", ""));
        list.add(new LeaveSummaryRow("ODFDP", "", "0", ""));
        list.add(new LeaveSummaryRow("ODAdmis", "", "0", ""));
        list.add(new LeaveSummaryRow("ODOthers", "", "4", ""));
        list.add(new LeaveSummaryRow("OHP", "", "0", ""));
        return list;
    }

    private List<LeaveDetailRow> createDetailRows() {
        List<LeaveDetailRow> list = new ArrayList<>();
        list.add(new LeaveDetailRow("05/01/2021", "CL", "P"));
        list.add(new LeaveDetailRow("10/02/2021", "CL", "CL"));
        list.add(new LeaveDetailRow("13/03/2021", "CL", "CL"));
        list.add(new LeaveDetailRow("26/04/2021", "CL", "CL"));
        list.add(new LeaveDetailRow("14/07/2021", "CL", "CL"));
        list.add(new LeaveDetailRow("13/08/2021", "CL", "CL"));
        list.add(new LeaveDetailRow("07/09/2021", "CL", "P"));
        list.add(new LeaveDetailRow("13/09/2021", "CL", "P"));
        list.add(new LeaveDetailRow("05/10/2021", "CL", "P"));
        list.add(new LeaveDetailRow("15/11/2021", "CL", "CL"));
        list.add(new LeaveDetailRow("08/12/2021", "CL", "CL"));
        list.add(new LeaveDetailRow("29/12/2021", "CL", "P"));
        return list;
    }

    public static class LeaveSummaryRow {
        String type, avail, taken, bal;
        LeaveSummaryRow(String type, String avail, String taken, String bal) {
            this.type = type; this.avail = avail; this.taken = taken; this.bal = bal;
        }
    }

    public static class LeaveDetailRow {
        String date, morning, afternoon;
        LeaveDetailRow(String date, String morning, String afternoon) {
            this.date = date; this.morning = morning; this.afternoon = afternoon;
        }
    }
}
