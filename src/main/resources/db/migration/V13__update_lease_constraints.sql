-- Update leases table to have nullable check_out_date but non-null check_in_date
ALTER TABLE leases
ALTER COLUMN check_in_date SET NOT NULL,
ALTER COLUMN check_out_date DROP NOT NULL;

-- Set check_out_date equal to end_date for existing leases with null check_out_date
UPDATE leases
SET check_out_date = end_date
WHERE check_out_date IS NULL; 