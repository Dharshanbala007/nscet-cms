package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.QuotaService;
import com.nscet.cms.db.entity.QuotaMaster;
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

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class QuotaMasterController implements Initializable {
    @FXML private TableView<QuotaMaster> table;
    @FXML private TableColumn<QuotaMaster, String> codeCol, nameCol, pctCol, discCol, admTypeCol;
    @FXML private TextField searchField, codeField, nameField, pctField, discField;
    @FXML private RadioButton perRadio, amountRadio;
    @FXML private ToggleGroup discountTypeGroup;
    @FXML private ComboBox<String> admTypeCombo;
    @FXML private VBox formPane;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn, nextBtn;

    @Autowired private QuotaService service;
    private ObservableList<QuotaMaster> tableData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (codeCol != null) codeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCode()));
        if (nameCol != null) nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        if (pctCol != null) pctCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPercentage() != null ? c.getValue().getPercentage().toPlainString() + "%" : ""));
        if (discCol != null) discCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDiscountAmount() != null ? "₹" + c.getValue().getDiscountAmount().toPlainString() : ""));
        if (admTypeCol != null) admTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionType()));

        if (admTypeCombo != null) {
            admTypeCombo.getItems().setAll("Select", "Government", "Management");
            admTypeCombo.getSelectionModel().selectFirst();
        }

        // Allow BOTH percentage and discount amount fields to be enabled and edited
        if (pctField != null) pctField.setDisable(false);
        if (discField != null) discField.setDisable(false);

        if (table != null) {
            table.setItems(tableData);
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    populateForm(newSel);
                }
            });
        }

        loadData();
        handleAdd();
    }

    private void loadData() {
        try {
            Page<QuotaMaster> page = service.getAll(searchField != null ? searchField.getText() : "", currentPage, pageSize, "id", "asc");
            tableData.clear();
            tableData.addAll(page.getContent());
            if (pageInfo != null) pageInfo.setText(String.format("Page %d of %d (Total: %d)", currentPage + 1, Math.max(1, page.getTotalPages()), page.getTotalElements()));
            if (prevBtn != null) prevBtn.setDisable(currentPage == 0);
            if (nextBtn != null) nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
        } catch (Exception e) {
            System.err.println("[QuotaMasterController] loadData error: " + e.getMessage());
        }
    }

    private void populateForm(QuotaMaster q) {
        editingId = q.getId();
        if (codeField != null) codeField.setText(q.getCode() != null ? q.getCode() : "");
        if (nameField != null) nameField.setText(q.getName() != null ? q.getName() : "");
        
        if (pctField != null) {
            pctField.setDisable(false);
            pctField.setText(q.getPercentage() != null ? q.getPercentage().toPlainString() : "");
        }
        if (discField != null) {
            discField.setDisable(false);
            discField.setText(q.getDiscountAmount() != null ? q.getDiscountAmount().toPlainString() : "");
        }

        if (q.getPercentage() != null && perRadio != null) {
            perRadio.setSelected(true);
        } else if (q.getDiscountAmount() != null && amountRadio != null) {
            amountRadio.setSelected(true);
        }

        if (admTypeCombo != null) {
            String type = q.getAdmissionType();
            if ("Govt".equalsIgnoreCase(type)) type = "Government";
            if ("Mgmt".equalsIgnoreCase(type)) type = "Management";
            if (type != null && admTypeCombo.getItems().contains(type)) {
                admTypeCombo.setValue(type);
            } else {
                admTypeCombo.getSelectionModel().selectFirst();
            }
        }
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { if (currentPage > 0) { currentPage--; loadData(); } }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null;
        if (codeField != null) codeField.setText(service.generateNextCode());
        if (nameField != null) nameField.clear();
        if (pctField != null) { pctField.clear(); pctField.setDisable(false); }
        if (discField != null) { discField.clear(); discField.setDisable(false); }
        if (perRadio != null) perRadio.setSelected(true);
        if (admTypeCombo != null) admTypeCombo.getSelectionModel().selectFirst();
        if (table != null) table.getSelectionModel().clearSelection();
    }

    @FXML private void handleModify() {
        if (table == null) return;
        QuotaMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a row from the table to modify.", Alert.AlertType.WARNING);
            return;
        }
        populateForm(selected);
    }

    @FXML private void handleDeleteSelected() {
        if (table == null) return;
        QuotaMaster selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a row from the table to delete.", Alert.AlertType.WARNING);
            return;
        }
        handleDelete(selected);
    }

    @FXML private void handleSave() {
        try {
            String name = nameField != null ? nameField.getText().trim() : "";
            if (name.isEmpty()) {
                showAlert("Validation Error", "Quota Name is required", Alert.AlertType.WARNING);
                return;
            }

            String code = codeField != null ? codeField.getText().trim() : "";

            QuotaMaster q = new QuotaMaster();
            q.setCode(code);
            q.setName(name);

            // Parse Percentage if provided
            if (pctField != null && !pctField.getText().trim().isEmpty()) {
                String cleanPct = pctField.getText().trim().replaceAll("[^0-9.]", "");
                if (!cleanPct.isEmpty()) {
                    q.setPercentage(new BigDecimal(cleanPct));
                } else {
                    q.setPercentage(null);
                }
            } else {
                q.setPercentage(null);
            }

            // Parse Discount Amount if provided
            if (discField != null && !discField.getText().trim().isEmpty()) {
                String cleanDisc = discField.getText().trim().replaceAll("[^0-9.]", "");
                if (!cleanDisc.isEmpty()) {
                    q.setDiscountAmount(new BigDecimal(cleanDisc));
                } else {
                    q.setDiscountAmount(null);
                }
            } else {
                q.setDiscountAmount(null);
            }

            String admType = admTypeCombo != null ? admTypeCombo.getValue() : "Select";
            q.setAdmissionType(admType != null && !"Select".equals(admType) ? admType : null);

            if (editingId != null) {
                service.update(editingId, q);
            } else {
                service.create(q);
            }

            handleAdd();
            loadData();
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter valid numbers for Discount % or Discount Amount", Alert.AlertType.WARNING);
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML private void handleCancel() {
        handleAdd();
    }

    private void handleDelete(QuotaMaster q) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION);
        c.setContentText("Are you sure you want to delete quota: " + q.getName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    service.softDelete(q.getId());
                    handleAdd();
                    loadData();
                } catch (Exception e) {
                    showAlert("Error", "Cannot delete: " + e.getMessage(), Alert.AlertType.ERROR);
                }
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
