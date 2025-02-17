package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher extends BaseUser {
    
    @Column(nullable = false)
    private String school;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
}
