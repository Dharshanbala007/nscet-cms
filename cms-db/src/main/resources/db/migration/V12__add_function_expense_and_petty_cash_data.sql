-- Migration V12: Function Expense Table & Petty Cash Mock Data

CREATE TABLE IF NOT EXISTS admin_function_expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    function_name VARCHAR(150) NOT NULL,
    department VARCHAR(100),
    expense_date DATE NOT NULL,
    allocated_budget DECIMAL(12,2) DEFAULT 0.00,
    total_expense DECIMAL(12,2) DEFAULT 0.00,
    balance_amount DECIMAL(12,2) DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'Completed',
    remarks VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed mock Function Expenses
INSERT INTO admin_function_expenses (function_name, department, expense_date, allocated_budget, total_expense, balance_amount, status, remarks)
VALUES 
('Annual Sports Day 2026', 'PHYSICAL EDUCATION', CURRENT_DATE, 75000.00, 62450.00, 12550.00, 'Completed', 'Ground arrangements, medals, and refreshments'),
('National Level Tech Symposium 2026', 'COMPUTER SCIENCE', CURRENT_DATE, 120000.00, 115000.00, 5000.00, 'Completed', 'Chief guest honorarium, banners, and student kits'),
('Graduation Day Ceremony 2025', 'GENERAL', DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), 250000.00, 238000.00, 12000.00, 'Completed', 'Stage setup, robes rental, and chief guest hospitality'),
('Inter-College Cultural Fest 2026', 'ELECTRONICS', DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), 150000.00, 142300.00, 7700.00, 'Completed', 'Sound system, judges remuneration, and prize trophies'),
('Alumni Meet 2026', 'GENERAL', DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), 80000.00, 71200.00, 8800.00, 'Completed', 'Dinner buffet, mementos, and registration kits');

-- Seed mock Petty Cash records for Day Book
INSERT INTO accounts_petty_cash (voucher_no, voucher_date, staff_name, department, amount, purpose, transaction_type, remarks, is_active)
VALUES
('PC-2026-001', CURRENT_DATE, 'Dr. S. Ramesh', 'CSE', 1500.00, 'Printer Cartridge Refill', 'DEBIT', 'Approved by Principal', TRUE),
('PC-2026-002', CURRENT_DATE, 'Prof. K. Venkatesh', 'ECE', 850.00, 'Guest Tea & Refreshments', 'DEBIT', 'Department Meeting', TRUE),
('PC-2026-003', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 'M. Sundar', 'MAINTENANCE', 2200.00, 'Electrical Fittings Repair', 'DEBIT', 'Lab Maintenance', TRUE);

-- Seed mock Petty Cash Suspense records
INSERT INTO accounts_petty_cash_suspense (voucher_no, voucher_date, college_or_hostel, staff_name, department, designation, amount, amount_in_words, purpose, is_active)
VALUES
('PCS-2026-001', CURRENT_DATE, 'COLLEGE', 'R. Anand (Lab Tech)', 'CSE', 'Lab Assistant', 5000.00, 'Five Thousand Only', 'Advance for Component Purchase', TRUE),
('PCS-2026-002', DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 'COLLEGE', 'P. Priya', 'ECE', 'Assistant Professor', 3000.00, 'Three Thousand Only', 'Advance for Project Materials', TRUE);

-- Seed mock Petty Vouchers
INSERT INTO accounts_petty_voucher (voucher_no, voucher_date, staff_name, staff_code, designation, department, suspense_voucher_no, suspense_date, suspense_amount, total_amount, purpose, is_active)
VALUES
('PV-2026-001', CURRENT_DATE, 'G. Sathish', 'EMP102', 'Assistant Professor', 'MECH', 'PCS-2026-001', CURRENT_DATE, 5000.00, 1800.00, 'Workshop Welding Rods', TRUE),
('PV-2026-002', CURRENT_DATE, 'V. Malathi', 'EMP105', 'Lab Technician', 'CIVIL', NULL, NULL, 0.00, 950.00, 'Surveying Tape Replacement', TRUE);
