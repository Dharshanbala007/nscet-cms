package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.entity.FeeReceiptItem;
import com.nscet.cms.db.repository.FeeReceiptRepository;
import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class DailyTransactionController implements Initializable {

    @FXML private Label totalCollectionLabel;
    @FXML private Label receiptCountLabel;
    @FXML private Label federalBankLabel;
    @FXML private Label tmbMainLabel;
    @FXML private Label cashLabel;
    @FXML private Label recordSummaryLabel;
    @FXML private Label filteredTotalLabel;

    @FXML private TextField searchField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> accountFilterCombo;
    @FXML private ComboBox<String> modeFilterCombo;

    @FXML private TableView<FeeReceipt> logTable;
    @FXML private TableColumn<FeeReceipt, String> colReceipt;
    @FXML private TableColumn<FeeReceipt, String> colDate;
    @FXML private TableColumn<FeeReceipt, String> colStudent;
    @FXML private TableColumn<FeeReceipt, String> colAmount;
    @FXML private TableColumn<FeeReceipt, String> colAccount;
    @FXML private TableColumn<FeeReceipt, String> colMode;
    @FXML private TableColumn<FeeReceipt, String> colStatus;
    @FXML private TableColumn<FeeReceipt, Void> colActions;

    @Autowired(required = false)
    private FeeReceiptRepository feeReceiptRepo;

    private List<FeeReceipt> allReceipts = new ArrayList<>();
    private ObservableList<FeeReceipt> filteredList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilterCombos();
        setupTableColumns();
        loadReceiptData();

        // Add real-time listener on search field
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    private void setupFilterCombos() {
        accountFilterCombo.setItems(FXCollections.observableArrayList(
                "All Accounts", "Federal Bank", "Federal Online", "TMB Main", "Cash"
        ));
        accountFilterCombo.getSelectionModel().selectFirst();
        accountFilterCombo.setOnAction(e -> applyFilter());

        modeFilterCombo.setItems(FXCollections.observableArrayList(
                "All Modes", "CASH", "ONLINE", "DD"
        ));
        modeFilterCombo.getSelectionModel().selectFirst();
        modeFilterCombo.setOnAction(e -> applyFilter());
    }

    private void setupTableColumns() {
        colReceipt.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                return data.getValue() != null ? data.getValue().getReceiptNumber() : "";
            }
        });

        colDate.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                return (data.getValue() != null && data.getValue().getReceiptDate() != null)
                        ? data.getValue().getReceiptDate().toString() : "";
            }
        });

        colStudent.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                if (data.getValue() == null || data.getValue().getStudent() == null) return "N/A";
                return data.getValue().getStudent().getName();
            }
        });

        colAmount.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                if (data.getValue() == null || data.getValue().getTotalAmount() == null) return "Rs.0.00";
                return "Rs." + String.format("%,.2f", data.getValue().getTotalAmount());
            }
        });

        colAccount.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                return (data.getValue() != null && data.getValue().getBaseAccount() != null)
                        ? data.getValue().getBaseAccount() : "General";
            }
        });

        colMode.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                return (data.getValue() != null && data.getValue().getPaymentMode() != null)
                        ? data.getValue().getPaymentMode() : "CASH";
            }
        });

        colStatus.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                return (data.getValue() != null && data.getValue().getStatus() != null)
                        ? data.getValue().getStatus() : "ACTIVE";
            }
        });

        // Add View Details button column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");

            {
                viewBtn.getStyleClass().add("dash-outline-btn");
                viewBtn.setStyle("-fx-padding: 3 10; -fx-font-size: 11px;");
                viewBtn.setOnAction(event -> {
                    FeeReceipt receipt = getTableView().getItems().get(getIndex());
                    showReceiptDetails(receipt);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                }
            }
        });
    }

    private void loadReceiptData() {
        try {
            if (feeReceiptRepo != null) {
                allReceipts = feeReceiptRepo.findAll();
                // Sort descending by date
                allReceipts.sort((r1, r2) -> {
                    if (r1.getReceiptDate() == null || r2.getReceiptDate() == null) return 0;
                    return r2.getReceiptDate().compareTo(r1.getReceiptDate());
                });
            }
        } catch (Exception e) {
            System.err.println("[DailyTransactionController] Error loading receipts: " + e.getMessage());
            allReceipts = new ArrayList<>();
        }

        updateTopMetricCards(allReceipts);
        applyFilter();
    }

    private void updateTopMetricCards(List<FeeReceipt> receipts) {
        BigDecimal totalSum = BigDecimal.ZERO;
        BigDecimal federalSum = BigDecimal.ZERO;
        BigDecimal tmbSum = BigDecimal.ZERO;
        BigDecimal cashSum = BigDecimal.ZERO;

        for (FeeReceipt r : receipts) {
            BigDecimal amt = r.getTotalAmount() != null ? r.getTotalAmount() : BigDecimal.ZERO;
            totalSum = totalSum.add(amt);

            String acc = r.getBaseAccount() != null ? r.getBaseAccount().toLowerCase() : "";
            if (acc.contains("federal")) {
                federalSum = federalSum.add(amt);
            } else if (acc.contains("tmb")) {
                tmbSum = tmbSum.add(amt);
            } else {
                cashSum = cashSum.add(amt);
            }
        }

        totalCollectionLabel.setText("Rs." + String.format("%,.0f", totalSum));
        receiptCountLabel.setText(receipts.size() + " receipts total");
        federalBankLabel.setText("Rs." + String.format("%,.0f", federalSum));
        tmbMainLabel.setText("Rs." + String.format("%,.0f", tmbSum));
        cashLabel.setText("Rs." + String.format("%,.0f", cashSum));
    }

    private void applyFilter() {
        String query = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String selectedAccount = accountFilterCombo.getValue();
        String selectedMode = modeFilterCombo.getValue();

        List<FeeReceipt> result = allReceipts.stream().filter(r -> {
            // Search text check
            if (!query.isEmpty()) {
                boolean matchReceipt = r.getReceiptNumber() != null && r.getReceiptNumber().toLowerCase().contains(query);
                boolean matchStudent = r.getStudent() != null && r.getStudent().getName() != null &&
                        r.getStudent().getName().toLowerCase().contains(query);
                boolean matchRoll = r.getStudent() != null && r.getStudent().getRollNumber() != null &&
                        r.getStudent().getRollNumber().toLowerCase().contains(query);
                if (!matchReceipt && !matchStudent && !matchRoll) return false;
            }

            // Date Range check
            if (fromDate != null && r.getReceiptDate() != null && r.getReceiptDate().isBefore(fromDate)) {
                return false;
            }
            if (toDate != null && r.getReceiptDate() != null && r.getReceiptDate().isAfter(toDate)) {
                return false;
            }

            // Account check
            if (selectedAccount != null && !selectedAccount.equals("All Accounts")) {
                String acc = r.getBaseAccount() != null ? r.getBaseAccount() : "";
                if (!acc.equalsIgnoreCase(selectedAccount)) return false;
            }

            // Payment Mode check
            if (selectedMode != null && !selectedMode.equals("All Modes")) {
                String mode = r.getPaymentMode() != null ? r.getPaymentMode() : "";
                if (!mode.equalsIgnoreCase(selectedMode)) return false;
            }

            return true;
        }).collect(Collectors.toList());

        filteredList.setAll(result);
        logTable.setItems(filteredList);

        // Update footer totals
        BigDecimal filteredTotal = result.stream()
                .map(r -> r.getTotalAmount() != null ? r.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        recordSummaryLabel.setText("Showing " + result.size() + " of " + allReceipts.size() + " entries");
        filteredTotalLabel.setText("Rs." + String.format("%,.2f", filteredTotal));
    }

    @FXML
    private void handleApplyFilter() {
        applyFilter();
    }

    @FXML
    private void handleResetFilter() {
        searchField.clear();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        accountFilterCombo.getSelectionModel().selectFirst();
        modeFilterCombo.getSelectionModel().selectFirst();
        applyFilter();
    }

    @FXML
    private void handleNewFeeEntry() {
        StackPane contentArea = NavigationManager.getActiveContentArea();
        if (contentArea != null) {
            NavigationManager.loadModule("feeCollection", contentArea);
        }
    }

    @FXML
    private void handleExport() {
        if (filteredList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No records to export.", ButtonType.OK);
            alert.setTitle("Export CSV");
            alert.showAndWait();
            return;
        }

        try {
            File exportFile = new File(System.getProperty("user.home"), "Accounts_Log_Report.csv");
            try (PrintWriter writer = new PrintWriter(exportFile)) {
                writer.println("Receipt Number,Date,Student Name,Amount,Account,Payment Mode,Status");
                for (FeeReceipt r : filteredList) {
                    String receiptNo = r.getReceiptNumber() != null ? r.getReceiptNumber() : "";
                    String date = r.getReceiptDate() != null ? r.getReceiptDate().toString() : "";
                    String student = r.getStudent() != null ? r.getStudent().getName() : "N/A";
                    String amount = r.getTotalAmount() != null ? r.getTotalAmount().toString() : "0.00";
                    String account = r.getBaseAccount() != null ? r.getBaseAccount() : "";
                    String mode = r.getPaymentMode() != null ? r.getPaymentMode() : "";
                    String status = r.getStatus() != null ? r.getStatus() : "";

                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                            receiptNo, date, student, amount, account, mode, status);
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Accounts Log exported successfully to:\n" + exportFile.getAbsolutePath(), ButtonType.OK);
            alert.setTitle("Export Success");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to export report: " + e.getMessage(), ButtonType.OK);
            alert.setTitle("Export Error");
            alert.showAndWait();
        }
    }

    @FXML
    private void handlePrint() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Print Accounts Log");
        info.setHeaderText("Accounts Log Summary Report");
        info.setContentText("Total Receipts: " + filteredList.size() + "\n" +
                "Filtered Sum: " + filteredTotalLabel.getText() + "\n\n" +
                "Ready to send to system printer.");
        info.showAndWait();
    }

    private void showReceiptDetails(FeeReceipt receipt) {
        if (receipt == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Receipt Details - " + receipt.getReceiptNumber());
        dialog.setHeaderText("NSCET CMS - Fee Receipt Breakdown");

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 25, 20, 25));

        int row = 0;
        grid.add(new Label("Receipt Number:"), 0, row);
        Label receiptVal = new Label(receipt.getReceiptNumber());
        receiptVal.setStyle("-fx-font-weight: bold;");
        grid.add(receiptVal, 1, row++);

        grid.add(new Label("Receipt Date:"), 0, row);
        grid.add(new Label(receipt.getReceiptDate() != null ? receipt.getReceiptDate().toString() : "-"), 1, row++);

        grid.add(new Label("Student Name:"), 0, row);
        grid.add(new Label(receipt.getStudent() != null ? receipt.getStudent().getName() : "N/A"), 1, row++);

        grid.add(new Label("Roll Number:"), 0, row);
        grid.add(new Label(receipt.getStudent() != null ? receipt.getStudent().getRollNumber() : "N/A"), 1, row++);

        grid.add(new Label("Base Account:"), 0, row);
        grid.add(new Label(receipt.getBaseAccount() != null ? receipt.getBaseAccount() : "General"), 1, row++);

        grid.add(new Label("Payment Mode:"), 0, row);
        grid.add(new Label(receipt.getPaymentMode() != null ? receipt.getPaymentMode() : "CASH"), 1, row++);

        grid.add(new Label("Total Amount:"), 0, row);
        Label amtVal = new Label("Rs. " + (receipt.getTotalAmount() != null ? String.format("%,.2f", receipt.getTotalAmount()) : "0.00"));
        amtVal.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a237e; -fx-font-size: 14px;");
        grid.add(amtVal, 1, row++);

        grid.add(new Label("Status:"), 0, row);
        grid.add(new Label(receipt.getStatus() != null ? receipt.getStatus() : "ACTIVE"), 1, row++);

        if (receipt.getItems() != null && !receipt.getItems().isEmpty()) {
            VBox itemsBox = new VBox(5);
            itemsBox.setStyle("-fx-padding: 8; -fx-background-color: #f5f5f5; -fx-background-radius: 4;");
            itemsBox.getChildren().add(new Label("Fee Breakdown Items:"));
            for (FeeReceiptItem item : receipt.getItems()) {
                String name = item.getFeesName() != null ? item.getFeesName().getName() : "Fee Item";
                String itemAmt = item.getAmount() != null ? "Rs." + item.getAmount() : "Rs.0";
                itemsBox.getChildren().add(new Label("  • " + name + ": " + itemAmt));
            }
            grid.add(itemsBox, 0, row, 2, 1);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }
}
