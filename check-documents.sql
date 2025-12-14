-- Check all documents and their status
SELECT document_id, document_name, status, type_id, subject_id, approved_at, created_at 
FROM documents 
ORDER BY created_at DESC;

-- Check approved documents count
SELECT COUNT(*) as approved_count FROM documents WHERE status = 'APPROVED';

-- Check approved documents with type or subject
SELECT COUNT(*) as approved_with_type_subject 
FROM documents 
WHERE status = 'APPROVED' AND (type_id IS NOT NULL OR subject_id IS NOT NULL);

