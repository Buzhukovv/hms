package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.MaintenanceRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a maintenance staff user in the housing management system.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("MAINTENANCE")
public class Maintenance extends BaseUser {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceRole role;
}
