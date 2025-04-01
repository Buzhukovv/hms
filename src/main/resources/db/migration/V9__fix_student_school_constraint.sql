-- First, drop the existing constraint
ALTER TABLE user_students
DROP CONSTRAINT IF EXISTS user_students_school_check;

-- Re-create the constraint with all possible values from the SchoolsAndSpecialties enum
ALTER TABLE user_students
ADD CONSTRAINT user_students_school_check 
CHECK (school IN (
    'SCHOOL_OF_ENGINEERING_AND_DIGITAL_SCIENCES',
    'SCHOOL_OF_MEDICINE',
    'SCHOOL_OF_SCIENCES_AND_HUMANITIES',
    'GRADUATE_SCHOOL_OF_PUBLIC_POLICY',
    'GRADUATE_SCHOOL_OF_BUSINESS',
    'GRADUATE_SCHOOL_OF_EDUCATION'
)); 