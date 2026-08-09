# NSCET College Management System

A comprehensive JavaFX desktop application for managing college administration, built with Java 17, Spring Framework, and MySQL.

## Project Overview

**NSCET CMS** is a full-featured College Management System designed for **Nadar Saraswathi College of Engineering and Technology (NSCET), Theni, Tamil Nadu**. It provides modules for student management, staff management, fee collection, transfer certificates, reports, and more.

### Key Features

- **Admin Portal** - Complete administrative control
- **Student Master** - Full student CRUD with 4-tab form (Personal, Family, Address, Educational)
- **Staff Master** - Staff management with department/designation from DB
- **Fee Collection** - Receipt generation with auto-allocation algorithm
- **Transfer Certificate** - TC generation and printing
- **8 Reports** - Application, Fees, Pending Fees, Bus Fees, Headwise, Receipt Reprint, Strength, TC Print
- **4 Tools** - Day Settlement, Bulk Fee Entry, Bus Fees Update, Student Enrollment
- **Dashboard** - Real-time stats, quick actions, recent transactions
- **Session Management** - 15-minute auto-logout with inactivity timeout
- **Audit Logging** - All CRUD operations logged with timestamps
- **Security** - BCrypt password hashing, brute-force lockout (5 attempts / 30 sec)
- **Role-Based Access** - 4 roles: ADMIN, ACCOUNTS, PAYROLL, VIEWER

## Tech Stack

| Layer | Technology |
|-------|------------|
| **UI** | JavaFX 17, FXML, CSS, ControlsFX 11.1.2 |
| **Backend** | Java 17, Spring Framework 6.1.6 (IoC only) |
| **ORM** | Hibernate 6.4.10, Spring Data JPA 3.2.5 |
| **Database** | MySQL 9.7, HikariCP 5.1.0 |
| **Migrations** | Flyway 10.5.0 |
| **Security** | BCrypt (favre lib 0.4.1) |
| **Reports** | JasperReports 6.20.6 |
| **Build** | Maven (multi-module) |
| **Packaging** | jpackage + Inno Setup |

## Project Structure

```
nscet-cms/
├── pom.xml                    # Parent POM (multi-module)
├── cms-db/                    # Database layer (entities, repositories, migrations)
├── cms-core/                  # Business logic (services, security, session)
├── cms-ui/                    # JavaFX UI (controllers, FXML, CSS)
├── cms-reports/               # JasperReports templates
└── cms-installer/             # jpackage + Inno Setup config
```

## Module Dependency Graph

```
cms-installer
  └── cms-ui
        └── cms-core
              └── cms-db
  └── cms-reports
        └── cms-db
```

## Quick Start

### Prerequisites

1. **Java 17** - [Eclipse Adoptium](https://adoptium.net/)
2. **Maven 3.8+** - [Apache Maven](https://maven.apache.org/)
3. **MySQL 9.7** - [MySQL Community](https://dev.mysql.com/)
4. **MySQL Client** - `mysql.exe` in PATH

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/Dharshanbala007/nscet-cms.git
cd nscet-cms

# 2. Create the database
mysql -u root -p -e "CREATE DATABASE nscet_cms;"

# 3. Update database credentials (if needed)
# Edit cms-db/src/main/resources/application.properties

# 4. Build the project
mvn clean install -DskipTests

# 5. Run the application
mvn javafx:run -pl cms-ui
```

### Default Login

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ADMIN |

## Development

### Running in Development

```bash
# Build and run
mvn javafx:run -pl cms-ui

# Build only (skip tests)
mvn clean install -DskipTests

# Run tests
mvn test
```

### Creating New Modules

See [DEVELOPER.md](DEVELOPER.md) for the complete guide on:
- Adding new entity classes
- Creating repositories and services
- Building FXML forms and controllers
- Database migrations with Flyway
- Navigation registration

### Code Conventions

- **All table names** prefixed with `admin_`
- **All ComboBoxes** have `"Select"` as first item
- **All forms** use `formPane.setVisible(true/false)` pattern
- **Soft deletes** via `isActive` flag (never hard delete)
- **Pagination** with `currentPage`, `pageSize=20`
- **Audit logging** on all CRUD operations
- **Input sanitization** via `SecurityUtil.sanitize()`

## Database

- **20 tables** across 5 modules (Auth, Masters, Students, Staff, Fees, Transactions, Reports, Audit, Accounts, Payroll)
- **55+ staff** records with correct department/designation mapping
- **37 students** with full details
- **15 bank accounts** across Canara, Federal, TMB, SBI
- **15 fee types** across 6 fee groups
- **12 quota types** for Government and Management admissions
- **23 fee receipts** with receipt items

See [DATABASE.md](DATABASE.md) for complete schema documentation.

## Module Status

| Module | Status | Notes |
|--------|--------|-------|
| Login / Auth | Complete | BCrypt, brute-force lockout, audit logging |
| Portal Selection | Complete | Admin/Accounts/Payroll portals |
| Dashboard | Complete | Stats, quick actions, recent transactions |
| Student Master | Complete | 4-tab form, editable combos, validation |
| Staff Master | Complete | 2-tab form, DB-driven dept/desig combos |
| Fee Collection | Complete | Auto-allocation, receipt generation |
| Transfer Certificate | Partial | Form complete, save/print stubs |
| Bank Master | Complete | Full CRUD |
| Department Master | Complete | Full CRUD |
| Designation Master | Complete | Full CRUD |
| Fees Master | Complete | Full CRUD |
| Quota Master | Complete | Full CRUD with live preview |
| User Master | Skeleton | Form only, no persistence |
| Student Details | Skeleton | Form only, no persistence |
| Fees Details | Skeleton | Form only, no persistence |
| Reports (8) | Skeleton | FXML only, no backend logic |
| Tools (4) | Skeleton | FXML only, no backend logic |
| Accounts Module | Placeholder | "Coming Soon" |
| Payroll Module | Placeholder | "Coming Soon" |

## Key Algorithms

### Fee Auto-Allocation (`FeeCollectionController`)
1. **Tuition Fee** - Capped at Rs. 20,000
2. **Other Fee** - Capped at Rs. 15,000
3. **Bus Fee** - Receives remainder

### Receipt Numbering (`FeeCollectionService`)
Format: `MIS-YY-MM-NNNN` (e.g., `MIS-25-08-0001`)

### Session Timeout (`MainShellController`)
- 15-minute inactivity timeout
- Resets on ANY mouse event
- Auto-logout with confirmation

## Screenshots

The application features a modern UI with:
- Navy blue theme with gradient accents
- Card-based dashboard layout
- Responsive sidebar navigation
- Tabbed forms for complex entities
- Pill-shaped search fields
- Custom-styled tables with alternating row colors

## Author

**Dharshanbala** - [Dharshanbala007](https://github.com/Dharshanbala007) | dharshanbala@gmail.com

## License

This project is proprietary software developed for NSCET College, Theni.
