-- Migration V16: Create payroll_late_permission table and seed mock records

CREATE TABLE IF NOT EXISTS payroll_late_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_date DATE NOT NULL,
    staff_code VARCHAR(30) NOT NULL,
    staff_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    permission_type VARCHAR(50) NOT NULL, -- LATE_ENTRY, EARLY_EXIT, PERMISSION_1HR, PERMISSION_2HR
    duration_mins VARCHAR(30) DEFAULT '60 Mins',
    reason VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed initial records matching screenshot
INSERT INTO payroll_late_permission (permission_date, staff_code, staff_name, department, permission_type, duration_mins, reason, is_active)
VALUES
('2026-09-07', 'NS101', 'DR. S. RAMESH', 'MECHANICAL', 'PERMISSION_1HR', '60 Mins', 'Official Permission', TRUE),
('2026-09-07', 'NS102', 'PROF. K. VENKATESH', 'ECE', 'PERMISSION_1HR', '60 Mins', 'Official Permission', TRUE),
('2026-09-07', 'NS103', 'V. MATHAVAN', 'CIVIL', 'PERMISSION_1HR', '60 Mins', 'Official Permission', TRUE),
('2026-09-07', 'NS104', 'S. GAYATHRI', 'CSE', 'PERMISSION_1HR', '60 Mins', 'Official Permission', TRUE),
('2026-09-07', 'NS105', 'M. SUNDARS', 'EEE', 'PERMISSION_1HR', '60 Mins', 'Official Permission', TRUE),
('2026-09-07', 'NSIOT21', 'VIGNESH L S', 'COMPUTER SCIENCE', 'PERMISSION_1HR', '60 Mins', 'Official Permission', TRUE);
