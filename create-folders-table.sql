-- ============================================
-- CREATE FOLDERS TABLE
-- ============================================
-- Tạo bảng folders để quản lý thư mục tài liệu
-- ============================================

-- Tạo bảng folders
CREATE TABLE IF NOT EXISTS folders (
    folder_id INT AUTO_INCREMENT PRIMARY KEY,
    folder_name VARCHAR(255) NOT NULL,
    user_id INT NOT NULL,
    parent_folder_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_folder_id) REFERENCES folders(folder_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Thêm cột folder_id vào bảng documents
ALTER TABLE documents 
ADD COLUMN folder_id INT NULL AFTER user_id,
ADD FOREIGN KEY (folder_id) REFERENCES folders(folder_id) ON DELETE SET NULL;

-- Tạo index để tăng tốc truy vấn
CREATE INDEX idx_folders_user_id ON folders(user_id);
CREATE INDEX idx_folders_parent_id ON folders(parent_folder_id);
CREATE INDEX idx_documents_folder_id ON documents(folder_id);

-- ============================================
-- HƯỚNG DẪN SỬ DỤNG:
-- ============================================
-- 1. Mở MySQL Workbench hoặc tool database
-- 2. Kết nối đến database: study_docs_sharing
-- 3. Chạy script này
-- 4. Kiểm tra: SELECT * FROM folders;
-- ============================================

