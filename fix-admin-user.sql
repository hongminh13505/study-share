-- Fix admin user password
-- Delete old admin user if exists
DELETE FROM users WHERE username = 'admin';

-- Insert admin user with correct BCrypt hash for password "admin123"
-- Hash generated: $2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO
INSERT INTO users (username, email, password_hash, full_name, role, status) 
VALUES ('admin', 'admin@example.com', '$2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO', 'Quản trị viên', 'admin', 'active');

-- Verify
SELECT user_id, username, email, full_name, role, status FROM users WHERE username = 'admin';

