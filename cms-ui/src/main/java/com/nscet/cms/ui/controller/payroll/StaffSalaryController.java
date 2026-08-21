package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.service.PayrollService;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.DesignationMaster;
import com.nscet.cms.db.entity.payroll.StaffSalary;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import com.nscet.cms.db.repository.DesignationMasterRepository;
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
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StaffSalaryController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<StaffSalary> table;
    @FXML private TableColumn<StaffSalary, String> colCode, colName, colDept, colDesig, colCategory, colBasic, colSpl, colHra, colGross, colNet, colBank;

    @FXML private TextField codeField, nameField, bankNameField, bankAccField;
    @FXML private TextField basicPayField, splAllowanceField, hraField, washingField, conveyanceField;
    @FXML private TextField epfField, esiField, incomeTaxField, profTaxField, staffClubField, clBalanceField;

    @FXML private ComboBox<String> categoryCombo, deptCombo, desigCombo;

    @Autowired private PayrollService payrollService;
    @Autowired private DepartmentMasterRepository departmentRepository;
    @Autowired private DesignationMasterRepository designationRepository;

    private ObservableList<StaffSalary> tableData = FXCollections.observableArrayList();
    private StaffSalary selectedEntity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
        setupTable();
        loadData();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) populateForm(newSel);
        });
    }

    private void setupCombos() {
        categoryCombo.getItems().setAll("Teaching", "Non-Teaching", "Contract", "Officer");
        categoryCombo.getSelectionModel().selectFirst();

        try {
            deptCombo.getItems().clear();
            for (DepartmentMaster d : departmentRepository.findAll()) {
                if (d.getName() != null && !deptCombo.getItems().contains(d.getName())) {
                    deptCombo.getItems().add(d.getName());
                }
            }
            if (deptCombo.getItems().isEmpty()) deptCombo.getItems().addAll("COMPUTER SCIENCE", "ELECTRONICS", "MECHANICAL", "CIVIL", "ADMIN");
            deptCombo.getSelectionModel().selectFirst();

            desigCombo.getItems().clear();
            for (DesignationMaster d : designationRepository.findAll()) {
                if (d.getName() != null && !desigCombo.getItems().contains(d.getName())) {
                    desigCombo.getItems().add(d.getName());
                }
            }
            if (desigCombo.getItems().isEmpty()) desigCombo.getItems().addAll("Assistant Professor", "HOD", "Lab Assistant", "Officer");
            desigCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.err.println("[StaffSalaryController] Error loading combos: " + e.getMessage());
        }
    }

    private void setupTable() {
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffCode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStaffName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartment()));
        colDesig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        colBasic.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getBasicPay())));
        colSpl.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getSpecialAllowance())));
        colHra.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getHra())));
        colGross.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getGrossSalary())));
        colNet.setCellValueFactory(c -> new SimpleStringProperty("₹" + String.format("%.2f", c.getValue().getNetSalary())));
        colBank.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankAccNo() != null ? c.getValue().getBankAccNo() : "N/A"));

        table.setItems(tableData);
    }

    private void loadData() {
        try {
            List<StaffSalary> list = payrollService.getAllStaffSalaries();
            tableData.setAll(list);
        } catch (Exception e) {
            System.err.println("[StaffSalaryController] Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText() != null ? searchField.getText().trim() : "";
        try {
            tableData.setAll(payrollService.searchStaffSalaries(query));
        } catch (Exception e) {
            System.err.println("[StaffSalaryController] Search error: " + e.getMessage());
        }
    }

    private void populateForm(StaffSalary entity) {
        selectedEntity = entity;
        codeField.setText(entity.getStaffCode());
        nameField.setText(entity.getStaffName());
        categoryCombo.setValue(entity.getCategory());
        deptCombo.setValue(entity.getDepartment());
        desigCombo.setValue(entity.getDesignation());
        bankNameField.setText(entity.getBankName());
        bankAccField.setText(entity.getBankAccNo());

        basicPayField.setText(entity.getBasicPay() != null ? entity.getBasicPay().toString() : "0.00");
        splAllowanceField.setText(entity.getSpecialAllowance() != null ? entity.getSpecialAllowance().toString() : "0.00");
        hraField.setText(entity.getHra() != null ? entity.getHra().toString() : "0.00");
        washingField.setText(entity.getWashingAllowance() != null ? entity.getWashingAllowance().toString() : "0.00");
        conveyanceField.setText(entity.getConveyance() != null ? entity.getConveyance().toString() : "0.00");

        epfField.setText(entity.getEpfDeduction() != null ? entity.getEpfDeduction().toString() : "0.00");
        esiField.setText(entity.getEsiDeduction() != null ? entity.getEsiDeduction().toString() : "0.00");
        incomeTaxField.setText(entity.getIncomeTax() != null ? entity.getIncomeTax().toString() : "0.00");
        profTaxField.setText(entity.getProfessionalTax() != null ? entity.getProfessionalTax().toString() : "0.00");
        staffClubField.setText(entity.getStaffClub() != null ? entity.getStaffClub().toString() : "0.00");
        clBalanceField.setText(entity.getClBalance() != null ? entity.getClBalance().toString() : "12");
    }

    @FXML
    private void handleSave() {
        String code = codeField.getText() != null ? codeField.getText().trim() : "";
        String name = nameField.getText() != null ? nameField.getText().trim() : "";

        if (code.isEmpty() || name.isEmpty()) {
            showAlert("Validation Error", "Please enter Staff Code and Staff Name.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (selectedEntity == null) selectedEntity = new StaffSalary();
            selectedEntity.setStaffCode(code);
            selectedEntity.setStaffName(name);
            selectedEntity.setCategory(categoryCombo.getValue());
            selectedEntity.setDepartment(deptCombo.getValue());
            selectedEntity.setDesignation(desigCombo.getValue());
            selectedEntity.setBankName(bankNameField.getText());
            selectedEntity.setBankAccNo(bankAccField.getText());

            selectedEntity.setBasicPay(parseDecimal(basicPayField.getText()));
            selectedEntity.setSpecialAllowance(parseDecimal(splAllowanceField.getText()));
            selectedEntity.setHra(parseDecimal(hraField.getText()));
            selectedEntity.setWashingAllowance(parseDecimal(washingField.getText()));
            selectedEntity.setConveyance(parseDecimal(conveyanceField.getText()));

            selectedEntity.setEpfDeduction(parseDecimal(epfField.getText()));
            selectedEntity.setEsiDeduction(parseDecimal(esiField.getText()));
            selectedEntity.setIncomeTax(parseDecimal(incomeTaxField.getText()));
            selectedEntity.setProfessionalTax(parseDecimal(profTaxField.getText()));
            selectedEntity.setStaffClub(parseDecimal(staffClubField.getText()));

            try {
                selectedEntity.setClBalance(Integer.parseInt(clBalanceField.getText().trim()));
            } catch (Exception e) {
                selectedEntity.setClBalance(12);
            }

            payrollService.saveStaffSalary(selectedEntity);
            showAlert("Save Success", "Staff Salary record saved successfully!", Alert.AlertType.INFORMATION);
            handleClear();
            loadData();
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save record: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClear() {
        selectedEntity = null;
        codeField.clear();
        nameField.clear();
        bankNameField.clear();
        bankAccField.clear();
        basicPayField.clear();
        splAllowanceField.clear();
        hraField.clear();
        washingField.clear();
        conveyanceField.clear();
        epfField.clear();
        esiField.clear();
        incomeTaxField.clear();
        profTaxField.clear();
        staffClubField.clear();
        clBalanceField.clear();
        categoryCombo.getSelectionModel().selectFirst();
        deptCombo.getSelectionModel().selectFirst();
        desigCombo.getSelectionModel().selectFirst();
        table.getSelectionModel().clearSelection();
    }

    private BigDecimal parseDecimal(String text) {
        if (text == null || text.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(text.trim().replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
