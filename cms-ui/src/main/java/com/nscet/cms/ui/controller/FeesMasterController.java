package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.FeesService;
import com.nscet.cms.db.entity.FeesMaster;
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
public class FeesMasterController implements Initializable {
    @FXML private TableView<FeesMaster> table;
    @FXML private TableColumn<FeesMaster, String> nameCol, groupCol, fromDateCol, toDateCol, semesterFeeCol, actionsCol;
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
        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    FeesMaster f = getTableView().getItems().get(getIndex());
                    Button edit = new Button("Edit"); edit.getStyleClass().add("btn-sm");
                    edit.setOnAction(e -> handleEdit(f));
                    Button del = new Button("Delete"); del.getStyleClass().add("btn-sm-danger");
                    del.setOnAction(e -> handleDelete(f));
                    setGraphic(new HBox(5, edit, del));
                }
            }
        });
        groupCombo.getItems().add("Select");
        groupCombo.getItems().addAll("Clg Fees", "Exam Fees", "Miscellaneous", "Bus Fee", "Hostel Fee", "Other");
        groupCombo.getSelectionModel().selectFirst();
        table.setItems(tableData); loadData();
    }

    private void loadData() {
        Page<FeesMaster> page = service.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear(); tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d", currentPage + 1, page.getTotalPages()));
        prevBtn.setDisable(currentPage == 0); nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    @FXML private void handleSearch() { currentPage = 0; loadData(); }
    @FXML private void handlePrevious() { currentPage--; loadData(); }
    @FXML private void handleNext() { currentPage++; loadData(); }
    @FXML private void handleAdd() {
        editingId = null; nameField.clear(); groupCombo.getSelectionModel().selectFirst(); semesterFeeCheck.setSelected(false);
        formPane.setVisible(true); formPane.setManaged(true);
    }
    @FXML private void handleEdit(FeesMaster f) {
        editingId = f.getId(); nameField.setText(f.getName()); groupCombo.setValue(f.getFeesGroup());
        semesterFeeCheck.setSelected(Boolean.TRUE.equals(f.getSemesterFee()));
        formPane.setVisible(true); formPane.setManaged(true);
    }
    @FXML private void handleSave() {
        try {
            FeesMaster f = new FeesMaster();
            f.setName(nameField.getText().trim());
            f.setFeesGroup(groupCombo.getValue() != null && !"Select".equals(groupCombo.getValue()) ? groupCombo.getValue() : null);
            f.setSemesterFee(semesterFeeCheck.isSelected());
            if (editingId != null) service.update(editingId, f); else service.create(f);
            formPane.setVisible(false); formPane.setManaged(false); loadData();
        } catch (Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); }
    }
    @FXML private void handleCancel() { formPane.setVisible(false); formPane.setManaged(false); }
    private void handleDelete(FeesMaster f) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION); c.setContentText("Delete: " + f.getName() + "?");
        c.showAndWait().ifPresent(r -> { if (r == ButtonType.OK) { service.softDelete(f.getId()); loadData(); } });
    }
}
