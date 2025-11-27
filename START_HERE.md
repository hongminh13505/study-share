# ✅ DỰ ÁN ĐÃ SẴN SÀNG!

Build thành công! Bây giờ bạn có thể chạy ứng dụng.

## 🚀 CHẠY NGAY (3 BƯỚC)

### Bước 1: Tạo Database
```bash
# Mở psql
psql -U postgres

# Tạo database
CREATE DATABASE study_docs_db;
\q

# Import schema
psql -U postgres -d study_docs_db -f database_schema.sql
```

### Bước 2: Tạo thư mục uploads
```bash
mkdir uploads
```

### Bước 3: Chạy ứng dụng
```bash
mvn spring-boot:run
```

Hoặc:
```bash
java -jar target/study-docs-sharing-1.0.0.jar
```

## 🌐 Truy cập

**URL**: http://localhost:8080

**Admin Login**:
- Username: `admin`
- Password: `admin123`

## ✨ Tính năng

- ✅ Đăng ký/Đăng nhập
- ✅ Tìm kiếm tài liệu
- ✅ Upload tài liệu
- ✅ Đánh giá & Bình luận
- ✅ Admin dashboard
- ✅ Duyệt tài liệu

## 📚 Tài liệu

- **QUICKSTART.md** - Hướng dẫn nhanh
- **SETUP.md** - Hướng dẫn chi tiết
- **README.md** - Tổng quan
- **PROJECT_SUMMARY.md** - Thống kê dự án

## 💡 Lưu ý

- Database password đã cấu hình: `ancutkhong1235`
- Port: 8080
- Upload directory: `./uploads`
- Max file size: 50MB

## ⚠️ Nếu gặp lỗi

### Port 8080 bị chiếm
Sửa trong `application.yml` dòng 41:
```yaml
server:
  port: 8081
```

### PostgreSQL chưa chạy
```bash
# Windows: Mở Services → Start postgresql
# Linux: sudo systemctl start postgresql
```

---

**Chúc bạn thành công!** 🎉

_Build completed: 2025-11-24_


