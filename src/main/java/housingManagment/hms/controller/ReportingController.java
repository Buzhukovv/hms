package housingManagment.hms.controller;

import housingManagment.hms.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    // Templates endpoints

    @GetMapping("/templates")
    public ResponseEntity<List<Map<String, Object>>> getReportTemplates() {
        // This would be implemented to return available report templates
        return ResponseEntity.ok(List.of(
                Map.of("id", "occupancy", "name", "Occupancy Report", "description", "Property occupancy statistics"),
                Map.of("id", "maintenance", "name", "Maintenance Report", "description",
                        "Maintenance request statistics"),
                Map.of("id", "tenant", "name", "Tenant Report", "description", "Tenant demographics and statistics")));
    }

    @GetMapping("/templates/{templateId}")
    public ResponseEntity<Map<String, Object>> getReportTemplate(@PathVariable String templateId) {
        // This would be implemented to return a specific template definition
        return ResponseEntity.ok(Map.of(
                "id", templateId,
                "name", templateId.substring(0, 1).toUpperCase() + templateId.substring(1) + " Report",
                "description", "Report template for " + templateId,
                "parameters", List.of("startDate", "endDate")));
    }

    // Report generation endpoints

    @PostMapping("/generate/occupancy")
    public ResponseEntity<Map<String, Object>> generateOccupancyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> propertyTypes,
            @RequestParam(required = false) List<String> locations) {

        Map<String, Object> report = reportingService.generateOccupancyReport(
                startDate, endDate, propertyTypes, locations);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/generate/maintenance")
    public ResponseEntity<Map<String, Object>> generateMaintenanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> requestStatuses,
            @RequestParam(required = false) List<UUID> maintenanceStaff) {

        Map<String, Object> report = reportingService.generateMaintenanceReport(
                startDate, endDate, requestStatuses, maintenanceStaff);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/generate/tenant")
    public ResponseEntity<Map<String, Object>> generateTenantReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> tenantTypes,
            @RequestParam(required = false) List<String> locations) {

        Map<String, Object> report = reportingService.generateTenantReport(
                startDate, endDate, tenantTypes, locations);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/generate/custom")
    public ResponseEntity<Map<String, Object>> generateCustomReport(
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestBody Map<String, Object> parameters) {

        Map<String, Object> report = reportingService.generateCustomReport(
                reportType, startDate, endDate, parameters);
        return ResponseEntity.ok(report);
    }

    // Saved reports endpoints

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveReport(
            @RequestParam String reportType,
            @RequestParam String reportName,
            @RequestBody Map<String, Object> reportData) {

        UUID reportId = reportingService.saveReport(reportType, reportData, reportName);
        return ResponseEntity.ok(Map.of("id", reportId));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<Map<String, Object>>> getSavedReports() {
        // This would be implemented to return the user's saved reports
        return ResponseEntity.ok(List.of(
                Map.of("id", UUID.randomUUID(), "name", "Monthly Occupancy - January 2023", "type", "occupancy"),
                Map.of("id", UUID.randomUUID(), "name", "Fall Semester Maintenance Summary", "type", "maintenance")));
    }

    @GetMapping("/saved/{reportId}")
    public ResponseEntity<Map<String, Object>> getSavedReport(@PathVariable UUID reportId) {
        Map<String, Object> report = reportingService.getSavedReport(reportId);
        return ResponseEntity.ok(report);
    }

    // Scheduling endpoints

    @PostMapping("/schedule")
    public ResponseEntity<Map<String, Object>> scheduleReport(
            @RequestParam String reportType,
            @RequestParam String schedule,
            @RequestParam List<String> recipients,
            @RequestParam String reportFormat,
            @RequestBody Map<String, Object> parameters) {

        UUID scheduleId = reportingService.scheduleReport(
                reportType, parameters, schedule, recipients, reportFormat);
        return ResponseEntity.ok(Map.of("scheduleId", scheduleId));
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<Map<String, Object>>> getScheduledReports() {
        // This would be implemented to return the user's scheduled reports
        return ResponseEntity.ok(List.of(
                Map.of(
                        "id", UUID.randomUUID(),
                        "name", "Monthly Occupancy Report",
                        "schedule", "MONTHLY",
                        "recipients", List.of("admin@example.com"))));
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<Void> cancelScheduledReport(@PathVariable UUID scheduleId) {
        reportingService.cancelScheduledReport(scheduleId);
        return ResponseEntity.noContent().build();
    }

    // Export endpoints

    @GetMapping("/export/{reportId}")
    public ResponseEntity<byte[]> exportReport(
            @PathVariable UUID reportId,
            @RequestParam String format) {

        byte[] reportContent = reportingService.exportReport(reportId, format);

        String filename = "report_" + reportId + "." + format.toLowerCase();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", filename);

        if ("PDF".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.APPLICATION_PDF);
        } else if ("EXCEL".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        } else if ("CSV".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.parseMediaType("text/csv"));
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(reportContent);
    }
}