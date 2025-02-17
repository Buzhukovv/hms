package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.Gender;
import housingManagment.hms.enums.userEnum.StudentRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends BaseUser {
    
    // Тип студента: MASTER_STUDENT, BACHELOR_DEGREE, DOCTORAL_STUDENT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentRole role;
    
    @Column(nullable = false)
    private String school;
    
    @Column(nullable = false)
    private String specialty;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
}
