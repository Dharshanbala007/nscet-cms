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
public class StaffMasterController implements Initializable {
    @FXML private TableView<StaffMaster> table;
    @FXML private TableColumn<StaffMaster, String> codeCol, nameCol, deptCol, desigCol, catCol, phoneCol, actionsCol;
    @FXML private TextField searchField, staffCodeField, nameField, phoneField, emailField;
    @FXML private TextField bloodGroupField, aadharField, esslField, staffGroupField;
    @FXML private TextField collegeCodeField, cityField, pinField;
    @FXML private TextField address1Field, address2Field;
    @FXML private ComboBox<String> genderCombo, categoryCombo;
    @FXML private ComboBox<String> transportCombo;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private ComboBox<DesignationMaster> desigCombo;
    @FXML private DatePicker dobPicker, dojPicker;
    @FXML private CheckBox pfCheck, activeCheck;
    @FXML private VBox formPane;
    @FXML private TabPane tabPane;
    @FXML private Label pageInfo, defaultBanner, warningLabel;
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
        codeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment() != null ? c.getValue().getDepartment().getName() : ""));
        desigCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation() != null ? c.getValue().getDesignation().getName() : ""));
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    StaffMaster s = getTableView().getItems().get(getIndex());
                    Button edit = new Button("Edit"); edit.getStyleClass().add("btn-sm");
                    edit.setOnAction(e -> handleEdit(s));
                    Button del = new Button("Delete"); del.getStyleClass().add("btn-sm-danger");
                    del.setOnAction(e -> handleDelete(s));
                    setGraphic(new HBox(5, edit, del));
                }
            }
        });
        genderCombo.getItems().addAll("Select", "Male", "Female", "Other");
        genderCombo.getSelectionModel().selectFirst();
        categoryCombo.getItems().addAll("Select", "Teaching", "Contract", "NT-Tech", "NT-Non Tech", "Office");
        categoryCombo.getSelectionModel().selectFirst();
        transportCombo.getItems().addAll("Select", "Own", "College");
        transportCombo.getSelectionModel().selectFirst();

        deptCombo.getItems().add(null);
        List<DepartmentMaster> depts = departmentService.getAll(null, 0, 100, "id", "asc").getContent();
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

        table.setItems(tableData);
        loadData();
    }

    private void loadData() {
        Page<StaffMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear();
        tableData.addAll(page.getContent());
        int totalPages = Math.max(page.getTotalPages(), 1);
        pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    @FXML private void handleSearch() {
        currentPage = 0;
        loadData();
    }

    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null;
        clearForm();
        activeCheck.setSelected(true);
        tabPane.getSelectionModel().selectFirst();
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML private void handleEdit(StaffMaster s) {
        editingId = s.getId();
        staffCodeField.setText(s.getStaffCode());
        nameField.setText(s.getName());
        phoneField.setText(s.getPhone());
        emailField.setText(s.getEmail());
        genderCombo.setValue(s.getSex() != null ? s.getSex() : "Select");
        categoryCombo.setValue(s.getCategory() != null ? s.getCategory() : "Select");
        transportCombo.setValue(s.getTransport() != null ? s.getTransport() : "Select");
        deptCombo.setValue(s.getDepartment());
        desigCombo.setValue(s.getDesignation());
        dobPicker.setValue(s.getDateOfBirth());
        dojPicker.setValue(s.getDateOfJoining());
        pfCheck.setSelected(Boolean.TRUE.equals(s.getPfActive()));
        activeCheck.setSelected(Boolean.TRUE.equals(s.getIsActive()));
        String addr = s.getAddress() != null ? s.getAddress() : "";
        String[] parts = addr.split("\n", 2);
        address1Field.setText(parts.length > 0 ? parts[0] : "");
        address2Field.setText(parts.length > 1 ? parts[1] : "");
        cityField.setText(s.getCity());
        pinField.setText(s.getPinCode());
        bloodGroupField.setText(s.getBloodGroup());
        aadharField.setText(s.getAadharNumber());
        esslField.setText(s.getEsslId());
        staffGroupField.setText(s.getStaffGroup());
        collegeCodeField.setText(s.getCollegeCode());
        tabPane.getSelectionModel().selectFirst();
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML private void handleModify() {
        StaffMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            warningLabel.setText("Select a staff member to modify");
            return;
        }
        handleEdit(selected);
    }

    @FXML private void handleSave() {
        try {
            String staffCode = staffCodeField.getText().trim();
            String name = nameField.getText().trim();
            if (staffCode.isEmpty()) { warningLabel.setText("Staff Code is required"); return; }
            if (name.isEmpty()) { warningLabel.setText("Staff Name is required"); return; }
            if ("Select".equals(categoryCombo.getValue()) || categoryCombo.getValue() == null) {
                warningLabel.setText("Category is required"); return;
            }
            if ("Select".equals(deptCombo.getValue()) || deptCombo.getValue() == null) {
                warningLabel.setText("Department is required"); return;
            }
            if ("Select".equals(desigCombo.getValue()) || desigCombo.getValue() == null) {
                warningLabel.setText("Designation is required"); return;
            }

            StaffMaster s = new StaffMaster();
            s.setStaffCode(staffCode);
            s.setName(name);
            s.setPhone(phoneField.getText().trim());
            s.setEmail(emailField.getText().trim());
            s.setSex("Select".equals(genderCombo.getValue()) ? null : genderCombo.getValue());
            s.setCategory(categoryCombo.getValue());
            s.setDepartment(deptCombo.getValue());
            s.setDesignation(desigCombo.getValue());
            s.setTransport("Select".equals(transportCombo.getValue()) ? null : transportCombo.getValue());
            s.setDateOfBirth(dobPicker.getValue());
            s.setDateOfJoining(dojPicker.getValue());
            s.setPfActive(pfCheck.isSelected());
            s.setIsActive(activeCheck.isSelected());
            String addr1 = address1Field.getText().trim();
            String addr2 = address2Field.getText().trim();
            s.setAddress(addr2.isEmpty() ? addr1 : addr1 + "\n" + addr2);
            s.setCity(cityField.getText().trim());
            s.setPinCode(pinField.getText().trim());
            s.setBloodGroup(bloodGroupField.getText().trim());
            s.setAadharNumber(aadharField.getText().trim());
            s.setEsslId(esslField.getText().trim());
            s.setStaffGroup(staffGroupField.getText().trim());
            s.setCollegeCode(collegeCodeField.getText().trim());

            if (editingId != null) {
                service.update(editingId, s);
            } else {
                service.create(s);
            }
            warningLabel.setText("");
            formPane.setVisible(false);
            formPane.setManaged(false);
            loadData();
        } catch (Exception e) {
            warningLabel.setText(e.getMessage());
        }
    }

    @FXML private void handleCancel() {
        warningLabel.setText("");
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    @FXML private void handleClose() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    @FXML private void handleDeleteSelected() {
        StaffMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            warningLabel.setText("Select a staff member to delete");
            return;
        }
        handleDelete(selected);
    }

    private void handleDelete(StaffMaster s) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("Delete staff: " + s.getName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                service.softDelete(s.getId());
                loadData();
            }
        });
    }

    private void clearForm() {
        staffCodeField.clear();
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        bloodGroupField.clear();
        aadharField.clear();
        esslField.clear();
        staffGroupField.clear();
        collegeCodeField.clear();
        cityField.clear();
        pinField.clear();
        address1Field.clear();
        address2Field.clear();
        genderCombo.getSelectionModel().selectFirst();
        categoryCombo.getSelectionModel().selectFirst();
        transportCombo.getSelectionModel().selectFirst();
        deptCombo.getSelectionModel().clearSelection();
        desigCombo.getSelectionModel().clearSelection();
        dobPicker.setValue(null);
        dojPicker.setValue(null);
        pfCheck.setSelected(false);
        warningLabel.setText("");
    }
}
