package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.BaseUser;

import java.util.List;
import java.util.UUID;

/**
 * Service for operations that are common to all user types
 */
public interface UserService {
    /**
     * Find a user by their email address
     * 
     * @param email the email address to search for
     * @return the user with the given email, or null if not found
     */
    BaseUser getUserByEmail(String email);

    /**
     * Find a user by their ID
     * 
     * @param id the user ID to search for
     * @return the user with the given ID, or null if not found
     */
    BaseUser getUserById(UUID id);

    /**
     * Update a user's information
     * 
     * @param id          the ID of the user to update
     * @param updatedUser the updated user information
     * @return the updated user
     */
    BaseUser updateUser(UUID id, BaseUser updatedUser);

    /**
     * Change a user's password
     * 
     * @param email           the email of the user
     * @param currentPassword the current password for verification
     * @param newPassword     the new password to set
     * @return true if the password was changed successfully, false otherwise
     */
    boolean changePassword(String email, String currentPassword, String newPassword);

    /**
     * Get all users in the system
     * 
     * @return a list of all users
     */
    List<BaseUser> getAllUsers();

    /**
     * Search for users by name or last name
     * 
     * @param keyword the search keyword
     * @return a list of matching users
     */
    List<BaseUser> searchUsersByNameOrLastName(String keyword);
}