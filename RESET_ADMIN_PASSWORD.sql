-- ============================================
-- RESET ADMIN PASSWORD
-- ============================================
-- Mật khẩu mới: admin123
-- Script này sẽ reset mật khẩu admin về "admin123"
-- ============================================

-- Xem thông tin admin hiện tại
SELECT user_id, username, email, full_name, role, status 
FROM users 
WHERE role = 'admin';

-- Reset mật khẩu admin về "admin123"
UPDATE users 
SET password_hash = '$2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO'
WHERE role = 'admin';

-- Đảm bảo status là active
UPDATE users 
SET status = 'active'
WHERE role = 'admin';

-- Xác nhận thay đổi
SELECT user_id, username, email, full_name, role, status, 
       'Password đã được reset về: admin123' as note
FROM users 
WHERE role = 'admin';

-- ============================================
-- HƯỚNG DẪN SỬ DỤNG:
-- ============================================
-- 1. Mở MySQL/PostgreSQL client
-- 2. Kết nối đến database: study_docs_sharing
-- 3. Chạy script này
-- 4. Đăng nhập với:
--    Username: admin (hoặc tên admin của bạn)
--    Password: admin123
-- 5. Đổi mật khẩu ngay sau khi đăng nhập
-- ============================================

