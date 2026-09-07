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
    @FXML private Label footerInfoLabel;
    @FXML private StackPane contentArea;

    @FXML private ToggleButton mastersToggle;
    @FXML private ToggleButton transactionsToggle;
    @FXML private ToggleButton reportsToggle;
    @FXML private ToggleButton toolsToggle;
    @FXML private ToggleButton staffClubToggle;

    @FXML private VBox mastersMenu;
    @FXML private VBox transactionsMenu;
    @FXML private VBox reportsMenu;
    @FXML private VBox toolsMenu;
    @FXML private VBox staffClubMenu;

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
        portalLabel.setText("PAYROLL");
        academicYearLabel.setText("2025-26");
        if (userNameLabel != null) {
            if (userSession != null && userSession.getCurrentUser() != null 
                    && !"Accounts Portal User".equals(userSession.getCurrentUser().getFullName())) {
                userNameLabel.setText(userSession.getCurrentUser().getFullName());
            } else {
                userNameLabel.setText("Payroll Administrator");
            }
        }
        if (footerInfoLabel != null) {
            String name = (userSession != null && userSession.getCurrentUser() != null && !"Accounts Portal User".equals(userSession.getCurrentUser().getFullName()))
                    ? userSession.getCurrentUser().getFullName().toUpperCase() : "PAYROLL ADMINISTRATOR";
            String username = (userSession != null && userSession.getCurrentUser() != null) ? userSession.getCurrentUser().getUsername() : "payroll";
            footerInfoLabel.setText(String.format("Logged User: %s | Username: %s | Role: ADMIN | Academic Year: 2025-26 | Nadar Saraswathi College of Engineering and Technology, Theni - Payroll Portal",
                    name, username));
        }
    }

    private void setupNavigation() {
        // MASTERS
        mastersMenu.getChildren().clear();
        addMenuItem(mastersMenu, "Leave Master", "leaveMaster");
        addMenuItem(mastersMenu, "Staff Salary Master", "staffSalary");
        addMenuItem(mastersMenu, "Salary Structure Definition", "salaryStructure");
        addMenuItem(mastersMenu, "Pay Bank Account Master", "payBankAccount");

        // TRANSACTIONS
        transactionsMenu.getChildren().clear();
        addMenuItem(transactionsMenu, "Attendance", "attendanceEntry");
        addMenuItem(transactionsMenu, "Attendance Single Entry", "attendanceSingle");
        addMenuItem(transactionsMenu, "Staff Details", "staffSalary");
        addMenuItem(transactionsMenu, "Transfer", "staffTransfer");
        addMenuItem(transactionsMenu, "Increment\\Revised Salary Details", "salaryIncrement");
        addMenuItem(transactionsMenu, "Late\\Permission Details", "latePermission");
        addMenuItem(transactionsMenu, "Resign\\Termination Details", "resignTermination");
        addMenuItem(transactionsMenu, "Payroll Calculation Engine", "payrollCalc");

        // REPORTS
        reportsMenu.getChildren().clear();
        addMenuItem(reportsMenu, "Leave Details", "leaveDetails");
        addMenuItem(reportsMenu, "Leave Check", "salaryLeaveCheck");
        addMenuItem(reportsMenu, "Payroll Calculation", "payrollCalc");
        addMenuItem(reportsMenu, "Pay BankAccount", "payBankAccount");
        addMenuItem(reportsMenu, "Payroll Acquittance Report", "payrollReports");
        addMenuItem(reportsMenu, "Salary Tally Statement", "salaryTally");
        addMenuItem(reportsMenu, "Net Salary Difference", "netSalaryDiff");
        addMenuItem(reportsMenu, "Attendance View", "attendanceView");
        addMenuItem(reportsMenu, "LOP Report", "lopReport");
        addMenuItem(reportsMenu, "Daily Attendance Report", "dailyAttendanceReport");
        addMenuItem(reportsMenu, "Full Leave Details", "fullLeaveDetails");
        addMenuItem(reportsMenu, "OD Admission Report", "odAdmissionReport");
        addMenuItem(reportsMenu, "CL Monthly View", "clMonthlyView");

        // TOOLS
        toolsMenu.getChildren().clear();
        addMenuItem(toolsMenu, "Monthly Leave Credit", "monthlyLeaveCredit");
        addMenuItem(toolsMenu, "Deduction Salary", "deductionSalaryReport");
        addMenuItem(toolsMenu, "leave check old", "salaryLeaveCheck");
        addMenuItem(toolsMenu, "Salary Structure", "salaryStructure");

        // STAFF CLUB
        staffClubMenu.getChildren().clear();
        addMenuItem(staffClubMenu, "Staff Club Details", "staffClub");
    }

    private Button lastActiveButton = null;

    private void addMenuItem(VBox menu, String label, String module) {
        Button button = new Button(label);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            if (lastActiveButton != null) {
                lastActiveButton.getStyleClass().remove("active");
            }
            button.getStyleClass().add("active");
            lastActiveButton = button;
            NavigationManager.loadModule(module, contentArea);
        });
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
    private void toggleStaffClub() {
        staffClubMenu.setVisible(!staffClubMenu.isVisible());
        staffClubMenu.setManaged(staffClubMenu.isVisible());
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
