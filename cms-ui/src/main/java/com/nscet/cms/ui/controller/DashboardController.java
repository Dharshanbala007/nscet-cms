package com.nscet.cms.ui.controller;

import com.nscet.cms.core.session.UserSession;
import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.repository.*;
import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class DashboardController implements Initializable {

    @FXML private Label totalStudents;
    @FXML private Label totalStaff;
    @FXML private Label todayCollection;
    @FXML private Label todayReceipts;
    @FXML private Label pendingFees;
    @FXML private Label deptCount;
    @FXML private Label welcomeSubtitle;
    @FXML private Label welcomeDate;
    @FXML private ImageView bgImage;

    @FXML private TableView<FeeReceipt> recentTable;
    @FXML private TableColumn<FeeReceipt, String> colReceipt;
    @FXML private TableColumn<FeeReceipt, String> colDate;
    @FXML private TableColumn<FeeReceipt, String> colStudent;
    @FXML private TableColumn<FeeReceipt, String> colAmount;
    @FXML private TableColumn<FeeReceipt, String> colAccount;

    @Autowired(required = false) private StudentMasterRepository studentRepo;
    @Autowired(required = false) private StaffMasterRepository staffRepo;
    @Autowired(required = false) private FeeReceiptRepository feeReceiptRepo;
    @Autowired(required = false) private FeesDetailsRepository feesDetailsRepo;
    @Autowired(required = false) private DepartmentMasterRepository deptRepo;
    @Autowired private UserSession userSession;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadBackgroundImage();
        loadWelcomeInfo();
        loadStats();
        setupRecentTable();
        loadRecentTransactions();
    }

    private void loadBackgroundImage() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/nscet.png"));
            bgImage.setImage(img);
            bgImage.setFitWidth(1280);
            bgImage.setFitHeight(800);
            bgImage.setPreserveRatio(true);
            bgImage.setOpacity(0.25);
        } catch (Exception e) {
            System.out.println("[Dashboard] nscet.png not found: " + e.getMessage());
        }
    }

    private void loadWelcomeInfo() {
        try {
            welcomeDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
            if (userSession != null && userSession.getCurrentUser() != null) {
                welcomeSubtitle.setText("Welcome, " + userSession.getCurrentUser().getFullName() + " | " + userSession.getCurrentAcademicYear());
            }
        } catch (Exception e) {
            System.out.println("[Dashboard] Error loading welcome info: " + e.getMessage());
        }
    }

    private void loadStats() {
        try {
            if (studentRepo != null) {
                totalStudents.setText(String.valueOf(studentRepo.count()));
            }
        } catch (Exception e) { totalStudents.setText("0"); }

        try {
            if (staffRepo != null) {
                totalStaff.setText(String.valueOf(staffRepo.count()));
            }
        } catch (Exception e) { totalStaff.setText("0"); }

        try {
            if (feeReceiptRepo != null) {
                java.math.BigDecimal total = feeReceiptRepo.sumTotalByDate(LocalDate.now()).orElse(java.math.BigDecimal.ZERO);
                todayCollection.setText("Rs." + String.format("%,.0f", total));
            }
        } catch (Exception e) { todayCollection.setText("Rs.0"); }

        try {
            if (feeReceiptRepo != null) {
                todayReceipts.setText(feeReceiptRepo.countByReceiptDate(LocalDate.now()) + " receipts today");
            }
        } catch (Exception e) { todayReceipts.setText("0 receipts today"); }

        try {
            if (feesDetailsRepo != null) {
                java.math.BigDecimal total = feesDetailsRepo.sumAmount().orElse(java.math.BigDecimal.ZERO);
                pendingFees.setText("Rs." + String.format("%,.0f", total));
            }
        } catch (Exception e) { pendingFees.setText("Rs.0"); }

        try {
            if (deptRepo != null) {
                deptCount.setText(deptRepo.count() + " departments configured");
            }
        } catch (Exception e) { deptCount.setText("0 departments"); }
    }

    private void setupRecentTable() {
        colReceipt.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                try {
                    return data.getValue().getReceiptNumber();
                } catch (Exception e) { return ""; }
            }
        });
        colDate.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                try {
                    return data.getValue().getReceiptDate() != null ? data.getValue().getReceiptDate().toString() : "";
                } catch (Exception e) { return ""; }
            }
        });
        colStudent.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                try {
                    return data.getValue().getStudent() != null ? data.getValue().getStudent().getName() : "";
                } catch (Exception e) { return ""; }
            }
        });
        colAmount.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                try {
                    return data.getValue().getTotalAmount() != null ? "Rs." + data.getValue().getTotalAmount() : "Rs.0";
                } catch (Exception e) { return "Rs.0"; }
            }
        });
        colAccount.setCellValueFactory(data -> new StringBinding() {
            @Override protected String computeValue() {
                try {
                    return data.getValue().getBaseAccount() != null ? data.getValue().getBaseAccount() : "";
                } catch (Exception e) { return ""; }
            }
        });
    }

    private void loadRecentTransactions() {
        try {
            if (feeReceiptRepo != null) {
                recentTable.setItems(FXCollections.observableArrayList(feeReceiptRepo.findTop10WithStudent()));
            }
        } catch (Exception e) {
            System.out.println("[Dashboard] Error loading transactions: " + e.getMessage());
            recentTable.setItems(FXCollections.observableArrayList());
        }
    }

    private StackPane getContentArea() {
        return NavigationManager.getActiveContentArea();
    }

    @FXML private void handleFeeCollection() { NavigationManager.loadModule("feeCollection", getContentArea()); }
    @FXML private void handleGenerateTC() { NavigationManager.loadModule("tc", getContentArea()); }
    @FXML private void handleDaySettlement() { NavigationManager.loadModule("daySettlement", getContentArea()); }
    @FXML private void handlePendingFees() { NavigationManager.loadModule("pendingFees", getContentArea()); }
    @FXML private void handleStudentMaster() { NavigationManager.loadModule("student", getContentArea()); }
    @FXML private void handleStaffMaster() { NavigationManager.loadModule("staff", getContentArea()); }
    @FXML private void handleViewDepartments() { NavigationManager.loadModule("department", getContentArea()); }
    @FXML private void handleViewDesignations() { NavigationManager.loadModule("designation", getContentArea()); }
}
