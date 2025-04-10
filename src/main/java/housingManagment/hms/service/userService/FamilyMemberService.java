package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.entities.userEntity.Teacher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamilyMemberService {
    FamilyMember createFamilyMember(FamilyMember familyMember);
    FamilyMember getFamilyMemberById(UUID id);
    FamilyMember updateFamilyMember(UUID id, FamilyMember updatedFamilyMember);
    void deleteFamilyMember(UUID id);
    List<FamilyMember> findAll();
    FamilyMember changeRelation(UUID id, String relation);
}