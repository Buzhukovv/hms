package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.repository.userRepository.BaseUserRepository;
import housingManagment.hms.repository.userRepository.FamilyMemberRepository;
import housingManagment.hms.service.userService.FamilyMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;
    private final BaseUserRepository baseUserRepository;

    public FamilyMemberServiceImpl(FamilyMemberRepository familyMemberRepository,
                                   BaseUserRepository baseUserRepository) {
        this.familyMemberRepository = familyMemberRepository;
        this.baseUserRepository = baseUserRepository;
    }

    /**
     * Создаёт нового члена семьи.
     * Проверяется, что основной пользователь существует и не является студентом.
     */
    @Override
    @Transactional
    public FamilyMember createFamilyMember(FamilyMember familyMember) {

        return familyMemberRepository.save(familyMember);
    }

    /**
     * Получение члена семьи по ID.
     */
    @Override
    public FamilyMember getFamilyMemberById(UUID id) {
        return familyMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Член семьи не найден"));
    }

    /**
     * Получение всех членов семьи по ID основного пользователя.
     */
    @Override
    public List<FamilyMember> getFamilyMembersByMainUserId(UUID mainUserId) {
        return familyMemberRepository.findByMainUserId(mainUserId);
    }

    /**
     * Обновление информации о члене семьи.
     */
    @Override
    @Transactional
    public FamilyMember updateFamilyMember(UUID id, FamilyMember updatedFamilyMember) {
        FamilyMember existing = getFamilyMemberById(id);
        existing.setFirstName(updatedFamilyMember.getFirstName());
        existing.setLastName(updatedFamilyMember.getLastName());
        existing.setMiddleName(updatedFamilyMember.getMiddleName());
        existing.setEmail(updatedFamilyMember.getEmail());
        existing.setLocalPhone(updatedFamilyMember.getLocalPhone());
        existing.setRelation(updatedFamilyMember.getRelation());
        return familyMemberRepository.save(existing);
    }

    /**
     * Удаление члена семьи по ID.
     */
    @Override
    @Transactional
    public void deleteFamilyMember(UUID id) {
        familyMemberRepository.deleteById(id);
    }
}
