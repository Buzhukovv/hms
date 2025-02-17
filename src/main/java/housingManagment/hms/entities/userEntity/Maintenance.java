package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.MaintenanceRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance extends BaseUser {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceRole role;
}
