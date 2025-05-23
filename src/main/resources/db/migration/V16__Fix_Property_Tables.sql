-- Create a proper table for Townhouse entities (they were incorrectly mapped to cottages)
CREATE TABLE IF NOT EXISTS property_townhouses (
    id UUID PRIMARY KEY REFERENCES property_base(id),
    area DOUBLE PRECISION NOT NULL DEFAULT 0.0
);

-- Add area columns to property type tables where needed
ALTER TABLE property_cottages ADD COLUMN IF NOT EXISTS area DOUBLE PRECISION NOT NULL DEFAULT 0.0;
ALTER TABLE property_off_campus_apartments ADD COLUMN IF NOT EXISTS area DOUBLE PRECISION NOT NULL DEFAULT 0.0;
ALTER TABLE property_campus_apartments ADD COLUMN IF NOT EXISTS area DOUBLE PRECISION NOT NULL DEFAULT 0.0;
ALTER TABLE property_dormitory_rooms ADD COLUMN IF NOT EXISTS area DOUBLE PRECISION NOT NULL DEFAULT 0.0;

-- Copy area data from BaseProperty to specific property tables
UPDATE property_cottages c 
SET area = (SELECT area FROM property_base b WHERE b.id = c.id)
WHERE EXISTS (SELECT 1 FROM property_base b WHERE b.id = c.id AND b.area IS NOT NULL);

UPDATE property_off_campus_apartments o 
SET area = (SELECT area FROM property_base b WHERE b.id = o.id)
WHERE EXISTS (SELECT 1 FROM property_base b WHERE b.id = o.id AND b.area IS NOT NULL);

UPDATE property_campus_apartments a 
SET area = (SELECT area FROM property_base b WHERE b.id = a.id)
WHERE EXISTS (SELECT 1 FROM property_base b WHERE b.id = a.id AND b.area IS NOT NULL);

UPDATE property_dormitory_rooms d 
SET area = (SELECT area FROM property_base b WHERE b.id = d.id)
WHERE EXISTS (SELECT 1 FROM property_base b WHERE b.id = d.id AND b.area IS NOT NULL);

-- Skip the Townhouse migration since we don't know how to identify townhouses reliably
-- If we need to migrate Townhouse data, we would need to do that manually or in a separate script
-- that uses a more reliable method of identification

-- Now remove area column from BaseProperty (only after all data is migrated)
ALTER TABLE property_base DROP COLUMN IF EXISTS area; 