-- =============================================
-- FIX ENUM COLUMNS - Change from ENUM to VARCHAR
-- =============================================

-- Bước 1: Đổi column type từ ENUM sang VARCHAR
ALTER TABLE users 
  ALTER COLUMN role TYPE VARCHAR(20),
  ALTER COLUMN status TYPE VARCHAR(20);

ALTER TABLE documents 
  ALTER COLUMN status TYPE VARCHAR(20);

ALTER TABLE comments 
  ALTER COLUMN status TYPE VARCHAR(20);

ALTER TABLE reports 
  ALTER COLUMN status TYPE VARCHAR(20);

ALTER TABLE feedback 
  ALTER COLUMN status TYPE VARCHAR(20);

-- Bước 2: Kiểm tra
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'users' 
  AND column_name IN ('role', 'status');

-- Bước 3: Update giá trị (nếu cần)
UPDATE users SET role = LOWER(role);
UPDATE users SET status = LOWER(status);

-- Kết quả mong đợi:
-- column_name | data_type
-- ------------|------------------
-- role        | character varying
-- status      | character varying

