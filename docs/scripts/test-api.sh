#!/bin/bash

# Set base URL
BASE_URL="http://localhost:8080/api"

echo "Testing HMS API endpoints"
echo "========================="

# Check if server is running
echo "Testing server connection..."
if ! curl -s -o /dev/null -w "%{http_code}" "$BASE_URL" | grep -q "200\|404"; then
    echo "Error: Server is not running. Please start the application first."
    exit 1
fi
echo "Server is running!"
echo 

# Get all students
echo "Fetching all students..."
curl -s "$BASE_URL/students" | json_pp
echo
echo

# Get all dormitory rooms
echo "Fetching all dormitory rooms..."
curl -s "$BASE_URL/dormitory-rooms" | json_pp
echo
echo

# Get all leases
echo "Fetching all leases..."
curl -s "$BASE_URL/leases" | json_pp
echo
echo

# Get all maintenance requests
echo "Fetching all maintenance requests..."
curl -s "$BASE_URL/maintenance-requests" | json_pp
echo
echo

# Create a new maintenance request (assuming we have IDs)
echo "Creating a new maintenance request..."
echo "Note: This requires existing IDs. Replace placeholder values with actual IDs."
echo "Skipping for now. Uncomment and modify with real IDs to test."

# Uncomment and update with real IDs to test
# STUDENT_ID="replace-with-actual-id"
# LEASE_ID="replace-with-actual-id"
# curl -s -X POST "$BASE_URL/maintenance-requests" \
#     -H "Content-Type: application/json" \
#     -d '{
#         "title": "Broken Window",
#         "description": "The window in my room is not closing properly",
#         "requesterId": "'$STUDENT_ID'",
#         "leaseId": "'$LEASE_ID'",
#         "serviceCharge": 75.0,
#         "notes": "Please fix as soon as possible, it gets cold at night"
#     }' | json_pp
# echo

echo "API testing complete!" 