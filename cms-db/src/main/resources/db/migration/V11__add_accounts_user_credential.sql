-- V11: Add 'accounts' user with password 'accounts123' and ensure 'admin' user has 'admin123'

INSERT INTO admin_users (username, password_hash, full_name, email, staff_id, is_active, is_locked, failed_attempts)
VALUES ('accounts', '$2a$12$PHY0uy3q6GxCcjrLIAXmdeSke/qJFTVPWOiM/qxcmb1.Bsx9VWFjS', 'Accounts Portal User', 'accounts@nscet.edu', NULL, TRUE, FALSE, 0)
ON DUPLICATE KEY UPDATE password_hash = '$2a$12$PHY0uy3q6GxCcjrLIAXmdeSke/qJFTVPWOiM/qxcmb1.Bsx9VWFjS', is_active = TRUE, is_locked = FALSE;

-- Assign ACCOUNTS role (role_id = 2) to 'accounts' user
INSERT INTO admin_user_roles (user_id, role_id)
SELECT id, 2 FROM admin_users WHERE username = 'accounts'
ON DUPLICATE KEY UPDATE role_id = 2;
