-- Add indexes for common search fields

-- Maintenance requests indexes
CREATE INDEX IF NOT EXISTS idx_maintenance_requests_status ON maintenance_requests(status);
CREATE INDEX IF NOT EXISTS idx_maintenance_requests_requester_id ON maintenance_requests(requester_id);
CREATE INDEX IF NOT EXISTS idx_maintenance_requests_lease_id ON maintenance_requests(lease_id);
CREATE INDEX IF NOT EXISTS idx_maintenance_requests_assigned_to ON maintenance_requests(assigned_to);
CREATE INDEX IF NOT EXISTS idx_maintenance_requests_is_paid ON maintenance_requests(is_paid);
CREATE INDEX IF NOT EXISTS idx_maintenance_requests_created_at ON maintenance_requests(created_at);
CREATE INDEX IF NOT EXISTS idx_maintenance_requests_completed_at ON maintenance_requests(completed_at);

-- Lease indexes
CREATE INDEX IF NOT EXISTS idx_leases_tenant_id ON leases(tenant_id);
CREATE INDEX IF NOT EXISTS idx_leases_property_id ON leases(property_id);
CREATE INDEX IF NOT EXISTS idx_leases_status ON leases(status);
CREATE INDEX IF NOT EXISTS idx_leases_start_date ON leases(start_date);
CREATE INDEX IF NOT EXISTS idx_leases_end_date ON leases(end_date);
CREATE INDEX IF NOT EXISTS idx_leases_check_in_date ON leases(check_in_date);
CREATE INDEX IF NOT EXISTS idx_leases_check_out_date ON leases(check_out_date);

-- Property indexes
CREATE INDEX IF NOT EXISTS idx_property_base_property_number ON property_base(property_number);
CREATE INDEX IF NOT EXISTS idx_property_base_property_block ON property_base(property_block);
CREATE INDEX IF NOT EXISTS idx_property_base_status ON property_base(status);

-- User indexes
CREATE INDEX IF NOT EXISTS idx_user_base_email ON user_base(email);
CREATE INDEX IF NOT EXISTS idx_user_base_role ON user_base(role);
CREATE INDEX IF NOT EXISTS idx_user_base_last_name ON user_base(last_name); 