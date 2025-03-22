package housingManagment.hms.service;

import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for asynchronous and batch processing of reports
 */
public interface BatchReportService {

    /**
     * Asynchronously generate an occupancy report
     */
    @Async
    CompletableFuture<Map<String, Object>> generateOccupancyReportAsync(
            LocalDate startDate,
            LocalDate endDate,
            List<String> propertyTypes,
            List<String> locations);

    /**
     * Asynchronously generate a maintenance report
     */
    @Async
    CompletableFuture<Map<String, Object>> generateMaintenanceReportAsync(
            LocalDate startDate,
            LocalDate endDate,
            List<String> requestStatuses,
            List<UUID> maintenanceStaff);

    /**
     * Asynchronously generate a tenant report
     */
    @Async
    CompletableFuture<Map<String, Object>> generateTenantReportAsync(
            LocalDate startDate,
            LocalDate endDate,
            List<String> tenantTypes,
            List<String> locations);

    /**
     * Generate multiple reports in parallel
     */
    Map<String, Map<String, Object>> generateMultipleReports(
            LocalDate startDate,
            LocalDate endDate,
            List<String> reportTypes);
}