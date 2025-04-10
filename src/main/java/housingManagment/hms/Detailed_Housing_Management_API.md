# 🏠 Housing Management Service – API Documentation

Welcome to the comprehensive API guide for the Housing Management Service. This document outlines every available endpoint, grouped by user roles and entities, and integrates **Role-Based Access Control (RBAC)** details. Additionally, it includes **suggested endpoints** for future report generation features to empower your system with insightful analytics.

---

## 📜 Table of Contents

- [Users](#users)
  - [Base User](#base-user)
  - [Student](#student)
  - [Teacher](#teacher)
  - [Family Member](#family-member)
  - [Housing Manager](#housing-manager)
  - [DSS](#dss)
  - [Maintenance](#maintenance)
- [Properties](#properties)
- [Future Enhancements](#future-enhancements)

---

## 👤 Users

### Base User

**RBAC:** Accessible by `HousingManager:MANAGER`, `DSS:MANAGER`

- `POST /api/users/base/create` – Create a new user
- `GET /api/users/base/{id}` – Retrieve user by ID
- `PUT /api/users/base/{id}` – Update user details
- `DELETE /api/users/base/{id}` – Delete a user

**Search and Filter Endpoints**
- `GET /api/users/base/by-nuid`
- `GET /api/users/base/by-email`
- `GET /api/users/base/by-name`
- `GET /api/users/base/by-national-id`
- `GET /api/users/base/by-identity-doc`

**Change Attribute Endpoints** [SELF]
- `PATCH /api/users/base/change-nuid`
- `PATCH /api/users/base/change-name`
- `PATCH /api/users/base/change-email`
- `PATCH /api/users/base/change-password`

**Stats**
- `GET /api/users/base/count`
- `GET /api/users/base/count-by-type`

**Get Specific Info**
- `GET /api/users/base/{id}/nuid`
- `GET /api/users/base/{id}/identity-doc`

---

### Student

**RBAC:** `HousingManager:MANAGER`, `DSS:MANAGER`

- CRUD: Create, Read, Update, Delete
- Filters: by `studentRole`, `school`, `specialty`
- Attribute Changes: `changeRole`, `changeSchool`
- Counts: by role, school, specialty

**Additional Access:**
- `GET /api/students/{id}/roommates` – View current roommates
- `GET /api/students/{id}/ex-roommates` – View historical roommates

---

### Teacher

**RBAC:** `HousingManager:MANAGER`

- Full CRUD
- Filters: by `teacherPosition`, `school`
- Attribute Changes and Counts

**Additional Access:**
- `GET /api/teachers/{id}/family-members` – View their own registered family members

---

### Family Member

**RBAC:** Managed by associated BaseUser's manager

- CRUD operations
- Get by `Relation`, `MainUser`
- Relation updates and count per user

---

### Housing Manager

**RBAC:** Managed by Admins

- Manage housing staff, roles, and block assignments
- Counts and filtering by `HMRole`, `Block`

---

### DSS (Dormitory Support Staff)

**RBAC:** Managed by HousingManager

- Full lifecycle management
- Filtering and stats by `DSSRole`, `Block`

---

### Maintenance

**RBAC:** `HousingManager`, `DSS`

- Register and manage maintenance workers
- Manage role changes and view role-based counts

**Additional Access:**
- `GET /api/maintenance-requests/{id}/assigned-staff` – View assigned staff info
- `GET /api/maintenance-requests/{id}/status` – Track status
- `GET /api/maintenance-requests/{id}/metadata` – View issue logs and metadata

---

## 🏨 Properties

### Base Property

**RBAC:** `HousingManager`, `DSS:MANAGER`

- `POST /api/properties/create`
- `GET /api/properties/{id}`
- `PUT /api/properties/{id}`
- Future: Associate property with lease or maintenance tickets

**Additional Access:**
- `GET /api/properties/{id}/ex-tenants` – Retrieve historical tenant list
- `GET /api/properties/{id}/maintenance-history` – View historical maintenance logs

---

## 📊 Future Enhancements: Suggested Endpoints

### Reports & Analytics (RBAC: Admins only)

- `GET /api/reports/user-distribution` – View user counts by type, age, and gender
- `GET /api/reports/property-occupancy` – Analyze occupancy trends per block/school
- `GET /api/reports/maintenance-activity` – Maintenance requests by time and status
- `GET /api/reports/student-accommodation` – Breakdown of students per property
- `GET /api/reports/teacher-distribution` – Teachers per block/school
- `GET /api/reports/block-utilization` – Property usage heatmap per academic year

---

## 🔐 Role-Based Access Control (RBAC)

| Role                  | Permission Scope                         |
|-----------------------|------------------------------------------|
| HousingManager:MANAGER | Full CRUD for users and properties       |
| DSS:MANAGER           | Limited CRUD, property + lease creation |
| Maintenance           | Update own tickets, status only         |
| Admin                 | Full system control & analytics access   |

---

Let's collaborate on refining each endpoint's response model, error codes, and additional utility APIs!

