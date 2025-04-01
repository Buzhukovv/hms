package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a student user in the housing management system.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("STUDENT")
public class Student extends BaseUser {

    // Тип студента: MASTER_STUDENT, BACHELOR_DEGREE, DOCTORAL_STUDENT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchoolsAndSpecialties school;

    @Column(nullable = false)
    private String specialty;
}
