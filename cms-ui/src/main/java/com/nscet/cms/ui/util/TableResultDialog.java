package com.nscet.cms.ui.util;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TableResultDialog {

    public static <T> void showPopupTable(String title, TableView<T> sourceTable) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title != null ? title : "Query Results View");

        VBox root = new VBox(12);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f8fafc;");

        // Top Header Bar
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(title != null ? title : "Data Table Results");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label totalLabel = new Label("Total Records: " + (sourceTable.getItems() != null ? sourceTable.getItems().size() : 0));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");

        header.getChildren().addAll(titleLabel, spacer, totalLabel);

        // Cloned / Rendered Table View
        TableView<T> popupTable = new TableView<>();
        popupTable.getColumns().addAll(sourceTable.getColumns());
        popupTable.setItems(sourceTable.getItems());
        popupTable.setPrefHeight(420);
        VBox.setVgrow(popupTable, Priority.ALWAYS);

        // Bottom Action Bar with Close Button
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button exportBtn = new Button("Export / Print View");
        exportBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        exportBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Exporting table data view to PDF format...");
            alert.setHeaderText("PDF Export");
            alert.showAndWait();
        });

        Button closeBtn = new Button("Close Window");
        closeBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> stage.close());

        footer.getChildren().addAll(exportBtn, closeBtn);

        root.getChildren().addAll(header, popupTable, footer);

        Scene scene = new Scene(root, 920, 540);
        stage.setScene(scene);
        stage.show();
    }
}
