package com.nscet.cms.ui.controller;

import com.nscet.cms.core.security.SecurityUtil;
import com.nscet.cms.core.service.AuditService;
import com.nscet.cms.core.service.DepartmentService;
import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.DepartmentMaster;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StudentMasterController implements Initializable {
    @FXML private TableView<StudentMaster> table;
    @FXML private TableColumn<StudentMaster, String> rollCol, nameCol, fatherCol, genderCol, phoneCol, admNoCol, actionsCol;
    @FXML private TextField searchField, admNoField, regField, nameField, phoneField, emailField;
    @FXML private TextField aadharField, fatherField, motherField, parentPhoneField;
    @FXML private TextField casteField, cityField, pinCodeField;
    @FXML private TextField fatherOccupationField, motherOccupationField, address1Field, address2Field;
    @FXML private TextField tenthMarkField, hscMarkField, cutOffField, yearOfPassingField, boardOfStudyField;
    @FXML private ComboBox<String> genderCombo, communityCombo, mediumCombo, religionCombo, degreeCombo, qualifyingExamCombo;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private DatePicker dobPicker, dojPicker;
    @FXML private VBox formPane;
    @FXML private TabPane formTabs;
    @FXML private Label pageInfo, statusDate;
    @FXML private Button prevBtn, nextBtn;
    @FXML private Button headerAddBtn, headerModifyBtn, headerDeleteBtn, headerCloseBtn;

    @FXML private Label semTitleLabel;
    @FXML private Label semBacklogLabel;
    @FXML private Label lblSemTuition;
    @FXML private Label lblSemOther;
    @FXML private Label lblSemBus;
    @FXML private Label lblSemPaid;

    @Autowired private StudentService service;
    @Autowired private DepartmentService departmentService;
    @Autowired(required = false) private AuditService auditService;
    private ObservableList<StudentMaster> tableData = FXCollections.observableArrayList();
    private ObservableList<DepartmentMaster> deptList = FXCollections.observableArrayList();
    private int currentPage = 0; private int pageSize = 20; private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rollCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber()));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        fatherCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFatherName()));
        genderCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGender()));
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(SecurityUtil.maskPhone(c.getValue().getPhone())));
        admNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionNo()));
        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    StudentMaster s = getTableView().getItems().get(getIndex());
                    Button edit = new Button("Edit"); edit.getStyleClass().add("btn-sm");
                    edit.setOnAction(e -> handleEdit(s));
                    Button del = new Button("Delete"); del.getStyleClass().add("btn-sm-danger");
                    del.setOnAction(e -> handleDelete(s));
                    setGraphic(new HBox(5, edit, del));
                }
            }
        });

        genderCombo.getItems().add("Select");
        genderCombo.getItems().addAll("Male", "Female", "Other");
        genderCombo.getSelectionModel().selectFirst();

        communityCombo.setEditable(true);
        communityCombo.getItems().add("Select");
        communityCombo.getItems().addAll("OC", "BC", "BCM", "MBC", "OBC", "DNC", "SC", "ST", "Others");
        communityCombo.getSelectionModel().selectFirst();

        religionCombo.setEditable(true);
        religionCombo.getItems().add("Select");
        religionCombo.getItems().addAll("Hindu", "Muslim", "Christian", "Others");
        religionCombo.getSelectionModel().selectFirst();

        mediumCombo.getItems().add("Select");
        mediumCombo.getItems().addAll("English", "Tamil");
        mediumCombo.getSelectionModel().selectFirst();

        qualifyingExamCombo.getItems().add("Select");
        qualifyingExamCombo.getItems().addAll("HSC(A)", "HSC(B)");
        qualifyingExamCombo.getSelectionModel().selectFirst();

        degreeCombo.getItems().add("Select");
        degreeCombo.getItems().addAll("B.E", "M.E");
        degreeCombo.getSelectionModel().selectFirst();

        deptList.addAll(departmentService.getAllActive()); 
        deptCombo.getItems().add(null);
        deptCombo.getItems().addAll(deptList);
        deptCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(DepartmentMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
        deptCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(DepartmentMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });

        setupEditableCombo(communityCombo);
        setupEditableCombo(religionCombo);

        if (statusDate != null) {
            statusDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        table.setItems(tableData);
        loadData();
    }

    private void loadData() {
        try {
            Page<StudentMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
            tableData.clear();
            tableData.addAll(page.getContent());
            int totalPages = Math.max(page.getTotalPages(), 1);
            if (pageInfo != null) {
                pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            }
            if (prevBtn != null) prevBtn.setDisable(currentPage == 0);
            if (nextBtn != null) nextBtn.setDisable(currentPage >= totalPages - 1);
        } catch (Exception e) {
            System.err.println("[StudentMasterController] loadData error: " + e.getMessage());
        }
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null;
        clearForm();
        showForm(true);
    }

    @FXML private void handleModify() {
        StudentMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a student to modify.").showAndWait();
            return;
        }
        handleEdit(selected);
    }

    @FXML private void handleDeleteSelected() {
        StudentMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a student to delete.").showAndWait();
            return;
        }
        handleDelete(selected);
    }

    @FXML private void handleClose() {
        showForm(false);
    }

    @FXML private void handleEdit(StudentMaster s) {
        editingId = s.getId();
        admNoField.setText(s.getAdmissionNo()); regField.setText(s.getRegistrationNo());
        nameField.setText(s.getName()); phoneField.setText(s.getPhone());
        emailField.setText(s.getEmail()); aadharField.setText(s.getAadharNumber());
        fatherField.setText(s.getFatherName()); motherField.setText(s.getMotherName());
        parentPhoneField.setText(s.getParentPhone());
        casteField.setText(s.getCaste()); cityField.setText(s.getCity());
        pinCodeField.setText(s.getState()); address1Field.setText(s.getAddress());
        address2Field.setText(s.getRegion());
        fatherOccupationField.setText(s.getOccupation());
        genderCombo.setValue(s.getGender()); communityCombo.setValue(s.getCommunity());
        mediumCombo.setValue(s.getMedium()); religionCombo.setValue(s.getReligion());
        degreeCombo.setValue(s.getAdmissionType());
        qualifyingExamCombo.getSelectionModel().selectFirst();
        dobPicker.setValue(s.getDateOfBirth()); dojPicker.setValue(s.getDateOfJoining());

        DepartmentMaster dept = null;
        for (DepartmentMaster d : deptList) {
            if (d.getId() != null && d.getId().equals(s.getId())) {
                dept = d;
                break;
            }
        }
        deptCombo.setValue(dept);

        tenthMarkField.setText(""); hscMarkField.setText(""); cutOffField.setText("");
        yearOfPassingField.setText(""); boardOfStudyField.setText("");

        showForm(true);
    }

    @FXML private void handleSave() {
        try {
            if (!validateInput()) return;
            StudentMaster s = new StudentMaster();
            s.setAdmissionNo(SecurityUtil.sanitize(admNoField.getText()));
            s.setRegistrationNo(SecurityUtil.sanitize(regField.getText()));
            s.setName(SecurityUtil.sanitize(nameField.getText()));
            s.setPhone(SecurityUtil.sanitize(phoneField.getText()));
            s.setEmail(SecurityUtil.sanitize(emailField.getText()));
            s.setAadharNumber(SecurityUtil.sanitize(aadharField.getText()));
            s.setFatherName(SecurityUtil.sanitize(fatherField.getText()));
            s.setMotherName(SecurityUtil.sanitize(motherField.getText()));
            s.setParentPhone(SecurityUtil.sanitize(parentPhoneField.getText()));
            s.setCaste(SecurityUtil.sanitize(casteField.getText()));
            s.setCity(SecurityUtil.sanitize(cityField.getText()));
            s.setState(SecurityUtil.sanitize(pinCodeField.getText()));
            s.setAddress(SecurityUtil.sanitize(address1Field.getText()));
            s.setRegion(SecurityUtil.sanitize(address2Field.getText()));
            s.setOccupation(SecurityUtil.sanitize(fatherOccupationField.getText()));
            s.setGender(genderCombo.getValue());
            s.setCommunity(communityCombo.getValue());
            s.setMedium(mediumCombo.getValue());
            s.setReligion(religionCombo.getValue());
            s.setAdmissionType(degreeCombo.getValue());
            s.setDateOfBirth(dobPicker.getValue());
            s.setDateOfJoining(dojPicker.getValue());

            if (editingId != null) {
                service.update(editingId, s);
                safeAuditLog("UPDATE", "admin_student_master", editingId, s.getName());
            } else {
                service.create(s);
                safeAuditLog("CREATE", "admin_student_master", s.getId(), s.getName());
            }
            showForm(false); loadData();
        } catch (Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); }
    }

    private void clearForm() {
        admNoField.clear(); regField.clear(); nameField.clear(); phoneField.clear();
        emailField.clear(); aadharField.clear(); fatherField.clear(); motherField.clear();
        parentPhoneField.clear(); casteField.clear(); cityField.clear(); pinCodeField.clear();
        address1Field.clear(); address2Field.clear(); fatherOccupationField.clear();
        motherOccupationField.clear(); tenthMarkField.clear(); hscMarkField.clear();
        cutOffField.clear(); yearOfPassingField.clear(); boardOfStudyField.clear();
        qualifyingExamCombo.getSelectionModel().selectFirst();
        genderCombo.getSelectionModel().selectFirst(); communityCombo.getSelectionModel().selectFirst();
        mediumCombo.getSelectionModel().selectFirst(); religionCombo.getSelectionModel().selectFirst();
        degreeCombo.getSelectionModel().selectFirst();
        qualifyingExamCombo.getSelectionModel().selectFirst();
        deptCombo.getSelectionModel().clearSelection();
        dobPicker.setValue(null); dojPicker.setValue(null);
        formTabs.getSelectionModel().selectFirst();
    }

    private boolean validateInput() {
        if (admNoField.getText().trim().isEmpty()) {
            showError("Admission No is required"); return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            showError("Student Name is required"); return false;
        }
        if (!SecurityUtil.isValidPhone(phoneField.getText().trim())) {
            showError("Phone must be exactly 10 digits"); return false;
        }
        if (!SecurityUtil.isValidPhone(parentPhoneField.getText().trim())) {
            showError("Parent phone must be exactly 10 digits"); return false;
        }
        if (!SecurityUtil.isValidAadhar(aadharField.getText().trim())) {
            showError("Aadhar must be exactly 12 digits"); return false;
        }
        if (!SecurityUtil.isValidEmail(emailField.getText().trim())) {
            showError("Invalid email format"); return false;
        }
        return true;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML private void handleCancel() { showForm(false); }

    private void showForm(boolean show) {
        formPane.setVisible(show); formPane.setManaged(show);
        headerAddBtn.setVisible(!show); headerAddBtn.setManaged(!show);
        headerModifyBtn.setVisible(!show); headerModifyBtn.setManaged(!show);
        headerDeleteBtn.setVisible(!show); headerDeleteBtn.setManaged(!show);
        headerCloseBtn.setVisible(!show); headerCloseBtn.setManaged(!show);
    }

    private void setupEditableCombo(ComboBox<String> combo) {
        combo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Others".equals(newVal)) {
                combo.getSelectionModel().clearSelection();
                combo.getEditor().clear();
                combo.getEditor().setPromptText("Type here...");
            }
        });
    }

    private void handleDelete(StudentMaster s) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION); c.setContentText("Delete student: " + s.getName() + "?");
        c.showAndWait().ifPresent(r -> { if (r == ButtonType.OK) {
            service.softDelete(s.getId());
            safeAuditLog("DELETE", "admin_student_master", s.getId(), s.getName());
            loadData();
        } });
    }

    private void safeAuditLog(String action, String tableName, Long recordId, String details) {
        try {
            if (auditService != null) {
                auditService.log(action, tableName, recordId, null, details, null);
            }
        } catch (Exception ignored) {}
    }

    @FXML private void handleSem1() { updateSemPreview(1, "2024-25 ODD", "Section A", 25000, 15000, 8000, 48000, 0); }
    @FXML private void handleSem2() { updateSemPreview(2, "2024-25 EVEN", "Section A", 25000, 15000, 8000, 48000, 0); }
    @FXML private void handleSem3() { updateSemPreview(3, "2025-26 ODD", "Section A", 25000, 15000, 8000, 43000, 5000); }
    @FXML private void handleSem4() { updateSemPreview(4, "2025-26 EVEN", "Section A", 25000, 15000, 8000, 20000, 28000); }
    @FXML private void handleSem5() { updateSemPreview(5, "2026-27 ODD", "Section A", 25000, 15000, 8000, 0, 48000); }
    @FXML private void handleSem6() { updateSemPreview(6, "2026-27 EVEN", "Section A", 25000, 15000, 8000, 0, 48000); }
    @FXML private void handleSem7() { updateSemPreview(7, "2027-28 ODD", "Section A", 25000, 15000, 8000, 0, 48000); }
    @FXML private void handleSem8() { updateSemPreview(8, "2027-28 EVEN", "Section A", 25000, 15000, 8000, 0, 48000); }

    private void updateSemPreview(int sem, String yearType, String sec, double tuition, double other, double bus, double paid, double backlog) {
        if (semTitleLabel == null) return;
        semTitleLabel.setText("SEMESTER " + sem + " PREVIEW (" + yearType + " - " + sec + ")");
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
