-- Remove duplicate gender column from user_students table
ALTER TABLE user_students
DROP COLUMN IF EXISTS gender;

-- Remove duplicate gender column from user_teachers table
ALTER TABLE user_teachers
DROP COLUMN IF EXISTS gender; 