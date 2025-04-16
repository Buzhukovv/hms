package housingManagment.hms.dto;

import housingManagment.hms.enums.userEnum.Gender;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserListDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String localPhone;
    private String userType; // Student, Teacher, DSS, etc.
    private String role; // Specific role within the user type
    private String school; // For academic users
    private String specialty; // For students
    private Gender gender;
    private int nuid;
    private LocalDate identityIssueDate;
    private String status; // Active, Inactive, etc.
}