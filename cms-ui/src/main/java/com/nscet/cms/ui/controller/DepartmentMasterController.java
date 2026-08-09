package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.DepartmentService;
import com.nscet.cms.db.entity.DepartmentMaster;
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
public class DepartmentMasterController implements Initializable {
    @FXML private TableView<DepartmentMaster> table;
    @FXML private TableColumn<DepartmentMaster, String> codeCol, shortNameCol, nameCol, typeCol, actionsCol;
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
        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    DepartmentMaster d = getTableView().getItems().get(getIndex());
                    Button edit = new Button("Edit"); edit.getStyleClass().add("btn-sm");
                    edit.setOnAction(e -> handleEdit(d));
                    Button del = new Button("Delete"); del.getStyleClass().add("btn-sm-danger");
                    del.setOnAction(e -> handleDelete(d));
                    setGraphic(new HBox(5, edit, del));
                }
            }
        });
        typeCombo.getItems().add("Select");
        typeCombo.getItems().addAll("Academic", "Official");
        typeCombo.getSelectionModel().selectFirst();
        table.setItems(tableData); loadData();
    }

    private void loadData() {
        Page<DepartmentMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear(); tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d", currentPage + 1, page.getTotalPages()));
        prevBtn.setDisable(currentPage == 0); nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }
    @FXML private void handleAdd() {
        editingId = null; codeField.clear(); shortNameField.clear(); nameField.clear(); typeCombo.getSelectionModel().selectFirst();
        formPane.setVisible(true); formPane.setManaged(true);
    }
    @FXML private void handleEdit(DepartmentMaster d) {
        editingId = d.getId(); codeField.setText(d.getCode()); shortNameField.setText(d.getShortName());
        nameField.setText(d.getName()); typeCombo.setValue(d.getType());
        formPane.setVisible(true); formPane.setManaged(true);
    }
    @FXML private void handleSave() {
        try {
            DepartmentMaster d = new DepartmentMaster();
            d.setCode(codeField.getText().trim()); d.setShortName(shortNameField.getText().trim());
            d.setName(nameField.getText().trim()); d.setType(typeCombo.getValue());
            if (editingId != null) service.update(editingId, d); else service.create(d);
            formPane.setVisible(false); formPane.setManaged(false); loadData();
        } catch (Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); }
    }
    @FXML private void handleCancel() { formPane.setVisible(false); formPane.setManaged(false); }
    private void handleDelete(DepartmentMaster d) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION); c.setContentText("Delete: " + d.getName() + "?");
        c.showAndWait().ifPresent(r -> { if (r == ButtonType.OK) { service.softDelete(d.getId()); loadData(); } });
    }
}
