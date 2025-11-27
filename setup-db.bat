@echo off
echo ========================================
echo    Setup Database - StudyDocs
echo ========================================
echo.

echo Step 1: Creating database...
psql -U postgres -c "CREATE DATABASE study_docs_db;"
if %ERRORLEVEL% EQU 0 (
    echo [OK] Database created successfully
) else (
    echo [INFO] Database may already exist, continuing...
)
echo.

echo Step 2: Importing schema...
psql -U postgres -d study_docs_db -f database_schema.sql
if %ERRORLEVEL% EQU 0 (
    echo [OK] Schema imported successfully
) else (
    echo [ERROR] Failed to import schema
    pause
    exit /b 1
)
echo.

echo Step 3: Verifying data...
psql -U postgres -d study_docs_db -c "SELECT COUNT(*) as total_majors FROM majors;"
echo.

echo ========================================
echo Database setup completed!
echo ========================================
echo.
echo You can now run the application:
echo   mvn spring-boot:run
echo.
pause

