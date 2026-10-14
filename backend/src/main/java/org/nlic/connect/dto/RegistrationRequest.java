
package org.nlic.connect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Defines the information required to register
 * a new NLIC Connect member account.
 *
 * Includes validation rules to ensure that
 * submitted registration data is acceptable.
 *
 * @author James Pinto
 */
public record RegistrationRequest(

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    String lastName,

    @NotBlank(message = "Email address is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 64,
          message = "Password must contain between 12 and 64 characters")
    String password

) {}
