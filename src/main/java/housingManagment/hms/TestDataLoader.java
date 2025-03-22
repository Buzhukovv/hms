package housingManagment.hms;

import housingManagment.hms.dto.LeaseCreateDTO;
import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.enums.property.RoomTypeDormitory;
import housingManagment.hms.enums.userEnum.Gender;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.repository.propertyRepository.PropertyRepository;
import housingManagment.hms.repository.userRepository.UserRepository;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.MaintenanceRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class populates the database with test data when the application starts.
 * Only active when the "default" profile is active to avoid loading test data
 * in
 * production.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("default")
public class TestDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final LeaseService leaseService;
    private final MaintenanceRequestService maintenanceRequestService;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Loading test data...");

        // Create a student
        Student student = createStudent();
        log.info("Created student: {}", student.getEmail());

        // Create a property (dormitory room)
        DormitoryRoom dormitoryRoom = createDormitoryRoom();
        log.info("Created dormitory room: {}", dormitoryRoom.getPropertyNumber());

        // Create a lease
        var lease = createLease(student, dormitoryRoom);
        log.info("Created lease with ID: {}", lease.getId());

        // Create a maintenance request
        var request = createMaintenanceRequest(student, lease.getId());
        log.info("Created maintenance request: {}", request.getTitle());

        log.info("Test data loading complete!");
    }

    private Student createStudent() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@university.edu");
        student.setPassword("password123"); // In a real app, this would be hashed
        student.setLocalPhone("+1234567890");
        student.setRole(StudentRole.BACHELOR_DEGREE_3_Y);
        student.setSchool("School of Engineering");
        student.setSpecialty("Computer Science");
        student.setGender(Gender.MALE);

        // Required BaseUser fields
        student.setNationalId(12345678);
        student.setNuid(987654);
        student.setIdentityDocNo(123456);
        student.setIdentityIssueDate(LocalDate.of(2018, 1, 15));

        return userRepository.save(student);
    }

    private DormitoryRoom createDormitoryRoom() {
        DormitoryRoom room = new DormitoryRoom();
        room.setPropertyBlock("Block A");
        room.setStatus(PropertyStatus.VACANT);
        room.setIsPaid(false);
        room.setMaxOccupant(2);
        room.setRent(500.0);
        room.setDepositAmount(500.0);
        room.setRoomType(RoomTypeDormitory.TWO_BEDDED_ROOM);

        return propertyRepository.save(room);
    }

    private housingManagment.hms.entities.Lease createLease(Student student, DormitoryRoom room) {
        LeaseCreateDTO leaseDTO = new LeaseCreateDTO();
        leaseDTO.setLeaseNumber("L" + System.currentTimeMillis());
        leaseDTO.setPropertyId(room.getId());
        leaseDTO.setTenantId(student.getId());
        leaseDTO.setStatus("ACTIVE");
        leaseDTO.setStartDate(LocalDate.now());
        leaseDTO.setEndDate(LocalDate.now().plusMonths(6));
        leaseDTO.setCheckInDate(LocalDate.now());
        leaseDTO.setCheckOutDate(LocalDate.now().plusMonths(6));
        leaseDTO.setMonthlyRent(room.getRent());
        leaseDTO.setSecurityDeposit(room.getDepositAmount());
        leaseDTO.setLeaseTerm(6);
        leaseDTO.setTerms("Standard lease terms");
        leaseDTO.setContractNumber("C" + System.currentTimeMillis());
        leaseDTO.setReservationStatus(true);
        leaseDTO.setDeposit(room.getDepositAmount());
        leaseDTO.setFamilyMembers(new ArrayList<>()); // No family members for dormitory

        return leaseService.createLease(leaseDTO);
    }

    private MaintenanceRequestDTO createMaintenanceRequest(Student student, UUID leaseId) {
        MaintenanceRequestDTO requestDTO = new MaintenanceRequestDTO();
        requestDTO.setTitle("Broken Light Fixture");
        requestDTO.setDescription("The ceiling light in the room is flickering and needs to be replaced.");
        requestDTO.setRequesterId(student.getId());
        requestDTO.setLeaseId(leaseId);
        requestDTO.setServiceCharge(50.0); // Sample service charge
        requestDTO.setNotes("Please fix ASAP as it's difficult to study at night");

        return maintenanceRequestService.createRequest(requestDTO);
    }
}