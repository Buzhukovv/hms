package housingManagment.hms.entities.userEntity;

import housingManagment.hms.enums.userEnum.HousingManagementRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_housing_management")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousingManagement extends BaseUser {
    
    // Тип роли для Housing Management (MANAGER или BLOCK_MANAGER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HousingManagementRole role;
    
    // Если роль BLOCK_MANAGER, то указываем, за какой блок отвечает
    private String block;
}
