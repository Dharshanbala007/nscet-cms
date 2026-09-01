package com.nscet.cms.ui.controller;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.security.SecurityUtil;
import com.nscet.cms.core.service.AuditService;
import com.nscet.cms.core.service.UserService;
import com.nscet.cms.db.entity.Role;
import com.nscet.cms.db.entity.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class UserMasterController implements Initializable {

    @FXML private TableView<User> table;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> fullNameCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TableColumn<User, String> statusCol;

    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private Label formTitleLabel;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> staffNameCombo;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private CheckBox lockedCheck;

    @FXML private Label pageInfo;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;

    @Autowired private UserService userService;
    @Autowired(required = false) private AuditService auditService;

    private ObservableList<User> tableData = FXCollections.observableArrayList();
    private ObservableList<Role> roleList = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (staffNameCombo != null) {
            staffNameCombo.getItems().clear();
            staffNameCombo.getItems().addAll("Select", "Dr. K. Arulraj", "Prof. M. Selvam", "Prof. P. Ramkumar", "Prof. S. Karthik", "Prof. R. Priya");
            staffNameCombo.getSelectionModel().selectFirst();
        }
        setupTableColumns();
        setupRoleCombo();
        if (table != null) {
            table.setItems(tableData);
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    handleEdit(newSel);
                }
            });
        }

        loadData();
    }

    private void setupTableColumns() {
        if (usernameCol != null) usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        if (fullNameCol != null) fullNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        if (emailCol != null) emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        
        if (roleCol != null) roleCol.setCellValueFactory(c -> {
            Set<Role> roles = c.getValue().getRoles();
            if (roles == null || roles.isEmpty()) return new SimpleStringProperty("None");
            return new SimpleStringProperty(roles.stream().map(Role::getName).collect(Collectors.joining(", ")));
        });

        if (statusCol != null) statusCol.setCellValueFactory(c -> {
            User u = c.getValue();
            if (Boolean.TRUE.equals(u.getIsLocked())) return new SimpleStringProperty("Locked");
            return new SimpleStringProperty("Active");
        });
    }

    private void setupRoleCombo() {
        try {
            List<Role> roles = userService.getAllRoles();
            roleList.clear();
            roleList.addAll(roles);
            if (roleCombo != null) {
                roleCombo.setItems(roleList);
                roleCombo.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(Role item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "Select Role" : item.getName() + " (" + item.getDescription() + ")");
                    }
                });
                roleCombo.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(Role item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "Select Role" : item.getName());
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("[UserMasterController] Error loading roles: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            Page<User> page = userService.getAll(searchField != null ? searchField.getText() : "", currentPage, pageSize, "id", "asc");
            tableData.clear();
            tableData.addAll(page.getContent());
            int totalPages = Math.max(page.getTotalPages(), 1);
            if (pageInfo != null) pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            if (prevBtn != null) prevBtn.setDisable(currentPage == 0);
            if (nextBtn != null) nextBtn.setDisable(currentPage >= totalPages - 1);
        } catch (Exception e) {
            System.err.println("[UserMasterController] Error loading users: " + e.getMessage());
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
        editingId = null;
        if (formTitleLabel != null) formTitleLabel.setText("Add New User");
        clearForm();
        if (formPane != null) {
            formPane.setVisible(true);
            formPane.setManaged(true);
        }
    }

    @FXML
    private void handleEdit(User u) {
        editingId = u.getId();
        if (formTitleLabel != null) formTitleLabel.setText("Edit User: " + u.getUsername());
        if (usernameField != null) usernameField.setText(u.getUsername());
        if (fullNameField != null) fullNameField.setText(u.getFullName());
        if (emailField != null) emailField.setText(u.getEmail());
        if (passwordField != null) passwordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();
        if (lockedCheck != null) lockedCheck.setSelected(Boolean.TRUE.equals(u.getIsLocked()));

        if (roleCombo != null) {
            if (u.getRoles() != null && !u.getRoles().isEmpty()) {
                Role userRole = u.getRoles().iterator().next();
                for (Role r : roleList) {
                    if (r.getId().equals(userRole.getId())) {
                        roleCombo.setValue(r);
                        break;
                    }
                }
            } else {
                roleCombo.getSelectionModel().clearSelection();
            }
        }

        if (formPane != null) {
            formPane.setVisible(true);
            formPane.setManaged(true);
        }
    }

    @FXML
    private void handleModify() {
        if (table == null) return;
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a user to modify.", Alert.AlertType.WARNING);
            return;
        }
        handleEdit(selected);
    }

    @FXML
    private void handleDeleteSelected() {
        if (table == null) return;
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a user to delete.", Alert.AlertType.WARNING);
            return;
        }
        handleDelete(selected);
    }

    @FXML
    private void handleSave() {
        try {
            String username = usernameField != null ? SecurityUtil.sanitize(usernameField.getText()) : "";
            String staffName = staffNameCombo != null ? staffNameCombo.getValue() : "Select";
            String fullName = fullNameField != null ? SecurityUtil.sanitize(fullNameField.getText()) : ("Select".equals(staffName) || staffName == null ? username : staffName);
            String email = emailField != null ? SecurityUtil.sanitize(emailField.getText()) : (username + "@nscet.edu");
            String password = passwordField != null ? passwordField.getText() : "";
            String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText() : "";

            if (username == null || username.trim().isEmpty()) {
                showAlert("Validation Error", "Login Name (Username) is required.", Alert.AlertType.WARNING);
                return;
            }

            if (editingId == null && (password == null || password.trim().isEmpty())) {
                showAlert("Validation Error", "Password is required for new users.", Alert.AlertType.WARNING);
                return;
            }

            if (password != null && !password.isEmpty() && confirmPasswordField != null && confirmPasswordField.getText() != null && !password.equals(confirmPassword)) {
                showAlert("Validation Error", "Passwords do not match.", Alert.AlertType.WARNING);
                return;
            }

            User user = new User();
            user.setUsername(username.trim());
            user.setFullName(fullName != null && !fullName.trim().isEmpty() ? fullName.trim() : username.trim());
            user.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : username.trim() + "@nscet.edu");
            user.setIsLocked(lockedCheck != null ? lockedCheck.isSelected() : false);

            Set<Role> selectedRoles = new HashSet<>();
            if (roleCombo != null && roleCombo.getValue() != null) {
                selectedRoles.add(roleCombo.getValue());
            }

            if (editingId != null) {
                userService.update(editingId, user, password, selectedRoles);
                safeAuditLog("UPDATE", "admin_users", editingId, user.getUsername());
            } else {
                User created = userService.create(user, password, selectedRoles);
                safeAuditLog("CREATE", "admin_users", created.getId(), user.getUsername());
            }

            clearForm();
            loadData();
            showAlert("Success", "User details saved successfully.", Alert.AlertType.INFORMATION);
        } catch (DuplicateResourceException e) {
            showAlert("Duplicate User", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            showAlert("Error", "Failed to save user: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
    }

    private void handleDelete(User u) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete user: " + u.getUsername() + "?");

        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    userService.softDelete(u.getId());
                    safeAuditLog("DELETE", "admin_users", u.getId(), u.getUsername());
                    clearForm();
                    loadData();
                } catch (Exception e) {
                    showAlert("Error", "Cannot delete user: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void clearForm() {
        editingId = null;
        if (usernameField != null) usernameField.clear();
        if (fullNameField != null) fullNameField.clear();
        if (emailField != null) emailField.clear();
        if (passwordField != null) passwordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();
        if (lockedCheck != null) lockedCheck.setSelected(false);
        if (staffNameCombo != null) staffNameCombo.getSelectionModel().selectFirst();
        if (roleCombo != null) roleCombo.getSelectionModel().clearSelection();
        if (table != null) table.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void safeAuditLog(String action, String tableName, Long recordId, String details) {
        try {
            if (auditService != null) {
                auditService.log(action, tableName, recordId, null, details, null);
            }
        } catch (Exception e) {
            System.err.println("[UserMasterController] Audit log failed: " + e.getMessage());
        }
    }
}
