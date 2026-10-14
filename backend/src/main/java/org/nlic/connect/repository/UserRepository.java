
package org.nlic.connect.repository;

import org.nlic.connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing NLIC Connect user accounts.
 * Provides database operations for registration and authentication.
 *
 * @author James Pinto
 */

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a registered user by their email address.
     *
     * @param email the user's email address
     * @return an Optional containing the matching user
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an email address is already registered.
     *
     * @param email the email address to check
     * @return true if the email already exists
     */
    boolean existsByEmail(String email);
}