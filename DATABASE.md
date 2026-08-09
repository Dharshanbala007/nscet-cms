# Database Schema Documentation

## Overview

- **Database:** MySQL 9.7
- **Database Name:** `nscet_cms`
- **ORM:** Hibernate 6.4.10 with Spring Data JPA
- **Migrations:** Flyway 10.5.0
- **Naming Strategy:** `CamelCaseToUnderscoresNamingStrategy`
- **Connection Pool:** HikariCP 5.1.0

## Connection Properties

```properties
# cms-db/src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/nscet_cms
spring.datasource.username=root
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
```

## Table Summary

| # | Table Name | Entity Class | Records | Purpose |
|---|------------|--------------|---------|---------|
| 1 | `admin_users` | `User` | 1 | User authentication |
| 2 | `admin_roles` | `Role` | 4 | Role definitions |
| 3 | `admin_permissions` | `Permission` | 20 | Permission definitions |
| 4 | `admin_user_roles` | (join table) | 1 | User-Role mapping |
| 5 | `admin_role_permissions` | (join table) | 20 | Role-Permission mapping |
| 6 | `admin_department_master` | `DepartmentMaster` | 25 | Department list |
| 7 | `admin_designation_master` | `DesignationMaster` | 9 | Designation list |
| 8 | `admin_quota_master` | `QuotaMaster` | 12 | Admission quotas |
| 9 | `admin_bank_master` | `BankMaster` | 15 | Bank accounts |
| 10 | `admin_fees_master` | `FeesMaster` | 15 | Fee types |
| 11 | `admin_staff_master` | `StaffMaster` | 55 | Staff records |
| 12 | `admin_student_master` | `StudentMaster` | 37 | Student records |
| 13 | `admin_student_details` | `StudentDetails` | 37 | Semester enrollments |
| 14 | `admin_fees_details` | `FeesDetails` | 16 | Fee structures |
| 15 | `admin_fee_receipts` | `FeeReceipt` | 23 | Fee receipts |
| 16 | `admin_fee_receipt_items` | `FeeReceiptItem` | 23 | Receipt line items |
| 17 | `admin_transfer_certificates` | `TransferCertificate` | 0 | TC records |
| 18 | `admin_day_settlements` | `DaySettlement` | 0 | Daily settlements |
| 19 | `admin_audit_log` | `AuditLog` | varies | Audit trail |
| 20 | `accounts_ledger` | (scaffold) | 0 | Accounts module |
| 21 | `accounts_vouchers` | (scaffold) | 0 | Accounts module |
| 22 | `payroll_salary_structure` | (scaffold) | 0 | Payroll module |
| 23 | `payroll_monthly` | (scaffold) | 0 | Payroll module |

## Entity Relationship Diagram

```
┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│   admin_users        │     │   admin_roles        │     │   admin_permissions  │
│─────────────────────│     │─────────────────────│     │─────────────────────│
│ id (PK)             │     │ id (PK)             │     │ id (PK)             │
│ username (UNIQUE)   │     │ name (UNIQUE)       │     │ name (UNIQUE)       │
│ password_hash       │     │ description         │     │ module              │
│ full_name           │     └──────────┬──────────┘     │ action              │
│ email               │                │                  └─────────────────────┘
│ staff_id            │                │
│ is_active           │                │ admin_role_permissions
│ is_locked           │                │ (role_id, permission_id)
│ failed_attempts     │                │
└──────────┬──────────┘                │
           │                           │
           │ admin_user_roles          │
           │ (user_id, role_id)        │
           │                           │
┌──────────┴──────────────────────────┴──────────────────────────────────────────┐
│                                                                                │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐    │
│  │ admin_department_master│  │ admin_designation_master│  │ admin_quota_master │    │
│  │─────────────────────│  │─────────────────────│  │─────────────────────│    │
│  │ id (PK)             │  │ id (PK)             │  │ id (PK)             │    │
│  │ code (UNIQUE)       │  │ code (UNIQUE)       │  │ code (UNIQUE)       │    │
│  │ short_name          │  │ short_name          │  │ name                │    │
│  │ name                │  │ name                │  │ percentage          │    │
│  │ type                │  │ category            │  │ amount              │    │
│  └──────────┬──────────┘  │ color_code          │  │ discount_amount     │    │
│             │              └──────────┬──────────┘  │ admission_type      │    │
│             │                         │              └──────────┬──────────┘    │
│             │                         │                         │               │
│  ┌──────────┴─────────────────────────┴─────────────────────────┴──────────┐   │
│  │                                                                          │   │
│  │  ┌─────────────────────┐         ┌─────────────────────┐                │   │
│  │  │ admin_staff_master   │         │ admin_student_master │                │   │
│  │  │─────────────────────│         │─────────────────────│                │   │
│  │  │ id (PK)             │         │ id (PK)             │                │   │
│  │  │ staff_code (UNIQUE) │         │ roll_number (UNIQUE)│                │   │
│  │  │ name                │         │ admission_no (UNIQUE│                │   │
│  │  │ department_id (FK)──┼────────>│ name                │                │   │
│  │  │ designation_id (FK)─┼───┐     │ father_name         │                │   │
│  │  └─────────────────────┘   │     │ phone               │                │   │
│  │                            │     │ gender              │                │   │
│  │                            │     │ community           │                │   │
│  │                            │     │ admission_type      │                │   │
│  │                            │     │ date_of_joining     │                │   │
│  │                            │     │ section             │                │   │
│  │                            │     │ occupation          │                │   │
│  │                            │     │ religion            │                │   │
│  │                            │     └──────────┬──────────┘                │   │
│  │                            │                │                           │   │
│  │                            │                │                           │   │
│  │  ┌─────────────────────────┴─────────────────┴───────────────────────┐  │   │
│  │  │                                                                    │  │   │
│  │  │  ┌─────────────────────┐  ┌─────────────────────┐                │  │   │
│  │  │  │ admin_student_details│  │ admin_fee_receipts   │                │  │   │
│  │  │  │─────────────────────│  │─────────────────────│                │  │   │
│  │  │  │ id (PK)             │  │ id (PK)             │                │  │   │
│  │  │  │ student_id (FK)────┼──│ receipt_number (UNIQ)│                │  │   │
│  │  │  │ semester            │  │ student_id (FK)─────┼────────────────│  │   │
│  │  │  │ department_id (FK)──┼──│ student_type         │                │  │   │
│  │  │  │ quota_id (FK)──────┼──│ receipt_date         │                │  │   │
│  │  │  │ degree              │  │ payment_mode         │                │  │   │
│  │  │  │ section             │  │ base_account         │                │  │   │
│  │  │  │ academic_year       │  │ total_amount         │                │  │   │
│  │  │  └─────────────────────┘  │ bank_id (FK)         │                │  │   │
│  │  │                           └──────────┬──────────┘                │  │   │
│  │  │                                      │                           │  │   │
│  │  │  ┌─────────────────────┐             │                           │  │   │
│  │  │  │ admin_fees_details   │             │                           │  │   │
│  │  │  │─────────────────────│             │                           │  │   │
│  │  │  │ id (PK)             │             │                           │  │   │
│  │  │  │ fees_name_id (FK)──┼─────────────┘                           │  │   │
│  │  │  │ department_id (FK)──┼──> admin_department_master              │  │   │
│  │  │  │ quota_id (FK)──────┼──> admin_quota_master                   │  │   │
│  │  │  │ degree              │                                         │  │   │
│  │  │  │ semester            │  ┌─────────────────────┐                │  │   │
│  │  │  │ amount              │  │ admin_fee_receipt_items│               │  │   │
│  │  │  └─────────────────────┘  │─────────────────────│                │  │   │
│  │  │                           │ id (PK)             │                │  │   │
│  │  │                           │ receipt_id (FK)────┼────────────────┘  │   │
│  │  │                           │ fees_name_id (FK)──┼──> admin_fees_master│ │
│  │  │                           │ amount              │                │  │   │
│  │  │                           └─────────────────────┘                │  │   │
│  │  └───────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────────┘   │
│                                                                                │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐    │
│  │ admin_bank_master    │  │ admin_fees_master    │  │ admin_audit_log     │    │
│  │─────────────────────│  │─────────────────────│  │─────────────────────│    │
│  │ id (PK)             │  │ id (PK)             │  │ id (PK)             │    │
│  │ bank_short_name     │  │ name                │  │ user_id             │    │
│  │ account_number      │  │ fees_group          │  │ action              │    │
│  │ bank_name           │  │ from_date           │  │ table_name          │    │
│  │ branch              │  │ to_date             │  │ record_id           │    │
│  │ ifsc_code           │  └─────────────────────┘  │ old_values (TEXT)   │    │
│  └─────────────────────┘                           │ new_values (TEXT)   │    │
│                                                    │ timestamp           │    │
│  ┌─────────────────────┐                           │ ip_address          │    │
│  │ admin_transfer_certs │                           └─────────────────────┘    │
│  │─────────────────────│                                                       │
│  │ id (PK)             │  ┌─────────────────────┐  ┌─────────────────────┐    │
│  │ tc_number (UNIQUE)  │  │ admin_day_settlements│  │ accounts_ledger     │    │
│  │ student_id (FK)────┼──│ (scaffold)           │  │ (scaffold)          │    │
│  │ tc_date             │  │─────────────────────│  │─────────────────────│    │
│  └─────────────────────┘  │ settlement_date     │  │ ledger_name         │    │
│                           │ opening_balance     │  │ ledger_type         │    │
│                           │ cash_collection     │  └─────────────────────┘    │
│                           │ closing_balance     │                             │
│                           │ status              │  ┌─────────────────────┐    │
│                           └─────────────────────┘  │ accounts_vouchers   │    │
│                                                    │ (scaffold)          │    │
│  ┌─────────────────────┐  ┌─────────────────────┐  │─────────────────────│    │
│  │ payroll_salary_struct│  │ payroll_monthly      │  │ voucher_number      │    │
│  │ (scaffold)           │  │ (scaffold)           │  │ voucher_date        │    │
│  │─────────────────────│  │─────────────────────│  │ voucher_type        │    │
│  │ staff_id (FK)───────┼──│ staff_id (FK)        │  └─────────────────────┘    │
│  │ basic_salary        │  │ gross_salary         │                             │
│  │ hra, da, ta, pf, esi│  │ deductions           │                             │
│  └─────────────────────┘  │ net_salary           │                             │
│                           └─────────────────────┘                             │
└────────────────────────────────────────────────────────────────────────────────┘
```

## Table Definitions

### 1. Authentication & Authorization

#### `admin_users`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login username |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hashed |
| full_name | VARCHAR(100) | | Display name |
| email | VARCHAR(100) | | Contact email |
| staff_id | BIGINT | | FK to staff (not enforced by JPA) |
| is_active | BOOLEAN | DEFAULT TRUE | Soft delete flag |
| is_locked | BOOLEAN | DEFAULT FALSE | Account lockout |
| failed_attempts | INT | DEFAULT 0 | Brute-force counter |
| created_by | BIGINT | | Audit: creator |
| updated_by | BIGINT | | Audit: updater |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | |

#### `admin_roles`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| name | VARCHAR(50) | UNIQUE, NOT NULL | ADMIN, ACCOUNTS, PAYROLL, VIEWER |
| description | VARCHAR(200) | | |

#### `admin_permissions`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| name | VARCHAR(100) | UNIQUE, NOT NULL | e.g., STUDENT_VIEW |
| module | VARCHAR(50) | NOT NULL | e.g., STUDENT |
| action | VARCHAR(50) | NOT NULL | e.g., VIEW, CREATE, UPDATE, DELETE |

#### `admin_user_roles` (Join Table)
| Column | Type | Constraints |
|--------|------|-------------|
| user_id | BIGINT | FK → admin_users, PK |
| role_id | BIGINT | FK → admin_roles, PK |

#### `admin_role_permissions` (Join Table)
| Column | Type | Constraints |
|--------|------|-------------|
| role_id | BIGINT | FK → admin_roles, PK |
| permission_id | BIGINT | FK → admin_permissions, PK |

---

### 2. Master Data

#### `admin_department_master`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| code | VARCHAR(10) | UNIQUE, NOT NULL | e.g., CSE, MECH |
| short_name | VARCHAR(20) | NOT NULL | |
| name | VARCHAR(100) | NOT NULL | Full name |
| type | VARCHAR(50) | | Academic / Official |
| is_active | BOOLEAN | DEFAULT TRUE | |
| created_by, updated_by | BIGINT | | Audit |
| created_at, updated_at | TIMESTAMP | | Audit |

**Seed Data (25 departments):**
MECH, CE, ECE, CSE, ACCOUNTS, ADMIN, SYSADMIN, TRANSPORT, LIB, CANTEEN, PHYSICS, CHEMISTRY, MATHS, PD, ENGLISH, T.P.O, S&H, EEE, ME, IT, AI, SE, TAMIL, EST, ME.CS

#### `admin_designation_master`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| code | VARCHAR(10) | UNIQUE, NOT NULL | |
| short_name | VARCHAR(20) | NOT NULL | |
| name | VARCHAR(100) | NOT NULL | |
| category | VARCHAR(50) | | Teaching, Contract, NT-Tech, NT-Non Tech, Office |
| color_code | VARCHAR(20) | | UI color hint |
| is_active | BOOLEAN | DEFAULT TRUE | |
| created_by, updated_by | BIGINT | | Audit |
| created_at, updated_at | TIMESTAMP | | Audit |

**Seed Data (9 designations):**
AP, HOD, Associate Professor, Library, PD, P.Co-ordinator, Teaching Fellow, Dept Incharge, EOC

#### `admin_quota_master`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| code | VARCHAR(15) | UNIQUE, NOT NULL | |
| name | VARCHAR(100) | NOT NULL | |
| percentage | DECIMAL(5,2) | | Discount percentage |
| amount | DECIMAL(12,2) | | Base amount |
| discount_amount | DECIMAL(12,2) | | Flat discount |
| admission_type | VARCHAR(50) | | Government / Management |
| is_active | BOOLEAN | DEFAULT TRUE | |

**Seed Data (12 quotas):**
GOVT, MGT, FSTG, SC/ST, MERIT25, MERIT50, MERIT75, MERIT100, FG_MERIT25, FG_MERIT50, FG_MERIT75, FG_MERIT100

#### `admin_bank_master`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| bank_short_name | VARCHAR(50) | NOT NULL | Display name |
| account_number | VARCHAR(30) | NOT NULL | |
| bank_name | VARCHAR(100) | | |
| branch | VARCHAR(100) | | |
| ifsc_code | VARCHAR(20) | | |
| remarks | VARCHAR(500) | | Purpose description |
| is_active | BOOLEAN | DEFAULT TRUE | |

**Seed Data (15 bank accounts):**
Canara (Alumni, Bus, Civil, CSE), NSCET Hostel, ECE Association, Federal (Main, Bus, Online), TMB (Main, Bus, Exam, Scholarship, Bus Fees AVCA), SBI Charges

#### `admin_fees_master`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| name | VARCHAR(100) | NOT NULL | Fee type name |
| fees_group | VARCHAR(50) | NOT NULL | Group category |
| from_date | DATE | | Validity start |
| to_date | DATE | | Validity end |
| is_active | BOOLEAN | DEFAULT TRUE | |

**Fee Groups:** Clg Fees, Exam Fees, Kanna Donor Club, Bus Fee, Hostel Fee, Alumni Activity

**Seed Data (15 fee types):**
Tuition Fee, Admission Fees, Library Fee, Lab Fee, Exam Fee, Bus Fee, Hostel Fee, Placement Fee, Sports Fee, Uniform Fee, Books Fee, Anna University Reg Fee, Professional Society, Student Insurance, Value Added Course

---

### 3. Staff Module

#### `admin_staff_master`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| staff_code | VARCHAR(20) | UNIQUE, NOT NULL | e.g., STF001 |
| name | VARCHAR(150) | NOT NULL | |
| address | VARCHAR(500) | | |
| city | VARCHAR(100) | | |
| pin_code | VARCHAR(10) | | |
| date_of_birth | DATE | | |
| category | VARCHAR(50) | | OC, BC, BCM, MBC, OBC, DNC, SC, ST |
| department_id | BIGINT | FK → admin_department_master | FetchType.EAGER |
| designation_id | BIGINT | FK → admin_designation_master | FetchType.EAGER |
| staff_group | VARCHAR(50) | | Teaching, Office |
| college_code | VARCHAR(20) | | |
| transport | VARCHAR(10) | | Own, College |
| email | VARCHAR(100) | | |
| pf_active | BOOLEAN | DEFAULT FALSE | |
| sex | VARCHAR(10) | | M, F |
| date_of_joining | DATE | | |
| phone | VARCHAR(15) | | |
| blood_group | VARCHAR(10) | | |
| aadhar_number | VARCHAR(20) | | |
| pan_number | VARCHAR(20) | | |
| essl_id | VARCHAR(50) | | |
| is_active | BOOLEAN | DEFAULT TRUE | |
| created_by, updated_by | BIGINT | | Audit |
| created_at, updated_at | TIMESTAMP | | Audit |

---

### 4. Student Module

#### `admin_student_master`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| roll_number | VARCHAR(20) | UNIQUE, NOT NULL | e.g., 23CSE001 |
| registration_no | VARCHAR(20) | | |
| admission_no | VARCHAR(20) | UNIQUE | e.g., ADM23001 |
| name | VARCHAR(150) | NOT NULL | |
| father_name | VARCHAR(150) | | |
| mother_name | VARCHAR(150) | | |
| phone | VARCHAR(15) | | Student phone |
| parent_phone | VARCHAR(15) | | Parent phone |
| gender | VARCHAR(10) | | Male, Female, Other |
| aadhar_number | VARCHAR(20) | | 12 digits |
| date_of_birth | DATE | | |
| community | VARCHAR(50) | | OC, BC, BCM, MBC, OBC, DNC, SC, ST, Others |
| caste | VARCHAR(50) | | |
| region | VARCHAR(100) | | |
| city | VARCHAR(100) | | |
| email | VARCHAR(100) | | |
| address | VARCHAR(500) | | |
| blood_group | VARCHAR(10) | | |
| medium | VARCHAR(20) | | English, Tamil |
| bus_stop | VARCHAR(100) | | |
| hostel | VARCHAR(50) | | |
| transport_type | VARCHAR(50) | | Own, College |
| state | VARCHAR(50) | | |
| admission_type | VARCHAR(50) | | Government, Management |
| date_of_joining | DATE | | |
| section | VARCHAR(5) | | |
| occupation | VARCHAR(100) | | Father's occupation |
| religion | VARCHAR(50) | | Hindu, Muslim, Christian, Others |
| is_active | BOOLEAN | DEFAULT TRUE | |
| created_by, updated_by | BIGINT | | Audit |
| created_at, updated_at | TIMESTAMP | | Audit |

#### `admin_student_details`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| student_id | BIGINT | FK → admin_student_master, NOT NULL | |
| semester | INT | NOT NULL | 1-8 |
| caste_category | VARCHAR(50) | | |
| bus_stop | VARCHAR(100) | | |
| hostel | VARCHAR(50) | | |
| transport_type | VARCHAR(50) | | |
| state | VARCHAR(50) | | |
| admission_year | INT | NOT NULL | |
| sem_type | VARCHAR(20) | | |
| department_id | BIGINT | FK → admin_department_master | |
| quota_id | BIGINT | FK → admin_quota_master | |
| degree | VARCHAR(50) | | B.E., M.E. |
| section | VARCHAR(5) | | |
| academic_year | VARCHAR(20) | | e.g., 2024-25 |
| is_active | BOOLEAN | DEFAULT TRUE | |
| created_by, updated_by | BIGINT | | Audit |
| created_at, updated_at | TIMESTAMP | | Audit |

**Unique Constraint:** `(student_id, semester, admission_year)`

---

### 5. Fees Module

#### `admin_fees_details`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| from_date | DATE | NOT NULL | |
| to_date | DATE | NOT NULL | |
| degree | VARCHAR(50) | | |
| semester | INT | | |
| quota_id | BIGINT | FK → admin_quota_master | |
| department_id | BIGINT | FK → admin_department_master | |
| dept_type | VARCHAR(50) | | |
| admission_type | VARCHAR(50) | | |
| fees_name_id | BIGINT | FK → admin_fees_master | |
| amount | DECIMAL(12,2) | NOT NULL | |
| is_active | BOOLEAN | DEFAULT TRUE | |
| created_by, updated_by | BIGINT | | Audit |
| created_at | TIMESTAMP | | |

---

### 6. Transaction Module

#### `admin_fee_receipts`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| receipt_number | VARCHAR(30) | UNIQUE, NOT NULL | Format: MIS-YY-MM-NNNN |
| student_id | BIGINT | FK → admin_student_master, NOT NULL | |
| student_type | VARCHAR(20) | NOT NULL | Regular, Passed Out, Staff, Misc |
| receipt_date | DATE | NOT NULL | |
| academic_year | VARCHAR(20) | | |
| payment_mode | VARCHAR(30) | | CASH, ONLINE, DD |
| base_account | VARCHAR(50) | | Bank account reference |
| bank_id | BIGINT | FK → admin_bank_master | |
| total_amount | DECIMAL(12,2) | NOT NULL | |
| pay_type | VARCHAR(30) | | Pay, OLP, DD-Cheque, Adjust Bill |
| dd_cheque_no | VARCHAR(30) | | |
| dd_cheque_bank | VARCHAR(100) | | |
| status | VARCHAR(20) | DEFAULT 'ACTIVE' | |
| created_by, updated_by | BIGINT | | Audit |
| created_at, updated_at | TIMESTAMP | | |

#### `admin_fee_receipt_items`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| receipt_id | BIGINT | FK → admin_fee_receipts, NOT NULL | |
| fees_name_id | BIGINT | FK → admin_fees_master | |
| amount | DECIMAL(12,2) | NOT NULL | |
| allocated_to | VARCHAR(30) | | CASH, ONLINE, DD |
| created_at | TIMESTAMP | | |

**Cascade:** `CascadeType.ALL, orphanRemoval=true` from FeeReceipt

#### `admin_transfer_certificates`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| tc_number | VARCHAR(30) | UNIQUE, NOT NULL | |
| serial_no | VARCHAR(30) | | |
| student_id | BIGINT | FK → admin_student_master, NOT NULL | |
| academic_year | VARCHAR(20) | | |
| admission_no | VARCHAR(20) | | |
| course | VARCHAR(50) | | |
| semester | INT | | |
| tc_date | DATE | | |
| date_of_left | DATE | | |
| character_conduct | VARCHAR(100) | | Good, Very Good, Excellent, Satisfactory |
| tc_application_date | DATE | | |
| id_marks | VARCHAR(200) | | |
| course_completion | VARCHAR(100) | | |
| promotion_status | VARCHAR(100) | | |
| fee_status | VARCHAR(100) | | |
| batch | VARCHAR(50) | | |
| umis_no | VARCHAR(50) | | |
| remarks | VARCHAR(500) | | |
| created_by, updated_by | BIGINT | | Audit |
| created_at, updated_at | TIMESTAMP | | |

---

### 7. Reports & Settlements

#### `admin_day_settlements`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| settlement_date | DATE | UNIQUE, NOT NULL | |
| opening_balance | DECIMAL(12,2) | | |
| cash_collection | DECIMAL(12,2) | | |
| bank_collection | DECIMAL(12,2) | | |
| online_collection | DECIMAL(12,2) | | |
| closing_balance | DECIMAL(12,2) | | |
| total_receipts | INT | | |
| status | VARCHAR(20) | DEFAULT 'PENDING' | |
| settled_by | BIGINT | | |
| created_at | TIMESTAMP | | |

---

### 8. Audit Trail

#### `admin_audit_log`
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| user_id | BIGINT | | |
| action | VARCHAR(50) | NOT NULL | CREATE, UPDATE, DELETE, LOGIN_SUCCESS, LOGIN_FAILED |
| table_name | VARCHAR(100) | NOT NULL | |
| record_id | BIGINT | | |
| old_values | TEXT | | JSON snapshot |
| new_values | TEXT | | JSON snapshot |
| timestamp | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |
| ip_address | VARCHAR(50) | | |

---

### 9. Scaffold Tables (Accounts & Payroll)

#### `accounts_ledger`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| ledger_name | VARCHAR(100) | |
| ledger_type | VARCHAR(50) | |
| parent_id | BIGINT | Self-referencing FK |
| is_active | BOOLEAN | |

#### `accounts_vouchers`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| voucher_number | VARCHAR(30) | UNIQUE |
| voucher_date | DATE | |
| voucher_type | VARCHAR(50) | |
| debit_amount | DECIMAL(12,2) | |
| credit_amount | DECIMAL(12,2) | |
| narration | VARCHAR(500) | |

#### `payroll_salary_structure`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| staff_id | BIGINT | FK → admin_staff_master |
| basic_salary | DECIMAL(12,2) | |
| hra, da, ta, pf, esi | DECIMAL(12,2) | Components |

#### `payroll_monthly`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| staff_id | BIGINT | FK → admin_staff_master |
| pay_month, pay_year | INT | |
| gross_salary, deductions, net_salary | DECIMAL(12,2) | |
| status | VARCHAR(20) | DRAFT |

---

## Flyway Migrations

| Version | File | Description |
|---------|------|-------------|
| V1 | `V1__initial_schema.sql` | All 23 tables + seed data (25 depts, 9 designations, 15 banks, 15 fees, 12 quotas, 4 roles, 20 permissions, 1 admin user) |
| V2 | `V2__mock_data.sql` | 55 staff, 37 students, student details, 16 fee structures, 23 receipts with items |
| V3 | `V3__activate_seed_data.sql` | Fix `is_active` for all seed data |
| V4 | `V4__more_staff.sql` | 30 additional staff (STF026–STF055) |
| V5 | `V5__student_master_new_columns.sql` | Add `date_of_joining`, `section`, `occupation`, `religion` to student master |

## Indexes

| Table | Index | Column(s) |
|-------|-------|-----------|
| admin_department_master | idx_dept_code | code |
| admin_department_master | idx_dept_name | name |
| admin_designation_master | idx_desig_code | code |
| admin_designation_master | idx_desig_name | name |
| admin_fees_master | idx_fees_group | fees_group |
| admin_staff_master | idx_staff_code | staff_code |
| admin_staff_master | idx_staff_name | name |
| admin_student_master | idx_student_roll | roll_number |
| admin_student_master | idx_student_name | name |
| admin_student_master | idx_student_admission | admission_no |
| admin_student_details | idx_sd_dept_sem | department_id, semester |
| admin_fees_details | idx_fd_dept_sem | department_id, semester |
| admin_fee_receipts | idx_receipt_number | receipt_number |
| admin_fee_receipts | idx_receipt_date | receipt_date |
| admin_fee_receipts | idx_receipt_student | student_id |
| admin_transfer_certificates | idx_tc_number | tc_number |
| admin_audit_log | idx_audit_user | user_id |
| admin_audit_log | idx_audit_table | table_name |
| admin_audit_log | idx_audit_timestamp | timestamp |
