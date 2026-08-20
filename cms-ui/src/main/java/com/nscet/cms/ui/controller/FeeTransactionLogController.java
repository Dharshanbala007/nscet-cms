package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.FeeCollectionService;
import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.entity.FeeReceiptItem;
import com.nscet.cms.db.entity.FeesMaster;
import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FeeTransactionLogController implements Initializable {

    // Filters
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> bankAccountCombo;
    @FXML private ComboBox<String> payTypeCombo;
    @FXML private TextField receiptNoSearch;
    @FXML private TextField studentNameSearch;
    @FXML private TextField rollNoSearch;
    @FXML private ComboBox<String> studentTypeCombo;

    // Summary
    @FXML private Label totalReceiptsLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label filterStatus;

    // Main Table
    @FXML private TableView<FeeReceipt> transactionTable;
    @FXML private TableColumn<FeeReceipt, String> colSlNo;
    @FXML private TableColumn<FeeReceipt, String> colReceiptNo;
    @FXML private TableColumn<FeeReceipt, String> colDate;
    @FXML private TableColumn<FeeReceipt, String> colStudentName;
    @FXML private TableColumn<FeeReceipt, String> colRollNo;
    @FXML private TableColumn<FeeReceipt, String> colStudentType;
    @FXML private TableColumn<FeeReceipt, String> colAmount;
    @FXML private TableColumn<FeeReceipt, String> colPaymentMode;
    @FXML private TableColumn<FeeReceipt, String> colBaseAccount;
    @FXML private TableColumn<FeeReceipt, String> colStatus;
    @FXML private TableColumn<FeeReceipt, String> colActions;

    // Detail Panel
    @FXML private VBox detailPanel;
    @FXML private Label detailReceiptNo;
    @FXML private Label detailDate;
    @FXML private Label detailStudentName;
    @FXML private Label detailRollNo;
    @FXML private Label detailStudentType;
    @FXML private Label detailPaymentMode;
    @FXML private Label detailBaseAccount;
    @FXML private Label detailTotalAmount;
    @FXML private TableView<FeeReceiptItem> detailItemsTable;
    @FXML private TableColumn<FeeReceiptItem, String> detailItemName;
    @FXML private TableColumn<FeeReceiptItem, String> detailItemAmount;
    @FXML private TableColumn<FeeReceiptItem, String> detailItemAllocated;

    // Pagination
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;
    @FXML private Label pageInfo;

    @Autowired
    private FeeCollectionService receiptService;

    private final ObservableList<FeeReceipt> transactionData = FXCollections.observableArrayList();
    private final ObservableList<FeeReceiptItem> detailItemsData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 25;
    private int totalPages = 0;
    private boolean isFiltered = false;

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComboBoxes();
        initDatePickers();
        initMainTable();
        initDetailTable();
        loadTransactions();
    }

    private void initComboBoxes() {
        bankAccountCombo.getItems().addAll("All", "Cash", "Federal Bank", "TMB Exam Fee", "TMB College");
        bankAccountCombo.getSelectionModel().selectFirst();

        payTypeCombo.getItems().addAll("All", "Pay", "Receipt Bill", "OLP", "DD\\Cheque");
        payTypeCombo.getSelectionModel().selectFirst();

        studentTypeCombo.getItems().addAll("All", "Current", "PassedOut", "Staff", "Misc");
        studentTypeCombo.getSelectionModel().selectFirst();
    }

    private void initDatePickers() {
        fromDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        toDatePicker.setValue(LocalDate.now());
    }

    private void initMainTable() {
        colSlNo.setCellValueFactory(cellData -> {
            int rowIndex = transactionTable.getItems().indexOf(cellData.getValue());
            return new SimpleStringProperty(String.valueOf((currentPage * pageSize) + rowIndex + 1));
        });

        colReceiptNo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getReceiptNumber()));

        colDate.setCellValueFactory(cellData -> {
            LocalDate d = cellData.getValue().getReceiptDate();
            return new SimpleStringProperty(d != null ? d.format(DISPLAY_FORMAT) : "");
        });

        colStudentName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStudent() != null) {
                return new SimpleStringProperty(cellData.getValue().getStudent().getName());
            }
            return new SimpleStringProperty("");
        });

        colRollNo.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStudent() != null) {
                return new SimpleStringProperty(cellData.getValue().getStudent().getRollNumber());
            }
            return new SimpleStringProperty("");
        });

        colStudentType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentType()));

        colAmount.setCellValueFactory(cellData -> {
            BigDecimal amt = cellData.getValue().getTotalAmount();
            return new SimpleStringProperty(amt != null ? String.format("%.2f", amt) : "0.00");
        });

        colPaymentMode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPaymentMode()));

        colBaseAccount.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBaseAccount()));

        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        colActions.setCellValueFactory(cellData -> new SimpleStringProperty("View"));

        // Make the Details column clickable
        colActions.setCellFactory(col -> new TableCell<>() {
            {
                setOnMouseClicked(event -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        FeeReceipt receipt = getTableView().getItems().get(getIndex());
                        if (receipt != null) {
                            showDetail(receipt);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Button btn = new Button("View");
                    btn.getStyleClass().add("btn-sm");
                    btn.setOnAction(e -> {
                        if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                            FeeReceipt receipt = getTableView().getItems().get(getIndex());
                            if (receipt != null) {
                                showDetail(receipt);
                            }
                        }
                    });
                    setGraphic(btn);
                    setText(null);
                }
            }
        });

        transactionTable.setItems(transactionData);

        // Row double-click to view details
        transactionTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FeeReceipt selected = transactionTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showDetail(selected);
                }
            }
        });
    }

    private void initDetailTable() {
        detailItemName.setCellValueFactory(cellData -> {
            FeesMaster fee = cellData.getValue().getFeesName();
            if (fee != null) {
                return new SimpleStringProperty(fee.getName());
            }
            return new SimpleStringProperty(cellData.getValue().getAllocatedTo());
        });

        detailItemAmount.setCellValueFactory(cellData -> {
            BigDecimal amt = cellData.getValue().getAmount();
            return new SimpleStringProperty(amt != null ? String.format("%.2f", amt) : "0.00");
        });

        detailItemAllocated.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAllocatedTo()));

        detailItemsTable.setItems(detailItemsData);
    }

    private void loadTransactions() {
        try {
            Page<FeeReceipt> page;
            if (isFiltered) {
                String studentName = studentNameSearch.getText().trim();
                String rollNo = rollNoSearch.getText().trim();
                String receiptNo = receiptNoSearch.getText().trim();
                String studentType = studentTypeCombo.getValue();
                String bankAccount = bankAccountCombo.getValue();
                String payType = payTypeCombo.getValue();

                // If searching by receipt number specifically
                if (!receiptNo.isEmpty()) {
                    try {
                        FeeReceipt receipt = receiptService.getTransactionByReceiptNumber(receiptNo);
                        transactionData.clear();
                        transactionData.add(receipt);
                        updateSummary(1, receipt.getTotalAmount());
                        updatePagination(0, 1);
                        filterStatus.setText("Showing receipt: " + receiptNo);
                        return;
                    } catch (Exception e) {
                        transactionData.clear();
                        updateSummary(0, BigDecimal.ZERO);
                        filterStatus.setText("No receipt found with number: " + receiptNo);
                        return;
                    }
                }

                // If searching by roll number
                if (!rollNo.isEmpty()) {
                    List<FeeReceipt> receipts = receiptService.getTransactionsByStudentRollNo(rollNo);
                    transactionData.clear();
                    transactionData.addAll(receipts);
                    BigDecimal totalAmt = receipts.stream()
                            .map(r -> r.getTotalAmount() != null ? r.getTotalAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    updateSummary(receipts.size(), totalAmt);
                    updatePagination(0, 1);
                    filterStatus.setText("Showing " + receipts.size() + " receipts for roll: " + rollNo);
                    return;
                }

                // Standard filtered search
                page = receiptService.getTransactionLog(
                        fromDatePicker.getValue(), toDatePicker.getValue(),
                        studentName, studentType, bankAccount, payType,
                        currentPage, pageSize);

                transactionData.clear();
                if (page != null && page.getContent() != null) {
                    transactionData.addAll(page.getContent());
                    totalPages = page.getTotalPages();
                    long totalElements = page.getTotalElements();
                    BigDecimal totalAmt = receiptService.getFilteredCollectionAmount(
                            fromDatePicker.getValue(), toDatePicker.getValue(),
                            studentName, studentType, bankAccount, payType);
                    updateSummary(totalElements, totalAmt);
                    updatePagination(currentPage, totalPages);
                    filterStatus.setText("Showing " + transactionData.size() + " of " + totalElements + " records");
                }
            } else {
                // Load all
                page = receiptService.getTransactionLog(
                        fromDatePicker.getValue(), toDatePicker.getValue(),
                        null, null, null, null,
                        currentPage, pageSize);

                transactionData.clear();
                if (page != null && page.getContent() != null) {
                    transactionData.addAll(page.getContent());
                    totalPages = page.getTotalPages();
                    long totalElements = page.getTotalElements();
                    BigDecimal totalAmt = receiptService.getFilteredCollectionAmount(
                            fromDatePicker.getValue(), toDatePicker.getValue(),
                            null, null, null, null);
                    updateSummary(totalElements, totalAmt);
                    updatePagination(currentPage, totalPages);
                    filterStatus.setText("Showing " + transactionData.size() + " of " + totalElements + " records");
                }
            }
        } catch (Exception e) {
            transactionData.clear();
            updateSummary(0, BigDecimal.ZERO);
            filterStatus.setText("Error loading data: " + e.getMessage());
        }
    }

    private void updateSummary(long totalReceipts, BigDecimal totalAmount) {
        totalReceiptsLabel.setText(String.valueOf(totalReceipts));
        totalAmountLabel.setText("Rs. " + String.format("%.2f", totalAmount));
    }

    private void updatePagination(int currentPage, int totalPages) {
        pageInfo.setText("Page " + (currentPage + 1) + " of " + Math.max(totalPages, 1));
        prevBtn.setDisable(currentPage <= 0);
        nextBtn.setDisable(currentPage >= totalPages - 1 || totalPages <= 1);
    }

    private void showDetail(FeeReceipt receipt) {
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);

        detailReceiptNo.setText(receipt.getReceiptNumber());
        detailDate.setText(receipt.getReceiptDate() != null ? receipt.getReceiptDate().format(DISPLAY_FORMAT) : "");
        detailStudentName.setText(receipt.getStudent() != null ? receipt.getStudent().getName() : "");
        detailRollNo.setText(receipt.getStudent() != null ? receipt.getStudent().getRollNumber() : "");
        detailStudentType.setText(receipt.getStudentType());
        detailPaymentMode.setText(receipt.getPaymentMode());
        detailBaseAccount.setText(receipt.getBaseAccount());
        detailTotalAmount.setText("Rs. " + String.format("%.2f",
                receipt.getTotalAmount() != null ? receipt.getTotalAmount() : BigDecimal.ZERO));

        detailItemsData.clear();
        if (receipt.getItems() != null) {
            detailItemsData.addAll(receipt.getItems());
        }
    }

    @FXML
    private void handleSearch() {
        isFiltered = true;
        currentPage = 0;
        loadTransactions();
    }

    @FXML
    private void handleReset() {
        fromDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        toDatePicker.setValue(LocalDate.now());
        bankAccountCombo.getSelectionModel().selectFirst();
        payTypeCombo.getSelectionModel().selectFirst();
        studentTypeCombo.getSelectionModel().selectFirst();
        receiptNoSearch.clear();
        studentNameSearch.clear();
        rollNoSearch.clear();
        isFiltered = false;
        currentPage = 0;
        handleCloseDetail();
        loadTransactions();
    }

    @FXML
    private void handleExport() {
        if (transactionData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No data to export");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Transaction Log");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("fee_transaction_log.csv");

        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());
        if (file == null) return;

        try (FileWriter writer = new FileWriter(file)) {
            // Header
            writer.append("Sl.No,Receipt No,Date,Student Name,Roll No,Student Type,Amount,Payment Mode,Bank/Account,Status\n");

            // Data rows
            int sl = 1;
            for (FeeReceipt receipt : transactionData) {
                writer.append(String.valueOf(sl++)).append(",");
                writer.append(safe(receipt.getReceiptNumber())).append(",");
                writer.append(receipt.getReceiptDate() != null ? receipt.getReceiptDate().format(DISPLAY_FORMAT) : "").append(",");
                writer.append(receipt.getStudent() != null ? safe(receipt.getStudent().getName()) : "").append(",");
                writer.append(receipt.getStudent() != null ? safe(receipt.getStudent().getRollNumber()) : "").append(",");
                writer.append(safe(receipt.getStudentType())).append(",");
                writer.append(receipt.getTotalAmount() != null ? String.format("%.2f", receipt.getTotalAmount()) : "0.00").append(",");
                writer.append(safe(receipt.getPaymentMode())).append(",");
                writer.append(safe(receipt.getBaseAccount())).append(",");
                writer.append(safe(receipt.getStatus())).append("\n");
            }

            showAlert(Alert.AlertType.INFORMATION, "Exported " + transactionData.size() + " records to:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    private String safe(String value) {
        if (value == null) return "";
        return value.contains(",") ? "\"" + value + "\"" : value;
    }

    @FXML
    private void handleCloseDetail() {
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
        detailItemsData.clear();
    }

    @FXML
    private void handlePrevious() {
        if (currentPage > 0) {
            currentPage--;
            loadTransactions();
        }
    }

    @FXML
    private void handleNext() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadTransactions();
        }
    }

    @FXML
    private void handleExit() {
        NavigationManager.loadModule("dashboard", NavigationManager.getActiveContentArea());
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Fee Transaction History");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
