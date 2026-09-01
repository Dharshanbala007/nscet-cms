package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.BankService;
import com.nscet.cms.db.entity.BankMaster;
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
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class BankMasterController implements Initializable {

    @FXML private TableView<BankMaster> table;
    @FXML private TableColumn<BankMaster, String> bankNameCol;
    @FXML private TableColumn<BankMaster, String> shortNameCol;
    @FXML private TableColumn<BankMaster, String> accNoCol;
    @FXML private TableColumn<BankMaster, String> branchCol;
    @FXML private TableColumn<BankMaster, String> remarksCol;
    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private TextField bankNameField;
    @FXML private TextField shortNameField;
    @FXML private TextField accNoField;
    @FXML private TextField branchField;
    @FXML private TextField ifscField;
    @FXML private TextField remarksField;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    @Autowired private BankService service;
    private ObservableList<BankMaster> tableData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bankNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankName()));
        shortNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankShortName()));
        accNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountNumber()));
        branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranch()));
        remarksCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks()));
        table.setItems(tableData);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateForm(newSel);
            }
        });

        loadData();
    }

    private void loadData() {
        Page<BankMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear(); tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d (Total: %d)", currentPage + 1, page.getTotalPages(), page.getTotalElements()));
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    private void populateForm(BankMaster b) {
        editingId = b.getId();
        bankNameField.setText(b.getBankName() != null ? b.getBankName() : "");
        shortNameField.setText(b.getBankShortName() != null ? b.getBankShortName() : "");
        accNoField.setText(b.getAccountNumber() != null ? b.getAccountNumber() : "");
        branchField.setText(b.getBranch() != null ? b.getBranch() : "");
        ifscField.setText(b.getIfscCode() != null ? b.getIfscCode() : "");
        remarksField.setText(b.getRemarks() != null ? b.getRemarks() : "");
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null; bankNameField.clear(); shortNameField.clear(); accNoField.clear();
        branchField.clear(); ifscField.clear(); remarksField.clear();
        table.getSelectionModel().clearSelection();
    }

    @FXML private void handleModify() {
        BankMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a row from the table to modify.", Alert.AlertType.WARNING);
            return;
        }
        populateForm(selected);
    }

    @FXML private void handleDeleteSelected() {
        BankMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a row from the table to delete.", Alert.AlertType.WARNING);
            return;
        }
        handleDelete(selected);
    }

    @FXML private void handleSave() {
        try {
            BankMaster b = new BankMaster();
            b.setBankName(bankNameField.getText().trim()); b.setBankShortName(shortNameField.getText().trim());
            b.setAccountNumber(accNoField.getText().trim()); b.setBranch(branchField.getText().trim());
            b.setIfscCode(ifscField.getText().trim()); b.setRemarks(remarksField.getText().trim());
            if (editingId != null) service.update(editingId, b); else service.create(b);
            handleAdd();
            loadData();
        } catch (Exception e) { showAlert("Error", e.getMessage(), Alert.AlertType.ERROR); }
    }

    @FXML private void handleCancel() {
        handleAdd();
    }

    private void handleDelete(BankMaster b) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Are you sure you want to delete bank: " + b.getBankName() + "?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                service.softDelete(b.getId());
                handleAdd();
                loadData();
            }
        });
    }

    private void showAlert(String t, String m, Alert.AlertType ty) {
        Alert a = new Alert(ty); a.setTitle(t); a.setContentText(m); a.showAndWait();
    }
}
