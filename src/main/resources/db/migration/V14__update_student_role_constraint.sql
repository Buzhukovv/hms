-- First, drop the existing constraint
ALTER TABLE user_students
DROP CONSTRAINT IF EXISTS user_students_role_check;

-- Update any existing records with old enum values to new enum values
UPDATE user_students SET role = 'MASTER_STUDENT_1_Y' WHERE role = 'MASTER_1_Y';
UPDATE user_students SET role = 'MASTER_STUDENT_2_Y' WHERE role = 'MASTER_2_Y';
UPDATE user_students SET role = 'BACHELOR_DEGREE_1_Y' WHERE role = 'BACHELOR_1_Y';
UPDATE user_students SET role = 'BACHELOR_DEGREE_2_Y' WHERE role = 'BACHELOR_2_Y';
UPDATE user_students SET role = 'BACHELOR_DEGREE_3_Y' WHERE role = 'BACHELOR_3_Y';
UPDATE user_students SET role = 'BACHELOR_DEGREE_4_Y' WHERE role = 'BACHELOR_4_Y';

-- Update any other records that don't match valid enum values to a default
UPDATE user_students
SET role = 'BACHELOR_DEGREE_1_Y'
WHERE role NOT IN (
    'MASTER_STUDENT_1_Y', 'MASTER_STUDENT_2_Y',
    'BACHELOR_DEGREE_1_Y', 'BACHELOR_DEGREE_2_Y', 'BACHELOR_DEGREE_3_Y', 'BACHELOR_DEGREE_4_Y', 'BACHELOR_DEGREE_5_Y',
    'NUFYP',
    'DOCTORAL_STUDENT_1_Y', 'DOCTORAL_STUDENT_2_Y', 'DOCTORAL_STUDENT_3_Y', 'DOCTORAL_STUDENT_4_Y', 'DOCTORAL_STUDENT_5_Y',
    'EXCHANGE_STUDENT', 'GUEST_STUDENT'
);

-- Re-create the constraint with the correct StudentRole enum values
ALTER TABLE user_students
ADD CONSTRAINT user_students_role_check 
CHECK (role IN (
    'MASTER_STUDENT_1_Y', 'MASTER_STUDENT_2_Y',
    'BACHELOR_DEGREE_1_Y', 'BACHELOR_DEGREE_2_Y', 'BACHELOR_DEGREE_3_Y', 'BACHELOR_DEGREE_4_Y', 'BACHELOR_DEGREE_5_Y',
    'NUFYP',
    'DOCTORAL_STUDENT_1_Y', 'DOCTORAL_STUDENT_2_Y', 'DOCTORAL_STUDENT_3_Y', 'DOCTORAL_STUDENT_4_Y', 'DOCTORAL_STUDENT_5_Y',
    'EXCHANGE_STUDENT', 'GUEST_STUDENT'
)); 