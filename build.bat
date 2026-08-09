@echo off
REM NSCET CMS - Build Script
REM Run from project root: build.bat

echo ========================================
echo  NSCET CMS - Build Process
echo ========================================
echo.

REM Check Maven
call mvn --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven not found!
    echo Please install Maven from https://maven.apache.org
    echo Set MAVEN_HOME and add to PATH
    pause
    exit /b 1
)

echo Step 1: Cleaning previous builds...
call mvn clean

echo.
echo Step 2: Compiling all modules...
call mvn compile

if errorlevel 1 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Step 3: Running tests...
call mvn test

echo.
echo Step 4: Packaging modules...
call mvn package -DskipTests

if errorlevel 1 (
    echo ERROR: Packaging failed!
    pause
    exit /b 1
)

echo.
echo Step 5: Creating installer...
echo To create the Windows installer:
echo   1. Install Inno Setup from https://jrsoftware.org/isinfo.php
echo   2. Open cms-installer\src\main\resources\installer\nscet-cms-setup.iss
echo   3. Compile with Inno Setup Compiler
echo.

echo ========================================
echo  Build Complete!
echo  JAR files are in target\ directories
echo ========================================
echo.

REM Run the application
echo Would you like to run the application now? (Y/N)
set /p choice=
if /i "%choice%"=="Y" (
    echo Starting NSCET CMS...
    call mvn javafx:run -pl cms-ui
)
