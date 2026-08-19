package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.DepartmentService;
import com.nscet.cms.core.service.FeesDetailsService;
import com.nscet.cms.core.service.FeesService;
import com.nscet.cms.core.service.QuotaService;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.FeesDetails;
import com.nscet.cms.db.entity.FeesMaster;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class FeesDetailsController implements Initializable {

    @FXML private TableView<FeesDetails> table;
    @FXML private TableColumn<FeesDetails, String> semesterCol;
    @FXML private TableColumn<FeesDetails, String> feesNameCol;
    @FXML private TableColumn<FeesDetails, String> admissionTypeCol;
    @FXML private TableColumn<FeesDetails, String> amountCol;
    @FXML private TableColumn<FeesDetails, String> fromDateCol;
    @FXML private TableColumn<FeesDetails, String> toDateCol;
    @FXML private TableColumn<FeesDetails, String> quotaCol;
    @FXML private TableColumn<FeesDetails, String> actionsCol;

    @FXML private TextField searchField;
    @FXML private VBox formPane;

    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private ComboBox<String> degreeCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<QuotaMaster> quotaCombo;
    @FXML private ComboBox<FeesMaster> feesCombo;
    @FXML private ComboBox<String> admissionTypeCombo;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private TextField amountField;

    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    @Autowired private FeesDetailsService feesDetailsService;
    @Autowired private FeesService feesService;
    @Autowired private QuotaService quotaService;
    @Autowired private DepartmentService departmentService;

    private ObservableList<FeesDetails> tableData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupFormFields();
        table.setItems(tableData);
        loadData();
    }

    private void setupTableColumns() {
        semesterCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSemester() != null ? String.valueOf(c.getValue().getSemester()) : ""));
        feesNameCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFeesName() != null ? c.getValue().getFeesName().getName() : ""));
        admissionTypeCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAdmissionType() != null ? c.getValue().getAdmissionType() : ""));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAmount() != null ? c.getValue().getAmount().toString() : ""));
        fromDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFromDate() != null ? c.getValue().getFromDate().format(dateFormatter) : ""));
        toDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getToDate() != null ? c.getValue().getToDate().format(dateFormatter) : ""));
        quotaCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getQuota() != null ? c.getValue().getQuota().getName() : ""));
        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    FeesDetails fd = getTableView().getItems().get(getIndex());
                    Button edit = new Button("Edit");
                    edit.getStyleClass().add("btn-sm");
                    edit.setOnAction(e -> handleEdit(fd));
                    Button del = new Button("Delete");
                    del.getStyleClass().add("btn-sm-danger");
                    del.setOnAction(e -> handleDelete(fd));
                    setGraphic(new HBox(5, edit, del));
                }
            }
        });
    }

    private void setupFormFields() {
        degreeCombo.getItems().addAll("Select", "B.Tech", "M.Tech", "MBA", "MCA", "B.Sc", "M.Sc", "B.Com", "M.Com", "BCA");
        degreeCombo.getSelectionModel().selectFirst();

        semesterCombo.getItems().addAll("Select", "1", "2", "3", "4", "5", "6", "7", "8");
        semesterCombo.getSelectionModel().selectFirst();

        admissionTypeCombo.getItems().addAll("Select", "Fresh", "Lateral", "Management", "Sports", "NRI", "NCC");
        admissionTypeCombo.getSelectionModel().selectFirst();

        List<FeesMaster> feesList = feesService.getAllActiveList();
        feesCombo.getItems().add(null);
        feesCombo.getItems().addAll(feesList);
        feesCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(FeesMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
        feesCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(FeesMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });

        List<QuotaMaster> quotaList = quotaService.getAll(null, 0, 100, "id", "asc").getContent();
        quotaCombo.getItems().add(null);
        quotaCombo.getItems().addAll(quotaList);
        quotaCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(QuotaMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
        quotaCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(QuotaMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });

        List<DepartmentMaster> deptList = departmentService.getAllActive();
        deptCombo.getItems().add(null);
        deptCombo.getItems().addAll(deptList);
        deptCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(DepartmentMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
        deptCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(DepartmentMaster item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select" : item.getName());
            }
        });
    }

    private void loadData() {
        Page<FeesDetails> page = feesDetailsService.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
        tableData.clear();
        tableData.addAll(page.getContent());
        pageInfo.setText(String.format("Page %d of %d", currentPage + 1, page.getTotalPages()));
        prevBtn.setDisable(currentPage == 0);
        nextBtn.setDisable(currentPage >= page.getTotalPages() - 1);
    }

    @FXML private void handleSearch() {
        currentPage = 0;
        loadData();
    }

    @FXML private void handlePrevious() {
        currentPage--;
        loadData();
    }

    @FXML private void handleNext() {
        currentPage++;
        loadData();
    }

    @FXML private void handleAdd() {
        editingId = null;
        clearForm();
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML private void handleEdit(FeesDetails fd) {
        editingId = fd.getId();
        fromDate.setValue(fd.getFromDate());
        toDate.setValue(fd.getToDate());
        if (fd.getDegree() != null) degreeCombo.getSelectionModel().select(fd.getDegree());
        if (fd.getSemester() != null) semesterCombo.getSelectionModel().select(String.valueOf(fd.getSemester()));
        feesCombo.getSelectionModel().select(fd.getFeesName());
        if (fd.getAdmissionType() != null) admissionTypeCombo.getSelectionModel().select(fd.getAdmissionType());
        quotaCombo.getSelectionModel().select(fd.getQuota());
        deptCombo.getSelectionModel().select(fd.getDepartment());
        amountField.setText(fd.getAmount() != null ? fd.getAmount().toString() : "");
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML private void handleSave() {
        try {
            if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Amount is required.").showAndWait();
                return;
            }
            FeesDetails fd = new FeesDetails();
            fd.setFromDate(fromDate.getValue());
            fd.setToDate(toDate.getValue());
            fd.setDegree(degreeCombo.getValue() != null && !"Select".equals(degreeCombo.getValue()) ? degreeCombo.getValue() : null);
            fd.setSemester(semesterCombo.getValue() != null && !"Select".equals(semesterCombo.getValue()) ? Integer.valueOf(semesterCombo.getValue()) : null);
            fd.setQuota(quotaCombo.getValue());
            fd.setFeesName(feesCombo.getValue());
            fd.setAdmissionType(admissionTypeCombo.getValue() != null && !"Select".equals(admissionTypeCombo.getValue()) ? admissionTypeCombo.getValue() : null);
            fd.setDepartment(deptCombo.getValue());
            fd.setAmount(new BigDecimal(amountField.getText().trim()));

            if (editingId != null) {
                feesDetailsService.update(editingId, fd);
            } else {
                feesDetailsService.create(fd);
            }
            formPane.setVisible(false);
            formPane.setManaged(false);
            loadData();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Invalid amount value.").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML private void handleFormAdd() {
        editingId = null;
        clearForm();
    }

    @FXML private void handleFormModify() {
        FeesDetails selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            handleEdit(selected);
        } else {
            new Alert(Alert.AlertType.WARNING, "Select a record to modify.").showAndWait();
        }
    }

    @FXML private void handleFormDelete() {
        FeesDetails selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            handleDelete(selected);
        } else {
            new Alert(Alert.AlertType.WARNING, "Select a record to delete.").showAndWait();
        }
    }

    @FXML private void handleCancel() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    @FXML private void handleClose() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    private void handleDelete(FeesDetails fd) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete this fee detail?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                feesDetailsService.softDelete(fd.getId());
                loadData();
            }
        });
    }

    private void clearForm() {
        fromDate.setValue(null);
        toDate.setValue(null);
        degreeCombo.getSelectionModel().selectFirst();
        semesterCombo.getSelectionModel().selectFirst();
        admissionTypeCombo.getSelectionModel().selectFirst();
        feesCombo.getSelectionModel().selectFirst();
        quotaCombo.getSelectionModel().selectFirst();
        deptCombo.getSelectionModel().selectFirst();
        amountField.clear();
    }
}
