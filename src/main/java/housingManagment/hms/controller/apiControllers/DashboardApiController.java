package housingManagment.hms.controller.apiControllers;

import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.enums.MaintenanceRequestType;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.enums.property.RoomTypeDormitory;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.repository.propertyRepository.PropertyRepository;
import housingManagment.hms.repository.userRepository.*;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.MaintenanceRequestService;
import housingManagment.hms.service.property.CampusApartmentService;
import housingManagment.hms.service.property.CottageService;
import housingManagment.hms.service.property.DormitoryRoomService;
import housingManagment.hms.service.property.OffCampusApartmentService;
import housingManagment.hms.service.property.TownhouseService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import housingManagment.hms.service.userService.StudentService;
import housingManagment.hms.service.userService.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final LeaseService leaseService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final MaintenanceRequestService maintenanceRequestService;
    private final DormitoryRoomService dormitoryRoomService;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final HousingManagementRepository housingManagementRepository;
    private final DepartmentOfStudentServicesRepository dssRepository;
    private final PropertyRepository propertyRepository;
    private final CottageService cottageService;
    private final CampusApartmentService campusApartmentService;
    private final OffCampusApartmentService offCampusApartmentService;
    private final TownhouseService townhouseService;

    @Autowired
    public DashboardApiController(LeaseService leaseService,
                                  StudentService studentService,
                                  TeacherService teacherService,
                                  MaintenanceRequestService maintenanceRequestService,
                                  DormitoryRoomService dormitoryRoomService,
                                  UserRepository userRepository,
                                  TeacherRepository teacherRepository,
                                  MaintenanceRepository maintenanceRepository,
                                  HousingManagementRepository housingManagementRepository,
                                  DepartmentOfStudentServicesRepository dssRepository,
                                  PropertyRepository propertyRepository,
                                  CottageService cottageService,
                                  CampusApartmentService campusApartmentService,
                                  OffCampusApartmentService offCampusApartmentService,
                                  TownhouseService townhouseService) {
        this.leaseService = leaseService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.maintenanceRequestService = maintenanceRequestService;
        this.dormitoryRoomService = dormitoryRoomService;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.housingManagementRepository = housingManagementRepository;
        this.dssRepository = dssRepository;
        this.propertyRepository = propertyRepository;
        this.cottageService = cottageService;
        this.campusApartmentService = campusApartmentService;
        this.offCampusApartmentService = offCampusApartmentService;
        this.townhouseService = townhouseService;
    }

    /**
     * Получить сводные метрики для дашборда.
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> response = new HashMap<>();
        try {
            long studentCount = 0;
            long roomCount = 0;
            long leaseCount = 0;
            long maintenanceRequestCount = 0;

            Map<String, Long> userTypeCounts = new HashMap<>();
            Map<String, Long> roomTypeCounts = new HashMap<>();
            Map<String, Long> maintenanceStatusCounts = new HashMap<>();

            // Подсчет студентов и распределение по ролям
            try {
                List<Student> students = studentService.getAllUsers();
                studentCount = students.size();
                Map<StudentRole, Long> studentRoleCounts = students.stream()
                        .collect(Collectors.groupingBy(Student::getRole, Collectors.counting()));
                studentRoleCounts.forEach((role, count) ->
                        userTypeCounts.put("STUDENT_" + role.name(), count));
            } catch (Exception e) {
                // Игнорируем исключения
            }

            // Подсчет аудиторий и распределение по типам
            try {
                List<DormitoryRoom> rooms = dormitoryRoomService.getAllProperties();
                roomCount = rooms.size();
                Map<RoomTypeDormitory, Long> roomsByType = rooms.stream()
                        .collect(Collectors.groupingBy(DormitoryRoom::getRoomType, Collectors.counting()));
                roomsByType.forEach((type, count) -> roomTypeCounts.put(type.name(), count));
            } catch (Exception e) {
                // Игнорируем исключения
            }

            // Подсчет активных лизингов
            try {
                leaseCount = leaseService.getActiveLeases().size();
            } catch (Exception e) {
                // Игнорируем исключения
            }

            // Подсчет заявок на обслуживание и распределение по статусам
            try {
                List<MaintenanceRequestDTO> requests = maintenanceRequestService.getAllRequests();
                maintenanceRequestCount = requests.size();
                Map<MaintenanceRequestStatus, Long> requestsByStatus = requests.stream()
                        .collect(Collectors.groupingBy(MaintenanceRequestDTO::getStatus, Collectors.counting()));
                requestsByStatus.forEach((status, count) -> maintenanceStatusCounts.put(status.name(), count));
            } catch (Exception e) {
                // Игнорируем исключения
            }

            // Дополнительные данные по статусу аудиторий
            long vacantRooms = 0;
            long occupiedRooms = 0;
            long maintenanceRooms = 0;
            long reservedRooms = 0;
            try {
                vacantRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.VACANT).size();
                occupiedRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.OCCUPIED).size();
                maintenanceRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.OUT_OF_SERVICE).size();
                reservedRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.RESERVED).size();
            } catch (Exception e) {
                // Игнорируем исключения
            }

            response.put("studentCount", studentCount);
            response.put("roomCount", roomCount);
            response.put("leaseCount", leaseCount);
            response.put("maintenanceRequestCount", maintenanceRequestCount);
            response.put("userTypeCounts", userTypeCounts);
            response.put("roomTypeCounts", roomTypeCounts);
            response.put("maintenanceStatusCounts", maintenanceStatusCounts);
            response.put("vacantRooms", vacantRooms);
            response.put("occupiedRooms", occupiedRooms);
            response.put("maintenanceRooms", maintenanceRooms);
            response.put("reservedRooms", reservedRooms);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Получить список студентов с фильтрацией и постраничной выборкой.
     */
    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> listStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Student> allStudents = studentService.getAllUsers();

            if (search != null && !search.isEmpty()) {
                String searchLower = search.toLowerCase();
                allStudents = allStudents.stream()
                        .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(searchLower))
                        .collect(Collectors.toList());
            }
            if (role != null && !role.isEmpty()) {
                allStudents = allStudents.stream()
                        .filter(s -> s.getRole().toString().equals(role))
                        .collect(Collectors.toList());
            }
            int totalItems = allStudents.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));
            page = Math.max(0, Math.min(page, totalPages - 1));
            List<Student> pagedStudents = totalItems > 0
                    ? allStudents.subList(page * size, Math.min(page * size + size, totalItems))
                    : new ArrayList<>();

            response.put("students", pagedStudents);
            response.put("currentPage", page);
            response.put("totalPages", totalPages);
            response.put("pageSize", size);
            response.put("totalItems", totalItems);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Получить подробную информацию о студенте, включая его лизинги.
     */
    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudent(@PathVariable UUID id) {
        try {
            Student student = studentService.getUserById(id);
            List<Lease> leases = leaseService.getLeasesByTenant(id);
            // Принудительная инициализация связанных сущностей
            leases.forEach(lease -> {
                if (lease.getProperty() != null) {
                    lease.getProperty().getPropertyNumber();
                    lease.getProperty().getPropertyBlock();
                }
            });
            Map<String, Object> result = new HashMap<>();
            result.put("student", student);
            result.put("leases", leases);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Получить список аудиторий (dormitory rooms) с фильтрами и постраничной выборкой.
     */
    @GetMapping("/dormitory-rooms")
    public ResponseEntity<Map<String, Object>> listDormitoryRooms(
            @RequestParam(required = false) String block,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DormitoryRoom> allRooms;
            if (status != null) {
                allRooms = dormitoryRoomService.getPropertiesByStatus(status);
            } else {
                allRooms = dormitoryRoomService.getAllProperties();
            }
            if (block != null && !block.isEmpty()) {
                allRooms = allRooms.stream()
                        .filter(room -> room.getPropertyBlock().equals(block))
                        .collect(Collectors.toList());
            }
            // Формирование карты активных лизингов для каждой аудитории
            Map<UUID, Integer> activeLeaseCountMap = new HashMap<>();
            for (DormitoryRoom room : allRooms) {
                try {
                    List<Lease> activeLeases = leaseService.getActiveLeasesByProperty(room.getId());
                    activeLeaseCountMap.put(room.getId(), activeLeases.size());
                } catch (Exception ex) {
                    activeLeaseCountMap.put(room.getId(), 0);
                }
            }
            int totalItems = allRooms.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));
            page = Math.max(0, Math.min(page, totalPages - 1));
            List<DormitoryRoom> pagedRooms = totalItems > 0
                    ? allRooms.subList(page * size, Math.min(page * size + size, totalItems))
                    : new ArrayList<>();

            response.put("rooms", pagedRooms);
            response.put("activeLeaseCountMap", activeLeaseCountMap);
            response.put("currentPage", page);
            response.put("totalPages", totalPages);
            response.put("pageSize", size);
            response.put("totalItems", totalItems);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Получить подробную информацию об аудитории, включая лизинговые данные.
     */
    @GetMapping("/dormitory-rooms/{id}")
    public ResponseEntity<?> getDormitoryRoom(@PathVariable UUID id) {
        try {
            DormitoryRoom room = dormitoryRoomService.getPropertyById(id);
            List<Lease> leases = leaseService.getLeasesByProperty(id);
            List<Lease> activeLeases = leaseService.getActiveLeasesByProperty(id);
            int activeLeaseCount = activeLeases.size();
            // Обновление статуса аудитории на основе активных лизингов (если требуется)
            if (room.getStatus() != PropertyStatus.OUT_OF_SERVICE && room.getStatus() != PropertyStatus.RESERVED) {
                double occupancyPercentage = room.getMaxOccupant() > 0
                        ? (double) activeLeaseCount / room.getMaxOccupant() : 0;
                if (activeLeaseCount == 0 && room.getStatus() != PropertyStatus.VACANT) {
                    room.setStatus(PropertyStatus.VACANT);
                    dormitoryRoomService.updateProperty(id, room);
                } else if (occupancyPercentage >= 1.0 && room.getStatus() != PropertyStatus.OCCUPIED) {
                    room.setStatus(PropertyStatus.OCCUPIED);
                    dormitoryRoomService.updateProperty(id, room);
                } else if (activeLeaseCount > 0 && occupancyPercentage < 1.0
                        && room.getStatus() != PropertyStatus.PARTIALLY_OCCUPIED) {
                    room.setStatus(PropertyStatus.PARTIALLY_OCCUPIED);
                    dormitoryRoomService.updateProperty(id, room);
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("room", room);
            result.put("leases", leases);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Получить список лизингов с фильтрами и постраничной выборкой.
     */
    @GetMapping("/leases")
    public ResponseEntity<Map<String, Object>> listLeases(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Lease> allLeases;
            if (status != null && !status.isEmpty()) {
                try {
                    LeaseStatus leaseStatus = LeaseStatus.valueOf(status.toUpperCase());
                    allLeases = leaseService.getLeasesByStatus(leaseStatus);
                } catch (IllegalArgumentException e) {
                    allLeases = leaseService.getAllLeases();
                }
            } else {
                allLeases = leaseService.getAllLeases();
            }
            if (search != null && !search.isEmpty()) {
                allLeases = allLeases.stream()
                        .filter(lease -> (lease.getLeaseNumber() != null && lease.getLeaseNumber().contains(search))
                                || (lease.getContractNumber() != null && lease.getContractNumber().contains(search)))
                        .collect(Collectors.toList());
            }
            int totalItems = allLeases.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));
            page = Math.max(0, Math.min(page, totalPages - 1));
            List<Lease> pagedLeases = totalItems > 0
                    ? allLeases.subList(page * size, Math.min(page * size + size, totalItems))
                    : new ArrayList<>();
            // Принудительная инициализация связанных сущностей
            pagedLeases.forEach(lease -> {
                if (lease.getTenant() != null) {
                    lease.getTenant().getFirstName();
                }
                if (lease.getProperty() != null) {
                    lease.getProperty().getPropertyNumber();
                }
            });
            response.put("leases", pagedLeases);
            response.put("currentPage", page);
            response.put("totalPages", totalPages);
            response.put("pageSize", size);
            response.put("totalItems", totalItems);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Получить подробную информацию о лизинге.
     */
    @GetMapping("/leases/{id}")
    public ResponseEntity<?> getLease(@PathVariable UUID id) {
        try {
            Lease lease = leaseService.getLeaseById(id);
            if (lease.getProperty() != null) {
                lease.getProperty().getPropertyNumber();
                lease.getProperty().getPropertyBlock();
            }
            if (lease.getTenant() != null) {
                lease.getTenant().getFirstName();
                lease.getTenant().getLastName();
            }
            return ResponseEntity.ok(lease);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Получить список заявок на обслуживание с фильтрами и постраничной выборкой.
     */
    @GetMapping("/maintenance-requests")
    public ResponseEntity<Map<String, Object>> listMaintenanceRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String requestType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<MaintenanceRequestDTO> allRequests = maintenanceRequestService.getAllRequests();
            if (status != null && !status.isEmpty()) {
                try {
                    MaintenanceRequestStatus reqStatus = MaintenanceRequestStatus.valueOf(status.toUpperCase());
                    allRequests = allRequests.stream()
                            .filter(r -> r.getStatus() == reqStatus)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    // Неверный статус — фильтрация не применяется
                }
            }
            if (requestType != null && !requestType.isEmpty()) {
                try {
                    MaintenanceRequestType type = MaintenanceRequestType.valueOf(requestType.toUpperCase());
                    allRequests = allRequests.stream()
                            .filter(r -> r.getRequestType() == type)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    // Неверный тип — фильтрация не применяется
                }
            }
            if (search != null && !search.isEmpty()) {
                String searchLower = search.toLowerCase();
                allRequests = allRequests.stream()
                        .filter(r -> (r.getTitle() != null && r.getTitle().toLowerCase().contains(searchLower))
                                || (r.getDescription() != null && r.getDescription().toLowerCase().contains(searchLower)))
                        .collect(Collectors.toList());
            }
            int totalItems = allRequests.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));
            page = Math.max(0, Math.min(page, totalPages - 1));
            List<MaintenanceRequestDTO> pagedRequests = totalItems > 0
                    ? allRequests.subList(page * size, Math.min(page * size + size, totalItems))
                    : new ArrayList<>();
            // Формирование карты имен заявителей
            Map<UUID, String> requesterNames = new HashMap<>();
            for (MaintenanceRequestDTO request : pagedRequests) {
                if (request.getRequesterId() != null) {
                    try {
                        Optional<?> userOptional = userRepository.findById(request.getRequesterId());
                        if (userOptional.isPresent()) {
                            BaseUser user = (BaseUser) userOptional.get();
                            requesterNames.put(request.getRequesterId(), user.getFirstName() + " " + user.getLastName());
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибки
                    }
                }
            }
            response.put("requests", pagedRequests);
            response.put("requesterNames", requesterNames);
            response.put("currentPage", page);
            response.put("totalPages", totalPages);
            response.put("pageSize", size);
            response.put("totalItems", totalItems);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Получить подробную информацию о заявке на обслуживание.
     */
    @GetMapping("/maintenance-requests/{id}")
    public ResponseEntity<?> getMaintenanceRequest(@PathVariable UUID id) {
        try {
            MaintenanceRequestDTO request = maintenanceRequestService.getRequestById(id);

            String requesterName = "Unknown";
            String requesterContact = "No contact information available";
            if (request.getRequesterId() != null) {
                try {
                    Optional<?> userOptional = userRepository.findById(request.getRequesterId());
                    if (userOptional.isPresent()) {
                        BaseUser user = (BaseUser) userOptional.get();
                        requesterName = user.getFirstName() + " " + user.getLastName();
                        requesterContact = (user.getEmail() != null) ? user.getEmail() : "No email provided";
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки
                }
            }

            String assignedToName = "Unknown";
            String assignedToContact = "No contact information available";
            if (request.getAssignedToId() != null) {
                try {
                    Optional<?> userOptional = userRepository.findById(request.getAssignedToId());
                    if (userOptional.isPresent()) {
                        BaseUser user = (BaseUser) userOptional.get();
                        assignedToName = user.getFirstName() + " " + user.getLastName();
                        assignedToContact = (user.getEmail() != null) ? user.getEmail() : "No email provided";
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки
                }
            }

            String leaseNumber = null;
            UUID propertyId = null;
            String propertyNumber = null;
            if (request.getLeaseId() != null) {
                try {
                    Lease lease = leaseService.getLeaseById(request.getLeaseId());
                    leaseNumber = lease.getLeaseNumber();
                    if (lease.getProperty() != null) {
                        propertyId = lease.getProperty().getId();
                        propertyNumber = lease.getProperty().getPropertyNumber();
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("request", request);
            result.put("requesterName", requesterName);
            result.put("requesterContact", requesterContact);
            result.put("assignedToName", assignedToName);
            result.put("assignedToContact", assignedToContact);
            result.put("leaseNumber", leaseNumber);
            result.put("propertyId", propertyId);
            result.put("propertyNumber", propertyNumber);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Обновить статусы всех аудиторий.
     */
    @GetMapping("/update-room-statuses")
    public ResponseEntity<Map<String, String>> updateRoomStatuses() {
        try {
            List<DormitoryRoom> allRooms = dormitoryRoomService.getAllProperties();
            int updatedCount = 0;
            for (DormitoryRoom room : allRooms) {
                // Пропускаем аудитории со специальными статусами
                if (room.getStatus() == PropertyStatus.RESERVED || room.getStatus() == PropertyStatus.OUT_OF_SERVICE) {
                    continue;
                }
                List<Lease> activeLeases = leaseService.getActiveLeasesByProperty(room.getId());
                int activeLeaseCount = activeLeases.size();
                double occupancyPercentage = room.getMaxOccupant() > 0
                        ? (double) activeLeaseCount / room.getMaxOccupant() : 0;
                PropertyStatus newStatus;
                if (activeLeaseCount == 0) {
                    newStatus = PropertyStatus.VACANT;
                } else if (occupancyPercentage >= 1.0) {
                    newStatus = PropertyStatus.OCCUPIED;
                } else {
                    newStatus = PropertyStatus.PARTIALLY_OCCUPIED;
                }
                if (room.getStatus() != newStatus) {
                    room.setStatus(newStatus);
                    dormitoryRoomService.updateProperty(room.getId(), room);
                    updatedCount++;
                }
            }
            return ResponseEntity.ok(Map.of("message", String.format("Successfully updated %d room statuses", updatedCount)));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
