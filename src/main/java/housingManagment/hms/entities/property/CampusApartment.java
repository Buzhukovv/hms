package housingManagment.hms.entities.property;

import housingManagment.hms.entities.Lease;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "property_campus_apartments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusApartment extends BaseProperty {

    @Column(nullable = false)
    private Double area;


}
