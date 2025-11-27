# ⚡ HƯỚNG DẪN NHANH - StudyDocs

## 🚀 Chạy ngay trong 5 phút

### Bước 1: Tạo Database (2 phút)
```bash
# Mở psql
psql -U postgres

# Tạo database
CREATE DATABASE study_docs_db;
\q

# Import schema
psql -U postgres -d study_docs_db -f database_schema.sql
```

### Bước 2: Cấu hình (30 giây)
Mở `src/main/resources/application.yml`, sửa dòng 10-11:
```yaml
username: postgres     # ← Thay username của bạn
password: postgres     # ← Thay password của bạn
```

### Bước 3: Tạo thư mục uploads (10 giây)
```bash
mkdir uploads
```

### Bước 4: Chạy (2 phút)
```bash
mvn spring-boot:run
```

### Bước 5: Truy cập (5 giây)
1. Mở trình duyệt: **http://localhost:8080**
2. Đăng nhập admin:
   - Username: `admin`
   - Password: `admin123`

---

## ✅ DONE! Enjoy! 🎉

---

## 📋 Checklist
- [ ] PostgreSQL đang chạy
- [ ] Database `study_docs_db` đã tạo
- [ ] Schema đã import
- [ ] `application.yml` đã cấu hình đúng
- [ ] Thư mục `uploads` đã tạo
- [ ] Port 8080 không bị chiếm

---

## ⚠️ Nếu gặp lỗi

### Lỗi: Connection refused
**→ PostgreSQL chưa chạy**
```bash
# Windows: Mở Services → Start postgresql
# Linux: sudo systemctl start postgresql
# macOS: brew services start postgresql
```

### Lỗi: Port 8080 đã sử dụng
**→ Đổi port**
- Mở `application.yml`
- Sửa `server.port: 8081`
- Truy cập: http://localhost:8081

### Lỗi: Cannot create uploads directory
**→ Tạo thủ công**
```bash
mkdir -p uploads
chmod 755 uploads
```

---

## 📚 Tài liệu đầy đủ
- **README.md** - Hướng dẫn tổng quan
- **SETUP.md** - Hướng dẫn chi tiết từng bước
- **PROJECT_SUMMARY.md** - Tổng kết dự án

---

## 🎯 Test nhanh

### 1. Đăng ký user mới
- Truy cập: http://localhost:8080/register
- Điền form → Đăng ký

### 2. Upload tài liệu
- Đăng nhập → Click "Tải lên"
- Chọn chuyên ngành, môn học, loại
- Chọn file → Tải lên

### 3. Duyệt tài liệu (Admin)
- Đăng nhập admin
- Quản trị → Duyệt tài liệu
- Click ✓ để duyệt

### 4. Tìm kiếm & Download
- Tìm kiếm → Nhập từ khóa
- Click tài liệu → Xem chi tiết
- Click "Tải xuống"

---

**Happy Coding! 💻✨**

