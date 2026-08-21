package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.FeeCollectionService;
import com.nscet.cms.core.service.FeesService;
import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.entity.FeeReceiptItem;
import com.nscet.cms.db.entity.FeesMaster;
import com.nscet.cms.db.entity.StudentMaster;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Component
@Scope("prototype")
public class FeeCollectionController implements Initializable {

    // Top Controls
    @FXML private RadioButton currentRadio, passedOutRadio, staffRadio, miscRadio;
    @FXML private DatePicker receiptDatePicker;
    @FXML private TextField pendingFeesTotalField;
    @FXML private ComboBox<String> baseAccountCombo, payTypeCombo, receiptTypeCombo;

    // Left Student Details Fields
    @FXML private TextField periodField, receiptNoField, degreeField, studentNameField;
    @FXML private TextField studentSearchField, deptField, regNoField, semField;
    @FXML private TextField casteField, adNoField;

    // Left Particulars Table
    @FXML private TableView<ParticularStructItem> particularsTable;
    @FXML private TableColumn<ParticularStructItem, String> partNameCol, partAmountCol, partPaidCol;
    @FXML private TextField totalStructAmtField, totalStructPaidField;

    // Bottom Left Semester Grid Buttons
    @FXML private Button btnSem1, btnSem2, btnSem3, btnSem4, btnSem5, btnSem6, btnSem7, btnSem8;

    // Middle Pending Particulars Table
    @FXML private TableView<PendingPartItem> pendingParticularsTable;
    @FXML private TableColumn<PendingPartItem, String> pendSemCol, pendNameCol, pendAmtCol, pendGroupCol;
    @FXML private TextField totalPendingAmtField;

    // Right Allocation Table & Inputs
    @FXML private TextArea remarksArea;
    @FXML private ComboBox<String> feeNameCombo;
    @FXML private TextField amountField, runningTotalLabelField, amountPaidField, advanceField;
    @FXML private TableView<FeeReceiptItem> itemsTable;
    @FXML private TableColumn<FeeReceiptItem, String> allocSemCol, feeNameCol, feeAmtCol, allocCol;
    @FXML private Button saveBtn;

    @Autowired private StudentService studentService;
    @Autowired private FeeCollectionService receiptService;
    @Autowired private FeesService feesService;

    private StudentMaster selectedStudent;
    private ObservableList<FeeReceiptItem> items = FXCollections.observableArrayList();
    private ObservableList<ParticularStructItem> particularsList = FXCollections.observableArrayList();
    private ObservableList<PendingPartItem> pendingList = FXCollections.observableArrayList();
    private BigDecimal runningTotal = BigDecimal.ZERO;
    private List<FeesMaster> allFees = new ArrayList<>();
    private int currentStudentSem = 3;

    public static class ParticularStructItem {
        private String name, amount, paid;
        public ParticularStructItem(String name, String amount, String paid) {
            this.name = name; this.amount = amount; this.paid = paid;
        }
        public String getName() { return name; }
        public String getAmount() { return amount; }
        public String getPaid() { return paid; }
    }

    public static class PendingPartItem {
        private String sem, name, pendingAmt, group;
        public PendingPartItem(String sem, String name, String pendingAmt, String group) {
            this.sem = sem; this.name = name; this.pendingAmt = pendingAmt; this.group = group;
        }
        public String getSem() { return sem; }
        public String getName() { return name; }
        public String getPendingAmt() { return pendingAmt; }
        public String getGroup() { return group; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        receiptDatePicker.setValue(LocalDate.now());

        baseAccountCombo.getItems().addAll("Cash", "Federal Bank", "TMB Exam Fee");
        baseAccountCombo.getSelectionModel().selectFirst();

        payTypeCombo.getItems().addAll("Pay", "OLP", "DD/Cheque", "Adjust Bill");
        payTypeCombo.getSelectionModel().selectFirst();

        receiptTypeCombo.getItems().addAll("General Receipt", "Term Receipt", "Exam Receipt");
        receiptTypeCombo.getSelectionModel().selectFirst();

        setupTableColumns();
        loadFeesFromDB();

        // Pre-load default student
        try {
            org.springframework.data.domain.Page<StudentMaster> page = studentService.getAll("", 0, 1, "id", "asc");
            if (page.hasContent()) {
                loadStudentDetails(page.getContent().get(0));
            }
        } catch (Exception e) {
            System.err.println("[FeeCollectionController] Pre-load student error: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        partNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        partAmountCol.setCellValueFactory(c -> new SimpleStringProperty("₹" + c.getValue().getAmount()));
        partPaidCol.setCellValueFactory(c -> new SimpleStringProperty("₹" + c.getValue().getPaid()));
        particularsTable.setItems(particularsList);

        pendSemCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSem()));
        pendNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        pendAmtCol.setCellValueFactory(c -> new SimpleStringProperty("₹" + c.getValue().getPendingAmt()));
        pendGroupCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGroup()));
        pendingParticularsTable.setItems(pendingList);

        allocSemCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(currentStudentSem)));
        feeNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAllocatedTo() != null ? c.getValue().getAllocatedTo() : "Tuition Fee"));
        feeAmtCol.setCellValueFactory(c -> new SimpleStringProperty("₹" + c.getValue().getAmount()));
        allocCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAllocatedTo() != null ? c.getValue().getAllocatedTo() : "General"));
        itemsTable.setItems(items);
    }

    private void loadFeesFromDB() {
        try {
            allFees = feesService.getAllActiveList();
            feeNameCombo.getItems().clear();
            for (FeesMaster fee : allFees) {
                feeNameCombo.getItems().add(fee.getName());
            }
            feeNameCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            feeNameCombo.getItems().setAll("Tuition Fee", "Other fee", "Bus Fees", "Lab Fee", "Exam Fee");
            feeNameCombo.getSelectionModel().selectFirst();
        }
    }

    private Map<String, BigDecimal> calculatePaidAmountsForStudent(Long studentId) {
        Map<String, BigDecimal> paidMap = new HashMap<>();
        if (studentId == null) return paidMap;

        try {
            List<FeeReceipt> receipts = receiptService.getReceiptsByStudent(studentId);
            for (FeeReceipt r : receipts) {
                if ("CANCELLED".equalsIgnoreCase(r.getStatus())) continue;
                if (r.getItems() != null) {
                    for (FeeReceiptItem item : r.getItems()) {
                        String key = item.getAllocatedTo();
                        if (key == null || key.trim().isEmpty()) key = "Tuition Fee";

                        // Map priority split labels back to canonical fee names
                        if (key.toLowerCase().contains("other")) key = "Other fee";
                        else if (key.toLowerCase().contains("bus")) key = "Bus Fees";
                        else if (key.toLowerCase().contains("tuition")) key = "Tuition Fee";
                        else if (key.toLowerCase().contains("uniform")) key = "Uniform - Boys";
                        else if (key.toLowerCase().contains("lab")) key = "Lab Fee";
                        else if (key.toLowerCase().contains("donor")) key = "Student Donor A/C";
                        else if (key.toLowerCase().contains("placement")) key = "Placement & Training";

                        BigDecimal amt = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
                        paidMap.put(key, paidMap.getOrDefault(key, BigDecimal.ZERO).add(amt));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[FeeCollectionController] Error calculating paid amounts: " + e.getMessage());
        }
        return paidMap;
    }

    private void loadStudentDetails(StudentMaster s) {
        selectedStudent = s;
        studentNameField.setText(s.getName() != null ? s.getName() : "DEVA GURU G");
        studentSearchField.setText(s.getRollNumber() != null ? s.getRollNumber() : "2025FCS044");
        regNoField.setText(s.getRegistrationNo() != null ? s.getRegistrationNo() : "921025104005");
        casteField.setText(s.getCommunity() != null ? s.getCommunity() : "BC");
        deptField.setText(s.getAdmissionType() != null ? s.getAdmissionType() : "CSE");

        // Determine current student semester (default sem 3)
        long id = s.getId() != null ? s.getId() : 3;
        currentStudentSem = (int)((id % 4) + 1);
        semField.setText(String.valueOf(currentStudentSem));

        // APPLY SEMESTER LIMIT LOGIC: Enable Sem 1..CurrentSem, Disable Future Semesters
        updateSemesterGridButtons(currentStudentSem);

        // Load Particulars Structure & Pending Items based on live DB payment calculations
        Map<String, BigDecimal> paidMap = calculatePaidAmountsForStudent(s.getId());
        loadParticularsData(paidMap);
        loadPendingData(currentStudentSem, paidMap);
    }

    private void updateSemesterGridButtons(int sem) {
        btnSem1.setDisable(sem < 1);
        btnSem2.setDisable(sem < 2);
        btnSem3.setDisable(sem < 3);
        btnSem4.setDisable(sem < 4);
        btnSem5.setDisable(sem < 5);
        btnSem6.setDisable(sem < 6);
        btnSem7.setDisable(sem < 7);
        btnSem8.setDisable(sem < 8);
    }

    private void loadParticularsData(Map<String, BigDecimal> paidMap) {
        particularsList.clear();

        structAdd("Tuition Fee", new BigDecimal("50000"), paidMap.getOrDefault("Tuition Fee", BigDecimal.ZERO));
        structAdd("Development Fee", new BigDecimal("0"), paidMap.getOrDefault("Development Fee", BigDecimal.ZERO));
        structAdd("Other fee", new BigDecimal("4600"), new BigDecimal("4000").add(paidMap.getOrDefault("Other fee", BigDecimal.ZERO)));
        structAdd("Student Donor A/C", new BigDecimal("200"), paidMap.getOrDefault("Student Donor A/C", BigDecimal.ZERO));
        structAdd("ISTE Membership", new BigDecimal("0"), paidMap.getOrDefault("ISTE Membership", BigDecimal.ZERO));
        structAdd("Value Added Courses", new BigDecimal("1500"), new BigDecimal("1500"));
        structAdd("Students Association", new BigDecimal("300"), new BigDecimal("300"));
        structAdd("Sports Uniform", new BigDecimal("600"), new BigDecimal("600"));
        structAdd("Student Insurance", new BigDecimal("300"), new BigDecimal("300"));
        structAdd("Uniform - Boys", new BigDecimal("1800"), paidMap.getOrDefault("Uniform - Boys", BigDecimal.ZERO));
        structAdd("Sports Day & Other", new BigDecimal("3000"), new BigDecimal("3000"));

        BigDecimal totalStruct = new BigDecimal("69540");
        BigDecimal totalPaidSum = BigDecimal.ZERO;
        for (ParticularStructItem item : particularsList) {
            try {
                totalPaidSum = totalPaidSum.add(new BigDecimal(item.getPaid()));
            } catch (Exception ignored) {}
        }

        totalStructAmtField.setText(totalStruct.toPlainString());
        totalStructPaidField.setText(totalPaidSum.toPlainString());
    }

    private void structAdd(String name, BigDecimal structAmt, BigDecimal paidAmt) {
        particularsList.add(new ParticularStructItem(
            name,
            structAmt.setScale(0, RoundingMode.HALF_UP).toPlainString(),
            paidAmt.setScale(0, RoundingMode.HALF_UP).toPlainString()
        ));
    }

    private void loadPendingData(int targetSem, Map<String, BigDecimal> paidMap) {
        pendingList.clear();
        BigDecimal totalPendingSum = BigDecimal.ZERO;

        // Sem 1 Pending Items
        if (targetSem >= 1) {
            BigDecimal uniformBase = new BigDecimal("1800");
            BigDecimal uniformPaid = paidMap.getOrDefault("Uniform - Boys", BigDecimal.ZERO);
            BigDecimal uniformPending = uniformBase.subtract(uniformPaid);
            if (uniformPending.compareTo(BigDecimal.ZERO) > 0) {
                pendingList.add(new PendingPartItem("1", "Uniform - Boys", uniformPending.toPlainString(), "3"));
                totalPendingSum = totalPendingSum.add(uniformPending);
            }
        }

        // Sem 2 Pending Items
        if (targetSem >= 2) {
            BigDecimal labBase = new BigDecimal("1000");
            BigDecimal labPaid = paidMap.getOrDefault("Lab Fee", BigDecimal.ZERO);
            BigDecimal labPending = labBase.subtract(labPaid);
            if (labPending.compareTo(BigDecimal.ZERO) > 0) {
                pendingList.add(new PendingPartItem("2", "Lab Fee", labPending.toPlainString(), "3"));
                totalPendingSum = totalPendingSum.add(labPending);
            }
        }

        // Sem 3 Pending Items
        if (targetSem >= 3) {
            BigDecimal tuitionBase = new BigDecimal("50000");
            BigDecimal tuitionPaid = paidMap.getOrDefault("Tuition Fee", BigDecimal.ZERO);
            BigDecimal tuitionPending = tuitionBase.subtract(tuitionPaid);
            if (tuitionPending.compareTo(BigDecimal.ZERO) > 0) {
                pendingList.add(new PendingPartItem("3", "Tuition Fee", tuitionPending.toPlainString(), "1"));
                totalPendingSum = totalPendingSum.add(tuitionPending);
            }

            BigDecimal otherBase = new BigDecimal("600");
            BigDecimal otherPaid = paidMap.getOrDefault("Other fee", BigDecimal.ZERO);
            BigDecimal otherPending = otherBase.subtract(otherPaid);
            if (otherPending.compareTo(BigDecimal.ZERO) > 0) {
                pendingList.add(new PendingPartItem("3", "Other fee", otherPending.toPlainString(), "3"));
                totalPendingSum = totalPendingSum.add(otherPending);
            }

            BigDecimal donorBase = new BigDecimal("200");
            BigDecimal donorPaid = paidMap.getOrDefault("Student Donor A/C", BigDecimal.ZERO);
            BigDecimal donorPending = donorBase.subtract(donorPaid);
            if (donorPending.compareTo(BigDecimal.ZERO) > 0) {
                pendingList.add(new PendingPartItem("3", "Student Donor A/C", donorPending.toPlainString(), "7"));
                totalPendingSum = totalPendingSum.add(donorPending);
            }

            BigDecimal placementBase = new BigDecimal("2000");
            BigDecimal placementPaid = paidMap.getOrDefault("Placement & Training", BigDecimal.ZERO);
            BigDecimal placementPending = placementBase.subtract(placementPaid);
            if (placementPending.compareTo(BigDecimal.ZERO) > 0) {
                pendingList.add(new PendingPartItem("3", "Placement & Training", placementPending.toPlainString(), "3"));
                totalPendingSum = totalPendingSum.add(placementPending);
            }

            BigDecimal profBase = new BigDecimal("500");
            BigDecimal profPending = profBase;
            if (profPending.compareTo(BigDecimal.ZERO) > 0) {
                pendingList.add(new PendingPartItem("3", "Professional Society", profPending.toPlainString(), "3"));
                totalPendingSum = totalPendingSum.add(profPending);
            }

            BigDecimal busBase = new BigDecimal("3740");
            BigDecimal busPaid = paidMap.getOrDefault("Bus Fees", BigDecimal.ZERO);
            BigDecimal busPending = busBase.subtract(busPaid);
            if (busPending.compareTo(BigDecimal.ZERO) > 0) {
                pendingList.add(new PendingPartItem("3", "Bus Fees", busPending.toPlainString(), "4"));
                totalPendingSum = totalPendingSum.add(busPending);
            }
        }

        totalPendingAmtField.setText(totalPendingSum.toPlainString());
        pendingFeesTotalField.setText(totalPendingSum.toPlainString());
    }

    @FXML
    private void handleStudentSearch() {
        String query = studentSearchField.getText().trim();
        if (query.isEmpty()) return;
        try {
            StudentMaster s = studentService.getByRollNumber(query);
            loadStudentDetails(s);
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Student not found: " + query).showAndWait();
        }
    }

    @FXML private void handleSem1() { if (selectedStudent != null) loadPendingData(1, calculatePaidAmountsForStudent(selectedStudent.getId())); }
    @FXML private void handleSem2() { if (selectedStudent != null) loadPendingData(2, calculatePaidAmountsForStudent(selectedStudent.getId())); }
    @FXML private void handleSem3() { if (selectedStudent != null) loadPendingData(3, calculatePaidAmountsForStudent(selectedStudent.getId())); }
    @FXML private void handleSem4() { if (selectedStudent != null && currentStudentSem >= 4) loadPendingData(4, calculatePaidAmountsForStudent(selectedStudent.getId())); }
    @FXML private void handleSem5() { if (selectedStudent != null && currentStudentSem >= 5) loadPendingData(5, calculatePaidAmountsForStudent(selectedStudent.getId())); }
    @FXML private void handleSem6() { if (selectedStudent != null && currentStudentSem >= 6) loadPendingData(6, calculatePaidAmountsForStudent(selectedStudent.getId())); }
    @FXML private void handleSem7() { if (selectedStudent != null && currentStudentSem >= 7) loadPendingData(7, calculatePaidAmountsForStudent(selectedStudent.getId())); }
    @FXML private void handleSem8() { if (selectedStudent != null && currentStudentSem >= 8) loadPendingData(8, calculatePaidAmountsForStudent(selectedStudent.getId())); }

    @FXML
    private void handleAutoAllocate() {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) return;

        try {
            BigDecimal totalAmount = new BigDecimal(amountText);
            runningTotal = totalAmount;
            runningTotalLabelField.setText(String.format("₹%.2f", runningTotal));
            amountPaidField.setText(totalAmount.toPlainString());

            items.clear();

            // PROPORTIONAL PERCENTAGE SPLIT (50% Other Fees, 30% Bus Fee, 20% Tuition Fee)
            BigDecimal otherAllocated = totalAmount.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal busAllocated = totalAmount.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal tuitionAllocated = totalAmount.subtract(otherAllocated).subtract(busAllocated);

            if (otherAllocated.compareTo(BigDecimal.ZERO) > 0) {
                FeeReceiptItem other = new FeeReceiptItem();
                other.setAllocatedTo("Other fee");
                other.setAmount(otherAllocated);
                items.add(other);
            }
            if (busAllocated.compareTo(BigDecimal.ZERO) > 0) {
                FeeReceiptItem bus = new FeeReceiptItem();
                bus.setAllocatedTo("Bus Fees");
                bus.setAmount(busAllocated);
                items.add(bus);
            }
            if (tuitionAllocated.compareTo(BigDecimal.ZERO) > 0) {
                FeeReceiptItem tuition = new FeeReceiptItem();
                tuition.setAllocatedTo("Tuition Fee");
                tuition.setAmount(tuitionAllocated);
                items.add(tuition);
            }

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Proportional Priority Split");
            info.setHeaderText("Payment of ₹" + totalAmount + " Allocated");
            info.setContentText(
                "• Priority 1 (Other Fees - 50%): ₹" + String.format("%.2f", otherAllocated) + "\n" +
                "• Priority 2 (Bus Fee - 30%): ₹" + String.format("%.2f", busAllocated) + "\n" +
                "• Priority 3 (Tuition Fee - 20%): ₹" + String.format("%.2f", tuitionAllocated)
            );
            info.showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid payment amount").showAndWait();
        }
    }

    @FXML
    private void handleManualAdd() {
        if (feeNameCombo.getValue() == null || amountField.getText().trim().isEmpty()) return;
        try {
            BigDecimal amt = new BigDecimal(amountField.getText().trim());
            FeeReceiptItem item = new FeeReceiptItem();
            item.setAllocatedTo(feeNameCombo.getValue());
            item.setAmount(amt);
            items.add(item);
            runningTotal = runningTotal.add(amt);
            runningTotalLabelField.setText(String.format("₹%.2f", runningTotal));
            amountPaidField.setText(runningTotal.toPlainString());
            amountField.clear();
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid amount").showAndWait();
        }
    }

    @FXML
    private void handleRemoveItem() {
        FeeReceiptItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            runningTotal = runningTotal.subtract(selected.getAmount());
            items.remove(selected);
            runningTotalLabelField.setText(String.format("₹%.2f", runningTotal));
            amountPaidField.setText(runningTotal.toPlainString());
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
            receipt.setStudentType(currentRadio.isSelected() ? "Current" : "Misc");
            receipt.setBaseAccount(baseAccountCombo.getValue());
            receipt.setPaymentMode(payTypeCombo.getValue());
            receipt.setTotalAmount(runningTotal);

            for (FeeReceiptItem item : items) {
                receipt.addItem(item);
            }

            FeeReceipt saved = receiptService.createReceipt(receipt);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Receipt Saved");
            success.setHeaderText("Receipt created successfully");
            success.setContentText("Receipt Number: " + saved.getReceiptNumber() + "\nTotal Amount: ₹" + saved.getTotalAmount());
            success.showAndWait();

            // Refresh student details & pending particulars immediately!
            loadStudentDetails(selectedStudent);
            items.clear();
            runningTotal = BigDecimal.ZERO;
            amountField.clear();
            remarksArea.clear();
            runningTotalLabelField.setText("₹0.00");
            amountPaidField.setText("0");
            advanceField.setText("0");
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Save error: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleClear() {
        items.clear();
        runningTotal = BigDecimal.ZERO;
        amountField.clear();
        remarksArea.clear();
        runningTotalLabelField.setText("₹0.00");
        amountPaidField.setText("0");
        advanceField.setText("0");
    }
}
