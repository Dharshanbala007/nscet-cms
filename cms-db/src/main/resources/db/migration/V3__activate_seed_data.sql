-- Fix is_active for all seed data (MySQL defaults to 0)
UPDATE admin_staff_master SET is_active = 1 WHERE is_active IS NULL OR is_active = 0;
UPDATE admin_student_master SET is_active = 1 WHERE is_active IS NULL OR is_active = 0;
