@echo off
REM NSCET CMS - First Run Setup
echo ========================================
echo  NSCET College Management System
echo  First Time Setup
echo ========================================
echo.

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java 17 not found!
    echo Please install JDK 17 from https://adoptium.net
    pause
    exit /b 1
)

REM Check MySQL
echo Checking MySQL connection...
mysql --version >nul 2>&1
if errorlevel 1 (
    echo WARNING: MySQL client not found in PATH.
    echo Please ensure MySQL 8.x is installed and running.
    echo.
)

REM Database setup
echo.
echo Setting up database...
set /p DB_HOST="MySQL Host [localhost]: "
if "%DB_HOST%"=="" set DB_HOST=localhost

set /p DB_PORT="MySQL Port [3306]: "
if "%DB_PORT%"=="" set DB_PORT=3306

set /p DB_USER="MySQL Username [root]: "
if "%DB_USER%"=="" set DB_USER=root

set /p DB_PASS="MySQL Password: "

echo.
echo Creating database...
mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% -e "CREATE DATABASE IF NOT EXISTS nscet_cms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

if errorlevel 1 (
    echo ERROR: Could not create database!
    echo Please check your MySQL credentials.
    pause
    exit /b 1
)

echo Database created successfully!

REM Save config
echo Saving configuration...
mkdir "%APPDATA%\NSCET_CMS" 2>nul
(
echo db.url=jdbc:mysql://%DB_HOST%:%DB_PORT%/nscet_cms?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=Asia/Kolkata
echo db.username=%DB_USER%
echo db.password=%DB_PASS%
echo db.driver=com.mysql.cj.jdbc.Driver
) > "%APPDATA%\NSCET_CMS\config.properties"

echo.
echo ========================================
echo  Setup Complete!
echo  You can now launch NSCET CMS.
echo ========================================
echo.
pause
