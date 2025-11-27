# StudyDocs - Web Chia Sẻ Tài Liệu Học Tập

## Giới thiệu

StudyDocs là một nền tảng chia sẻ tài liệu học tập miễn phí dành cho sinh viên, được xây dựng với Spring Boot 3.5.0, Spring Security 6, và PostgreSQL.

## Tính năng

### Người dùng
- ✅ Đăng ký, đăng nhập tài khoản
- ✅ Tìm kiếm tài liệu theo chuyên ngành, môn học, từ khóa
- ✅ Xem chi tiết tài liệu
- ✅ Tải xuống tài liệu
- ✅ Đánh giá tài liệu (1-5 sao)
- ✅ Bình luận trên tài liệu
- ✅ Tải lên tài liệu mới
- ✅ Quản lý tài liệu của mình

### Quản trị viên
- ✅ Dashboard thống kê
- ✅ Duyệt/Từ chối tài liệu
- ✅ Quản lý người dùng (Khóa/Mở khóa)
- ✅ Xem báo cáo thống kê

## Công nghệ sử dụng

### Backend
- **Spring Boot 3.5.0** - Framework chính
- **Spring Security 6** - Bảo mật và xác thực
- **Spring Data JPA** - ORM với Hibernate
- **PostgreSQL** - Cơ sở dữ liệu
- **Maven** - Quản lý dependencies

### Frontend
- **Thymeleaf** - Template engine
- **HTML5, CSS3, JavaScript** - Giao diện người dùng
- **Font Awesome 6** - Icon

## Yêu cầu hệ thống

- Java 17 trở lên
- PostgreSQL 12 trở lên
- Maven 3.6 trở lên
- RAM tối thiểu: 2GB
- Dung lượng ổ cứng: 500MB

## Cài đặt và chạy

### 1. Cài đặt PostgreSQL

Tải và cài đặt PostgreSQL từ: https://www.postgresql.org/download/

### 2. Tạo cơ sở dữ liệu

```sql
CREATE DATABASE study_docs_db;
```

Chạy file `database_schema.sql` để tạo các bảng:

```bash
psql -U postgres -d study_docs_db -f database_schema.sql
```

### 3. Cấu hình application.yml

Mở file `src/main/resources/application.yml` và cập nhật thông tin kết nối database:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/study_docs_db
    username: postgres  # Thay bằng username của bạn
    password: postgres  # Thay bằng password của bạn
```

### 4. Tạo thư mục upload

```bash
mkdir uploads
```

### 5. Build project

```bash
mvn clean install
```

### 6. Chạy ứng dụng

```bash
mvn spring-boot:run
```

Hoặc chạy file JAR:

```bash
java -jar target/study-docs-sharing-1.0.0.jar
```

### 7. Truy cập ứng dụng

Mở trình duyệt và truy cập: **http://localhost:8080**

## Tài khoản mặc định

### Admin
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: admin@example.com

## Cấu trúc thư mục

```
study-docs-sharing/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/studydocs/
│   │   │       ├── config/              # Cấu hình Spring
│   │   │       ├── controller/          # Controllers
│   │   │       ├── model/
│   │   │       │   ├── entity/          # JPA Entities
│   │   │       │   └── enums/           # Enums
│   │   │       ├── repository/          # Repositories
│   │   │       ├── security/            # Security config
│   │   │       ├── service/             # Business logic
│   │   │       └── StudyDocsSharingApplication.java
│   │   ├── resources/
│   │   │   ├── static/
│   │   │   │   ├── css/                 # CSS files
│   │   │   │   └── js/                  # JavaScript files
│   │   │   ├── templates/               # Thymeleaf templates
│   │   │   │   ├── admin/               # Admin pages
│   │   │   │   ├── auth/                # Login/Register
│   │   │   │   ├── documents/           # Document pages
│   │   │   │   ├── home.html
│   │   │   │   └── layout.html
│   │   │   └── application.yml          # Cấu hình
├── uploads/                              # Thư mục lưu file
├── database_schema.sql                   # Database schema
├── pom.xml                               # Maven config
└── README.md
```

## API Endpoints

### Public
- `GET /` - Trang chủ
- `GET /home` - Trang chủ
- `GET /login` - Đăng nhập
- `POST /login` - Xử lý đăng nhập
- `GET /register` - Đăng ký
- `POST /register` - Xử lý đăng ký
- `GET /documents/search` - Tìm kiếm tài liệu
- `GET /documents/view/{id}` - Xem chi tiết tài liệu
- `GET /documents/download/{id}` - Tải xuống tài liệu

### Authenticated
- `GET /documents/upload` - Trang tải lên
- `POST /documents/upload` - Xử lý tải lên
- `GET /documents/my-documents` - Tài liệu của tôi
- `POST /documents/rate/{id}` - Đánh giá tài liệu
- `POST /comments/add` - Thêm bình luận
- `POST /comments/delete/{id}` - Xóa bình luận
- `POST /logout` - Đăng xuất

### Admin
- `GET /admin/dashboard` - Dashboard
- `GET /admin/documents/pending` - Tài liệu chờ duyệt
- `POST /admin/documents/approve/{id}` - Duyệt tài liệu
- `POST /admin/documents/reject/{id}` - Từ chối tài liệu
- `GET /admin/users` - Quản lý người dùng
- `POST /admin/users/lock/{id}` - Khóa người dùng
- `POST /admin/users/unlock/{id}` - Mở khóa người dùng

### REST API
- `GET /api/subjects/by-major/{majorId}` - Lấy môn học theo chuyên ngành

## Database Schema

### Các bảng chính

1. **users** - Người dùng
2. **majors** - Chuyên ngành
3. **subjects** - Môn học
4. **document_types** - Loại tài liệu
5. **documents** - Tài liệu
6. **ratings** - Đánh giá
7. **comments** - Bình luận
8. **reports** - Báo cáo
9. **notifications** - Thông báo
10. **feedback** - Phản hồi

Chi tiết schema xem file `database_schema.sql`

## Tính năng nâng cao

### 1. Full-text Search
- Tìm kiếm nhanh tài liệu theo tên, mô tả, tags
- Sử dụng PostgreSQL Full-text Search

### 2. File Upload
- Hỗ trợ: PDF, DOC, DOCX, PPT, PPTX
- Giới hạn: 50MB/file
- Lưu trữ file với tên unique (UUID)

### 3. Security
- Password hashing với BCrypt
- Session management
- Role-based access control (USER, ADMIN)
- CSRF protection

### 4. Rating System
- Đánh giá 1-5 sao
- Tính trung bình rating tự động
- Mỗi user chỉ đánh giá 1 lần

### 5. Comment System
- Bình luận đa cấp (reply)
- Soft delete comments
- Real-time comment count

## Troubleshooting

### Lỗi kết nối database
```
Error: Connection refused
```
**Giải pháp**: Kiểm tra PostgreSQL đã chạy chưa và cấu hình đúng thông tin trong `application.yml`

### Lỗi upload file
```
Error: File size exceeds maximum
```
**Giải pháp**: Kiểm tra cấu hình `spring.servlet.multipart.max-file-size` trong `application.yml`

### Lỗi khởi động ứng dụng
```
Error: Port 8080 is already in use
```
**Giải pháp**: Thay đổi port trong `application.yml` hoặc dừng ứng dụng đang chạy trên port 8080

## Development

### Chạy ở chế độ development

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Debug

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Xem logs

Logs được lưu tại console. Để lưu vào file, thêm vào `application.yml`:

```yaml
logging:
  file:
    name: logs/application.log
```

## Testing

### Chạy tests

```bash
mvn test
```

### Test coverage

```bash
mvn clean test jacoco:report
```

## Production Deployment

### 1. Build production JAR

```bash
mvn clean package -DskipTests
```

### 2. Chạy với profile production

```bash
java -jar target/study-docs-sharing-1.0.0.jar --spring.profiles.active=prod
```

### 3. Tối ưu JVM

```bash
java -Xms512m -Xmx2048m -jar target/study-docs-sharing-1.0.0.jar
```

## Bảo mật

- ✅ Password được hash với BCrypt
- ✅ SQL Injection prevention với JPA
- ✅ XSS protection với Thymeleaf
- ✅ CSRF protection được bật
- ✅ Session timeout: 30 phút
- ✅ File upload validation

## Tương lai

- [ ] Elasticsearch cho tìm kiếm nâng cao
- [ ] Redis cho caching
- [ ] WebSocket cho thông báo real-time
- [ ] Export tài liệu sang PDF
- [ ] API RESTful đầy đủ
- [ ] Mobile app
- [ ] Tích hợp mạng xã hội
- [ ] AI gợi ý tài liệu

## Đóng góp

Mọi đóng góp đều được hoan nghênh! Vui lòng tạo Pull Request hoặc Issue.

## Liên hệ

- Email: support@studydocs.com
- Website: https://studydocs.com

## License

© 2024 StudyDocs. All rights reserved.

---

**Chúc bạn sử dụng vui vẻ! 🎓📚**

