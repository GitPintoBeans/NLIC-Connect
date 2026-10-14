
package org.nlic.connect.service;

import org.nlic.connect.model.Role;
import org.nlic.connect.model.UserRole;
import org.nlic.connect.repository.RoleRepository;
import org.nlic.connect.repository.UserRepository;
import org.nlic.connect.repository.UserRoleRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads NLIC Connect member accounts and authorization
 * roles from the MySQL database for Spring Security.
 *
 * @author James Pinto
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository) {

        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Loads a registered user by email address.
     *
     * Spring Security uses this method during
     * authentication to verify account credentials.
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Find the registered member.
        org.nlic.connect.model.User user = userRepository
                .findByEmail(email.trim().toLowerCase(java.util.Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Invalid email or password."
                ));

        // Retrieve role assignments for the member.
        List<UserRole> assignments =
                userRoleRepository.findByUserId(user.getUserId());

        // Convert active database roles into
        // Spring Security authorities.
        List<SimpleGrantedAuthority> authorities =
                assignments.stream()
                        .map(assignment ->
                                roleRepository.findById(
                                        assignment.getRoleId()
                                ).orElse(null)
                        )
                        .filter(role -> role != null && role.isActive())
                        .map(Role::getRoleName)
                        .map(name -> new SimpleGrantedAuthority(
                                "ROLE_" + name
                        ))
                        .toList();

        // Accounts must have at least one active role.
        if (authorities.isEmpty()) {
            throw new UsernameNotFoundException(
                    "Account has no active roles."
            );
        }

        // Return the user details to Spring Security.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(!user.isActive())
                .authorities(authorities)
                .build();
    }
}