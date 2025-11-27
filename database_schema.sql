-- CƠ SỞ DỮ LIỆU CHO WEB CHIA SẺ TÀI LIỆU HỌC TẬP
-- ============================================

-- ============================================
-- PostgreSQL-specific type definitions
-- ============================================

-- Enum types
DO $$ BEGIN
    CREATE TYPE user_role_enum AS ENUM ('admin', 'user');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE user_status_enum AS ENUM ('active', 'locked');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE document_status_enum AS ENUM ('pending', 'approved', 'rejected', 'deleted');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE comment_status_enum AS ENUM ('active', 'deleted');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE report_status_enum AS ENUM ('pending', 'reviewed', 'resolved', 'dismissed');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE feedback_status_enum AS ENUM ('pending', 'read', 'replied');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Generic trigger to auto-update updated_at
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN
    NEW.updated_at := NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 1. Bảng người dùng (Users)
CREATE TABLE IF NOT EXISTS users (
	user_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	username VARCHAR(50) UNIQUE NOT NULL,
	email VARCHAR(100) UNIQUE NOT NULL,
	password_hash VARCHAR(255) NOT NULL,
	full_name VARCHAR(100),
	role user_role_enum DEFAULT 'user',
	status user_status_enum DEFAULT 'active',
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	last_login TIMESTAMP NULL
);

DO $$ BEGIN
    CREATE TRIGGER trg_users_set_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- 2. Bảng chuyên ngành/khoa (Majors/Departments) - Lớp 1
CREATE TABLE IF NOT EXISTS majors (
	major_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	major_name VARCHAR(100) NOT NULL,
	major_code VARCHAR(20) UNIQUE,
	description TEXT
);

-- 3. Bảng môn học/học phần (Subjects/Courses) - Lớp 2
CREATE TABLE IF NOT EXISTS subjects (
	subject_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	major_id INTEGER NOT NULL,
	subject_name VARCHAR(100) NOT NULL,
	subject_code VARCHAR(20),
	description TEXT,
	FOREIGN KEY (major_id) REFERENCES majors(major_id) ON DELETE CASCADE
);

-- 4. Bảng loại tài liệu (Document Types) - Lớp 3
CREATE TABLE IF NOT EXISTS document_types (
	type_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	type_name VARCHAR(50) NOT NULL,
	type_code VARCHAR(20) UNIQUE,
	description TEXT
);

-- 5. Bảng tài liệu (Documents) - Lớp 4
CREATE TABLE IF NOT EXISTS documents (
	document_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	user_id INTEGER NOT NULL,
	subject_id INTEGER NOT NULL,
	type_id INTEGER NOT NULL,
	document_name VARCHAR(200) NOT NULL,
	description TEXT,
	tags TEXT,
	file_path VARCHAR(500) NOT NULL,
	download_count INTEGER DEFAULT 0,
	view_count INTEGER DEFAULT 0,
	status document_status_enum DEFAULT 'pending',
	rejection_reason TEXT,
	approved_by INTEGER NULL,
	approved_at TIMESTAMP NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
	FOREIGN KEY (type_id) REFERENCES document_types(type_id) ON DELETE CASCADE,
	FOREIGN KEY (approved_by) REFERENCES users(user_id) ON DELETE SET NULL
);

DO $$ BEGIN
    CREATE TRIGGER trg_documents_set_updated_at
    BEFORE UPDATE ON documents
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- 6. Bảng đánh giá (Ratings)
CREATE TABLE IF NOT EXISTS ratings (
	rating_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	document_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	rating_value INTEGER NOT NULL CHECK (rating_value >= 1 AND rating_value <= 5),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UNIQUE (document_id, user_id),
	FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

DO $$ BEGIN
    CREATE TRIGGER trg_ratings_set_updated_at
    BEFORE UPDATE ON ratings
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- 7. Bảng bình luận (Comments)
CREATE TABLE IF NOT EXISTS comments (
	comment_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	document_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	parent_comment_id INTEGER NULL,
	content TEXT NOT NULL,
	status comment_status_enum DEFAULT 'active',
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE
);

DO $$ BEGIN
    CREATE TRIGGER trg_comments_set_updated_at
    BEFORE UPDATE ON comments
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- 8. Bảng báo cáo (Reports)
CREATE TABLE IF NOT EXISTS reports (
	report_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	document_id INTEGER NOT NULL,
	reporter_id INTEGER NOT NULL,
	report_type VARCHAR(50),
	report_reason TEXT NOT NULL,
	status report_status_enum DEFAULT 'pending',
	reviewed_by INTEGER NULL,
	reviewed_at TIMESTAMP NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE,
	FOREIGN KEY (reporter_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (reviewed_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 9. Bảng thông báo (Notifications)
CREATE TABLE IF NOT EXISTS notifications (
	notification_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	user_id INTEGER NOT NULL,
	notification_type VARCHAR(50) NOT NULL,
	title VARCHAR(200) NOT NULL,
	content TEXT NOT NULL,
	is_read BOOLEAN DEFAULT FALSE,
	related_document_id INTEGER NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (related_document_id) REFERENCES documents(document_id) ON DELETE SET NULL
);

-- 10. Bảng phản hồi/góp ý (Feedback)
CREATE TABLE IF NOT EXISTS feedback (
	feedback_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	user_id INTEGER NOT NULL,
	subject VARCHAR(200) NOT NULL,
	content TEXT NOT NULL,
	status feedback_status_enum DEFAULT 'pending',
	replied_by INTEGER NULL,
	reply_content TEXT,
	replied_at TIMESTAMP NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (replied_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ============================================
-- INDEXES để tối ưu hiệu suất truy vấn
-- ============================================

-- Indexes cho bảng users
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);

-- Indexes cho bảng documents
CREATE INDEX idx_documents_user ON documents(user_id);
CREATE INDEX idx_documents_subject ON documents(subject_id);
CREATE INDEX idx_documents_type ON documents(type_id);
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_documents_created ON documents(created_at);
-- Full-text search index using GIN on a tsvector
CREATE INDEX idx_documents_search ON documents USING GIN (
	to_tsvector('simple', COALESCE(document_name, '') || ' ' || COALESCE(description, '') || ' ' || COALESCE(tags, ''))
);

-- Indexes cho bảng subjects
CREATE INDEX idx_subjects_major ON subjects(major_id);

-- Indexes cho bảng ratings
CREATE INDEX idx_ratings_document ON ratings(document_id);
CREATE INDEX idx_ratings_user ON ratings(user_id);

-- Indexes cho bảng comments
CREATE INDEX idx_comments_document ON comments(document_id);
CREATE INDEX idx_comments_user ON comments(user_id);
CREATE INDEX idx_comments_parent ON comments(parent_comment_id);

-- Indexes cho bảng reports
CREATE INDEX idx_reports_document ON reports(document_id);
CREATE INDEX idx_reports_status ON reports(status);

-- Indexes cho bảng notifications
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_notifications_created ON notifications(created_at);

-- ============================================
-- DỮ LIỆU MẪU (Sample Data)
-- ============================================

-- Thêm chuyên ngành mẫu
INSERT INTO majors (major_name, major_code, description) VALUES
('Công nghệ thông tin', 'CNTT', 'Ngành công nghệ thông tin'),
('Kinh tế', 'KT', 'Ngành kinh tế'),
('Vật lý kỹ thuật', 'VLKT', 'Ngành vật lý kỹ thuật'),
('Ngoại ngữ', 'NN', 'Ngành ngoại ngữ'),
('Điện – Điện tử', 'DDE', 'Ngành điện – điện tử');

-- Thêm môn học mẫu cho CNTT
INSERT INTO subjects (major_id, subject_name, subject_code, description) VALUES
(1, 'Lập trình C/C++', 'LTC', 'Môn học lập trình C/C++'),
(1, 'Cấu trúc dữ liệu & Giải thuật', 'CTDL', 'Môn học về cấu trúc dữ liệu và giải thuật'),
(1, 'Cơ sở dữ liệu', 'CSDL', 'Môn học về cơ sở dữ liệu'),
(1, 'Mạng máy tính', 'MMT', 'Môn học về mạng máy tính'),
(1, 'Nguyên lý hệ điều hành', 'HDH', 'Môn học về nguyên lý hệ điều hành'),
(1, 'Kiến trúc máy tính', 'KTM', 'Môn học về kiến trúc máy tính'),
(1, 'Phân tích và thiết kế hệ thống', 'PTTKHT', 'Môn học về phân tích và thiết kế hệ thống');

-- Thêm môn học mẫu cho Kinh tế
INSERT INTO subjects (major_id, subject_name, subject_code, description) VALUES
(2, 'Kinh tế vĩ mô', 'KTVM', 'Môn học về kinh tế vĩ mô'),
(2, 'Kinh tế vi mô', 'KTVM2', 'Môn học về kinh tế vi mô'),
(2, 'Nguyên lý kế toán', 'NLKT', 'Môn học về nguyên lý kế toán'),
(2, 'Luật kinh tế', 'LKT', 'Môn học về luật kinh tế');

-- Thêm loại tài liệu
INSERT INTO document_types (type_name, type_code, description) VALUES
('Giáo trình', 'GT', 'Giáo trình học tập'),
('Slides bài giảng', 'SLIDE', 'Slide bài giảng'),
('Bài tập', 'BT', 'Bài tập'),
('Sách tham khảo', 'STK', 'Sách tham khảo'),
('Đề thi học phần', 'DTHP', 'Đề thi học phần'),
('Tài liệu trắc nghiệm', 'TLTN', 'Tài liệu trắc nghiệm'),
('Đồ án', 'DA', 'Đồ án'),
('Một số tài liệu khác', 'KHAC', 'Các tài liệu khác');

-- Lưu ý: Tags được lưu trực tiếp trong cột 'tags' của bảng documents
-- Ví dụ: "lý thuyết, thực hành, bài tập" hoặc dạng JSON

-- Tạo user admin mẫu (password: admin123 - cần hash trong ứng dụng)
INSERT INTO users (username, email, password_hash, full_name, role) VALUES
('admin', 'admin@example.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Quản trị viên', 'admin');

