package housingManagment.hms.entities.property;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.enums.property.OnCampusApartmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "property_campus_apartments")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CampusApartment")
public class CampusApartment extends BaseProperty {
    @Enumerated(EnumType.STRING)
    private OnCampusApartmentType onCampusApartmentType;

    @Column(nullable = false)
    private Double area;
}
