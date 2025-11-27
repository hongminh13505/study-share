@echo off
echo ============================================
echo RESET ADMIN PASSWORD
echo ============================================
echo.
echo Mat khau moi: admin123
echo.
echo Dang reset mat khau admin...
echo.

mysql -u root -p study_docs_sharing < RESET_ADMIN_PASSWORD.sql

echo.
echo ============================================
echo Hoan thanh!
echo ============================================
echo Thong tin dang nhap:
echo Username: admin (hoac username admin cua ban)
echo Password: admin123
echo.
echo Vui long doi mat khau sau khi dang nhap!
echo ============================================
pause

