package housingManagment.hms.entities.userEntity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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

    // Get the mainUser's ID safely
    public UUID getMainUserId() {
        if (mainUser == null) {
            throw new IllegalStateException("Main user is not set for this FamilyMember");
        }
        return mainUser.getId();
    }

    // Set the mainUser by fetching or creating a BaseUser instance
    public void setMainUser(BaseUser mainUser) {
        this.mainUser = mainUser;
    }
}