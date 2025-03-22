# Maintenance Request System - Frontend Requirements

## Overview

The Maintenance Request system allows residents to submit maintenance requests for their leased properties. The system enforces a payment flow before maintenance work begins and tracks the status of each request through completion.

## User Roles

### Tenant

- Submit maintenance requests
- View their submitted requests
- Pay for maintenance services
- Track request status

### Maintenance Staff

- View assigned maintenance requests
- Update request status
- Add notes to requests
- Mark requests as completed

### Housing Management

- Approve or reject maintenance requests
- Assign maintenance staff to requests
- View all maintenance requests
- Generate reports on maintenance activities

## Feature Requirements

### 1. Maintenance Request Submission

**UI Components:**

- Form with the following fields:
  - Title (required)
  - Description of the issue (required)
  - Optional notes
- The tenant's active lease should be automatically identified and associated with the request

**Workflow:**

1. Tenant navigates to "Submit Maintenance Request"
2. System pre-fills the tenant information based on authentication
3. System displays a dropdown of the tenant's active leases
4. Tenant fills out the request details
5. System generates a unique request number and confirms submission

### 2. Payment Processing

**UI Components:**

- Payment status indicator
- Payment action button
- Service charge display

**Workflow:**

1. Once a request is approved, tenant is notified
2. Tenant navigates to the request details
3. System displays the service charge (if applicable)
4. Tenant clicks "Process Payment"
5. After successful payment, the status changes to "PAID"

### 3. Request Tracking Dashboard

**UI Components:**

- Request list with filterable columns:
  - Request number
  - Title
  - Status
  - Date submitted
  - Property
  - Assigned to
- Detailed view for each request
- Status timeline visualization

**For Tenants:**

- Show only their own requests
- Filter by status

**For Maintenance Staff:**

- Show assigned requests
- Filter by property, status, date range
- Option to update status
- Add notes to requests

**For Housing Management:**

- Show all requests
- Advanced filtering and sorting options
- Ability to assign staff
- Approval functionality

### 4. Status Management

**UI Components:**

- Status update dropdown
- Confirmation dialogs for status changes
- Notes field for status updates

**Status Flow:**

1. PENDING (initial state)
2. APPROVED (waiting for payment)
3. PAID (payment completed)
4. IN_PROGRESS (work has begun)
5. COMPLETED (work is finished)
6. CANCELLED/REJECTED (if applicable)

**Rules:**

- Only Housing Management / Maintenance Staff can approve/reject requests
- Maintenance work cannot begin until payment is processed
- Only Maintenance Staff can mark a request as "IN_PROGRESS" or "COMPLETED"
- A completion timestamp is recorded when marking a request as "COMPLETED"

## API Integration

The frontend should integrate with the following backend endpoints:

### Request Creation

```http
POST /api/maintenance-requests
```

### Request Retrieval

```http
GET /api/maintenance-requests
GET /api/maintenance-requests/{id}
GET /api/maintenance-requests/number/{requestNumber}
GET /api/maintenance-requests/user/{userId}
GET /api/maintenance-requests/lease/{leaseId}
GET /api/maintenance-requests/assigned-to/{staffId}
GET /api/maintenance-requests/status/{status}
```

### Request Updates

```http
PUT /api/maintenance-requests/{id}
PUT /api/maintenance-requests/{requestId}/assign/{staffId}
PUT /api/maintenance-requests/{requestId}/status/{status}
PUT /api/maintenance-requests/{requestId}/mark-paid
PUT /api/maintenance-requests/{requestId}/mark-completed
```

### Request Deletion

```http
DELETE /api/maintenance-requests/{id}
```

## Technical Requirements

1. Responsive design for all screens (desktop, tablet, mobile)
2. Real-time status updates using websockets (if possible)
3. Form validation for all inputs
4. Error handling and appropriate user feedback
5. Notifications for status changes
6. Optimistic UI updates with proper error recovery
7. Accessibility compliance (WCAG 2.1)
8. Proper loading states and pagination for request lists

## Design Guidelines

1. Use the existing design system and component library
2. Maintain consistent branding and color scheme
3. Ensure clear status indicators using colors and icons
4. Provide intuitive navigation between requests and actions
5. Design for easy mobile access for maintenance staff in the field

## Implementation Phases

### Phase 1

- Basic request submission
- Request listing and details view
- Simple status updates

### Phase 2

- Payment integration
- Advanced filtering and sorting
- Maintenance staff assignment

### Phase 3

- Reporting and analytics
- Notifications system
- Mobile optimizations

## Testing Requirements

1. Unit tests for all components
2. Integration tests for form submissions and status flows
3. End-to-end tests for complete user journeys
4. Accessibility testing
5. Cross-browser testing
