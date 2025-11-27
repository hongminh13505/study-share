@echo off
echo ============================================
echo TAO TAI KHOAN ADMIN MOI
echo ============================================
echo.
echo Username: admin2
echo Password: admin123
echo Email: admin2@studyshare.com
echo.
echo Dang tao tai khoan admin moi...
echo.

mysql -u root -p study_docs_sharing < CREATE_NEW_ADMIN.sql

echo.
echo ============================================
echo Hoan thanh!
echo ============================================
echo Thong tin dang nhap moi:
echo Username: admin2
echo Password: admin123
echo.
echo Vui long doi mat khau sau khi dang nhap!
echo ============================================
pause

