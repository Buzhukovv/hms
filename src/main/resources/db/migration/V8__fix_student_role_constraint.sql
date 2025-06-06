-- First, drop the existing constraint
ALTER TABLE user_students
DROP CONSTRAINT IF EXISTS user_students_role_check;

-- First, update any existing rows that might have incorrect role values
UPDATE user_students
SET role = 'MASTER_STUDENT_1_Y'
WHERE role NOT IN (
    'MASTER_STUDENT_1_Y', 'MASTER_STUDENT_2_Y',
    'BACHELOR_DEGREE_1_Y', 'BACHELOR_DEGREE_2_Y', 'BACHELOR_DEGREE_3_Y', 'BACHELOR_DEGREE_4_Y', 'BACHELOR_DEGREE_5_Y',
    'NUFYP',
    'DOCTORAL_STUDENT_1_Y', 'DOCTORAL_STUDENT_2_Y', 'DOCTORAL_STUDENT_3_Y', 'DOCTORAL_STUDENT_4_Y', 'DOCTORAL_STUDENT_5_Y',
    'EXCHANGE_STUDENT', 'GUEST_STUDENT'
);

-- Re-create the constraint with all possible values from the StudentRole enum
ALTER TABLE user_students
ADD CONSTRAINT user_students_role_check 
CHECK (role IN (
    'MASTER_STUDENT_1_Y', 'MASTER_STUDENT_2_Y',
    'BACHELOR_DEGREE_1_Y', 'BACHELOR_DEGREE_2_Y', 'BACHELOR_DEGREE_3_Y', 'BACHELOR_DEGREE_4_Y', 'BACHELOR_DEGREE_5_Y',
    'NUFYP',
    'DOCTORAL_STUDENT_1_Y', 'DOCTORAL_STUDENT_2_Y', 'DOCTORAL_STUDENT_3_Y', 'DOCTORAL_STUDENT_4_Y', 'DOCTORAL_STUDENT_5_Y',
    'EXCHANGE_STUDENT', 'GUEST_STUDENT'
)); 