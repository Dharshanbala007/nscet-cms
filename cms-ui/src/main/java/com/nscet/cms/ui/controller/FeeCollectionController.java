package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.FeeCollectionService;
import com.nscet.cms.core.service.FeesService;
import com.nscet.cms.core.service.StudentDetailsService;
import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.*;
import com.nscet.cms.db.repository.FeesDetailsRepository;
import com.nscet.cms.reports.ReportManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Scope("prototype")
public class FeeCollectionController implements Initializable {

    @FXML private RadioButton currentRadio, passedOutRadio, staffRadio, miscRadio;
    @FXML private TextField studentSearchField, amountField;
    @FXML private TextField studentNameLabel, rollLabel, deptLabel, semLabel;
    @FXML private TextField totalLabel;
    @FXML private Label runningTotalLabel;
    @FXML private ComboBox<String> baseAccountCombo, payTypeCombo;
    @FXML private TableView<FeeReceiptItem> itemsTable;
    @FXML private TableColumn<FeeReceiptItem, String> feeNameCol, feeAmtCol, allocCol;
    @FXML private ComboBox<String> feeNameCombo;
    @FXML private TextField manualAmtField;
    @FXML private VBox pendingPane, itemsPane;
    @FXML private Button saveBtn, cancelBtn;

    @FXML private DatePicker receiptDatePicker;
    @FXML private TextField pendingFeesField;
    @FXML private ComboBox<String> receiptTypeCombo;
    @FXML private TextField receiptNoField;
    @FXML private TextField regNoField, casteField, busRouteField;
    @FXML private ComboBox<String> periodCombo;
    @FXML private TextField degreeField, adTypeField, quotaField, busStopField;
    @FXML private TableView<FeesDetails> pendingFeesTable;
    @FXML private TableColumn<FeesDetails, String> pendingSemCol, pendingPartCol, pendingAmtCol, pendingGroupCol;
    @FXML private TextField remarksField;
    @FXML private ToggleButton sem1Tab, sem2Tab, sem3Tab;
    @FXML private Button addBtn, modifyBtn, deleteBtn, closeBtn;

    @Autowired private StudentService studentService;
    @Autowired private StudentDetailsService studentDetailsService;
    @Autowired private FeeCollectionService receiptService;
    @Autowired private FeesService feesService;
    @Autowired private FeesDetailsRepository feesDetailsRepository;

    private StudentMaster selectedStudent;
    private StudentDetails selectedStudentDetails;
    private ObservableList<FeeReceiptItem> items = FXCollections.observableArrayList();
    private ObservableList<FeesDetails> pendingFeesList = FXCollections.observableArrayList();
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

        ToggleGroup semesterGroup = new ToggleGroup();
        sem1Tab.setToggleGroup(semesterGroup);
        sem2Tab.setToggleGroup(semesterGroup);
        sem3Tab.setToggleGroup(semesterGroup);

        receiptDatePicker.setValue(LocalDate.now());

        receiptTypeCombo.getItems().addAll("Select", "Regular", "Counter", "Online", "Misc");
        receiptTypeCombo.getSelectionModel().selectFirst();

        baseAccountCombo.getItems().addAll("Select", "Federal Bank", "TMB Exam Fee", "TMB College", "Cash");
        baseAccountCombo.getSelectionModel().selectFirst();

        payTypeCombo.getItems().addAll("Select", "Pay", "Credit Bill", "OLP", "DD/Cheque");
        payTypeCombo.getSelectionModel().selectFirst();

        int currentYear = LocalDate.now().getYear();
        periodCombo.getItems().addAll(
            (currentYear) + "-" + (currentYear + 1),
            (currentYear - 1) + "-" + (currentYear),
            (currentYear - 2) + "-" + (currentYear - 1)
        );
        periodCombo.getSelectionModel().selectFirst();

        generateReceiptNumber();
        loadFeesFromDB();

        feeNameCol.setCellValueFactory(c -> {
            FeesMaster fm = c.getValue().getFeesName();
            return new SimpleStringProperty(fm != null ? fm.getName() : c.getValue().getAllocatedTo() != null ? c.getValue().getAllocatedTo() : "");
        });
        feeAmtCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getAmount() != null ? c.getValue().getAmount().toString() : ""));
        allocCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getAllocatedTo() != null ? c.getValue().getAllocatedTo() : ""));
        itemsTable.setItems(items);

        pendingSemCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getSemester() != null ? String.valueOf(c.getValue().getSemester()) : ""));
        pendingPartCol.setCellValueFactory(c -> {
            FeesMaster fm = c.getValue().getFeesName();
            return new SimpleStringProperty(fm != null ? fm.getName() : "");
        });
        pendingAmtCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getAmount() != null ? c.getValue().getAmount().toString() : ""));
        pendingGroupCol.setCellValueFactory(c -> {
            FeesMaster fm = c.getValue().getFeesName();
            return new SimpleStringProperty(fm != null ? fm.getFeesGroup() : "");
        });
        pendingFeesTable.setItems(pendingFeesList);

        pendingFeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                FeesMaster fm = newVal.getFeesName();
                if (fm != null) {
                    feeNameCombo.getSelectionModel().select(fm.getName());
                }
                manualAmtField.setText(newVal.getAmount() != null ? newVal.getAmount().toString() : "");
            }
        });
    }

    private void generateReceiptNumber() {
        try {
            String prefix = "MIS";
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yy-MM-dd"));
            long count = receiptService.getReceiptCountByDate(LocalDate.now());
            receiptNoField.setText(String.format("%s-%s-%04d", prefix, dateStr, count + 1));
        } catch (Exception e) {
            receiptNoField.setText("MIS-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yy-MM-dd")) + "-0001");
        }
    }

    private void loadFeesFromDB() {
        try {
            allFees = feesService.getAllActiveList();
            feeNameCombo.getItems().clear();
            feeNameCombo.getItems().add("Select");
            for (FeesMaster fee : allFees) {
                feeNameCombo.getItems().add(fee.getName());
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
            regNoField.setText(selectedStudent.getRegistrationNo() != null ? selectedStudent.getRegistrationNo() : "");
            casteField.setText(selectedStudent.getCaste() != null ? selectedStudent.getCaste() : "");
            busRouteField.setText(selectedStudent.getTransportType() != null ? selectedStudent.getTransportType() : "");
            adTypeField.setText(selectedStudent.getAdmissionType() != null ? selectedStudent.getAdmissionType() : "");
            busStopField.setText(selectedStudent.getBusStop() != null ? selectedStudent.getBusStop() : "");

            String academicYear = periodCombo.getValue();
            Page<StudentDetails> detailsPage = studentDetailsService.getAll(query, 0, 10, "semester", "desc");
            if (!detailsPage.isEmpty()) {
                selectedStudentDetails = detailsPage.getContent().get(0);
                degreeField.setText(selectedStudentDetails.getDegree() != null ? selectedStudentDetails.getDegree() : "");
                deptLabel.setText(selectedStudentDetails.getDepartment() != null ? selectedStudentDetails.getDepartment().getName() : "");
                semLabel.setText(selectedStudentDetails.getSemester() != null ? String.valueOf(selectedStudentDetails.getSemester()) : "");
                quotaField.setText(selectedStudentDetails.getQuota() != null ? selectedStudentDetails.getQuota().getName() : "");
                if (selectedStudentDetails.getBusStop() != null && !selectedStudentDetails.getBusStop().isEmpty()) {
                    busStopField.setText(selectedStudentDetails.getBusStop());
                }
                loadPendingFees();
            } else {
                degreeField.clear();
                deptLabel.clear();
                semLabel.clear();
                quotaField.clear();
                pendingFeesList.clear();
                pendingFeesTable.refresh();
            }
            updatePendingFeesTotal();
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Student not found: " + query).showAndWait();
        }
    }

    private void loadPendingFees() {
        pendingFeesList.clear();
        if (selectedStudentDetails == null) return;

        try {
            Long deptId = selectedStudentDetails.getDepartment() != null ? selectedStudentDetails.getDepartment().getId() : null;
            Integer semester = selectedStudentDetails.getSemester();
            Long quotaId = selectedStudentDetails.getQuota() != null ? selectedStudentDetails.getQuota().getId() : null;
            String admissionType = selectedStudent != null ? selectedStudent.getAdmissionType() : null;

            if (deptId != null && semester != null && quotaId != null && admissionType != null) {
                List<FeesDetails> fees = feesDetailsRepository.findFeeStructure(deptId, semester, quotaId, admissionType);

                Set<Long> paidFeeIds = new HashSet<>();
                if (selectedStudent != null) {
                    List<FeeReceipt> receipts = receiptService.getReceiptsByStudent(selectedStudent.getId());
                    for (FeeReceipt r : receipts) {
                        if (r.getItems() != null) {
                            for (FeeReceiptItem item : r.getItems()) {
                                if (item.getFeesName() != null) {
                                    paidFeeIds.add(item.getFeesName().getId());
                                }
                            }
                        }
                    }
                }

                for (FeesDetails fd : fees) {
                    pendingFeesList.add(fd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePendingFeesTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (FeesDetails fd : pendingFeesList) {
            if (fd.getAmount() != null) {
                total = total.add(fd.getAmount());
            }
        }
        pendingFeesField.setText(total.toString());
    }

    @FXML
    private void handleAutoAllocate() {
        if (selectedStudent == null) {
            new Alert(Alert.AlertType.WARNING, "Please search and select a student first").showAndWait();
            return;
        }

        FeesDetails selected = pendingFeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a pending fee item first").showAndWait();
            return;
        }

        FeeReceiptItem item = new FeeReceiptItem();
        item.setFeesName(selected.getFeesName());
        item.setAllocatedTo(selected.getFeesName() != null ? selected.getFeesName().getName() : "");
        item.setAmount(selected.getAmount() != null ? selected.getAmount() : BigDecimal.ZERO);
        items.add(item);

        runningTotal = runningTotal.add(item.getAmount());
        runningTotalLabel.setText(String.format("Total: ₹%.2f", runningTotal));
        totalLabel.setText(String.format("%.2f", runningTotal));
    }

    @FXML
    private void handleManualAdd() {
        if (feeNameCombo.getValue() == null || "Select".equals(feeNameCombo.getValue()) || manualAmtField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select fee name and enter amount").showAndWait();
            return;
        }

        String feeName = feeNameCombo.getValue();
        BigDecimal amount;
        try {
            amount = new BigDecimal(manualAmtField.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid amount").showAndWait();
            return;
        }

        FeesMaster feeMaster = null;
        for (FeesMaster f : allFees) {
            if (f.getName().equalsIgnoreCase(feeName)) {
                feeMaster = f;
                break;
            }
        }

        FeeReceiptItem item = new FeeReceiptItem();
        item.setFeesName(feeMaster);
        item.setAllocatedTo(feeName);
        item.setAmount(amount);
        items.add(item);

        runningTotal = runningTotal.add(amount);
        runningTotalLabel.setText(String.format("Total: ₹%.2f", runningTotal));
        totalLabel.setText(String.format("%.2f", runningTotal));
        manualAmtField.clear();
    }

    @FXML
    private void handleRemoveItem() {
        FeeReceiptItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            runningTotal = runningTotal.subtract(selected.getAmount() != null ? selected.getAmount() : BigDecimal.ZERO);
            items.remove(selected);
            runningTotalLabel.setText(String.format("Total: ₹%.2f", runningTotal));
            totalLabel.setText(String.format("%.2f", runningTotal));
        } else {
            new Alert(Alert.AlertType.WARNING, "Select an item to remove").showAndWait();
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

        String payTypeValue = payTypeCombo.getValue();
        if (payTypeValue == null || "Select".equals(payTypeValue)) {
            new Alert(Alert.AlertType.WARNING, "Select a Pay Type").showAndWait();
            return;
        }

        String bankValue = baseAccountCombo.getValue();
        if (bankValue == null || "Select".equals(bankValue)) {
            new Alert(Alert.AlertType.WARNING, "Select a Bank A/C").showAndWait();
            return;
        }

        try {
            FeeReceipt receipt = new FeeReceipt();
            receipt.setStudent(selectedStudent);
            receipt.setStudentType(currentRadio.isSelected() ? "Current" : passedOutRadio.isSelected() ? "PassedOut" : staffRadio.isSelected() ? "Staff" : "Misc");
            receipt.setReceiptDate(receiptDatePicker.getValue() != null ? receiptDatePicker.getValue() : LocalDate.now());
            receipt.setAcademicYear(periodCombo.getValue());
            receipt.setBaseAccount(bankValue);
            receipt.setPaymentMode(payTypeValue);
            receipt.setPayType(payTypeValue);
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
        selectedStudent = null;
        selectedStudentDetails = null;
        items.clear();
        pendingFeesList.clear();
        runningTotal = BigDecimal.ZERO;

        studentSearchField.clear();
        receiptNoField.clear();
        studentNameLabel.clear();
        rollLabel.clear();
        regNoField.clear();
        casteField.clear();
        busRouteField.clear();
        degreeField.clear();
        deptLabel.clear();
        semLabel.clear();
        adTypeField.clear();
        quotaField.clear();
        busStopField.clear();
        remarksField.clear();
        manualAmtField.clear();
        amountField.clear();

        totalLabel.setText("");
        pendingFeesField.setText("");
        runningTotalLabel.setText("Total: ₹0.00");

        receiptTypeCombo.getSelectionModel().selectFirst();
        baseAccountCombo.getSelectionModel().selectFirst();
        payTypeCombo.getSelectionModel().selectFirst();
        feeNameCombo.getSelectionModel().selectFirst();
        periodCombo.getSelectionModel().selectFirst();
        receiptDatePicker.setValue(LocalDate.now());

        sem1Tab.setSelected(true);

        generateReceiptNumber();
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
            params.put("ACADEMIC_YEAR", periodCombo.getValue() != null ? periodCombo.getValue() : "2025-2026");

            List<Map<String, Object>> reportData = new ArrayList<>();
            for (FeeReceiptItem item : items) {
                Map<String, Object> row = new HashMap<>();
                row.put("receiptNumber", receiptNoField.getText());
                row.put("receiptDate", receiptDatePicker.getValue() != null ? receiptDatePicker.getValue() : LocalDate.now());
                row.put("studentName", selectedStudent.getName());
                row.put("rollNumber", selectedStudent.getRollNumber());
                row.put("department", deptLabel.getText());
                row.put("semester", semLabel.getText());
                row.put("paymentMode", payTypeCombo.getValue());
                row.put("baseAccount", baseAccountCombo.getValue());
                row.put("totalAmount", runningTotal);
                row.put("feeName", item.getAllocatedTo() != null ? item.getAllocatedTo() : "");
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
            params.put("ACADEMIC_YEAR", periodCombo.getValue() != null ? periodCombo.getValue() : "2025-2026");

            List<Map<String, Object>> reportData = new ArrayList<>();
            for (FeeReceiptItem item : items) {
                Map<String, Object> row = new HashMap<>();
                row.put("receiptNumber", receiptNoField.getText());
                row.put("receiptDate", receiptDatePicker.getValue() != null ? receiptDatePicker.getValue() : LocalDate.now());
                row.put("studentName", selectedStudent.getName());
                row.put("rollNumber", selectedStudent.getRollNumber());
                row.put("department", deptLabel.getText());
                row.put("semester", semLabel.getText());
                row.put("paymentMode", payTypeCombo.getValue());
                row.put("baseAccount", baseAccountCombo.getValue());
                row.put("totalAmount", runningTotal);
                row.put("feeName", item.getAllocatedTo() != null ? item.getAllocatedTo() : "");
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
