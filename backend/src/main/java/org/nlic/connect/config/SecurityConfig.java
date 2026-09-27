package org.nlic.connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Defines security and cross-origin request configuration
 * for the NLIC Connect application.
 *
 * @author James Pinto
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures application HTTP security rules.
     *
     * @param http Spring Security HTTP configuration
     * @return configured security filter chain
     * @throws Exception if configuration cannot be completed
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Enable CORS using the configuration defined below.
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // Disable CSRF protection for the development phase.
        http.csrf(csrf -> csrf.disable());

        // Publicly expose the development health and event endpoints.
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/api/health", 
                    "/api/events",
                     "/api/events/**",
                    "/api/prayer-requests",
                    "/api/prayer-requests/**",
                    "/api/new-life-path",
                    "/api/new-life-path/**",
                    "/api/volunteer/**"
                    ).permitAll()
                .anyRequest().authenticated()
        );

        // HTTP Basic is retained during the initial development phase.
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Allows the local React development application to communicate
     * with the Spring Boot REST API.
     *
     * @return CORS configuration used by Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}