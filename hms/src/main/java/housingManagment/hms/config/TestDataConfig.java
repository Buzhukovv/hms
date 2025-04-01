package housingManagment.hms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "test.data")
public class TestDataConfig {
    private boolean enabled = true;
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
    private int numberOfStudents = 1000;
    private int numberOfKazakhStudents = 500;
    private int numberOfTeachers = 50;
    private int numberOfMaintenanceStaff = 20;
    private int numberOfHousingManagementStaff = 10;
    private int numberOfDSSStaff = 5;
    private int numberOfCottages = 5;
    private int numberOfOffCampusApartments = 10;
    private int numberOfCampusApartments = 10;
    private int numberOfTownhouses = 10;

    private int occupancy_by_students = 90;
    private int occupancy_of_dormitory_rooms = 80;
    private int occupancy_of_campus_apartments = 20;
}