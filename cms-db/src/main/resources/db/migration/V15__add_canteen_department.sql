-- V15: Add Canteen Department to admin_department_master
INSERT INTO admin_department_master (code, short_name, name, type, is_active)
SELECT 'CAN', 'CANTEEN', 'Canteen', 'Administrative', TRUE
WHERE NOT EXISTS (SELECT 1 FROM admin_department_master WHERE code = 'CAN' OR name = 'Canteen');
