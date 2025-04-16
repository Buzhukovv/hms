package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.enums.userEnum.HousingManagementRole;
import housingManagment.hms.service.userService.HousingManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
//todo add enum blocks

    @PostMapping
    @Operation(summary = "Создать Housing Manager ", description = "Создает нового пользователя Housing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    })
    public ResponseEntity<HousingManagement> createUser(@RequestBody HousingManagement user) {
        HousingManagement createdUser = service.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


    @GetMapping
    @Operation(summary = "Gett all member HM users")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "HS users retrieved successfully")})
    public ResponseEntity<List<HousingManagement>> getAllHsUsers(){
        List<HousingManagement> all_users = service.findAll();
        return ResponseEntity.ok(all_users);
    }

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

    @GetMapping("/count-all")
    @Operation(summary = "Count housing managers by role", description = "Returns the count of housing managers by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countAllMember() {
        long count = service.countAllMember();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/count-by-role")
    @Operation(summary = "Count housing managers by role", description = "Returns the count of housing managers by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countByRole(@RequestParam HousingManagementRole role) {
        long count = service.countByRole(role);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-by-block")
    @Operation(summary = "Count housing managers by block", description = "Returns the count of housing managers by block")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countByBlock(@RequestParam String block) {
        long count = service.countByBlock(block);
        return ResponseEntity.ok(count);
    }
}