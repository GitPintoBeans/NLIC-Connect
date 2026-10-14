
package org.nlic.connect.repository;

import org.nlic.connect.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing user-role assignments
 * within NLIC Connect.
 *
 * Provides database operations for retrieving and
 * verifying authorization roles assigned to users.
 *
 * @author James Pinto
 */
public interface UserRoleRepository
        extends JpaRepository<UserRole, Long> {

    /**
     * Retrieves all role assignments for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return list of assigned user roles
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * Checks whether a user already has a specific role.
     *
     * @param userId the unique identifier of the user
     * @param roleId the unique identifier of the role
     * @return true if the role is already assigned
     */
    boolean existsByUserIdAndRoleId(
            Long userId,
            Long roleId
    );
}