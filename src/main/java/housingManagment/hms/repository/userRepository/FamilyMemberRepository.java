package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {
    @Query("SELECT fm FROM FamilyMember fm LEFT JOIN FETCH fm.mainUser WHERE fm.mainUser.id = :mainUserId")
    List<FamilyMember> findByMainUserIdWithMainUser(@Param("mainUserId") UUID mainUserId);

    @Query("SELECT COUNT(fm) FROM FamilyMember fm WHERE fm.mainUser.id = :mainUserId")
    long countByMainUser(@Param("mainUserId") UUID mainUserId);


}
