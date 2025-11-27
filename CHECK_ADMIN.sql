-- =============================================
-- KIỂM TRA ADMIN USER HIỆN TẠI
-- =============================================

SELECT 
    user_id,
    username,
    email,
    full_name,
    role,
    status,
    password_hash,
    created_at
FROM users 
WHERE username = 'admin';

-- KẾT QUẢ MONG ĐỢI:
-- password_hash phải là: $2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO

