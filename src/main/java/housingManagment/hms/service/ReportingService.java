package housingManagment.hms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for generating reports on various aspects of the housing management
 * system.
 * Supports different time periods (monthly, quarterly, semester) and multiple
 * report types.
 */
public interface ReportingService {

        /**
         * Generate an occupancy report for properties
         *
         * @param startDate     Start date for the report period
         * @param endDate       End date for the report period
         * @param propertyTypes Optional list of property types to filter by
         * @param locations     Optional list of locations to filter by
         * @return Map containing the report data
         */
        Map<String, Object> generateOccupancyReport(
                        LocalDate startDate,
                        LocalDate endDate,
                        List<String> propertyTypes,
                        List<String> locations);

        /**
         * Generate a maintenance activity report
         *
         * @param startDate        Start date for the report period
         * @param endDate          End date for the report period
         * @param requestStatuses  Optional list of request statuses to filter by
         * @param maintenanceStaff Optional list of maintenance staff IDs to filter by
         * @return Map containing the report data
         */
        Map<String, Object> generateMaintenanceReport(
                        LocalDate startDate,
                        LocalDate endDate,
                        List<String> requestStatuses,
                        List<UUID> maintenanceStaff);

        /**
         * Generate a tenant demographics report
         *
         * @param startDate   Start date for the report period
         * @param endDate     End date for the report period
         * @param tenantTypes Optional list of tenant types to filter by
         * @param locations   Optional list of locations to filter by
         * @return Map containing the report data
         */
        Map<String, Object> generateTenantReport(
                        LocalDate startDate,
                        LocalDate endDate,
                        List<String> tenantTypes,
                        List<String> locations);

        /**
         * Generate a custom report based on the provided parameters
         *
         * @param reportType The type of report to generate
         * @param startDate  Start date for the report period
         * @param endDate    End date for the report period
         * @param parameters Custom parameters for the report
         * @return Map containing the report data
         */
        Map<String, Object> generateCustomReport(
                        String reportType,
                        LocalDate startDate,
                        LocalDate endDate,
                        Map<String, Object> parameters);

        /**
         * Save a report for future reference
         *
         * @param reportType The type of report
         * @param reportData The report data
         * @param reportName User-specified name for the report
         * @return The ID of the saved report
         */
        UUID saveReport(String reportType, Map<String, Object> reportData, String reportName);

        /**
         * Retrieve a previously saved report
         *
         * @param reportId The ID of the saved report
         * @return The report data
         */
        Map<String, Object> getSavedReport(UUID reportId);

        /**
         * Schedule a report to be generated and delivered periodically
         *
         * @param reportType   The type of report to generate
         * @param parameters   The report parameters
         * @param schedule     The schedule specification (e.g., "MONTHLY", "WEEKLY")
         * @param recipients   List of email addresses to receive the report
         * @param reportFormat The format of the report (e.g., "PDF", "EXCEL")
         * @return The ID of the scheduled report
         */
        UUID scheduleReport(
                        String reportType,
                        Map<String, Object> parameters,
                        String schedule,
                        List<String> recipients,
                        String reportFormat);

        /**
         * Cancel a scheduled report
         *
         * @param scheduleId The ID of the scheduled report
         */
        void cancelScheduledReport(UUID scheduleId);

        /**
         * Export a report in the specified format
         *
         * @param reportId The ID of the report to export
         * @param format   The format to export to (e.g., "PDF", "EXCEL", "CSV")
         * @return Byte array containing the exported report
         */
        byte[] exportReport(UUID reportId, String format);
}