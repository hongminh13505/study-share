# Hướng dẫn cài đặt chi tiết StudyDocs

## Bước 1: Cài đặt môi trường

### 1.1 Cài đặt Java 17

**Windows:**
1. Tải Java 17 JDK từ: https://www.oracle.com/java/technologies/downloads/#java17
2. Cài đặt và thiết lập JAVA_HOME
3. Thêm `%JAVA_HOME%\bin` vào PATH
4. Kiểm tra: `java -version`

**Linux:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version
```

**macOS:**
```bash
brew install openjdk@17
java -version
```

### 1.2 Cài đặt PostgreSQL

**Windows:**
1. Tải từ: https://www.postgresql.org/download/windows/
2. Cài đặt với port mặc định: 5432
3. Ghi nhớ password của user `postgres`
4. Cài đặt pgAdmin 4 (đi kèm)

**Linux:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**macOS:**
```bash
brew install postgresql@14
brew services start postgresql@14
```

### 1.3 Cài đặt Maven

**Windows:**
1. Tải từ: https://maven.apache.org/download.cgi
2. Giải nén vào `C:\Program Files\Apache\maven`
3. Thêm `M2_HOME` và `%M2_HOME%\bin` vào PATH
4. Kiểm tra: `mvn -version`

**Linux/macOS:**
```bash
# Linux
sudo apt install maven

# macOS
brew install maven

# Kiểm tra
mvn -version
```

## Bước 2: Chuẩn bị Database

### 2.1 Tạo Database

**Sử dụng psql:**
```bash
# Đăng nhập PostgreSQL
psql -U postgres

# Tạo database
CREATE DATABASE study_docs_db;

# Thoát
\q
```

**Sử dụng pgAdmin:**
1. Mở pgAdmin 4
2. Kết nối đến server PostgreSQL
3. Right-click "Databases" → "Create" → "Database"
4. Đặt tên: `study_docs_db`
5. Click "Save"

### 2.2 Import Schema

```bash
# Từ terminal/command prompt
cd C:\Users\Laptop31.vn\Downloads\Proj1
psql -U postgres -d study_docs_db -f database_schema.sql
```

Hoặc trong pgAdmin:
1. Chọn database `study_docs_db`
2. Tools → Query Tool
3. Mở file `database_schema.sql`
4. Click "Execute" (F5)

### 2.3 Kiểm tra tables

```sql
-- Đăng nhập psql
psql -U postgres -d study_docs_db

-- Xem các bảng
\dt

-- Kiểm tra dữ liệu mẫu
SELECT * FROM majors;
SELECT * FROM users;
```

## Bước 3: Cấu hình Project

### 3.1 Clone hoặc tải project

```bash
cd C:\Users\Laptop31.vn\Downloads\Proj1
```

### 3.2 Cập nhật application.yml

Mở `src/main/resources/application.yml` và cập nhật:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/study_docs_db
    username: postgres          # Thay username của bạn
    password: your_password     # Thay password của bạn
```

### 3.3 Tạo thư mục uploads

```bash
# Windows
mkdir uploads

# Linux/macOS
mkdir -p uploads
chmod 755 uploads
```

## Bước 4: Build và chạy

### 4.1 Build project

```bash
# Từ thư mục gốc của project
mvn clean install
```

Nếu gặp lỗi test, bỏ qua test:
```bash
mvn clean install -DskipTests
```

### 4.2 Chạy ứng dụng

**Cách 1: Sử dụng Maven**
```bash
mvn spring-boot:run
```

**Cách 2: Chạy file JAR**
```bash
java -jar target/study-docs-sharing-1.0.0.jar
```

**Cách 3: Trong IDE**
- Mở project trong IntelliJ IDEA hoặc Eclipse
- Chạy class `StudyDocsSharingApplication.java`

### 4.3 Kiểm tra ứng dụng

1. Mở trình duyệt
2. Truy cập: http://localhost:8080
3. Bạn sẽ thấy trang chủ StudyDocs

## Bước 5: Đăng nhập Admin

### 5.1 Tài khoản admin mặc định

- **URL**: http://localhost:8080/login
- **Username**: `admin`
- **Password**: `admin123`

### 5.2 Truy cập Admin Dashboard

Sau khi đăng nhập:
- Click "Quản trị" trên menu
- Hoặc truy cập: http://localhost:8080/admin/dashboard

## Bước 6: Test chức năng

### 6.1 Đăng ký user mới

1. Truy cập: http://localhost:8080/register
2. Điền thông tin:
   - Username: `testuser`
   - Email: `test@example.com`
   - Họ tên: `Nguyễn Văn A`
   - Password: `test123`
   - Xác nhận password: `test123`
3. Click "Đăng ký"

### 6.2 Upload tài liệu

1. Đăng nhập bằng user vừa tạo
2. Click "Tải lên" trên menu
3. Điền thông tin:
   - Tên tài liệu: `Giáo trình C++`
   - Mô tả: `Tài liệu học C++ cơ bản`
   - Chuyên ngành: `Công nghệ thông tin`
   - Môn học: `Lập trình C/C++`
   - Loại: `Giáo trình`
   - Tags: `c++, lập trình, cơ bản`
4. Chọn file PDF/DOC
5. Click "Tải lên"

### 6.3 Duyệt tài liệu (Admin)

1. Đăng nhập admin
2. Truy cập: http://localhost:8080/admin/documents/pending
3. Click nút "Duyệt" (✓) để duyệt tài liệu

### 6.4 Tìm kiếm và xem tài liệu

1. Truy cập: http://localhost:8080/documents/search
2. Nhập từ khóa: `C++`
3. Click "Tìm kiếm"
4. Click vào tài liệu để xem chi tiết
5. Đánh giá và bình luận

## Troubleshooting

### Lỗi: Connection refused

**Nguyên nhân**: PostgreSQL chưa chạy

**Giải pháp**:
```bash
# Windows
# Mở Services, start "postgresql-x64-14"

# Linux
sudo systemctl start postgresql
sudo systemctl status postgresql

# macOS
brew services start postgresql@14
```

### Lỗi: Port 8080 đã được sử dụng

**Giải pháp 1**: Đổi port trong `application.yml`:
```yaml
server:
  port: 8081
```

**Giải pháp 2**: Kill process trên port 8080:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti:8080 | xargs kill -9
```

### Lỗi: Cannot create uploads directory

**Giải pháp**:
```bash
# Tạo thủ công
mkdir uploads

# Kiểm tra quyền (Linux/macOS)
chmod 755 uploads
```

### Lỗi: Maven build failed

**Giải pháp**:
```bash
# Xóa cache Maven
rm -rf ~/.m2/repository

# Build lại
mvn clean install -U
```

### Lỗi: Hibernate dialect

**Giải pháp**: Thêm vào `application.yml`:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## Tips & Tricks

### 1. Development với live reload

Thêm Spring Boot DevTools (đã có trong pom.xml):
- Thay đổi code tự động reload
- Không cần restart server

### 2. Xem SQL queries

Trong `application.yml`:
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### 3. Test API với curl

```bash
# Login
curl -X POST http://localhost:8080/login \
  -d "username=admin&password=admin123"

# Search documents
curl http://localhost:8080/documents/search?keyword=java
```

### 4. Backup database

```bash
pg_dump -U postgres study_docs_db > backup.sql
```

### 5. Restore database

```bash
psql -U postgres study_docs_db < backup.sql
```

## Kết luận

Sau khi hoàn thành các bước trên, bạn đã có:

- ✅ Web application chạy tại http://localhost:8080
- ✅ Database PostgreSQL với dữ liệu mẫu
- ✅ Tài khoản admin để quản trị
- ✅ Khả năng upload, tìm kiếm, đánh giá tài liệu

Chúc bạn thành công! 🎉

