package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.repository.userRepository.BaseUserRepository;
import housingManagment.hms.repository.userRepository.FamilyMemberRepository;
import housingManagment.hms.service.userService.FamilyMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;
    private final BaseUserRepository baseUserRepository;

    public FamilyMemberServiceImpl(FamilyMemberRepository familyMemberRepository,
                                   BaseUserRepository baseUserRepository) {
        this.familyMemberRepository = familyMemberRepository;
        this.baseUserRepository = baseUserRepository;
    }

    @Override
    @Transactional
    public FamilyMember createFamilyMember(FamilyMember familyMember) {
        if (familyMember == null) {
            throw new IllegalArgumentException("Family member cannot be null");
        }
        if (familyMember.getMainUserId() == null) {
            throw new IllegalArgumentException("Main user ID cannot be null");
        }
        baseUserRepository.findById(familyMember.getMainUserId())
                .orElseThrow(() -> new IllegalArgumentException("Main user with id " + familyMember.getMainUserId() + " not found"));
        return familyMemberRepository.save(familyMember);
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyMember getFamilyMemberById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return familyMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Family member with id " + id + " not found"));
    }


    @Override
    @Transactional
    public FamilyMember updateFamilyMember(UUID id, FamilyMember updatedFamilyMember) {
        if (id == null || updatedFamilyMember == null) {
            throw new IllegalArgumentException("ID and family member cannot be null");
        }
        FamilyMember existing = getFamilyMemberById(id);
        existing.setFirstName(updatedFamilyMember.getFirstName());
        existing.setLastName(updatedFamilyMember.getLastName());
        existing.setMiddleName(updatedFamilyMember.getMiddleName());
        existing.setEmail(updatedFamilyMember.getEmail());
        existing.setLocalPhone(updatedFamilyMember.getLocalPhone());
        existing.setRelation(updatedFamilyMember.getRelation());
        return familyMemberRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteFamilyMember(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        familyMemberRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilyMember> findAll() {
        return familyMemberRepository.findAll();
    }

    @Override
    @Transactional
    public FamilyMember changeRelation(UUID id, String relation) {
        if (id == null || relation == null || relation.trim().isEmpty()) {
            throw new IllegalArgumentException("ID and relation cannot be null or empty");
        }
        FamilyMember familyMember = getFamilyMemberById(id);
        familyMember.setRelation(relation);
        return familyMemberRepository.save(familyMember);
    }

}