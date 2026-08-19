SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE accounts_petty_cash (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_no VARCHAR(30) NOT NULL,
    voucher_date DATE NOT NULL,
    staff_name VARCHAR(100),
    department VARCHAR(100),
    amount DECIMAL(12,2),
    purpose VARCHAR(500),
    transaction_type VARCHAR(20),
    remarks VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_petty_cash_vno (voucher_no)
);

CREATE TABLE accounts_petty_cash_suspense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_no VARCHAR(30) NOT NULL,
    voucher_date DATE NOT NULL,
    college_or_hostel VARCHAR(20),
    staff_name VARCHAR(100),
    department VARCHAR(100),
    designation VARCHAR(100),
    amount DECIMAL(12,2),
    amount_in_words VARCHAR(500),
    purpose VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_suspense_vno (voucher_no)
);

CREATE TABLE accounts_petty_voucher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_no VARCHAR(30) NOT NULL,
    voucher_date DATE NOT NULL,
    staff_name VARCHAR(100),
    staff_code VARCHAR(30),
    designation VARCHAR(100),
    department VARCHAR(100),
    suspense_voucher_no VARCHAR(30),
    suspense_date DATE,
    suspense_amount DECIMAL(12,2),
    purpose VARCHAR(500),
    total_amount DECIMAL(12,2) DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_petty_voucher_vno (voucher_no)
);

CREATE TABLE accounts_petty_voucher_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    item_date DATE,
    details VARCHAR(200),
    attendance_no VARCHAR(30),
    item_type VARCHAR(50),
    amount DECIMAL(12,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (voucher_id) REFERENCES accounts_petty_voucher(id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
