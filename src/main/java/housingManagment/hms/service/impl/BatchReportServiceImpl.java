package housingManagment.hms.service.impl;

import housingManagment.hms.service.BatchReportService;
import housingManagment.hms.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchReportServiceImpl implements BatchReportService {

    private final ReportingService reportingService;

    @Override
    @Async("reportExecutor")
    public CompletableFuture<Map<String, Object>> generateOccupancyReportAsync(
            LocalDate startDate, LocalDate endDate, List<String> propertyTypes, List<String> locations) {
        return CompletableFuture.completedFuture(
                reportingService.generateOccupancyReport(startDate, endDate, propertyTypes, locations));
    }

    @Override
    @Async("reportExecutor")
    public CompletableFuture<Map<String, Object>> generateMaintenanceReportAsync(
            LocalDate startDate, LocalDate endDate, List<String> requestStatuses, List<UUID> maintenanceStaff) {
        return CompletableFuture.completedFuture(
                reportingService.generateMaintenanceReport(startDate, endDate, requestStatuses, maintenanceStaff));
    }

    @Override
    @Async("reportExecutor")
    public CompletableFuture<Map<String, Object>> generateTenantReportAsync(
            LocalDate startDate, LocalDate endDate, List<String> tenantTypes, List<String> locations) {
        return CompletableFuture.completedFuture(
                reportingService.generateTenantReport(startDate, endDate, tenantTypes, locations));
    }

    @Override
    public Map<String, Map<String, Object>> generateMultipleReports(
            LocalDate startDate, LocalDate endDate, List<String> reportTypes) {

        // Create futures for each report type
        Map<String, CompletableFuture<Map<String, Object>>> futures = new HashMap<>();

        for (String reportType : reportTypes) {
            switch (reportType.toLowerCase()) {
                case "occupancy":
                    futures.put(reportType, generateOccupancyReportAsync(startDate, endDate, null, null));
                    break;
                case "maintenance":
                    futures.put(reportType, generateMaintenanceReportAsync(startDate, endDate, null, null));
                    break;
                case "tenant":
                    futures.put(reportType, generateTenantReportAsync(startDate, endDate, null, null));
                    break;
                default:
                    // Skip invalid report types
                    break;
            }
        }

        // Wait for all reports to complete
        CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0])).join();

        // Collect results
        return futures.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().join()));
    }
}