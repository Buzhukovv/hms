package housingManagment.hms.dto;

import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.entities.userEntity.Student;
import java.util.List;
import java.util.Map;

public class DashboardData {

    // Метрики
    private long studentCount;
    private long roomCount;
    private long leaseCount;
    private long maintenanceRequestCount;

    // Подробная статистика
    private Map<String, Long> userTypeCounts;
    private Map<String, Long> roomTypeCounts;
    private Map<String, Long> maintenanceStatusCounts;

    // Статусы комнат
    private long vacantRooms;
    private long partiallyOccupiedRooms;
    private long occupiedRooms;
    private long maintenanceRooms;
    private long reservedRooms;

    // Последние студенты
    private List<Student> latestStudents;

    // Последние заявки на обслуживание
    private List<MaintenanceRequestDTO> recentRequests;

    // Геттеры/сеттеры

    public long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(long studentCount) {
        this.studentCount = studentCount;
    }

    public long getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(long roomCount) {
        this.roomCount = roomCount;
    }

    public long getLeaseCount() {
        return leaseCount;
    }

    public void setLeaseCount(long leaseCount) {
        this.leaseCount = leaseCount;
    }

    public long getMaintenanceRequestCount() {
        return maintenanceRequestCount;
    }

    public void setMaintenanceRequestCount(long maintenanceRequestCount) {
        this.maintenanceRequestCount = maintenanceRequestCount;
    }

    public Map<String, Long> getUserTypeCounts() {
        return userTypeCounts;
    }

    public void setUserTypeCounts(Map<String, Long> userTypeCounts) {
        this.userTypeCounts = userTypeCounts;
    }

    public Map<String, Long> getRoomTypeCounts() {
        return roomTypeCounts;
    }

    public void setRoomTypeCounts(Map<String, Long> roomTypeCounts) {
        this.roomTypeCounts = roomTypeCounts;
    }

    public Map<String, Long> getMaintenanceStatusCounts() {
        return maintenanceStatusCounts;
    }

    public void setMaintenanceStatusCounts(Map<String, Long> maintenanceStatusCounts) {
        this.maintenanceStatusCounts = maintenanceStatusCounts;
    }

    public long getVacantRooms() {
        return vacantRooms;
    }

    public void setVacantRooms(long vacantRooms) {
        this.vacantRooms = vacantRooms;
    }

    public long getPartiallyOccupiedRooms() {
        return partiallyOccupiedRooms;
    }

    public void setPartiallyOccupiedRooms(long partiallyOccupiedRooms) {
        this.partiallyOccupiedRooms = partiallyOccupiedRooms;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public long getMaintenanceRooms() {
        return maintenanceRooms;
    }

    public void setMaintenanceRooms(long maintenanceRooms) {
        this.maintenanceRooms = maintenanceRooms;
    }

    public long getReservedRooms() {
        return reservedRooms;
    }

    public void setReservedRooms(long reservedRooms) {
        this.reservedRooms = reservedRooms;
    }

    public List<Student> getLatestStudents() {
        return latestStudents;
    }

    public void setLatestStudents(List<Student> latestStudents) {
        this.latestStudents = latestStudents;
    }

    public List<MaintenanceRequestDTO> getRecentRequests() {
        return recentRequests;
    }

    public void setRecentRequests(List<MaintenanceRequestDTO> recentRequests) {
        this.recentRequests = recentRequests;
    }
}
