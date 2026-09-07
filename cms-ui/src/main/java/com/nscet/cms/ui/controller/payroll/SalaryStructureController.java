package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.payroll.StaffSalary;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class SalaryStructureController implements Initializable {

    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colSNo, colStaffName, colDesig, colDoj;
    @FXML private TableColumn<StaffSalary, String> colGrossPay, colBasicPay, colHra, colWashing, colTravelling, colSpl, colDiff;
    @FXML private TableColumn<StaffSalary, String> colLopDays, colLopAmt;

    @Autowired private PayrollService payrollService;
    private final ObservableList<StaffSalary> structureList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        handleView();
    }

    private void setupTable() {
        colSNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(structureList.indexOf(c.getValue()) + 1)));
        colStaffName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation() != null ? c.getValue().getDesignation() : ""));
        colDoj.setCellValueFactory(c -> new SimpleStringProperty("01/08/2013"));

        colLopDays.setCellValueFactory(c -> new SimpleStringProperty("0"));
        colLopDays.setEditable(true);
        colLopDays.setCellFactory(TextFieldTableCell.forTableColumn());
        colLopDays.setOnEditCommit(e -> {
            StaffSalary staff = e.getTableView().getItems().get(e.getTablePosition().getRow());
            try {
                int lopDays = Integer.parseInt(e.getNewValue().trim());
                staff.setLopDays(lopDays);
                recalculateLopAmount(staff);
                table.refresh();
            } catch (NumberFormatException ex) {
                staff.setLopDays(0);
                recalculateLopAmount(staff);
                table.refresh();
            }
        });
        setRightAligned(colLopDays);

        colGrossPay.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrossSalary() != null ? c.getValue().getGrossSalary().toPlainString() : "0.00"));
        colGrossPay.setEditable(true);
        colGrossPay.setCellFactory(TextFieldTableCell.forTableColumn());
        colGrossPay.setOnEditCommit(e -> {
            StaffSalary staff = e.getTableView().getItems().get(e.getTablePosition().getRow());
            try {
                BigDecimal gross = new BigDecimal(e.getNewValue().trim());
                calculateAndSet(staff, gross);
                table.refresh();
            } catch (NumberFormatException ex) {
                System.err.println("[SalaryStructureController] Invalid Gross Pay: " + e.getNewValue());
            }
        });
        setRightAligned(colGrossPay);

        colBasicPay.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBasicPay() != null ? c.getValue().getBasicPay().toPlainString() : "0.00"));
        setRightAligned(colBasicPay);

        colHra.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHra() != null ? c.getValue().getHra().toPlainString() : "0.00"));
        setRightAligned(colHra);

        colWashing.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getWashingAllowance() != null ? c.getValue().getWashingAllowance().toPlainString() : "0.00"));
        setRightAligned(colWashing);

        colTravelling.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getConveyance() != null ? c.getValue().getConveyance().toPlainString() : "0.00"));
        setRightAligned(colTravelling);

        colSpl.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSpecialAllowance() != null ? c.getValue().getSpecialAllowance().toPlainString() : "0.00"));
        setRightAligned(colSpl);

        colLopAmt.setCellValueFactory(c -> {
            StaffSalary s = c.getValue();
            BigDecimal lopAmt = calculateLopAmount(s);
            return new SimpleStringProperty(lopAmt.toPlainString());
        });
        setRightAligned(colLopAmt);

        colDiff.setCellValueFactory(c -> new SimpleStringProperty("0"));
        setRightAligned(colDiff);

        table.setEditable(true);
        table.setItems(structureList);
    }

    private void setRightAligned(TableColumn<StaffSalary, String> column) {
        column.setCellFactory(tc -> {
            TableCell<StaffSalary, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setAlignment(Pos.CENTER_RIGHT);
                }
            };
            return cell;
        });
    }

    private BigDecimal calculateLopAmount(StaffSalary staff) {
        if (staff.getGrossSalary() == null || staff.getGrossSalary().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        Integer lopDays = staff.getLopDays();
        if (lopDays == null || lopDays == 0) {
            return BigDecimal.ZERO;
        }
        int totalDays = YearMonth.now().lengthOfMonth();
        return staff.getGrossSalary()
                .divide(new BigDecimal(totalDays), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(lopDays))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void recalculateLopAmount(StaffSalary staff) {
        staff.setLopAmount(calculateLopAmount(staff));
    }

    private void calculateAndSet(StaffSalary staff, BigDecimal gross) {
        BigDecimal basicPay = gross.multiply(new BigDecimal("0.60")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal splAllowance = basicPay.subtract(new BigDecimal("15000")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal hra = splAllowance.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal washing = hra.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal travelling = hra.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);

        staff.setGrossSalary(gross);
        staff.setBasicPay(basicPay);
        staff.setSpecialAllowance(splAllowance);
        staff.setHra(hra);
        staff.setWashingAllowance(washing);
        staff.setConveyance(travelling);
        recalculateLopAmount(staff);
    }

    @FXML
    private void handleView() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            for (StaffSalary s : list) {
                if (s.getGrossSalary() != null && s.getGrossSalary().compareTo(BigDecimal.ZERO) > 0) {
                    calculateAndSet(s, s.getGrossSalary());
                }
            }
            structureList.setAll(list);
        } catch (Exception e) {
            System.err.println("[SalaryStructureController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            for (StaffSalary staff : structureList) {
                recalculateLopAmount(staff);
                payrollService.saveStaffSalary(staff);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Salary Structure");
            alert.setHeaderText(null);
            alert.setContentText("Salary Structure details saved successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
