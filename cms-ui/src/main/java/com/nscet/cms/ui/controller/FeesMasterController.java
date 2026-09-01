package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.FeesService;
import com.nscet.cms.db.entity.FeesMaster;
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
public class FeesMasterController implements Initializable {
    @FXML private TableView<FeesMaster> table;
    @FXML private TableColumn<FeesMaster, String> nameCol, groupCol, fromDateCol, toDateCol, semesterFeeCol;
    @FXML private TextField searchField, nameField;
    @FXML private ComboBox<String> groupCombo;
    @FXML private CheckBox semesterFeeCheck;
    @FXML private VBox formPane;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn, nextBtn;

    @Autowired private FeesService service;
    private ObservableList<FeesMaster> tableData = FXCollections.observableArrayList();
    private int currentPage = 0; private int pageSize = 20; private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        groupCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFeesGroup()));
        fromDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFromDate() != null ? c.getValue().getFromDate().toString() : ""));
        toDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getToDate() != null ? c.getValue().getToDate().toString() : ""));
        semesterFeeCol.setCellValueFactory(c -> new SimpleStringProperty(Boolean.TRUE.equals(c.getValue().getSemesterFee()) ? "Yes" : "No"));

        groupCombo.getItems().add("Select");
        groupCombo.getItems().addAll(
            "College Fees", "Karuna Donar Club", "Bus Fees", "Exam Fees", "Admission Fees", "Alumni Registration", "Other"
        );
        groupCombo.getSelectionModel().selectFirst();
        table.setItems(tableData);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateForm(newSel);
            }
        });

        loadData();
    }

    private void loadData() {
        Page<FeesMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear(); tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d (Total: %d)", currentPage + 1, page.getTotalPages(), page.getTotalElements()));
        prevBtn.setDisable(currentPage == 0); nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    private void populateForm(FeesMaster f) {
        editingId = f.getId();
        nameField.setText(f.getName() != null ? f.getName() : "");
        if (f.getFeesGroup() != null && groupCombo.getItems().contains(f.getFeesGroup())) {
            groupCombo.setValue(f.getFeesGroup());
        } else {
            groupCombo.getSelectionModel().selectFirst();
        }
        semesterFeeCheck.setSelected(Boolean.TRUE.equals(f.getSemesterFee()));
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null;
        nameField.clear();
        groupCombo.getSelectionModel().selectFirst();
        semesterFeeCheck.setSelected(false);
        table.getSelectionModel().clearSelection();
    }

    @FXML private void handleModify() {
        FeesMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a row from the table to modify.", Alert.AlertType.WARNING);
            return;
        }
        populateForm(selected);
    }

    @FXML private void handleDeleteSelected() {
        FeesMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a row from the table to delete.", Alert.AlertType.WARNING);
            return;
        }
        handleDelete(selected);
    }

    @FXML private void handleSave() {
        try {
            FeesMaster f = new FeesMaster();
            f.setName(nameField.getText().trim());
            f.setFeesGroup(groupCombo.getValue() != null && !"Select".equals(groupCombo.getValue()) ? groupCombo.getValue() : null);
            f.setSemesterFee(semesterFeeCheck.isSelected());
            if (editingId != null) service.update(editingId, f); else service.create(f);
            handleAdd();
            loadData();
        } catch (Exception e) { showAlert("Error", e.getMessage(), Alert.AlertType.ERROR); }
    }

    @FXML private void handleCancel() {
        handleAdd();
    }

    private void handleDelete(FeesMaster f) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("Are you sure you want to delete fee: " + f.getName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                service.softDelete(f.getId());
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
