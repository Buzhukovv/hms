package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.service.userService.BaseUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/self")
@RequiredArgsConstructor
@Tag(name = "Self-Service", description = "APIs for users to manage their own information")
public class SelfController {

    private final BaseUserService baseUserService;

    @PatchMapping("/change-nuid")
    @Operation(summary = "Change user's NUID", description = "Updates the authenticated user's NUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NUID updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<BaseUser> changeNuid(@RequestParam int nuid) {
        BaseUser user = getAuthenticatedUser();
        user.setNuid(nuid);
        BaseUser updated = baseUserService.updateUser(user.getId(), user);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/change-name")
    @Operation(summary = "Change user's name", description = "Updates the authenticated user's name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Name updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<BaseUser> changeName(@RequestParam String firstName, @RequestParam String lastName) {
        BaseUser user = getAuthenticatedUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        BaseUser updated = baseUserService.updateUser(user.getId(), user);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/change-email")
    @Operation(summary = "Change user's email", description = "Updates the authenticated user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<BaseUser> changeEmail(@RequestParam String email) {
        BaseUser user = getAuthenticatedUser();
        user.setEmail(email);
        BaseUser updated = baseUserService.updateUser(user.getId(), user);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change user's password", description = "Updates the authenticated user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> changePassword(@RequestParam String newPassword) {
        BaseUser user = getAuthenticatedUser();
        user.setPassword(newPassword); // Note: In a real app, hash the password
        baseUserService.updateUser(user.getId(), user);
        return ResponseEntity.ok().build();
    }

    private BaseUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        BaseUser user = baseUserService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Authenticated user not found");
        }
        return user;
    }
}