package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.FeesDetails;
import com.nscet.cms.db.entity.FeesMaster;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import com.nscet.cms.db.repository.FeesDetailsRepository;
import com.nscet.cms.db.repository.FeesMasterRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FeesDetailsController implements Initializable {

    @FXML private TableView<FeesDetails> table;
    @FXML private TableColumn<FeesDetails, String> feeNameCol;
    @FXML private TableColumn<FeesDetails, String> feeTypeCol;
    @FXML private TableColumn<FeesDetails, String> amountCol;
    @FXML private TableColumn<FeesDetails, String> deptCol;
    @FXML private TableColumn<FeesDetails, String> actionsCol;

    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private TextField feeNameField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> feeTypeCombo;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    @Autowired private FeesDetailsRepository feesDetailsRepository;
    @Autowired private FeesMasterRepository feesMasterRepository;
    @Autowired private DepartmentMasterRepository departmentRepository;

    private ObservableList<FeesDetails> tableData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupCombos();
        table.setItems(tableData);
        loadData();
    }

    private void setupTableColumns() {
        feeNameCol.setCellValueFactory(c -> {
            FeesMaster fm = c.getValue().getFeesName();
            return new SimpleStringProperty(fm != null ? fm.getName() : "Tuition Fee");
        });

        feeTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionType() != null ? c.getValue().getAdmissionType() : "Fresh"));

        amountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount() != null ? "\u20B9" + c.getValue().getAmount().toPlainString() : "\u20B90"));

        deptCol.setCellValueFactory(c -> {
            DepartmentMaster d = c.getValue().getDepartment();
            return new SimpleStringProperty(d != null ? d.getName() : "All Departments");
        });

        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    FeesDetails fd = getTableView().getItems().get(getIndex());
                    Button editBtn = new Button("Edit");
                    editBtn.getStyleClass().add("btn-sm");
                    editBtn.setOnAction(e -> handleEdit(fd));

                    Button delBtn = new Button("Delete");
                    delBtn.getStyleClass().add("btn-sm-danger");
                    delBtn.setOnAction(e -> handleDelete(fd));

                    setGraphic(new HBox(5, editBtn, delBtn));
                }
            }
        });
    }

    private void setupCombos() {
        try {
            List<DepartmentMaster> depts = departmentRepository.findAll();
            deptCombo.setItems(FXCollections.observableArrayList(depts));
            deptCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select Department" : item.getName());
                }
            });
            deptCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(DepartmentMaster item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Select Department" : item.getName());
                }
            });

            feeTypeCombo.setItems(FXCollections.observableArrayList("Fresh", "Lateral", "Transfer"));
        } catch (Exception e) {
            System.err.println("[FeesDetailsController] Error loading combos: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").ascending());
            Page<FeesDetails> page = feesDetailsRepository.findAllActive(pageable);
            tableData.clear();
            tableData.addAll(page.getContent());
            int totalPages = Math.max(page.getTotalPages(), 1);
            pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            prevBtn.setDisable(currentPage == 0);
            nextBtn.setDisable(currentPage >= totalPages - 1);
        } catch (Exception e) {
            System.err.println("[FeesDetailsController] Error loading fee details: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        currentPage = 0;
        loadData();
    }

    @FXML
    private void handlePrevious() {
        if (currentPage > 0) {
            currentPage--;
            loadData();
        }
    }

    @FXML
    private void handleNext() {
        currentPage++;
        loadData();
    }

    @FXML
    private void handleAdd() {
        editingId = null;
        feeNameField.clear();
        amountField.clear();
        feeTypeCombo.getSelectionModel().clearSelection();
        deptCombo.getSelectionModel().clearSelection();

        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void handleEdit(FeesDetails fd) {
        editingId = fd.getId();
        if (fd.getFeesName() != null) {
            feeNameField.setText(fd.getFeesName().getName());
        } else {
            feeNameField.setText("Tuition Fee");
        }

        if (fd.getAmount() != null) {
            amountField.setText(fd.getAmount().toPlainString());
        }

        if (fd.getAdmissionType() != null) {
            feeTypeCombo.setValue(fd.getAdmissionType());
        }

        if (fd.getDepartment() != null) {
            deptCombo.setValue(fd.getDepartment());
        }

        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML
    private void handleSave() {
        try {
            String name = feeNameField.getText();
            String amtStr = amountField.getText();

            if (amtStr == null || amtStr.trim().isEmpty()) {
                showAlert("Validation Error", "Amount is required.", Alert.AlertType.WARNING);
                return;
            }

            BigDecimal amount = new BigDecimal(amtStr.trim());

            FeesDetails fd = editingId != null ? feesDetailsRepository.findById(editingId).orElse(new FeesDetails()) : new FeesDetails();
            fd.setAmount(amount);
            if (feeTypeCombo.getValue() != null) {
                fd.setAdmissionType(feeTypeCombo.getValue());
            }
            if (deptCombo.getValue() != null) {
                fd.setDepartment(deptCombo.getValue());
            }

            feesDetailsRepository.save(fd);

            formPane.setVisible(false);
            formPane.setManaged(false);
            loadData();
        } catch (Exception e) {
            showAlert("Error", "Failed to save fee details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    private void handleDelete(FeesDetails fd) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this fee detail record?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    fd.setIsActive(false);
                    feesDetailsRepository.save(fd);
                    loadData();
                } catch (Exception e) {
                    showAlert("Error", "Cannot delete record: " + e.getMessage(), Alert.AlertType.ERROR);
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
