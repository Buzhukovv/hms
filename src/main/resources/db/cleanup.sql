-- Disable foreign key constraints
SET session_replication_role = 'replica';

-- Truncate all tables
TRUNCATE TABLE 
    leases,
    maintenance_requests,
    scheduled_reports,
    saved_reports,
    user_base
CASCADE;

-- Re-enable foreign key constraints
SET session_replication_role = 'origin';

-- Verify tables are empty
SELECT COUNT(*) FROM leases;
SELECT COUNT(*) FROM maintenance_requests;
SELECT COUNT(*) FROM scheduled_reports;
SELECT COUNT(*) FROM saved_reports;
SELECT COUNT(*) FROM user_base; 