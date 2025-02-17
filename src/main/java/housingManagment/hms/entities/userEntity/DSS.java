package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_d_s_s")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DSS extends BaseUser {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentOfStudentServicesRole role;
}
