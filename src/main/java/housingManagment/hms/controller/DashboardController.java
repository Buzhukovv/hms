package housingManagment.hms.controller;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.MaintenanceRequest;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.enums.property.RoomTypeDormitory;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.MaintenanceRequestService;
import housingManagment.hms.service.property.DormitoryRoomService;
import housingManagment.hms.service.userService.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;

import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.repository.userRepository.UserRepository;
import housingManagment.hms.enums.MaintenanceRequestType;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final StudentService studentService;
    private final DormitoryRoomService dormitoryRoomService;
    private final LeaseService leaseService;
    private final MaintenanceRequestService maintenanceRequestService;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String dashboard(Model model) {
        try {
            // Count metrics
            long studentCount = 0;
            long roomCount = 0;
            long leaseCount = 0;
            long maintenanceRequestCount = 0;

            // User type breakdowns
            Map<String, Long> userTypeCounts = new HashMap<>();

            try {
                studentCount = studentService.getAllUsers().size();

                // Get user type breakdowns
                List<Student> students = studentService.getAllUsers();
                Map<StudentRole, Long> studentRoleCounts = students.stream()
                        .collect(Collectors.groupingBy(Student::getRole, Collectors.counting()));

                // Add student role counts to the user type breakdown
                for (Map.Entry<StudentRole, Long> entry : studentRoleCounts.entrySet()) {
                    userTypeCounts.put("STUDENT_" + entry.getKey().name(), entry.getValue());
                }

                // Add other user types if available
                try {
                    // This would need to be implemented with other user services
                    // userTypeCounts.put("STAFF_TOTAL", staffService.getAllUsers().size());
                    // etc.
                } catch (Exception e) {
                    // Ignore if services aren't available
                }

            } catch (Exception e) {
                // Fallback if service throws exception
            }

            // Room type breakdowns
            Map<String, Long> roomTypeCounts = new HashMap<>();

            try {
                List<DormitoryRoom> rooms = dormitoryRoomService.getAllProperties();
                roomCount = rooms.size();

                // Group rooms by type
                Map<RoomTypeDormitory, Long> roomsByType = rooms.stream()
                        .collect(Collectors.groupingBy(DormitoryRoom::getRoomType, Collectors.counting()));

                // Add room type counts
                for (Map.Entry<RoomTypeDormitory, Long> entry : roomsByType.entrySet()) {
                    roomTypeCounts.put(entry.getKey().name(), entry.getValue());
                }

                // Count rooms by status
                long vacantRooms = 0;
                long partiallyOccupiedRooms = 0;
                long occupiedRooms = 0;
                long maintenanceRooms = 0;
                long reservedRooms = 0;

                try {
                    vacantRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.VACANT).size();
                    partiallyOccupiedRooms = dormitoryRoomService
                            .getPropertiesByStatus(PropertyStatus.PARTIALLY_OCCUPIED).size();
                    occupiedRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.OCCUPIED).size();
                    maintenanceRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.OUT_OF_SERVICE).size();
                    reservedRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.RESERVED).size();
                } catch (Exception e) {
                    // Fallback if service throws exception
                }

                model.addAttribute("roomTypeCounts", roomTypeCounts);
                model.addAttribute("vacantRooms", vacantRooms);
                model.addAttribute("partiallyOccupiedRooms", partiallyOccupiedRooms);
                model.addAttribute("occupiedRooms", occupiedRooms);
                model.addAttribute("maintenanceRooms", maintenanceRooms);
                model.addAttribute("reservedRooms", reservedRooms);

            } catch (Exception e) {
                // Fallback if service throws exception
                model.addAttribute("roomCount", 0);
                model.addAttribute("roomTypeCounts", new HashMap<>());
                model.addAttribute("vacantRooms", 0);
                model.addAttribute("partiallyOccupiedRooms", 0);
                model.addAttribute("occupiedRooms", 0);
                model.addAttribute("maintenanceRooms", 0);
                model.addAttribute("reservedRooms", 0);
            }

            try {
                leaseCount = leaseService.getActiveLeases().size();
            } catch (Exception e) {
                // Fallback if service throws exception
            }

            // Maintenance request breakdowns
            Map<String, Long> maintenanceStatusCounts = new HashMap<>();

            try {
                List<MaintenanceRequestDTO> requests = maintenanceRequestService.getAllRequests();
                maintenanceRequestCount = requests.size();

                // Group maintenance requests by status
                Map<MaintenanceRequestStatus, Long> requestsByStatus = requests.stream()
                        .collect(Collectors.groupingBy(MaintenanceRequestDTO::getStatus, Collectors.counting()));

                // Add maintenance request status counts
                for (Map.Entry<MaintenanceRequestStatus, Long> entry : requestsByStatus.entrySet()) {
                    maintenanceStatusCounts.put(entry.getKey().name(), entry.getValue());
                }

            } catch (Exception e) {
                // Fallback if service throws exception
            }

            model.addAttribute("studentCount", studentCount);
            model.addAttribute("roomCount", roomCount);
            model.addAttribute("leaseCount", leaseCount);
            model.addAttribute("maintenanceRequestCount", maintenanceRequestCount);

            // Add detailed breakdowns to the model
            model.addAttribute("userTypeCounts", userTypeCounts);
            model.addAttribute("maintenanceStatusCounts", maintenanceStatusCounts);

            // Room occupancy data for doughnut chart
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
                // Fallback if service throws exception
            }

            model.addAttribute("vacantRooms", vacantRooms);
            model.addAttribute("occupiedRooms", occupiedRooms);
            model.addAttribute("maintenanceRooms", maintenanceRooms);
            model.addAttribute("reservedRooms", reservedRooms);

            // Recent students
            List<Student> latestStudents = new ArrayList<>();
            try {
                latestStudents = studentService.getAllUsers().stream()
                        .limit(5)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                // Fallback if service throws exception
            }
            model.addAttribute("latestStudents", latestStudents);

            // Recent maintenance requests
            List<MaintenanceRequestDTO> recentRequests = new ArrayList<>();
            try {
                recentRequests = maintenanceRequestService.getAllRequests().stream()
                        .limit(5)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                // Fallback if service throws exception
            }
            model.addAttribute("recentRequests", recentRequests);
        } catch (Exception e) {
            // Global fallback
            model.addAttribute("error", "Error loading dashboard data: " + e.getMessage());

            // Ensure all required attributes are set even on error
            model.addAttribute("studentCount", 0);
            model.addAttribute("roomCount", 0);
            model.addAttribute("leaseCount", 0);
            model.addAttribute("maintenanceRequestCount", 0);
            model.addAttribute("vacantRooms", 0);
            model.addAttribute("occupiedRooms", 0);
            model.addAttribute("maintenanceRooms", 0);
            model.addAttribute("reservedRooms", 0);
            model.addAttribute("latestStudents", new ArrayList<>());
            model.addAttribute("recentRequests", new ArrayList<>());
        }

        return "dashboard";
    }

    @GetMapping("/students")
    public String listStudents(Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Student> allStudents = studentService.getAllUsers();

        // Apply filters if provided
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

        // Pagination logic
        int totalItems = allStudents.size();
        // Ensure totalPages is at least 1 to prevent errors in the template
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));

        // Ensure page is within bounds
        page = Math.max(0, Math.min(page, totalPages - 1));

        // Extract the current page items
        List<Student> pagedStudents;
        if (totalItems > 0) {
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalItems);
            pagedStudents = allStudents.subList(fromIndex, toIndex);
        } else {
            pagedStudents = new ArrayList<>();
        }

        // Add pagination attributes to the model
        model.addAttribute("students", pagedStudents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("availableSizes", List.of(10, 20, 30, 40));

        return "students/list";
    }

    @GetMapping("/students/{id}")
    public String viewStudent(@PathVariable UUID id, Model model) {
        Student student = studentService.getUserById(id);
        model.addAttribute("student", student);

        // Get leases related to this student
        model.addAttribute("leases", leaseService.getLeasesByTenant(id));

        return "students/view";
    }

    @GetMapping("/dormitory-rooms")
    public String listDormitoryRooms(Model model,
            @RequestParam(required = false) String block,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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

        // Create a map to store active lease counts for each room
        Map<UUID, Integer> activeLeaseCountMap = new HashMap<>();

        // Calculate active lease count for each room
        for (DormitoryRoom room : allRooms) {
            try {
                List<Lease> activeLeases = leaseService.getActiveLeasesByProperty(room.getId());
                activeLeaseCountMap.put(room.getId(), activeLeases.size());
            } catch (Exception e) {
                // Fallback if service throws exception
                activeLeaseCountMap.put(room.getId(), 0);
            }
        }

        // Pagination logic
        int totalItems = allRooms.size();
        // Ensure totalPages is at least 1 to prevent errors in the template
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));

        // Ensure page is within valid range
        page = Math.max(0, Math.min(page, totalPages - 1));

        // Get current page items
        List<DormitoryRoom> rooms;
        if (totalItems > 0) {
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalItems);
            rooms = allRooms.subList(fromIndex, toIndex);
        } else {
            rooms = new ArrayList<>();
        }

        model.addAttribute("activeLeaseCountMap", activeLeaseCountMap);
        model.addAttribute("rooms", rooms);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("availableSizes", new int[] { 10, 20, 30, 40 });

        // Get list of unique blocks for filtering
        List<String> blocks = dormitoryRoomService.getAllProperties().stream()
                .map(DormitoryRoom::getPropertyBlock)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("blocks", blocks);

        return "dormitory-rooms/list";
    }

    @GetMapping("/dormitory-rooms/visual")
    public String visualDormitoryRooms(Model model,
            @RequestParam(required = false) String block,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(required = false) List<String> floors) {

        // Fetch all dormitory rooms
        List<DormitoryRoom> allRooms = dormitoryRoomService.getAllProperties();

        // Apply status filter if specified
        if (status != null) {
            allRooms = allRooms.stream()
                    .filter(room -> room.getStatus() == status)
                    .collect(Collectors.toList());
        }

        // Apply block filter if specified
        if (block != null && !block.isEmpty()) {
            allRooms = allRooms.stream()
                    .filter(room -> room.getPropertyBlock().equals(block))
                    .collect(Collectors.toList());
        }

        // Create a map to store active lease counts for each room
        Map<UUID, Integer> activeLeaseCountMap = new HashMap<>();
        Map<UUID, Double> occupancyPercentageMap = new HashMap<>();

        // Create a set to collect all unique floors
        Set<String> allFloors = new HashSet<>();

        // Calculate active lease count for each room and collect floor information
        for (DormitoryRoom room : allRooms) {
            try {
                List<Lease> activeLeases = leaseService.getActiveLeasesByProperty(room.getId());
                int activeCount = activeLeases.size();
                activeLeaseCountMap.put(room.getId(), activeCount);

                // Calculate occupancy percentage for color determination
                double percentage = room.getMaxOccupant() > 0
                        ? (double) activeCount / room.getMaxOccupant()
                        : 0;
                occupancyPercentageMap.put(room.getId(), percentage);

                // Update room status based on occupancy percentage
                if (room.getStatus() != PropertyStatus.OUT_OF_SERVICE && room.getStatus() != PropertyStatus.RESERVED) {
                    if (percentage <= 0) {
                        if (room.getStatus() != PropertyStatus.VACANT) {
                            room.setStatus(PropertyStatus.VACANT);
                            dormitoryRoomService.updateProperty(room.getId(), room);
                        }
                    } else if (percentage >= 1.0) {
                        if (room.getStatus() != PropertyStatus.OCCUPIED) {
                            room.setStatus(PropertyStatus.OCCUPIED);
                            dormitoryRoomService.updateProperty(room.getId(), room);
                        }
                    } else {
                        if (room.getStatus() != PropertyStatus.PARTIALLY_OCCUPIED) {
                            room.setStatus(PropertyStatus.PARTIALLY_OCCUPIED);
                            dormitoryRoomService.updateProperty(room.getId(), room);
                        }
                    }
                }

                // Extract floor from property number
                String propertyNumber = room.getPropertyNumber();
                String floorKey = extractFloorFromPropertyNumber(propertyNumber);
                allFloors.add(floorKey);
            } catch (Exception e) {
                activeLeaseCountMap.put(room.getId(), 0);
                occupancyPercentageMap.put(room.getId(), 0.0);
            }
        }

        // Apply floor filtering if specified
        if (floors != null && !floors.isEmpty()) {
            allRooms = allRooms.stream()
                    .filter(room -> {
                        String propertyNumber = room.getPropertyNumber();
                        String floorKey = extractFloorFromPropertyNumber(propertyNumber);
                        return floors.contains(floorKey);
                    })
                    .collect(Collectors.toList());
        }

        // Calculate total items after all filtering
        int totalItems = allRooms.size();

        // Organize rooms by block and floor - using all rooms, no pagination
        Map<String, Map<String, List<DormitoryRoom>>> roomsByBlock = new HashMap<>();

        for (DormitoryRoom room : allRooms) {
            String blockKey = room.getPropertyBlock();

            // Extract floor from property number
            String propertyNumber = room.getPropertyNumber();
            String floorKey = extractFloorFromPropertyNumber(propertyNumber);

            roomsByBlock.computeIfAbsent(blockKey, k -> new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    try {
                        // Compare as integers for proper floor ordering
                        return Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2));
                    } catch (NumberFormatException e) {
                        // Fall back to string comparison if parsing fails
                        return o1.compareTo(o2);
                    }
                }
            })).computeIfAbsent(floorKey, k -> new ArrayList<>()).add(room);
        }

        // Convert allFloors set to a sorted list
        List<String> sortedFloors = new ArrayList<>(allFloors);
        Collections.sort(sortedFloors, (o1, o2) -> {
            try {
                return Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2));
            } catch (NumberFormatException e) {
                return o1.compareTo(o2);
            }
        });

        model.addAttribute("roomsByBlock", roomsByBlock);
        model.addAttribute("activeLeaseCountMap", activeLeaseCountMap);
        model.addAttribute("occupancyPercentageMap", occupancyPercentageMap);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("allFloors", sortedFloors);
        model.addAttribute("selectedFloors", floors != null ? floors : new ArrayList<>());

        // Get list of unique blocks for filtering
        List<String> blocks = dormitoryRoomService.getAllProperties().stream()
                .map(DormitoryRoom::getPropertyBlock)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("blocks", blocks);

        return "dormitory-rooms/visual";
    }

    // Helper method to extract floor from property number
    private String extractFloorFromPropertyNumber(String propertyNumber) {
        String floorKey = "1"; // Default floor if can't parse
        try {
            if (propertyNumber != null && propertyNumber.contains(".")) {
                String[] parts = propertyNumber.split("\\.");
                if (parts.length > 1 && parts[1].length() >= 2) {
                    floorKey = parts[1].substring(0, 2).replaceFirst("^0+", ""); // Remove leading zeros
                    if (floorKey.isEmpty())
                        floorKey = "0";
                }
            }
        } catch (Exception e) {
            // Use default floor if parsing fails
        }
        return floorKey;
    }

    @GetMapping("/dormitory-rooms/{id}")
    public String viewDormitoryRoom(@PathVariable UUID id, Model model) {
        DormitoryRoom room = dormitoryRoomService.getPropertyById(id);
        model.addAttribute("room", room);

        // Get all leases related to this room
        List<Lease> leases = leaseService.getLeasesByProperty(id);
        model.addAttribute("leases", leases);

        // Get active leases (using the optimized method)
        List<Lease> activeLeases = leaseService.getActiveLeasesByProperty(id);
        int activeLeaseCount = activeLeases.size();

        // Update room status based on active leases if needed
        if (room.getStatus() != PropertyStatus.OUT_OF_SERVICE && room.getStatus() != PropertyStatus.RESERVED) {
            // Calculate occupancy percentage
            double occupancyPercentage = room.getMaxOccupant() > 0
                    ? (double) activeLeaseCount / room.getMaxOccupant()
                    : 0;

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

        // Get maintenance requests related to this room (limit to 10 most recent)
        // This would need maintenance request repository to support finding by property
        // ID
        List<MaintenanceRequestDTO> maintenanceRequests = new ArrayList<>();
        // TODO: Implement getMaintenanceRequestsByPropertyId in
        // MaintenanceRequestService
        model.addAttribute("maintenanceRequests", maintenanceRequests);

        // Get available students (limited to 100)
        // Optimized to use a more targeted database query instead of fetching all and
        // filtering in memory
        List<Student> availableStudents = studentService.getAllUsers().stream()
                .filter(student -> student.getRole().toString().equals("STUDENT"))
                .limit(100)
                .collect(Collectors.toList());
        model.addAttribute("availableStudents", availableStudents);

        return "dormitory-rooms/view";
    }

    @GetMapping("/leases")
    public String listLeases(Model model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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
                    .filter(lease -> (lease.getLeaseNumber() != null && lease.getLeaseNumber().contains(search)) ||
                            (lease.getContractNumber() != null && lease.getContractNumber().contains(search)))
                    .collect(Collectors.toList());
        }

        // Pagination logic
        int totalItems = allLeases.size();
        // Ensure totalPages is at least 1 to prevent errors in the template
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));

        // Ensure page is within bounds
        page = Math.max(0, Math.min(page, totalPages - 1));

        // Extract the current page items
        List<Lease> pagedLeases;
        if (totalItems > 0) {
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalItems);
            pagedLeases = allLeases.subList(fromIndex, toIndex);
        } else {
            pagedLeases = new ArrayList<>();
        }

        // Ensure eager loading of tenant and property details
        for (Lease lease : pagedLeases) {
            if (lease.getTenant() != null) {
                lease.getTenant().getFirstName(); // Force loading
            }
            if (lease.getProperty() != null) {
                lease.getProperty().getPropertyNumber(); // Force loading
            }
        }

        // Add pagination attributes to the model
        model.addAttribute("leases", pagedLeases);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("availableSizes", List.of(10, 20, 30, 40));

        return "leases/list";
    }

    @GetMapping("/leases/{id}")
    public String viewLease(@PathVariable UUID id, Model model) {
        Lease lease = leaseService.getLeaseById(id);
        model.addAttribute("lease", lease);
        return "leases/view";
    }

    @GetMapping("/maintenance-requests")
    public String listMaintenanceRequests(Model model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String requestType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<MaintenanceRequestDTO> allRequests = maintenanceRequestService.getAllRequests();

        // Apply filters if provided
        if (status != null && !status.isEmpty()) {
            try {
                MaintenanceRequestStatus requestStatus = MaintenanceRequestStatus.valueOf(status.toUpperCase());
                allRequests = allRequests.stream()
                        .filter(r -> r.getStatus() == requestStatus)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
            }
        }

        // Filter by request type if provided
        if (requestType != null && !requestType.isEmpty()) {
            try {
                MaintenanceRequestType type = MaintenanceRequestType.valueOf(requestType.toUpperCase());
                allRequests = allRequests.stream()
                        .filter(r -> r.getRequestType() == type)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid request type, ignore filter
            }
        }

        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            allRequests = allRequests.stream()
                    .filter(r -> (r.getTitle() != null && r.getTitle().toLowerCase().contains(searchLower)) ||
                            (r.getDescription() != null && r.getDescription().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        }

        // Pagination logic
        int totalItems = allRequests.size();
        // Ensure totalPages is at least 1 to prevent errors in the template
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));

        // Ensure page is within bounds
        page = Math.max(0, Math.min(page, totalPages - 1));

        // Extract the current page items
        List<MaintenanceRequestDTO> pagedRequests;
        if (totalItems > 0) {
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalItems);
            pagedRequests = allRequests.subList(fromIndex, toIndex);
        } else {
            pagedRequests = new ArrayList<>();
        }

        // Create a map of requester IDs to requester names
        Map<UUID, String> requesterNames = new HashMap<>();
        for (MaintenanceRequestDTO request : pagedRequests) {
            if (request.getRequesterId() != null) {
                try {
                    Optional<?> userOptional = userRepository.findById(request.getRequesterId());
                    if (userOptional.isPresent()) {
                        BaseUser user = (BaseUser) userOptional.get();
                        requesterNames.put(request.getRequesterId(),
                                user.getFirstName() + " " + user.getLastName());
                    }
                } catch (Exception e) {
                    // Log the error but continue processing
                    System.err.println("Error fetching requester name: " + e.getMessage());
                }
            }
        }

        // Add pagination attributes to the model
        model.addAttribute("requests", pagedRequests);
        model.addAttribute("requesterNames", requesterNames);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("availableSizes", List.of(10, 20, 30, 40));

        return "maintenance-requests/list";
    }

    @GetMapping("/maintenance-requests/{id}")
    public String viewMaintenanceRequest(@PathVariable UUID id, Model model) {
        MaintenanceRequestDTO request = maintenanceRequestService.getRequestById(id);

        // Get requester name if available
        String requesterName = "Unknown";
        String requesterContact = "No contact information available";
        if (request.getRequesterId() != null) {
            try {
                Optional<?> userOptional = userRepository.findById(request.getRequesterId());
                if (userOptional.isPresent()) {
                    BaseUser user = (BaseUser) userOptional.get();
                    requesterName = user.getFirstName() + " " + user.getLastName();
                    requesterContact = user.getEmail() != null ? user.getEmail() : "No email provided";
                }
            } catch (Exception e) {
                // Log the error but continue
                System.err.println("Error fetching requester name: " + e.getMessage());
            }
        }

        // Get assigned staff name if available
        String assignedToName = "Unknown";
        String assignedToContact = "No contact information available";
        if (request.getAssignedToId() != null) {
            try {
                Optional<?> userOptional = userRepository.findById(request.getAssignedToId());
                if (userOptional.isPresent()) {
                    BaseUser user = (BaseUser) userOptional.get();
                    assignedToName = user.getFirstName() + " " + user.getLastName();
                    assignedToContact = user.getEmail() != null ? user.getEmail() : "No email provided";
                }
            } catch (Exception e) {
                // Log the error but continue
                System.err.println("Error fetching assigned staff name: " + e.getMessage());
            }
        }

        // Get lease and property information if available
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
                // Log the error but continue
                System.err.println("Error fetching lease/property information: " + e.getMessage());
            }
        }

        model.addAttribute("request", request);
        model.addAttribute("requesterName", requesterName);
        model.addAttribute("requesterContact", requesterContact);
        model.addAttribute("assignedToName", assignedToName);
        model.addAttribute("assignedToContact", assignedToContact);
        model.addAttribute("leaseNumber", leaseNumber);
        model.addAttribute("propertyId", propertyId);
        model.addAttribute("propertyNumber", propertyNumber);
        return "maintenance-requests/view";
    }
}