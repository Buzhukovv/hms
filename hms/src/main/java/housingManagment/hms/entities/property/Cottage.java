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
@Table(name = "property_cottages")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Cottage")
public class Cottage extends BaseProperty {

    @Column(nullable = false)
    private Double area;
}
