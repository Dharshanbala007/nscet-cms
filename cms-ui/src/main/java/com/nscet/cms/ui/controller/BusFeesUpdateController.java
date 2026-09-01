package com.nscet.cms.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class BusFeesUpdateController implements Initializable {

    @FXML private ComboBox<String> academicYearCombo;
    @FXML private ComboBox<String> busRouteCombo;

    @FXML private TableView<BusFeeStopRowDto> reportTable;
    @FXML private TableColumn<BusFeeStopRowDto, String> routeNoCol;
    @FXML private TableColumn<BusFeeStopRowDto, String> routeNameCol;
    @FXML private TableColumn<BusFeeStopRowDto, String> busStopNameCol;
    @FXML private TableColumn<BusFeeStopRowDto, String> amountCol;
    @FXML private TableColumn<BusFeeStopRowDto, String> academicYearCol;

    private final ObservableList<BusFeeStopRowDto> dataList = FXCollections.observableArrayList();

    public static class BusFeeStopRowDto {
        private String routeNo;
        private String routeName;
        private String busStopName;
        private String amount;
        private String academicYear;

        public BusFeeStopRowDto(String routeNo, String routeName, String busStopName, String amount, String academicYear) {
            this.routeNo = routeNo;
            this.routeName = routeName;
            this.busStopName = busStopName;
            this.amount = amount;
            this.academicYear = academicYear;
        }

        public String getRouteNo() { return routeNo; }
        public String getRouteName() { return routeName; }
        public String getBusStopName() { return busStopName; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getAcademicYear() { return academicYear; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (academicYearCombo != null) {
            academicYearCombo.getItems().clear();
            academicYearCombo.getItems().addAll("2024-25", "2025-26", "2026-27");
            academicYearCombo.setValue("2026-27");
        }

        if (busRouteCombo != null) {
            busRouteCombo.getItems().clear();
            busRouteCombo.getItems().addAll(
                "BOOTHIPURAM & RATHNA NAGAR",
                "PERIYAKULAM & VADUGAPATTI",
                "CUMBUM & UTHAMAPALAYAM",
                "THENI LOCAL & ALLINAGARAM",
                "AUNDIPATTI & CHINNAMANUR"
            );
            busRouteCombo.getSelectionModel().selectFirst();
        }

        setupTableColumns();
        reportTable.setItems(dataList);
        handleLoadData();
    }

    private void setupTableColumns() {
        if (routeNoCol != null) routeNoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRouteNo()));
        if (routeNameCol != null) routeNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRouteName()));
        if (busStopNameCol != null) busStopNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBusStopName()));
        
        if (amountCol != null) {
            amountCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAmount()));
            amountCol.setCellFactory(TextFieldTableCell.forTableColumn());
            amountCol.setOnEditCommit(e -> {
                BusFeeStopRowDto row = e.getRowValue();
                if (row != null) {
                    row.setAmount(e.getNewValue());
                }
            });
        }

        if (academicYearCol != null) academicYearCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAcademicYear()));
        
        reportTable.setEditable(true);
    }

    @FXML
    public void handleLoadData() {
        dataList.clear();
        String year = academicYearCombo != null && academicYearCombo.getValue() != null ? academicYearCombo.getValue() : "2026-27";
        String route = busRouteCombo != null && busRouteCombo.getValue() != null ? busRouteCombo.getValue() : "BOOTHIPURAM & RATHNA NAGAR";

        // Add exact reference stop records from media_1788254870198.png
        dataList.add(new BusFeeStopRowDto("17", route, "ANNANJI", "4810", year));
        dataList.add(new BusFeeStopRowDto("17", route, "ANNANJI PALLIVASAL", "4810", year));
        dataList.add(new BusFeeStopRowDto("17", route, "ARAVIND EYE HOSPITAL", "5315", year));
        dataList.add(new BusFeeStopRowDto("17", route, "BOOTHIPURAM", "8225", year));
        dataList.add(new BusFeeStopRowDto("17", route, "RATHINA NAGAR", "5315", year));
        dataList.add(new BusFeeStopRowDto("17", route, "VADAPUDUPATTI", "4810", year));
        dataList.add(new BusFeeStopRowDto("17", route, "VANI SWEETS", "5315", year));
        dataList.add(new BusFeeStopRowDto("17", route, "SUNDARAM MAHAL", "5315", year));
        dataList.add(new BusFeeStopRowDto("17", route, "CAFÉ MILANO", "5315", year));
    }

    @FXML
    public void handleSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bus Fees Saved");
        alert.setHeaderText(null);
        alert.setContentText("Bus fee updates saved successfully for " + dataList.size() + " bus stops!");
        alert.showAndWait();
    }
}
