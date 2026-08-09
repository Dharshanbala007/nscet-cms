package com.nscet.cms.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class TcPrintController implements Initializable {

    @FXML private TextField rollNoField;
    @FXML private VBox detailsPane;
    @FXML private Label nameLabel;
    @FXML private Label rollNoLabel;
    @FXML private Label deptLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML private void handleSearch() {
        detailsPane.setVisible(true);
        detailsPane.setManaged(true);
    }

    @FXML private void handlePrint() {
    }
}
