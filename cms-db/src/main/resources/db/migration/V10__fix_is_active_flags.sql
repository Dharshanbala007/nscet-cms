-- V10: Ensure all seed records across all master tables have is_active = TRUE

UPDATE admin_fees_details SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_student_details SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_users SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_bank_master SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_department_master SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_designation_master SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_quota_master SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_fees_master SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_staff_master SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
UPDATE admin_student_master SET is_active = TRUE WHERE is_active IS NULL OR is_active = FALSE;
