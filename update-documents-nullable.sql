-- ============================================
-- Update documents table to allow NULL for subject_id and type_id
-- This enables private documents that don't need classification
-- ============================================

-- Make subject_id nullable
ALTER TABLE documents ALTER COLUMN subject_id DROP NOT NULL;

-- Make type_id nullable
ALTER TABLE documents ALTER COLUMN type_id DROP NOT NULL;

-- Verification
SELECT 
    column_name,
    is_nullable,
    data_type
FROM information_schema.columns
WHERE table_name = 'documents'
    AND column_name IN ('subject_id', 'type_id');




image.png


