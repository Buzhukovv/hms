package housingManagment.hms.service.impl;

import housingManagment.hms.dto.DashboardData;
import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.enums.property.RoomTypeDormitory;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.repository.userRepository.UserRepository;
import housingManagment.hms.service.*;
import housingManagment.hms.service.property.DormitoryRoomService;
import housingManagment.hms.service.userService.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final StudentService studentService;
    private final LeaseService leaseService;
    private final MaintenanceRequestService maintenanceRequestService;
    private final DormitoryRoomService dormitoryRoomService;
    private final UserRepository userRepository;

    @Autowired
    public DashboardServiceImpl(StudentService studentService,
                                LeaseService leaseService,
                                MaintenanceRequestService maintenanceRequestService,
                                DormitoryRoomService dormitoryRoomService,
                                UserRepository userRepository) {
        this.studentService = studentService;
        this.leaseService = leaseService;
        this.maintenanceRequestService = maintenanceRequestService;
        this.dormitoryRoomService = dormitoryRoomService;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardData getDashboardData() {
        DashboardData data = new DashboardData();

        // 1) Кол-во студентов
        long studentCount = 0;
        List<Student> students = new ArrayList<>();
        try {
            students = studentService.getAllUsers();
            studentCount = students.size();
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setStudentCount(studentCount);

        // 2) Кол-во комнат (DormitoryRoom)
        long roomCount = 0;
        List<DormitoryRoom> allRooms = new ArrayList<>();
        try {
            allRooms = dormitoryRoomService.getAllProperties();
            roomCount = allRooms.size();
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setRoomCount(roomCount);

        // 3) Кол-во активных лизингов
        long leaseCount = 0;
        try {
            leaseCount = leaseService.getActiveLeases().size();
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setLeaseCount(leaseCount);

        // 4) Кол-во заявок на обслуживание
        long maintenanceRequestCount = 0;
        List<MaintenanceRequestDTO> allRequests = new ArrayList<>();
        try {
            allRequests = maintenanceRequestService.getAllRequests();
            maintenanceRequestCount = allRequests.size();
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setMaintenanceRequestCount(maintenanceRequestCount);

        // 5) Распределение пользователей по типам/ролям
        Map<String, Long> userTypeCounts = new HashMap<>();
        try {
            // Пример: считаем только студентов по их StudentRole
            Map<StudentRole, Long> studentRoleCounts = students.stream()
                    .collect(Collectors.groupingBy(Student::getRole, Collectors.counting()));
            for (Map.Entry<StudentRole, Long> entry : studentRoleCounts.entrySet()) {
                userTypeCounts.put("STUDENT_" + entry.getKey().name(), entry.getValue());
            }
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setUserTypeCounts(userTypeCounts);

        // 6) Распределение комнат по типу (DormitoryRoom)
        Map<String, Long> roomTypeCounts = new HashMap<>();
        try {
            Map<RoomTypeDormitory, Long> roomsByType = allRooms.stream()
                    .collect(Collectors.groupingBy(DormitoryRoom::getRoomType, Collectors.counting()));
            for (Map.Entry<RoomTypeDormitory, Long> entry : roomsByType.entrySet()) {
                roomTypeCounts.put(entry.getKey().name(), entry.getValue());
            }
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setRoomTypeCounts(roomTypeCounts);

        // 7) Статусы комнат
        long vacantRooms = 0;
        long partiallyOccupiedRooms = 0;
        long occupiedRooms = 0;
        long maintenanceRooms = 0;
        long reservedRooms = 0;
        try {
            vacantRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.VACANT).size();
            partiallyOccupiedRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.PARTIALLY_OCCUPIED).size();
            occupiedRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.OCCUPIED).size();
            maintenanceRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.OUT_OF_SERVICE).size();
            reservedRooms = dormitoryRoomService.getPropertiesByStatus(PropertyStatus.RESERVED).size();
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setVacantRooms(vacantRooms);
        data.setPartiallyOccupiedRooms(partiallyOccupiedRooms);
        data.setOccupiedRooms(occupiedRooms);
        data.setMaintenanceRooms(maintenanceRooms);
        data.setReservedRooms(reservedRooms);

        // 8) Распределение заявок на обслуживание по статусу
        Map<String, Long> maintenanceStatusCounts = new HashMap<>();
        try {
            Map<MaintenanceRequestStatus, Long> requestsByStatus = allRequests.stream()
                    .collect(Collectors.groupingBy(MaintenanceRequestDTO::getStatus, Collectors.counting()));
            for (Map.Entry<MaintenanceRequestStatus, Long> entry : requestsByStatus.entrySet()) {
                maintenanceStatusCounts.put(entry.getKey().name(), entry.getValue());
            }
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setMaintenanceStatusCounts(maintenanceStatusCounts);

        // 9) Последние студенты
        List<Student> latestStudents = new ArrayList<>();
        try {
            latestStudents = students.stream()
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setLatestStudents(latestStudents);

        // 10) Последние заявки
        List<MaintenanceRequestDTO> recentRequests = new ArrayList<>();
        try {
            recentRequests = allRequests.stream()
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // логируем или обрабатываем
        }
        data.setRecentRequests(recentRequests);

        return data;
    }
}
