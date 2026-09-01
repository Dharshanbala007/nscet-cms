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
public class DeductionSalaryDetailsController implements Initializable {

    @FXML private DatePicker fromDate, toDate;
    @FXML private ComboBox<String> yearCombo;
    @FXML private VBox tableContainer;

    @FXML private TableView<DeductionSalaryRow> table;
    @FXML private TableColumn<DeductionSalaryRow, String> colSlNo, colStaffName, colDesig, colDoj;
    @FXML private TableColumn<DeductionSalaryRow, String> colJun, colJul, colAug, colSep, colOct, colNov, colDec, colJan, colTotal;

    @Autowired private PayrollService payrollService;

    private final ObservableList<DeductionSalaryRow> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.of(2022, 1, 1));
        toDate.setValue(LocalDate.of(2022, 6, 1));

        yearCombo.getItems().setAll("Select", "2020-21", "2021-22", "2022-23", "2023-24", "2024-25", "2025-26");
        yearCombo.setValue("2021-22");

        setupTable();

        if (tableContainer != null) {
            tableContainer.setVisible(false);
            tableContainer.setManaged(false);
        }
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().slNo));
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().staffName));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().designation));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().doj));

        colJun.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().jun));
        colJul.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().jul));
        colAug.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().aug));
        colSep.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().sep));
        colOct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().oct));
        colNov.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nov));
        colDec.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dec));
        colJan.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().jan));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().total));

        table.setItems(dataList);
    }

    @FXML
    private void handleView() {
        dataList.clear();
        dataList.addAll(createSampleRows());

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
        dataList.clear();
    }

    private List<DeductionSalaryRow> createSampleRows() {
        List<DeductionSalaryRow> list = new ArrayList<>();
        try {
            if (payrollService != null) {
                List<com.nscet.cms.db.entity.payroll.StaffSalary> salaries = payrollService.getAllStaffSalaries();
                int idx = 1;
                for (com.nscet.cms.db.entity.payroll.StaffSalary s : salaries) {
                    list.add(new DeductionSalaryRow(
                        String.valueOf(idx++),
                        s.getStaffName(),
                        s.getDesignation() != null ? s.getDesignation() : "Assistant Professor",
                        "01/06/2024",
                        "0", "0", "0", "0", "0", "0", "0", "0", "0"
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("[DeductionSalaryDetailsController] Error loading DB salaries: " + e.getMessage());
        }
        return list;
    }

    @FXML
    private void handleExportCsv() {
        if (dataList.isEmpty()) handleView();
        com.nscet.cms.ui.util.ExportUtils.exportToCsv(table, "Deduction_Salary_Details", table.getScene().getWindow());
    }

    @FXML
    private void handleExportPdf() {
        if (dataList.isEmpty()) handleView();
        com.nscet.cms.ui.util.ExportUtils.exportToPdf(table, "Deduction Salary Details Report", table.getScene().getWindow());
    }

    @FXML
    private void handlePrint() {
        handleExportPdf();
    }

    @FXML private void handleFind() { handleView(); }
    @FXML private void handleClose() { dataList.clear(); }

    private static class DeductionSalaryRow {
        String slNo, staffName, designation, doj;
        String jun, jul, aug, sep, oct, nov, dec, jan, total;
        DeductionSalaryRow(String slNo, String staffName, String designation, String doj, String jun, String jul, String aug, String sep, String oct, String nov, String dec, String jan, String total) {
            this.slNo = slNo; this.staffName = staffName; this.designation = designation; this.doj = doj;
            this.jun = jun; this.jul = jul; this.aug = aug; this.sep = sep; this.oct = oct;
            this.nov = nov; this.dec = dec; this.jan = jan; this.total = total;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }
}
