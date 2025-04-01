package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a Department of Student Services staff user in the housing
 * management system.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_d_s_s")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("DSS")
public class DSS extends BaseUser {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentOfStudentServicesRole role;
}
