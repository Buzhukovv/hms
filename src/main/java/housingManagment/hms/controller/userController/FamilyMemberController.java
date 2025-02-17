package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.service.userService.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/family-members")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    public FamilyMemberController(FamilyMemberService familyMemberService) {
        this.familyMemberService = familyMemberService;
    }

    @PostMapping
    public ResponseEntity<FamilyMember> createFamilyMember(@RequestBody FamilyMember familyMember) {
        FamilyMember created = familyMemberService.createFamilyMember(familyMember);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyMember> getFamilyMember(@PathVariable UUID id) {
        FamilyMember familyMember = familyMemberService.getFamilyMemberById(id);
        return ResponseEntity.ok(familyMember);
    }

    @GetMapping("/main/{mainUserId}")
    public ResponseEntity<List<FamilyMember>> getFamilyMembersByMainUser(@PathVariable UUID mainUserId) {
        List<FamilyMember> members = familyMemberService.getFamilyMembersByMainUserId(mainUserId);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamilyMember> updateFamilyMember(@PathVariable UUID id, @RequestBody FamilyMember familyMember) {
        FamilyMember updated = familyMemberService.updateFamilyMember(id, familyMember);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable UUID id) {
        familyMemberService.deleteFamilyMember(id);
        return ResponseEntity.noContent().build();
    }
}
