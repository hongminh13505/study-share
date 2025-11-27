-- =============================================
-- FIX ADMIN PASSWORD
-- Password: admin123
-- =============================================

-- Xóa admin cũ (nếu có)
DELETE FROM users WHERE username = 'admin';

-- Tạo admin mới với password hash đúng
-- Password: admin123
-- Hash: $2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO
INSERT INTO users (username, email, password_hash, full_name, role, status, created_at, updated_at) 
VALUES (
    'admin', 
    'admin@example.com', 
    '$2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO', 
    'Quản trị viên', 
    'admin', 
    'active',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Kiểm tra kết quả
SELECT 
    user_id, 
    username, 
    email, 
    full_name, 
    role, 
    status,
    LEFT(password_hash, 20) as password_hash_preview,
    created_at
FROM users 
WHERE username = 'admin';

-- Kết quả mong đợi:
-- user_id | username | email              | full_name      | role  | status | password_hash_preview      | created_at
-- --------|----------|-------------------|----------------|-------|--------|----------------------------|------------------
-- 1       | admin    | admin@example.com | Quản trị viên  | admin | active | $2a$10$xn3LI/AjqicN... | 2025-11-24 ...

