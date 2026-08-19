package com.nscet.cms.ui.controller;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.security.SecurityUtil;
import com.nscet.cms.core.service.*;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.QuotaMaster;
import com.nscet.cms.db.entity.StudentDetails;
import com.nscet.cms.db.entity.StudentMaster;
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
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StudentDetailsController implements Initializable {

    @FXML private TableView<StudentDetails> table;
    @FXML private TableColumn<StudentDetails, String> deptCol;
    @FXML private TableColumn<StudentDetails, String> rollNoCol;
    @FXML private TableColumn<StudentDetails, String> regNoCol;
    @FXML private TableColumn<StudentDetails, String> nameCol;
    @FXML private TableColumn<StudentDetails, String> fatherCol;
    @FXML private TableColumn<StudentDetails, String> phoneCol;
    @FXML private TableColumn<StudentDetails, String> admNoCol;
    @FXML private TableColumn<StudentDetails, String> actionsCol;
    @FXML private TextField searchField;
    @FXML private TextField rollNoField;
    @FXML private TextField studentNameField;
    @FXML private TextField regNoField;
    @FXML private TextField busStopField;
    @FXML private TextField busRouteField;
    @FXML private ComboBox<String> casteCategoryCombo;
    @FXML private ComboBox<String> hostelCombo;
    @FXML private ComboBox<String> stateCombo;
    @FXML private ComboBox<String> academicYearCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<String> transportTypeCombo;
    @FXML private ComboBox<String> semTypeCombo;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private ComboBox<QuotaMaster> quotaCombo;
    @FXML private VBox formPane;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    @Autowired private StudentService studentService;
    @Autowired private StudentDetailsService studentDetailsService;
    @Autowired private DepartmentService departmentService;
    @Autowired private QuotaService quotaService;
    @Autowired(required = false) private AuditService auditService;

    private ObservableList<StudentDetails> tableData = FXCollections.observableArrayList();
    private ObservableList<DepartmentMaster> deptList = FXCollections.observableArrayList();
    private ObservableList<QuotaMaster> quotaList = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupDeptCombo();
        setupQuotaCombo();
        setupComboOptions();
        table.setItems(tableData);
        loadData();

        rollNoField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                lookupStudent();
            }
        });
    }

    private void setupTableColumns() {
        deptCol.setCellValueFactory(c -> {
            StudentDetails sd = c.getValue();
            return new SimpleStringProperty(sd.getDepartment() != null ? sd.getDepartment().getName() : "");
        });
        rollNoCol.setCellValueFactory(c -> {
            StudentDetails sd = c.getValue();
            return new SimpleStringProperty(sd.getStudent() != null ? sd.getStudent().getRollNumber() : "");
        });
        regNoCol.setCellValueFactory(c -> {
            StudentDetails sd = c.getValue();
            return new SimpleStringProperty(sd.getStudent() != null ? sd.getStudent().getRegistrationNo() : "");
        });
        nameCol.setCellValueFactory(c -> {
            StudentDetails sd = c.getValue();
            return new SimpleStringProperty(sd.getStudent() != null ? sd.getStudent().getName() : "");
        });
        fatherCol.setCellValueFactory(c -> {
            StudentDetails sd = c.getValue();
            return new SimpleStringProperty(sd.getStudent() != null ? sd.getStudent().getFatherName() : "");
        });
        phoneCol.setCellValueFactory(c -> {
            StudentDetails sd = c.getValue();
            return new SimpleStringProperty(sd.getStudent() != null
                    ? SecurityUtil.maskPhone(sd.getStudent().getPhone()) : "");
        });
        admNoCol.setCellValueFactory(c -> {
            StudentDetails sd = c.getValue();
            return new SimpleStringProperty(sd.getStudent() != null ? sd.getStudent().getAdmissionNo() : "");
        });
        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    StudentDetails sd = getTableView().getItems().get(getIndex());
                    Button edit = new Button("Edit");
                    edit.getStyleClass().add("btn-sm");
                    edit.setOnAction(e -> handleEdit(sd));
                    Button del = new Button("Delete");
                    del.getStyleClass().add("btn-sm-danger");
                    del.setOnAction(e -> handleDelete(sd));
                    setGraphic(new HBox(5, edit, del));
                }
            }
        });
    }

    private void setupDeptCombo() {
        deptList.addAll(departmentService.getAllActive());
        deptCombo.getItems().add(null);
        deptCombo.getItems().addAll(deptList);
        deptCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(DepartmentMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
        deptCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DepartmentMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
    }

    private void setupQuotaCombo() {
        quotaList.addAll(quotaService.getAllActive());
        quotaCombo.getItems().add(null);
        quotaCombo.getItems().addAll(quotaList);
        quotaCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(QuotaMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
        quotaCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(QuotaMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
    }

    private void setupComboOptions() {
        casteCategoryCombo.getItems().addAll("BC", "MBC", "SC", "ST", "OC", "DNC", "DNT", "BCM", "Others");
        casteCategoryCombo.getSelectionModel().selectFirst();

        hostelCombo.getItems().addAll("Yes", "No");
        hostelCombo.getSelectionModel().selectFirst();

        stateCombo.getItems().addAll("Own", "Other");
        stateCombo.getSelectionModel().selectFirst();

        academicYearCombo.getItems().addAll(
                "2021-22", "2022-23", "2023-24", "2024-25", "2025-26", "2026-27"
        );
        academicYearCombo.getSelectionModel().selectFirst();

        semesterCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8");
        semesterCombo.getSelectionModel().selectFirst();

        transportTypeCombo.getItems().addAll("College", "Own");
        transportTypeCombo.getSelectionModel().selectFirst();

        semTypeCombo.getItems().addAll("Even", "Odd");
        semTypeCombo.getSelectionModel().selectFirst();
    }

    private void lookupStudent() {
        String rollNo = rollNoField.getText();
        if (rollNo == null || rollNo.trim().isEmpty()) {
            return;
        }
        try {
            StudentMaster student = studentService.getByRollNumber(rollNo.trim());
            studentNameField.setText(student.getName());
            regNoField.setText(student.getRegistrationNo());
        } catch (Exception e) {
            studentNameField.clear();
            regNoField.clear();
        }
    }

    private void loadData() {
        Page<StudentDetails> page = studentDetailsService.getAll(
                searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear();
        tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d", currentPage + 1,
                Math.max(page.getTotalPages(), 1)));
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    @FXML
    private void handleSearch() {
        currentPage = 0;
        loadData();
    }

    @FXML
    private void handlePrevious() {
        currentPage--;
        loadData();
    }

    @FXML
    private void handleNext() {
        currentPage++;
        loadData();
    }

    @FXML
    private void handleAdd() {
        editingId = null;
        clearForm();
        showForm(true);
    }

    @FXML
    private void handleModify() {
        StudentDetails selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a record to modify.").showAndWait();
            return;
        }
        handleEdit(selected);
    }

    @FXML
    private void handleDeleteSelected() {
        StudentDetails selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a record to delete.").showAndWait();
            return;
        }
        handleDelete(selected);
    }

    @FXML
    private void handleClose() {
        showForm(false);
    }

    @FXML
    private void handleEdit(StudentDetails sd) {
        editingId = sd.getId();
        if (sd.getStudent() != null) {
            rollNoField.setText(sd.getStudent().getRollNumber());
            studentNameField.setText(sd.getStudent().getName());
            regNoField.setText(sd.getStudent().getRegistrationNo());
        }
        semesterCombo.setValue(String.valueOf(sd.getSemester()));
        casteCategoryCombo.setValue(sd.getCasteCategory());
        busStopField.setText(sd.getBusStop());
        hostelCombo.setValue(sd.getHostel());
        transportTypeCombo.setValue(sd.getTransportType());
        stateCombo.setValue(sd.getState());
        semTypeCombo.setValue(sd.getSemType());
        academicYearCombo.setValue(sd.getAcademicYear());

        if (sd.getDepartment() != null) {
            for (DepartmentMaster d : deptList) {
                if (d.getId() != null && d.getId().equals(sd.getDepartment().getId())) {
                    deptCombo.setValue(d);
                    break;
                }
            }
        }

        if (sd.getQuota() != null) {
            for (QuotaMaster q : quotaList) {
                if (q.getId() != null && q.getId().equals(sd.getQuota().getId())) {
                    quotaCombo.setValue(q);
                    break;
                }
            }
        }

        showForm(true);
    }

    @FXML
    private void handleSave() {
        try {
            if (!validateInput()) return;

            StudentMaster student = studentService.getByRollNumber(rollNoField.getText().trim());

            StudentDetails sd = new StudentDetails();
            sd.setStudent(student);
            sd.setSemester(Integer.parseInt(semesterCombo.getValue()));
            sd.setCasteCategory(casteCategoryCombo.getValue());
            sd.setBusStop(SecurityUtil.sanitize(busStopField.getText()));
            sd.setHostel(hostelCombo.getValue());
            sd.setTransportType(transportTypeCombo.getValue());
            sd.setState(stateCombo.getValue());
            sd.setSemType(semTypeCombo.getValue());
            sd.setAcademicYear(academicYearCombo.getValue());
            sd.setDepartment(deptCombo.getValue());
            sd.setQuota(quotaCombo.getValue());

            if (editingId != null) {
                studentDetailsService.update(editingId, sd);
                safeAuditLog("UPDATE", "admin_student_details", editingId,
                        student.getRollNumber());
            } else {
                studentDetailsService.create(sd);
                safeAuditLog("CREATE", "admin_student_details", sd.getId(),
                        student.getRollNumber());
            }
            showForm(false);
            loadData();
        } catch (DuplicateResourceException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        showForm(false);
    }

    private void handleDelete(StudentDetails sd) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete record for: "
                + (sd.getStudent() != null ? sd.getStudent().getName() : "Unknown") + "?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                studentDetailsService.softDelete(sd.getId());
                safeAuditLog("DELETE", "admin_student_details", sd.getId(),
                        sd.getStudent() != null ? sd.getStudent().getRollNumber() : "");
                loadData();
            }
        });
    }

    private void clearForm() {
        rollNoField.clear();
        studentNameField.clear();
        regNoField.clear();
        busStopField.clear();
        busRouteField.clear();
        casteCategoryCombo.getSelectionModel().selectFirst();
        hostelCombo.getSelectionModel().selectFirst();
        stateCombo.getSelectionModel().selectFirst();
        academicYearCombo.getSelectionModel().selectFirst();
        semesterCombo.getSelectionModel().selectFirst();
        transportTypeCombo.getSelectionModel().selectFirst();
        semTypeCombo.getSelectionModel().selectFirst();
        deptCombo.getSelectionModel().clearSelection();
        quotaCombo.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (rollNoField.getText() == null || rollNoField.getText().trim().isEmpty()) {
            showError("Roll No is required");
            return false;
        }
        try {
            studentService.getByRollNumber(rollNoField.getText().trim());
        } catch (Exception e) {
            showError("No student found with Roll No: " + rollNoField.getText().trim());
            return false;
        }
        if (semesterCombo.getValue() == null || semesterCombo.getValue().isEmpty()) {
            showError("Semester is required");
            return false;
        }
        if (academicYearCombo.getValue() == null || academicYearCombo.getValue().isEmpty()) {
            showError("Academic Year is required");
            return false;
        }
        return true;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showForm(boolean show) {
        formPane.setVisible(show);
        formPane.setManaged(show);
    }

    private void safeAuditLog(String action, String tableName, Long recordId, String details) {
        try {
            if (auditService != null) {
                auditService.log(action, tableName, recordId, null, details, null);
            }
        } catch (Exception e) {
            System.err.println("[StudentDetails] Audit log failed: " + e.getMessage());
        }
    }
}
