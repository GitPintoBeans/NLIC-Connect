
package org.nlic.connect.service;

import org.nlic.connect.dto.RegistrationRequest;
import org.nlic.connect.model.Role;
import org.nlic.connect.model.User;
import org.nlic.connect.model.UserRole;
import org.nlic.connect.repository.RoleRepository;
import org.nlic.connect.repository.UserRepository;
import org.nlic.connect.repository.UserRoleRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Handles new member registration for NLIC Connect.
 *
 * Validates account information, prevents duplicate
 * email registrations, securely hashes passwords,
 * and assigns the default MEMBER role.
 *
 * @author James Pinto
 */
@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor-based dependency injection.
     */
    public RegistrationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new NLIC Connect member.
     *
     * Both the user account and role assignment
     * are saved within the same transaction.
     *
     * @param request validated registration details
     * @return ID of the newly registered user
     */
    @Transactional
    public Long registerMember(RegistrationRequest request) {

        // Normalize the email address.
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Prevent duplicate email registrations.
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        // Locate the active MEMBER role.
        Role memberRole = roleRepository
                .findByRoleNameAndActiveTrue("MEMBER")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The MEMBER role is unavailable."
                        )
                );

        // Create the new user account.
        User user = new User();

        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);

        // Never save the original password.
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        user.setActive(true);

        // Persist the member account.
        User savedUser = userRepository.save(user);

        // Assign the default MEMBER role.
        UserRole userRole = new UserRole();

        userRole.setUserId(savedUser.getUserId());
        userRole.setRoleId(memberRole.getRoleId());

        // Persist the user's role assignment.
        userRoleRepository.save(userRole);

        // Return the new user's identifier.
        return savedUser.getUserId();
    }
}
