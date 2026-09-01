package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.FeesDetails;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import com.nscet.cms.db.repository.FeesDetailsRepository;
import com.nscet.cms.ui.navigation.NavigationManager;
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
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class OtherFeesController implements Initializable {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> degreeCombo;
    @FXML private ComboBox<String> semCombo;
    @FXML private ComboBox<String> deptCombo;
    @FXML private ComboBox<String> feesNameCombo;
    @FXML private ComboBox<String> admissionTypeCombo;
    @FXML private ComboBox<String> quotaCombo;
    @FXML private ComboBox<String> stateCombo;
    @FXML private TextField amountField;
    @FXML private TextField totalField;

    @FXML private TableView<FeesDetails> table;
    @FXML private TableColumn<FeesDetails, String> slNoCol;
    @FXML private TableColumn<FeesDetails, String> degreeCol;
    @FXML private TableColumn<FeesDetails, String> deptCol;
    @FXML private TableColumn<FeesDetails, String> semCol;
    @FXML private TableColumn<FeesDetails, String> feesNameCol;
    @FXML private TableColumn<FeesDetails, String> amountCol;
    @FXML private TableColumn<FeesDetails, String> fromDateCol;
    @FXML private TableColumn<FeesDetails, String> toDateCol;

    @Autowired private FeesDetailsRepository feesDetailsRepository;
    @Autowired private DepartmentMasterRepository departmentRepository;

    private ObservableList<FeesDetails> tableData = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
        setupTable();
        loadData();
    }

    private void setupCombos() {
        if (fromDatePicker != null) fromDatePicker.setValue(LocalDate.now());
        if (toDatePicker != null) toDatePicker.setValue(LocalDate.now());

        if (degreeCombo != null) {
            degreeCombo.setItems(FXCollections.observableArrayList("Select", "B.E", "B.Tech", "M.E", "MBA", "MCA"));
            degreeCombo.getSelectionModel().selectFirst();
        }

        if (semCombo != null) {
            semCombo.setItems(FXCollections.observableArrayList("Select", "1", "2", "3", "3LE", "4", "4LE", "5", "6", "7", "8"));
            semCombo.getSelectionModel().selectFirst();
        }

        if (deptCombo != null) {
            deptCombo.setItems(FXCollections.observableArrayList("Select", "CE", "CSE", "ECE", "MECH", "EEE", "IT", "AI", "SE"));
            deptCombo.getSelectionModel().selectFirst();
        }

        if (feesNameCombo != null) {
            feesNameCombo.setItems(FXCollections.observableArrayList(
                "Select", "Tuition Fee", "Anna University Reg Fee", "Other fee", "Library fee", "Development Fees", "Advance", "Bonafied", "Fine"
            ));
            feesNameCombo.getSelectionModel().selectFirst();
        }

        if (admissionTypeCombo != null) {
            admissionTypeCombo.setItems(FXCollections.observableArrayList(
                "Select", "Fresh", "Lateral", "Transfer", "Regular", "Irregular", "READMISSION"
            ));
            admissionTypeCombo.getSelectionModel().selectFirst();
        }

        if (quotaCombo != null) {
            quotaCombo.setItems(FXCollections.observableArrayList(
                "Select", "All", "Govt", "Mgmt", "SCST", "FSTG", "Uravinmurai Letter", "Merit 25", "Merit 50"
            ));
            quotaCombo.getSelectionModel().selectFirst();
        }

        if (stateCombo != null) {
            stateCombo.setItems(FXCollections.observableArrayList(
                "Select", "Own", "Others"
            ));
            stateCombo.getSelectionModel().selectFirst();
        }
    }

    private void setupTable() {
        if (slNoCol != null) slNoCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(tableData.indexOf(c.getValue()) + 1)));
        if (degreeCol != null) degreeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDegree() != null ? c.getValue().getDegree() : "B.E"));
        if (deptCol != null) deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment() != null ? c.getValue().getDepartment().getName() : "ALL"));
        if (semCol != null) semCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSemester() != null ? String.valueOf(c.getValue().getSemester()) : "1"));
        if (feesNameCol != null) feesNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFeesName() != null ? c.getValue().getFeesName().getName() : "Other Fee"));
        if (amountCol != null) amountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount() != null ? "₹" + c.getValue().getAmount().toPlainString() : "₹0"));
        if (fromDateCol != null) fromDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFromDate() != null ? c.getValue().getFromDate().format(DATE_FORMATTER) : "-"));
        if (toDateCol != null) toDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getToDate() != null ? c.getValue().getToDate().format(DATE_FORMATTER) : "-"));

        if (table != null) table.setItems(tableData);
    }

    private void loadData() {
        try {
            org.springframework.data.domain.Page<FeesDetails> page = feesDetailsRepository.findAllActive(org.springframework.data.domain.PageRequest.of(0, 100));
            tableData.clear();
            tableData.addAll(page.getContent());
            calculateTotal();
        } catch (Exception e) {
            System.err.println("[OtherFeesController] Error loading data: " + e.getMessage());
        }
    }

    private void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (FeesDetails fd : tableData) {
            if (fd.getAmount() != null) {
                total = total.add(fd.getAmount());
            }
        }
        if (totalField != null) totalField.setText(String.format("₹%,.2f", total));
    }

    @FXML
    private void handleAdd() {
        clearForm();
    }

    @FXML
    private void handleModify() {
        if (table == null) return;
        FeesDetails selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a row to modify.", Alert.AlertType.WARNING);
            return;
        }
        if (selected.getDegree() != null && degreeCombo != null) degreeCombo.setValue(selected.getDegree());
        if (selected.getSemester() != null && semCombo != null) semCombo.setValue(String.valueOf(selected.getSemester()));
        if (selected.getAmount() != null && amountField != null) amountField.setText(selected.getAmount().toPlainString());
        if (selected.getFromDate() != null && fromDatePicker != null) fromDatePicker.setValue(selected.getFromDate());
        if (selected.getToDate() != null && toDatePicker != null) toDatePicker.setValue(selected.getToDate());
    }

    @FXML
    private void handleDelete() {
        if (table == null) return;
        FeesDetails selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a row to delete.", Alert.AlertType.WARNING);
            return;
        }
        selected.setIsActive(false);
        feesDetailsRepository.save(selected);
        loadData();
    }

    @FXML
    private void handleSave() {
        try {
            FeesDetails fd = new FeesDetails();
            if (degreeCombo != null && degreeCombo.getValue() != null && !"Select".equalsIgnoreCase(degreeCombo.getValue())) {
                fd.setDegree(degreeCombo.getValue());
            }
            if (semCombo != null && semCombo.getValue() != null && !"Select".equalsIgnoreCase(semCombo.getValue())) {
                try {
                    fd.setSemester(Integer.parseInt(semCombo.getValue().replaceAll("[^0-9]", "")));
                } catch (Exception ignored) {
                    fd.setSemester(1);
                }
            }
            if (deptCombo != null && deptCombo.getValue() != null && !"Select".equalsIgnoreCase(deptCombo.getValue())) {
                String deptCode = deptCombo.getValue();
                List<DepartmentMaster> depts = departmentRepository.findAll();
                DepartmentMaster found = depts.stream()
                        .filter(d -> deptCode.equalsIgnoreCase(d.getShortName()) || deptCode.equalsIgnoreCase(d.getName()) || deptCode.equalsIgnoreCase(d.getCode()))
                        .findFirst().orElse(null);
                fd.setDepartment(found);
            }
            if (admissionTypeCombo != null && admissionTypeCombo.getValue() != null && !"Select".equalsIgnoreCase(admissionTypeCombo.getValue())) {
                fd.setAdmissionType(admissionTypeCombo.getValue());
            }

            if (amountField != null && amountField.getText() != null && !amountField.getText().trim().isEmpty()) {
                String cleanAmt = amountField.getText().trim().replaceAll("[^0-9.]", "");
                if (!cleanAmt.isEmpty()) {
                    fd.setAmount(new BigDecimal(cleanAmt));
                } else {
                    fd.setAmount(BigDecimal.ZERO);
                }
            } else {
                fd.setAmount(BigDecimal.ZERO);
            }

            if (fromDatePicker != null) fd.setFromDate(fromDatePicker.getValue());
            if (toDatePicker != null) fd.setToDate(toDatePicker.getValue());

            feesDetailsRepository.save(fd);
            clearForm();
            loadData();
            showAlert("Success", "Other Fees entry saved successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Failed to save: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
    }

    @FXML
    private void handleClose() {
        if (NavigationManager.getActiveContentArea() != null) {
            NavigationManager.loadModule("dashboard", NavigationManager.getActiveContentArea());
        }
    }

    private void clearForm() {
        if (amountField != null) amountField.clear();
        if (degreeCombo != null) degreeCombo.getSelectionModel().selectFirst();
        if (semCombo != null) semCombo.getSelectionModel().selectFirst();
        if (deptCombo != null) deptCombo.getSelectionModel().selectFirst();
        if (feesNameCombo != null) feesNameCombo.getSelectionModel().selectFirst();
        if (admissionTypeCombo != null) admissionTypeCombo.getSelectionModel().selectFirst();
        if (quotaCombo != null) quotaCombo.getSelectionModel().selectFirst();
        if (stateCombo != null) stateCombo.getSelectionModel().selectFirst();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
