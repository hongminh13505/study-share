@echo off
echo ========================================
echo    StudyDocs - Document Sharing Web
echo ========================================
echo.

REM Check if uploads directory exists
if not exist "uploads" (
    echo Creating uploads directory...
    mkdir uploads
    echo [OK] Uploads directory created
) else (
    echo [OK] Uploads directory exists
)
echo.

REM Check if PostgreSQL is running
echo Checking PostgreSQL...
tasklist /FI "IMAGENAME eq postgres.exe" 2>NUL | find /I /N "postgres.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo [OK] PostgreSQL is running
) else (
    echo [WARNING] PostgreSQL is not running!
    echo Please start PostgreSQL service first.
    echo.
    pause
    exit /b 1
)
echo.

REM Build and run
echo Building project...
call mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)
echo.

echo ========================================
echo Starting StudyDocs...
echo.
echo Open your browser and go to:
echo   http://localhost:8080
echo.
echo Admin login:
echo   Username: admin
echo   Password: admin123
echo ========================================
echo.

call mvn spring-boot:run

