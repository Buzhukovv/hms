package housingManagment.hms.controller;

import housingManagment.hms.dto.DashboardData;
import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.*;
import housingManagment.hms.entities.userEntity.*;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.enums.MaintenanceRequestType;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.enums.property.RoomTypeDormitory;
import housingManagment.hms.enums.userEnum.*;
import housingManagment.hms.repository.propertyRepository.PropertyRepository;
import housingManagment.hms.repository.userRepository.*;
import housingManagment.hms.service.DashboardService;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.MaintenanceRequestService;
import housingManagment.hms.service.property.*;
import housingManagment.hms.service.userService.StudentService;
import housingManagment.hms.service.userService.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        try {
            DashboardData data = dashboardService.getDashboardData();

            model.addAttribute("studentCount", data.getStudentCount());
            model.addAttribute("roomCount", data.getRoomCount());
            model.addAttribute("leaseCount", data.getLeaseCount());
            model.addAttribute("maintenanceRequestCount", data.getMaintenanceRequestCount());

            model.addAttribute("userTypeCounts", data.getUserTypeCounts());
            model.addAttribute("roomTypeCounts", data.getRoomTypeCounts());
            model.addAttribute("maintenanceStatusCounts", data.getMaintenanceStatusCounts());

            model.addAttribute("vacantRooms", data.getVacantRooms());
            model.addAttribute("partiallyOccupiedRooms", data.getPartiallyOccupiedRooms());
            model.addAttribute("occupiedRooms", data.getOccupiedRooms());
            model.addAttribute("maintenanceRooms", data.getMaintenanceRooms());
            model.addAttribute("reservedRooms", data.getReservedRooms());

            model.addAttribute("latestStudents", data.getLatestStudents());
            model.addAttribute("recentRequests", data.getRecentRequests());
        } catch (Exception e) {
            // Глобальный fallback
            model.addAttribute("error", "Error loading dashboard data: " + e.getMessage());
            // Подставляем безопасные дефолтные значения
            model.addAttribute("studentCount", 0);
            model.addAttribute("roomCount", 0);
            model.addAttribute("leaseCount", 0);
            model.addAttribute("maintenanceRequestCount", 0);
            model.addAttribute("vacantRooms", 0);
            model.addAttribute("occupiedRooms", 0);
            model.addAttribute("maintenanceRooms", 0);
            model.addAttribute("reservedRooms", 0);
            model.addAttribute("latestStudents", new java.util.ArrayList<>());
            model.addAttribute("recentRequests", new java.util.ArrayList<>());
        }

        return "dashboard";
    }
}

