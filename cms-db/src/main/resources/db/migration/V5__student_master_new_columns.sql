-- Add new columns to admin_student_master
ALTER TABLE admin_student_master
    ADD COLUMN date_of_joining DATE NULL,
    ADD COLUMN section VARCHAR(5) NULL,
    ADD COLUMN occupation VARCHAR(100) NULL,
    ADD COLUMN religion VARCHAR(50) NULL;

-- Update V1 seed data with date_of_joining, section, occupation, religion
UPDATE admin_student_master SET
    date_of_joining = '2024-07-01',
    section = 'A',
    occupation = 'Service',
    religion = 'Hindu'
WHERE is_active = 1;
