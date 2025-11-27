-- ============================================
-- TẠO TÀI KHOẢN ADMIN MỚI
-- ============================================
-- Username: admin2
-- Password: admin123
-- Email: admin2@studyshare.com
-- ============================================

-- Xem danh sách admin hiện tại
SELECT user_id, username, email, full_name, role, status 
FROM users 
WHERE role = 'admin';

-- Tạo tài khoản admin mới
INSERT INTO users (username, email, password_hash, full_name, role, status, created_at)
VALUES (
    'admin2',
    'admin2@studyshare.com',
    '$2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO',
    'Administrator 2',
    'admin',
    'active',
    NOW()
);

-- Xem tất cả admin sau khi tạo
SELECT user_id, username, email, full_name, role, status, created_at,
       'Password: admin123' as note
FROM users 
WHERE role = 'admin'
ORDER BY user_id DESC;

-- ============================================
-- THÔNG TIN ĐĂNG NHẬP MỚI:
-- ============================================
-- Username: admin2
-- Password: admin123
-- Email: admin2@studyshare.com
-- ============================================

-- Nếu muốn tạo nhiều admin khác:
-- Chỉ cần thay đổi username và email, giữ nguyên password_hash

-- VÍ DỤ: Tạo admin3
-- INSERT INTO users (username, email, password_hash, full_name, role, status, created_at)
-- VALUES (
--     'admin3',
--     'admin3@studyshare.com',
--     '$2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO',
--     'Administrator 3',
--     'admin',
--     'active',
--     NOW()
-- );

-- ============================================
-- LƯU Ý:
-- ============================================
-- 1. Tất cả admin đều có mật khẩu: admin123
-- 2. Đổi mật khẩu ngay sau khi đăng nhập
-- 3. Password hash trên là BCrypt của "admin123"
-- ============================================

