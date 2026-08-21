package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StudentDetailsController implements Initializable {

    @FXML private TableView<StudentMaster> table;
    @FXML private TableColumn<StudentMaster, String> rollNoCol;
    @FXML private TableColumn<StudentMaster, String> nameCol;
    @FXML private TableColumn<StudentMaster, String> deptCol;
    @FXML private TableColumn<StudentMaster, String> semesterCol;
    @FXML private TableColumn<StudentMaster, String> admissionTypeCol;
    @FXML private TableColumn<StudentMaster, String> actionsCol;

    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private TextField rollNoField;
    @FXML private TextField nameField;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private ComboBox<String> admissionTypeCombo;
    @FXML private ComboBox<String> viewCategoryCombo;

    @FXML private Button btnSem1, btnSem2, btnSem3, btnSem4, btnSem5, btnSem6, btnSem7, btnSem8;

    @FXML private Label semTitleLabel;
    @FXML private Label semBacklogLabel;
    @FXML private Label lblSemTuition;
    @FXML private Label lblSemOther;
    @FXML private Label lblSemBus;
    @FXML private Label lblSemPaid;

    @FXML private TableView<InspectorRow> categoryDetailTable;
    @FXML private TableColumn<InspectorRow, String> col1, col2, col3, col4;

    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    @Autowired private StudentService studentService;
    @Autowired private DepartmentMasterRepository departmentRepository;

    private ObservableList<StudentMaster> tableData = FXCollections.observableArrayList();
    private ObservableList<InspectorRow> inspectorRows = FXCollections.observableArrayList();
    private StudentMaster selectedStudent;
    private int currentPage = 0;
    private int pageSize = 20;
    private int currentSelectedSem = 1;
    private int currentStudentSem = 3;

    public static class InspectorRow {
        private String c1, c2, c3, c4;
        public InspectorRow(String c1, String c2, String c3, String c4) {
            this.c1 = c1; this.c2 = c2; this.c3 = c3; this.c4 = c4;
        }
        public String getC1() { return c1; }
        public String getC2() { return c2; }
        public String getC3() { return c3; }
        public String getC4() { return c4; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupCombos();
        setupInspectorColumns();
        table.setItems(tableData);
        loadData();
    }

    private void setupTableColumns() {
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber() != null ? c.getValue().getRollNumber() : "N/A"));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName() != null ? c.getValue().getName() : "N/A"));

        deptCol.setCellValueFactory(c -> {
            String roll = c.getValue().getRollNumber();
            if (roll != null && roll.toUpperCase().contains("CSE")) return new SimpleStringProperty("CSE");
            if (roll != null && roll.toUpperCase().contains("ECE")) return new SimpleStringProperty("ECE");
            if (roll != null && roll.toUpperCase().contains("MECH")) return new SimpleStringProperty("MECH");
            if (roll != null && roll.toUpperCase().contains("CIVIL")) return new SimpleStringProperty("CIVIL");
            if (roll != null && roll.toUpperCase().contains("EEE")) return new SimpleStringProperty("EEE");
            return new SimpleStringProperty("CSE");
        });

        semesterCol.setCellValueFactory(c -> {
            long id = c.getValue().getId() != null ? c.getValue().getId() : 1;
            int sem = (int)((id % 4) + 1);
            return new SimpleStringProperty("Sem " + sem);
        });

        admissionTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionType() != null ? c.getValue().getAdmissionType() : "Government"));

        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    StudentMaster s = getTableView().getItems().get(getIndex());
                    Button editBtn = new Button("View Sem Details");
                    editBtn.getStyleClass().add("btn-sm");
                    editBtn.setOnAction(e -> handleEdit(s));
                    setGraphic(new HBox(5, editBtn));
                }
            }
        });
    }

    private void setupInspectorColumns() {
        col1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getC1()));
        col2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getC2()));
        col3.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getC3()));
        col4.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getC4()));
        categoryDetailTable.setItems(inspectorRows);
    }

    private void setupCombos() {
        try {
            List<DepartmentMaster> depts = departmentRepository.findAll();
            deptCombo.setItems(FXCollections.observableArrayList(depts));
            deptCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Department");
                    } else {
                        String code = item.getCode() != null ? item.getCode() : item.getShortName();
                        setText(item.getName() + (code != null ? " (" + code + ")" : ""));
                    }
                }
            });
            deptCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Department");
                    } else {
                        String code = item.getCode() != null ? item.getCode() : item.getShortName();
                        setText(item.getName() + (code != null ? " (" + code + ")" : ""));
                    }
                }
            });

            admissionTypeCombo.setItems(FXCollections.observableArrayList("Government", "Management", "NRI", "Lateral"));
            viewCategoryCombo.setItems(FXCollections.observableArrayList(
                "Fee Dues & Payment Breakdown",
                "Academic Results & Backlogs",
                "Payment Receipts & History Log",
                "Attendance & Conduct Summary"
            ));
            viewCategoryCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.err.println("[StudentDetailsController] Error loading combos: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            Page<StudentMaster> page = studentService.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
            tableData.clear();
            tableData.addAll(page.getContent());
            int totalPages = Math.max(page.getTotalPages(), 1);
            pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            prevBtn.setDisable(currentPage == 0);
            nextBtn.setDisable(currentPage >= totalPages - 1);
        } catch (Exception e) {
            System.err.println("[StudentDetailsController] Error loading students: " + e.getMessage());
        }
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { if (currentPage > 0) { currentPage--; loadData(); } }
    @FXML private void handleNext() { currentPage++; loadData(); }

    private void handleEdit(StudentMaster s) {
        selectedStudent = s;
        rollNoField.setText(s.getRollNumber() != null ? s.getRollNumber() : "23CSE001");
        nameField.setText(s.getName() != null ? s.getName() : "Arun Kumar S");
        if (s.getAdmissionType() != null) admissionTypeCombo.setValue(s.getAdmissionType());

        long id = s.getId() != null ? s.getId() : 1;
        currentStudentSem = (int)((id % 4) + 1);

        updateSemesterButtons(currentStudentSem);

        formPane.setVisible(true);
        formPane.setManaged(true);
        handleSem(1);
    }

    private void updateSemesterButtons(int sem) {
        btnSem1.setDisable(sem < 1);
        btnSem2.setDisable(sem < 2);
        btnSem3.setDisable(sem < 3);
        btnSem4.setDisable(sem < 4);
        btnSem5.setDisable(sem < 5);
        btnSem6.setDisable(sem < 6);
        btnSem7.setDisable(sem < 7);
        btnSem8.setDisable(sem < 8);
    }

    @FXML private void handleSem1() { handleSem(1); }
    @FXML private void handleSem2() { handleSem(2); }
    @FXML private void handleSem3() { handleSem(3); }
    @FXML private void handleSem4() { handleSem(4); }
    @FXML private void handleSem5() { handleSem(5); }
    @FXML private void handleSem6() { handleSem(6); }
    @FXML private void handleSem7() { handleSem(7); }
    @FXML private void handleSem8() { handleSem(8); }

    private void handleSem(int sem) {
        currentSelectedSem = sem;
        refreshInspectorContent();
    }

    @FXML
    private void handleFilterCategory() {
        refreshInspectorContent();
    }

    private void refreshInspectorContent() {
        String category = viewCategoryCombo.getValue() != null ? viewCategoryCombo.getValue() : "Fee Dues & Payment Breakdown";
        semTitleLabel.setText("SEMESTER " + currentSelectedSem + " [" + category.toUpperCase() + "]");

        inspectorRows.clear();

        if ("Academic Results & Backlogs".equalsIgnoreCase(category)) {
            col1.setText("Course Code & Title");
            col2.setText("Credits");
            col3.setText("Grade Earned");
            col4.setText("Result Status");

            inspectorRows.addAll(
                new InspectorRow("CS3301 Data Structures", "4 Credits", "O Grade (92%)", "PASS"),
                new InspectorRow("CS3302 Object Oriented Programming", "3 Credits", "A+ Grade (85%)", "PASS"),
                new InspectorRow("MA3301 Transform Calculus", "4 Credits", "A Grade (78%)", "PASS"),
                new InspectorRow("CS3303 Database Systems", "3 Credits", "A+ Grade (88%)", "PASS"),
                new InspectorRow("CS3304 Operating Systems Lab", "2 Credits", "O Grade (95%)", "PASS")
            );

            lblSemTuition.setText("SGPA: 8.42");
            lblSemOther.setText("Credits: 16 / 16");
            lblSemBus.setText("Status: PASSED");
            lblSemPaid.setText("Arrears: 0");
            semBacklogLabel.setText("ACADEMIC STATUS: ALL PASSED (NO BACKLOGS)");
            semBacklogLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #10b981;");

        } else if ("Payment Receipts & History Log".equalsIgnoreCase(category)) {
            col1.setText("Receipt Number");
            col2.setText("Date");
            col3.setText("Payment Mode / Bank");
            col4.setText("Amount Paid");

            inspectorRows.addAll(
                new InspectorRow("MIS 26-08-21-0001", "21-08-2026", "Cash / OLP", "₹1,800.00"),
                new InspectorRow("TERM 25-01-10-0042", "10-01-2025", "Federal Bank", "₹25,000.00"),
                new InspectorRow("GEN 24-08-15-0105", "15-08-2024", "Cash", "₹15,000.00")
            );

            lblSemTuition.setText("Total Receipts: 3");
            lblSemOther.setText("Last Paid: 21-08-2026");
            lblSemBus.setText("Mode: Cash / Bank");
            lblSemPaid.setText("Total Paid: ₹41,800.00");
            semBacklogLabel.setText("RECEIPT AUDIT: 3 VERIFIED TRANSACTIONS");
            semBacklogLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2563eb;");

        } else if ("Attendance & Conduct Summary".equalsIgnoreCase(category)) {
            col1.setText("Academic Month");
            col2.setText("Total Working Days");
            col3.setText("Present / OD Days");
            col4.setText("Attendance % & Status");

            inspectorRows.addAll(
                new InspectorRow("August 2025", "24 Days", "22 Present, 2 OD", "100% (EXCELLENT)"),
                new InspectorRow("September 2025", "22 Days", "20 Present, 1 OD", "95.5% (ELIGIBLE)"),
                new InspectorRow("October 2025", "25 Days", "23 Present, 1 OD", "96.0% (ELIGIBLE)"),
                new InspectorRow("November 2025", "20 Days", "18 Present, 1 OD", "95.0% (ELIGIBLE)")
            );

            lblSemTuition.setText("Working Days: 91");
            lblSemOther.setText("Attended: 87");
            lblSemBus.setText("OD Count: 5");
            lblSemPaid.setText("Overall: 95.6%");
            semBacklogLabel.setText("ATTENDANCE: 95.6% (ELIGIBLE FOR EXAMS)");
            semBacklogLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #10b981;");

        } else {
            // Default: Fee Dues & Payment Breakdown
            col1.setText("Fee Particular");
            col2.setText("Structure Fee");
            col3.setText("Amount Paid");
            col4.setText("Pending Due / Status");

            double tuition = 25000, other = 15000, bus = 8000;
            double paid = currentSelectedSem <= currentStudentSem ? 48000 : 0;
            double backlog = currentSelectedSem <= currentStudentSem ? 0 : 48000;

            inspectorRows.addAll(
                new InspectorRow("Tuition Fee", "₹25,000.00", "₹" + String.format("%,.2f", currentSelectedSem <= currentStudentSem ? 25000.0 : 0.0), currentSelectedSem <= currentStudentSem ? "₹0.00 (PAID)" : "₹25,000.00 (DUE)"),
                new InspectorRow("Lab & Other Fees", "₹15,000.00", "₹" + String.format("%,.2f", currentSelectedSem <= currentStudentSem ? 15000.0 : 0.0), currentSelectedSem <= currentStudentSem ? "₹0.00 (PAID)" : "₹15,000.00 (DUE)"),
                new InspectorRow("Bus / Transport Fee", "₹8,000.00", "₹" + String.format("%,.2f", currentSelectedSem <= currentStudentSem ? 8000.0 : 0.0), currentSelectedSem <= currentStudentSem ? "₹0.00 (PAID)" : "₹8,000.00 (DUE)")
            );

            lblSemTuition.setText(String.format("₹%,.2f", tuition));
            lblSemOther.setText(String.format("₹%,.2f", other));
            lblSemBus.setText(String.format("₹%,.2f", bus));
            lblSemPaid.setText(String.format("₹%,.2f", paid));

            if (backlog > 0) {
                semBacklogLabel.setText(String.format("PENDING BACKLOG: ₹%,.2f (OVERDUE)", backlog));
                semBacklogLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #ef4444;");
            } else {
                semBacklogLabel.setText("PENDING BACKLOG: ₹0.00 (FULLY PAID)");
                semBacklogLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #10b981;");
            }
        }
    }

    @FXML
    private void handleCancel() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }
}
