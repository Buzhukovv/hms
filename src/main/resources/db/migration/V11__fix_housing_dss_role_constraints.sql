-- Fix housing management role constraint
ALTER TABLE user_housing_management
DROP CONSTRAINT IF EXISTS user_housing_management_role_check;

ALTER TABLE user_housing_management
ADD CONSTRAINT user_housing_management_role_check 
CHECK (role IN ('MANAGER', 'BLOCK_MANAGER'));

-- Fix DSS role constraint
ALTER TABLE user_d_s_s
DROP CONSTRAINT IF EXISTS user_d_s_s_role_check;

ALTER TABLE user_d_s_s
ADD CONSTRAINT user_d_s_s_role_check 
CHECK (role IN ('DSS_MANAGER', 'DSS_ASSISTANT')); 