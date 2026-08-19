package com.nscet.cms.ui.controller;

import com.nscet.cms.core.service.StudentService;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
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
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class StudentDetailsController implements Initializable {

    @FXML private TableView<StudentMaster> table;
    @FXML private TableColumn<StudentMaster, String> rollNoCol;
    @FXML private TableColumn<StudentMaster, String> nameCol;
    @FXML private TableColumn<StudentMaster, String> deptCol;
    @FXML private TableColumn<StudentMaster, String> semesterCol;
    @FXML private TableColumn<StudentMaster, String> admissionTypeCol;
    @FXML private TableColumn<StudentMaster, String> actionsCol;

    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private TextField rollNoField;
    @FXML private TextField nameField;
    @FXML private ComboBox<DepartmentMaster> deptCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private ComboBox<String> admissionTypeCombo;

    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    @Autowired private StudentService studentService;
    @Autowired private DepartmentMasterRepository departmentRepository;

    private ObservableList<StudentMaster> tableData = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupCombos();
        table.setItems(tableData);
        loadData();
    }

    private void setupTableColumns() {
        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber() != null ? c.getValue().getRollNumber() : "N/A"));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName() != null ? c.getValue().getName() : "N/A"));
        deptCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCommunity() != null ? c.getValue().getCommunity() : "CSE"));
        semesterCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedium() != null ? c.getValue().getMedium() : "Regular"));
        admissionTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdmissionType() != null ? c.getValue().getAdmissionType() : "Government"));

        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    StudentMaster s = getTableView().getItems().get(getIndex());
                    Button editBtn = new Button("View");
                    editBtn.getStyleClass().add("btn-sm");
                    editBtn.setOnAction(e -> handleEdit(s));
                    setGraphic(new HBox(5, editBtn));
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

            semesterCombo.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8"));
            admissionTypeCombo.setItems(FXCollections.observableArrayList("Government", "Management", "NRI", "Lateral"));
        } catch (Exception e) {
            System.err.println("[StudentDetailsController] Error loading combos: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            Page<StudentMaster> page = studentService.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
            tableData.clear();
            tableData.addAll(page.getContent());
            int totalPages = Math.max(page.getTotalPages(), 1);
            pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            prevBtn.setDisable(currentPage == 0);
            nextBtn.setDisable(currentPage >= totalPages - 1);
        } catch (Exception e) {
            System.err.println("[StudentDetailsController] Error loading students: " + e.getMessage());
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
        rollNoField.clear();
        nameField.clear();
        deptCombo.getSelectionModel().clearSelection();
        semesterCombo.getSelectionModel().clearSelection();
        admissionTypeCombo.getSelectionModel().clearSelection();

        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void handleEdit(StudentMaster s) {
        rollNoField.setText(s.getRollNumber());
        nameField.setText(s.getName());
        if (s.getAdmissionType() != null) {
            admissionTypeCombo.setValue(s.getAdmissionType());
        }

        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML
    private void handleSave() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        loadData();
    }

    @FXML
    private void handleCancel() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }
}
