
package org.nlic.connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configures password hashing for NLIC Connect.
 *
 * BCrypt securely hashes user passwords before
 * they are stored in the MySQL database.
 *
 * @author James Pinto
 */
@Configuration
public class PasswordConfig {

    /**
     * Provides the password encoder used by
     * registration and authentication services.
     *
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}