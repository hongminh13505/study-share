-- Fix notifications table - add missing document_id column
-- Run this in pgAdmin or your PostgreSQL tool

-- Option 1: Add the missing column
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS document_id INTEGER;

-- Option 2: If above doesn't work, drop and let Hibernate recreate
-- DROP TABLE IF EXISTS notifications;

