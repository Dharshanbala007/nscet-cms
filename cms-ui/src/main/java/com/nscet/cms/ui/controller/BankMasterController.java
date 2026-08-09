package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.BankService;
import com.nscet.cms.db.entity.BankMaster;
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
    @FXML private TableColumn<BankMaster, String> actionsCol;
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
        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    BankMaster b = getTableView().getItems().get(getIndex());
                    Button edit = new Button("Edit"); edit.getStyleClass().add("btn-sm");
                    edit.setOnAction(e -> handleEdit(b));
                    Button del = new Button("Delete"); del.getStyleClass().add("btn-sm-danger");
                    del.setOnAction(e -> handleDelete(b));
                    setGraphic(new HBox(5, edit, del));
                }
            }
        });
        table.setItems(tableData);
        loadData();
    }

    private void loadData() {
        Page<BankMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear(); tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d", currentPage + 1, page.getTotalPages()));
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }

    @FXML private void handleAdd() {
        editingId = null; bankNameField.clear(); shortNameField.clear(); accNoField.clear();
        branchField.clear(); ifscField.clear(); remarksField.clear();
        formPane.setVisible(true); formPane.setManaged(true);
    }

    @FXML private void handleEdit(BankMaster b) {
        editingId = b.getId(); bankNameField.setText(b.getBankName()); shortNameField.setText(b.getBankShortName());
        accNoField.setText(b.getAccountNumber()); branchField.setText(b.getBranch());
        ifscField.setText(b.getIfscCode()); remarksField.setText(b.getRemarks());
        formPane.setVisible(true); formPane.setManaged(true);
    }

    @FXML private void handleSave() {
        try {
            BankMaster b = new BankMaster();
            b.setBankName(bankNameField.getText().trim()); b.setBankShortName(shortNameField.getText().trim());
            b.setAccountNumber(accNoField.getText().trim()); b.setBranch(branchField.getText().trim());
            b.setIfscCode(ifscField.getText().trim()); b.setRemarks(remarksField.getText().trim());
            if (editingId != null) service.update(editingId, b); else service.create(b);
            formPane.setVisible(false); formPane.setManaged(false); loadData();
        } catch (Exception e) { showAlert("Error", e.getMessage(), Alert.AlertType.ERROR); }
    }

    @FXML private void handleCancel() { formPane.setVisible(false); formPane.setManaged(false); }

    private void handleDelete(BankMaster b) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete bank: " + b.getBankName() + "?");
        confirm.showAndWait().ifPresent(r -> { if (r == ButtonType.OK) { service.softDelete(b.getId()); loadData(); } });
    }

    private void showAlert(String t, String m, Alert.AlertType ty) {
        Alert a = new Alert(ty); a.setTitle(t); a.setContentText(m); a.showAndWait();
    }
}
