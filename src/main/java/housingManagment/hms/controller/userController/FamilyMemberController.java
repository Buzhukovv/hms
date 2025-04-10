package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.repository.userRepository.FamilyMemberRepository;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.userService.FamilyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/family-member")
@RequiredArgsConstructor
@Tag(name = "Family Member Management", description = "APIs for managing family member information")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;
    private final LeaseService leaseService;
    private final FamilyMemberRepository familyMemberRepository;

    @GetMapping
    @Operation(summary = "Get all family members", description = "Returns a list of all family members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family members retrieved successfully")
    })
    public ResponseEntity<List<FamilyMember>> getAllFamilyMembers() {
        List<FamilyMember> familyMembers = familyMemberService.findAll();
        return ResponseEntity.ok(familyMembers);
    }

    @PostMapping
    @Operation(summary = "Create family member", description = "Creates a new family member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family member created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Family member already exists")
    })
    public ResponseEntity<FamilyMember> createFamilyMember(@RequestBody FamilyMember familyMember) {
        FamilyMember created = familyMemberService.createFamilyMember(familyMember);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get family member by ID", description = "Returns the family member with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family member found"),
            @ApiResponse(responseCode = "404", description = "Family member not found")
    })
    public ResponseEntity<FamilyMember> getFamilyMember(@PathVariable UUID id) {
        FamilyMember familyMember = familyMemberService.getFamilyMemberById(id);
        return familyMember != null ? ResponseEntity.ok(familyMember) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-main-user")
    @Operation(summary = "Get family members by main user ID", description = "Returns a list of family members associated with the given main user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family members found")
    })
    public ResponseEntity<List<FamilyMember>> getFamilyMembersByMainUser(@RequestParam BaseUser mainUser) {
        List<FamilyMember> members = familyMemberRepository.findByMainUserIdWithMainUser(mainUser.getId());
        return ResponseEntity.ok(members);
    }

    @GetMapping("/count-by-main-user")
    @Operation(summary = "Get family members COUNT by main user ID", description = "Returns an Integer of family members associated with the given main user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family members found")
    })
    public ResponseEntity<Long> countByMainUser(@RequestParam BaseUser mainUser) {
        long count = familyMemberRepository.countByMainUser(mainUser.getId());
        return ResponseEntity.ok(count);
    }



    @PutMapping("/{id}")
    @Operation(summary = "Update family member", description = "Updates the details of the family member with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family member updated successfully"),
            @ApiResponse(responseCode = "404", description = "Family member not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<FamilyMember> updateFamilyMember(@PathVariable UUID id, @RequestBody FamilyMember familyMember) {
        FamilyMember updated = familyMemberService.updateFamilyMember(id, familyMember);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete family member", description = "Deletes the family member with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family member deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Family member not found")
    })
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable UUID id) {
        familyMemberService.deleteFamilyMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search family members", description = "Searches for family members by name or last name using a keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family members found")
    })
    public ResponseEntity<List<FamilyMember>> searchFamilyMembersByNameOrLastName(@RequestParam String keyword) {
        List<FamilyMember> familyMembers = familyMemberService.findAll().stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(familyMembers);
    }

    @GetMapping("/{id}/leases")
    @Operation(summary = "Get family member's leases", description = "Retrieves the leases associated with a family member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leases retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Family member not found")
    })
    public ResponseEntity<List<Lease>> getLeases(@PathVariable UUID id) {
        List<Lease> leases = leaseService.getLeasesByTenant(id);
        return ResponseEntity.ok(leases);
    }

    @PatchMapping("/{id}/change-relation")
    @Operation(summary = "Change family member's relation", description = "Updates the relation of a family member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relation updated successfully"),
            @ApiResponse(responseCode = "404", description = "Family member not found"),
            @ApiResponse(responseCode = "400", description = "Invalid relation")
    })
    public ResponseEntity<FamilyMember> changeRelation(@PathVariable UUID id, @RequestParam String relation) {
        FamilyMember updated = familyMemberService.changeRelation(id, relation);
        return ResponseEntity.ok(updated);
    }

}