package housingManagment.hms.entities.property;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.enums.property.OffCampusApartmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "property_off_campus")
@DiscriminatorValue("OFF_CAMPUS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffCampusApartment extends BaseProperty {

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double area;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OffCampusApartmentType offCampusType;
}
