package housingManagment.hms.entities.property;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.enums.property.OffCampusApartmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "property_off_campus_apartments")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Off_Campus")
public class OffCampusApartment extends BaseProperty {

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OffCampusApartmentType offCampusType;

    @Column(nullable = false)
    private Double area;
}
