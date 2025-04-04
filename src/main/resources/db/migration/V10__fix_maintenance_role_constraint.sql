-- First, drop the existing constraint
ALTER TABLE user_maintenance
DROP CONSTRAINT IF EXISTS user_maintenance_role_check;

-- Re-create the constraint with all possible values from the MaintenanceRole enum
ALTER TABLE user_maintenance
ADD CONSTRAINT user_maintenance_role_check 
CHECK (role IN ('MAINTENANCE_DISPATCHER', 'MAINTENANCE_ENGINEER', 'MAINTENANCE_STAFF')); 