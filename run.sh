#!/bin/bash

echo "========================================"
echo "   StudyDocs - Document Sharing Web"
echo "========================================"
echo

# Check if uploads directory exists
if [ ! -d "uploads" ]; then
    echo "Creating uploads directory..."
    mkdir -p uploads
    chmod 755 uploads
    echo "[OK] Uploads directory created"
else
    echo "[OK] Uploads directory exists"
fi
echo

# Check if PostgreSQL is running
echo "Checking PostgreSQL..."
if pgrep -x "postgres" > /dev/null; then
    echo "[OK] PostgreSQL is running"
else
    echo "[WARNING] PostgreSQL is not running!"
    echo "Please start PostgreSQL first:"
    echo "  sudo systemctl start postgresql"
    echo
    read -p "Press Enter to continue or Ctrl+C to exit..."
fi
echo

# Build and run
echo "Building project..."
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "[ERROR] Build failed!"
    exit 1
fi
echo

echo "========================================"
echo "Starting StudyDocs..."
echo
echo "Open your browser and go to:"
echo "  http://localhost:8080"
echo
echo "Admin login:"
echo "  Username: admin"
echo "  Password: admin123"
echo "========================================"
echo

mvn spring-boot:run

