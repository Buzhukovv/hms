-- Modify check_out_date column to allow NULL values
ALTER TABLE leases
ALTER COLUMN check_out_date DROP NOT NULL; 