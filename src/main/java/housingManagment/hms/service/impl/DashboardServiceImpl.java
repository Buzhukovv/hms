package housingManagment.hms.service.impl;

import housingManagment.hms.dto.DashboardData;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.MaintenanceRequest;
import housingManagment.hms.entities.property.*;
import housingManagment.hms.entities.userEntity.*;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.enums.userEnum.*;
import housingManagment.hms.repository.LeaseRepository;
import housingManagment.hms.repository.MaintenanceRequestRepository;
import housingManagment.hms.repository.userRepository.UserRepository;
import housingManagment.hms.repository.propertyRepository.PropertyRepository;
import housingManagment.hms.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository; // Lowercase to match field name

    @Autowired
    private LeaseRepository leaseRepository;

    @Autowired
    private PropertyRepository propertyRepository; // Lowercase to match field name

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    @Override
    public DashboardData getDashboardData(BaseUser user) {
        DashboardData dashboardData = new DashboardData();

        // Common data for all roles
        dashboardData.setProfile(user);

        // Role-based data
        if (user instanceof Student || user instanceof Teacher) {
            populateStudentOrTeacherDashboard(user, dashboardData);
        } else if (user instanceof Maintenance maintenance) {
            MaintenanceRole role = maintenance.getRole();
            if (role == MaintenanceRole.MAINTENANCE_DISPATCHER || role == MaintenanceRole.MAINTENANCE_ENGINEER) {
                populateMaintenanceDispatcherOrEngineerDashboard(dashboardData);
            } else if (role == MaintenanceRole.MAINTENANCE_STAFF) {
                populateMaintenanceStaffDashboard(maintenance, dashboardData);
            }
        } else if (user instanceof HousingManagement housingManagment) {
            HousingManagementRole role = housingManagment.getRole();
            if (role == HousingManagementRole.MANAGER) {
                populateManagerDashboard(dashboardData);
            } else if (role == HousingManagementRole.BLOCK_MANAGER) {
                populateBlockManagerDashboard(housingManagment.getBlock(), dashboardData);
            }
        } else if (user instanceof DSS dss) {
            DepartmentOfStudentServicesRole role = dss.getRole();
            if (role == DepartmentOfStudentServicesRole.DSS_MANAGER) {
                populateManagerDashboard(dashboardData);
            } else if (role == DepartmentOfStudentServicesRole.DSS_ASSISTANT) {
                populateDSSAssistantDashboard(dashboardData);
            }
        }
        List<Lease> activeLeases = leaseRepository.findByTenant(user);
        dashboardData.setActiveLeases(activeLeases != null ? activeLeases : List.of());

        return dashboardData;
    }

    private void populateStudentOrTeacherDashboard(BaseUser user, DashboardData dashboardData) {
        Lease currentLease = null;
        List<Lease> historicalLeases = null;

        // Fetch leases based on user type (Student or Teacher)
        if (user instanceof Student) {
            // Students can only be in DormitoryRoom or CampusApartment
            currentLease = Optional.ofNullable(dashboardData.getActiveLeases())
                    .orElse(List.of())
                    .stream()
                    .findFirst()
                    .orElse(null);
            historicalLeases = leaseRepository.findHistoricalLeasesForStudent(user);
        } else if (user instanceof Teacher) {
            // Teachers can be in any property except DormitoryRoom
            currentLease = leaseRepository.findActiveLeaseForTeacher(user);
            historicalLeases = leaseRepository.findHistoricalLeasesForTeacher(user);
        }

        // Set current lease and property if a valid lease exists
        if (currentLease != null) {
            dashboardData.setCurrentLease(currentLease);
            dashboardData.setCurrentProperty(currentLease.getProperty());
        }

        // Set historical leases (filtered by property type)
        if (historicalLeases != null) {
            dashboardData.setHistoricalLeases(historicalLeases);
        } else {
            dashboardData.setHistoricalLeases(List.of()); // Empty list if no historical leases
        }

        // Maintenance Requests
        List<MaintenanceRequest> maintenanceRequests = maintenanceRequestRepository.findByRequester(user);
        dashboardData.setMaintenanceRequests(maintenanceRequests);

        // Upcoming Payment
        if (currentLease != null && !currentLease.getProperty().getIsPaid()) {
            dashboardData.setUpcomingPayment(currentLease.getMonthlyRent());
        } else {
            dashboardData.setUpcomingPayment(0.0); // Default to 0 if no valid lease or already paid
        }
    }

    private void populateMaintenanceDispatcherOrEngineerDashboard(DashboardData dashboardData) {
        // All Maintenance Requests
        List<MaintenanceRequest> allRequests = maintenanceRequestRepository.findAll();
        dashboardData.setAllMaintenanceRequests(allRequests);

        // Maintenance Stats
        Map<String, Long> maintenanceStats = allRequests.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getStatus().name(),
                        Collectors.counting()
                ));
        dashboardData.setMaintenanceStats(maintenanceStats);
    }

    private void populateMaintenanceStaffDashboard(Maintenance user, DashboardData dashboardData) {
        // Assigned Maintenance Requests
        List<MaintenanceRequest> assignedRequests = maintenanceRequestRepository.findByAssignedTo(user);
        dashboardData.setAssignedMaintenanceRequests(assignedRequests);
    }

    private void populateManagerDashboard(DashboardData dashboardData) {
        // Metrics
        dashboardData.setUserCount(userRepository.count());
        dashboardData.setPropertyCount(propertyRepository.count());
        dashboardData.setLeaseCount(leaseRepository.count());
        dashboardData.setMaintenanceRequestCount(maintenanceRequestRepository.count());

        // Detailed Statistics for User Types
        Map<String, Long> userTypeCounts = new HashMap<>();
        userTypeCounts.put("Students", userRepository.countByType(Student.class));
        userTypeCounts.put("Teachers", userRepository.countByType(Teacher.class));
        userTypeCounts.put("Maintenance", userRepository.countByType(Maintenance.class));
        userTypeCounts.put("HousingManagement", userRepository.countByType(HousingManagement.class));
        userTypeCounts.put("FamilyMember", userRepository.countByType(FamilyMember.class));
        userTypeCounts.put("DSS", userRepository.countByType(DSS.class));
        dashboardData.setUserTypeCounts(userTypeCounts);

        // Detailed Statistics for Property Types
        Map<String, Long> propertyTypeCounts = new HashMap<>();
        propertyTypeCounts.put("CampusApartment", propertyRepository.countByType(CampusApartment.class));
        propertyTypeCounts.put("Cottage", propertyRepository.countByType(Cottage.class));
        propertyTypeCounts.put("DormitoryRoom", propertyRepository.countByType(DormitoryRoom.class));
        propertyTypeCounts.put("OffCampusApartment", propertyRepository.countByType(OffCampusApartment.class));
        propertyTypeCounts.put("Townhouse", propertyRepository.countByType(Townhouse.class));
        dashboardData.setPropertyTypeCounts(propertyTypeCounts);

        // Maintenance Status Counts
        Map<String, Long> maintenanceStatusCounts = maintenanceRequestRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        request -> request.getStatus().name(),
                        Collectors.counting()
                ));
        dashboardData.setMaintenanceStatusCounts(maintenanceStatusCounts);

        // Room Statuses
        dashboardData.setVacantRooms(propertyRepository.countByStatus(PropertyStatus.VACANT));
        dashboardData.setOccupiedRooms(propertyRepository.countByStatus(PropertyStatus.OCCUPIED));
        // Fix: Use the correct field for partially occupied rooms (already calculated below)
        dashboardData.setMaintenanceRooms(propertyRepository.countByStatus(PropertyStatus.OUT_OF_SERVICE));
        dashboardData.setReservedRooms(propertyRepository.countByStatus(PropertyStatus.RESERVED));

        // Partially Occupied Rooms (requires custom logic)
        long partiallyOccupiedRooms = propertyRepository.findAll().stream()
                .filter(property -> {
                    long occupantCount = leaseRepository.countActiveLeasesByPropertyId(property.getId(), LeaseStatus.ACTIVE);
                    return occupantCount > 0 && occupantCount < property.getMaxOccupant();
                })
                .count();
        dashboardData.setPartiallyOccupiedRooms(partiallyOccupiedRooms);

        dashboardData.setIsManager(true);
    }

    private void populateBlockManagerDashboard(String block, DashboardData dashboardData) {
        // Block-specific Properties
        List<BaseProperty> blockProperties = propertyRepository.findByPropertyBlock(block);
        dashboardData.setBlockProperties(blockProperties);

        // Block-specific Property Stats
        Map<String, Long> blockPropertyStats = blockProperties.stream()
                .collect(Collectors.groupingBy(
                        property -> property.getStatus().name(),
                        Collectors.counting()
                ));
        dashboardData.setBlockPropertyStats(blockPropertyStats);

        // Block-specific Leases
        List<Lease> blockLeases = leaseRepository.findByPropertyBlock(block);
        List<Lease> blockActiveLeases = blockLeases.stream()
                .filter(lease -> lease.getStatus() == LeaseStatus.ACTIVE)
                .collect(Collectors.toList());
        dashboardData.setBlockActiveLeases(blockActiveLeases);

        // Block-specific Lease Stats
        Map<String, Long> blockLeaseStats = blockLeases.stream()
                .collect(Collectors.groupingBy(
                        lease -> lease.getStatus().name(),
                        Collectors.counting()
                ));
        dashboardData.setBlockLeaseStats(blockLeaseStats);

        // Block-specific Maintenance Requests
        List<MaintenanceRequest> blockRequests = maintenanceRequestRepository.findByPropertyBlock(block);
        dashboardData.setBlockMaintenanceRequests(blockRequests);

        // Block-specific Maintenance Stats
        Map<String, Long> blockMaintenanceStats = blockRequests.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getStatus().name(),
                        Collectors.counting()
                ));
        dashboardData.setBlockMaintenanceStats(blockMaintenanceStats);

        dashboardData.setBlock(block);
        dashboardData.setIsManager(false);
    }

    private void populateDSSAssistantDashboard(DashboardData dashboardData) {
        // Student-specific Metrics
        dashboardData.setUserCount(userRepository.countByType(Student.class)); // Fix: Count only students

        // Student Role Distribution
        List<? extends BaseUser> studentList = userRepository.findAllByType(Student.class);
        List<Student> students = studentList.stream()
                .map(user -> (Student) user)
                .toList();
        Map<String, Long> studentRoleCounts = students.stream()
                .collect(Collectors.groupingBy(
                        student -> student.getRole().name(),
                        Collectors.counting()
                ));
        dashboardData.setStudentRoleCounts(studentRoleCounts);

        // Active Leases for Students
        List<Lease> studentLeases = leaseRepository.findAll().stream()
                .filter(lease -> lease.getTenant() instanceof Student)
                .toList();
        List<Lease> studentActiveLeases = studentLeases.stream()
                .filter(lease -> lease.getStatus() == LeaseStatus.ACTIVE)
                .collect(Collectors.toList());
        dashboardData.setStudentActiveLeases(studentActiveLeases);

        // Maintenance Requests by Students
        List<MaintenanceRequest> studentRequests = maintenanceRequestRepository.findAll().stream()
                .filter(request -> request.getRequester() instanceof Student)
                .collect(Collectors.toList());
        dashboardData.setStudentMaintenanceRequests(studentRequests);

        // Student Housing Distribution by Property Type
        Map<String, Long> studentPropertyTypeCounts = studentActiveLeases.stream()
                .collect(Collectors.groupingBy(
                        lease -> {
                            BaseProperty property = lease.getProperty();
                            if (property instanceof DormitoryRoom) return "DormitoryRoom";
                            if (property instanceof CampusApartment) return "CampusApartment";
                            if (property instanceof OffCampusApartment) return "OffCampusApartment";
                            if (property instanceof Cottage) return "Cottage";
                            if (property instanceof Townhouse) return "Townhouse";
                            return "Unknown";
                        },
                        Collectors.counting()
                ));
        dashboardData.setStudentPropertyTypeCounts(studentPropertyTypeCounts);

        dashboardData.setIsManager(false);
    }
}