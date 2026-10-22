
package org.nlic.connect.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.nlic.connect.dto.LoginRequest;
import org.nlic.connect.dto.RegistrationRequest;
import org.nlic.connect.service.RegistrationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

/**
 * REST controller for NLIC Connect authentication.
 *
 * Handles member registration, login, and
 * authenticated account information.
 *
 * @author James Pinto
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    /**
     * Constructor-based dependency injection.
     */
    public AuthController(
            RegistrationService registrationService,
            AuthenticationManager authenticationManager) {

        this.registrationService = registrationService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new NLIC Connect member.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegistrationRequest request) {

        try {

            Long userId =
                    registrationService.registerMember(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Account created successfully.",
                            "userId", userId
                    ));

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", exception.getMessage()
                    ));
        }
    }

    /**
     * Authenticates a registered NLIC Connect member
     * and establishes a server-managed HTTP session.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        try {

            // Normalize the submitted email address.
            String email = request.email()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            // Create an authentication request.
            Authentication authenticationRequest =
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            email,
                            request.password()
                    );

            // Verify the credentials using Spring Security.
            Authentication authentication =
                    authenticationManager.authenticate(
                            authenticationRequest
                    );

            // Create a security context for this member.
            SecurityContext securityContext =
                    SecurityContextHolder.createEmptyContext();

            securityContext.setAuthentication(authentication);

            // Store the authenticated identity for this request.
            SecurityContextHolder.setContext(securityContext);

            // Create or rotate the session identifier.
            httpRequest.getSession(true);
            httpRequest.changeSessionId();

            // Persist authentication in the HTTP session.
            securityContextRepository.saveContext(
                    securityContext,
                    httpRequest,
                    httpResponse
            );

            // Return only application role authorities.
            var roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(role -> role.startsWith("ROLE_"))
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful.",
                    "email", authentication.getName(),
                    "roles", roles
            ));

        } catch (AuthenticationException exception) {

            // Do not reveal which credential was incorrect.
            SecurityContextHolder.clearContext();

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Invalid email or password."
                    ));
        }
    }

    /**
     * Returns the currently authenticated member.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            Authentication authentication) {

        var roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))
                .toList();

        return ResponseEntity.ok(Map.of(
                "email", authentication.getName(),
                "roles", roles,
                "message", "Authentication successful."
        ));
    }

        /**
     * Provides a CSRF token for secure requests
     * from the NLIC Connect React frontend.
     *
     * The token is associated with the user's
     * server-managed HTTP session.
     *
     * @param csrfToken Spring Security CSRF token
     * @return token and required header name
     */
    @GetMapping("/csrf")
    public Map<String, String> getCsrfToken(
            CsrfToken csrfToken) {

        return Map.of(
                "headerName", csrfToken.getHeaderName(),
                "token", csrfToken.getToken()
        );
    }
}