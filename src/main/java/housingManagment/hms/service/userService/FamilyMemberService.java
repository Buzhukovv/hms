package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.FamilyMember;
import java.util.List;
import java.util.UUID;

public interface FamilyMemberService {

    FamilyMember createFamilyMember(FamilyMember familyMember);

    FamilyMember getFamilyMemberById(UUID id);

    List<FamilyMember> getFamilyMembersByMainUserId(UUID mainUserId);

    FamilyMember updateFamilyMember(UUID id, FamilyMember updatedFamilyMember);

    void deleteFamilyMember(UUID id);
}
