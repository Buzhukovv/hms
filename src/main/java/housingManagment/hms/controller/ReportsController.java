package housingManagment.hms.controller;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.MaintenanceRequest;
import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.enums.MaintenanceRequestType;
import housingManagment.hms.enums.ReportFormat;
import housingManagment.hms.repository.LeaseRepository;
import housingManagment.hms.repository.MaintenanceRequestRepository;
import housingManagment.hms.repository.propertyRepository.DormitoryRoomRepository;
import housingManagment.hms.service.ReportingService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    @Autowired
    private LeaseRepository leaseRepository;

    @Autowired
    private DormitoryRoomRepository roomRepository;

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    @Autowired
    private ReportingService reportingService;

    @GetMapping
    public String showReportsPage(
            Model model,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String location) {

        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Occupancy Rate by Block
        Map<String, Double> occupancyByBlock = calculateOccupancyByBlock(location);
        model.addAttribute("occupancyByBlock", occupancyByBlock);

        // Maintenance Requests by Status
        Map<String, Long> maintenanceByStatus = calculateMaintenanceByStatus(startDate, endDate);
        model.addAttribute("maintenanceByStatus", maintenanceByStatus);

        // Maintenance Requests by Type (if available)
        try {
            Map<String, Long> maintenanceByType = calculateMaintenanceByType(startDate, endDate);
            model.addAttribute("maintenanceByType", maintenanceByType);
        } catch (Exception e) {
            // Maintenance type might not be available yet
            model.addAttribute("maintenanceByType", new HashMap<String, Long>());
        }

        // Lease Expiry in next 30 days
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plusDays(30);
        List<Lease> expiringLeases = leaseRepository.findAll().stream()
                .filter(lease -> {
                    LocalDate leaseEndDate = lease.getEndDate();
                    return leaseEndDate != null && !leaseEndDate.isBefore(today)
                            && !leaseEndDate.isAfter(thirtyDaysLater);
                })
                .collect(Collectors.toList());

        model.addAttribute("expiringLeases", expiringLeases);
        model.addAttribute("expiringLeaseCount", expiringLeases.size());

        // Add date range to model
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "reports/index";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam String reportType,
            @RequestParam String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String location) {

        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        try {
            byte[] reportData;
            String filename;
            HttpHeaders headers = new HttpHeaders();

            switch (reportType.toLowerCase()) {
                case "occupancy":
                    Map<String, Double> occupancyData = calculateOccupancyByBlock(location);
                    reportData = generateExportContent(format, "Occupancy by Block", occupancyData, startDate, endDate);
                    filename = "occupancy_report_" + formatDate(LocalDate.now()) + "." + getFileExtension(format);
                    break;
                case "maintenance":
                    Map<String, Long> maintenanceData = calculateMaintenanceByStatus(startDate, endDate);
                    reportData = generateExportContent(format, "Maintenance Requests by Status", maintenanceData,
                            startDate, endDate);
                    filename = "maintenance_report_" + formatDate(LocalDate.now()) + "." + getFileExtension(format);
                    break;
                case "leases":
                    // Get expiring leases
                    LocalDate today = LocalDate.now();
                    LocalDate thirtyDaysLater = today.plusDays(30);
                    List<Lease> expiringLeases = leaseRepository.findAll().stream()
                            .filter(lease -> {
                                LocalDate leaseEndDate = lease.getEndDate();
                                return leaseEndDate != null && !leaseEndDate.isBefore(today)
                                        && !leaseEndDate.isAfter(thirtyDaysLater);
                            })
                            .collect(Collectors.toList());
                    reportData = generateExportContentForLeases(format, "Expiring Leases", expiringLeases);
                    filename = "leases_report_" + formatDate(LocalDate.now()) + "." + getFileExtension(format);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid report type".getBytes());
            }

            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentType(getMediaType(format));

            return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }

    private MediaType getMediaType(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return MediaType.APPLICATION_PDF;
            case "EXCEL":
                return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "CSV":
                return MediaType.parseMediaType("text/csv");
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private String getFileExtension(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return "pdf";
            case "EXCEL":
                return "xlsx";
            case "CSV":
                return "csv";
            default:
                return "txt";
        }
    }

    private byte[] generateExportContent(String format, String title, Map<?, ?> data, LocalDate startDate,
            LocalDate endDate) throws IOException {
        switch (format.toUpperCase()) {
            case "PDF":
                // For now, we'll delegate to the reporting service
                Map<String, Object> params = new HashMap<>();
                params.put("title", title);
                params.put("data", data);
                return reportingService.exportReport(java.util.UUID.randomUUID(), format);
            case "EXCEL":
                return generateExcelReport(title, data, startDate, endDate);
            case "CSV":
                return generateCsvReport(title, data);
            default:
                return "Unsupported format".getBytes();
        }
    }

    private byte[] generateExportContentForLeases(String format, String title, List<Lease> leases) throws IOException {
        switch (format.toUpperCase()) {
            case "PDF":
                // For now, we'll delegate to the reporting service
                Map<String, Object> params = new HashMap<>();
                params.put("title", title);
                params.put("leases", leases);
                return reportingService.exportReport(java.util.UUID.randomUUID(), format);
            case "EXCEL":
                return generateExcelLeasesReport(title, leases);
            case "CSV":
                return generateCsvLeasesReport(title, leases);
            default:
                return "Unsupported format".getBytes();
        }
    }

    private byte[] generateExcelReport(String title, Map<?, ?> data, LocalDate startDate, LocalDate endDate)
            throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(title);

            // Create header row
            Row headerRow = sheet.createRow(0);
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue(title);

            // Report date range
            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0)
                    .setCellValue("Report Period: " + formatDate(startDate) + " to " + formatDate(endDate));

            // Column headers
            Row columnHeaderRow = sheet.createRow(3);
            columnHeaderRow.createCell(0).setCellValue("Key");
            columnHeaderRow.createCell(1).setCellValue("Value");

            // Data rows
            int rowNum = 4;
            for (Map.Entry<?, ?> entry : data.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey().toString());
                Object value = entry.getValue();
                Cell valueCell = row.createCell(1);
                if (value instanceof Number) {
                    valueCell.setCellValue(((Number) value).doubleValue());
                } else {
                    valueCell.setCellValue(value.toString());
                }
            }

            // Auto-size columns
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] generateExcelLeasesReport(String title, List<Lease> leases) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(title);

            // Create header row
            Row headerRow = sheet.createRow(0);
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue(title);

            // Column headers
            Row columnHeaderRow = sheet.createRow(2);
            columnHeaderRow.createCell(0).setCellValue("Lease Number");
            columnHeaderRow.createCell(1).setCellValue("Tenant");
            columnHeaderRow.createCell(2).setCellValue("Property");
            columnHeaderRow.createCell(3).setCellValue("End Date");
            columnHeaderRow.createCell(4).setCellValue("Monthly Rent");

            // Data rows
            int rowNum = 3;
            for (Lease lease : leases) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(lease.getLeaseNumber());

                String tenantName = "Unknown";
                if (lease.getTenant() != null) {
                    tenantName = lease.getTenant().getFirstName() + " " + lease.getTenant().getLastName();
                }
                row.createCell(1).setCellValue(tenantName);

                String propertyNumber = "Unknown";
                if (lease.getProperty() != null) {
                    propertyNumber = lease.getProperty().getPropertyNumber();
                }
                row.createCell(2).setCellValue(propertyNumber);

                Cell endDateCell = row.createCell(3);
                if (lease.getEndDate() != null) {
                    endDateCell.setCellValue(formatDate(lease.getEndDate()));
                } else {
                    endDateCell.setCellValue("N/A");
                }

                Cell rentCell = row.createCell(4);
                if (lease.getMonthlyRent() != null) {
                    rentCell.setCellValue(lease.getMonthlyRent());
                } else {
                    rentCell.setCellValue(0.0);
                }
            }

            // Auto-size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] generateCsvReport(String title, Map<?, ?> data) {
        StringBuilder csv = new StringBuilder();
        csv.append(title).append("\n\n");
        csv.append("Key,Value\n");

        for (Map.Entry<?, ?> entry : data.entrySet()) {
            csv.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
        }

        return csv.toString().getBytes();
    }

    private byte[] generateCsvLeasesReport(String title, List<Lease> leases) {
        StringBuilder csv = new StringBuilder();
        csv.append(title).append("\n\n");
        csv.append("Lease Number,Tenant,Property,End Date,Monthly Rent\n");

        for (Lease lease : leases) {
            String tenantName = "Unknown";
            if (lease.getTenant() != null) {
                tenantName = lease.getTenant().getFirstName() + " " + lease.getTenant().getLastName();
            }

            String propertyNumber = "Unknown";
            if (lease.getProperty() != null) {
                propertyNumber = lease.getProperty().getPropertyNumber();
            }

            String endDate = lease.getEndDate() != null ? formatDate(lease.getEndDate()) : "N/A";
            String monthlyRent = lease.getMonthlyRent() != null ? lease.getMonthlyRent().toString() : "0.0";

            csv.append(lease.getLeaseNumber()).append(",")
                    .append(tenantName).append(",")
                    .append(propertyNumber).append(",")
                    .append(endDate).append(",")
                    .append(monthlyRent).append("\n");
        }

        return csv.toString().getBytes();
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private Map<String, Double> calculateOccupancyByBlock(String specificBlock) {
        Map<String, Double> occupancyByBlock = new HashMap<>();
        List<DormitoryRoom> allRooms = roomRepository.findAll();

        // Apply block filter if specified
        if (specificBlock != null && !specificBlock.isEmpty()) {
            allRooms = allRooms.stream()
                    .filter(room -> specificBlock.equals(room.getPropertyBlock()))
                    .collect(Collectors.toList());
        }

        // Group by block
        Map<String, List<DormitoryRoom>> roomsByBlock = allRooms.stream()
                .collect(Collectors
                        .groupingBy(room -> room.getPropertyBlock() != null ? room.getPropertyBlock() : "Unknown"));

        // Calculate occupancy percentage for each block
        for (Map.Entry<String, List<DormitoryRoom>> entry : roomsByBlock.entrySet()) {
            String block = entry.getKey();
            List<DormitoryRoom> rooms = entry.getValue();

            long occupiedCount = rooms.stream()
                    .filter(room -> "OCCUPIED".equals(room.getStatus().name()))
                    .count();

            double occupancyRate = rooms.isEmpty() ? 0 : (double) occupiedCount / rooms.size() * 100;
            occupancyByBlock.put(block, Math.round(occupancyRate * 10) / 10.0); // Round to 1 decimal place
        }

        return occupancyByBlock;
    }

    private Map<String, Long> calculateMaintenanceByStatus(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> statusCounts = new HashMap<>();
        List<MaintenanceRequest> allRequests = maintenanceRequestRepository.findAll().stream()
                .filter(request -> {
                    LocalDate createdAt = request.getCreatedAt().toLocalDate();
                    return !createdAt.isBefore(startDate) && !createdAt.isAfter(endDate);
                })
                .collect(Collectors.toList());

        // Count requests by status
        Map<MaintenanceRequestStatus, Long> countsByStatus = allRequests.stream()
                .collect(Collectors.groupingBy(MaintenanceRequest::getStatus, Collectors.counting()));

        // Ensure all statuses have a value
        for (MaintenanceRequestStatus status : MaintenanceRequestStatus.values()) {
            statusCounts.put(status.name(), countsByStatus.getOrDefault(status, 0L));
        }

        return statusCounts;
    }

    private Map<String, Long> calculateMaintenanceByType(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> typeCounts = new HashMap<>();
        List<MaintenanceRequest> allRequests = maintenanceRequestRepository.findAll().stream()
                .filter(request -> {
                    LocalDate createdAt = request.getCreatedAt().toLocalDate();
                    return !createdAt.isBefore(startDate) && !createdAt.isAfter(endDate);
                })
                .collect(Collectors.toList());

        // Count requests by type
        Map<MaintenanceRequestType, Long> countsByType = allRequests.stream()
                .filter(request -> request.getRequestType() != null)
                .collect(Collectors.groupingBy(MaintenanceRequest::getRequestType, Collectors.counting()));

        // Ensure all types have a value
        for (MaintenanceRequestType type : MaintenanceRequestType.values()) {
            typeCounts.put(type.name(), countsByType.getOrDefault(type, 0L));
        }

        return typeCounts;
    }
}