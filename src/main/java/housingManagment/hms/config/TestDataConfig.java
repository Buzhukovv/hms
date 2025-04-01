package housingManagment.hms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "test.data")
public class TestDataConfig {
    private boolean enabled = false;
    private boolean loadDormitoryRooms = true;
    private boolean loadOffCampusProperties = true;
    private boolean loadStudents = true;
    private boolean loadLeases = true;
    private boolean loadMaintenanceRequests = true;

    // New configuration options
    private boolean loadTeachers = true;
    private boolean loadMaintenanceStaff = true;
    private boolean loadHousingManagementStaff = true;
    private boolean loadDSSStaff = true;
    private boolean loadCottages = true;
    private boolean loadOffCampusApartments = true;
    private boolean loadCampusApartments = true;
    private boolean loadTownhouses = true;

    // Data generation settings
    private int numberOfStudents = 5000;
    private int numberOfKazakhStudents = 4000;
    private int numberOfTeachers = 100;
    private int numberOfMaintenanceStaff = 100;
    private int numberOfHousingManagementStaff = 100;
    private int numberOfDSSStaff = 50;
    private int numberOfCottages = 50;
    private int numberOfOffCampusApartments = 100;
    private int numberOfCampusApartments = 100;
    private int numberOfTownhouses = 100;

    private int occupancy_by_students = 900;
    private int occupancy_of_dormitory_rooms = 800;
    private int occupancy_of_campus_apartments = 200;
}