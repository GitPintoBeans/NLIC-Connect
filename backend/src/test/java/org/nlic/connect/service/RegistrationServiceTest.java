
package org.nlic.connect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.nlic.connect.dto.RegistrationRequest;
import org.nlic.connect.model.Role;
import org.nlic.connect.model.User;
import org.nlic.connect.model.UserRole;
import org.nlic.connect.repository.RoleRepository;
import org.nlic.connect.repository.UserRepository;
import org.nlic.connect.repository.UserRoleRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Automated unit tests for NLIC Connect
 * member registration business logic.
 *
 * Uses mocked repositories to avoid modifying
 * the actual application database.
 *
 * @author James Pinto
 */
@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    /**
     * TC-AUTO-REG-05
     *
     * Existing email addresses must not be
     * registered more than once.
     */
    @Test
    void duplicateEmailShouldBeRejected() {

        RegistrationRequest request =
            new RegistrationRequest(
                "Duplicate",
                "Tester",
                "existing@example.com",
                "StrongPassword123!"
            );

        when(userRepository.existsByEmail(
            "existing@example.com"
        )).thenReturn(true);

        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> registrationService.registerMember(request)
            );

        assertTrue(
            exception.getMessage().contains("already exists")
        );

        // Duplicate registration must never save a user.
        verify(userRepository, never()).save(any(User.class));

        // No role assignment or password encoding.
        verifyNoInteractions(
            roleRepository,
            userRoleRepository,
            passwordEncoder
        );
    }

    /**
     * TC-AUTO-REG-06
     *
     * Newly registered accounts must receive
     * the default MEMBER role.
     */
    @Test
    void newMemberShouldReceiveMemberRole() {

        configureSuccessfulRegistration();

        RegistrationRequest request =
            new RegistrationRequest(
                "New",
                "Member",
                "newmember@example.com",
                "StrongPassword123!"
            );

        Long userId =
            registrationService.registerMember(request);

        assertEquals(42L, userId);

        verify(roleRepository)
            .findByRoleNameAndActiveTrue("MEMBER");

        ArgumentCaptor<UserRole> roleCaptor =
            ArgumentCaptor.forClass(UserRole.class);

        verify(userRoleRepository)
            .save(roleCaptor.capture());

        UserRole assignedRole = roleCaptor.getValue();

        assertEquals(42L, assignedRole.getUserId());
        assertEquals(3L, assignedRole.getRoleId());

        // Confirm that account information was saved.
        ArgumentCaptor<User> userCaptor =
            ArgumentCaptor.forClass(User.class);

        verify(userRepository)
            .save(userCaptor.capture());

        User createdUser = userCaptor.getValue();

        assertEquals("New", createdUser.getFirstName());
        assertEquals("Member", createdUser.getLastName());
        assertEquals(
            "newmember@example.com",
            createdUser.getEmail()
        );
        assertTrue(createdUser.isActive());
    }

    /**
     * TC-AUTO-REG-07
     *
     * Registration must encode passwords
     * rather than saving plaintext passwords.
     */
    @Test
    void passwordShouldBeEncodedBeforeSaving() {

        configureSuccessfulRegistration();

        RegistrationRequest request =
            new RegistrationRequest(
                "Password",
                "Tester",
                "passwordtest@example.com",
                "StrongPassword123!"
            );

        registrationService.registerMember(request);

        verify(passwordEncoder)
            .encode("StrongPassword123!");

        ArgumentCaptor<User> userCaptor =
            ArgumentCaptor.forClass(User.class);

        verify(userRepository)
            .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
            "encoded-password-for-test",
            savedUser.getPasswordHash()
        );

        assertNotEquals(
            "StrongPassword123!",
            savedUser.getPasswordHash()
        );
    }

    /**
     * Configures simulated repository responses
     * for successful registration scenarios.
     *
     * No real database operations occur.
     */
    private void configureSuccessfulRegistration() {

        Role memberRole = mock(Role.class);

        when(memberRole.getRoleId())
            .thenReturn(3L);

        when(roleRepository.findByRoleNameAndActiveTrue(
            "MEMBER"
        )).thenReturn(Optional.of(memberRole));

        when(passwordEncoder.encode(
            "StrongPassword123!"
        )).thenReturn("encoded-password-for-test");

        User savedUser = mock(User.class);

        when(savedUser.getUserId())
            .thenReturn(42L);

        when(userRepository.save(any(User.class)))
            .thenReturn(savedUser);
    }
}