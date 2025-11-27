@echo off
echo ========================================
echo    Test Database Connection
echo ========================================
echo.

echo Testing PostgreSQL connection...
psql -U postgres -d study_docs_db -c "SELECT 'DATABASE CONNECTION OK!' as status;"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [OK] Database connection successful!
    echo.
    echo Checking tables...
    psql -U postgres -d study_docs_db -c "SELECT COUNT(*) as total_tables FROM information_schema.tables WHERE table_schema='public';"
    echo.
    echo Checking users...
    psql -U postgres -d study_docs_db -c "SELECT COUNT(*) as total_users FROM users;"
    echo.
    echo Checking majors...
    psql -U postgres -d study_docs_db -c "SELECT major_name FROM majors;"
    echo.
    echo ========================================
    echo All checks passed!
    echo ========================================
) else (
    echo.
    echo [ERROR] Cannot connect to database!
    echo.
    echo Please check:
    echo 1. PostgreSQL is running
    echo 2. Database 'study_docs_db' exists
    echo 3. Password is correct
)

echo.
pause

