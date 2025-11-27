@echo off
echo ============================================
echo TAO BANG FOLDERS
echo ============================================
echo.
echo Dang tao bang folders va cap nhat documents...
echo.

mysql -u root -p study_docs_sharing < create-folders-table.sql

echo.
echo ============================================
echo Hoan thanh!
echo ============================================
pause

