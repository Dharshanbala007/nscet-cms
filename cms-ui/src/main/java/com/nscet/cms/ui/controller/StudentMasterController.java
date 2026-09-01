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
    @FXML private TableColumn<StudentMaster, String> rollCol, nameCol, fatherCol, genderCol, phoneCol, admNoCol;
    @FXML private TextField searchField, admNoField, regField, nameField, phoneField, emailField, sectionField;
    @FXML private TextField aadharField, fatherField, motherField, parentPhoneField;
    @FXML private TextField casteField, cityField, pinCodeField;
    @FXML private TextField fatherOccupationField, motherOccupationField, address1Field, address2Field;
    @FXML private TextField tenthMarkField, hscMarkField, cutOffField, yearOfPassingField, boardOfStudyField;
    @FXML private ComboBox<String> genderCombo, communityCombo, mediumCombo, religionCombo, degreeCombo, qualifyingExamCombo;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private DatePicker dobPicker, dojPicker;
    @FXML private VBox formPane;
    @FXML private Label pageInfo, statusDate;
    @FXML private Button prevBtn, nextBtn;

    @Autowired private StudentService service;
    @Autowired private DepartmentService departmentService;
    @Autowired(required = false) private AuditService auditService;
    private final ObservableList<StudentMaster> tableData = FXCollections.observableArrayList();
    private final ObservableList<DepartmentMaster> deptList = FXCollections.observableArrayList();
    private int currentPage = 0;
    private final int pageSize = 20;
    private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (rollCol != null) rollCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber()));
        if (nameCol != null) nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        if (fatherCol != null) fatherCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFatherName()));
        if (genderCol != null) genderCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGender()));
        if (phoneCol != null) phoneCol.setCellValueFactory(c -> new SimpleStringProperty(SecurityUtil.maskPhone(c.getValue().getPhone())));
        if (admNoCol != null) admNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionNo()));

        if (genderCombo != null) {
            genderCombo.getItems().setAll("Select", "Male", "Female", "Other");
            genderCombo.getSelectionModel().selectFirst();
        }

        if (communityCombo != null) {
            communityCombo.getItems().setAll("Select", "OC", "BC", "BC(M)", "MBC", "OBC", "DNC", "SC", "ST");
            communityCombo.getSelectionModel().selectFirst();
        }

        if (religionCombo != null) {
            religionCombo.getItems().setAll("Select", "Hindu", "Muslim", "Christian", "Others");
            religionCombo.getSelectionModel().selectFirst();
        }

        if (mediumCombo != null) {
            mediumCombo.getItems().setAll("Select", "English", "Tamil");
            mediumCombo.getSelectionModel().selectFirst();
        }

        if (qualifyingExamCombo != null) {
            qualifyingExamCombo.getItems().setAll("Select", "HSC (A)", "HSC (V)");
            qualifyingExamCombo.getSelectionModel().selectFirst();
        }

        if (degreeCombo != null) {
            degreeCombo.getItems().setAll("Select", "B.E", "M.E");
            degreeCombo.getSelectionModel().selectFirst();
        }

        if (deptCombo != null) {
            deptList.clear();
            deptList.addAll(departmentService.getAllActive());
            deptCombo.getItems().clear();
            deptCombo.getItems().add(null);
            deptCombo.getItems().addAll(deptList);
            deptCombo.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select" : (item.getShortName() != null ? item.getShortName() : item.getName()));
                }
            });
            deptCombo.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select" : (item.getShortName() != null ? item.getShortName() : item.getName()));
                }
            });
        }

        if (statusDate != null) {
            statusDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }

        if (table != null) {
            table.setItems(tableData);
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    handleEdit(newSel);
                }
            });
        }

        loadData();
    }

    private void loadData() {
        try {
            Page<StudentMaster> page = service.getAll(searchField != null ? searchField.getText() : "", currentPage, pageSize, "id", "asc");
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
    @FXML private void handlePrevious() { if (currentPage > 0) { currentPage--; loadData(); } }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null;
        clearForm();
    }

    @FXML private void handleModify() {
        if (table == null) return;
        StudentMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a student from the table to modify.").showAndWait();
            return;
        }
        handleEdit(selected);
    }

    @FXML private void handleDeleteSelected() {
        if (table == null) return;
        StudentMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a student from the table to delete.").showAndWait();
            return;
        }
        handleDelete(selected);
    }

    @FXML private void handleClose() {
        clearForm();
    }

    @FXML private void handleEdit(StudentMaster s) {
        editingId = s.getId();
        if (admNoField != null) admNoField.setText(s.getAdmissionNo());
        if (regField != null) regField.setText(s.getRegistrationNo());
        if (nameField != null) nameField.setText(s.getName());
        if (phoneField != null) phoneField.setText(s.getPhone());
        if (emailField != null) emailField.setText(s.getEmail());
        if (sectionField != null) sectionField.setText(s.getSection());
        if (aadharField != null) aadharField.setText(s.getAadharNumber());
        if (fatherField != null) fatherField.setText(s.getFatherName());
        if (motherField != null) motherField.setText(s.getMotherName());
        if (parentPhoneField != null) parentPhoneField.setText(s.getParentPhone());
        if (casteField != null) casteField.setText(s.getCaste());
        if (cityField != null) cityField.setText(s.getCity());
        if (pinCodeField != null) pinCodeField.setText(s.getState());
        if (address1Field != null) address1Field.setText(s.getAddress());
        if (address2Field != null) address2Field.setText(s.getRegion());
        if (fatherOccupationField != null) fatherOccupationField.setText(s.getOccupation());
        if (genderCombo != null) genderCombo.setValue(s.getGender() != null ? s.getGender() : "Select");
        if (communityCombo != null) communityCombo.setValue(s.getCommunity() != null ? s.getCommunity() : "Select");
        if (mediumCombo != null) mediumCombo.setValue(s.getMedium() != null ? s.getMedium() : "Select");
        if (religionCombo != null) religionCombo.setValue(s.getReligion() != null ? s.getReligion() : "Select");
        if (degreeCombo != null) degreeCombo.setValue(s.getAdmissionType() != null ? s.getAdmissionType() : "Select");
        if (dobPicker != null) dobPicker.setValue(s.getDateOfBirth());
        if (dojPicker != null) dojPicker.setValue(s.getDateOfJoining() != null ? s.getDateOfJoining() : LocalDate.now());

        if (deptCombo != null && s.getDepartment() != null) {
            DepartmentMaster match = deptCombo.getItems().stream()
                    .filter(d -> d != null && (
                            (s.getDepartment().getId() != null && s.getDepartment().getId().equals(d.getId())) ||
                            (d.getName() != null && d.getName().equalsIgnoreCase(s.getDepartment().getName())) ||
                            (d.getShortName() != null && d.getShortName().equalsIgnoreCase(s.getDepartment().getShortName()))
                    ))
                    .findFirst().orElse(s.getDepartment());
            deptCombo.setValue(match);
        }
    }

    @FXML private void handleSave() {
        try {
            if (!validateInput()) return;
            StudentMaster s = new StudentMaster();
            s.setAdmissionNo(SecurityUtil.sanitize(admNoField != null ? admNoField.getText() : ""));
            s.setRegistrationNo(SecurityUtil.sanitize(regField != null ? regField.getText() : ""));
            s.setName(SecurityUtil.sanitize(nameField != null ? nameField.getText() : ""));
            s.setPhone(SecurityUtil.sanitize(phoneField != null ? phoneField.getText() : ""));
            s.setEmail(SecurityUtil.sanitize(emailField != null ? emailField.getText() : ""));
            s.setSection(SecurityUtil.sanitize(sectionField != null ? sectionField.getText() : ""));
            s.setAadharNumber(SecurityUtil.sanitize(aadharField != null ? aadharField.getText() : ""));
            s.setFatherName(SecurityUtil.sanitize(fatherField != null ? fatherField.getText() : ""));
            s.setMotherName(SecurityUtil.sanitize(motherField != null ? motherField.getText() : ""));
            s.setParentPhone(SecurityUtil.sanitize(parentPhoneField != null ? parentPhoneField.getText() : ""));
            s.setCaste(SecurityUtil.sanitize(casteField != null ? casteField.getText() : ""));
            s.setCity(SecurityUtil.sanitize(cityField != null ? cityField.getText() : ""));
            s.setState(SecurityUtil.sanitize(pinCodeField != null ? pinCodeField.getText() : ""));
            s.setAddress(SecurityUtil.sanitize(address1Field != null ? address1Field.getText() : ""));
            s.setRegion(SecurityUtil.sanitize(address2Field != null ? address2Field.getText() : ""));
            s.setOccupation(SecurityUtil.sanitize(fatherOccupationField != null ? fatherOccupationField.getText() : ""));
            if (genderCombo != null) s.setGender(genderCombo.getValue());
            if (communityCombo != null) s.setCommunity(communityCombo.getValue());
            if (mediumCombo != null) s.setMedium(mediumCombo.getValue());
            if (religionCombo != null) s.setReligion(religionCombo.getValue());
            if (degreeCombo != null) s.setAdmissionType(degreeCombo.getValue());
            if (dobPicker != null) s.setDateOfBirth(dobPicker.getValue());
            if (dojPicker != null) s.setDateOfJoining(dojPicker.getValue());
            if (deptCombo != null) s.setDepartment(deptCombo.getValue());

            if (editingId != null) {
                StudentMaster existing = service.getById(editingId);
                s.setRollNumber(existing.getRollNumber());
                service.update(editingId, s);
                safeAuditLog("UPDATE", "admin_student_master", editingId, s.getName());
            } else {
                String rollNo = (s.getRegistrationNo() != null && !s.getRegistrationNo().isEmpty())
                    ? s.getRegistrationNo()
                    : ((s.getAdmissionNo() != null && !s.getAdmissionNo().isEmpty()) ? s.getAdmissionNo() : "STU" + (System.currentTimeMillis() % 100000));
                s.setRollNumber(rollNo);
                service.create(s);
                safeAuditLog("CREATE", "admin_student_master", s.getId(), s.getName());
            }
            clearForm();
            loadData();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void clearForm() {
        editingId = null;
        if (admNoField != null) admNoField.clear();
        if (regField != null) regField.clear();
        if (nameField != null) nameField.clear();
        if (phoneField != null) phoneField.clear();
        if (emailField != null) emailField.clear();
        if (sectionField != null) sectionField.clear();
        if (aadharField != null) aadharField.clear();
        if (fatherField != null) fatherField.clear();
        if (motherField != null) motherField.clear();
        if (parentPhoneField != null) parentPhoneField.clear();
        if (casteField != null) casteField.clear();
        if (cityField != null) cityField.clear();
        if (pinCodeField != null) pinCodeField.clear();
        if (address1Field != null) address1Field.clear();
        if (address2Field != null) address2Field.clear();
        if (fatherOccupationField != null) fatherOccupationField.clear();
        if (motherOccupationField != null) motherOccupationField.clear();
        if (tenthMarkField != null) tenthMarkField.clear();
        if (hscMarkField != null) hscMarkField.clear();
        if (cutOffField != null) cutOffField.clear();
        if (yearOfPassingField != null) yearOfPassingField.clear();
        if (boardOfStudyField != null) boardOfStudyField.clear();

        if (genderCombo != null) genderCombo.getSelectionModel().selectFirst();
        if (communityCombo != null) communityCombo.getSelectionModel().selectFirst();
        if (mediumCombo != null) mediumCombo.getSelectionModel().selectFirst();
        if (religionCombo != null) religionCombo.getSelectionModel().selectFirst();
        if (degreeCombo != null) degreeCombo.getSelectionModel().selectFirst();
        if (qualifyingExamCombo != null) qualifyingExamCombo.getSelectionModel().selectFirst();
        if (deptCombo != null) deptCombo.getSelectionModel().clearSelection();

        if (dobPicker != null) dobPicker.setValue(null);
        if (dojPicker != null) dojPicker.setValue(LocalDate.now());
        if (table != null) table.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (admNoField != null && admNoField.getText().trim().isEmpty() && regField != null && regField.getText().trim().isEmpty()) {
            showError("Admission No or Registration No is required"); return false;
        }
        if (nameField != null && nameField.getText().trim().isEmpty()) {
            showError("Student Name is required"); return false;
        }
        if (phoneField != null && !phoneField.getText().trim().isEmpty() && !SecurityUtil.isValidPhone(phoneField.getText().trim())) {
            showError("Phone must be exactly 10 digits"); return false;
        }
        if (parentPhoneField != null && !parentPhoneField.getText().trim().isEmpty() && !SecurityUtil.isValidPhone(parentPhoneField.getText().trim())) {
            showError("Parent phone must be exactly 10 digits"); return false;
        }
        if (aadharField != null && !aadharField.getText().trim().isEmpty() && !SecurityUtil.isValidAadhar(aadharField.getText().trim())) {
            showError("Aadhar must be exactly 12 digits"); return false;
        }
        return true;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML private void handleCancel() { clearForm(); }

    private void handleDelete(StudentMaster s) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("Delete student: " + s.getName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                service.softDelete(s.getId());
                safeAuditLog("DELETE", "admin_student_master", s.getId(), s.getName());
                clearForm();
                loadData();
            }
        });
    }

    private void safeAuditLog(String action, String tableName, Long recordId, String details) {
        try {
            if (auditService != null) {
                auditService.log(action, tableName, recordId, null, details, null);
            }
        } catch (Exception ignored) {}
    }
}
