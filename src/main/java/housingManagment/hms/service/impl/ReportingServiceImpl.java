package housingManagment.hms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import housingManagment.hms.entities.SavedReport;
import housingManagment.hms.entities.ScheduledReport;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.property.*;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.ReportFormat;
import housingManagment.hms.enums.ReportSchedule;
import housingManagment.hms.enums.ReportType;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.repository.LeaseRepository;
import housingManagment.hms.repository.MaintenanceRequestRepository;
import housingManagment.hms.repository.SavedReportRepository;
import housingManagment.hms.repository.ScheduledReportRepository;
import housingManagment.hms.repository.propertyRepository.PropertyRepository;
import housingManagment.hms.repository.userRepository.UserRepository;
import housingManagment.hms.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final PropertyRepository propertyRepository;
    private final LeaseRepository leaseRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;
    private final SavedReportRepository savedReportRepository;
    private final ScheduledReportRepository scheduledReportRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "occupancyReports", key = "#startDate.toString() + '-' + #endDate.toString() + '-' + #propertyTypes + '-' + #locations")
    @Transactional(readOnly = true)
    public Map<String, Object> generateOccupancyReport(
            LocalDate startDate, LocalDate endDate, List<String> propertyTypes, List<String> locations) {

        Map<String, Object> reportData = new HashMap<>();

        // Basic report metadata
        reportData.put("reportType", ReportType.OCCUPANCY.name());
        reportData.put("startDate", startDate);
        reportData.put("endDate", endDate);
        reportData.put("generatedAt", LocalDateTime.now());

        // Get total properties count
        long totalProperties = propertyRepository.count();
        reportData.put("totalProperties", totalProperties);

        // Get occupancy metrics from database
        Map<String, Object> occupancyData = new HashMap<>();

        // Count properties with active leases during the period
        long occupiedCount = leaseRepository.countByStartDateBeforeAndEndDateAfter(endDate, startDate);
        long vacantCount = totalProperties - occupiedCount;
        double occupancyRate = totalProperties > 0 ? (double) occupiedCount / totalProperties * 100 : 0;

        occupancyData.put("occupiedCount", occupiedCount);
        occupancyData.put("vacantCount", vacantCount);
        occupancyData.put("occupancyRate", occupancyRate);

        // Calculate average vacancy duration
        // This would require more complex query logic based on your data model
        // For now, we'll query all properties without active leases and calculate
        double averageVacancyDuration = leaseRepository.findAverageVacancyDuration(startDate, endDate);
        occupancyData.put("averageVacancyDuration", averageVacancyDuration);

        // Property type breakdown (from actual data)
        Map<String, Object> propertyTypeData = new HashMap<>();

        if (propertyTypes != null && !propertyTypes.isEmpty()) {
            for (String propertyType : propertyTypes) {
                Class<? extends BaseProperty> propertyTypeClass = getPropertyTypeClass(propertyType);
                long typeTotal = propertyRepository.countByType(propertyTypeClass);
                long typeOccupied = leaseRepository.countByPropertyTypeAndDateRange(propertyTypeClass, startDate,
                        endDate);
                double typeOccupancyRate = typeTotal > 0 ? (double) typeOccupied / typeTotal * 100 : 0;

                propertyTypeData.put(propertyType, Map.of(
                        "total", typeTotal,
                        "occupied", typeOccupied,
                        "occupancyRate", typeOccupancyRate));
            }
        } else {
            // If no property types specified, get all types
            List<Class<? extends BaseProperty>> allPropertyTypes = propertyRepository.findAllPropertyTypes();
            for (Class<? extends BaseProperty> propertyTypeClass : allPropertyTypes) {
                String propertyType = propertyTypeClass.getSimpleName();
                long typeTotal = propertyRepository.countByType(propertyTypeClass);
                long typeOccupied = leaseRepository.countByPropertyTypeAndDateRange(propertyTypeClass, startDate,
                        endDate);
                double typeOccupancyRate = typeTotal > 0 ? (double) typeOccupied / typeTotal * 100 : 0;

                propertyTypeData.put(propertyType, Map.of(
                        "total", typeTotal,
                        "occupied", typeOccupied,
                        "occupancyRate", typeOccupancyRate));
            }
        }

        occupancyData.put("byPropertyType", propertyTypeData);
        reportData.put("occupancyData", occupancyData);

        // Historical trend data - last 6 months
        List<Map<String, Object>> trendData = leaseRepository.findOccupancyTrend(startDate.minusMonths(6), endDate);
        reportData.put("trendData", trendData);

        return reportData;
    }

    @Override
    @Cacheable(value = "tenantReports", key = "#startDate.toString() + '-' + #endDate.toString() + '-' + #tenantTypes + '-' + #locations")
    @Transactional(readOnly = true)
    public Map<String, Object> generateTenantReport(
            LocalDate startDate, LocalDate endDate, List<String> tenantTypes, List<String> locations) {

        Map<String, Object> reportData = new HashMap<>();

        // Basic report metadata
        reportData.put("reportType", ReportType.TENANT.name());
        reportData.put("startDate", startDate);
        reportData.put("endDate", endDate);
        reportData.put("generatedAt", LocalDateTime.now());

        // Get tenant data from database
        Map<String, Object> tenantSummary = new HashMap<>();

        // Count total tenants
        long totalTenants = userRepository.countTenants();

        // Count active tenants (with active lease in the period)
        long activeTenants = leaseRepository.countActiveTenantsInPeriod(startDate, endDate);

        // Count new tenants (lease started in period)
        long newTenants = leaseRepository.countNewTenantsInPeriod(startDate, endDate);

        // Count departing tenants (lease ended in period)
        long departingTenants = leaseRepository.countDepartingTenantsInPeriod(startDate, endDate);

        // Calculate average lease duration
        double averageLeaseDuration = leaseRepository.calculateAverageLeaseDuration();

        tenantSummary.put("totalTenants", totalTenants);
        tenantSummary.put("activeTenants", activeTenants);
        tenantSummary.put("newTenants", newTenants);
        tenantSummary.put("departingTenants", departingTenants);
        tenantSummary.put("averageLeaseDuration", averageLeaseDuration);

        // Get tenant type breakdown from database
        Map<String, Object> tenantTypeStats;

        // Get counts for all tenant types
        tenantTypeStats = userRepository.countAllTenantTypes();

        tenantSummary.put("tenantTypeBreakdown", tenantTypeStats);
        reportData.put("tenantSummary", tenantSummary);

        // Academic programs data
        // Note: We're not using countStudentsByProgram since that method was removed
        // due to missing academicProgram field in Student entity
        Map<String, Object> academicPrograms = new HashMap<>();

        // Get student data from the repository - fetch all students
        List<BaseUser> students = userRepository.findAll().stream()
                .filter(user -> user.getClass().getSimpleName().equals("Student"))
                .collect(Collectors.toList());

        // Count students (this avoids hardcoded values)
        long studentCount = students.size();

        // Group students by their roles (academic programs)
        Map<StudentRole, Long> roleDistribution = students.stream()
                .filter(user -> user instanceof Student)
                .map(user -> (Student) user)
                .collect(Collectors.groupingBy(Student::getRole, Collectors.counting()));

        // Add the real distribution data
        academicPrograms.put("TOTAL_STUDENTS", studentCount);
        academicPrograms.put("DISTRIBUTION_BY_PROGRAM", roleDistribution);

        // Add percentage distribution
        Map<String, Double> percentages = new HashMap<>();
        for (Map.Entry<StudentRole, Long> entry : roleDistribution.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / studentCount;
            percentages.put(entry.getKey().toString(), Math.round(percentage * 10) / 10.0); // Round to 1 decimal place
        }
        academicPrograms.put("PERCENTAGE_BY_PROGRAM", percentages);

        // Also group by school
        Map<String, Long> schoolDistribution = students.stream()
                .filter(user -> user instanceof Student)
                .map(user -> (Student) user)
                .collect(Collectors.groupingBy(Student::getSchool, Collectors.counting()));

        academicPrograms.put("DISTRIBUTION_BY_SCHOOL", schoolDistribution);

        reportData.put("academicPrograms", academicPrograms);

        // Get lease duration statistics from database
        Map<String, Object> leaseDuration = leaseRepository.getLeaseDurationDistribution();
        reportData.put("leaseDuration", leaseDuration);

        // Get tenant turnover trend from database
        List<Map<String, Object>> turnoverTrend = leaseRepository.getTenantTurnoverByMonth(
                startDate.minusMonths(6), endDate);
        reportData.put("turnoverTrend", turnoverTrend);

        // Filter by location if provided
        if (locations != null && !locations.isEmpty()) {
            // Get tenant distribution by provided locations
            Map<String, Object> locationDistribution = leaseRepository.getTenantCountByLocations(
                    locations, startDate, endDate);
            reportData.put("locationDistribution", locationDistribution);
        }

        return reportData;
    }

    @Override
    @Cacheable(value = "maintenanceReports", key = "#startDate.toString() + '-' + #endDate.toString() + '-' + #requestStatuses + '-' + #maintenanceStaff")
    @Transactional(readOnly = true)
    public Map<String, Object> generateMaintenanceReport(
            LocalDate startDate, LocalDate endDate, List<String> requestStatuses, List<UUID> maintenanceStaff) {

        Map<String, Object> reportData = new HashMap<>();

        // Basic report metadata
        reportData.put("reportType", ReportType.MAINTENANCE.name());
        reportData.put("startDate", startDate);
        reportData.put("endDate", endDate);
        reportData.put("generatedAt", LocalDateTime.now());

        // Get maintenance request data from database
        Map<String, Object> requestSummary = new HashMap<>();

        // Query total requests in period
        long totalRequests = maintenanceRequestRepository.countByCreatedAtBetween(startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59));

        // Filter by status if provided
        if (requestStatuses != null && !requestStatuses.isEmpty()) {
            totalRequests = maintenanceRequestRepository.countByStatusInAndCreatedAtBetween(
                    requestStatuses, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }

        // Count completed requests
        long completedRequests = maintenanceRequestRepository.countByStatusAndCompletedAtBetween(
                "COMPLETED", startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        // Count pending requests
        long pendingRequests = maintenanceRequestRepository.countByStatusAndCreatedAtBetween(
                "PENDING", startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        // Count in-progress requests
        long inProgressRequests = maintenanceRequestRepository.countByStatusAndCreatedAtBetween(
                "IN_PROGRESS", startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        // Calculate completion rate
        double completionRate = totalRequests > 0 ? (double) completedRequests / totalRequests * 100 : 0;

        // Calculate average resolution time
        double averageResolutionTime = maintenanceRequestRepository.calculateAverageResolutionTime(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        requestSummary.put("totalRequests", totalRequests);
        requestSummary.put("completedRequests", completedRequests);
        requestSummary.put("pendingRequests", pendingRequests);
        requestSummary.put("inProgressRequests", inProgressRequests);
        requestSummary.put("completionRate", completionRate);
        requestSummary.put("averageResolutionTime", averageResolutionTime);

        // Get status breakdown from database
        Map<String, Object> statusBreakdown = maintenanceRequestRepository.getRequestStatusCounts(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        requestSummary.put("statusBreakdown", statusBreakdown);
        reportData.put("requestSummary", requestSummary);

        // Request type breakdown from database
        Map<String, Object> requestTypes = maintenanceRequestRepository.getRequestTypeCounts(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        reportData.put("requestTypes", requestTypes);

        // Get maintenance cost data from database
        Map<String, Object> costData = new HashMap<>();

        // Query total cost for the period
        double totalCost = maintenanceRequestRepository.calculateTotalCostBetween(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        // Calculate average cost per request
        double averageCostPerRequest = totalRequests > 0 ? totalCost / totalRequests : 0;

        costData.put("totalCost", totalCost);
        costData.put("averageCostPerRequest", averageCostPerRequest);

        // Get cost by type from database
        Map<String, Object> costByType = maintenanceRequestRepository.getCostByRequestType(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        costData.put("costByType", costByType);
        reportData.put("costData", costData);

        // Get staff performance metrics from database
        List<Map<String, Object>> staffPerformance;

        if (maintenanceStaff != null && !maintenanceStaff.isEmpty()) {
            // Filter by specified staff
            staffPerformance = maintenanceRequestRepository.getStaffPerformanceByIds(
                    maintenanceStaff, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            // Get all staff performance
            staffPerformance = maintenanceRequestRepository.getAllStaffPerformance(
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }

        reportData.put("staffPerformance", staffPerformance);

        return reportData;
    }

    @Override
    public Map<String, Object> generateCustomReport(String reportType, LocalDate startDate, LocalDate endDate,
            Map<String, Object> parameters) {
        switch (reportType.toLowerCase()) {
            case "occupancy":
                List<String> propertyTypes = (List<String>) parameters.getOrDefault("propertyTypes",
                        Collections.emptyList());
                List<String> locations = (List<String>) parameters.getOrDefault("locations", Collections.emptyList());
                return generateOccupancyReport(startDate, endDate, propertyTypes, locations);
            case "maintenance":
                List<String> requestStatuses = (List<String>) parameters.getOrDefault("requestStatuses",
                        Collections.emptyList());
                List<UUID> maintenanceStaff = (List<UUID>) parameters.getOrDefault("maintenanceStaff",
                        Collections.emptyList());
                return generateMaintenanceReport(startDate, endDate, requestStatuses, maintenanceStaff);
            case "tenant":
                List<String> tenantTypes = (List<String>) parameters.getOrDefault("tenantTypes",
                        Collections.emptyList());
                List<String> tenantLocations = (List<String>) parameters.getOrDefault("locations",
                        Collections.emptyList());
                return generateTenantReport(startDate, endDate, tenantTypes, tenantLocations);
            default:
                throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
    }

    @Override
    @Transactional
    public UUID saveReport(String reportType, Map<String, Object> reportData, String reportName) {
        try {
            // For a real implementation, you would get the current user from security
            // context
            // This is just a placeholder
            BaseUser currentUser;
            try {
                var user = userRepository.findById(UUID.randomUUID());
                if (user.isEmpty()) {
                    throw new IllegalStateException("Current user not found");
                }
                currentUser = (BaseUser) user.get();
            } catch (ClassCastException e) {
                throw new RuntimeException("Error casting to BaseUser", e);
            }

            SavedReport savedReport = SavedReport.builder()
                    .name(reportName)
                    .reportType(reportType)
                    .createdBy(currentUser)
                    .reportParameters("{}") // Would be populated with actual parameters in real implementation
                    .reportData(objectMapper.writeValueAsString(reportData))
                    .build();

            savedReport = savedReportRepository.save(savedReport);
            return savedReport.getId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize report data", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSavedReport(UUID reportId) {
        SavedReport savedReport = savedReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with ID: " + reportId));

        try {
            return objectMapper.readValue(savedReport.getReportData(), new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize report data", e);
        }
    }

    @Override
    @Transactional
    public UUID scheduleReport(
            String reportType, Map<String, Object> parameters, String schedule,
            List<String> recipients, String reportFormat) {

        try {
            // For a real implementation, you would get the current user from security
            // context
            // This is just a placeholder
            BaseUser currentUser;
            try {
                var user = userRepository.findById(UUID.randomUUID());
                if (user.isEmpty()) {
                    throw new IllegalStateException("Current user not found");
                }
                currentUser = (BaseUser) user.get();
            } catch (ClassCastException e) {
                throw new RuntimeException("Error casting to BaseUser", e);
            }

            // Validate schedule type
            ReportSchedule reportSchedule;
            try {
                reportSchedule = ReportSchedule.valueOf(schedule.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid schedule type: " + schedule);
            }

            // Calculate next run time based on schedule
            LocalDateTime nextRunAt = calculateNextRunTime(reportSchedule);

            ScheduledReport scheduledReport = ScheduledReport.builder()
                    .name(reportType + " Report - " + schedule)
                    .reportType(reportType)
                    .createdBy(currentUser)
                    .schedule(schedule)
                    .cronExpression(reportSchedule.getCronExpression())
                    .reportParameters(objectMapper.writeValueAsString(parameters))
                    .recipients(recipients)
                    .reportFormat(reportFormat)
                    .active(true)
                    .nextRunAt(nextRunAt)
                    .build();

            scheduledReport = scheduledReportRepository.save(scheduledReport);
            return scheduledReport.getId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize report parameters", e);
        }
    }

    @Override
    @Transactional
    public void cancelScheduledReport(UUID scheduleId) {
        ScheduledReport scheduledReport = scheduledReportRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled report not found with ID: " + scheduleId));

        scheduledReport.setActive(false);
        scheduledReportRepository.save(scheduledReport);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportReport(UUID reportId, String format) {
        SavedReport savedReport = savedReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with ID: " + reportId));

        try {
            Map<String, Object> reportData = objectMapper.readValue(savedReport.getReportData(),
                    new TypeReference<Map<String, Object>>() {
                    });

            // Validate format
            ReportFormat reportFormat;
            try {
                reportFormat = ReportFormat.valueOf(format.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid report format: " + format);
            }

            // In a real implementation, this would generate the appropriate file format
            // For demonstration purposes, we'll just return a placeholder
            String reportContent = "Report ID: " + reportId + "\n";
            reportContent += "Format: " + reportFormat + "\n";
            reportContent += "Generated at: " + LocalDateTime.now() + "\n\n";
            reportContent += "Report data: " + objectMapper.writeValueAsString(reportData);

            return reportContent.getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process report data", e);
        }
    }

    // Helper methods

    @SuppressWarnings("unchecked")
    private <T> List<T> getListParameter(Map<String, Object> parameters, String key) {
        if (parameters == null || !parameters.containsKey(key)) {
            return null;
        }

        Object value = parameters.get(key);
        if (value instanceof List) {
            return (List<T>) value;
        }

        return null;
    }

    private boolean getBooleanParameter(Map<String, Object> parameters, String key, boolean defaultValue) {
        if (parameters == null || !parameters.containsKey(key)) {
            return defaultValue;
        }

        Object value = parameters.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return defaultValue;
    }

    private LocalDateTime calculateNextRunTime(ReportSchedule schedule) {
        LocalDateTime now = LocalDateTime.now();

        switch (schedule) {
            case DAILY:
                return now.plusDays(1).truncatedTo(ChronoUnit.DAYS);
            case WEEKLY:
                return now.plusWeeks(1).with(java.time.DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
            case MONTHLY:
                return now.plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            case QUARTERLY:
                // Calculate next quarter start
                int month = now.getMonthValue();
                int nextQuarterFirstMonth;

                if (month < 4)
                    nextQuarterFirstMonth = 4; // Q2 starts in April
                else if (month < 7)
                    nextQuarterFirstMonth = 7; // Q3 starts in July
                else if (month < 10)
                    nextQuarterFirstMonth = 10; // Q4 starts in October
                else
                    nextQuarterFirstMonth = 1; // Q1 starts in January of next year

                if (nextQuarterFirstMonth == 1) {
                    return now.plusYears(1).withMonth(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                } else {
                    return now.withMonth(nextQuarterFirstMonth).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                }
            case SEMESTER:
                // Calculate next semester start (January or August)
                month = now.getMonthValue();

                if (month < 8) {
                    return now.withMonth(8).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                } else {
                    return now.plusYears(1).withMonth(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                }
            default:
                return now.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        }
    }

    /**
     * Helper method to convert property type string to the corresponding class
     */
    private Class<? extends BaseProperty> getPropertyTypeClass(String propertyType) {
        if (propertyType == null) {
            return BaseProperty.class;
        }

        switch (propertyType.toUpperCase()) {
            case "DORMITORYROOM":
                return DormitoryRoom.class;
            case "CAMPUSAPARTMENT":
                return CampusApartment.class;
            case "COTTAGE":
                return Cottage.class;
            case "OFFCAMPUSAPARTMENT":
                return OffCampusApartment.class;
            default:
                throw new IllegalArgumentException("Unknown property type: " + propertyType);
        }
    }
}