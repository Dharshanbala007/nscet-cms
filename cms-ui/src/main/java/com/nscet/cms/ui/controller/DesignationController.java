package com.nscet.cms.ui.controller;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.core.service.DesignationService;
import com.nscet.cms.db.entity.DesignationMaster;
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
public class DesignationController implements Initializable {

    @FXML private TableView<DesignationMaster> table;
    @FXML private TableColumn<DesignationMaster, String> codeCol;
    @FXML private TableColumn<DesignationMaster, String> shortNameCol;
    @FXML private TableColumn<DesignationMaster, String> nameCol;
    @FXML private TableColumn<DesignationMaster, String> categoryCol;
    @FXML private TableColumn<DesignationMaster, String> actionsCol;
    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private TextField codeField;
    @FXML private TextField shortNameField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    @Autowired
    private DesignationService service;

    private ObservableList<DesignationMaster> tableData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupCategoryCombo();
        loadData();
    }

    private void setupTable() {
        codeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCode()));
        shortNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getShortName()));
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        categoryCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory()));

        actionsCol.setCellValueFactory(cell -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DesignationMaster desig = getTableView().getItems().get(getIndex());
                    Button editBtn = new Button("Edit");
                    editBtn.getStyleClass().add("btn-sm");
                    editBtn.setOnAction(e -> handleEdit(desig));

                    Button deleteBtn = new Button("Delete");
                    deleteBtn.getStyleClass().add("btn-sm-danger");
                    deleteBtn.setOnAction(e -> handleDelete(desig));

                    HBox actions = new HBox(5, editBtn, deleteBtn);
                    setGraphic(actions);
                }
            }
        });

        table.setItems(tableData);
    }

    private void setupCategoryCombo() {
        categoryCombo.getItems().add("Select");
        categoryCombo.getItems().addAll(
            "Teaching", "Contract", "NT-Tech", "NT-Non Tech", "Office"
        );
        categoryCombo.getSelectionModel().selectFirst();
    }

    private void loadData() {
        String search = searchField.getText();
        Page<DesignationMaster> page = service.getAll(search, currentPage, pageSize, "id", "asc");
        tableData.clear();
        tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d (Total: %d)",
                currentPage + 1, page.getTotalPages(), page.getTotalElements()));
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    @FXML
    private void handleSearch() {
        currentPage = 0;
        loadData();
    }

    @FXML
    private void handlePrevious() {
        currentPage--;
        loadData();
    }

    @FXML
    private void handleNext() {
        currentPage++;
        loadData();
    }

    @FXML
    private void handleAdd() {
        editingId = null;
        codeField.clear();
        shortNameField.clear();
        nameField.clear();
        categoryCombo.getSelectionModel().selectFirst();
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML
    private void handleEdit(DesignationMaster desig) {
        editingId = desig.getId();
        codeField.setText(desig.getCode());
        shortNameField.setText(desig.getShortName());
        nameField.setText(desig.getName());
        categoryCombo.setValue(desig.getCategory());
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML
    private void handleSave() {
        try {
            DesignationMaster desig = new DesignationMaster();
            desig.setCode(codeField.getText().trim());
            desig.setShortName(shortNameField.getText().trim());
            desig.setName(nameField.getText().trim());
            desig.setCategory(categoryCombo.getValue() != null && !"Select".equals(categoryCombo.getValue()) ? categoryCombo.getValue() : null);

            if (editingId != null) {
                service.update(editingId, desig);
                showAlert("Success", "Designation updated successfully", Alert.AlertType.INFORMATION);
            } else {
                service.create(desig);
                showAlert("Success", "Designation created successfully", Alert.AlertType.INFORMATION);
            }

            formPane.setVisible(false);
            formPane.setManaged(false);
            loadData();
        } catch (DuplicateResourceException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            showAlert("Validation Error", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    private void handleDelete(DesignationMaster desig) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete designation: " + desig.getName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.softDelete(desig.getId());
                    showAlert("Success", "Designation deleted successfully", Alert.AlertType.INFORMATION);
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
