-- Migration V13: Payroll Portal Tables & Seed Data

-- 1. Leave Master Table
CREATE TABLE IF NOT EXISTS payroll_leave_master (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    leave_code VARCHAR(20) NOT NULL UNIQUE,
    leave_name VARCHAR(100) NOT NULL,
    short_name VARCHAR(20) NOT NULL,
    max_allowed INT DEFAULT 12,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Staff Salary Master Table
CREATE TABLE IF NOT EXISTS payroll_staff_salary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_code VARCHAR(30) NOT NULL UNIQUE,
    staff_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    designation VARCHAR(100),
    category VARCHAR(50) DEFAULT 'Teaching',
    bank_name VARCHAR(100),
    bank_acc_no VARCHAR(50),
    basic_pay DECIMAL(12,2) DEFAULT 0.00,
    special_allowance DECIMAL(12,2) DEFAULT 0.00,
    hra DECIMAL(12,2) DEFAULT 0.00,
    ta_type VARCHAR(20) DEFAULT 'MONTHLY',
    ta_amount DECIMAL(12,2) DEFAULT 0.00,
    washing_allowance DECIMAL(12,2) DEFAULT 0.00,
    conveyance DECIMAL(12,2) DEFAULT 0.00,
    gross_salary DECIMAL(12,2) DEFAULT 0.00,
    epf_deduction DECIMAL(12,2) DEFAULT 0.00,
    esi_deduction DECIMAL(12,2) DEFAULT 0.00,
    income_tax DECIMAL(12,2) DEFAULT 0.00,
    professional_tax DECIMAL(12,2) DEFAULT 0.00,
    staff_club DECIMAL(12,2) DEFAULT 0.00,
    other_deductions DECIMAL(12,2) DEFAULT 0.00,
    net_salary DECIMAL(12,2) DEFAULT 0.00,
    cl_balance INT DEFAULT 12,
    el_balance INT DEFAULT 10,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Attendance Log Table
CREATE TABLE IF NOT EXISTS payroll_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attendance_date DATE NOT NULL,
    staff_code VARCHAR(30) NOT NULL,
    staff_name VARCHAR(100),
    department VARCHAR(100),
    session_type VARCHAR(20) DEFAULT 'FULL_DAY', -- FORENOON, AFTERNOON, FULL_DAY
    attendance_type VARCHAR(20) DEFAULT 'PRESENT', -- PRESENT, LOP, CL, OD, Late
    remarks VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Salary Increment Log Table
CREATE TABLE IF NOT EXISTS payroll_increments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_code VARCHAR(30) NOT NULL,
    staff_name VARCHAR(100),
    department VARCHAR(100),
    effective_date DATE NOT NULL,
    old_basic DECIMAL(12,2) DEFAULT 0.00,
    new_basic DECIMAL(12,2) DEFAULT 0.00,
    old_special_allowance DECIMAL(12,2) DEFAULT 0.00,
    new_special_allowance DECIMAL(12,2) DEFAULT 0.00,
    increment_amount DECIMAL(12,2) DEFAULT 0.00,
    new_gross DECIMAL(12,2) DEFAULT 0.00,
    remarks VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. Monthly Payroll Calculation Run Table
CREATE TABLE IF NOT EXISTS payroll_monthly_run (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pay_period VARCHAR(20) NOT NULL, -- e.g. Jul-2026
    staff_code VARCHAR(30) NOT NULL,
    staff_name VARCHAR(100),
    department VARCHAR(100),
    working_days INT DEFAULT 30,
    paid_days INT DEFAULT 30,
    lop_days INT DEFAULT 0,
    basic_pay DECIMAL(12,2) DEFAULT 0.00,
    special_allowance DECIMAL(12,2) DEFAULT 0.00,
    hra DECIMAL(12,2) DEFAULT 0.00,
    conveyance DECIMAL(12,2) DEFAULT 0.00,
    washing_allowance DECIMAL(12,2) DEFAULT 0.00,
    gross_pay DECIMAL(12,2) DEFAULT 0.00,
    lop_deduction DECIMAL(12,2) DEFAULT 0.00,
    epf_deduction DECIMAL(12,2) DEFAULT 0.00,
    esi_deduction DECIMAL(12,2) DEFAULT 0.00,
    income_tax DECIMAL(12,2) DEFAULT 0.00,
    professional_tax DECIMAL(12,2) DEFAULT 0.00,
    staff_club DECIMAL(12,2) DEFAULT 0.00,
    total_deductions DECIMAL(12,2) DEFAULT 0.00,
    net_pay DECIMAL(12,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed Leave Master
INSERT INTO payroll_leave_master (leave_code, leave_name, short_name, max_allowed)
VALUES
('CL', 'Casual Leave', 'CL', 12),
('LOP', 'Loss of Pay', 'LOP', 0),
('OD', 'On Duty', 'OD', 15),
('ML', 'Maternity Leave', 'ML', 90),
('CPL', 'Compensatory Leave', 'CPL', 10);

-- Seed Staff Salary Master
INSERT INTO payroll_staff_salary (staff_code, staff_name, department, designation, category, bank_name, bank_acc_no, basic_pay, special_allowance, hra, ta_type, ta_amount, washing_allowance, conveyance, gross_salary, epf_deduction, esi_deduction, income_tax, professional_tax, staff_club, other_deductions, net_salary, cl_balance, el_balance)
VALUES
('NSIOT21', 'VIGNESH L S', 'COMPUTER SCIENCE', 'Assistant Professor', 'Teaching', 'Federal Bank', '14820100045028', 15000.00, 21000.00, 12000.00, 'MONTHLY', 6000.00, 6000.00, 0.00, 60000.00, 1800.00, 0.00, 0.00, 200.00, 0.00, 0.00, 58000.00, 10, 8),
('NS101', 'DR. S. RAMESH', 'MECHANICAL', 'HOD - MECH', 'Teaching', 'Federal Bank', '14820100045755', 23000.00, 58784.00, 13794.00, 'MONTHLY', 8000.00, 8000.00, 2641.00, 108219.00, 1800.00, 0.00, 1500.00, 200.00, 200.00, 0.00, 104519.00, 12, 10),
('NS102', 'PROF. K. VENKATESH', 'ELECTRONICS', 'HOD - ECE', 'Teaching', 'Federal Bank', '14820100038271', 15000.00, 21000.00, 5380.00, 'MONTHLY', 5000.00, 5000.00, 2587.00, 53967.00, 1800.00, 0.00, 0.00, 200.00, 200.00, 0.00, 51767.00, 11, 9),
('NS103', 'V. MATHAVAN', 'COMPUTER SCIENCE', 'Assistant Professor', 'Teaching', 'Federal Bank', '14820100045227', 15000.00, 21000.00, 8000.00, 'MONTHLY', 5000.00, 5000.00, 2589.00, 56589.00, 1800.00, 0.00, 0.00, 200.00, 200.00, 0.00, 54389.00, 12, 10),
('NS104', 'S. GAYATHRI', 'CIVIL', 'Assistant Professor', 'Teaching', 'Federal Bank', '14820100039014', 15000.00, 30460.00, 7577.00, 'MONTHLY', 5000.00, 5000.00, 3044.00, 66081.00, 1800.00, 0.00, 0.00, 200.00, 200.00, 0.00, 63881.00, 10, 8),
('NS105', 'M. SUNDAR', 'ADMIN', 'Lab Assistant', 'Non-Teaching', 'Federal Bank', '14820100043412', 12000.00, 8000.00, 3500.00, 'MONTHLY', 2000.00, 2000.00, 1500.00, 29000.00, 1440.00, 0.00, 0.00, 150.00, 100.00, 0.00, 27310.00, 11, 9);

-- Seed Attendance Records
INSERT INTO payroll_attendance (attendance_date, staff_code, staff_name, department, session_type, attendance_type, remarks)
VALUES
(CURRENT_DATE, 'NSIOT21', 'VIGNESH L S', 'COMPUTER SCIENCE', 'FULL_DAY', 'PRESENT', 'On time'),
(CURRENT_DATE, 'NS101', 'DR. S. RAMESH', 'MECHANICAL', 'FULL_DAY', 'PRESENT', 'On time'),
(CURRENT_DATE, 'NS102', 'PROF. K. VENKATESH', 'ELECTRONICS', 'FULL_DAY', 'PRESENT', 'On time'),
(CURRENT_DATE, 'NS103', 'V. MATHAVAN', 'COMPUTER SCIENCE', 'FULL_DAY', 'CL', 'Approved Casual Leave'),
(CURRENT_DATE, 'NS104', 'S. GAYATHRI', 'CIVIL', 'FULL_DAY', 'PRESENT', 'On time'),
(CURRENT_DATE, 'NS105', 'M. SUNDAR', 'ADMIN', 'FULL_DAY', 'LOP', 'Uninformed absence');

-- Seed Salary Increments
INSERT INTO payroll_increments (staff_code, staff_name, department, effective_date, old_basic, new_basic, old_special_allowance, new_special_allowance, increment_amount, new_gross, remarks)
VALUES
('NSIOT21', 'VIGNESH L S', 'COMPUTER SCIENCE', CURRENT_DATE, 12000.00, 15000.00, 18000.00, 21000.00, 6000.00, 60000.00, 'Annual Performance Appraisal 2026'),
('NS101', 'DR. S. RAMESH', 'MECHANICAL', DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), 20000.00, 23000.00, 50000.00, 58784.00, 11784.00, 108219.00, 'Promotional Grade Revision');

-- Seed Monthly Payroll Calculation Run
INSERT INTO payroll_monthly_run (pay_period, staff_code, staff_name, department, working_days, paid_days, lop_days, basic_pay, special_allowance, hra, conveyance, washing_allowance, gross_pay, lop_deduction, epf_deduction, esi_deduction, income_tax, professional_tax, staff_club, total_deductions, net_pay)
VALUES
('Jul-2026', 'NSIOT21', 'VIGNESH L S', 'COMPUTER SCIENCE', 30, 30, 0, 15000.00, 21000.00, 12000.00, 6000.00, 6000.00, 60000.00, 0.00, 1800.00, 0.00, 0.00, 200.00, 0.00, 2000.00, 58000.00),
('Jul-2026', 'NS101', 'DR. S. RAMESH', 'MECHANICAL', 30, 30, 0, 23000.00, 58784.00, 13794.00, 8000.00, 8000.00, 108219.00, 0.00, 1800.00, 0.00, 1500.00, 200.00, 200.00, 3700.00, 104519.00),
('Jul-2026', 'NS102', 'PROF. K. VENKATESH', 'ELECTRONICS', 30, 30, 0, 15000.00, 21000.00, 5380.00, 5000.00, 5000.00, 53967.00, 0.00, 1800.00, 0.00, 0.00, 200.00, 200.00, 2200.00, 51767.00);
