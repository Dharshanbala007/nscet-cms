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
import javafx.scene.layout.HBox;
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
    @FXML private TableColumn<User, String> actionsCol;

    @FXML private TextField searchField;
    @FXML private VBox formPane;
    @FXML private Label formTitleLabel;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
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
        setupTableColumns();
        setupRoleCombo();
        table.setItems(tableData);
        loadData();
    }

    private void setupTableColumns() {
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        fullNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        
        roleCol.setCellValueFactory(c -> {
            Set<Role> roles = c.getValue().getRoles();
            if (roles == null || roles.isEmpty()) return new SimpleStringProperty("None");
            return new SimpleStringProperty(roles.stream().map(Role::getName).collect(Collectors.joining(", ")));
        });

        statusCol.setCellValueFactory(c -> {
            User u = c.getValue();
            if (Boolean.TRUE.equals(u.getIsLocked())) return new SimpleStringProperty("Locked");
            return new SimpleStringProperty("Active");
        });

        actionsCol.setCellValueFactory(c -> new SimpleStringProperty(""));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User u = getTableView().getItems().get(getIndex());
                    Button editBtn = new Button("Edit");
                    editBtn.getStyleClass().add("btn-sm");
                    editBtn.setOnAction(e -> handleEdit(u));

                    Button delBtn = new Button("Delete");
                    delBtn.getStyleClass().add("btn-sm-danger");
                    delBtn.setOnAction(e -> handleDelete(u));

                    setGraphic(new HBox(5, editBtn, delBtn));
                }
            }
        });
    }

    private void setupRoleCombo() {
        try {
            List<Role> roles = userService.getAllRoles();
            roleList.clear();
            roleList.addAll(roles);
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
        } catch (Exception e) {
            System.err.println("[UserMasterController] Error loading roles: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            Page<User> page = userService.getAll(searchField.getText(), currentPage, pageSize, "id", "asc");
            tableData.clear();
            tableData.addAll(page.getContent());
            int totalPages = Math.max(page.getTotalPages(), 1);
            pageInfo.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            prevBtn.setDisable(currentPage == 0);
            nextBtn.setDisable(currentPage >= totalPages - 1);
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
        formTitleLabel.setText("Add New User");
        clearForm();
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML
    private void handleEdit(User u) {
        editingId = u.getId();
        formTitleLabel.setText("Edit User: " + u.getUsername());
        usernameField.setText(u.getUsername());
        fullNameField.setText(u.getFullName());
        emailField.setText(u.getEmail());
        passwordField.clear();
        lockedCheck.setSelected(Boolean.TRUE.equals(u.getIsLocked()));

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

        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML
    private void handleSave() {
        try {
            String username = SecurityUtil.sanitize(usernameField.getText());
            String fullName = SecurityUtil.sanitize(fullNameField.getText());
            String email = SecurityUtil.sanitize(emailField.getText());
            String password = passwordField.getText();

            if (username == null || username.trim().isEmpty()) {
                showAlert("Validation Error", "Username is required.", Alert.AlertType.WARNING);
                return;
            }

            if (editingId == null && (password == null || password.trim().isEmpty())) {
                showAlert("Validation Error", "Password is required for new users.", Alert.AlertType.WARNING);
                return;
            }

            if (email != null && !email.trim().isEmpty() && !SecurityUtil.isValidEmail(email.trim())) {
                showAlert("Validation Error", "Invalid email format.", Alert.AlertType.WARNING);
                return;
            }

            User user = new User();
            user.setUsername(username.trim());
            user.setFullName(fullName != null ? fullName.trim() : "");
            user.setEmail(email != null ? email.trim() : "");
            user.setIsLocked(lockedCheck.isSelected());

            Set<Role> selectedRoles = new HashSet<>();
            if (roleCombo.getValue() != null) {
                selectedRoles.add(roleCombo.getValue());
            }

            if (editingId != null) {
                userService.update(editingId, user, password, selectedRoles);
                safeAuditLog("UPDATE", "admin_users", editingId, user.getUsername());
            } else {
                User created = userService.create(user, password, selectedRoles);
                safeAuditLog("CREATE", "admin_users", created.getId(), user.getUsername());
            }

            formPane.setVisible(false);
            formPane.setManaged(false);
            loadData();
        } catch (DuplicateResourceException e) {
            showAlert("Duplicate User", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            showAlert("Error", "Failed to save user: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        formPane.setVisible(false);
        formPane.setManaged(false);
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
                    loadData();
                } catch (Exception e) {
                    showAlert("Error", "Cannot delete user: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void clearForm() {
        usernameField.clear();
        fullNameField.clear();
        emailField.clear();
        passwordField.clear();
        lockedCheck.setSelected(false);
        roleCombo.getSelectionModel().clearSelection();
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
