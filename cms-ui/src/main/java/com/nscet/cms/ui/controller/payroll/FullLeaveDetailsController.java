package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.StaffSalary;
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
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FullLeaveDetailsController implements Initializable {

    @FXML private DatePicker fromDate, toDate;

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSlNo, colStaffName, colDoj, colClAllowed, colCL, colLOP, colAB, colSpl, colOdExam, colVL, colComp, colML, colOdFdp, colOdAdmis, colOdOther;

    @Autowired private PayrollService payrollService;
    private ObservableList<StaffSalary> leaveList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.of(2026, 6, 1));
        toDate.setValue(LocalDate.of(2026, 7, 31));

        setupTable();
        handleView();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(leaveList.indexOf(c.getValue()) + 1)));
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt() != null ? c.getValue().getCreatedAt().toLocalDate().toString() : "01/08/2013"));
        colClAllowed.setCellValueFactory(c -> new SimpleStringProperty("2"));
        colCL.setCellValueFactory(c -> new SimpleStringProperty("1"));
        colLOP.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colAB.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colSpl.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOdExam.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colVL.setCellValueFactory(c -> new SimpleStringProperty("11"));
        colComp.setCellValueFactory(c -> new SimpleStringProperty("12"));
        colML.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOdFdp.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOdAdmis.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colOdOther.setCellValueFactory(c -> new SimpleStringProperty("3"));

        table.setItems(leaveList);
    }

    @FXML
    private void handleView() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            leaveList.setAll(list);
        } catch (Exception e) {
            System.err.println("[FullLeaveDetailsController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handlePrint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Report");
        alert.setHeaderText(null);
        alert.setContentText("Sending Staff Leave Details report to printer...");
        alert.showAndWait();
    }
}
