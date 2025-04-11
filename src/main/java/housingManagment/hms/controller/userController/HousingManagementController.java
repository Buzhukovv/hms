package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.enums.userEnum.HousingManagementRole;
import housingManagment.hms.service.userService.HousingManagementService;
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
@RequestMapping("/api/housing-manager")
@RequiredArgsConstructor
@Tag(name = "Housing Manager Management", description = "APIs for managing housing manager information")
public class HousingManagementController {

    private final HousingManagementService service;
//todo all get


    @GetMapping("/{id}")
    @Operation(summary = "Get housing manager by ID", description = "Retrieves a housing manager by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Housing manager found"),
            @ApiResponse(responseCode = "404", description = "Housing manager not found")
    })
    public ResponseEntity<HousingManagement> getUserById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }





    @GetMapping("/by-role")
    @Operation(summary = "Get housing managers by role", description = "Retrieves housing managers filtered by their role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Housing managers found")
    })
    public ResponseEntity<List<HousingManagement>> getUsersByRole(@RequestParam HousingManagementRole role) {
        List<HousingManagement> users = service.findHousingManagementByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/by-block")
    @Operation(summary = "Get housing managers by block", description = "Retrieves housing managers filtered by their block")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Housing managers found")
    })
    public ResponseEntity<List<HousingManagement>> getUsersByBlock(@RequestParam String block) {
        List<HousingManagement> users = service.findByBlock(block);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search housing managers", description = "Searches for housing managers by name or last name using a keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Housing managers found")
    })
    public ResponseEntity<List<HousingManagement>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<HousingManagement> users = service.findAll().stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/assign-block")
    @Operation(summary = "Assign block to housing manager", description = "Assigns a block to a housing manager")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Block assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Housing manager not found"),
            @ApiResponse(responseCode = "400", description = "Invalid block")
    })
    public ResponseEntity<Void> assignBlock(@PathVariable UUID id, @RequestParam String block) {
        service.assignBlock(id, block);
        return ResponseEntity.ok().build();
    }
    //todo add param

    @GetMapping("/count-by-role")
    @Operation(summary = "Count housing managers by role", description = "Returns the count of housing managers by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countByRole() {
        long count = service.countByRole();
        return ResponseEntity.ok(count);
    }
//todo add param for block
    @GetMapping("/count-by-block")
    @Operation(summary = "Count housing managers by block", description = "Returns the count of housing managers by block")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countByBlock() {
        long count = service.countByBlock();
        return ResponseEntity.ok(count);
    }
}