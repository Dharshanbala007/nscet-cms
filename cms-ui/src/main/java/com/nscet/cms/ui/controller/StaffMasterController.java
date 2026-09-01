package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.DepartmentService;
import com.nscet.cms.core.service.DesignationService;
import com.nscet.cms.core.service.StaffService;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.DesignationMaster;
import com.nscet.cms.db.entity.StaffMaster;
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
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StaffMasterController implements Initializable {
    @FXML private TableView<StaffMaster> table;
    @FXML private TableColumn<StaffMaster, String> codeCol, nameCol, deptCol, desigCol, catCol, phoneCol;
    @FXML private TextField searchField, staffCodeField, nameField, phoneField, emailField;
    @FXML private TextField bloodGroupField, aadharField, esslField;
    @FXML private ComboBox<String> staffGroupCombo;
    @FXML private TextField collegeCodeField, cityField, pinField;
    @FXML private TextField address1Field, address2Field;
    @FXML private ComboBox<String> genderCombo, categoryCombo;
    @FXML private ComboBox<String> transportCombo;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private ComboBox<DesignationMaster> desigCombo;
    @FXML private DatePicker dobPicker, dojPicker, epfDojPicker;
    @FXML private CheckBox activeCheck;
    @FXML private VBox formPane;
    @FXML private Label pageInfo, warningLabel;
    @FXML private Button prevBtn, nextBtn;

    @Autowired private StaffService service;
    @Autowired private DepartmentService departmentService;
    @Autowired private DesignationService designationService;
    private ObservableList<StaffMaster> tableData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (codeCol != null) codeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        if (nameCol != null) nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        if (deptCol != null) deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment() != null ? c.getValue().getDepartment().getName() : ""));
        if (desigCol != null) desigCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation() != null ? c.getValue().getDesignation().getName() : ""));
        if (catCol != null) catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        if (phoneCol != null) phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));

        if (genderCombo != null) {
            genderCombo.getItems().setAll("Select", "Male", "Female", "Other");
            genderCombo.getSelectionModel().selectFirst();
        }
        
        if (categoryCombo != null) {
            categoryCombo.getItems().setAll("Select", "Teaching", "Contract", "NT-Tech", "NT-Non Tech", "Office");
            categoryCombo.getSelectionModel().selectFirst();
        }

        if (staffGroupCombo != null) {
            staffGroupCombo.getItems().setAll(
                "Select", "Teaching", "Non-Teaching", "Admin", "Attender", "Electrician", "Gardener", "Scavenger", "Security"
            );
            staffGroupCombo.getSelectionModel().selectFirst();
        }

        if (transportCombo != null) {
            transportCombo.getItems().setAll("Select", "Own", "College");
            transportCombo.getSelectionModel().selectFirst();
        }

        if (deptCombo != null) {
            deptCombo.getItems().add(null);
            List<DepartmentMaster> depts = new java.util.ArrayList<>(departmentService.getAll(null, 0, 100, "id", "asc").getContent());
            boolean hasCanteen = depts.stream().anyMatch(d -> d != null && "Canteen".equalsIgnoreCase(d.getName()));
            if (!hasCanteen) {
                DepartmentMaster canteen = new DepartmentMaster();
                canteen.setCode("CAN");
                canteen.setShortName("CANTEEN");
                canteen.setName("Canteen");
                canteen.setType("Administrative");
                canteen.setIsActive(true);
                depts.add(canteen);
            }
            deptCombo.getItems().addAll(depts);
            deptCombo.setCellFactory(c -> new ListCell<>() {
                protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select" : item.getName());
                }
            });
            deptCombo.setButtonCell(new ListCell<>() {
                protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select" : item.getName());
                }
            });
        }

        if (desigCombo != null) {
            desigCombo.getItems().add(null);
            List<DesignationMaster> desigs = designationService.getAll(null, 0, 100, "id", "asc").getContent();
            desigCombo.getItems().addAll(desigs);
            desigCombo.setCellFactory(c -> new ListCell<>() {
                protected void updateItem(DesignationMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select" : item.getName());
                }
            });
            desigCombo.setButtonCell(new ListCell<>() {
                protected void updateItem(DesignationMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select" : item.getName());
                }
            });
        }

        if (table != null) {
            table.setItems(tableData);
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    populateForm(newSel);
                }
            });
        }

        loadData();
    }

    private void loadData() {
        try {
            Page<StaffMaster> page = service.getAll(searchField != null ? searchField.getText() : "", currentPage, pageSize, "id", "asc");
            tableData.clear();
            tableData.addAll(page.getContent());
            int totalPages = Math.max(page.getTotalPages(), 1);
            if (pageInfo != null) pageInfo.setText(String.format("Page %d of %d (Total: %d)", currentPage + 1, totalPages, page.getTotalElements()));
            if (prevBtn != null) prevBtn.setDisable(currentPage == 0);
            if (nextBtn != null) nextBtn.setDisable(currentPage >= totalPages - 1);
        } catch (Exception e) {
            System.err.println("[StaffMasterController] loadData error: " + e.getMessage());
        }
    }

    private void populateForm(StaffMaster s) {
        editingId = s.getId();
        if (staffCodeField != null) staffCodeField.setText(s.getStaffCode() != null ? s.getStaffCode() : "");
        if (nameField != null) nameField.setText(s.getName() != null ? s.getName() : "");
        if (phoneField != null) phoneField.setText(s.getPhone() != null ? s.getPhone() : "");
        if (emailField != null) emailField.setText(s.getEmail() != null ? s.getEmail() : "");
        if (genderCombo != null) genderCombo.setValue(s.getSex() != null ? s.getSex() : "Select");
        if (categoryCombo != null) categoryCombo.setValue(s.getCategory() != null ? s.getCategory() : "Select");
        if (staffGroupCombo != null) {
            if (s.getStaffGroup() != null && staffGroupCombo.getItems().contains(s.getStaffGroup())) {
                staffGroupCombo.setValue(s.getStaffGroup());
            } else {
                staffGroupCombo.getSelectionModel().selectFirst();
            }
        }
        if (transportCombo != null) transportCombo.setValue(s.getTransport() != null ? s.getTransport() : "Select");
        if (deptCombo != null) {
            if (s.getDepartment() != null) {
                DepartmentMaster match = deptCombo.getItems().stream()
                        .filter(d -> d != null && (
                                (s.getDepartment().getId() != null && s.getDepartment().getId().equals(d.getId())) ||
                                (d.getName() != null && d.getName().equalsIgnoreCase(s.getDepartment().getName())) ||
                                (d.getShortName() != null && d.getShortName().equalsIgnoreCase(s.getDepartment().getShortName()))
                        ))
                        .findFirst().orElse(s.getDepartment());
                deptCombo.setValue(match);
            } else {
                deptCombo.getSelectionModel().selectFirst();
            }
        }
        if (desigCombo != null) {
            if (s.getDesignation() != null) {
                com.nscet.cms.db.entity.DesignationMaster match = desigCombo.getItems().stream()
                        .filter(d -> d != null && (
                                (s.getDesignation().getId() != null && s.getDesignation().getId().equals(d.getId())) ||
                                (d.getName() != null && d.getName().equalsIgnoreCase(s.getDesignation().getName()))
                        ))
                        .findFirst().orElse(s.getDesignation());
                desigCombo.setValue(match);
            } else {
                desigCombo.getSelectionModel().selectFirst();
            }
        }
        if (dobPicker != null) dobPicker.setValue(s.getDateOfBirth());
        if (dojPicker != null) dojPicker.setValue(s.getDateOfJoining());
        if (activeCheck != null) activeCheck.setSelected(Boolean.TRUE.equals(s.getIsActive()));
        
        String addr = s.getAddress() != null ? s.getAddress() : "";
        String[] parts = addr.split("\n", 2);
        if (address1Field != null) address1Field.setText(parts.length > 0 ? parts[0] : "");
        if (address2Field != null) address2Field.setText(parts.length > 1 ? parts[1] : "");
        if (cityField != null) cityField.setText(s.getCity() != null ? s.getCity() : "");
        if (pinField != null) pinField.setText(s.getPinCode() != null ? s.getPinCode() : "");
        if (bloodGroupField != null) bloodGroupField.setText(s.getBloodGroup() != null ? s.getBloodGroup() : "");
        if (aadharField != null) aadharField.setText(s.getAadharNumber() != null ? s.getAadharNumber() : "");
        if (esslField != null) esslField.setText(s.getEsslId() != null ? s.getEsslId() : "");
        if (collegeCodeField != null) collegeCodeField.setText(s.getCollegeCode() != null ? s.getCollegeCode() : "");
    }

    @FXML private void handleSearch() {
        currentPage = 0;
        loadData();
    }

    @FXML private void handlePrevious() { if (currentPage > 0) { currentPage--; loadData(); } }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null;
        clearForm();
        if (activeCheck != null) activeCheck.setSelected(true);
        if (table != null) table.getSelectionModel().clearSelection();
    }

    @FXML private void handleModify() {
        if (table == null) return;
        StaffMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            if (warningLabel != null) warningLabel.setText("Select a staff member to modify");
            return;
        }
        populateForm(selected);
    }

    @FXML private void handleSave() {
        try {
            String staffCode = staffCodeField != null && staffCodeField.getText() != null ? staffCodeField.getText().trim() : "";
            String name = nameField != null && nameField.getText() != null ? nameField.getText().trim() : "";
            if (staffCode.isEmpty()) { if (warningLabel != null) warningLabel.setText("Staff Code is required"); return; }
            if (name.isEmpty()) { if (warningLabel != null) warningLabel.setText("Staff Name is required"); return; }

            StaffMaster s = new StaffMaster();
            s.setStaffCode(staffCode);
            s.setName(name);
            s.setPhone(phoneField != null && phoneField.getText() != null ? phoneField.getText().trim() : "");
            s.setEmail(emailField != null && emailField.getText() != null ? emailField.getText().trim() : "");
            s.setSex(genderCombo != null && !"Select".equals(genderCombo.getValue()) ? genderCombo.getValue() : null);
            s.setCategory(categoryCombo != null && !"Select".equals(categoryCombo.getValue()) ? categoryCombo.getValue() : null);
            s.setStaffGroup(staffGroupCombo != null && !"Select".equals(staffGroupCombo.getValue()) ? staffGroupCombo.getValue() : null);
            s.setDepartment(deptCombo != null ? deptCombo.getValue() : null);
            s.setDesignation(desigCombo != null ? desigCombo.getValue() : null);
            s.setTransport(transportCombo != null && !"Select".equals(transportCombo.getValue()) ? transportCombo.getValue() : null);
            s.setDateOfBirth(dobPicker != null ? dobPicker.getValue() : null);
            s.setDateOfJoining(dojPicker != null ? dojPicker.getValue() : null);
            s.setIsActive(activeCheck != null ? activeCheck.isSelected() : true);
            
            String addr1 = address1Field != null && address1Field.getText() != null ? address1Field.getText().trim() : "";
            String addr2 = address2Field != null && address2Field.getText() != null ? address2Field.getText().trim() : "";
            s.setAddress(addr2.isEmpty() ? addr1 : addr1 + "\n" + addr2);
            s.setCity(cityField != null && cityField.getText() != null ? cityField.getText().trim() : "");
            s.setPinCode(pinField != null && pinField.getText() != null ? pinField.getText().trim() : "");
            s.setBloodGroup(bloodGroupField != null && bloodGroupField.getText() != null ? bloodGroupField.getText().trim() : null);
            s.setAadharNumber(aadharField != null && aadharField.getText() != null ? aadharField.getText().trim() : null);
            s.setEsslId(esslField != null && esslField.getText() != null ? esslField.getText().trim() : null);
            s.setCollegeCode(collegeCodeField != null && collegeCodeField.getText() != null ? collegeCodeField.getText().trim() : null);

            if (editingId != null) {
                service.update(editingId, s);
            } else {
                service.create(s);
            }
            if (warningLabel != null) warningLabel.setText("");
            handleAdd();
            loadData();
        } catch (Exception e) {
            if (warningLabel != null) warningLabel.setText(e.getMessage());
            System.err.println("[StaffMasterController] Error saving staff: " + e.getMessage());
        }
    }

    @FXML private void handleCancel() {
        handleAdd();
    }

    @FXML private void handleDeleteSelected() {
        if (table == null) return;
        StaffMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            if (warningLabel != null) warningLabel.setText("Select a staff member to delete");
            return;
        }
        handleDelete(selected);
    }

    private void handleDelete(StaffMaster s) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("Are you sure you want to delete staff: " + s.getName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                service.softDelete(s.getId());
                handleAdd();
                loadData();
            }
        });
    }

    private void clearForm() {
        if (staffCodeField != null) staffCodeField.clear();
        if (nameField != null) nameField.clear();
        if (phoneField != null) phoneField.clear();
        if (emailField != null) emailField.clear();
        if (bloodGroupField != null) bloodGroupField.clear();
        if (aadharField != null) aadharField.clear();
        if (esslField != null) esslField.clear();
        if (collegeCodeField != null) collegeCodeField.clear();
        if (cityField != null) cityField.clear();
        if (pinField != null) pinField.clear();
        if (address1Field != null) address1Field.clear();
        if (address2Field != null) address2Field.clear();
        if (genderCombo != null) genderCombo.getSelectionModel().selectFirst();
        if (categoryCombo != null) categoryCombo.getSelectionModel().selectFirst();
        if (staffGroupCombo != null) staffGroupCombo.getSelectionModel().selectFirst();
        if (transportCombo != null) transportCombo.getSelectionModel().selectFirst();
        if (deptCombo != null) deptCombo.getSelectionModel().clearSelection();
        if (desigCombo != null) desigCombo.getSelectionModel().clearSelection();
        if (dobPicker != null) dobPicker.setValue(null);
        if (dojPicker != null) dojPicker.setValue(null);
        if (epfDojPicker != null) epfDojPicker.setValue(null);
        if (warningLabel != null) warningLabel.setText("");
    }
}
