# 🔧 HƯỚNG DẪN FIX LỖI ĐĂNG NHẬP

## ❌ Vấn đề
- Khi đăng nhập, trang chỉ reload lại
- Không vào được sau khi bấm đăng nhập
- **Nguyên nhân**: Password hash trong database không đúng

---

## ✅ GIẢI PHÁP - Fix Admin User

### Cách 1: Sử dụng pgAdmin (KHUYẾN NGHỊ)

1. **Mở pgAdmin**
   - Start pgAdmin từ Start Menu

2. **Kết nối database**
   - Servers → PostgreSQL → Databases → `study_docs_db`
   - Nhập password: `ancutkhong1235`

3. **Mở Query Tool**
   - Click phải vào `study_docs_db`
   - Chọn: **Tools** → **Query Tool**

4. **Mở file SQL**
   - Trong Query Tool, click icon **Open File**
   - Chọn file: `fix-admin-user.sql`

5. **Execute**
   - Click nút **Execute** (hoặc nhấn F5)
   - Xem kết quả: Sẽ hiện "DELETE 1" và "INSERT 0 1"

6. **Verify**
   - Kiểm tra trong tab "Data Output" phía dưới
   - Sẽ thấy thông tin admin user:
     ```
     user_id | username | email              | full_name       | role  | status
     --------|----------|-------------------|----------------|-------|--------
     1       | admin    | admin@example.com | Quản trị viên  | admin | active
     ```

---

### Cách 2: Sử dụng Command Line

**Nếu bạn đã add psql vào PATH:**

```bash
# Chạy script tự động
fix-admin.bat

# Hoặc chạy thủ công
psql -U postgres -d study_docs_db -f fix-admin-user.sql
```

---

## 🚀 SAU KHI FIX

### 1. Restart ứng dụng Spring Boot

**Nếu đang chạy:**
- Nhấn `Ctrl+C` trong terminal để dừng
- Chạy lại: `mvn spring-boot:run`

**Hoặc:**
```bash
# Build lại
mvn clean install -DskipTests

# Run
mvn spring-boot:run
```

---

### 2. Test đăng nhập

1. **Mở trình duyệt**: http://localhost:8080/login

2. **Chọn role**:
   - Click vào icon **"Quản trị"** (icon shield)
   - Card sẽ chuyển màu tím khi được chọn

3. **Nhập thông tin**:
   - **Username**: `admin`
   - **Password**: `admin123`
   
4. **Đăng nhập**:
   - Click nút "Đăng nhập"
   - Bạn sẽ được chuyển đến **Admin Dashboard**

---

## 🔐 TÀI KHOẢN MẶC ĐỊNH

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: admin@example.com
- **Redirect**: `/admin/dashboard` sau khi login

### User Account (Tạo mới)
- Click "Đăng ký ngay" để tạo tài khoản user
- Sau khi đăng ký, đăng nhập sẽ vào trang `/home`

---

## 🎨 GIAO DIỆN MỚI

### Thay đổi giao diện Login:
✅ Có chọn role (Admin/User) với icon đẹp
✅ Nền gradient xanh tím
✅ Animation mượt mà
✅ Hiển thị lỗi rõ ràng
✅ Toggle show/hide password

---

## ⚠️ NẾU VẪN GẶP LỖI

### Lỗi 1: "Cannot connect to database"
```bash
# Kiểm tra PostgreSQL có chạy không
# Mở Services → PostgreSQL → Start
```

### Lỗi 2: "study_docs_db does not exist"
```bash
# Tạo database
psql -U postgres
CREATE DATABASE study_docs_db;
\q

# Import schema
psql -U postgres -d study_docs_db -f database_schema.sql
```

### Lỗi 3: "Tên đăng nhập hoặc mật khẩu không đúng"
- Chạy lại `fix-admin-user.sql` trong pgAdmin
- Đảm bảo username: `admin` và password: `admin123`

### Lỗi 4: Port 8080 đã được sử dụng
```yaml
# Sửa trong application.yml
server:
  port: 8081  # Đổi sang port khác
```

---

## 📝 KIỂM TRA LOG

Khi chạy ứng dụng, xem log để kiểm tra:

```
✅ GOOD LOG:
  - HikariPool-1 - Start completed.
  - Tomcat started on port 8080
  - Started StudyDocsSharingApplication

❌ BAD LOG:
  - Failed to configure a DataSource
  - Connection refused
  - Password authentication failed
```

---

## 📞 DEBUG STEPS

1. **Kiểm tra database có tồn tại không:**
   ```sql
   psql -U postgres -l | grep study_docs_db
   ```

2. **Kiểm tra admin user:**
   ```sql
   psql -U postgres -d study_docs_db
   SELECT username, role, status FROM users WHERE username = 'admin';
   ```

3. **Kiểm tra password hash:**
   ```sql
   SELECT username, password_hash FROM users WHERE username = 'admin';
   -- Hash phải bắt đầu bằng: $2a$10$xn3LI...
   ```

4. **Test kết nối database:**
   ```bash
   psql -U postgres -d study_docs_db -c "SELECT version();"
   ```

---

## ✅ CHECKLIST

Trước khi test login, đảm bảo:

- [ ] PostgreSQL đang chạy
- [ ] Database `study_docs_db` đã tồn tại
- [ ] Schema đã được import (có 10 tables)
- [ ] Admin user đã được fix (chạy fix-admin-user.sql)
- [ ] Spring Boot app đang chạy (http://localhost:8080)
- [ ] Không có lỗi trong log

---

**🎉 Sau khi làm theo hướng dẫn, login sẽ hoạt động!**

_Updated: 2025-11-24_

