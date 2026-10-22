
package org.nlic.connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/**
 * Configures the authentication manager for NLIC Connect.
 *
 * The authentication manager works with the database-backed
 * CustomUserDetailsService and BCrypt PasswordEncoder
 * to verify registered member credentials.
 *
 * @author James Pinto
 */
@Configuration
public class AuthenticationConfig {

    /**
     * Exposes Spring Security's AuthenticationManager
     * for use by the NLIC Connect login controller.
     *
     * @param configuration Spring Security authentication configuration
     * @return configured AuthenticationManager
     * @throws Exception if authentication configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}