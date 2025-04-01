package housingManagment.hms;

import housingManagment.hms.config.TestDataConfig;
import housingManagment.hms.dto.LeaseCreateDTO;
import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.entities.MaintenanceRequest;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.*;
import housingManagment.hms.entities.userEntity.*;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.enums.property.RoomTypeDormitory;
import housingManagment.hms.enums.property.OffCampusApartmentType;
import housingManagment.hms.enums.property.OnCampusApartmentType;

import housingManagment.hms.enums.userEnum.*;
import housingManagment.hms.repository.*;
import housingManagment.hms.repository.propertyRepository.*;
import housingManagment.hms.repository.userRepository.*;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.MaintenanceRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import housingManagment.hms.enums.userEnum.schools.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class populates the database with test data when the application starts.
 * Only active when the "default" profile is active to avoid loading test data
 * in production.
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
    private final TestDataConfig testDataConfig;
    private final LeaseRepository leaseRepository;
    private final Random random = new Random();

    // Reusable name arrays
    private static final String[] MALE_FIRST_NAMES = { "Aidar", "Alikhan", "Arman", "Aslan", "Bakhyt", "Daulet", "Erik",
            "Kairat", "Maksat", "Nursultan", "Arlan", "Aibarys" };
    private static final String[] FEMALE_FIRST_NAMES = { "Aigerim", "Aisha", "Alima", "Amina", "Dana", "Gulnara",
            "Karlygash", "Madina", "Nazgul", "Zarina" };
    private static final String[] LAST_NAMES = { "Abdullayev", "Alimzhanov", "Bekmukhanbetov", "Dauletov", "Ermekov",
            "Kazakhov", "Mukhamedov", "Nurpeisov", "Ospanov", "Suleimenov" };

    // School names mapping
    private static final Map<SchoolsAndSpecialties, String> SCHOOL_NAMES = Map.of(
            SchoolsAndSpecialties.SCHOOL_OF_ENGINEERING_AND_DIGITAL_SCIENCES,
            "School of Engineering and Digital Sciences",
            SchoolsAndSpecialties.SCHOOL_OF_MEDICINE, "School of Medicine",
            SchoolsAndSpecialties.SCHOOL_OF_SCIENCES_AND_HUMANITIES, "School of Sciences and Humanities",
            SchoolsAndSpecialties.GRADUATE_SCHOOL_OF_PUBLIC_POLICY, "Graduate School of Public Policy",
            SchoolsAndSpecialties.GRADUATE_SCHOOL_OF_BUSINESS, "Graduate School of Business",
            SchoolsAndSpecialties.GRADUATE_SCHOOL_OF_EDUCATION, "Graduate School of Education");

    // School to specialty mapping
    private static final Map<SchoolsAndSpecialties, Enum<?>[]> SCHOOL_SPECIALTIES = Map.of(
            SchoolsAndSpecialties.SCHOOL_OF_ENGINEERING_AND_DIGITAL_SCIENCES,
            SchoolOfEngineeringAndDigitalSciences.values(),
            SchoolsAndSpecialties.SCHOOL_OF_MEDICINE,
            SchoolOfMedicine.values(),
            SchoolsAndSpecialties.SCHOOL_OF_SCIENCES_AND_HUMANITIES,
            SchoolOfSciencesAndHumanities.values(),
            SchoolsAndSpecialties.GRADUATE_SCHOOL_OF_PUBLIC_POLICY,
            GraduateSchoolOfPublicPolicy.values(),
            SchoolsAndSpecialties.GRADUATE_SCHOOL_OF_BUSINESS,
            GraduateSchoolOfBusiness.values(),
            SchoolsAndSpecialties.GRADUATE_SCHOOL_OF_EDUCATION,
            GraduateSchoolOfEducation.values());

    @Override
    public void run(String... args) {
        try {
            if (!testDataConfig.isEnabled()) {
                log.info("Test data loading is disabled.");
                return;
            }

            log.info("Starting test data loading...");

            // Check and load each entity type independently
            try {
                if (shouldLoadEntity("dormitory rooms",
                        testDataConfig.isLoadDormitoryRooms(),
                        propertyRepository.findAll().stream().filter(prop -> prop instanceof DormitoryRoom).count(),
                        testDataConfig.getNumberOfStudents())) {
                    List<DormitoryRoom> dormitoryRooms = createDormitoryRooms();
                    log.info("Created {} dormitory rooms", dormitoryRooms.size());
                }
            } catch (Exception e) {
                log.error("Error loading dormitory rooms: {}", e.getMessage(), e);
            }

            try {
                if (shouldLoadEntity("cottages",
                        testDataConfig.isLoadCottages(),
                        propertyRepository.findAll().stream().filter(prop -> prop instanceof Cottage).count(),
                        testDataConfig.getNumberOfCottages())) {
                    List<Cottage> cottages = createCottages();
                    log.info("Created {} cottages", cottages.size());
                }
            } catch (Exception e) {
                log.error("Error loading cottages: {}", e.getMessage(), e);
            }

            if (shouldLoadEntity("off-campus apartments",
                    testDataConfig.isLoadOffCampusApartments(),
                    propertyRepository.findAll().stream().filter(prop -> prop instanceof OffCampusApartment).count(),
                    testDataConfig.getNumberOfOffCampusApartments())) {
                List<OffCampusApartment> offCampusApartments = createOffCampusApartments();
                log.info("Created {} off-campus apartments", offCampusApartments.size());
            }

            if (shouldLoadEntity("campus apartments",
                    testDataConfig.isLoadCampusApartments(),
                    propertyRepository.findAll().stream().filter(prop -> prop instanceof CampusApartment).count(),
                    testDataConfig.getNumberOfCampusApartments())) {
                List<CampusApartment> campusApartments = createCampusApartments();
                log.info("Created {} campus apartments", campusApartments.size());
            }

            if (shouldLoadEntity("townhouses",
                    testDataConfig.isLoadTownhouses(),
                    propertyRepository.findAll().stream().filter(prop -> prop instanceof Townhouse).count(),
                    testDataConfig.getNumberOfTownhouses())) {
                List<Townhouse> townhouses = createTownhouses();
                log.info("Created {} townhouses", townhouses.size());
            }

            if (shouldLoadEntity("students",
                    testDataConfig.isLoadStudents(),
                    userRepository.findAll().stream().filter(user -> user instanceof Student).count(),
                    testDataConfig.getNumberOfKazakhStudents())) {
                List<Student> students = createKazakhStudents();
                log.info("Created {} Kazakh students", students.size());
            }

            if (shouldLoadEntity("teachers",
                    testDataConfig.isLoadTeachers(),
                    userRepository.findAll().stream().filter(user -> user instanceof Teacher).count(),
                    testDataConfig.getNumberOfTeachers())) {
                List<Teacher> teachers = createTeachers();
                log.info("Created {} teachers", teachers.size());
            }

            if (shouldLoadEntity("maintenance staff",
                    testDataConfig.isLoadMaintenanceStaff(),
                    userRepository.findAll().stream().filter(user -> user instanceof Maintenance).count(),
                    testDataConfig.getNumberOfMaintenanceStaff())) {
                List<Maintenance> maintenanceStaff = createMaintenanceStaff();
                log.info("Created {} maintenance staff", maintenanceStaff.size());
            }

            if (shouldLoadEntity("housing management staff",
                    testDataConfig.isLoadHousingManagementStaff(),
                    userRepository.findAll().stream().filter(user -> user instanceof HousingManagement).count(),
                    testDataConfig.getNumberOfHousingManagementStaff())) {
                List<HousingManagement> housingManagementStaff = createHousingManagementStaff();
                log.info("Created {} housing management staff", housingManagementStaff.size());
            }

            if (shouldLoadEntity("DSS staff",
                    testDataConfig.isLoadDSSStaff(),
                    userRepository.findAll().stream().filter(user -> user instanceof DSS).count(),
                    testDataConfig.getNumberOfDSSStaff())) {
                List<DSS> dssStaff = createDSSStaff();
                log.info("Created {} DSS staff", dssStaff.size());
            }

            // For leases and maintenance requests, we need all other entities to be loaded
            // first
            if (shouldLoadEntity("leases",
                    testDataConfig.isLoadLeases(),
                    leaseRepository.findAll().size(),
                    testDataConfig.getNumberOfStudents() + testDataConfig.getNumberOfTeachers())) {
                List<Student> students = userRepository.findAll().stream()
                        .filter(user -> user instanceof Student)
                        .map(user -> (Student) user)
                        .toList();
                List<Teacher> teachers = userRepository.findAll().stream()
                        .filter(user -> user instanceof Teacher)
                        .map(user -> (Teacher) user)
                        .toList();
                List<DormitoryRoom> dormitoryRooms = propertyRepository.findAll().stream()
                        .filter(prop -> prop instanceof DormitoryRoom)
                        .map(prop -> (DormitoryRoom) prop)
                        .toList();
                List<BaseProperty> properties = propertyRepository.findAll().stream()
                        .filter(prop -> !(prop instanceof DormitoryRoom))
                        .toList();

                createLeases(students, dormitoryRooms, properties);
                createTeacherLeases(teachers, properties);
                log.info("Created leases for students and teachers");
            }

            if (shouldLoadEntity("maintenance requests",
                    testDataConfig.isLoadMaintenanceRequests(),
                    maintenanceRequestService.getAllRequests().size(),
                    leaseRepository.findAll().size() / 3)) {
                List<Student> students = userRepository.findAll().stream()
                        .filter(user -> user instanceof Student)
                        .map(user -> (Student) user)
                        .toList();
                createMaintenanceRequests(students);
                log.info("Created maintenance requests");
            }

        log.info("Test data loading complete!");
        } catch (Exception e) {
            log.error("Error loading test data: {}", e.getMessage(), e);
        }
    }

    private boolean shouldLoadEntity(String entityName, boolean isLoadEnabled, long currentCount, long requiredCount) {
        if (!isLoadEnabled) {
            log.info("Skipping {} creation as it is disabled in configuration", entityName);
            return false;
        }

        if (currentCount >= requiredCount) {
            log.info("Skipping {} creation as required count is met (current: {}, required: {})",
                    entityName, currentCount, requiredCount);
            return false;
        }

        log.info("Will create {} (current: {}, required: {})",
                entityName, currentCount, requiredCount);
        return true;
    }

    @Transactional
    private List<Cottage> createCottages() {
        List<Cottage> cottages = new ArrayList<>();
        Set<String> usedPropertyNumbers = new HashSet<>();

        log.info("Starting to create cottages...");

        for (int i = 0; i < testDataConfig.getNumberOfCottages(); i++) {
            Cottage cottage = new Cottage();

            // Generate unique property number
            String propertyNumber;
            do {
                propertyNumber = "COT-" + String.format("%03d", i + 1);
            } while (usedPropertyNumbers.contains(propertyNumber));

            usedPropertyNumbers.add(propertyNumber);
            cottage.setPropertyNumber(propertyNumber);
            cottage.setPropertyBlock("Cottage");
            cottage.setStatus(PropertyStatus.VACANT);
            cottage.setIsPaid(false);
            cottage.setMaxOccupant(1); // Cottages typically accommodate families
            cottage.setRent(250000.0); // Higher rent for cottages
            cottage.setDepositAmount(cottage.getRent());
            cottage.setArea(150.0 + random.nextInt(200));

            try {
                Cottage savedCottage = propertyRepository.save(cottage);
                cottages.add(savedCottage);
                log.info("Created cottage: {}", propertyNumber);
            } catch (Exception e) {
                log.error("Failed to create cottage {}: {}", propertyNumber, e.getMessage());
            }
        }

        return cottages;
    }

    @Transactional
    private List<OffCampusApartment> createOffCampusApartments() {
        List<OffCampusApartment> apartments = new ArrayList<>();
        Set<String> usedPropertyNumbers = new HashSet<>();

        log.info("Starting to create off-campus apartments...");

        String[] districts = { "Highvill", "NorthernLights" };
        String prefix = "";
        String street = "";

        OffCampusApartmentType[] apartmentTypes = OffCampusApartmentType.values();

        for (int i = 0; i < testDataConfig.getNumberOfOffCampusApartments(); i++) {
            OffCampusApartment apartment = new OffCampusApartment();

            String block = districts[random.nextInt(districts.length)];

            if (block.equals("Highvill")) {
                prefix = "HV";
                street = "Highvill Street";
                apartment.setOffCampusType(OffCampusApartmentType.HV_APARTMENT);
            } else if (block.equals("NorthernLights")) {
                prefix = "NL";
                street = "NorthernLights Street";
                apartment.setOffCampusType(OffCampusApartmentType.NL_APARTMENT);
            }

            // Generate unique property number
            String propertyNumber;
            do {
                propertyNumber = prefix + "-" + String.format("%03d", i + 1);
            } while (usedPropertyNumbers.contains(propertyNumber));

            usedPropertyNumbers.add(propertyNumber);
            apartment.setPropertyNumber(propertyNumber);
            apartment.setPropertyBlock(block);
            apartment.setStatus(PropertyStatus.VACANT);
            apartment.setIsPaid(false);
            apartment.setMaxOccupant(2 + random.nextInt(2)); // 2-3 occupants
            apartment.setRent(180000.0 + random.nextInt(70000)); // Rent between 180,000 and 250,000
            apartment.setDepositAmount(apartment.getRent());
            apartment.setArea(60.0 + random.nextInt(40)); // Area between 60 and 100 square meters
            apartment.setAddress(street);

            try {
                OffCampusApartment savedApartment = propertyRepository.save(apartment);
                apartments.add(savedApartment);
                log.info("Created off-campus apartment: {}", propertyNumber);
            } catch (Exception e) {
                log.error("Failed to create off-campus apartment {}: {}", propertyNumber, e.getMessage());
            }
        }

        return apartments;
    }

    @Transactional
    private List<Teacher> createTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();
        SchoolsAndSpecialties[] schools = SchoolsAndSpecialties.values();

        log.info("Starting to create teachers...");

        TeacherPosition[] positions = TeacherPosition.values();

        for (int i = 0; i < testDataConfig.getNumberOfTeachers(); i++) {
            Teacher teacher = new Teacher();

            boolean isMale = Math.random() < 0.5;
            String firstName = isMale ? MALE_FIRST_NAMES[random.nextInt(MALE_FIRST_NAMES.length)]
                    : FEMALE_FIRST_NAMES[random.nextInt(FEMALE_FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = String.format("%s.%s%d@nu.edu.kz", firstName.toLowerCase(), lastName.toLowerCase(), i);

            SchoolsAndSpecialties school = schools[random.nextInt(schools.length)];

            usedEmails.add(email);
            teacher.setEmail(email);
            teacher.setPassword("password123"); // In a real app, this would be hashed
            teacher.setFirstName(firstName);
            teacher.setLastName(lastName);
            teacher.setGender(isMale ? Gender.MALE : Gender.FEMALE);
            teacher.setSchool(school);
            teacher.setPosition(positions[random.nextInt(positions.length)]);
            teacher.setLocalPhone("+7" + (700 + random.nextInt(8)) + String.format("%06d", random.nextInt(1000000)));
            teacher.setNationalId(100000000 + random.nextInt(900000000));
            teacher.setNuid(202000000 + random.nextInt(999999));
            teacher.setIdentityDocNo(100000 + random.nextInt(900000));
            teacher.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                Teacher savedTeacher = userRepository.save(teacher);
                teachers.add(savedTeacher);
                log.info("Created teacher: {} {}", firstName, lastName);
            } catch (Exception e) {
                log.error("Failed to create teacher {}: {}", email, e.getMessage());
            }
        }

        return teachers;
    }

    @Transactional
    private List<Maintenance> createMaintenanceStaff() {
        List<Maintenance> maintenanceStaff = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        log.info("Starting to create maintenance staff...");

        for (int i = 0; i < testDataConfig.getNumberOfMaintenanceStaff(); i++) {
            Maintenance staff = new Maintenance();

            boolean isMale = Math.random() < 0.5;
            String firstName = isMale ? MALE_FIRST_NAMES[random.nextInt(MALE_FIRST_NAMES.length)]
                    : FEMALE_FIRST_NAMES[random.nextInt(FEMALE_FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = String.format("%s.%s%d@nu.edu.kz", firstName.toLowerCase(), lastName.toLowerCase(), i);

            usedEmails.add(email);
            staff.setEmail(email);
            staff.setPassword("password123"); // In a real app, this would be hashed
            staff.setFirstName(firstName);
            staff.setLastName(lastName);
            staff.setGender(isMale ? Gender.MALE : Gender.FEMALE);
            // Assign roles based on index: first staff is dispatcher, second is engineer,
            // rest are maintenance staff
            if (i == 0) {
                staff.setRole(MaintenanceRole.MAINTENANCE_DISPATCHER);
            } else if (i == 1) {
                staff.setRole(MaintenanceRole.MAINTENANCE_ENGINEER);
            } else {
                staff.setRole(MaintenanceRole.MAINTENANCE_STAFF);
            }
            staff.setLocalPhone("+7" + (700 + random.nextInt(8)) + String.format("%06d", random.nextInt(1000000)));
            staff.setNationalId(100000000 + random.nextInt(900000000));
            staff.setNuid(202000000 + random.nextInt(999999));
            staff.setIdentityDocNo(100000 + random.nextInt(900000));
            staff.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                Maintenance savedStaff = userRepository.save(staff);
                maintenanceStaff.add(savedStaff);
                log.info("Created maintenance staff: {} {}", firstName, lastName);
            } catch (Exception e) {
                log.error("Failed to create maintenance staff {}: {}", email, e.getMessage());
            }
        }

        return maintenanceStaff;
    }

    @Transactional
    private List<HousingManagement> createHousingManagementStaff() {
        List<HousingManagement> housingManagementStaff = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        log.info("Starting to create housing management staff...");

        // dorm blocks
        String[] blocks = { "D1", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D10" };

        for (int i = 0; i < testDataConfig.getNumberOfHousingManagementStaff(); i++) {
            HousingManagement staff = new HousingManagement();

            boolean isMale = Math.random() < 0.5;
            String firstName = isMale ? MALE_FIRST_NAMES[random.nextInt(MALE_FIRST_NAMES.length)]
                    : FEMALE_FIRST_NAMES[random.nextInt(FEMALE_FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = String.format("%s.%s%d@nu.edu.kz", firstName.toLowerCase(), lastName.toLowerCase(), i);

            usedEmails.add(email);
            staff.setEmail(email);
            staff.setPassword("password123"); // In a real app, this would be hashed
            staff.setFirstName(firstName);
            staff.setLastName(lastName);
            staff.setGender(isMale ? Gender.MALE : Gender.FEMALE);
            staff.setRole(i == 0 ? HousingManagementRole.MANAGER : HousingManagementRole.BLOCK_MANAGER);
            staff.setBlock(i == 0 ? null : blocks[random.nextInt(blocks.length)]);
            staff.setLocalPhone("+7" + (700 + random.nextInt(8)) + String.format("%06d", random.nextInt(1000000)));
            staff.setNationalId(100000000 + random.nextInt(900000000));
            staff.setNuid(202000000 + random.nextInt(999999));
            staff.setIdentityDocNo(100000 + random.nextInt(900000));
            staff.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                HousingManagement savedStaff = userRepository.save(staff);
                housingManagementStaff.add(savedStaff);
                log.info("Created housing management staff: {} {}", firstName, lastName);
            } catch (Exception e) {
                log.error("Failed to create housing management staff {}: {}", email, e.getMessage());
            }
        }

        return housingManagementStaff;
    }

    @Transactional
    private List<DSS> createDSSStaff() {
        List<DSS> dssStaff = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        log.info("Starting to create DSS staff...");

        for (int i = 0; i < testDataConfig.getNumberOfDSSStaff(); i++) {
            DSS staff = new DSS();

            boolean isMale = Math.random() < 0.5;
            String firstName = isMale ? MALE_FIRST_NAMES[random.nextInt(MALE_FIRST_NAMES.length)]
                    : FEMALE_FIRST_NAMES[random.nextInt(FEMALE_FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = String.format("%s.%s%d@nu.edu.kz", firstName.toLowerCase(), lastName.toLowerCase(), i);

            // Generate unique email
            usedEmails.add(email);
            staff.setEmail(email);
            staff.setPassword("password123"); // In a real app, this would be hashed
            staff.setFirstName(firstName);
            staff.setLastName(lastName);
            staff.setGender(isMale ? Gender.MALE : Gender.FEMALE);
            staff.setRole(i == 0 ? DepartmentOfStudentServicesRole.DSS_MANAGER
                    : DepartmentOfStudentServicesRole.DSS_ASSISTANT);
            staff.setLocalPhone("+7" + (700 + random.nextInt(8)) + String.format("%06d", random.nextInt(1000000)));
            staff.setNationalId(100000000 + random.nextInt(900000000));
            staff.setNuid(202000000 + random.nextInt(999999));
            staff.setIdentityDocNo(100000 + random.nextInt(900000));
            staff.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                DSS savedStaff = userRepository.save(staff);
                dssStaff.add(savedStaff);
                log.info("Created DSS staff: {} {}", firstName, lastName);
            } catch (Exception e) {
                log.error("Failed to create DSS staff {}: {}", email, e.getMessage());
            }
        }

        return dssStaff;
    }

    @Transactional
    private List<DormitoryRoom> createDormitoryRooms() {
        List<DormitoryRoom> dormitoryRooms = new ArrayList<>();
        Set<String> usedPropertyNumbers = new HashSet<>(); // Track used property numbers

        // Block names and their numerical aliases
        // D5(22), D6(23), D7(24), D8(25), D9(26), D10(27)
        String[] blockNames = { "D1", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D10" };
        int[] blockAliases = { 18, 20, 21, 22, 23, 24, 25, 26, 27 };

        log.info("Starting to create all possible dormitory rooms...");
        int failedRooms = 0;
        int totalRooms = 0;

        // Generate all possible rooms for each block
        for (int blockIndex = 0; blockIndex < blockNames.length; blockIndex++) {
            String blockName = blockNames[blockIndex];
            int blockAlias = blockAliases[blockIndex];

            log.info("Generating rooms for block {}", blockName);

            // For each floor (2-12)
            for (int floor = 2; floor <= 12; floor++) {
                // For each room on the floor (1-24)
                for (int roomNum = 1; roomNum <= 24; roomNum++) {
        DormitoryRoom room = new DormitoryRoom();

                    // Generate unique property number using block alias
                    String propertyNumber = String.format("%d.%02d%02d", blockAlias, floor, roomNum);

                    if (usedPropertyNumbers.contains(propertyNumber)) {
                        log.warn("Property number {} already exists, skipping", propertyNumber);
                        failedRooms++;
                        continue;
                    }

                    usedPropertyNumbers.add(propertyNumber);

                    room.setPropertyBlock(blockName);
                    room.setPropertyNumber(propertyNumber);

                    // D5 block contains three-bedded rooms
                    if (blockName.equals("D5")) {
                        room.setRoomType(RoomTypeDormitory.THREE_BEDDED_ROOM);
                        room.setMaxOccupant(3);
                        room.setRent(125000.0);
                    } else if (blockName.equals("D1") || blockName.equals("D3") || blockName.equals("D4")) { // d1,d3,d4
                        room.setRoomType(RoomTypeDormitory.FOUR_BEDDED_ROOM);
                        room.setMaxOccupant(4);
                        room.setRent(125000.0);
                    } else {
                        room.setRoomType(RoomTypeDormitory.TWO_BEDDED_ROOM);
                        room.setMaxOccupant(2);
                        room.setRent(125000.0);
                    }

        room.setStatus(PropertyStatus.VACANT);
        room.setIsPaid(false);
                    room.setDepositAmount(0.0);
                    room.setArea(20.0 + random.nextInt(10)); // Approximate dorm room area between 20-30 sq meters

                    try {
                        DormitoryRoom savedRoom = propertyRepository.save(room);
                        dormitoryRooms.add(savedRoom);
                        totalRooms++;

                        // Log progress every 100 rooms
                        if (totalRooms % 100 == 0) {
                            log.info("Created {} dormitory rooms so far...", totalRooms);
                        }
                    } catch (Exception e) {
                        log.error("Failed to create room with property number {}: {}", propertyNumber, e.getMessage());
                        failedRooms++;
                    }
                }
            }

            log.info("Completed generating rooms for block {}. Total rooms so far: {}", blockName, totalRooms);
        }

        log.info("Dormitory room creation complete. Successfully created {} rooms ({} failed).",
                dormitoryRooms.size(), failedRooms);
        return dormitoryRooms;
    }

    @Transactional
    private List<CampusApartment> createCampusApartments() {
        List<CampusApartment> campusApartments = new ArrayList<>();
        Set<String> usedPropertyNumbers = new HashSet<>();

        String[] blockNames = { "A1", "A2", "A3", "A4" };
        int[] blockAliases = { 38, 44, 49, 45 };

        log.info("Starting to create all possible campus apartments...");
        int failedApartments = 0;
        int totalApartments = 0;

        // Generate all possible apartments for each block
        for (int blockIndex = 0; blockIndex < blockNames.length; blockIndex++) {
            String blockName = blockNames[blockIndex];
            int blockAlias = blockAliases[blockIndex];
            OnCampusApartmentType[] apartmentTypes = OnCampusApartmentType.values();

            log.info("Generating apartments for block {}", blockName);

            // For each floor (2-12)
            for (int floor = 2; floor <= 12; floor++) {
                // For each apartment on the floor (1-12)
                for (int apartmentNum = 1; apartmentNum <= 6; apartmentNum++) {
                    CampusApartment apartment = new CampusApartment();

                    // Generate unique property number using block alias
                    String propertyNumber = String.format("%d.%02d%02d", blockAlias, floor, apartmentNum);

                    if (usedPropertyNumbers.contains(propertyNumber)) {
                        log.warn("Property number {} already exists, skipping", propertyNumber);
                        failedApartments++;
                        continue;
                    }

                    usedPropertyNumbers.add(propertyNumber);

                    apartment.setPropertyBlock(blockName);
                    apartment.setPropertyNumber(propertyNumber);

                    apartment.setMaxOccupant(1);
                    apartment.setArea(random.nextDouble() * (100.0 - 50.0) + 50.0);
                    apartment.setOnCampusApartmentType(apartmentTypes[random.nextInt(apartmentTypes.length)]);

                    apartment.setStatus(PropertyStatus.VACANT);
                    apartment.setIsPaid(false);
                    apartment.setDepositAmount(0.0);
                    apartment.setRent(180000.0);
                    try {
                        CampusApartment savedApartment = propertyRepository.save(apartment);
                        campusApartments.add(savedApartment);
                        totalApartments++;

                        // Log progress every 100 apartments
                        if (totalApartments % 100 == 0) {
                            log.info("Created {} campus apartments so far...", totalApartments);
                        }
                    } catch (Exception e) {
                        log.error("Failed to create apartment with property number {}: {}", propertyNumber,
                                e.getMessage());
                        failedApartments++;
                    }
                }
            }

            log.info("Completed generating apartments for block {}. Total apartments so far: {}", blockName,
                    totalApartments);
        }

        log.info("Campus apartment creation complete. Successfully created {} apartments ({} failed).",
                campusApartments.size(), failedApartments);
        return campusApartments;
    }

    @Transactional
    private List<Townhouse> createTownhouses() {
        List<Townhouse> townhouses = new ArrayList<>();
        Set<String> usedPropertyNumbers = new HashSet<>();

        String blockName = "37";
        String blockAliase = "Townhouse";

        log.info("Starting to create all possible townhouses...");
        int failedTownhouses = 0;
        int totalTownhouses = 0;
        int floor = 1;
        int maxUnits = 62;

        for (int unitNum = 1; unitNum <= maxUnits; unitNum++) {
            Townhouse townhouse = new Townhouse();

            String propertyNumber = String.format("%s.%02d%02d", blockAliase, floor, unitNum);

            if (usedPropertyNumbers.contains(propertyNumber)) {
                log.warn("Property number {} already exists, skipping", propertyNumber);
                failedTownhouses++;
                continue;
            }

            usedPropertyNumbers.add(propertyNumber);

            townhouse.setPropertyBlock(blockName);
            townhouse.setPropertyNumber(propertyNumber);

            townhouse.setMaxOccupant(1);
            townhouse.setArea(random.nextDouble() * (100.0 - 50.0) + 50.0);
            townhouse.setStatus(PropertyStatus.VACANT);
            townhouse.setIsPaid(false);
            townhouse.setDepositAmount(0.0);
            townhouse.setRent(350000.0);

            try {
                Townhouse savedTownhouse = propertyRepository.save(townhouse);
                townhouses.add(savedTownhouse);
                totalTownhouses++;

                if (totalTownhouses % 100 == 0) {
                    log.info("Created {} townhouses so far...", totalTownhouses);
                }
            } catch (Exception e) {
                log.error("Failed to create townhouse with property number {}: {}", propertyNumber, e.getMessage());
                failedTownhouses++;
            }
        }

        log.info("Townhouse creation complete. Successfully created {} townhouses ({} failed).",
                townhouses.size(), failedTownhouses);
        return townhouses;
    }

    @Transactional(noRollbackFor = Exception.class)
    private void createLeases(List<Student> students, List<DormitoryRoom> dormitoryRooms,
            List<BaseProperty> properties) {
        log.info("Starting to create leases for students...");
        int createdLeases = 0;
        int failedLeases = 0;

        // Check if we have properties and users to create leases
        if (dormitoryRooms.isEmpty()) {
            log.error("Cannot create leases: No dormitory rooms available");
            return;
        }

        if (students.isEmpty()) {
            log.error("Cannot create leases: No students available");
            return;
        }

        // Separate properties by type for students
        List<CampusApartment> campusApartments = properties.stream()
                .filter(p -> p instanceof CampusApartment)
                .map(p -> (CampusApartment) p)
                .toList();

        // Track room occupancy for dormitories and apartments
        Map<UUID, Integer> roomOccupancy = new HashMap<>();

        // Generate random start dates between last semester and next semester
        LocalDate startDateMin = LocalDate.now().minusMonths(6);
        LocalDate startDateMax = LocalDate.now().plusMonths(1);

        // Create student leases (80% dormitory, 20% campus apartments)
        int totalStudentLeases = Math.max(1, (int) (students.size() * testDataConfig.getOccupancy_by_students() / 100)); // 90%
                                                                                                                         // of
                                                                                                                         // students
                                                                                                                         // get
                                                                                                                         // housing
        int campusApartmentLeases = Math.min(
                (int) (totalStudentLeases * testDataConfig.getOccupancy_of_campus_apartments() / 100),
                campusApartments.size());
        int dormitoryLeases = totalStudentLeases - campusApartmentLeases;

        log.info("Planning to create {} dormitory leases and {} campus apartment leases for students",
                dormitoryLeases, campusApartmentLeases);

        // Create dormitory leases for students
        for (int i = 0; i < dormitoryLeases && i < students.size(); i++) {
            Student student = students.get(i);
            DormitoryRoom room = findAvailableRoom(dormitoryRooms, roomOccupancy);

            if (room == null) {
                log.warn("Could not find available dormitory room for student {}", student.getId());
                failedLeases++;
                continue;
            }

            try {
                createLeaseForStudent(student, room, startDateMin, startDateMax);
                createdLeases++;
                if (createdLeases % 100 == 0) {
                    log.info("Created {} dormitory leases so far...", createdLeases);
                }
            } catch (Exception e) {
                log.error("Failed to create lease for student {} in room {}: {}",
                        student.getId(), room.getPropertyNumber(), e.getMessage());
                failedLeases++;
            }
        }

        // Create campus apartment leases for remaining students
        for (int i = dormitoryLeases; i < dormitoryLeases + campusApartmentLeases && i < students.size(); i++) {
            Student student = students.get(i);
            if (campusApartments.isEmpty()) {
                break;
            }
            CampusApartment apartment = findAvailableApartment(campusApartments, roomOccupancy);

            if (apartment == null) {
                log.warn("Could not find available campus apartment for student {}", student.getId());
                failedLeases++;
                continue;
            }

            try {
                createLeaseForStudent(student, apartment, startDateMin, startDateMax);
                createdLeases++;
                if (createdLeases % 10 == 0) {
                    log.info("Created {} campus apartment leases so far...", createdLeases - dormitoryLeases);
                }
            } catch (Exception e) {
                log.error("Failed to create lease for student {} in apartment {}: {}",
                        student.getId(), apartment.getPropertyNumber(), e.getMessage());
                failedLeases++;
            }
        }

        log.info("Student lease creation complete. Successfully created {} leases ({} failed).",
                createdLeases, failedLeases);
    }

    private DormitoryRoom findAvailableRoom(List<DormitoryRoom> rooms, Map<UUID, Integer> roomOccupancy) {
        for (int attempts = 0; attempts < 50; attempts++) {
            if (rooms.isEmpty()) {
                return null;
            }
            DormitoryRoom candidate = rooms.get(random.nextInt(rooms.size()));
            int currentOccupancy = roomOccupancy.getOrDefault(candidate.getId(), 0);

            if (currentOccupancy < candidate.getMaxOccupant()) {
                roomOccupancy.put(candidate.getId(), currentOccupancy + 1);
                return candidate;
            }
        }
        return null;
    }

    private CampusApartment findAvailableApartment(List<CampusApartment> apartments, Map<UUID, Integer> roomOccupancy) {
        for (int attempts = 0; attempts < 50; attempts++) {
            if (apartments.isEmpty()) {
                return null;
            }
            CampusApartment candidate = apartments.get(random.nextInt(apartments.size()));
            int currentOccupancy = roomOccupancy.getOrDefault(candidate.getId(), 0);

            if (currentOccupancy < candidate.getMaxOccupant()) {
                roomOccupancy.put(candidate.getId(), currentOccupancy + 1);
                return candidate;
            }
        }
        return null;
    }

    @Transactional(noRollbackFor = Exception.class)
    private void createTeacherLeases(List<Teacher> teachers, List<BaseProperty> properties) {
        log.info("Starting to create leases for teachers...");
        int createdLeases = 0;
        int failedLeases = 0;

        if (properties.isEmpty()) {
            log.error("Cannot create leases: No properties available for teachers");
            return;
        }

        if (teachers.isEmpty()) {
            log.error("Cannot create leases: No teachers available");
            return;
        }

        // Separate properties by type
        List<Cottage> cottages = properties.stream()
                .filter(p -> p instanceof Cottage)
                .map(p -> (Cottage) p)
                .toList();

        List<OffCampusApartment> offCampusApartments = properties.stream()
                .filter(p -> p instanceof OffCampusApartment)
                .map(p -> (OffCampusApartment) p)
                .toList();

        List<Townhouse> townhouses = properties.stream()
                .filter(p -> p instanceof Townhouse)
                .map(p -> (Townhouse) p)
                .toList();

        if (cottages.isEmpty() && offCampusApartments.isEmpty() && townhouses.isEmpty()) {
            log.error("Cannot create leases: No suitable properties available for teachers");
            return;
        }

        // Generate random start dates
        LocalDate startDateMin = LocalDate.now().minusMonths(6);
        LocalDate startDateMax = LocalDate.now().plusMonths(1);

        // Assign properties to teachers (60% cottages, 40% off-campus apartments)
        int totalTeacherLeases = Math.min(teachers.size(), cottages.size() + offCampusApartments.size());
        int cottageLeases = Math.min((int) (totalTeacherLeases * 0.6), cottages.size());
        int offCampusLeases = totalTeacherLeases - cottageLeases;

        log.info("Planning to create {} cottage leases and {} off-campus apartment leases for teachers",
                cottageLeases, offCampusLeases);

        // Create cottage leases
        List<Cottage> availableCottages = new ArrayList<>(cottages);
        for (int i = 0; i < cottageLeases && i < teachers.size(); i++) {
            Teacher teacher = teachers.get(i);
            if (availableCottages.isEmpty()) {
                break;
            }
            Cottage cottage = availableCottages.get(random.nextInt(availableCottages.size()));
            availableCottages.remove(cottage); // Remove assigned cottage

            try {
                createLeaseForTeacher(teacher, cottage, startDateMin, startDateMax);
                createdLeases++;
                if (createdLeases % 10 == 0) {
                    log.info("Created {} cottage leases so far...", createdLeases);
                }
            } catch (Exception e) {
                log.error("Failed to create lease for teacher {} in cottage {}: {}",
                        teacher.getId(), cottage.getPropertyNumber(), e.getMessage(), e);
                failedLeases++;
            }
        }

        // Create off-campus apartment leases
        List<OffCampusApartment> availableApartments = new ArrayList<>(offCampusApartments);
        for (int i = cottageLeases; i < totalTeacherLeases && i < teachers.size(); i++) {
            Teacher teacher = teachers.get(i);
            if (availableApartments.isEmpty()) {
                break;
            }
            OffCampusApartment apartment = availableApartments.get(random.nextInt(availableApartments.size()));
            availableApartments.remove(apartment); // Remove assigned apartment

            try {
                createLeaseForTeacher(teacher, apartment, startDateMin, startDateMax);
                createdLeases++;
                if (createdLeases % 10 == 0) {
                    log.info("Created {} off-campus apartment leases so far...", createdLeases - cottageLeases);
                }
            } catch (Exception e) {
                log.error("Failed to create lease for teacher {} in apartment {}: {}",
                        teacher.getId(), apartment.getPropertyNumber(), e.getMessage());
                failedLeases++;
            }
        }

        // Create townhouse leases
        List<Townhouse> availableTownhouses = new ArrayList<>(townhouses);
        for (int i = cottageLeases + offCampusLeases; i < totalTeacherLeases && i < teachers.size(); i++) {
            Teacher teacher = teachers.get(i);
            if (availableTownhouses.isEmpty()) {
                break;
            }
            Townhouse townhouse = availableTownhouses.get(random.nextInt(availableTownhouses.size()));
            availableTownhouses.remove(townhouse); // Remove assigned townhouse

            try {
                createLeaseForTeacher(teacher, townhouse, startDateMin, startDateMax);
                createdLeases++;
                if (createdLeases % 10 == 0) {
                    log.info("Created {} townhouse leases so far...", createdLeases - cottageLeases - offCampusLeases);
                }
            } catch (Exception e) {
                log.error("Failed to create lease for teacher {} in townhouse {}: {}",
                        teacher.getId(), townhouse.getPropertyNumber(), e.getMessage());
                failedLeases++;
            }
        }
        log.info("Teacher lease creation complete. Successfully created {} leases ({} failed).",
                createdLeases, failedLeases);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    private void createLeaseForStudent(Student student, BaseProperty property, LocalDate startDateMin,
            LocalDate startDateMax) {
        try {
        LeaseCreateDTO leaseDTO = new LeaseCreateDTO();
            leaseDTO.setLeaseNumber("L" + System.currentTimeMillis() + "-" + random.nextInt(10000));
            leaseDTO.setPropertyId(property.getId());
        leaseDTO.setTenantId(student.getId());
        leaseDTO.setStatus("ACTIVE");

            // Generate random dates
            LocalDate startDate = startDateMin
                    .plusDays(random.nextInt((int) ChronoUnit.DAYS.between(startDateMin, startDateMax)));
            LocalDate endDate = startDate.plusMonths(random.nextInt(4) + 8); // 8-12 month lease

            leaseDTO.setStartDate(startDate);
            leaseDTO.setEndDate(endDate);
            leaseDTO.setCheckInDate(startDate);
            leaseDTO.setCheckOutDate(endDate);
            leaseDTO.setLeaseTerm(6);
            leaseDTO.setSecurityDeposit(0.0);
            leaseDTO.setMonthlyRent(Math.round(property.getRent() / 6 * 100.0) / 100.0);
            leaseDTO.setDeposit(property.getRent());
            leaseDTO.setReservationStatus(true);

            // Use a simpler note to avoid potential problems
            leaseDTO.setNotes("This lease is created for testing purposes");
            leaseDTO.setPenalties(0.0);

            leaseService.createLease(leaseDTO);
            log.info("Successfully created lease for student {} in property {}", student.getId(),
                    property.getPropertyNumber());
        } catch (Exception e) {
            log.error("Failed to create lease for student {} in property {}: {}",
                    student.getId(), property.getPropertyNumber(), e.getMessage(), e);
        }
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    private void createLeaseForTeacher(Teacher teacher, BaseProperty property, LocalDate startDateMin,
            LocalDate startDateMax) {
        try {
            LeaseCreateDTO leaseDTO = new LeaseCreateDTO();
            leaseDTO.setLeaseNumber("L" + System.currentTimeMillis() + "-" + random.nextInt(10000));
            leaseDTO.setPropertyId(property.getId());
            leaseDTO.setTenantId(teacher.getId());
            leaseDTO.setStatus("ACTIVE");

            // Generate random dates
            LocalDate startDate = startDateMin
                    .plusDays(random.nextInt((int) ChronoUnit.DAYS.between(startDateMin, startDateMax)));
            LocalDate endDate = startDate.plusMonths(random.nextInt(6) + 6); // 6-12 month lease

            leaseDTO.setStartDate(startDate);
            leaseDTO.setEndDate(endDate);
            leaseDTO.setCheckInDate(startDate);
            leaseDTO.setCheckOutDate(endDate);
        leaseDTO.setLeaseTerm(6);
            leaseDTO.setSecurityDeposit(0.0);
            leaseDTO.setMonthlyRent(Math.round(property.getRent() / 6 * 100.0) / 100.0);
            leaseDTO.setDeposit(property.getRent());
        leaseDTO.setReservationStatus(true);

            // Use a simpler note to avoid potential problems
            leaseDTO.setNotes("This lease is created for testing purposes");
            leaseDTO.setPenalties(0.0);

            leaseService.createLease(leaseDTO);
            log.info("Successfully created lease for teacher {} in property {}", teacher.getId(),
                    property.getPropertyNumber());
        } catch (Exception e) {
            log.error("Failed to create lease for teacher {} in property {}: {}",
                    teacher.getId(), property.getPropertyNumber(), e.getMessage(), e);
        }
    }

    @Transactional
    private void createMaintenanceRequests(List<Student> students) {
        log.info("Starting to create maintenance requests...");

        // Get active leases
        List<Lease> activeLeases = leaseService.getActiveLeases();
        if (activeLeases.isEmpty()) {
            log.warn("No active leases found. Cannot create maintenance requests.");
            return;
        }

        log.info("Found {} active leases to create maintenance requests for", activeLeases.size());

        // Create maintenance requests for approximately 30% of leases
        int targetRequests = activeLeases.size() / 3;
        Collections.shuffle(activeLeases);

        String[] requestTitles = {
                "Broken Light Fixture", "Leaking Faucet", "Clogged Drain", "Broken Furniture",
                "Heating Issue", "Window Won't Close", "Door Lock Problem", "Electrical Outlet Not Working",
                "Water Pressure Low", "AC Not Working", "Internet Connectivity Issues", "Broken Shower",
                "Toilet Clogged", "Pest Control Needed", "Paint Peeling", "Ceiling Leak"
        };

        String[] requestDescriptions = {
                "The light fixture in the room is not working properly.",
                "The kitchen faucet is leaking and wasting water.",
                "The sink drain is clogged and water is not going down properly.",
                "The desk chair is broken and needs to be replaced.",
                "The heating unit is not working properly in cold weather.",
                "The window doesn't close completely and lets in cold air.",
                "The door lock is difficult to open and may need replacement.",
                "The electrical outlet near the desk is not functioning.",
                "The water pressure in the shower is very low and needs adjustment.",
                "The air conditioning unit is not cooling properly.",
                "The internet connection keeps dropping and needs to be fixed.",
                "The shower head is broken and needs replacement.",
                "The toilet is clogged and requires plumbing service.",
                "I've noticed insects in the room and need pest control.",
                "The paint on the walls is peeling in several places.",
                "There's a water leak from the ceiling when it rains."
        };

        int createdRequests = 0;
        int failedRequests = 0;
        Set<String> usedLeaseIds = new HashSet<>(); // Track which leases already have maintenance requests

        for (int i = 0; i < targetRequests && i < activeLeases.size(); i++) {
            Lease lease = activeLeases.get(i);

            // Skip duplicate leases
            if (usedLeaseIds.contains(lease.getId().toString())) {
                continue;
            }

            usedLeaseIds.add(lease.getId().toString());

            try {
        MaintenanceRequestDTO requestDTO = new MaintenanceRequestDTO();

                int titleIndex = random.nextInt(requestTitles.length);
                requestDTO.setTitle(requestTitles[titleIndex]);
                requestDTO.setDescription(requestDescriptions[titleIndex]);

                requestDTO.setRequesterId(lease.getTenant().getId());
                requestDTO.setLeaseId(lease.getId());
                requestDTO.setServiceCharge(50.0 + random.nextInt(200));

                LocalDate randomDate = lease.getStartDate().plusDays(random.nextInt(30));
                // Set to random date if it's in the past, otherwise today
                LocalDate requestDate = randomDate.isBefore(LocalDate.now()) ? randomDate : LocalDate.now();

                // Add notes with dates
                requestDTO.setNotes("Reported on " + requestDate + ". Please fix ASAP.");

                // Add a sleep to ensure timestamp uniqueness in request number generation
                Thread.sleep(10);

                maintenanceRequestService.createRequest(requestDTO);
                createdRequests++;

                if (createdRequests % 50 == 0) {
                    log.info("Created {} maintenance requests so far...", createdRequests);
                }
            } catch (Exception e) {
                log.error("Failed to create maintenance request for lease {}: {}",
                        lease.getId(), e.getMessage());
                failedRequests++;

                // Skip to next lease if we encounter a constraint violation
                if (e.getMessage() != null && e.getMessage().contains("constraint")) {
                    continue;
                }
            }
        }

        log.info("Maintenance request creation complete. Successfully created {} requests ({} failed).",
                createdRequests, failedRequests);
    }

    @Transactional
    private List<Student> createKazakhStudents() {
        List<Student> students = new ArrayList<>();
        SchoolsAndSpecialties[] schools = SchoolsAndSpecialties.values();
        StudentRole[] roles = StudentRole.values();

        for (int i = 0; i < testDataConfig.getNumberOfKazakhStudents(); i++) {
            try {
                boolean isMale = Math.random() < 0.5;
                String firstName = isMale ? MALE_FIRST_NAMES[random.nextInt(MALE_FIRST_NAMES.length)]
                        : FEMALE_FIRST_NAMES[random.nextInt(FEMALE_FIRST_NAMES.length)];
                String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
                String email = String.format("%s.%s%d@nu.edu.kz", firstName.toLowerCase(), lastName.toLowerCase(), i);

                // Select random school and its corresponding specialty
                SchoolsAndSpecialties school = schools[random.nextInt(schools.length)];
                Enum<?>[] specialties = SCHOOL_SPECIALTIES.get(school);
                Enum<?> specialty = specialties[random.nextInt(specialties.length)];

                // Get the school name and specialty name
                String schoolName = SCHOOL_NAMES.get(school);
                String specialtyName = specialty.name();

                StudentRole role = roles[random.nextInt(roles.length)];

                Student student = new Student();
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setEmail(email);
                student.setSchool(school);
                student.setSpecialty(specialtyName);
                student.setRole(role);
                student.setGender(isMale ? Gender.MALE : Gender.FEMALE);
                student.setPassword("password123"); // In a real app, this would be hashed
                student.setLocalPhone(
                        "+7" + (700 + random.nextInt(300)) + String.format("%06d", random.nextInt(1000000)));
                student.setNationalId(100000000 + random.nextInt(900000000));
                student.setIdentityDocNo(100000 + random.nextInt(900000));
                student.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));
                student.setNuid(202000000 + random.nextInt(999999));
                Student savedStudent = userRepository.save(student);
                students.add(savedStudent);
                log.info("Created Kazakh student: {} {}", firstName, lastName);
            } catch (Exception e) {
                log.error("Error creating Kazakh student: {}", e.getMessage());
            }
        }

        return students;
    }
}