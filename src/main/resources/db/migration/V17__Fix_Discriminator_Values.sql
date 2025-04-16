-- Fix discriminator values in property_base table
-- This ensures the values match the @DiscriminatorValue annotations in entity classes

-- Update discriminator value for existing OffCampusApartment entities
UPDATE property_base 
SET property_type = 'Off_Campus' 
WHERE id IN (SELECT id FROM property_off_campus_apartments);

-- Update discriminator value for existing Cottage entities
UPDATE property_base 
SET property_type = 'Cottage' 
WHERE id IN (SELECT id FROM property_cottages);

-- Update discriminator value for existing DormitoryRoom entities
UPDATE property_base 
SET property_type = 'DormitoryRoom' 
WHERE id IN (SELECT id FROM property_dormitory_rooms);

-- Update discriminator value for existing CampusApartment entities
UPDATE property_base 
SET property_type = 'CampusApartment' 
WHERE id IN (SELECT id FROM property_campus_apartments);

-- Update discriminator value for existing Townhouse entities
UPDATE property_base 
SET property_type = 'Townhouse' 
WHERE id IN (SELECT id FROM property_townhouses); 