package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.FamilyMember;
import java.util.List;
import java.util.UUID;

public interface FamilyMemberService {
    /**
     * Создаёт нового члена семьи.
     */
    FamilyMember createFamilyMember(FamilyMember familyMember);

    /**
     * Получение члена семьи по ID.
     */
    FamilyMember getFamilyMemberById(UUID id);

    /**
     * Получение всех членов семьи, привязанных к конкретному основному пользователю.
     */
    List<FamilyMember> getFamilyMembersByMainUserId(UUID mainUserId);

    /**
     * Обновление информации о члене семьи.
     */
    FamilyMember updateFamilyMember(UUID id, FamilyMember updatedFamilyMember);

    /**
     * Удаление члена семьи по ID.
     */
    void deleteFamilyMember(UUID id);
}
