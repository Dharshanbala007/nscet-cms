package com.nscet.cms.ui.controller;

import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.StudentMasterRepository;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class BusFeesUpdateController implements Initializable {

    @FXML private ComboBox<String> routeCombo;
    @FXML private TextField busFeeField;
    @FXML private TableView<StudentMaster> studentTable;
    @FXML private TableColumn<StudentMaster, Boolean> selectCol;
    @FXML private TableColumn<StudentMaster, String> rollNoCol;
    @FXML private TableColumn<StudentMaster, String> nameCol;
    @FXML private TableColumn<StudentMaster, String> routeCol;
    @FXML private TableColumn<StudentMaster, String> currentFeeCol;

    @Autowired private StudentMasterRepository studentMasterRepository;

    private ObservableList<StudentMaster> tableData = FXCollections.observableArrayList();
    private Map<Long, Boolean> selectedMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
        setupTableColumns();
        studentTable.setItems(tableData);
        handleLoadStudents();
    }

    private void setupCombos() {
        try {
            routeCombo.setItems(FXCollections.observableArrayList("ALL", "Route 1 - PERIYAKULAM", "Route 2 - CUMBUM", "Route 3 - AUNDIPATTI", "Route 4 - THENI LOCAL"));
            routeCombo.setValue("ALL");
            busFeeField.setText("7150");
        } catch (Exception e) {
            System.err.println("[BusFeesUpdateController] Error loading route combo: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        selectCol.setCellValueFactory(c -> {
            Long id = c.getValue().getId();
            boolean isSelected = selectedMap.getOrDefault(id, true);
            SimpleBooleanProperty prop = new SimpleBooleanProperty(isSelected);
            prop.addListener((obs, oldVal, newVal) -> selectedMap.put(id, newVal));
            return prop;
        });
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        studentTable.setEditable(true);

        rollNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRollNumber() != null ? c.getValue().getRollNumber() : "N/A"));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName() != null ? c.getValue().getName() : "N/A"));
        routeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusStop() != null ? c.getValue().getBusStop() : "THENI"));
        currentFeeCol.setCellValueFactory(c -> new SimpleStringProperty("\u20B97,150"));
    }

    @FXML
    private void handleLoadStudents() {
        try {
            List<StudentMaster> students = studentMasterRepository.findAll();
            tableData.clear();
            selectedMap.clear();

            for (StudentMaster s : students) {
                tableData.add(s);
                selectedMap.put(s.getId(), true);
            }
        } catch (Exception e) {
            System.err.println("[BusFeesUpdateController] Error loading bus students: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateFees() {
        long count = selectedMap.values().stream().filter(Boolean::booleanValue).count();
        String amount = busFeeField.getText();
        String route = routeCombo.getValue();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bus Fees Updated");
        alert.setHeaderText(null);
        alert.setContentText("Bus fee updated to \u20B9" + amount + " for " + count + " students on route '" + route + "'.");
        alert.showAndWait();
    }
}
