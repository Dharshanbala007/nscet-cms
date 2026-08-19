-- V9: Seed mock users and assign roles
-- Default password for all mock accounts: admin123 (BCrypt hash)

INSERT INTO admin_users (username, password_hash, full_name, email, staff_id, is_active, is_locked, failed_attempts) VALUES
('accounts_user', '$2a$12$ggj7sSTUbozOIHeYCL.Cz.ZEAIM6j3ZfuDDwA4hShxAUjFeKt1h7m', 'Accounts Manager', 'accounts@nscet.edu', 18, TRUE, FALSE, 0),
('payroll_user', '$2a$12$ggj7sSTUbozOIHeYCL.Cz.ZEAIM6j3ZfuDDwA4hShxAUjFeKt1h7m', 'Payroll Officer', 'payroll@nscet.edu', 19, TRUE, FALSE, 0),
('viewer_user', '$2a$12$ggj7sSTUbozOIHeYCL.Cz.ZEAIM6j3ZfuDDwA4hShxAUjFeKt1h7m', 'Audit Inspector', 'viewer@nscet.edu', 20, TRUE, FALSE, 0),
('principal', '$2a$12$ggj7sSTUbozOIHeYCL.Cz.ZEAIM6j3ZfuDDwA4hShxAUjFeKt1h7m', 'Dr. R. Velraj (Principal)', 'principal@nscet.edu', 1, TRUE, FALSE, 0),
('hod_cse', '$2a$12$ggj7sSTUbozOIHeYCL.Cz.ZEAIM6j3ZfuDDwA4hShxAUjFeKt1h7m', 'Dr. S. Kannan (HOD CSE)', 'hod_cse@nscet.edu', 2, TRUE, FALSE, 0);

-- Assign Roles:
-- accounts_user (id=2) -> ACCOUNTS (role_id=2)
-- payroll_user (id=3) -> PAYROLL (role_id=3)
-- viewer_user (id=4) -> VIEWER (role_id=4)
-- principal (id=5) -> ADMIN (role_id=1)
-- hod_cse (id=6) -> ADMIN (role_id=1)

INSERT INTO admin_user_roles (user_id, role_id)
SELECT id, 2 FROM admin_users WHERE username = 'accounts_user';

INSERT INTO admin_user_roles (user_id, role_id)
SELECT id, 3 FROM admin_users WHERE username = 'payroll_user';

INSERT INTO admin_user_roles (user_id, role_id)
SELECT id, 4 FROM admin_users WHERE username = 'viewer_user';

INSERT INTO admin_user_roles (user_id, role_id)
SELECT id, 1 FROM admin_users WHERE username = 'principal';

INSERT INTO admin_user_roles (user_id, role_id)
SELECT id, 1 FROM admin_users WHERE username = 'hod_cse';
