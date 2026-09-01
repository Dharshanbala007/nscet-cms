# NSCET College Management System (CMS) - Database Schema Documentation

## Overview
This document outlines the database schema architecture for the **NSCET College Management System (CMS)**. The system utilizes MySQL/MariaDB with Flyway migrations (`V1__initial_schema.sql`). All table names follow the system standard with the `admin_` prefix.

---

## 1. Authentication & Security Layer

### `admin_users`
Stores user credentials, authentication statuses, and account lock states.
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `username` (VARCHAR 50, UNIQUE)
- `password_hash` (VARCHAR 255)
- `full_name` (VARCHAR 100)
- `email` (VARCHAR 100)
- `staff_id` (BIGINT)
- `is_active` (BOOLEAN)
- `is_locked` (BOOLEAN)
- `failed_attempts` (INT)

### `admin_roles` & `admin_permissions`
Defines RBAC (Role-Based Access Control) roles and module-level permissions.
- `admin_roles`: `id`, `name`, `description`
- `admin_permissions`: `id`, `name`, `module`, `action`
- `admin_user_roles`: Mapping table (`user_id`, `role_id`)
- `admin_role_permissions`: Mapping table (`role_id`, `permission_id`)

---

## 2. Master Data Layer

### `admin_department_master`
Department records across Teaching, Non-Teaching, and Administrative divisions.
- `id` (BIGINT, PK)
- `code` (VARCHAR 10, UNIQUE)
- `short_name` (VARCHAR 20)
- `name` (VARCHAR 100)
- `type` (VARCHAR 50)
- `is_active` (BOOLEAN)

### `admin_designation_master`
Designation titles, categories, and color codes for UI rendering.
- `id` (BIGINT, PK)
- `code` (VARCHAR 10, UNIQUE)
- `short_name` (VARCHAR 20)
- `name` (VARCHAR 100)
- `category` (VARCHAR 50)
- `color_code` (VARCHAR 20)

### `admin_bank_master`
Bank information for staff salary disbursemnt.
- `id` (BIGINT, PK)
- `bank_short_name` (VARCHAR 50)
- `account_number` (VARCHAR 30)
- `bank_name` (VARCHAR 100)
- `branch` (VARCHAR 100)
- `ifsc_code` (VARCHAR 20)

---

## 3. Staff & Human Resources Layer

### `admin_staff_master`
Core staff registry for teaching, non-teaching, and contract employees.
- `id` (BIGINT, PK)
- `staff_code` (VARCHAR 20, UNIQUE)
- `name` (VARCHAR 150)
- `department_id` (BIGINT, FK)
- `designation_id` (BIGINT, FK)
- `category` (VARCHAR 50)
- `staff_group` (VARCHAR 50)
- `date_of_joining` (DATE)
- `date_of_birth` (DATE)
- `sex` (VARCHAR 10)
- `mobile_no` (VARCHAR 20)
- `email` (VARCHAR 100)
- `pf_active` (BOOLEAN)
- `esi_active` (BOOLEAN)
- `bank_acc_no` (VARCHAR 30)

---

## 4. Payroll & Salary Layer

### `admin_staff_salary`
Main salary structure details for each employee.
- `id` (BIGINT, PK)
- `staff_id` (BIGINT, FK)
- `basic_pay` (DECIMAL 12,2)
- `hra` (DECIMAL 12,2)
- `da` (DECIMAL 12,2)
- `washing_allowance` (DECIMAL 12,2)
- `travelling_allowance` (DECIMAL 12,2)
- `special_allowance` (DECIMAL 12,2)
- `gross_pay` (DECIMAL 12,2)
- `net_pay` (DECIMAL 12,2)
- `pf_deduction` (DECIMAL 12,2)
- `esi_deduction` (DECIMAL 12,2)
- `effective_date` (DATE)

### `admin_payroll_calculation`
Monthly payroll calculation log with breakdown of earnings and deductions.
- `id` (BIGINT, PK)
- `staff_id` (BIGINT, FK)
- `pay_month` (INT)
- `pay_year` (INT)
- `working_days` (INT)
- `present_days` (DECIMAL 4,1)
- `lop_days` (DECIMAL 4,1)
- `gross_earned` (DECIMAL 12,2)
- `total_deductions` (DECIMAL 12,2)
- `net_earned` (DECIMAL 12,2)

---

## 5. Attendance & Leave Layer

### `admin_leave_master`
Leave types (CL, LOP, AB, SPL, OD Exam, VL, Comp, ML, OD FDP, OD Admission, OD Others).
- `id` (BIGINT, PK)
- `code` (VARCHAR 10, UNIQUE)
- `name` (VARCHAR 50)
- `annual_allowed` (DECIMAL 4,1)

### `admin_staff_attendance`
Daily attendance transactions and morning/afternoon session statuses.
- `id` (BIGINT, PK)
- `staff_id` (BIGINT, FK)
- `attendance_date` (DATE)
- `session` (VARCHAR 20) -- Fore Noon / After Noon
- `status` (VARCHAR 10) -- P, CL, LOP, AB, OD, etc.

---

## 6. Student & Fee Management Layer

### `admin_student_master`
Student profiles, roll numbers, admission details, and academic batches.
- `id` (BIGINT, PK)
- `register_number` (VARCHAR 20, UNIQUE)
- `roll_number` (VARCHAR 20)
- `name` (VARCHAR 150)
- `department_id` (BIGINT, FK)
- `batch_year` (INT)

### `admin_fee_collection`
Student fee payment transactions and receipt logs.
- `id` (BIGINT, PK)
- `receipt_no` (VARCHAR 30, UNIQUE)
- `student_id` (BIGINT, FK)
- `payment_date` (DATE)
- `amount_paid` (DECIMAL 12,2)
- `payment_mode` (VARCHAR 30)
