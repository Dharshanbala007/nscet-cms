-- V1: Initial schema for NSCET CMS
-- All tables prefixed with admin_ per Section 9 guidelines

-- ============================
-- USERS & AUTHENTICATION
-- ============================
CREATE TABLE admin_users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100),
    email           VARCHAR(100),
    staff_id        BIGINT,
    is_active       BOOLEAN DEFAULT TRUE,
    is_locked       BOOLEAN DEFAULT FALSE,
    failed_attempts INT DEFAULT 0,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE admin_roles (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50) UNIQUE NOT NULL,
    description     VARCHAR(200)
);

CREATE TABLE admin_permissions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) UNIQUE NOT NULL,
    module          VARCHAR(50) NOT NULL,
    action          VARCHAR(50) NOT NULL
);

CREATE TABLE admin_user_roles (
    user_id         BIGINT NOT NULL,
    role_id         BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES admin_users(id),
    FOREIGN KEY (role_id) REFERENCES admin_roles(id)
);

CREATE TABLE admin_role_permissions (
    role_id         BIGINT NOT NULL,
    permission_id   BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES admin_roles(id),
    FOREIGN KEY (permission_id) REFERENCES admin_permissions(id)
);

-- ============================
-- MASTER DATA
-- ============================
CREATE TABLE admin_designation_master (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(10) UNIQUE NOT NULL,
    short_name      VARCHAR(20) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    category        VARCHAR(50),
    color_code      VARCHAR(20),
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_desig_code (code),
    INDEX idx_desig_name (name)
);

CREATE TABLE admin_fees_master (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    fees_group      VARCHAR(50) NOT NULL,
    from_date       DATE,
    to_date         DATE,
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_fees_group (fees_group)
);

CREATE TABLE admin_bank_master (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    bank_short_name VARCHAR(50) NOT NULL,
    account_number  VARCHAR(30) NOT NULL,
    bank_name       VARCHAR(100),
    branch          VARCHAR(100),
    ifsc_code       VARCHAR(20),
    remarks         VARCHAR(500),
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE admin_department_master (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(10) UNIQUE NOT NULL,
    short_name      VARCHAR(20) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(50),
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_dept_code (code),
    INDEX idx_dept_name (name)
);

CREATE TABLE admin_quota_master (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(15) UNIQUE NOT NULL,
    name            VARCHAR(100) NOT NULL,
    percentage      DECIMAL(5,2),
    amount          DECIMAL(12,2),
    discount_amount DECIMAL(12,2),
    admission_type  VARCHAR(50),
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================
-- STAFF MODULE
-- ============================
CREATE TABLE admin_staff_master (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_code      VARCHAR(20) UNIQUE NOT NULL,
    name            VARCHAR(150) NOT NULL,
    address         VARCHAR(500),
    city            VARCHAR(100),
    pin_code        VARCHAR(10),
    date_of_birth   DATE,
    category        VARCHAR(50),
    department_id   BIGINT,
    designation_id  BIGINT,
    staff_group     VARCHAR(50),
    college_code    VARCHAR(20),
    transport       VARCHAR(10),
    email           VARCHAR(100),
    pf_active       BOOLEAN DEFAULT FALSE,
    sex             VARCHAR(10),
    date_of_joining DATE,
    phone           VARCHAR(15),
    blood_group     VARCHAR(10),
    aadhar_number   VARCHAR(20),
    pan_number      VARCHAR(20),
    essl_id         VARCHAR(50),
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES admin_department_master(id),
    FOREIGN KEY (designation_id) REFERENCES admin_designation_master(id),
    INDEX idx_staff_code (staff_code),
    INDEX idx_staff_name (name)
);

-- ============================
-- STUDENT MODULE
-- ============================
CREATE TABLE admin_student_master (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    roll_number     VARCHAR(20) UNIQUE NOT NULL,
    registration_no VARCHAR(20),
    admission_no    VARCHAR(20) UNIQUE,
    name            VARCHAR(150) NOT NULL,
    father_name     VARCHAR(150),
    mother_name     VARCHAR(150),
    phone           VARCHAR(15),
    parent_phone    VARCHAR(15),
    gender          VARCHAR(10),
    aadhar_number   VARCHAR(20),
    date_of_birth   DATE,
    community       VARCHAR(50),
    caste           VARCHAR(50),
    region          VARCHAR(100),
    city            VARCHAR(100),
    email           VARCHAR(100),
    address         VARCHAR(500),
    blood_group     VARCHAR(10),
    medium          VARCHAR(20),
    bus_stop        VARCHAR(100),
    hostel          VARCHAR(50),
    transport_type  VARCHAR(50),
    state           VARCHAR(50),
    admission_type  VARCHAR(50),
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_roll (roll_number),
    INDEX idx_student_name (name),
    INDEX idx_student_admission (admission_no)
);

CREATE TABLE admin_student_details (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT NOT NULL,
    semester        INT NOT NULL,
    caste_category  VARCHAR(50),
    bus_stop        VARCHAR(100),
    hostel          VARCHAR(50),
    transport_type  VARCHAR(50),
    state           VARCHAR(50),
    admission_year  INT NOT NULL,
    sem_type        VARCHAR(20),
    department_id   BIGINT,
    quota_id        BIGINT,
    degree          VARCHAR(50),
    section         VARCHAR(5),
    academic_year   VARCHAR(20),
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES admin_student_master(id),
    FOREIGN KEY (department_id) REFERENCES admin_department_master(id),
    FOREIGN KEY (quota_id) REFERENCES admin_quota_master(id),
    UNIQUE KEY uk_student_sem_year (student_id, semester, admission_year),
    INDEX idx_sd_dept_sem (department_id, semester)
);

-- ============================
-- FEES STRUCTURE
-- ============================
CREATE TABLE admin_fees_details (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_date       DATE NOT NULL,
    to_date         DATE NOT NULL,
    degree          VARCHAR(50),
    semester        INT,
    quota_id        BIGINT,
    department_id   BIGINT,
    dept_type       VARCHAR(50),
    admission_type  VARCHAR(50),
    fees_name_id    BIGINT,
    amount          DECIMAL(12,2) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quota_id) REFERENCES admin_quota_master(id),
    FOREIGN KEY (department_id) REFERENCES admin_department_master(id),
    FOREIGN KEY (fees_name_id) REFERENCES admin_fees_master(id),
    INDEX idx_fd_dept_sem (department_id, semester)
);

-- ============================
-- TRANSACTION MODULES
-- ============================
CREATE TABLE admin_fee_receipts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_number  VARCHAR(30) UNIQUE NOT NULL,
    student_id      BIGINT NOT NULL,
    student_type    VARCHAR(20) NOT NULL,
    receipt_date    DATE NOT NULL,
    academic_year   VARCHAR(20),
    payment_mode    VARCHAR(30),
    base_account    VARCHAR(50),
    bank_id         BIGINT,
    total_amount    DECIMAL(12,2) NOT NULL,
    pay_type        VARCHAR(30),
    dd_cheque_no    VARCHAR(30),
    dd_cheque_bank  VARCHAR(100),
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES admin_student_master(id),
    FOREIGN KEY (bank_id) REFERENCES admin_bank_master(id),
    INDEX idx_receipt_number (receipt_number),
    INDEX idx_receipt_date (receipt_date),
    INDEX idx_receipt_student (student_id)
);

CREATE TABLE admin_fee_receipt_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_id      BIGINT NOT NULL,
    fees_name_id    BIGINT,
    amount          DECIMAL(12,2) NOT NULL,
    allocated_to    VARCHAR(30),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receipt_id) REFERENCES admin_fee_receipts(id),
    FOREIGN KEY (fees_name_id) REFERENCES admin_fees_master(id)
);

CREATE TABLE admin_transfer_certificates (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tc_number       VARCHAR(30) UNIQUE NOT NULL,
    serial_no       VARCHAR(30),
    student_id      BIGINT NOT NULL,
    academic_year   VARCHAR(20),
    admission_no    VARCHAR(20),
    course          VARCHAR(50),
    semester        INT,
    tc_date         DATE,
    date_of_left    DATE,
    character_conduct VARCHAR(100),
    tc_application_date DATE,
    id_marks        VARCHAR(200),
    course_completion VARCHAR(100),
    promotion_status VARCHAR(100),
    fee_status      VARCHAR(100),
    batch           VARCHAR(50),
    umis_no         VARCHAR(50),
    remarks         VARCHAR(500),
    created_by      BIGINT,
    updated_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES admin_student_master(id),
    INDEX idx_tc_number (tc_number)
);

-- ============================
-- REPORTS & SETTLEMENTS
-- ============================
CREATE TABLE admin_day_settlements (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    settlement_date DATE NOT NULL UNIQUE,
    opening_balance DECIMAL(12,2),
    cash_collection DECIMAL(12,2),
    bank_collection DECIMAL(12,2),
    online_collection DECIMAL(12,2),
    closing_balance DECIMAL(12,2),
    total_receipts  INT,
    status          VARCHAR(20) DEFAULT 'PENDING',
    settled_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- AUDIT LOG
-- ============================
CREATE TABLE admin_audit_log (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT,
    action          VARCHAR(50) NOT NULL,
    table_name      VARCHAR(100) NOT NULL,
    record_id       BIGINT,
    old_values      TEXT,
    new_values      TEXT,
    timestamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address      VARCHAR(50),
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_table (table_name),
    INDEX idx_audit_timestamp (timestamp)
);

-- ============================
-- ACCOUNTS MODULE (SCAFFOLD)
-- ============================
CREATE TABLE accounts_ledger (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ledger_name     VARCHAR(100) NOT NULL,
    ledger_type     VARCHAR(50),
    parent_id       BIGINT,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE accounts_vouchers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_number  VARCHAR(30) UNIQUE NOT NULL,
    voucher_date    DATE NOT NULL,
    voucher_type    VARCHAR(50),
    debit_amount    DECIMAL(12,2),
    credit_amount   DECIMAL(12,2),
    narration       VARCHAR(500),
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- PAYROLL MODULE (SCAFFOLD)
-- ============================
CREATE TABLE payroll_salary_structure (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id        BIGINT,
    basic_salary    DECIMAL(12,2),
    hra             DECIMAL(12,2),
    da              DECIMAL(12,2),
    ta              DECIMAL(12,2),
    pf              DECIMAL(12,2),
    esi             DECIMAL(12,2),
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES admin_staff_master(id)
);

CREATE TABLE payroll_monthly (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id        BIGINT,
    pay_month       INT,
    pay_year        INT,
    gross_salary    DECIMAL(12,2),
    deductions      DECIMAL(12,2),
    net_salary      DECIMAL(12,2),
    status          VARCHAR(20) DEFAULT 'DRAFT',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES admin_staff_master(id)
);

-- ============================
-- SEED DATA
-- ============================
-- Default roles
INSERT INTO admin_roles (name, description) VALUES ('ADMIN', 'System Administrator');
INSERT INTO admin_roles (name, description) VALUES ('ACCOUNTS', 'Accounts Operator');
INSERT INTO admin_roles (name, description) VALUES ('PAYROLL', 'Payroll Operator');
INSERT INTO admin_roles (name, description) VALUES ('VIEWER', 'Read Only Access');

-- Default admin user (password: admin123 - BCrypt hashed)
INSERT INTO admin_users (username, password_hash, full_name, email) VALUES
('admin', '$2a$12$ggj7sSTUbozOIHeYCL.Cz.ZEAIM6j3ZfuDDwA4hShxAUjFeKt1h7m', 'System Administrator', 'admin@nscet.edu');

INSERT INTO admin_user_roles (user_id, role_id) VALUES (1, 1);

-- Permissions
INSERT INTO admin_permissions (name, module, action) VALUES ('DESIGNATION_VIEW', 'DESIGNATION', 'VIEW');
INSERT INTO admin_permissions (name, module, action) VALUES ('DESIGNATION_CREATE', 'DESIGNATION', 'CREATE');
INSERT INTO admin_permissions (name, module, action) VALUES ('DESIGNATION_UPDATE', 'DESIGNATION', 'UPDATE');
INSERT INTO admin_permissions (name, module, action) VALUES ('DESIGNATION_DELETE', 'DESIGNATION', 'DELETE');
INSERT INTO admin_permissions (name, module, action) VALUES ('FEES_VIEW', 'FEES', 'VIEW');
INSERT INTO admin_permissions (name, module, action) VALUES ('FEES_CREATE', 'FEES', 'CREATE');
INSERT INTO admin_permissions (name, module, action) VALUES ('FEES_UPDATE', 'FEES', 'UPDATE');
INSERT INTO admin_permissions (name, module, action) VALUES ('FEES_DELETE', 'FEES', 'DELETE');
INSERT INTO admin_permissions (name, module, action) VALUES ('STUDENT_VIEW', 'STUDENT', 'VIEW');
INSERT INTO admin_permissions (name, module, action) VALUES ('STUDENT_CREATE', 'STUDENT', 'CREATE');
INSERT INTO admin_permissions (name, module, action) VALUES ('STUDENT_UPDATE', 'STUDENT', 'UPDATE');
INSERT INTO admin_permissions (name, module, action) VALUES ('STUDENT_DELETE', 'STUDENT', 'DELETE');
INSERT INTO admin_permissions (name, module, action) VALUES ('STAFF_VIEW', 'STAFF', 'VIEW');
INSERT INTO admin_permissions (name, module, action) VALUES ('STAFF_CREATE', 'STAFF', 'CREATE');
INSERT INTO admin_permissions (name, module, action) VALUES ('STAFF_UPDATE', 'STAFF', 'UPDATE');
INSERT INTO admin_permissions (name, module, action) VALUES ('STAFF_DELETE', 'STAFF', 'DELETE');
INSERT INTO admin_permissions (name, module, action) VALUES ('FEE_COLLECTION', 'TRANSACTION', 'COLLECT');
INSERT INTO admin_permissions (name, module, action) VALUES ('TC_GENERATE', 'TRANSACTION', 'TC');
INSERT INTO admin_permissions (name, module, action) VALUES ('REPORT_VIEW', 'REPORT', 'VIEW');
INSERT INTO admin_permissions (name, module, action) VALUES ('SETTLEMENT', 'TOOLS', 'SETTLE');

-- Assign all permissions to ADMIN
INSERT INTO admin_role_permissions (role_id, permission_id) SELECT 1, id FROM admin_permissions;

-- Seed designations
INSERT INTO admin_designation_master (code, short_name, name, category, color_code, is_active) VALUES
('01', 'AP', 'AP', 'Teaching', '#1E88E5', 1),
('02', 'HOD', 'HOD', 'Teaching', '#1E88E5', 1),
('03', 'APROF', 'Associate Professor', 'Teaching', '#1E88E5', 1),
('04', 'LIB', 'Library', 'Teaching', '#43A047', 1),
('05', 'PD', 'PD', 'NT-Tech', '#FB8C00', 1),
('06', 'PCOORD', 'P.Co-ordinator', 'Teaching', '#1E88E5', 1),
('07', 'TF', 'Teaching Fellow', 'Teaching', '#1E88E5', 1),
('08', 'DINC', 'Dept Incharge', 'Teaching', '#1E88E5', 1),
('09', 'EOC', 'EOC', 'Contract', '#8E24AA', 1);

-- Seed departments
INSERT INTO admin_department_master (code, short_name, name, type, is_active) VALUES
('MECH', 'MECH', 'MECH', 'Academic', 1),
('CE', 'CE', 'CE', 'Academic', 1),
('ECE', 'ECE', 'ECE', 'Academic', 1),
('CSE', 'CSE', 'CSE', 'Academic', 1),
('ACCT', 'ACCT', 'ACCOUNTS', 'Official', 1),
('ADMIN', 'ADMIN', 'ADMIN', 'Official', 1),
('SYS', 'SYS', 'SYSADMIN', 'Official', 1),
('TRANS', 'TRANS', 'TRANSPORT', 'Official', 1),
('LIB', 'LIB', 'LIB', 'Official', 1),
('CAN', 'CAN', 'CANTEEN', 'Official', 1),
('PHY', 'PHY', 'PHYSICS', 'Academic', 1),
('CHEM', 'CHEM', 'CHEMISTRY', 'Academic', 1),
('MATH', 'MATH', 'MATHS', 'Academic', 1),
('PD', 'PD', 'PD', 'Academic', 1),
('ENG', 'ENG', 'ENGLISH', 'Academic', 1),
('TPO', 'TPO', 'T.P.O', 'Academic', 1),
('SH', 'SH', 'S&H', 'Academic', 1),
('EEE', 'EEE', 'EEE', 'Academic', 1),
('ME', 'ME', 'ME', 'Academic', 1),
('IT', 'IT', 'IT', 'Academic', 1),
('AI', 'AI', 'AI', 'Academic', 1),
('SE', 'SE', 'SE', 'Academic', 1),
('TAM', 'TAM', 'TAMIL', 'Academic', 1),
('EST', 'EST', 'EST', 'Academic', 1),
('MECS', 'MECS', 'ME.CS', 'Academic', 1);

-- Seed bank accounts
INSERT INTO admin_bank_master (bank_short_name, account_number, bank_name, branch, remarks, is_active) VALUES
('Canara Alumni', '102010102569', 'Canara Bank', 'Theni', 'Alumni Association', 1),
('Canara Bus', '102010104466', 'Canara Bank', 'Theni', 'Bus Association Fees', 1),
('Canara Civil', '163013048', 'Canara Bank', 'Theni', 'Civil Association', 1),
('Canara CSE', '1630130-C51', 'Canara Bank', 'Theni', 'CSE Association', 1),
('NSCET Hostel', '1630130134', 'Canara Bank', 'Theni', 'Hostel Club Donars', 1),
('ECE Association', '1630130-ECI', 'Canara Bank', 'Theni', 'ECE Association', 1),
('Federal Bank', '1452010002682', 'Federal Bank', 'Theni', 'Main Account', 1),
('Federal Bus', '1462010004700', 'Federal Bank', 'Theni', 'Bus Account', 1),
('Federal Online', '1462010007303', 'Federal Bank', 'Theni', 'Online A/C', 1),
('TMB Main', '0071001404500', 'Tamilnadu Mercantile Bank', 'Theni', 'Main Account', 1),
('TMB Bus', '0071000503057', 'Tamilnadu Mercantile Bank', 'Theni', 'Bus Fees', 1),
('TMB Exam', '0071000503077', 'Tamilnadu Mercantile Bank', 'Theni', 'Exam Fees', 1),
('TMB Scholarship', '0071000503063', 'Tamilnadu Mercantile Bank', 'Theni', 'Scholarship', 1),
('TMB Bus Fees', '0071000500508', 'Tamilnadu Mercantile Bank', 'Theni', 'Bus Fees AVCA', 1),
('SBI Charges', '3238070808', 'State Bank of India', 'Theni', 'SBI Charges', 1);

-- Seed fee groups
INSERT INTO admin_fees_master (name, fees_group, is_active) VALUES
('Tuition Fee', 'Clg Fees', 1),
('Admission Fees', 'Clg Fees', 1),
('Library Fee', 'Clg Fees', 1),
('Lab Fee', 'Clg Fees', 1),
('Exam Fee', 'Exam Fees', 1),
('Bus Fee', 'Bus Fee', 1),
('Hostel Fee', 'Hostel Fee', 1),
('Placement Fee', 'Clg Fees', 1),
('Sports Fee', 'Clg Fees', 1),
('Uniform Fee', 'Clg Fees', 1),
('Books Fee', 'Clg Fees', 1),
('Anna University Reg Fee', 'Exam Fees', 1),
('Professional Society', 'Kanna Donor Club', 1),
('Student Insurance', 'Alumni Activity', 1),
('Value Added Course', 'Alumni Activity', 1);

-- Seed quotas
INSERT INTO admin_quota_master (code, name, percentage, amount, discount_amount, admission_type, is_active) VALUES
('GOVT', 'Government', NULL, 40000, NULL, 'Government', 1),
('MGT', 'Management', NULL, NULL, NULL, 'Management', 1),
('FSTG', 'First Graduate', 50, NULL, 20000, 'Government', 1),
('SC/ST', 'SC/ST', NULL, 40000, NULL, 'Government', 1),
('MERIT25', 'Merit 25%', 25, NULL, 10000, 'Government', 1),
('MERIT50', 'Merit 50%', 50, NULL, 20000, 'Government', 1),
('MERIT75', 'Merit 75%', 75, NULL, 30000, 'Government', 1),
('MERIT100', 'Merit 100%', 100, NULL, 40000, 'Government', 1),
('FG_MERIT25', 'FSTG+Merit 25', 25, NULL, 20000, 'Government', 1),
('FG_MERIT50', 'FSTG+Merit 50', 50, NULL, 30000, 'Government', 1),
('FG_MERIT75', 'FSTG+Merit 75', 75, NULL, 30000, 'Government', 1),
('FG_MERIT100', 'FSTG+Merit 100', 100, NULL, 40000, 'Government', 1);
