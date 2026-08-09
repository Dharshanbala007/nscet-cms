# Developer Onboarding Guide

Welcome to the NSCET College Management System! This guide will help you understand the codebase and start contributing.

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java | 17+ | [Eclipse Adoptium](https://adoptium.net/) |
| Maven | 3.8+ | [Apache Maven](https://maven.apache.org/) |
| MySQL | 9.7+ | [MySQL Community](https://dev.mysql.com/) |
| IDE | IntelliJ IDEA / VS Code | With JavaFX plugin |

## Repository Setup

```bash
# 1. Clone the repository
git clone https://github.com/Dharshanbala007/nscet-cms.git
cd nscet-cms

# 2. Create the database
mysql -u root -p1234 -e "CREATE DATABASE nscet_cms;"

# 3. Build the project
mvn clean install -DskipTests

# 4. Run the application
mvn javafx:run -pl cms-ui
```

## Project Architecture

### Module Structure

```
nscet-cms/
├── pom.xml                    # Parent POM (versions, plugins)
├── cms-db/                    # Data Access Layer
│   ├── entity/                # JPA entities (18 classes)
│   ├── repository/            # Spring Data JPA repositories (15)
│   ├── config/                # DatabaseConfig (Spring config)
│   └── db/migration/          # Flyway SQL migrations (V1-V5)
├── cms-core/                  # Business Logic Layer
│   ├── service/               # Service classes (10)
│   ├── security/              # PasswordUtil, SecurityUtil
│   ├── session/               # UserSession (singleton)
│   └── exception/             # Custom exceptions (2)
├── cms-ui/                    # Presentation Layer
│   ├── controller/            # JavaFX controllers (29)
│   ├── navigation/            # NavigationManager
│   ├── NscetCmsApp.java       # Main entry point
│   └── resources/
│       ├── fxml/              # FXML layouts (31)
│       ├── css/main.css       # Theme stylesheet
│       └── images/            # Icons and images
├── cms-reports/               # Report Generation
│   ├── ReportManager.java     # JasperReports integration
│   └── jasper/                # Report templates (.jrxml)
└── cms-installer/             # Windows Installer
    └── installer/             # Inno Setup scripts
```

### Dependency Flow

```
cms-installer → cms-ui → cms-core → cms-db
cms-reports → cms-db
```

## Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| UI | JavaFX 17 + FXML | Desktop GUI |
| Styling | CSS (main.css) | Theme and layout |
| Controls | ControlsFX 11.1.2 | Enhanced UI components |
| Backend | Spring 6.1.6 (IoC only) | Dependency injection |
| ORM | Hibernate 6.4.10 + JPA | Object-relational mapping |
| Database | MySQL 9.7 | Persistent storage |
| Connection Pool | HikariCP 5.1.0 | Database connections |
| Migrations | Flyway 10.5.0 | Schema versioning |
| Security | BCrypt (favre 0.4.1) | Password hashing |
| Reports | JasperReports 6.20.6 | PDF report generation |
| Build | Maven | Build automation |

## Code Conventions

### Naming Conventions

| Item | Convention | Example |
|------|------------|---------|
| Table names | `admin_` prefix + snake_case | `admin_student_master` |
| Entity classes | PascalCase | `StudentMaster` |
| Repository interfaces | Entity + `Repository` | `StudentMasterRepository` |
| Service classes | Entity + `Service` | `StudentService` |
| FXML files | PascalCase | `StudentMaster.fxml` |
| Controller classes | Entity + `Controller` | `StudentMasterController` |
| CSS classes | kebab-case | `module-header`, `btn-primary` |
| Package names | lowercase | `com.nscet.cms.db.entity` |

### Entity Conventions

All entities extending `BaseEntity` inherit:
- `id` (BIGINT, AUTO_INCREMENT)
- `isActive` (BOOLEAN, DEFAULT TRUE)
- `createdBy`, `updatedBy` (BIGINT)
- `createdAt` (TIMESTAMP, @CreationTimestamp)
- `updatedAt` (TIMESTAMP, @UpdateTimestamp)

**Always use `@Column(name = "...")` annotations** to match Flyway snake_case column names.

### ComboBox Conventions

All ComboBoxes across the app must:
1. Have `"Select"` as the first item
2. Set `promptText="Select"`
3. Validation must reject `"Select"` as empty

```java
@FXML private ComboBox<String> myCombo;

@FXML private void initialize() {
    myCombo.getItems().add("Select");
    myCombo.getItems().addAll("Option 1", "Option 2");
    myCombo.getSelectionModel().selectFirst();
}

private boolean validateInput() {
    if (myCombo.getValue() == null || "Select".equals(myCombo.getValue())) {
        showError("Please select an option");
        return false;
    }
    return true;
}
```

### Controller Conventions

All master controllers follow this pattern:

```java
@Component
@Scope("prototype")
public class MyMasterController implements Initializable {

    // FXML fields
    @FXML private TableView<MyEntity> table;
    @FXML private VBox formPane;
    @FXML private TextField searchField;
    @FXML private Label pageInfo;

    // State
    private int currentPage = 0;
    private int pageSize = 20;
    private Long editingId = null;

    // Service (injected)
    @Autowired private MyService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() { /* column cell factories */ }
    private void loadData() { /* paginated fetch */ }

    @FXML private void handleSearch() {
        currentPage = 0;
        loadData();
    }

    @FXML private void handleAdd() {
        editingId = null;
        clearForm();
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML private void handleEdit(MyEntity entity) {
        editingId = entity.getId();
        populateForm(entity);
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    @FXML private void handleSave() {
        try {
            if (!validateInput()) return;
            MyEntity entity = buildEntityFromForm();
            if (editingId != null) {
                service.update(editingId, entity);
            } else {
                service.create(entity);
            }
            formPane.setVisible(false);
            formPane.setManaged(false);
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML private void handleCancel() {
        formPane.setVisible(false);
        formPane.setManaged(false);
    }

    @FXML private void handleDelete(MyEntity entity) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete " + entity.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.softDelete(entity.getId());
                loadData();
            }
        });
    }

    @FXML private void handlePrevious() {
        if (currentPage > 0) { currentPage--; loadData(); }
    }

    @FXML private void handleNext() { currentPage++; loadData(); }

    private void clearForm() { /* reset all fields */ }
    private boolean validateInput() { /* return true if valid */ }
    private MyEntity buildEntityFromForm() { /* populate entity from form */ }
}
```

### Service Conventions

```java
@Service
public class MyService {

    private final MyRepository repository;

    @Autowired
    public MyService(MyRepository repository) {
        this.repository = repository;
    }

    public Page<MyEntity> getAll(String search, int page, int size, String sortBy, String sortDir) {
        // Pagination + search logic
    }

    public MyEntity getById(Long id) {
        return repository.findById(id)
            .filter(e -> e.getIsActive())
            .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id));
    }

    @Transactional
    public MyEntity create(MyEntity entity) {
        entity.setIsActive(true);
        return repository.save(entity);
    }

    @Transactional
    public MyEntity update(Long id, MyEntity updates) {
        MyEntity existing = getById(id);
        // Update fields
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        MyEntity entity = getById(id);
        entity.setIsActive(false);
        repository.save(entity);
    }
}
```

### FXML Conventions

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>

<VBox fx:id="masterContainer" styleClass="master-container"
      xmlns="http://javafx.com/javafx/17"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="com.nscet.cms.ui.controller.MyController">

    <!-- Header -->
    <HBox styleClass="module-header" alignment="CENTER_LEFT" spacing="10" padding="10">
        <Label text="My Module" styleClass="module-title"/>
        <Region HBox.hgrow="ALWAYS"/>
        <TextField fx:id="searchField" styleClass="search-field"
                   promptText="Search..." onKeyTyped="#handleSearch"/>
        <Button text="Add" styleClass="btn-primary" onAction="#handleAdd"/>
    </HBox>

    <!-- Table -->
    <VBox styleClass="table-container" VBox.vgrow="ALWAYS">
        <TableView fx:id="table" styleClass="data-table" VBox.vgrow="ALWAYS">
            <columns>
                <TableColumn fx:id="nameCol" text="Name" prefWidth="200"/>
                <TableColumn fx:id="actionsCol" text="Actions" prefWidth="120"/>
            </columns>
        </TableView>
        <HBox styleClass="pagination-bar" alignment="CENTER" padding="8">
            <Button fx:id="prevBtn" text="Previous" onAction="#handlePrevious"/>
            <Label fx:id="pageInfo" text="Page 1"/>
            <Button fx:id="nextBtn" text="Next" onAction="#handleNext"/>
        </HBox>
    </VBox>

    <!-- Form (hidden by default) -->
    <VBox fx:id="formPane" styleClass="form-container" visible="false" managed="false"
          spacing="15" padding="20">
        <Label text="My Entity Details" styleClass="form-title"/>
        <GridPane hgap="15" vgap="10">
            <!-- Form fields -->
        </GridPane>
        <HBox spacing="10" alignment="CENTER_RIGHT">
            <Button text="Save" styleClass="btn-primary" onAction="#handleSave"/>
            <Button text="Cancel" styleClass="btn-secondary" onAction="#handleCancel"/>
        </HBox>
    </VBox>
</VBox>
```

## Adding a New Module

### Step 1: Entity (cms-db)

```java
// cms-db/src/main/java/com/nscet/cms/db/entity/MyEntity.java
@Entity
@Table(name = "admin_my_entity")
public class MyEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    // getters, setters, constructors
}
```

### Step 2: Repository (cms-db)

```java
// cms-db/src/main/java/com/nscet/cms/db/repository/MyEntityRepository.java
@Repository
public interface MyEntityRepository extends JpaRepository<MyEntity, Long> {

    @Query("SELECT e FROM MyEntity e WHERE e.isActive = true " +
           "AND (LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MyEntity> search(@Param("search") String search, Pageable pageable);

    Page<MyEntity> findByIsActiveTrue(Pageable pageable);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
```

### Step 3: Service (cms-core)

```java
// cms-core/src/main/java/com/nscet/cms/core/service/MyEntityService.java
@Service
public class MyEntityService {

    private final MyEntityRepository repository;

    @Autowired
    public MyEntityService(MyEntityRepository repository) {
        this.repository = repository;
    }

    public Page<MyEntity> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search.trim(), pageable);
        }
        return repository.findByIsActiveTrue(pageable);
    }

    public MyEntity getById(Long id) {
        return repository.findById(id)
            .filter(e -> e.getIsActive())
            .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id));
    }

    @Transactional
    public MyEntity create(MyEntity entity) {
        if (repository.existsByName(entity.getName())) {
            throw new DuplicateResourceException("Entity already exists with name: " + entity.getName());
        }
        entity.setIsActive(true);
        return repository.save(entity);
    }

    @Transactional
    public MyEntity update(Long id, MyEntity updates) {
        MyEntity existing = getById(id);
        if (repository.existsByNameAndIdNot(updates.getName(), id)) {
            throw new DuplicateResourceException("Entity already exists with name: " + updates.getName());
        }
        existing.setName(updates.getName());
        existing.setDescription(updates.getDescription());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        MyEntity entity = getById(id);
        entity.setIsActive(false);
        repository.save(entity);
    }
}
```

### Step 4: Controller (cms-ui)

Follow the [Controller Conventions](#controller-conventions) section above.

### Step 5: FXML (cms-ui)

Follow the [FXML Conventions](#fxml-conventions) section above.

### Step 6: Register in NavigationManager

```java
// cms-ui/src/main/java/com/nscet/cms/ui/navigation/NavigationManager.java
MODULE_FXML_MAP.put("myModule", "/fxml/masters/MyMaster.fxml");
```

### Step 7: Add to MainShellController sidebar

```java
// cms-ui/src/main/java/com/nscet/cms/ui/controller/MainShellController.java
addMenuItem(mastersMenu, "My Module", "myModule");
```

### Step 8: Database Migration (if needed)

```sql
-- cms-db/src/main/resources/db/migration/V6__my_new_table.sql
CREATE TABLE admin_my_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## Navigation System

### NavigationManager

The `NavigationManager` is the central hub for screen navigation:

```java
// Load a module by key
NavigationManager.loadModule("student", contentArea);

// Available module keys:
"designation", "fees", "bank", "department", "quota",
"staff", "student", "studentDetails", "feesDetails", "users",
"feeCollection", "tc", "regUpdate",
"appReport", "feesReport", "pendingFees", "pendingBusFees",
"headwise", "receiptReprint", "strength", "tcPrint",
"daySettlement", "bulkFeeEntry", "busFeesUpdate", "enrollment",
"dashboard"
```

### Adding a New Screen

1. Create FXML in `cms-ui/src/main/resources/fxml/`
2. Create Controller in `cms-ui/src/main/java/com/nscet/cms/ui/controller/`
3. Register in `NavigationManager.MODULE_FXML_MAP`
4. Add menu item in `MainShellController.setupNavigation()`

## Security

### Password Hashing

```java
@Autowired private PasswordUtil passwordUtil;

String hash = passwordUtil.hashPassword("rawPassword");  // BCrypt, 12 rounds
boolean valid = passwordUtil.verifyPassword("rawPassword", hash);
```

### Input Sanitization

```java
String clean = SecurityUtil.sanitize(userInput);  // Strips <>"';&

// Validation
SecurityUtil.isValidPhone("9876543210");  // true
SecurityUtil.isValidAadhar("123456789012");  // true
SecurityUtil.isValidEmail("user@example.com");  // true
SecurityUtil.isValidPassword("StrongP@ss1");  // true
```

### Session Management

```java
@Autowired private UserSession userSession;

userSession.login(user);           // Set current user
userSession.isLoggedIn();          // Check if logged in
userSession.hasRole("ADMIN");      // Check role
userSession.getPortalType();       // ADMIN, ACCOUNTS, PAYROLL, VIEWER
userSession.logout();              // Clear session
```

### Audit Logging

```java
@Autowired(required = false) private AuditService auditService;

// Safe wrapper
private void safeAuditLog(String action, String tableName, Long recordId, String details) {
    try {
        if (auditService != null) {
            auditService.log(action, tableName, recordId, null, details,
                userSession.getCurrentUser() != null ? userSession.getCurrentUser().getId() : null);
        }
    } catch (Exception e) { /* silent */ }
}
```

## CSS Theme

The application uses a custom CSS theme at `cms-ui/src/main/resources/css/main.css` (769 lines).

### Key Style Classes

| Class | Purpose |
|-------|---------|
| `.login-container` | Login page background gradient |
| `.login-card` | Login card with shadow |
| `.top-bar` | Main shell header bar |
| `.sidebar` | Left navigation panel |
| `.sidebar-button` | Navigation menu items |
| `.dashboard-welcome` | Dashboard welcome banner |
| `.dash-card-*` | Dashboard stat cards (blue, green, orange, purple) |
| `.module-header` | Module page header bar |
| `.search-field` | Pill-shaped search input |
| `.btn-primary` | Primary action buttons |
| `.btn-secondary` | Secondary action buttons |
| `.btn-danger` | Danger/delete buttons |
| `.data-table` | Table styling |
| `.form-container` | Form panel |
| `.pagination-bar` | Pagination controls |

### Adding Custom Styles

```css
/* In main.css */
.my-custom-class {
    -fx-background-color: #1a237e;
    -fx-text-fill: white;
    -fx-font-weight: bold;
    -fx-background-radius: 6;
}
```

## Testing

```bash
# Run all tests
mvn test

# Run specific module tests
mvn test -pl cms-db
mvn test -pl cms-core

# Skip tests (for build)
mvn clean install -DskipTests
```

## Build & Run

```bash
# Development run
mvn javafx:run -pl cms-ui

# Full build
mvn clean install -DskipTests

# Build JAR
mvn package -pl cms-ui -am

# Run JAR
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar cms-ui-1.0.0.jar
```

## Windows Installer

```bash
# Build installer
mvn package -pl cms-installer -am

# The Inno Setup script is at:
# cms-installer/src/main/resources/installer/nscet-cms-setup.iss
```

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| `Cannot find FXML` | Check FXML path in `NavigationManager.MODULE_FXML_MAP` |
| `NullPointerException on combo` | Ensure `"Select"` is first item and `selectFirst()` is called |
| `Empty table rows` | Check `FetchType.EAGER` on `@ManyToOne` relationships |
| `Module not loading` | Check `module-error.log` in project root |
| `CSS not applied` | Verify `/css/main.css` path in `NscetCmsApp.start()` |
| `Database connection failed` | Check `application.properties` credentials |

### Error Log

Runtime errors are logged to: `D:\javadev\nscet-cms\module-error.log`

## Getting Help

- **Project Owner:** Dharshanbala (dharshanbala@gmail.com)
- **GitHub:** [Dharshanbala007](https://github.com/Dharshanbala007)
- **Error Log:** `module-error.log` in project root
