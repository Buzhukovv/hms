package housingManagment.hms.entities.property;

import housingManagment.hms.entities.Lease;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "property_townhouses")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Townhouse")
public class Townhouse extends BaseProperty {

    @Column(nullable = false)
    private Double area;
}
