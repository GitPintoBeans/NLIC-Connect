
package org.nlic.connect.repository;

import org.nlic.connect.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing NLIC Connect roles.
 * Provides database access for authentication and
 * role-based authorization.
 *
 * @author James Pinto
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its unique role name.
     *
     * @param roleName the name of the role
     * @return an Optional containing the matching role
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * Finds an active role by its name.
     *
     * @param roleName the name of the role
     * @return an Optional containing the active role
     */
    Optional<Role> findByRoleNameAndActiveTrue(String roleName);
}