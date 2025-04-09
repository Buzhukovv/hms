package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.FamilyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/family-members")
@Tag(name = "User Management")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;
    private final BaseUserService baseUserService;

    public FamilyMemberController(FamilyMemberService familyMemberService, BaseUserService baseUserService) {
        this.familyMemberService = familyMemberService;
        this.baseUserService = baseUserService;
    }

    @GetMapping("/")
    @Operation(summary = "Get all family members", description = "Returns a list of all family members")
    public ResponseEntity<List<FamilyMember>> getAllFamilyMembers(){
        List<FamilyMember> familyMembers = baseUserService.findAllByType(FamilyMember.class);
        return ResponseEntity.ok(familyMembers);
    }

    @PostMapping
    @Operation(summary = "Create family member", description = "Creates a new family member")
    public ResponseEntity<FamilyMember> createFamilyMember(@RequestBody FamilyMember familyMember) {
        FamilyMember created = familyMemberService.createFamilyMember(familyMember);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get family member by ID", description = "Returns the family member with the given ID")
    public ResponseEntity<FamilyMember> getFamilyMember(@PathVariable UUID id) {
        FamilyMember familyMember = familyMemberService.getFamilyMemberById(id);
        return ResponseEntity.ok(familyMember);
    }

    @GetMapping("/main/{mainUserId}")
    @Operation(summary = "Get family members by main user ID", description = "Returns a list of family members associated with the given main user ID")
    public ResponseEntity<List<FamilyMember>> getFamilyMembersByMainUser(@PathVariable UUID mainUserId) {
        List<FamilyMember> members = familyMemberService.getFamilyMembersByMainUserId(mainUserId);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update family member", description = "Updates the details of the family member with the given ID")
    public ResponseEntity<FamilyMember> updateFamilyMember(@PathVariable UUID id, @RequestBody FamilyMember familyMember) {
        FamilyMember updated = familyMemberService.updateFamilyMember(id, familyMember);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete family member", description = "Deletes the family member with the given ID")
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable UUID id) {
        familyMemberService.deleteFamilyMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search family members", description = "Searches for family members by name or last name using a keyword")
    public ResponseEntity<List<FamilyMember>> searchFamilyMembersByNameOrLastName(@RequestParam String keyword) {
        List<FamilyMember> familyMembers = baseUserService.findAllByType(FamilyMember.class).stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(familyMembers);
    }

    @GetMapping("/nuid/{nuid}")
    @Operation(summary = "Get User by NUID", description = "Fetches the user details for the given NUID")
    public ResponseEntity<BaseUser> getUserByNuid(@PathVariable int nuid) {
        BaseUser user = baseUserService.findByNuid(nuid);
        return ResponseEntity.ok(user);
    }
}
