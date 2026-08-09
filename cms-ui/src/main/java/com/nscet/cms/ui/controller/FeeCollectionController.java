package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.FeeCollectionService;
import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.entity.FeeReceiptItem;
import com.nscet.cms.db.entity.StudentMaster;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

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

    private StudentMaster selectedStudent;
    private ObservableList<FeeReceiptItem> items = FXCollections.observableArrayList();
    private BigDecimal runningTotal = BigDecimal.ZERO;

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
        feeNameCombo.getItems().add("Select");
        feeNameCombo.getItems().addAll("Tuition Fee", "Admission Fees", "Bus Fee", "Exam Fee", "Library Fee", "Lab Fee", "Hostel Fee", "Uniform Fee", "Placement Fee", "Sports Fee");
        feeNameCombo.getSelectionModel().selectFirst();

        feeNameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getFeesName() != null ? c.getValue().getFeesName().getName() : ""));
        feeAmtCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            "₹" + c.getValue().getAmount()));
        allocCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getAllocatedTo() != null ? c.getValue().getAllocatedTo() : ""));

        itemsTable.setItems(items);
        itemsPane.setVisible(false); itemsPane.setManaged(false);
        studentNameLabel.setText("--"); rollLabel.setText("--"); deptLabel.setText("--"); semLabel.setText("--");
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

            // Auto-allocate: Tuition -> Other -> Bus
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                FeeReceiptItem tuition = new FeeReceiptItem();
                tuition.setAllocatedTo("Tuition Fee");
                tuition.setAmount(totalAmount.min(new BigDecimal("20000")));
                items.add(tuition);

                BigDecimal remaining = totalAmount.subtract(tuition.getAmount());
                if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                    FeeReceiptItem other = new FeeReceiptItem();
                    other.setAllocatedTo("Other Fee");
                    other.setAmount(remaining.min(new BigDecimal("15000")));
                    items.add(other);
                    remaining = remaining.subtract(other.getAmount());
                }

                if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                    FeeReceiptItem bus = new FeeReceiptItem();
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

    @FXML
    private void handleManualAdd() {
        if (feeNameCombo.getValue() == null || "Select".equals(feeNameCombo.getValue()) || manualAmtField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select fee name and enter amount").showAndWait();
            return;
        }

        FeeReceiptItem item = new FeeReceiptItem();
        item.setAllocatedTo(feeNameCombo.getValue());
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
        new Alert(Alert.AlertType.INFORMATION, "Print functionality will open receipt preview").showAndWait();
    }
}
