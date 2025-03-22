package housingManagment.hms;

import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.Gender;
import housingManagment.hms.enums.userEnum.HousingManagementRole;
import housingManagment.hms.enums.userEnum.MaintenanceRole;
import housingManagment.hms.repository.userRepository.HousingManagementRepository;
import housingManagment.hms.repository.userRepository.MaintenanceRepository;
import housingManagment.hms.repository.userRepository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class creates additional user types:
 * - 6 Block Manager users (HousingManagementRole.BLOCK_MANAGER)
 * - 3 Manager users (HousingManagementRole.MANAGER)
 * - 5 Maintenance Staff users (MAINTENANCE_STAFF)
 * - 2 Maintenance Manager users (MAINTENANCE_MANAGER)
 * - 100 Professor users (Teacher)
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("production") // Only run in production profile
@Order(3) // Run after other data loaders
public class UserDataCreator implements CommandLineRunner {

    private final HousingManagementRepository housingManagementRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final TeacherRepository teacherRepository;
    private final Random random = new Random();

    // Add a Set to track used NUID values across all user types
    private final Set<Integer> usedNuids = new HashSet<>();

    // Dormitory block names
    private final String[] blockNames = { "D5", "D6", "D7", "D8", "D9", "D10" };

    // Schools
    private final String[] schools = {
            "School of Engineering", "School of Medicine", "School of Sciences",
            "School of Law", "School of Business", "School of Arts",
            "School of Education", "School of Social Sciences", "School of Architecture",
            "School of Mathematics"
    };

    // Male and female names
    private final String[] maleFirstNames = {
            "James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Charles",
            "Christopher", "Daniel", "Matthew", "Anthony", "Mark", "Donald", "Steven", "Paul", "Andrew", "Kenneth"
    };

    private final String[] femaleFirstNames = {
            "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen",
            "Lisa", "Nancy", "Betty", "Margaret", "Sandra", "Ashley", "Kimberly", "Emily", "Donna", "Michelle"
    };

    private final String[] lastNames = {
            "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
            "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson"
    };

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Creating additional user types...");

        try {
            // Initialize the usedNuids set with existing NUIDs from the database
            initializeUsedNuids();

            // Create block manager users
            createBlockManagerUsers();

            // Create manager users
            createManagerUsers();

            // Create maintenance staff
            createMaintenanceStaffUsers();

            // Create maintenance managers
            createMaintenanceManagerUsers();

            // Create professor users
            createTeacherUsers();

            log.info("Additional user creation complete!");
        } catch (Exception e) {
            log.error("Error creating additional users: {}", e.getMessage(), e);
        }
    }

    /**
     * Initializes the usedNuids set with all existing NUIDs from the database
     * to prevent collisions when creating new users.
     */
    private void initializeUsedNuids() {
        log.info("Initializing used NUID tracking...");

        try {
            // Get all existing NUIDs from housing management users
            housingManagementRepository.findAll().forEach(user -> usedNuids.add(user.getNuid()));

            // Get all existing NUIDs from maintenance users
            maintenanceRepository.findAll().forEach(user -> usedNuids.add(user.getNuid()));

            // Get all existing NUIDs from teacher users
            teacherRepository.findAll().forEach(user -> usedNuids.add(user.getNuid()));

            log.info("Found {} existing NUIDs to avoid duplicates", usedNuids.size());
        } catch (Exception e) {
            log.error("Error initializing used NUIDs: {}", e.getMessage());
        }
    }

    @Transactional
    private void createBlockManagerUsers() {
        log.info("Creating Block Manager users...");
        List<HousingManagement> blockManagers = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        // Create 6 block managers, one for each block
        for (int i = 0; i < 6; i++) {
            HousingManagement blockManager = new HousingManagement();

            // Basic user information
            boolean isMale = random.nextBoolean();
            String firstName = isMale ? maleFirstNames[random.nextInt(maleFirstNames.length)]
                    : femaleFirstNames[random.nextInt(femaleFirstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            blockManager.setFirstName(firstName);
            blockManager.setLastName(lastName);

            // Generate unique email
            String email;
            do {
                email = (firstName.toLowerCase() + "." + lastName.toLowerCase() +
                        random.nextInt(1000) + "@housing.university.edu");
            } while (usedEmails.contains(email));
            usedEmails.add(email);
            blockManager.setEmail(email);

            // Set role and assigned block
            blockManager.setRole(HousingManagementRole.BLOCK_MANAGER);
            blockManager.setBlock(blockNames[i]);

            // Set other required fields
            blockManager.setPassword("password123"); // In a real app, this would be hashed
            blockManager.setLocalPhone("+1" + (300 + random.nextInt(600)) +
                    String.format("%07d", random.nextInt(10000000)));
            blockManager.setNationalId(100000000 + random.nextInt(900000000));

            // Generate unique NUID
            int nuid;
            do {
                nuid = 200000 + random.nextInt(99999);
            } while (usedNuids.contains(nuid));
            usedNuids.add(nuid);
            blockManager.setNuid(nuid);

            blockManager.setIdentityDocNo(100000 + random.nextInt(900000));
            blockManager.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                HousingManagement saved = housingManagementRepository.save(blockManager);
                blockManagers.add(saved);
                log.info("Created Block Manager for block {}: {}", blockManager.getBlock(),
                        blockManager.getFirstName() + " " + blockManager.getLastName());
            } catch (Exception e) {
                log.error("Failed to create Block Manager: {}", e.getMessage());
            }
        }

        log.info("Successfully created {} Block Manager users", blockManagers.size());
    }

    @Transactional
    private void createManagerUsers() {
        log.info("Creating Manager users...");
        List<HousingManagement> managers = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        // Create 3 manager users
        for (int i = 0; i < 3; i++) {
            HousingManagement manager = new HousingManagement();

            // Basic user information
            boolean isMale = random.nextBoolean();
            String firstName = isMale ? maleFirstNames[random.nextInt(maleFirstNames.length)]
                    : femaleFirstNames[random.nextInt(femaleFirstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            manager.setFirstName(firstName);
            manager.setLastName(lastName);

            // Generate unique email
            String email;
            do {
                email = (firstName.toLowerCase() + "." + lastName.toLowerCase() +
                        random.nextInt(1000) + "@housing.university.edu");
            } while (usedEmails.contains(email));
            usedEmails.add(email);
            manager.setEmail(email);

            // Set role (no block assignment for managers)
            manager.setRole(HousingManagementRole.MANAGER);

            // Set other required fields
            manager.setPassword("password123"); // In a real app, this would be hashed
            manager.setLocalPhone("+1" + (300 + random.nextInt(600)) +
                    String.format("%07d", random.nextInt(10000000)));
            manager.setNationalId(100000000 + random.nextInt(900000000));

            // Generate unique NUID
            int nuid;
            do {
                nuid = 200000 + random.nextInt(99999);
            } while (usedNuids.contains(nuid));
            usedNuids.add(nuid);
            manager.setNuid(nuid);

            manager.setIdentityDocNo(100000 + random.nextInt(900000));
            manager.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                HousingManagement saved = housingManagementRepository.save(manager);
                managers.add(saved);
                log.info("Created Manager: {}", manager.getFirstName() + " " + manager.getLastName());
            } catch (Exception e) {
                log.error("Failed to create Manager: {}", e.getMessage());
            }
        }

        log.info("Successfully created {} Manager users", managers.size());
    }

    @Transactional
    private void createMaintenanceStaffUsers() {
        log.info("Creating Maintenance Staff users...");
        List<Maintenance> maintenanceStaff = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        // Create 5 maintenance staff users
        for (int i = 0; i < 5; i++) {
            Maintenance staff = new Maintenance();

            // Basic user information
            boolean isMale = random.nextBoolean();
            String firstName = isMale ? maleFirstNames[random.nextInt(maleFirstNames.length)]
                    : femaleFirstNames[random.nextInt(femaleFirstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            staff.setFirstName(firstName);
            staff.setLastName(lastName);

            // Generate unique email
            String email;
            do {
                email = (firstName.toLowerCase() + "." + lastName.toLowerCase() +
                        random.nextInt(1000) + "@maintenance.university.edu");
            } while (usedEmails.contains(email));
            usedEmails.add(email);
            staff.setEmail(email);

            // Set role
            staff.setRole(MaintenanceRole.MAINTENANCE_STAFF);

            // Set other required fields
            staff.setPassword("password123"); // In a real app, this would be hashed
            staff.setLocalPhone("+1" + (300 + random.nextInt(600)) +
                    String.format("%07d", random.nextInt(10000000)));
            staff.setNationalId(100000000 + random.nextInt(900000000));

            // Generate unique NUID
            int nuid;
            do {
                nuid = 300000 + random.nextInt(99999);
            } while (usedNuids.contains(nuid));
            usedNuids.add(nuid);
            staff.setNuid(nuid);

            staff.setIdentityDocNo(100000 + random.nextInt(900000));
            staff.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                Maintenance saved = maintenanceRepository.save(staff);
                maintenanceStaff.add(saved);
                log.info("Created Maintenance Staff: {}", staff.getFirstName() + " " + staff.getLastName());
            } catch (Exception e) {
                log.error("Failed to create Maintenance Staff: {}", e.getMessage());
            }
        }

        log.info("Successfully created {} Maintenance Staff users", maintenanceStaff.size());
    }

    @Transactional
    private void createMaintenanceManagerUsers() {
        log.info("Creating Maintenance Manager users...");
        List<Maintenance> maintenanceManagers = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        // Create 2 maintenance manager users
        for (int i = 0; i < 2; i++) {
            Maintenance manager = new Maintenance();

            // Basic user information
            boolean isMale = random.nextBoolean();
            String firstName = isMale ? maleFirstNames[random.nextInt(maleFirstNames.length)]
                    : femaleFirstNames[random.nextInt(femaleFirstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            manager.setFirstName(firstName);
            manager.setLastName(lastName);

            // Generate unique email
            String email;
            do {
                email = (firstName.toLowerCase() + "." + lastName.toLowerCase() +
                        random.nextInt(1000) + "@maintenance.university.edu");
            } while (usedEmails.contains(email));
            usedEmails.add(email);
            manager.setEmail(email);

            // Set role
            manager.setRole(MaintenanceRole.MAINTENANCE_MANAGER);

            // Set other required fields
            manager.setPassword("password123"); // In a real app, this would be hashed
            manager.setLocalPhone("+1" + (300 + random.nextInt(600)) +
                    String.format("%07d", random.nextInt(10000000)));
            manager.setNationalId(100000000 + random.nextInt(900000000));

            // Generate unique NUID
            int nuid;
            do {
                nuid = 300000 + random.nextInt(99999);
            } while (usedNuids.contains(nuid));
            usedNuids.add(nuid);
            manager.setNuid(nuid);

            manager.setIdentityDocNo(100000 + random.nextInt(900000));
            manager.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                Maintenance saved = maintenanceRepository.save(manager);
                maintenanceManagers.add(saved);
                log.info("Created Maintenance Manager: {}", manager.getFirstName() + " " + manager.getLastName());
            } catch (Exception e) {
                log.error("Failed to create Maintenance Manager: {}", e.getMessage());
            }
        }

        log.info("Successfully created {} Maintenance Manager users", maintenanceManagers.size());
    }

    @Transactional
    private void createTeacherUsers() {
        log.info("Creating Teacher (Professor) users...");
        List<Teacher> teachers = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();

        // Create 100 professor users
        for (int i = 0; i < 100; i++) {
            Teacher teacher = new Teacher();

            // Basic user information
            boolean isMale = random.nextBoolean();
            String firstName = isMale ? maleFirstNames[random.nextInt(maleFirstNames.length)]
                    : femaleFirstNames[random.nextInt(femaleFirstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            teacher.setFirstName(firstName);
            teacher.setLastName(lastName);

            // Set gender
            teacher.setGender(isMale ? Gender.MALE : Gender.FEMALE);

            // Generate unique email
            String email;
            do {
                email = (firstName.toLowerCase() + "." + lastName.toLowerCase() +
                        random.nextInt(1000) + "@faculty.university.edu");
            } while (usedEmails.contains(email));
            usedEmails.add(email);
            teacher.setEmail(email);

            // Set school
            teacher.setSchool(schools[random.nextInt(schools.length)]);

            // Set other required fields
            teacher.setPassword("password123"); // In a real app, this would be hashed
            teacher.setLocalPhone("+1" + (300 + random.nextInt(600)) +
                    String.format("%07d", random.nextInt(10000000)));
            teacher.setNationalId(100000000 + random.nextInt(900000000));

            // Generate unique NUID
            int nuid;
            do {
                nuid = 400000 + random.nextInt(99999);
            } while (usedNuids.contains(nuid));
            usedNuids.add(nuid);
            teacher.setNuid(nuid);

            teacher.setIdentityDocNo(100000 + random.nextInt(900000));
            teacher.setIdentityIssueDate(LocalDate.now().minusYears(random.nextInt(5) + 1));

            try {
                Teacher saved = teacherRepository.save(teacher);
                teachers.add(saved);

                // Log progress every 10 teachers
                if ((i + 1) % 10 == 0) {
                    log.info("Created {} Teacher users so far...", i + 1);
                }
            } catch (Exception e) {
                log.error("Failed to create Teacher: {}", e.getMessage());
            }
        }

        log.info("Successfully created {} Teacher users", teachers.size());
    }
}