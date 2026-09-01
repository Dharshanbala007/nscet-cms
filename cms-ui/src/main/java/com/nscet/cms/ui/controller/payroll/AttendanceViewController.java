package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
public class AttendanceViewController implements Initializable {

    @FXML private DatePicker fromDate, toDate;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private VBox tableContainer;

    @FXML private TableView<AttendanceGridRow> table;
    @FXML private TableColumn<AttendanceGridRow, String> colStaffName;
    @FXML private TableColumn<AttendanceGridRow, String> colD26, colD27, colD28, colD29, colD30, colD31;
    @FXML private TableColumn<AttendanceGridRow, String> colD1, colD2, colD3, colD4, colD5, colD6, colD7, colD8, colD9, colD10;
    @FXML private TableColumn<AttendanceGridRow, String> colD11, colD12, colD13, colD14, colD15, colD16, colD17, colD18, colD19, colD20;
    @FXML private TableColumn<AttendanceGridRow, String> colD21, colD22, colD23, colD24, colD25;
    @FXML private TableColumn<AttendanceGridRow, String> colP, colCL, colLOP;

    @Autowired private PayrollService payrollService;

    private final ObservableList<AttendanceGridRow> gridData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.of(2026, 7, 1));
        toDate.setValue(LocalDate.of(2026, 8, 20));

        categoryCombo.getItems().setAll("Select", "Regular", "New Emp", "Contract Emp", "Reliving Emp", "Outsourcing Emp");
        categoryCombo.setValue("New Emp");

        setupTable();

        if (tableContainer != null) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
        }
    }

    private void setupTable() {
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().staffName));
        colD26.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[0]));
        colD27.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[1]));
        colD28.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[2]));
        colD29.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[3]));
        colD30.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[4]));
        colD31.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[5]));
        colD1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[6]));
        colD2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[7]));
        colD3.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[8]));
        colD4.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[9]));
        colD5.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[10]));
        colD6.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[11]));
        colD7.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[12]));
        colD8.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[13]));
        colD9.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[14]));
        colD10.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[15]));
        colD11.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[16]));
        colD12.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[17]));
        colD13.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[18]));
        colD14.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[19]));
        colD15.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[20]));
        colD16.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[21]));
        colD17.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[22]));
        colD18.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[23]));
        colD19.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[24]));
        colD20.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[25]));
        colD21.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[26]));
        colD22.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[27]));
        colD23.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[28]));
        colD24.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[29]));
        colD25.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().days[30]));

        colP.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().pCount));
        colCL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().clCount));
        colLOP.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().lopCount));

        table.setItems(gridData);
    }

    @FXML
    private void handleView() {
        gridData.clear();
        try {
            if (payrollService != null) {
                List<com.nscet.cms.db.entity.payroll.StaffSalary> salaries = payrollService.getAllStaffSalaries();
                String[] monthP = new String[31];
                java.util.Arrays.fill(monthP, "P");
                for (com.nscet.cms.db.entity.payroll.StaffSalary s : salaries) {
                    gridData.add(new AttendanceGridRow(s.getStaffName(), monthP, "31", "0", "0"));
                }
            }
        } catch (Exception e) {
            System.err.println("[AttendanceViewController] Error loading staff attendance: " + e.getMessage());
        }

        if (tableContainer != null) {
            tableContainer.setVisible(true);
            tableContainer.setManaged(true);
        }
    }

    @FXML
    private void handleCloseTable() {
        if (tableContainer != null) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
        }
        gridData.clear();
    }

    @FXML
    private void handleExportCsv() {
        if (gridData.isEmpty()) handleView();
        com.nscet.cms.ui.util.ExportUtils.exportToCsv(table, "Attendance_View_Report", table.getScene().getWindow());
    }

    @FXML
    private void handleExportPdf() {
        if (gridData.isEmpty()) handleView();
        com.nscet.cms.ui.util.ExportUtils.exportToPdf(table, "Attendance View Report", table.getScene().getWindow());
    }

    @FXML
    private void handlePrint() {
        handleExportPdf();
    }

    @FXML private void handleFind() { handleView(); }
    @FXML private void handleClose() { handleCloseTable(); }

    private static class AttendanceGridRow {
        String staffName;
        String[] days;
        String pCount, clCount, lopCount;

        AttendanceGridRow(String name, String[] d, String p, String cl, String lop) {
            this.staffName = name; this.days = d; this.pCount = p; this.clCount = cl; this.lopCount = lop;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }
}
