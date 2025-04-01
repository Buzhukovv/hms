package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {
    List<FamilyMember> findByMainUserId(UUID mainUserId);
}
