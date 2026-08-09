package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.QuotaService;
import com.nscet.cms.db.entity.QuotaMaster;
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

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class QuotaMasterController implements Initializable {
    @FXML private TableView<QuotaMaster> table;
    @FXML private TableColumn<QuotaMaster, String> codeCol, nameCol, pctCol, amtCol, discCol, admTypeCol, actionsCol;
    @FXML private TextField searchField, codeField, nameField, pctField, amtField, discField;
    @FXML private ComboBox<String> admTypeCombo;
    @FXML private VBox formPane;
    @FXML private Label pageInfo, previewLabel;
    @FXML private Button prevBtn, nextBtn;

    @Autowired private QuotaService service;
    private ObservableList<QuotaMaster> tableData = FXCollections.observableArrayList();
    private int currentPage = 0; private int pageSize = 20; private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCode()));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        pctCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPercentage() != null ? c.getValue().getPercentage() + "%" : ""));
        amtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount() != null ? "₹" + c.getValue().getAmount() : ""));
        discCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDiscountAmount() != null ? "₹" + c.getValue().getDiscountAmount() : ""));
        admTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionType()));
        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    QuotaMaster q = getTableView().getItems().get(getIndex());
                    Button edit = new Button("Edit"); edit.getStyleClass().add("btn-sm");
                    edit.setOnAction(e -> handleEdit(q));
                    Button del = new Button("Delete"); del.getStyleClass().add("btn-sm-danger");
                    del.setOnAction(e -> handleDelete(q));
                    setGraphic(new HBox(5, edit, del));
                }
            }
        });
        admTypeCombo.getItems().add("Select");
        admTypeCombo.getItems().addAll("Government", "Management");
        admTypeCombo.getSelectionModel().selectFirst();
        pctField.textProperty().addListener((obs, o, n) -> updatePreview());
        discField.textProperty().addListener((obs, o, n) -> updatePreview());
        table.setItems(tableData); loadData();
    }

    private void updatePreview() {
        try {
            BigDecimal amt = amtField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(amtField.getText());
            BigDecimal pct = pctField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(pctField.getText());
            BigDecimal disc = discField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(discField.getText());
            BigDecimal calc = amt.multiply(pct).divide(new BigDecimal("100")).add(disc);
            previewLabel.setText(String.format("Live preview: Discount = ₹%.2f", calc));
        } catch (Exception e) { previewLabel.setText("Live preview: Enter valid numbers"); }
    }

    private void loadData() {
        Page<QuotaMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear(); tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d", currentPage + 1, page.getTotalPages()));
        prevBtn.setDisable(currentPage == 0); nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }
    @FXML private void handleAdd() {
        editingId = null; codeField.clear(); nameField.clear(); pctField.clear(); amtField.clear(); discField.clear(); admTypeCombo.getSelectionModel().selectFirst();
        formPane.setVisible(true); formPane.setManaged(true);
    }
    @FXML private void handleEdit(QuotaMaster q) {
        editingId = q.getId(); codeField.setText(q.getCode()); nameField.setText(q.getName());
        pctField.setText(q.getPercentage() != null ? q.getPercentage().toString() : "");
        amtField.setText(q.getAmount() != null ? q.getAmount().toString() : "");
        discField.setText(q.getDiscountAmount() != null ? q.getDiscountAmount().toString() : "");
        admTypeCombo.setValue(q.getAdmissionType());
        formPane.setVisible(true); formPane.setManaged(true);
    }
    @FXML private void handleSave() {
        try {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            if (code.isEmpty() || name.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Code and Name are required").showAndWait();
                return;
            }
            String admType = admTypeCombo.getValue();
            if (admType == null || "Select".equals(admType)) {
                new Alert(Alert.AlertType.WARNING, "Please select Admission Type").showAndWait();
                return;
            }
            QuotaMaster q = new QuotaMaster();
            q.setCode(code); q.setName(name);
            q.setPercentage(pctField.getText().isEmpty() ? null : new BigDecimal(pctField.getText()));
            q.setAmount(amtField.getText().isEmpty() ? null : new BigDecimal(amtField.getText()));
            q.setDiscountAmount(discField.getText().isEmpty() ? null : new BigDecimal(discField.getText()));
            q.setAdmissionType(admType);
            if (editingId != null) service.update(editingId, q); else service.create(q);
            formPane.setVisible(false); formPane.setManaged(false); loadData();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Please enter valid numbers for Discount %, Amount, and Discount Amt").showAndWait();
        } catch (Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); }
    }
    @FXML private void handleCancel() { formPane.setVisible(false); formPane.setManaged(false); }
    private void handleDelete(QuotaMaster q) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION); c.setContentText("Delete: " + q.getName() + "?");
        c.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try { service.softDelete(q.getId()); loadData(); }
                catch (Exception e) { new Alert(Alert.AlertType.ERROR, "Cannot delete: " + e.getMessage()).showAndWait(); }
            }
        });
    }
}
