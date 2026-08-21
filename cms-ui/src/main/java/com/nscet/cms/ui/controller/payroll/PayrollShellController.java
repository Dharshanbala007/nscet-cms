package com.nscet.cms.ui.controller.payroll;

import com.nscet.cms.core.session.UserSession;
import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
public class PayrollShellController implements Initializable {

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

    @Autowired private UserSession userSession;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUserInfo();
        setupNavigation();
        setupSessionTimer();
        NavigationManager.loadModule("payrollDashboard", contentArea);
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
        if (userSession != null && userSession.getCurrentUser() != null) {
            userNameLabel.setText(userSession.getCurrentUser().getFullName());
            portalLabel.setText("PAYROLL");
            academicYearLabel.setText(userSession.getCurrentAcademicYear() != null ? userSession.getCurrentAcademicYear() : "2025-26");
        } else {
            portalLabel.setText("PAYROLL");
            academicYearLabel.setText("2025-26");
        }
    }

    private void setupNavigation() {
        // MASTERS
        mastersMenu.getChildren().clear();
        addMenuItem(mastersMenu, "Leave Master", "leaveMaster");
        addMenuItem(mastersMenu, "Staff Salary Master", "staffSalary");

        // TRANSACTIONS
        transactionsMenu.getChildren().clear();
        addMenuItem(transactionsMenu, "Attendance Entry (Daily)", "attendanceEntry");
        addMenuItem(transactionsMenu, "Attendance Entry (Single)", "attendanceSingle");
        addMenuItem(transactionsMenu, "Increment / Revised Salary", "salaryIncrement");
        addMenuItem(transactionsMenu, "Leave Details & Check", "leaveDetails");
        addMenuItem(transactionsMenu, "Payroll Calculation Engine", "payrollCalc");

        // REPORTS
        reportsMenu.getChildren().clear();
        addMenuItem(reportsMenu, "Payroll Acquittance Report", "payrollReports");
        addMenuItem(reportsMenu, "Payslip Print", "payslipPrint");
        addMenuItem(reportsMenu, "Casual Leave Monthly View", "clMonthlyView");
        addMenuItem(reportsMenu, "Deduction Salary Details Matrix", "deductionSalaryReport");
        addMenuItem(reportsMenu, "OD Admission Duty Report", "odAdmissionReport");

        // TOOLS
        toolsMenu.getChildren().clear();
        addMenuItem(toolsMenu, "Monthly Leave Credit / Deduction", "monthlyLeaveCredit");
        addMenuItem(toolsMenu, "Salary Leave Check", "salaryLeaveCheck");
        addMenuItem(toolsMenu, "Old Salary Structure", "oldSalaryStructure");
        addMenuItem(toolsMenu, "PF / ESI Statements & ECR", "pfEsiTools");
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
        if (userSession != null) userSession.logout();
        NavigationManager.openPortalSelection();
    }

    @FXML
    private void handleDashboard() {
        NavigationManager.loadModule("payrollDashboard", contentArea);
    }
}
