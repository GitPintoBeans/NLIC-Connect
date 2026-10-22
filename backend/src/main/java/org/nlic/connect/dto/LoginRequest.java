
package org.nlic.connect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a member login request for NLIC Connect.
 *
 * Contains the credentials submitted by the
 * React login form for authentication.
 *
 * @author James Pinto
 */
public record LoginRequest(

    @NotBlank(message = "Email address is required")
    @Email(message = "Please enter a valid email address")
    String email,

    @NotBlank(message = "Password is required")
    String password

) {}