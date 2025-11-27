@echo off
echo ========================================
echo    Fix Admin User - StudyDocs
echo ========================================
echo.

echo Fixing admin user password...
echo Password will be: admin123
echo.

REM Check if psql is available
where psql >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] psql not found in PATH
    echo Please use pgAdmin to run fix-admin-user.sql
    echo.
    echo Steps in pgAdmin:
    echo 1. Open Query Tool in database study_docs_db
    echo 2. Open file: fix-admin-user.sql
    echo 3. Execute the query
    echo.
    pause
    exit /b 0
)

echo Running SQL script...
psql -U postgres -d study_docs_db -f fix-admin-user.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [OK] Admin user fixed successfully!
    echo.
    echo You can now login with:
    echo   Username: admin
    echo   Password: admin123
    echo.
) else (
    echo.
    echo [ERROR] Failed to fix admin user
    echo.
    echo Please run fix-admin-user.sql manually in pgAdmin:
    echo 1. Open pgAdmin
    echo 2. Connect to database study_docs_db
    echo 3. Tools - Query Tool
    echo 4. Open file: fix-admin-user.sql
    echo 5. Execute (F5)
    echo.
)

pause

