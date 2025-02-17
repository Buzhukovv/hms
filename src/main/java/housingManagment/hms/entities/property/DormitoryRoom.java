package housingManagment.hms.entities.property;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.enums.property.RoomTypeDormitory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "property_dormitory_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DormitoryRoom extends BaseProperty {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomTypeDormitory roomType;

}
