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
public class ClMonthlyViewController implements Initializable {

    @FXML private DatePicker fromDate, toDate;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> typeCombo;

    @FXML private TableView<ClGridRow> table;
    @FXML private TableColumn<ClGridRow, String> colStaffName, colDept, colDoj, colAllowed;
    @FXML private TableColumn<ClGridRow, String> colJan, colFeb, colMar, colApr, colMay, colJun, colJul, colAug, colSep, colOct, colNov, colDec;
    @FXML private TableColumn<ClGridRow, String> colTotal, colBalance;

    @Autowired private PayrollService payrollService;
    private final ObservableList<ClGridRow> gridData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.of(2026, 1, 1));
        toDate.setValue(LocalDate.of(2026, 7, 31));

        categoryCombo.setItems(FXCollections.observableArrayList("Select", "Regular", "Contract Emp"));
        categoryCombo.setValue("Regular");

        typeCombo.setItems(FXCollections.observableArrayList("Select", "CL", "CPL", "CLB", "LOP", "OD Admis", "OD Others", "OD FDP"));
        typeCombo.setValue("CL");

        setupTable();
        handleView();
    }

    private void setupTable() {
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().staffName));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dept));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().doj));
        colAllowed.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().allowed));

        colJan.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().jan));
        colFeb.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().feb));
        colMar.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().mar));
        colApr.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().apr));
        colMay.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().may));
        colJun.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().jun));
        colJul.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().jul));
        colAug.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().aug));
        colSep.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().sep));
        colOct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().oct));
        colNov.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nov));
        colDec.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dec));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().total));
        colBalance.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().balance));

        table.setItems(gridData);
    }

    @FXML
    private void handleView() {
        gridData.setAll(createSampleGrid());
    }

    private List<ClGridRow> createSampleGrid() {
        List<ClGridRow> list = new ArrayList<>();
        list.add(new ClGridRow("Dr.C.MATHALAI SUNDARAM", "MECH", "01/08/2013", "12", "0", "0", "0", "0", "0.5", "0", "0", "0", "0", "0", "1", "0", "1.5", "10.5"));
        list.add(new ClGridRow("DR.M SATHYA", "CSE", "10/04/2023", "12", "0", "2", "1", "0", "0", "1", "1", "1", "2", "0", "1", "1.5", "11.5", "0.5"));
        list.add(new ClGridRow("NATHIRUN SABINASH", "CE", "27/05/2024", "12", "1", "0", "1", "0", "3", "1", "0.5", "1", "0", "1", "0.5", "1", "10", "2"));
        list.add(new ClGridRow("HARI PRASATH T", "CE", "20/12/2024", "12", "0", "0", "1", "1", "0.5", "1", "1", "3", "1.5", "1", "1", "1", "12", "0"));
        list.add(new ClGridRow("GAYATHRI S", "CE", "03/08/2017", "12", "0.5", "0", "2", "1", "1", "0", "0", "1", "0", "3", "1", "0", "9.5", "2.5"));
        list.add(new ClGridRow("SHANMUGAPRIYAN.R", "CE", "06/12/2017", "12", "0", "2", "0", "0", "2.5", "0.5", "1", "2", "0", "0", "0", "1.5", "9.5", "2.5"));
        list.add(new ClGridRow("SINDHU M", "CE", "17/08/2020", "12", "0", "2", "0", "1", "1", "0.5", "1", "1", "1", "0.5", "1", "0", "9", "3"));
        list.add(new ClGridRow("AADHITYA P", "CE", "02/06/2026", "1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "1"));
        list.add(new ClGridRow("NAGARATHINAM.N", "CE", "22/01/2015", "12", "0", "1", "2", "1", "1", "0", "1", "0", "0", "2", "2", "1", "11", "1"));
        list.add(new ClGridRow("KANIMOZHI M", "CE", "11/07/2022", "12", "0", "1", "0", "2", "1", "0", "0", "3", "0", "1", "3", "0", "11", "1"));
        list.add(new ClGridRow("BENITA MERLIN ISABELLA K", "CE", "09/12/2024", "12", "0", "0", "2", "1", "0", "2", "1", "0", "2", "1", "2", "0", "11", "1"));
        list.add(new ClGridRow("ARUL JEBARAJ P", "CE", "22/07/2022", "12", "1", "0", "1", "1", "1", "0", "2", "0", "0", "1.5", "1.5", "0", "9", "3"));
        list.add(new ClGridRow("Dr.S.PREMKUMAR", "CE", "15/04/2026", "3", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "3"));
        list.add(new ClGridRow("HARIKISHORE.S", "MECH", "21/07/2016", "12", "0", "1", "2", "0", "0", "2", "0.5", "0", "2", "1", "0", "2.5", "11", "1"));
        list.add(new ClGridRow("CHAKRAVARTHY SAMY DURAI J", "MECH", "22/05/2017", "12", "1", "0", "1", "0", "2", "0", "2", "1.5", "0", "1.5", "0", "1.5", "10.5", "1.5"));
        list.add(new ClGridRow("SURULIMANI. P", "MECH", "22/06/2015", "12", "0", "0", "0", "1", "0", "0", "1", "3", "0", "2", "1", "1.5", "9.5", "2.5"));
        list.add(new ClGridRow("SIVAGANESAN.V", "MECH", "02/05/2014", "12", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "1.5", "1.5", "10.5"));
        return list;
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Casual Leave Monthly View to printer...");
        alert.showAndWait();
    }

    public static class ClGridRow {
        String staffName, dept, doj, allowed;
        String jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec;
        String total, balance;

        public ClGridRow(String staffName, String dept, String doj, String allowed, String jan, String feb, String mar, String apr, String may, String jun, String jul, String aug, String sep, String oct, String nov, String dec, String total, String balance) {
            this.staffName = staffName; this.dept = dept; this.doj = doj; this.allowed = allowed;
            this.jan = jan; this.feb = feb; this.mar = mar; this.apr = apr; this.may = may;
            this.jun = jun; this.jul = jul; this.aug = aug; this.sep = sep; this.oct = oct;
            this.nov = nov; this.dec = dec; this.total = total; this.balance = balance;
        }

        public String getStaffName() { return staffName; }
        public String getDept() { return dept; }
        public String getDoj() { return doj; }
        public String getAllowed() { return allowed; }
        public String getJan() { return jan; }
        public String getFeb() { return feb; }
        public String getMar() { return mar; }
        public String getApr() { return apr; }
        public String getMay() { return may; }
        public String getJun() { return jun; }
        public String getJul() { return jul; }
        public String getAug() { return aug; }
        public String getSep() { return sep; }
        public String getOct() { return oct; }
        public String getNov() { return nov; }
        public String getDec() { return dec; }
        public String getTotal() { return total; }
        public String getBalance() { return balance; }
    }
}
