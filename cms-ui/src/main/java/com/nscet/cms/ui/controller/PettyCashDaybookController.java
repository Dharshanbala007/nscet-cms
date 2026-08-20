package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.PettyCashService;
import com.nscet.cms.core.service.PettyCashSuspenseService;
import com.nscet.cms.core.service.PettyVoucherService;
import com.nscet.cms.db.entity.PettyCash;
import com.nscet.cms.db.entity.PettyCashSuspense;
import com.nscet.cms.db.entity.PettyVoucher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class PettyCashDaybookController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private TableView<DaybookEntry> daybookTable;
    @FXML private TableColumn<DaybookEntry, Integer> colSlNo;
    @FXML private TableColumn<DaybookEntry, String> colDate;
    @FXML private TableColumn<DaybookEntry, String> colType;
    @FXML private TableColumn<DaybookEntry, String> colVchNo;
    @FXML private TableColumn<DaybookEntry, String> colParticulars;
    @FXML private TableColumn<DaybookEntry, String> colDrAmount;
    @FXML private TableColumn<DaybookEntry, String> colCrAmount;
    @FXML private TextField totalDrField;
    @FXML private TextField totalCrField;

    @Autowired
    private PettyCashService pettyCashService;
    @Autowired
    private PettyCashSuspenseService suspenseService;
    @Autowired
    private PettyVoucherService voucherService;

    private final ObservableList<DaybookEntry> daybookData = FXCollections.observableArrayList();

    public static class DaybookEntry {
        private final javafx.beans.property.IntegerProperty slNo;
        private final javafx.beans.property.StringProperty date;
        private final javafx.beans.property.StringProperty type;
        private final javafx.beans.property.StringProperty vchNo;
        private final javafx.beans.property.StringProperty particulars;
        private final javafx.beans.property.StringProperty drAmount;
        private final javafx.beans.property.StringProperty crAmount;

        public DaybookEntry(int sl, String date, String type, String vchNo, String particulars, String dr, String cr) {
            this.slNo = new javafx.beans.property.SimpleIntegerProperty(sl);
            this.date = new SimpleStringProperty(date);
            this.type = new SimpleStringProperty(type);
            this.vchNo = new SimpleStringProperty(vchNo);
            this.particulars = new SimpleStringProperty(particulars);
            this.drAmount = new SimpleStringProperty(dr);
            this.crAmount = new SimpleStringProperty(cr);
        }

        public javafx.beans.property.IntegerProperty slNoProperty() { return slNo; }
        public javafx.beans.property.StringProperty dateProperty() { return date; }
        public javafx.beans.property.StringProperty typeProperty() { return type; }
        public javafx.beans.property.StringProperty vchNoProperty() { return vchNo; }
        public javafx.beans.property.StringProperty particularsProperty() { return particulars; }
        public javafx.beans.property.StringProperty drAmountProperty() { return drAmount; }
        public javafx.beans.property.StringProperty crAmountProperty() { return crAmount; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        setupTable();
        handleView();
    }

    private void setupTable() {
        colSlNo.setCellValueFactory(cellData -> cellData.getValue().slNoProperty().asObject());
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        colVchNo.setCellValueFactory(cellData -> cellData.getValue().vchNoProperty());
        colParticulars.setCellValueFactory(cellData -> cellData.getValue().particularsProperty());
        colDrAmount.setCellValueFactory(cellData -> cellData.getValue().drAmountProperty());
        colCrAmount.setCellValueFactory(cellData -> cellData.getValue().crAmountProperty());
        daybookTable.setItems(daybookData);
    }

    @FXML
    private void handleView() {
        daybookData.clear();
        LocalDate date = datePicker.getValue();
        if (date == null) return;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        int sl = 1;
        BigDecimal totalDr = BigDecimal.ZERO;
        BigDecimal totalCr = BigDecimal.ZERO;

        List<DaybookEntry> entries = new ArrayList<>();

        List<PettyCash> cashList = pettyCashService.findByDate(date);
        for (PettyCash pc : cashList) {
            String amt = pc.getAmount() != null ? String.format("%.2f", pc.getAmount()) : "0.00";
            entries.add(new DaybookEntry(sl++, date.format(fmt), "Cash", pc.getVoucherNo(),
                    pc.getStaffName() != null ? pc.getStaffName() : pc.getPurpose(),
                    amt, "0.00"));
            if (pc.getAmount() != null) totalDr = totalDr.add(pc.getAmount());
        }

        List<PettyCashSuspense> suspenseList = suspenseService.findByDate(date);
        for (PettyCashSuspense ps : suspenseList) {
            String amt = ps.getAmount() != null ? String.format("%.2f", ps.getAmount()) : "0.00";
            entries.add(new DaybookEntry(sl++, date.format(fmt), "Suspense", ps.getVoucherNo(),
                    ps.getStaffName() != null ? ps.getStaffName() : ps.getPurpose(),
                    amt, "0.00"));
            if (ps.getAmount() != null) totalDr = totalDr.add(ps.getAmount());
        }

        List<PettyVoucher> voucherList = voucherService.findByDate(date);
        for (PettyVoucher pv : voucherList) {
            String amt = pv.getTotalAmount() != null ? String.format("%.2f", pv.getTotalAmount()) : "0.00";
            entries.add(new DaybookEntry(sl++, date.format(fmt), "Voucher", pv.getVoucherNo(),
                    pv.getStaffName() != null ? pv.getStaffName() : pv.getPurpose(),
                    amt, "0.00"));
            if (pv.getTotalAmount() != null) totalDr = totalDr.add(pv.getTotalAmount());
        }

        entries.add(new DaybookEntry(sl++, date.format(fmt), "Total", "", "Total:",
                String.format("%.2f", totalDr), String.format("%.2f", totalCr)));

        BigDecimal closingBalance = totalDr.subtract(totalCr);
        entries.add(new DaybookEntry(sl++, date.format(fmt), "Cash", "", "Closing Balance",
                String.format("%.2f", closingBalance), "0.00"));

        daybookData.addAll(entries);
        totalDrField.setText(String.format("%.2f", totalDr));
        totalCrField.setText(String.format("%.2f", totalCr));
    }

    @FXML
    private void handleSave() {
        showAlert(Alert.AlertType.INFORMATION, "Daybook data saved successfully");
    }

    @FXML
    private void handlePrint() {
        showAlert(Alert.AlertType.INFORMATION, "Print functionality will be available in a future update");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Petty Cash Daybook");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
