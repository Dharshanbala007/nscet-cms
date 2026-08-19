package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.PettyVoucherService;
import com.nscet.cms.db.entity.PettyVoucher;
import com.nscet.cms.db.entity.PettyVoucherItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class PettyVoucherController implements Initializable {

    @FXML private TextField voucherNoField;
    @FXML private DatePicker datePicker;
    @FXML private TextField staffNameField;
    @FXML private TextField staffCodeField;
    @FXML private TextField designationField;
    @FXML private TextField deptField;
    @FXML private TextField susVNoField;
    @FXML private DatePicker susDatePicker;
    @FXML private TextField susAmountField;
    @FXML private TextArea purposeField;
    @FXML private DatePicker itemDatePicker;
    @FXML private TextField itemDetailsField;
    @FXML private TextField itemAttNoField;
    @FXML private ComboBox<String> itemTypeCombo;
    @FXML private TextField itemAmountField;
    @FXML private TableView<PettyVoucherItem> itemsTable;
    @FXML private TextField totalAmountField;

    @FXML private TableColumn<PettyVoucherItem, String> colItemDate;
    @FXML private TableColumn<PettyVoucherItem, String> colItemDetails;
    @FXML private TableColumn<PettyVoucherItem, String> colItemAttNo;
    @FXML private TableColumn<PettyVoucherItem, String> colItemType;
    @FXML private TableColumn<PettyVoucherItem, BigDecimal> colItemAmount;
    @FXML private TableColumn<PettyVoucherItem, String> colItemAction;

    @Autowired
    private PettyVoucherService service;

    private final ObservableList<PettyVoucherItem> itemsList = FXCollections.observableArrayList();
    private PettyVoucher selectedRecord;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        susDatePicker.setValue(LocalDate.now());
        itemDatePicker.setValue(LocalDate.now());
        voucherNoField.setText(service.generateNextVoucherNo());
        itemTypeCombo.setItems(FXCollections.observableArrayList("Select", "TA", "DA", "Local Conveyance", "Printing", "Stationery", "Miscellaneous"));
        itemTypeCombo.getSelectionModel().selectFirst();
        setupTable();
        itemsTable.setItems(itemsList);
    }

    private void setupTable() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        colItemDate.setCellValueFactory(cellData -> {
            LocalDate d = cellData.getValue().getItemDate();
            return new SimpleStringProperty(d != null ? d.format(fmt) : "");
        });
        colItemDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        colItemAttNo.setCellValueFactory(new PropertyValueFactory<>("attendanceNo"));
        colItemType.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        colItemAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colItemAction.setCellValueFactory(cellData -> new SimpleStringProperty(""));

        itemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillItemForm(newVal);
            }
        });
    }

    private void fillItemForm(PettyVoucherItem item) {
        itemDatePicker.setValue(item.getItemDate());
        itemDetailsField.setText(item.getDetails());
        itemAttNoField.setText(item.getAttendanceNo());
        itemTypeCombo.getSelectionModel().select(item.getItemType());
        itemAmountField.setText(item.getAmount() != null ? item.getAmount().toString() : "");
    }

    private void updateTotal() {
        BigDecimal total = itemsList.stream()
                .map(i -> i.getAmount() != null ? i.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAmountField.setText(total.toString());
    }

    @FXML
    private void handleAddItem() {
        String amtText = itemAmountField.getText().trim();
        if (amtText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter an amount for the item");
            return;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amtText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid amount");
            return;
        }

        PettyVoucherItem item = new PettyVoucherItem();
        item.setItemDate(itemDatePicker.getValue());
        item.setDetails(itemDetailsField.getText().trim());
        item.setAttendanceNo(itemAttNoField.getText().trim());
        String type = itemTypeCombo.getSelectionModel().getSelectedItem();
        item.setItemType("Select".equals(type) ? "" : type);
        item.setAmount(amount);

        itemsList.add(item);
        updateTotal();
        clearItemForm();
    }

    private void clearItemForm() {
        itemDatePicker.setValue(LocalDate.now());
        itemDetailsField.clear();
        itemAttNoField.clear();
        itemTypeCombo.getSelectionModel().selectFirst();
        itemAmountField.clear();
    }

    @FXML
    private void handleAdd() {
        clearAll();
    }

    @FXML
    private void handleModify() {
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a record to modify");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a record to delete");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete voucher " + selectedRecord.getVoucherNo() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.softDelete(selectedRecord.getId());
                clearAll();
            }
        });
    }

    @FXML
    private void handleCancel() {
        clearAll();
    }

    @FXML
    private void handleSave() {
        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a date");
            return;
        }

        PettyVoucher entity;
        if (selectedRecord != null) {
            entity = selectedRecord;
        } else {
            entity = new PettyVoucher();
            entity.setVoucherNo(voucherNoField.getText());
            entity.setIsActive(true);
        }

        entity.setVoucherDate(datePicker.getValue());
        entity.setStaffName(staffNameField.getText().trim());
        entity.setStaffCode(staffCodeField.getText().trim());
        entity.setDesignation(designationField.getText().trim());
        entity.setDepartment(deptField.getText().trim());
        entity.setSuspenseVoucherNo(susVNoField.getText().trim());
        entity.setSuspenseDate(susDatePicker.getValue());
        entity.setSuspenseAmount(susAmountField.getText().isEmpty() ? null : new BigDecimal(susAmountField.getText()));
        entity.setPurpose(purposeField.getText().trim());

        if (entity.getItems() == null) {
            entity.setItems(new ArrayList<>());
        }
        entity.getItems().clear();
        entity.getItems().addAll(itemsList);

        service.create(entity);
        showAlert(Alert.AlertType.INFORMATION, "Voucher saved successfully");
        clearAll();
    }

    private void clearAll() {
        selectedRecord = null;
        voucherNoField.setText(service.generateNextVoucherNo());
        datePicker.setValue(LocalDate.now());
        staffNameField.clear();
        staffCodeField.clear();
        designationField.clear();
        deptField.clear();
        susVNoField.clear();
        susDatePicker.setValue(LocalDate.now());
        susAmountField.clear();
        purposeField.clear();
        itemsList.clear();
        totalAmountField.setText("0");
        clearItemForm();
    }

    @FXML
    private void handleClose() {
        StackPane contentArea = com.nscet.cms.ui.navigation.NavigationManager.getActiveContentArea();
        if (contentArea != null) {
            Label placeholder = new Label("Select a module from the menu");
            placeholder.getStyleClass().add("placeholder-label");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(placeholder);
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Petty Voucher");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
