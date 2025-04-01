package housingManagment.hms.entities.userEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_family_member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMember extends BaseUser {

    @Column(nullable = false)
    private String relation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_user_id", nullable = false)
    private BaseUser mainUser;
}
