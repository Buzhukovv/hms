package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.Gender;
import housingManagment.hms.enums.userEnum.TeacherPosition;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a teacher user in the housing management system.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("TEACHER")
public class Teacher extends BaseUser {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeacherPosition position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchoolsAndSpecialties school;
}
