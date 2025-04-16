package housingManagment.hms.dto;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.MaintenanceRequest;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.userEntity.BaseUser;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@Data
public class DashboardData {
    // For Students and Teachers
    private BaseUser profile;
    private Lease currentLease;
    private List<Lease> activeLeases; // For showing all concurrent leases
    private BaseProperty currentProperty;
    private List<Lease> historicalLeases;
    private List<MaintenanceRequest> maintenanceRequests;
    private Double upcomingPayment;

    // For Maintenance Roles
    private List<MaintenanceRequest> allMaintenanceRequests;
    private List<MaintenanceRequest> assignedMaintenanceRequests;
    private Map<String, Long> maintenanceStats;

    // For HousingManagement and DSS Roles (Managers)
    private long userCount;
    private long propertyCount;
    private long leaseCount;
    private long maintenanceRequestCount;
    private Map<String, Long> userTypeCounts;
    private Map<String, Long> propertyTypeCounts;
    private Map<String, Long> maintenanceStatusCounts;
    private long vacantRooms;
    private long partiallyOccupiedRooms;
    private long occupiedRooms;
    private long maintenanceRooms;
    private long reservedRooms;

    // For HousingManagement (Block Managers)
    private List<BaseProperty> blockProperties;
    private List<Lease> blockActiveLeases;
    private List<MaintenanceRequest> blockMaintenanceRequests;
    private Map<String, Long> blockPropertyStats;
    private Map<String, Long> blockLeaseStats;
    private Map<String, Long> blockMaintenanceStats;

    // For DSS Assistants (non-block-based, student-focused)
    private List<Lease> studentActiveLeases;
    private List<MaintenanceRequest> studentMaintenanceRequests;
    private Map<String, Long> studentRoleCounts;
    private Map<String, Long> studentPropertyTypeCounts;

    // Role-specific filters
    private String block;

    @JsonProperty("is_manager")
    private Boolean isManager; // Ensure this field is present
}