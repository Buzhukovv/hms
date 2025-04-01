package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.HousingManagementRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a housing management staff user in the housing management system.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_housing_management")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("HOUSING_MANAGEMENT")
public class HousingManagement extends BaseUser {

    // Role type for Housing Management (MANAGER or BLOCK_MANAGER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HousingManagementRole role;

    // If role is BLOCK_MANAGER, specify which block they are responsible for
    private String block;
}
