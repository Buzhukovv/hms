# Property Management System - Frontend Requirements

## Overview

The Property Management System handles all aspects of housing properties available in the system, including dormitory rooms, campus apartments, off-campus apartments, and cottages. It provides functionality for managing property listings, availability, pricing, and assignments.

## User Roles

### Housing Management

- Create, update, and delete property listings
- Manage property status (available, occupied, maintenance, etc.)
- Set and update rental prices
- View all properties and their statuses
- Generate property reports

### Students/Teachers/Other Tenants

- Browse available properties
- Filter and search for properties based on preferences
- View property details and amenities
- Request property bookings

## Feature Requirements

### 1. Property Management Dashboard

**UI Components:**

- Property listing with filterable columns:
  - Property ID/Number
  - Type (Dormitory, Campus Apartment, etc.)
  - Status
  - Location/Building
  - Price
  - Size
  - Occupancy details
- Detailed view for each property
- Status indicators (available, occupied, maintenance)
- Map view of property locations (if applicable)

**For Housing Management:**

- Full CRUD capabilities
- Advanced filtering and sorting options
- Bulk actions for property management
- Status update tools

### 2. Property Type Management

#### 2.1 Dormitory Room Management

**UI Components:**

- Dormitory-specific fields:
  - Building
  - Floor
  - Room number
  - Bed count
  - Shared facilities
  - Gender restrictions

**Specific Features:**

- Room allocation tools
- Bed/space tracking within rooms
- Roommate matching interface

#### 2.2 Campus Apartment Management

**UI Components:**

- Apartment-specific fields:
  - Building
  - Unit number
  - Number of bedrooms/bathrooms
  - Furnished status
  - Included utilities

#### 2.3 Off-Campus Apartment Management

**UI Components:**

- Off-campus specific fields:
  - Address
  - Distance from campus
  - Transportation options
  - Neighborhood information
  - External landlord information

#### 2.4 Cottage Management

**UI Components:**

- Cottage-specific fields:
  - Location
  - Size
  - Number of bedrooms/bathrooms
  - Outdoor space details
  - Special amenities

### 3. Property Search and Filtering

**UI Components:**

- Advanced search form with filters for:
  - Property type
  - Price range
  - Location
  - Availability
  - Amenities
  - Size
  - Occupancy limit
- Sort options (price, availability date, etc.)
- Map-based search interface
- Saved search functionality

**Workflow:**

1. User selects search criteria
2. System displays matching properties
3. User can refine search or view property details
4. User can save search criteria for future use

### 4. Property Status Management

**UI Components:**

- Status update form
- Maintenance scheduling tools
- Availability calendar
- Status history log

**Workflow:**

1. Housing Management selects property
2. Views current status and history
3. Updates status with effective date
4. Adds notes if needed
5. System updates property status

## API Integration

The frontend should integrate with the following backend endpoints:

### Dormitory Room APIs

```http
POST /api/dormitory-rooms
PUT /api/dormitory-rooms/{id}
DELETE /api/dormitory-rooms/{id}
GET /api/dormitory-rooms/{id}
GET /api/dormitory-rooms
GET /api/dormitory-rooms/status?status={status}
GET /api/dormitory-rooms/search?q={keyword}
GET /api/dormitory-rooms/available
PUT /api/dormitory-rooms/{id}/status?status={status}
GET /api/dormitory-rooms/price-range?min={minPrice}&max={maxPrice}
```

### Campus Apartment APIs

```http
POST /api/campus-apartments
PUT /api/campus-apartments/{id}
DELETE /api/campus-apartments/{id}
GET /api/campus-apartments/{id}
GET /api/campus-apartments
GET /api/campus-apartments/status?status={status}
GET /api/campus-apartments/search?q={keyword}
GET /api/campus-apartments/available
PUT /api/campus-apartments/{id}/status?status={status}
GET /api/campus-apartments/price-range?min={minPrice}&max={maxPrice}
```

### Off-Campus Apartment APIs

```http
POST /api/off-campus-apartments
PUT /api/off-campus-apartments/{id}
DELETE /api/off-campus-apartments/{id}
GET /api/off-campus-apartments/{id}
GET /api/off-campus-apartments
GET /api/off-campus-apartments/status?status={status}
GET /api/off-campus-apartments/search?q={keyword}
GET /api/off-campus-apartments/available
PUT /api/off-campus-apartments/{id}/status?status={status}
GET /api/off-campus-apartments/price-range?min={minPrice}&max={maxPrice}
```

### Cottage APIs

```http
POST /api/cottages
PUT /api/cottages/{id}
DELETE /api/cottages/{id}
GET /api/cottages/{id}
GET /api/cottages
GET /api/cottages/status?status={status}
GET /api/cottages/search?q={keyword}
GET /api/cottages/available
PUT /api/cottages/{id}/status?status={status}
GET /api/cottages/price-range?min={minPrice}&max={maxPrice}
```

## Technical Requirements

1. Responsive design for all screens (desktop, tablet, mobile)
2. Interactive maps for property locations
3. Image galleries for property photos
4. Virtual tour capability (if possible)
5. Form validation for all inputs
6. Error handling and appropriate user feedback
7. Advanced filtering and search capabilities
8. Proper loading states and pagination for property lists
9. Role-based access control

## Design Guidelines

1. Use the existing design system and component library
2. Maintain consistent branding and color scheme
3. Clear status indicators using colors and icons
4. Intuitive navigation between property sections
5. Rich media display for property details (photos, floor plans)
6. Consistent property card design across different property types
7. Map-based UI elements for location-based features

## Implementation Phases

### Phase 1

- Basic property management (CRUD operations)
- Property listing and details view
- Simple property creation forms

### Phase 2

- Advanced filtering and search
- Property status management
- Map integration

### Phase 3

- Virtual tours
- Advanced reporting
- Integration with lease management system
- Roommate matching features
- Saved searches and favorites

## Testing Requirements

1. Unit tests for all components
2. Integration tests for property workflows
3. End-to-end tests for complete user journeys
4. Accessibility testing
5. Cross-browser testing
6. Performance testing for search and filter operations
