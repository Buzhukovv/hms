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
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private final Random random = new Random();

    @Override
    public void run(String... args) {
        try {
            log.info("Starting Kazakh test data loading...");

            // Create dormitory rooms
            List<DormitoryRoom> dormitoryRooms = createDormitoryRooms();
            log.info("Created {} dormitory rooms", dormitoryRooms.size());

            // Create off-campus properties
            List<BaseProperty> offCampusProperties = createOffCampusProperties();
            log.info("Created {} off-campus properties", offCampusProperties.size());

            // Create Kazakh students
            List<Student> students = createKazakhStudents();
            log.info("Created {} Kazakh students", students.size());

            // Create leases
            createLeases(students, dormitoryRooms, offCampusProperties);
            log.info("Created leases for students");

            // Create maintenance requests
            createMaintenanceRequests(students);
            log.info("Created maintenance requests");

            log.info("Kazakh test data loading complete!");
        } catch (Exception e) {
            log.error("Error loading Kazakh test data: {}", e.getMessage(), e);
        }
    }

    @Transactional
    private List<BaseProperty> createOffCampusProperties() {
        List<BaseProperty> offCampusProperties = new ArrayList<>();

        log.info("Starting to create 10 off-campus properties...");

        // Sample districts
        String[] districts = { "Yesil", "Saryarka", "Almaty", "Baiterek" };

        for (int i = 0; i < 10; i++) {
            // Using CampusApartment instead of OffCampusApartment for simplicity
            housingManagment.hms.entities.property.CampusApartment property = new housingManagment.hms.entities.property.CampusApartment();

            // Set unique property number
            String propertyNumber = "OC-" + (i + 1);
            property.setPropertyNumber(propertyNumber);

            // Set property block using the district
            property.setPropertyBlock(districts[random.nextInt(districts.length)]);

            // Set status to available
            property.setStatus(PropertyStatus.VACANT);
            property.setIsPaid(false);

            // Set capacity (1-3 bedrooms)
            int bedrooms = random.nextInt(3) + 1;
            property.setMaxOccupant(bedrooms + 1); // Bedrooms + living room

            // Set rent based on number of bedrooms
            double baseRent = 150000.0; // Base rent for 1 bedroom
            property.setRent(baseRent * bedrooms);

            // Set deposit amount
            property.setDepositAmount(property.getRent());

            try {
                BaseProperty savedProperty = propertyRepository.save(property);
                offCampusProperties.add(savedProperty);
                log.info("Created off-campus property: {}", propertyNumber);
            } catch (Exception e) {
                log.error("Failed to create off-campus property {}: {}", propertyNumber, e.getMessage());
            }
        }

        log.info("Off-campus property creation complete. Created {} properties.", offCampusProperties.size());
        return offCampusProperties;
    }

    @Transactional
    private List<Student> createKazakhStudents() {
        // Male first names (Kazakh)
        String[] maleFirstNames = {
                "Arman", "Nurlan", "Serik", "Marat", "Bolat", "Almas", "Yerlan", "Timur", "Galym", "Yerbol",
                "Daulet", "Nursultan", "Maksat", "Berik", "Daniyar", "Askhat", "Zhanbolat", "Yerzhan", "Azamat",
                "Erkin", "Nurzhan", "Kairat", "Adil", "Aibek", "Bakhytzhan", "Talgat", "Askar", "Zhanat", "Daryn",
                "Samat",
                "Yernar", "Amanzhol", "Olzhas", "Darkhan", "Kanat", "Rustem", "Bakyt", "Erlan", "Bauyrzhan", "Madiyar"
        };

        // Female first names (Kazakh)
        String[] femaleFirstNames = {
                "Aizhan", "Ainur", "Gulnara", "Saltanat", "Madina", "Saule", "Zhanar", "Dana", "Dinara", "Asel",
                "Gaukhar", "Zarina", "Aliya", "Aigerim", "Gulzhan", "Nazgul", "Almagul", "Zhuldyz", "Karlygash",
                "Gulnar", "Aizat", "Kamila", "Ainagul", "Moldir", "Sandugash", "Gulden", "Symbat", "Aidana", "Gulnaz",
                "Togzhan",
                "Aruzhan", "Aizada", "Akbota", "Bibigul", "Dariga", "Bagila", "Laila", "Makpal", "Sholpan", "Aiman"
        };

        // Last names (Kazakh)
        String[] lastNames = {
                "Akhmetov", "Aliyev", "Amanov", "Bekmuratov", "Dosanov", "Esimov", "Gabdullin", "Iskakov", "Kasenov",
                "Kurmanov", "Mukhamedzanov", "Nazarbayev", "Nurpeisov", "Omarov", "Ospanov", "Rakhimov", "Suleimenov",
                "Tulegenov",
                "Usenov", "Zhakiyanov", "Abdulin", "Baitursynov", "Erzhanov", "Ibrahimov", "Karimov", "Madirov",
                "Nurgaliyev", "Satpayev",
                "Tazhin", "Zhumabayev", "Asanov", "Baitov", "Daulethanov", "Ivanov", "Kaliyev", "Moldakhmetov",
                "Nurkeev", "Sadykov",
                "Turysbek", "Zhunussov", "Bazarbayev", "Dulatov", "Imanov", "Kadyrov", "Mukhamedjanov", "Nurtazin",
                "Sarsenov", "Ualikhanov",
                "Yerzhanov", "Zhumagaliyev"
        };

        // Schools
        String[] schools = {
                "School of Engineering", "School of Medicine", "School of Sciences",
                "School of Law", "School of Business", "School of Arts"
        };

        // Specialties by school
        Map<String, String[]> specialtiesBySchool = new HashMap<>();
        specialtiesBySchool.put("School of Engineering", new String[] {
                "Computer Science", "Electrical Engineering", "Mechanical Engineering", "Civil Engineering",
                "Chemical Engineering"
        });
        specialtiesBySchool.put("School of Medicine", new String[] {
                "General Medicine", "Dentistry", "Pharmacy", "Nursing", "Public Health"
        });
        specialtiesBySchool.put("School of Sciences", new String[] {
                "Mathematics", "Physics", "Chemistry", "Biology", "Environmental Science"
        });
        specialtiesBySchool.put("School of Law", new String[] {
                "Criminal Law", "Civil Law", "International Law", "Business Law", "Environmental Law"
        });
        specialtiesBySchool.put("School of Business", new String[] {
                "Finance", "Marketing", "Management", "Accounting", "Economics"
        });
        specialtiesBySchool.put("School of Arts", new String[] {
                "Fine Arts", "Music", "Theater", "Design", "Film Studies"
        });

        // Student roles
        StudentRole[] roles = StudentRole.values();

        List<Student> students = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();
        int failedStudents = 0;

        log.info("Starting to create Kazakh students...");

        // Create 1000 students
        for (int i = 0; i < 1000; i++) {
            Student student = new Student();

            // Set basic info
            boolean isMale = random.nextBoolean();
            String firstName;
            if (isMale) {
                firstName = maleFirstNames[random.nextInt(maleFirstNames.length)];
                student.setGender(Gender.MALE);
            } else {
                firstName = femaleFirstNames[random.nextInt(femaleFirstNames.length)];
                student.setGender(Gender.FEMALE);
            }

            // Get a random last name
            String lastName = lastNames[random.nextInt(lastNames.length)];
            // For female students, change last name ending from "-ov" or "-ev" to "-ova" or
            // "-eva"
            if (!isMale) {
                if (lastName.endsWith("ov")) {
                    lastName = lastName + "a";
                } else if (lastName.endsWith("ev")) {
                    lastName = lastName + "a";
                }
            }

            student.setFirstName(firstName);
            student.setLastName(lastName);

            // Generate a unique email
            String email;
            int emailAttempts = 0;
            do {
                email = (firstName + "." + lastName + random.nextInt(1000) + "@university.edu").toLowerCase();
                emailAttempts++;
                if (emailAttempts > 50) {
                    log.warn("Too many attempts to generate unique email. Skipping this student.");
                    break;
                }
            } while (usedEmails.contains(email));

            if (emailAttempts > 50) {
                failedStudents++;
                continue;
            }

            usedEmails.add(email);
            student.setEmail(email);

            // Set password
            student.setPassword("password123"); // In a real app, this would be hashed

            // Set phone number
            student.setLocalPhone("+7" + (700 + random.nextInt(300)) + String.format("%06d", random.nextInt(1000000)));

            // Set role
            student.setRole(roles[random.nextInt(roles.length)]);

            // Set school
            String school = schools[random.nextInt(schools.length)];
            student.setSchool(school);

            // Set specialty based on school
            String[] specialties = specialtiesBySchool.get(school);
            student.setSpecialty(specialties[random.nextInt(specialties.length)]);

            // Required BaseUser fields
            student.setNationalId(100000000 + random.nextInt(900000000));

            // Format NUID as "202011322" with the last 6 digits changing
            student.setNuid(Integer.parseInt("202" + String.format("%06d", random.nextInt(999999))));

            student.setIdentityDocNo(100000 + random.nextInt(900000));
            student.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                Student savedStudent = userRepository.save(student);
                students.add(savedStudent);

                // Log progress every 100 students
                if (students.size() % 100 == 0) {
                    log.info("Created {} students so far...", students.size());
                }
            } catch (Exception e) {
                log.error("Failed to create student with email {}: {}", email, e.getMessage());
                failedStudents++;
            }
        }

        log.info("Successfully created {} Kazakh students ({} failed).", students.size(), failedStudents);
        return students;
    }

    @Transactional
    private List<DormitoryRoom> createDormitoryRooms() {
        List<DormitoryRoom> dormitoryRooms = new ArrayList<>();
        Set<String> usedPropertyNumbers = new HashSet<>(); // Track used property numbers

        // Block names and their numerical aliases
        // D5(22), D6(23), D7(24), D8(25), D9(26), D10(27)
        String[] blockNames = { "D5", "D6", "D7", "D8", "D9", "D10" };
        int[] blockAliases = { 22, 23, 24, 25, 26, 27 };

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
                    } else {
                        room.setRoomType(RoomTypeDormitory.TWO_BEDDED_ROOM);
                        room.setMaxOccupant(2);
                        room.setRent(125000.0);
                    }

                    room.setStatus(PropertyStatus.VACANT);
                    room.setIsPaid(false);
                    room.setDepositAmount(0.0);

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
    private void createLeases(List<Student> students, List<DormitoryRoom> dormitoryRooms,
                              List<BaseProperty> offCampusProperties) {
        log.info("Starting to create leases for students...");
        int createdLeases = 0;
        int failedLeases = 0;

        // Check if we have rooms and students to create leases
        if (dormitoryRooms.isEmpty()) {
            log.error("Cannot create leases: No dormitory rooms available");
            return;
        }

        if (students.isEmpty()) {
            log.error("Cannot create leases: No students available");
            return;
        }

        // Choose a subset of students to assign leases to
        // In a dormitory, we can have multiple students in a room, but limit to max
        // occupants
        Map<UUID, Integer> roomOccupancy = new HashMap<>();

        // Generate random start dates between last semester and next semester
        LocalDate startDateMin = LocalDate.now().minusMonths(6);
        LocalDate startDateMax = LocalDate.now().plusMonths(1);

        // 80% of students get dormitory assignments, 10% get off-campus housing, 10% no
        // housing
        int totalToAssign = Math.max(1, (int) (students.size() * 0.9));
        int offCampusAssignments = Math.min(totalToAssign - 1, (int) (students.size() * 0.1));

        // Ensure we have a positive number of dormitory assignments
        int dormitoryAssignments = totalToAssign - offCampusAssignments;
        if (dormitoryAssignments <= 0) {
            dormitoryAssignments = 1; // Ensure at least one dormitory assignment
        }

        log.info("Planning to create {} dormitory leases and {} off-campus leases",
                dormitoryAssignments, offCampusAssignments);

        // Create dormitory leases
        for (int i = 0; i < dormitoryAssignments && i < students.size(); i++) {
            Student student = students.get(i);

            // Find a room that isn't full
            DormitoryRoom room = null;
            for (int attempts = 0; attempts < 50; attempts++) {
                if (dormitoryRooms.isEmpty()) {
                    break;
                }
                DormitoryRoom candidate = dormitoryRooms.get(random.nextInt(dormitoryRooms.size()));
                int currentOccupancy = roomOccupancy.getOrDefault(candidate.getId(), 0);

                if (currentOccupancy < candidate.getMaxOccupant()) {
                    room = candidate;
                    roomOccupancy.put(room.getId(), currentOccupancy + 1);
                    break;
                }
            }

            if (room == null) {
                log.warn("Could not find available room for student {}", student.getId());
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

        // Create off-campus leases
        if (offCampusProperties.isEmpty()) {
            log.warn("No off-campus properties available for lease assignments");
        } else {
            for (int i = 0; i < offCampusAssignments
                    && totalToAssign - offCampusAssignments + i < students.size(); i++) {
                Student student = students.get(totalToAssign - offCampusAssignments + i);
                BaseProperty property = offCampusProperties.get(i % offCampusProperties.size());

                try {
                    createLeaseForStudent(student, property, startDateMin, startDateMax);
                    createdLeases++;

                    if ((createdLeases - dormitoryAssignments) % 10 == 0) {
                        log.info("Created {} off-campus leases so far...",
                                createdLeases - dormitoryAssignments);
                    }
                } catch (Exception e) {
                    log.error("Failed to create lease for student {} in property {}: {}",
                            student.getId(), property.getPropertyNumber(), e.getMessage());
                    failedLeases++;
                }
            }
        }

        log.info("Lease creation complete. Successfully created {} leases ({} failed).",
                createdLeases, failedLeases);
    }

    private void createLeaseForStudent(Student student, BaseProperty room, LocalDate startDateMin,
                                       LocalDate startDateMax) {
        LeaseCreateDTO leaseDTO = new LeaseCreateDTO();

        // Generate a unique lease number
        leaseDTO.setLeaseNumber("L" + System.currentTimeMillis() + "-" + random.nextInt(10000));

        leaseDTO.setPropertyId(room.getId());
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

        leaseDTO.setMonthlyRent(room.getRent());
        leaseDTO.setSecurityDeposit(room.getDepositAmount());
        leaseDTO.setLeaseTerm((int) ChronoUnit.MONTHS.between(startDate, endDate));
        leaseDTO.setTerms("Standard lease terms");
        leaseDTO.setContractNumber("C" + System.currentTimeMillis() + "-" + random.nextInt(10000));
        leaseDTO.setReservationStatus(true);
        leaseDTO.setDeposit(room.getDepositAmount());
        leaseDTO.setFamilyMembers(new ArrayList<>()); // No family members for students

        leaseService.createLease(leaseDTO);
    }

    @Transactional
    private void createMaintenanceRequests(List<Student> students) {
        log.info("Starting to create maintenance requests...");

        // Get active leases
        List<housingManagment.hms.entities.Lease> activeLeases = leaseService.getActiveLeases();
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

        for (int i = 0; i < targetRequests && i < activeLeases.size(); i++) {
            housingManagment.hms.entities.Lease lease = activeLeases.get(i);

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

                maintenanceRequestService.createRequest(requestDTO);
                createdRequests++;

                if (createdRequests % 50 == 0) {
                    log.info("Created {} maintenance requests so far...", createdRequests);
                }
            } catch (Exception e) {
                log.error("Failed to create maintenance request for lease {}: {}",
                        lease.getId(), e.getMessage());
                failedRequests++;
            }
        }

        log.info("Maintenance request creation complete. Successfully created {} requests ({} failed).",
                createdRequests, failedRequests);
    }
}