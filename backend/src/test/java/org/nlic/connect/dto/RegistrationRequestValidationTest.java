
package org.nlic.connect.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated validation tests for NLIC Connect
 * member registration requests.
 *
 * @author James Pinto
 */
class RegistrationRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void validRegistrationShouldPass() {

        RegistrationRequest request =
            new RegistrationRequest(
                "Registration",
                "Tester",
                "test@example.com",
                "StrongPassword123!"
            );

        assertTrue(
            validator.validate(request).isEmpty()
        );
    }

    @Test
    void shortPasswordShouldBeRejected() {

        RegistrationRequest request =
            new RegistrationRequest(
                "Registration",
                "Tester",
                "test@example.com",
                "Short123"
            );

        assertTrue(
            validator.validate(request).stream()
                .anyMatch(violation ->
                    violation.getPropertyPath()
                        .toString().equals("password")
                )
        );
    }

    @Test
    void invalidEmailShouldBeRejected() {

        RegistrationRequest request =
            new RegistrationRequest(
                "Registration",
                "Tester",
                "invalid-email",
                "StrongPassword123!"
            );

        assertTrue(
            validator.validate(request).stream()
                .anyMatch(violation ->
                    violation.getPropertyPath()
                        .toString().equals("email")
                )
        );
    }

    @Test
    void blankFirstNameShouldBeRejected() {

        RegistrationRequest request =
            new RegistrationRequest(
                "",
                "Tester",
                "test@example.com",
                "StrongPassword123!"
            );

        assertTrue(
            validator.validate(request).stream()
                .anyMatch(violation ->
                    violation.getPropertyPath()
                        .toString().equals("firstName")
                )
        );
    }
}