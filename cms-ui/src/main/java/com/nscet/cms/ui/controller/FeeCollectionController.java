package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.FeeCollectionService;
import com.nscet.cms.core.service.FeesService;
import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.entity.FeeReceiptItem;
import com.nscet.cms.db.entity.FeesMaster;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.reports.ReportManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Component
@Scope("prototype")
public class FeeCollectionController implements Initializable {

    @FXML private RadioButton currentRadio, passedOutRadio, staffRadio, miscRadio;
    @FXML private TextField studentSearchField, amountField;
    @FXML private Label studentNameLabel, rollLabel, deptLabel, semLabel, totalLabel;
    @FXML private Label runningTotalLabel;
    @FXML private ComboBox<String> baseAccountCombo, payTypeCombo;
    @FXML private TableView<FeeReceiptItem> itemsTable;
    @FXML private TableColumn<FeeReceiptItem, String> feeNameCol, feeAmtCol, allocCol;
    @FXML private ComboBox<String> feeNameCombo;
    @FXML private TextField manualAmtField;
    @FXML private VBox pendingPane, itemsPane;
    @FXML private Button saveBtn, cancelBtn;

    @Autowired private StudentService studentService;
    @Autowired private FeeCollectionService receiptService;
    @Autowired private FeesService feesService;

    private StudentMaster selectedStudent;
    private ObservableList<FeeReceiptItem> items = FXCollections.observableArrayList();
    private BigDecimal runningTotal = BigDecimal.ZERO;
    private List<FeesMaster> allFees = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup studentTypeGroup = new ToggleGroup();
        currentRadio.setToggleGroup(studentTypeGroup);
        passedOutRadio.setToggleGroup(studentTypeGroup);
        staffRadio.setToggleGroup(studentTypeGroup);
        miscRadio.setToggleGroup(studentTypeGroup);
        currentRadio.setSelected(true);

        baseAccountCombo.getItems().add("Select");
        baseAccountCombo.getItems().addAll("Cash", "Federal Bank", "TMB Exam Fee");
        baseAccountCombo.getSelectionModel().selectFirst();
        payTypeCombo.getItems().add("Select");
        payTypeCombo.getItems().addAll("Pay", "OLP", "DD/Cheque", "Adjust Bill");
        payTypeCombo.getSelectionModel().selectFirst();

        loadFeesFromDB();

        feeNameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getAllocatedTo() != null ? c.getValue().getAllocatedTo() : ""));
        feeAmtCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            "₹" + c.getValue().getAmount()));
        allocCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getAllocatedTo() != null ? c.getValue().getAllocatedTo() : ""));

        itemsTable.setItems(items);
        itemsPane.setVisible(true); 
        itemsPane.setManaged(true);

        try {
            org.springframework.data.domain.Page<StudentMaster> page = studentService.getAll("", 0, 1, "id", "asc");
            if (page.hasContent()) {
                selectedStudent = page.getContent().get(0);
                studentNameLabel.setText(selectedStudent.getName());
                rollLabel.setText(selectedStudent.getRollNumber() != null ? selectedStudent.getRollNumber() : "--");
                deptLabel.setText(selectedStudent.getCommunity() != null ? selectedStudent.getCommunity() : "CSE");
                semLabel.setText("1");
                studentSearchField.setText(selectedStudent.getRollNumber());
            }
        } catch (Exception e) {
            System.err.println("[FeeCollectionController] Pre-load student info: " + e.getMessage());
        }
    }

    private void loadFeesFromDB() {
        try {
            allFees = feesService.getAllActiveList();
            feeNameCombo.getItems().clear();
            feeNameCombo.getItems().add("Select");
            for (FeesMaster fee : allFees) {
                String label = fee.getName() + (Boolean.TRUE.equals(fee.getSemesterFee()) ? " [Sem]" : " [Other]");
                feeNameCombo.getItems().add(label);
            }
            feeNameCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            feeNameCombo.getItems().clear();
            feeNameCombo.getItems().add("Select");
            feeNameCombo.getItems().addAll("Tuition Fee", "Admission Fees", "Bus Fee", "Exam Fee", "Library Fee", "Lab Fee", "Hostel Fee", "Uniform Fee", "Placement Fee", "Sports Fee");
            feeNameCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleStudentSearch() {
        String query = studentSearchField.getText().trim();
        if (query.isEmpty()) return;

        try {
            selectedStudent = studentService.getByRollNumber(query);
            studentNameLabel.setText(selectedStudent.getName());
            rollLabel.setText(selectedStudent.getRollNumber());
            deptLabel.setText(selectedStudent.getAdmissionType() != null ? selectedStudent.getAdmissionType() : "--");
            semLabel.setText("--");
            itemsPane.setVisible(true); itemsPane.setManaged(true);
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Student not found: " + query).showAndWait();
        }
    }

    @FXML
    private void handleAutoAllocate() {
        if (selectedStudent == null) {
            new Alert(Alert.AlertType.WARNING, "Please search and select a student first").showAndWait();
            return;
        }

        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) return;

        try {
            BigDecimal totalAmount = new BigDecimal(amountText);
            runningTotal = totalAmount;
            runningTotalLabel.setText(String.format("₹%.2f", runningTotal));

            items.clear();

            List<FeesMaster> semesterFees = feesService.getSemesterFees();
            List<FeesMaster> otherFees = feesService.getOtherFees();

            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                FeeReceiptItem tuition = new FeeReceiptItem();
                tuition.setFeesName(findFeeByName("Tuition Fee"));
                tuition.setAllocatedTo("Tuition Fee");
                tuition.setAmount(totalAmount.min(new BigDecimal("20000")));
                items.add(tuition);

                BigDecimal remaining = totalAmount.subtract(tuition.getAmount());
                if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                    FeeReceiptItem other = new FeeReceiptItem();
                    other.setFeesName(findFeeByName("Other fee"));
                    other.setAllocatedTo("Other Fee");
                    other.setAmount(remaining.min(new BigDecimal("15000")));
                    items.add(other);
                    remaining = remaining.subtract(other.getAmount());
                }

                if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                    FeeReceiptItem bus = new FeeReceiptItem();
                    bus.setFeesName(findFeeByName("Bus Fees"));
                    bus.setAllocatedTo("Bus Fee");
                    bus.setAmount(remaining);
                    items.add(bus);
                }
            }

            totalLabel.setText(String.format("₹%.2f", runningTotal));
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid amount").showAndWait();
        }
    }

    private FeesMaster findFeeByName(String name) {
        return allFees.stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @FXML
    private void handleManualAdd() {
        if (feeNameCombo.getValue() == null || "Select".equals(feeNameCombo.getValue()) || manualAmtField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select fee name and enter amount").showAndWait();
            return;
        }

        String selectedLabel = feeNameCombo.getValue();
        String feeName = selectedLabel.replace(" [Sem]", "").replace(" [Other]", "").trim();

        FeeReceiptItem item = new FeeReceiptItem();
        item.setFeesName(findFeeByName(feeName));
        item.setAllocatedTo(feeName);
        item.setAmount(new BigDecimal(manualAmtField.getText().trim()));
        items.add(item);

        runningTotal = runningTotal.add(item.getAmount());
        runningTotalLabel.setText(String.format("₹%.2f", runningTotal));
        totalLabel.setText(String.format("₹%.2f", runningTotal));
        manualAmtField.clear();
    }

    @FXML
    private void handleRemoveItem() {
        FeeReceiptItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            runningTotal = runningTotal.subtract(selected.getAmount());
            items.remove(selected);
            runningTotalLabel.setText(String.format("₹%.2f", runningTotal));
            totalLabel.setText(String.format("₹%.2f", runningTotal));
        }
    }

    @FXML
    private void handleSave() {
        if (selectedStudent == null) {
            new Alert(Alert.AlertType.WARNING, "Select a student first").showAndWait();
            return;
        }
        if (items.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Add at least one fee item").showAndWait();
            return;
        }

        try {
            FeeReceipt receipt = new FeeReceipt();
            receipt.setStudent(selectedStudent);
            receipt.setStudentType(currentRadio.isSelected() ? "Current" : passedOutRadio.isSelected() ? "PassedOut" : staffRadio.isSelected() ? "Staff" : "Misc");
            receipt.setBaseAccount(baseAccountCombo.getValue() != null && !"Select".equals(baseAccountCombo.getValue()) ? baseAccountCombo.getValue() : "Cash");
            receipt.setPaymentMode(payTypeCombo.getValue() != null && !"Select".equals(payTypeCombo.getValue()) ? payTypeCombo.getValue() : "Pay");
            receipt.setTotalAmount(runningTotal);

            for (FeeReceiptItem item : items) {
                receipt.addItem(item);
            }

            FeeReceipt saved = receiptService.createReceipt(receipt);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Receipt Created");
            success.setHeaderText("Receipt saved successfully");
            success.setContentText("Receipt Number: " + saved.getReceiptNumber() + "\nAmount: ₹" + saved.getTotalAmount());
            success.showAndWait();

            handleClear();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleClear() {
        selectedStudent = null; items.clear(); runningTotal = BigDecimal.ZERO;
        studentSearchField.clear(); amountField.clear(); manualAmtField.clear();
        studentNameLabel.setText("--"); rollLabel.setText("--"); deptLabel.setText("--"); semLabel.setText("--");
        totalLabel.setText("₹0.00"); runningTotalLabel.setText("₹0.00");
        baseAccountCombo.getSelectionModel().selectFirst();
        payTypeCombo.getSelectionModel().selectFirst();
        feeNameCombo.getSelectionModel().selectFirst();
        itemsPane.setVisible(false); itemsPane.setManaged(false);
    }

    @FXML
    private void handlePrint() {
        if (selectedStudent == null) {
            new Alert(Alert.AlertType.WARNING, "Select a student first").showAndWait();
            return;
        }
        if (items.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Add fee items before printing").showAndWait();
            return;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("COLLEGE_NAME", "Nadar Saraswathi College of Engineering and Technology");
            params.put("COLLEGE_LOCATION", "Theni");
            params.put("ACADEMIC_YEAR", "2025-2026");

            List<Map<String, Object>> reportData = new ArrayList<>();
            for (FeeReceiptItem item : items) {
                Map<String, Object> row = new HashMap<>();
                row.put("receiptNumber", "PREVIEW");
                row.put("receiptDate", LocalDate.now());
                row.put("studentName", selectedStudent.getName());
                row.put("rollNumber", selectedStudent.getRollNumber());
                row.put("department", selectedStudent.getAdmissionType() != null ? selectedStudent.getAdmissionType() : "");
                row.put("semester", 1);
                row.put("paymentMode", payTypeCombo.getValue() != null ? payTypeCombo.getValue() : "Cash");
                row.put("baseAccount", baseAccountCombo.getValue() != null ? baseAccountCombo.getValue() : "Cash");
                row.put("totalAmount", runningTotal);
                row.put("feeName", item.getAllocatedTo());
                row.put("feeAmount", item.getAmount());
                reportData.add(row);
            }

            ReportManager.printReport("FeeReceipt", reportData, params);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error generating receipt: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleExportPdf() {
        if (selectedStudent == null) {
            new Alert(Alert.AlertType.WARNING, "Select a student first").showAndWait();
            return;
        }
        if (items.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Add fee items before exporting").showAndWait();
            return;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("COLLEGE_NAME", "Nadar Saraswathi College of Engineering and Technology");
            params.put("COLLEGE_LOCATION", "Theni");
            params.put("ACADEMIC_YEAR", "2025-2026");

            List<Map<String, Object>> reportData = new ArrayList<>();
            for (FeeReceiptItem item : items) {
                Map<String, Object> row = new HashMap<>();
                row.put("receiptNumber", "PREVIEW");
                row.put("receiptDate", LocalDate.now());
                row.put("studentName", selectedStudent.getName());
                row.put("rollNumber", selectedStudent.getRollNumber());
                row.put("department", selectedStudent.getAdmissionType() != null ? selectedStudent.getAdmissionType() : "");
                row.put("semester", 1);
                row.put("paymentMode", payTypeCombo.getValue() != null ? payTypeCombo.getValue() : "Cash");
                row.put("baseAccount", baseAccountCombo.getValue() != null ? baseAccountCombo.getValue() : "Cash");
                row.put("totalAmount", runningTotal);
                row.put("feeName", item.getAllocatedTo());
                row.put("feeAmount", item.getAmount());
                reportData.add(row);
            }

            byte[] pdfBytes = ReportManager.exportToPdf("FeeReceipt", reportData, params);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Fee Receipt PDF");
            fileChooser.setInitialFileName("FeeReceipt_" + selectedStudent.getRollNumber() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(pdfBytes);
                }
                new Alert(Alert.AlertType.INFORMATION, "PDF exported successfully:\n" + file.getAbsolutePath()).showAndWait();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error exporting PDF: " + e.getMessage()).showAndWait();
        }
    }
}
