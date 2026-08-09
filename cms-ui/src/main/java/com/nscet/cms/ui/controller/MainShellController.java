package com.nscet.cms.ui.controller;

import com.nscet.cms.core.session.UserSession;
import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class MainShellController implements Initializable {

    private static final int SESSION_TIMEOUT_MINUTES = 15;
    private Timeline sessionTimer;

    @FXML private BorderPane mainPane;
    @FXML private VBox sidebar;
    @FXML private Label userNameLabel;
    @FXML private Label portalLabel;
    @FXML private Label academicYearLabel;
    @FXML private StackPane contentArea;
    @FXML private ToggleButton mastersToggle;
    @FXML private ToggleButton transactionsToggle;
    @FXML private ToggleButton reportsToggle;
    @FXML private ToggleButton toolsToggle;
    @FXML private VBox mastersMenu;
    @FXML private VBox transactionsMenu;
    @FXML private VBox reportsMenu;
    @FXML private VBox toolsMenu;

    @Autowired
    private UserSession userSession;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUserInfo();
        setupNavigation();
        setupSessionTimer();
        NavigationManager.loadModule("dashboard", contentArea);
    }

    private void setupSessionTimer() {
        sessionTimer = new Timeline(new KeyFrame(Duration.minutes(SESSION_TIMEOUT_MINUTES), e -> handleLogout()));
        sessionTimer.setCycleCount(Timeline.INDEFINITE);
        resetSessionTimer();

        mainPane.addEventFilter(MouseEvent.ANY, e -> resetSessionTimer());
        contentArea.addEventFilter(MouseEvent.ANY, e -> resetSessionTimer());
    }

    private void resetSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.stop();
            sessionTimer.playFromStart();
        }
    }

    private void setupUserInfo() {
        if (userSession.getCurrentUser() != null) {
            userNameLabel.setText(userSession.getCurrentUser().getFullName());
            portalLabel.setText(userSession.getPortalType());
            academicYearLabel.setText(userSession.getCurrentAcademicYear());
        }
    }

    private void setupNavigation() {
        mastersMenu.getChildren().clear();
        addMenuItem(mastersMenu, "Designation Master", "designation");
        addMenuItem(mastersMenu, "Fees Master", "fees");
        addMenuItem(mastersMenu, "Bank Master", "bank");
        addMenuItem(mastersMenu, "Department Master", "department");
        addMenuItem(mastersMenu, "Quota Master", "quota");
        addMenuItem(mastersMenu, "Staff Master", "staff");
        addMenuItem(mastersMenu, "Student Master", "student");
        addMenuItem(mastersMenu, "Student Details", "studentDetails");
        addMenuItem(mastersMenu, "Fees Details", "feesDetails");
        addMenuItem(mastersMenu, "User Master", "users");

        transactionsMenu.getChildren().clear();
        addMenuItem(transactionsMenu, "Fee Collection", "feeCollection");
        addMenuItem(transactionsMenu, "Transfer Certificate", "tc");
        addMenuItem(transactionsMenu, "Registration Update", "regUpdate");

        reportsMenu.getChildren().clear();
        addMenuItem(reportsMenu, "Application Report", "appReport");
        addMenuItem(reportsMenu, "Fees Details Report", "feesReport");
        addMenuItem(reportsMenu, "Pending Fees", "pendingFees");
        addMenuItem(reportsMenu, "Pending Bus Fees", "pendingBusFees");
        addMenuItem(reportsMenu, "Headwise Details", "headwise");
        addMenuItem(reportsMenu, "Receipt Reprint", "receiptReprint");
        addMenuItem(reportsMenu, "Strength Report", "strength");
        addMenuItem(reportsMenu, "TC Print", "tcPrint");

        toolsMenu.getChildren().clear();
        addMenuItem(toolsMenu, "Day Settlement", "daySettlement");
        addMenuItem(toolsMenu, "Bulk Fee Entry", "bulkFeeEntry");
        addMenuItem(toolsMenu, "Bus Fees Update", "busFeesUpdate");
        addMenuItem(toolsMenu, "Student Enrollment", "enrollment");
    }

    private void addMenuItem(VBox menu, String label, String module) {
        Button button = new Button(label);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> NavigationManager.loadModule(module, contentArea));
        menu.getChildren().add(button);
    }

    @FXML
    private void toggleMasters() {
        mastersMenu.setVisible(!mastersMenu.isVisible());
        mastersMenu.setManaged(mastersMenu.isVisible());
    }

    @FXML
    private void toggleTransactions() {
        transactionsMenu.setVisible(!transactionsMenu.isVisible());
        transactionsMenu.setManaged(transactionsMenu.isVisible());
    }

    @FXML
    private void toggleReports() {
        reportsMenu.setVisible(!reportsMenu.isVisible());
        reportsMenu.setManaged(reportsMenu.isVisible());
    }

    @FXML
    private void toggleTools() {
        toolsMenu.setVisible(!toolsMenu.isVisible());
        toolsMenu.setManaged(toolsMenu.isVisible());
    }

    @FXML
    private void handleLogout() {
        if (sessionTimer != null) sessionTimer.stop();
        userSession.logout();
        NavigationManager.openPortalSelection();
    }

    @FXML
    private void handleDashboard() {
        NavigationManager.loadModule("dashboard", contentArea);
    }
}
