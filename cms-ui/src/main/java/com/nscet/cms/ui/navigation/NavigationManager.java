package com.nscet.cms.ui.navigation;

import com.nscet.cms.ui.NscetCmsApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class NavigationManager {

    private static final Map<String, String> MODULE_FXML_MAP = new HashMap<>();

    static {
        MODULE_FXML_MAP.put("designation", "/fxml/masters/DesignationMaster.fxml");
        MODULE_FXML_MAP.put("fees", "/fxml/masters/FeesMaster.fxml");
        MODULE_FXML_MAP.put("bank", "/fxml/masters/BankMaster.fxml");
        MODULE_FXML_MAP.put("department", "/fxml/masters/DepartmentMaster.fxml");
        MODULE_FXML_MAP.put("quota", "/fxml/masters/QuotaMaster.fxml");
        MODULE_FXML_MAP.put("staff", "/fxml/masters/StaffMaster.fxml");
        MODULE_FXML_MAP.put("student", "/fxml/masters/StudentMaster.fxml");
        MODULE_FXML_MAP.put("studentDetails", "/fxml/masters/StudentDetails.fxml");
        MODULE_FXML_MAP.put("feesDetails", "/fxml/masters/FeesDetails.fxml");
        MODULE_FXML_MAP.put("users", "/fxml/masters/UserMaster.fxml");
        MODULE_FXML_MAP.put("feeCollection", "/fxml/transactions/FeeCollection.fxml");
        MODULE_FXML_MAP.put("tc", "/fxml/transactions/TransferCertificate.fxml");
        MODULE_FXML_MAP.put("regUpdate", "/fxml/transactions/RegistrationUpdate.fxml");
        MODULE_FXML_MAP.put("appReport", "/fxml/reports/ApplicationReport.fxml");
        MODULE_FXML_MAP.put("feesReport", "/fxml/reports/FeesDetailsReport.fxml");
        MODULE_FXML_MAP.put("pendingFees", "/fxml/reports/PendingFeesReport.fxml");
        MODULE_FXML_MAP.put("pendingBusFees", "/fxml/reports/PendingBusFeesReport.fxml");
        MODULE_FXML_MAP.put("headwise", "/fxml/reports/HeadwiseDetailsReport.fxml");
        MODULE_FXML_MAP.put("receiptReprint", "/fxml/reports/ReceiptReprint.fxml");
        MODULE_FXML_MAP.put("strength", "/fxml/reports/StrengthReport.fxml");
        MODULE_FXML_MAP.put("tcPrint", "/fxml/reports/TcPrint.fxml");
        MODULE_FXML_MAP.put("daySettlement", "/fxml/tools/DaySettlement.fxml");
        MODULE_FXML_MAP.put("bulkFeeEntry", "/fxml/tools/BulkFeeEntry.fxml");
        MODULE_FXML_MAP.put("busFeesUpdate", "/fxml/tools/BusFeesUpdate.fxml");
        MODULE_FXML_MAP.put("enrollment", "/fxml/tools/StudentEnrollment.fxml");
        MODULE_FXML_MAP.put("dashboard", "/fxml/Dashboard.fxml");
        MODULE_FXML_MAP.put("pettyCash", "/fxml/accounts/PettyCash.fxml");
        MODULE_FXML_MAP.put("pettyCashSuspense", "/fxml/accounts/PettyCashSuspense.fxml");
        MODULE_FXML_MAP.put("pettyVoucher", "/fxml/accounts/PettyVoucher.fxml");
        MODULE_FXML_MAP.put("pettyCashDaybook", "/fxml/accounts/PettyCashDaybook.fxml");
        MODULE_FXML_MAP.put("dailyTransaction", "/fxml/accounts/DailyTransaction.fxml");
        MODULE_FXML_MAP.put("accountsDashboard", "/fxml/accounts/AccountsDashboard.fxml");
        MODULE_FXML_MAP.put("functionExpense", "/fxml/accounts/FunctionExpense.fxml");
    }

    private static String currentStylesheet;
    private static StackPane activeContentArea;

    public static StackPane getActiveContentArea() {
        return activeContentArea;
    }

    private static void applyStylesheet(javafx.scene.Scene scene) {
        if (currentStylesheet == null) {
            currentStylesheet = NavigationManager.class.getResource("/css/main.css").toExternalForm();
        }
        if (!scene.getStylesheets().contains(currentStylesheet)) {
            scene.getStylesheets().add(currentStylesheet);
        }
    }

    private static void switchRoot(Parent root) {
        javafx.stage.Stage stage = NscetCmsApp.getPrimaryStage();
        javafx.scene.Scene scene = stage.getScene();
        if (scene == null) {
            scene = new javafx.scene.Scene(root, stage.getWidth(), stage.getHeight());
            applyStylesheet(scene);
            stage.setScene(scene);
        } else {
            applyStylesheet(scene);
            scene.setRoot(root);
        }
    }

    public static void openPortalSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/fxml/PortalSelection.fxml"));
            loader.setControllerFactory(NscetCmsApp.getContext()::getBean);
            Parent root = loader.load();
            switchRoot(root);
            NscetCmsApp.getPrimaryStage().setMinWidth(900);
            NscetCmsApp.getPrimaryStage().setMinHeight(600);
        } catch (Exception e) {
            System.err.println("[NavigationManager] Failed to load PortalSelection");
            e.printStackTrace();
            try (PrintWriter pw = new PrintWriter(new FileWriter("D:\\javadev\\nscet-cms\\module-error.log"))) {
                pw.println("Failed to load PortalSelection");
                e.printStackTrace(pw);
            } catch (Exception ignored) {}
        }
    }

    public static void openLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/fxml/Login.fxml"));
            loader.setControllerFactory(NscetCmsApp.getContext()::getBean);
            Parent root = loader.load();
            switchRoot(root);
            NscetCmsApp.getPrimaryStage().setMinWidth(900);
            NscetCmsApp.getPrimaryStage().setMinHeight(600);
        } catch (Exception e) {
            System.err.println("[NavigationManager] Failed to load Login");
            e.printStackTrace();
            try (PrintWriter pw = new PrintWriter(new FileWriter("D:\\javadev\\nscet-cms\\module-error.log"))) {
                pw.println("Failed to load Login");
                e.printStackTrace(pw);
            } catch (Exception ignored) {}
        }
    }

    public static void openMainShell() {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/fxml/MainShell.fxml"));
            loader.setControllerFactory(NscetCmsApp.getContext()::getBean);
            Parent root = loader.load();
            switchRoot(root);
            NscetCmsApp.getPrimaryStage().setMinWidth(1024);
            NscetCmsApp.getPrimaryStage().setMinHeight(768);
        } catch (Exception e) {
            System.err.println("[NavigationManager] Failed to load MainShell");
            e.printStackTrace();
            try (PrintWriter pw = new PrintWriter(new FileWriter("D:\\javadev\\nscet-cms\\module-error.log"))) {
                pw.println("Failed to load MainShell");
                e.printStackTrace(pw);
            } catch (Exception ignored) {}
        }
    }

    public static void openAccountsShell() {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/fxml/accounts/AccountsShell.fxml"));
            loader.setControllerFactory(NscetCmsApp.getContext()::getBean);
            Parent root = loader.load();
            switchRoot(root);
            NscetCmsApp.getPrimaryStage().setMinWidth(1024);
            NscetCmsApp.getPrimaryStage().setMinHeight(768);
        } catch (Exception e) {
            System.err.println("[NavigationManager] Failed to load AccountsShell");
            e.printStackTrace();
            try (PrintWriter pw = new PrintWriter(new FileWriter("D:\\javadev\\nscet-cms\\module-error.log"))) {
                pw.println("Failed to load AccountsShell");
                e.printStackTrace(pw);
            } catch (Exception ignored) {}
        }
    }

    public static void loadModule(String module, StackPane contentArea) {
        String fxmlPath = MODULE_FXML_MAP.get(module);
        if (fxmlPath == null) {
            loadPlaceholder(module, contentArea);
            return;
        }

        activeContentArea = contentArea;

        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            loader.setControllerFactory(NscetCmsApp.getContext()::getBean);
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (Exception e) {
            System.err.println("[NavigationManager] Failed to load module '" + module + "' from " + fxmlPath);
            e.printStackTrace();
            try (PrintWriter pw = new PrintWriter(new FileWriter("D:\\javadev\\nscet-cms\\module-error.log"))) {
                pw.println("Failed to load module: " + module + " from " + fxmlPath);
                e.printStackTrace(pw);
            } catch (Exception ignored) {}
            loadPlaceholder(module, contentArea);
        }
    }

    public static void loadPlaceholder(String module, StackPane contentArea) {
        javafx.scene.control.Label placeholder = new javafx.scene.control.Label("Module: " + module + "\nComing Soon...");
        placeholder.getStyleClass().add("placeholder-label");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(placeholder);
    }
}
