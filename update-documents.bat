@echo off
echo Updating documents table to allow nullable subject_id and type_id...
psql -U postgres -d studydocs -f update-documents-nullable.sql
echo.
echo Done! Press any key to exit...
pause







