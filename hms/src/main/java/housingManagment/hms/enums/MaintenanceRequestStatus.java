package housingManagment.hms.enums;

public enum MaintenanceRequestStatus {
    PENDING, // Initial state when request is submitted
    APPROVED, // Request has been approved but waiting for payment
    PAID, // Payment completed, work can begin
    IN_PROGRESS, // Maintenance work has begun
    COMPLETED, // Maintenance work is finished
    CANCELLED, // Request was cancelled
    REJECTED // Request was rejected
}