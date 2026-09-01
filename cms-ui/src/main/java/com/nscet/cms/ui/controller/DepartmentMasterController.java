package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.DepartmentService;
import com.nscet.cms.db.entity.DepartmentMaster;
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
public class DepartmentMasterController implements Initializable {
    @FXML private TableView<DepartmentMaster> table;
    @FXML private TableColumn<DepartmentMaster, String> codeCol, shortNameCol, nameCol, typeCol;
    @FXML private TextField searchField, codeField, shortNameField, nameField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private VBox formPane;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn, nextBtn;

    @Autowired private DepartmentService service;
    private ObservableList<DepartmentMaster> tableData = FXCollections.observableArrayList();
    private int currentPage = 0; private int pageSize = 20; private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCode()));
        shortNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShortName()));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));

        typeCombo.getItems().add("Select");
        typeCombo.getItems().addAll("Academic", "Official");
        typeCombo.getSelectionModel().selectFirst();
        table.setItems(tableData);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateForm(newSel);
            }
        });

        loadData();
    }

    private void loadData() {
        Page<DepartmentMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear(); tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d (Total: %d)", currentPage + 1, page.getTotalPages(), page.getTotalElements()));
        prevBtn.setDisable(currentPage == 0); nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    private void populateForm(DepartmentMaster d) {
        editingId = d.getId();
        codeField.setText(d.getCode() != null ? d.getCode() : "");
        shortNameField.setText(d.getShortName() != null ? d.getShortName() : "");
        nameField.setText(d.getName() != null ? d.getName() : "");
        if (d.getType() != null && typeCombo.getItems().contains(d.getType())) {
            typeCombo.setValue(d.getType());
        } else {
            typeCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null; codeField.clear(); shortNameField.clear(); nameField.clear();
        typeCombo.getSelectionModel().selectFirst();
        table.getSelectionModel().clearSelection();
    }

    @FXML private void handleModify() {
        DepartmentMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a row from the table to modify.", Alert.AlertType.WARNING);
            return;
        }
        populateForm(selected);
    }

    @FXML private void handleDeleteSelected() {
        DepartmentMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a row from the table to delete.", Alert.AlertType.WARNING);
            return;
        }
        handleDelete(selected);
    }

    @FXML private void handleSave() {
        try {
            DepartmentMaster d = new DepartmentMaster();
            d.setCode(codeField.getText().trim()); d.setShortName(shortNameField.getText().trim());
            d.setName(nameField.getText().trim());
            d.setType(typeCombo.getValue() != null && !"Select".equals(typeCombo.getValue()) ? typeCombo.getValue() : null);
            if (editingId != null) service.update(editingId, d); else service.create(d);
            handleAdd();
            loadData();
        } catch (Exception e) { showAlert("Error", e.getMessage(), Alert.AlertType.ERROR); }
    }

    @FXML private void handleCancel() {
        handleAdd();
    }

    private void handleDelete(DepartmentMaster d) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("Are you sure you want to delete department: " + d.getName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                service.softDelete(d.getId());
                handleAdd();
                loadData();
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
