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
import java.util.*;

@Component
@Scope("prototype")
public class LopReportController implements Initializable {

    @FXML private DatePicker fromDate, toDate;
    @FXML private Label reportSubtitleLabel;
    @FXML private TableView<LopRow> reportTable;
    @FXML private TableColumn<LopRow, String> colSlNo, colStaffName, colDept, colDesig, colDoj, colValue;

    @Autowired private PayrollService payrollService;
    private final ObservableList<LopRow> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.of(2026, 6, 26));
        toDate.setValue(LocalDate.of(2026, 7, 25));

        setupTable();
        handleShow();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().slNo));
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().staffName));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dept));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().designation));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().doj));
        colValue.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().value));

        reportTable.setItems(dataList);
    }

    @FXML
    private void handleShow() {
        reportSubtitleLabel.setText(fromDate.getValue() + " To " + toDate.getValue());
        dataList.setAll(createLopRows());
    }

    private List<LopRow> createLopRows() {
        List<LopRow> list = new ArrayList<>();
        list.add(new LopRow("1", "GEERTHIGA G", "AI", "AP", "03/02/2025", "3.0"));
        list.add(new LopRow("2", "GAYATHRI S", "CE", "AP", "03/08/2017", "3.0"));
        list.add(new LopRow("3", "VELKUMAR K", "CSE", "AP", "07/05/2025", "2.5"));
        list.add(new LopRow("4", "ARCHANA R", "CSE", "AP", "02/03/2023", "0.5"));
        list.add(new LopRow("5", "JENIFER K", "CSE", "AP", "29/05/2026", "1.0"));
        list.add(new LopRow("6", "KODEESWARAN S", "CSE", "AP", "04/06/2026", "2.0"));
        list.add(new LopRow("7", "SNEGA PRIYANKA J S", "CSE", "AP", "08/05/2026", "30.0"));
        list.add(new LopRow("8", "KALAIVANI S", "ECE", "AP", "29/01/2024", "1.0"));
        list.add(new LopRow("9", "RAJESHSHREE S", "ECE", "AP", "21/02/2025", "30.0"));
        list.add(new LopRow("10", "CHITRA R", "ECE", "AP", "07/06/2018", "1.0"));
        list.add(new LopRow("11", "Dr. N MATHAVAN", "ECE", "AP", "01/08/2013", "2.0"));
        list.add(new LopRow("12", "PRADEEP KUMAR . R", "ECE", "AP", "06/03/2015", "1.0"));
        list.add(new LopRow("13", "GANESH K", "EEE", "AP", "08/07/2016", "2.0"));
        list.add(new LopRow("14", "JURIYA BANU H", "EEE", "AP", "19/02/2025", "2.5"));
        list.add(new LopRow("15", "DR N PANDISELVI", "EEE", "AP", "29/08/2025", "1.0"));
        list.add(new LopRow("16", "SUBATHAMANI T", "English", "AP", "04/11/2024", "1.5"));
        return list;
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print LOP Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending LOP Report to printer...");
        alert.showAndWait();
    }

    public static class LopRow {
        String slNo, staffName, dept, designation, doj, value;
        LopRow(String slNo, String staffName, String dept, String designation, String doj, String value) {
            this.slNo = slNo; this.staffName = staffName; this.dept = dept;
            this.designation = designation; this.doj = doj; this.value = value;
        }
        public String getSlNo() { return slNo; }
        public String getStaffName() { return staffName; }
        public String getDept() { return dept; }
        public String getDesignation() { return designation; }
        public String getDoj() { return doj; }
        public String getValue() { return value; }
    }
}
