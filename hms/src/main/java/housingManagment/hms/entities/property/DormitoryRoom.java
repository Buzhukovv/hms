package housingManagment.hms.entities.property;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.enums.property.RoomTypeDormitory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "property_dormitory_rooms")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("DormitoryRoom")
public class DormitoryRoom extends BaseProperty {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomTypeDormitory roomType;

    @Column(nullable = false)
    private Double area;

}
